package io.swapastack.gomoku;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * This is the GameScreen class.
 * This class can be used to implement your game logic.
 *
 * The current implementation provides a colorful grid and a leave game button.
 *
 * A good place to gather further information is here:
 * https://github.com/libgdx/libgdx/wiki/Input-handling
 * Input and Event handling is necessary to handle mouse and keyboard input.
 *
 * @author Dennis Jehle
 */
public class GameScreen implements Screen {

    // reference to the parent object
    private final Gomoku parent_;
    // OrthographicCamera
    private final OrthographicCamera camera_;
    // Viewport
    private final Viewport viewport_;
    // Stage
    private final Stage stage_;
    // SpriteBatch
    private final SpriteBatch sprite_batch_;
    // ShapeRenderer
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/glutils/ShapeRenderer.html
    private final ShapeRenderer shape_renderer_;
    // Skin
    private final Skin skin_;
    // Text
    private Texture lava_texture;
    // Colors
    private static final Color red_   = new Color(1.f,0.f,0.f,1.f);
    private static final Color green_ = new Color(0.f,1.f,0.f,1.f);
    private static final Color blue_  = new Color(0.f,0.f,1.f,1.f);
    // grid dimensions
    private static final int grid_size_ = 15;
    private static final float padding = 100.f;
    private static final float line_width = 5.f;



    public GameScreen(Gomoku parent) {
        // store reference to parent class
        parent_ = parent;
        // initialize OrthographicCamera with current screen size
        // e.g. OrthographicCamera(1280.f, 720.f)
        Tuple<Integer> client_area_dimensions = parent_.get_window_dimensions();
        camera_ = new OrthographicCamera((float)client_area_dimensions.first, (float)client_area_dimensions.second);
        // initialize ScreenViewport with the OrthographicCamera created above
        viewport_ = new ScreenViewport(camera_);
        // initialize SpriteBatch
        sprite_batch_ = new SpriteBatch();
        // initialize the Stage with the ScreenViewport created above
        stage_ = new Stage(viewport_, sprite_batch_);
        // initialize ShapeRenderer
        shape_renderer_ = new ShapeRenderer();
        // initialize the Skin
        skin_ = new Skin(Gdx.files.internal("glassy/skin/glassy-ui.json"));
        // initalize the texture
        lava_texture = new Texture(Gdx.files.internal("texture/lava.jpg"));
        // create switch to MainMenu button
        Button menu_screen_button = new TextButton("LEAVE GAME", skin_, "small");
        menu_screen_button.setPosition(25.f, 25.f);
        // add InputListener to Button, and close app if Button is clicked
        menu_screen_button.addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                parent_.change_screen(ScreenEnum.MENU);
            }
        });

        // add exit button to Stage
        stage_.addActor(menu_screen_button);
    }



    /**
     * Interpolate between RGB(A) values.
     * Inspired by: https://stackoverflow.com/a/21010385/5380008
     *
     * @param color1 first color to mix
     * @param color2 second color to mix
     * @param fraction percentage 0.f-1.f
     * @return {@link Color} mixed color
     * @author Dennis Jehle
     */
    private Color mix(Color color1, Color color2, float fraction) {
        // calculated the mixed RGB values
        float r =  (color2.r - color1.r) * fraction + color1.r;
        float g =  (color2.g - color1.g) * fraction + color1.g;
        float b =  (color2.b - color1.b) * fraction + color1.b;
        // return the mixed color
        return new Color(r, g, b, 1.f);
    }

    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     * @author Dennis Jehle
     */
    @Override
    public void show() {
        // InputProcessor for Stage
        Gdx.input.setInputProcessor(stage_);
    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     * @author Dennis Jehle
     */
    @Override
    public void render(float delta) {
        // clear the client area (Screen) with the clear color (black)
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // update camera
        camera_.update();

        // update the current SpriteBatch
        sprite_batch_.setProjectionMatrix(camera_.combined);

        // gather necessary information for grid drawing
        float screen_width  = Gdx.graphics.getWidth();
        float screen_height = Gdx.graphics.getHeight();
        float column_height = screen_height - 2.f * padding;
        float row_width = column_height;
        float offset = row_width / ((float)grid_size_ - 1.f);
        float top_left_x = screen_width / 2.f - row_width / 2.f;

        // draw grid
        shape_renderer_.begin(ShapeType.Filled);
        for (int i = 0; i < grid_size_; i++) {
            float fraction = (float)(i + 1) / (float)grid_size_;
            shape_renderer_.rectLine(
                    top_left_x + i * offset, padding + column_height
                    , top_left_x + i * offset, padding
                    , line_width
                    , mix(green_, blue_, fraction)
                    , mix(blue_, green_, fraction)
            );
            shape_renderer_.rectLine(
                    top_left_x, padding + column_height - i * offset
                    , top_left_x + row_width, padding + column_height - i * offset
                    , line_width
                    , mix(green_, blue_, fraction)
                    , mix(blue_, green_, fraction)
            );
        }
        shape_renderer_.end();

        sprite_batch_.begin();
        //draw rectangle
        sprite_batch_.draw(lava_texture, 400,350,400,100,20,20);
        sprite_batch_.end();

        shape_renderer_.setColor(Color.WHITE);
        shape_renderer_.begin(ShapeType.Filled);
        shape_renderer_.circle(400, 550, 10);
        shape_renderer_.end();

        // update the Stage
        stage_.act(delta);
        // draw the Stage
        stage_.draw();



    }

    /**
     * This method is called if the window gets resized.
     *
     * @param width new window width
     * @param height new window height
     * @see ApplicationListener#resize(int, int)
     * @author Dennis Jehle
     */
    @Override
    public void resize(int width, int height) {
        // could be ignored because you cannot resize the window at the moment
    }

    /**
     * This method is called if the application lost focus.
     *
     * @see ApplicationListener#pause()
     * @author Dennis Jehle
     */
    @Override
    public void pause() {

    }

    /**
     * This method is called if the applaction regained focus.
     *
     * @see ApplicationListener#resume()
     * @author Dennis Jehle
     */
    @Override
    public void resume() {

    }

    /**
     * Called when this screen is no longer the current screen for a {@link Game}.
     * @author Dennis Jehle
     */
    @Override
    public void hide() {

    }

    /**
     * Called when this screen should release all resources.
     * @author Dennis Jehle
     */
    @Override
    public void dispose() {
        skin_.dispose();
        stage_.dispose();
        sprite_batch_.dispose();
    }
}
