package io.swapastack.gomoku;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.java_websocket.client.WebSocketClient;

import java.net.URI;

/**
 * This is the MainMenuScreen class.
 * This class is used to display the main menu.
 * It displays the name of the game in latin and japanese letters.
 * Multiple buttons for interaction with the app.
 *
 * @author Dennis Jehle & Ibtsam Ali Mahmood
 */
public class MainMenuScreen implements Screen {

    // reference to the parent object
    // the reference is used to call methods of the parent object
    // e.g. parent_.get_window_dimensions()
    // the 'parent' object has nothing to do with inheritance in the accustomed manner
    // it is called 'parent' because the Gomoku class extends com.badlogic.gdx.Game
    // and each Game can have multiple classes which implement the com.badlogic.gdx.Screen
    // interface, so in this special case the Game is the parent of a Screen
    private final Gomoku parent_;
    // see: https://github.com/libgdx/libgdx/wiki/Orthographic-camera
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/OrthographicCamera.html
    private final OrthographicCamera camera_;
    // see: https://github.com/libgdx/libgdx/wiki/Viewports
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/utils/viewport/ScreenViewport.html
    private final Viewport viewport_;
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/scenes/scene2d/Stage.html
    private final Stage stage_;
    // see: https://github.com/libgdx/libgdx/wiki/Spritebatch,-Textureregions,-and-Sprites
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/g2d/SpriteBatch.html
    private final SpriteBatch sprite_batch_;
    // see: https://github.com/libgdx/libgdx/wiki/2D-ParticleEffects
    // see: https://github.com/libgdx/libgdx/wiki/2D-Particle-Editor
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/g2d/ParticleEffect.html
    private final ParticleEffect particle_effect_;
    // see: https://github.com/libgdx/libgdx/wiki/Skin
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/scenes/scene2d/ui/Skin.html
    // see: https://github.com/czyzby/gdx-skins (!!! other skins available here)
    private final Skin skin_;
    // see: https://libgdx.info/basic-label/
    private final FreeTypeFontGenerator bitmap_font_generator_;
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/Texture.html
    private final Texture background_texture_;
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/audio/Music.html
    private final Music background_music_;

    // hostname / ip of server
    // e.g. localhost
    // e.g. 127.0.0.1
    public static final String host = "localhost";
    // port to connect to
    // see: https://en.wikipedia.org/wiki/List_of_TCP_and_UDP_port_numbers
    public static final int port = 42000;

    /**
     * This is the constructor for the MainMenuScreen class.
     *
     * @param parent reference to the parent object
     * @author Dennis Jehle
     */
    public MainMenuScreen(Gomoku parent) {
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
        // initialize and configure ParticleEffect
        particle_effect_ = new ParticleEffect();
        particle_effect_.load(Gdx.files.internal("slowbuzz.p"), Gdx.files.internal(""));
        particle_effect_.start();
        particle_effect_.setPosition(640.f, 460.f);
        // initialize the Skin
        skin_ = new Skin(Gdx.files.internal("ShadeUI/shadeui/uiskin.json"));

        // create string for BitmapFont and Label creation
        String gomoku_string = "Gomoku - 五目並べ";

        // initialize FreeTypeFontGenerator for BitmapFont generation
        bitmap_font_generator_ = new FreeTypeFontGenerator(Gdx.files.internal("fonts/NotoSansCJKtc_ttf/NotoSansCJKtc-Bold.ttf"));
        // specify parameters for BitmapFont generation
        FreeTypeFontGenerator.FreeTypeFontParameter bitmap_font_parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        // set font size
        bitmap_font_parameter.size = 60;
        // specify available letters
        bitmap_font_parameter.characters = gomoku_string;
        // set font color in RGBA format (red, green, blue, alpha)
        bitmap_font_parameter.color = new Color(1.f, 1.f, 0, 1.f);
        // other specifications
        bitmap_font_parameter.borderWidth = 1;
        bitmap_font_parameter.borderColor = Color.BLACK; // alternative enum color specification
        bitmap_font_parameter.shadowOffsetX = 3;
        bitmap_font_parameter.shadowOffsetY = 3;
        bitmap_font_parameter.shadowColor = new Color(1.f, 1.f, 0, 0.25f);

        // generate BitmapFont with FreeTypeFontGenerator and FreeTypeFontParameter specification
        BitmapFont japanese_latin_font = bitmap_font_generator_.generateFont(bitmap_font_parameter);

        // create a LabelStyle object to specify Label font
        Label.LabelStyle japanese_latin_label_style = new Label.LabelStyle();
        japanese_latin_label_style.font = japanese_latin_font;

        // create a Label with the main menu title string
        Label gomoku_label = new Label(gomoku_string, japanese_latin_label_style);
        gomoku_label.setFontScale(1, 1);
        gomoku_label.setPosition(
                (float)client_area_dimensions.first / 2.f - gomoku_label.getWidth() / 2.f
                , (float)client_area_dimensions.second / 2.f - gomoku_label.getHeight() / 2.f
        );

        // add main menu title string Label to Stage
        stage_.addActor(gomoku_label);

        // load background texture (keine lizenz benötigt bzw. Stanard Pixaby Lizenz, frei nutzbares Bild)
        background_texture_ = new Texture("texture/wood.jpg");

        // load background music
        // note: every game should have some background music
        //       feel free to exchange the current wav with one of your own music files
        //       but you must have the right license for the music file
        background_music_ = Gdx.audio.newMusic(Gdx.files.internal("piano/fallout.mp3"));
        background_music_.setLooping(true);
        background_music_.play();

        // create switch to GameScreen button
        Button game_screen_button = new TextButton("START", skin_, "round");
        game_screen_button.setPosition(
                (float)client_area_dimensions.first / 2.f - game_screen_button.getWidth() / 2.f
                , (float)client_area_dimensions.second / 2.f - 125.f
        );
        // add InputListener to Button, and close app if Button is clicked
        game_screen_button.addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                parent_.change_screen(ScreenEnum.GAME);
            }
        });

        // add exit button to Stage
        stage_.addActor(game_screen_button);


        // create switch to Ranking button
        Button ranking_screen_button = new TextButton("RANKING", skin_, "round");
        ranking_screen_button.setPosition(
                (float)client_area_dimensions.first / 2.f - ranking_screen_button.getWidth() / 2.f
                , (float)client_area_dimensions.second / 2.f - 200.f
        );
        // add InputListener to Button and switch to Rankingscreen if Button is clicked
        ranking_screen_button.addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                parent_.change_screen(ScreenEnum.RANKING);
            }
        });

        // add switch to Ranking button to Stage
        stage_.addActor(ranking_screen_button);

        // create exit application button
        Button exit_button = new TextButton("EXIT", skin_, "round");
        exit_button.setPosition(
                (float)client_area_dimensions.first / 2.f - exit_button.getWidth() / 2.f
                , (float)client_area_dimensions.second / 2.f - 275.f
        );
        // add InputListener to Button, and close app if Button is clicked
        exit_button.addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
            }
        });

        // add exit button to Stage
        stage_.addActor(exit_button);

        // create exit application button
        Button simple_client_button = new TextButton("WSS TEST", skin_, "round");
        simple_client_button.setPosition(25.f, 25.f);
        // add InputListener to Button, and close app if Button is clicked
        simple_client_button.addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                try {
                    WebSocketClient client = new SimpleClient(new URI(String.format("ws://%s:%d", host, port)));
                    client.connect();
                    client.close();
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });

        // add exit button to Stage
        stage_.addActor(simple_client_button);
    }

    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     *
     * @author Dennis Jehle
     */
    @Override
    public void show() {
        // this command is necessary that the stage receives input events
        // e.g. mouse click on exit button
        // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/Input.html
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

        // draw background graphic
        // note: it is not necessary to use two SpriteBatch blocks
        // the background rendering is separated from the ParticleEffect rendering
        // for the sake of clarity
        sprite_batch_.begin();
        sprite_batch_.draw(background_texture_, 0, 0, viewport_.getScreenWidth(), viewport_.getScreenHeight());
        sprite_batch_.end();

        // update and draw the ParticleEffect
        sprite_batch_.begin();
        if (particle_effect_.isComplete())
            particle_effect_.reset();
        particle_effect_.draw(sprite_batch_, delta);
        sprite_batch_.end();

        // update the Stage
        stage_.act(delta);
        // draw the Stage
        stage_.draw();
    }

    /**
     * This method gets called after a window resize.
     *
     * @param width new window width
     * @param height new window height
     * @see ApplicationListener#resize(int, int)
     * @author Dennis Jehle
     */
    @Override
    public void resize(int width, int height) {

    }

    /**
     * This method gets called if the application lost focus.
     *
     * @see ApplicationListener#pause()
     * @author Dennis Jehle
     */
    @Override
    public void pause() {}

    /**
     * This method gets called if the application regained focus.
     *
     * @see ApplicationListener#resume()
     * @author Dennis Jehle
     */
    @Override
    public void resume() {}

    /**
     * Called when this screen is no longer the current screen for a {@link Game}.
     * @author Dennis Jehle
     */
    @Override
    public void hide() {
       background_music_.stop();
    }

    /**
     * Called when this screen should release all resources.
     * @author Dennis Jehle
     */
    @Override
    public void dispose() {
        background_music_.dispose();
        background_texture_.dispose();
        bitmap_font_generator_.dispose();
        skin_.dispose();
        particle_effect_.dispose();
        stage_.dispose();
        sprite_batch_.dispose();
    }
}
