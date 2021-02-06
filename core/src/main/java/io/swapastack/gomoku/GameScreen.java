package io.swapastack.gomoku;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * This is the GameScreen class.
 * This class can be used to implement your game logic.
 * <p>
 * The current implementation provides a colorful grid and a leave game button.
 * <p>
 * A good place to gather further information is here:
 * https://github.com/libgdx/libgdx/wiki/Input-handling
 * Input and Event handling is necessary to handle mouse and keyboard input.
 *
 * @author Dennis Jehle
 */
public class GameScreen implements Screen {
    //Gamepiece placing
    private final GameScreenModel gameScreenModel;
    // reference to the parent object
    private final Gomoku parent_;
    // OrthographicCamera
    private final OrthographicCamera camera_;
    // Viewport
    private final Viewport viewport_;
    // Stage
    private final Stage stage_;
    //multiplexer
    private InputMultiplexer multiplexer;
    // SpriteBatch
    private final SpriteBatch sprite_batch_;
    // ShapeRenderer
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/glutils/ShapeRenderer.html
    private final ShapeRenderer shape_renderer_;
    // Skin
    private final Skin skin_;
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/Texture.html
    private final Texture background_texture_;
    // Colors
    private static final Color red_ = new Color(1.f, 0.f, 0.f, 10.f);
    private static final Color green_ = new Color(0.f, 1.f, 0.f, 1.f);
    private static final Color blue_ = new Color(0.f, 0.f, 1.f, 100.f);
    //Player Input in Dialog
    private TextField dialog_player_input1;
    private TextField dialog_player_input2;
    //Player Label
    private Label player_label2;
    //Openingrule
    private int opening_rule;

    Player[][] gamestone_positions;
    // grid dimensions
    private static final int grid_size_ = 15;
    private static final float padding = 100.f;
    private static final float line_width = 5.f;


    public GameScreen(Gomoku parent) {
        //GamescreemModel method
        gameScreenModel = new GameScreenModel();
        // store reference to parent class
        parent_ = parent;
        // initialize OrthographicCamera with current screen size
        // e.g. OrthographicCamera(1280.f, 720.f)
        Tuple<Integer> client_area_dimensions = parent_.get_window_dimensions();
        camera_ = new OrthographicCamera((float) client_area_dimensions.first, (float) client_area_dimensions.second);
        // initialize ScreenViewport with the OrthographicCamera created above
        viewport_ = new ScreenViewport(camera_);
        // initialize SpriteBatch
        sprite_batch_ = new SpriteBatch();
        // initialize the Stage with the ScreenViewport created above
        stage_ = new Stage(viewport_, sprite_batch_);
        // initialize ShapeRenderer
        shape_renderer_ = new ShapeRenderer();
        // initialize the Skin //Shade UI can be used under the CC BY license.
        //http://creativecommons.org/licenses/by/4.0/
        skin_ = new Skin(Gdx.files.internal("ShadeUI/shadeui/uiskin.json"));
        // create switch to MainMenu button
        Button menu_screen_button = new TextButton("LEAVE GAME", skin_, "round"); // "small");
        menu_screen_button.setPosition(25.f, 25.f);

        // add InputListener to Button, and close app if Button is clicked
        menu_screen_button.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                change_screen_to_menu();
            }
        });

        // add exit button to Stage
        stage_.addActor(menu_screen_button);

        // create a Label with the Playersname string
        Label player_label = new Label("Current Player:          ", skin_, "title");
        player_label.setFontScale(1, 1);

        player_label.setPosition(
                (float) 10,
                (float) 570
        );
        stage_.addActor(player_label);

        player_label2 = new Label("                           ", skin_, "title");
        player_label2.setScale(20f, 20f);
        player_label2.setFontScale(1, 1);
        player_label2.setPosition(
                (float) 10,
                (float) 500
        );
        stage_.addActor(player_label2);

        // load background texture
        background_texture_ = new Texture("texture/wood.jpg");

    }

    private void change_screen_to_menu() {
        Player.ONE.setName("Player 1");
        Player.TWO.setName("Player 2");
        parent_.change_screen(ScreenEnum.MENU);
    }


    /**
     * Interpolate between RGB(A) values.
     * Inspired by: https://stackoverflow.com/a/21010385/5380008
     *
     * @param color1   first color to mix
     * @param color2   second color to mix
     * @param fraction percentage 0.f-1.f
     * @return {@link Color} mixed color
     * @author Dennis Jehle
     */
    private Color mix(Color color1, Color color2, float fraction) {
        // calculated the mixed RGB values
        float r = (color2.r - color1.r) * fraction + color1.r;
        float g = (color2.g - color1.g) * fraction + color1.g;
        float b = (color2.b - color1.b) * fraction + color1.b;
        // return the mixed color
        return new Color(r, g, b, 1.f);
    }

    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     *
     * @author Dennis Jehle
     */
    @Override
    public void show() {
        multiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(multiplexer);

        //Mouse click & game piece set
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button != Input.Buttons.LEFT || pointer > 0) return false;
                Tuple tiles_position = gameScreenModel.findTilesPosition(screenX, Gdx.graphics.getHeight() - screenY);
                if (tiles_position == null) return false;
                boolean pos_free = gameScreenModel.setGamestone_position((int) tiles_position.first, (int) tiles_position.second);
                if (!pos_free) {
                    return false;
                }

                if (gameScreenModel.win_condition()==true){
                    show_winner_dialog();

                }else {

                }

                int c = gameScreenModel.handle_rules_after_gamestone();

                switch (c){
                    case 1:
                        show_openingrule_dialog();
                        break;
                    case 2:
                        show_change_player_colour_dialog();
                        break;
                }

                return true;
            }

        });

        // InputProcessor for Stage
        multiplexer.addProcessor(stage_);
        //show Dialog
        show_set_name_dialog();
    }

    private void show_change_player_colour_dialog() {
        //show Colour change dialog
        Dialog dialog = new Dialog("    Change colour?      ", skin_) {
            protected void result(Object obj) {
                boolean ya_no = (boolean) obj;
                Gdx.input.setInputProcessor(multiplexer);
                if (ya_no) {
                    gameScreenModel.change_player_colour();
                    gameScreenModel.change_player();
                }
            }

        };
        dialog.text(gameScreenModel.getCurrent_player().getName() + " Do you want to change your colour?");
        dialog.button("Yes", true);
        dialog.button("No", false);
        dialog.show(stage_);

        Gdx.input.setInputProcessor(stage_);

    }

    private void show_openingrule_dialog() {
        //show Openingdialog
        Dialog dialog = new Dialog("Openingrule", skin_) {
            protected void result(Object obj) {
                gameScreenModel.setOpening_rule((int) obj);
                Gdx.input.setInputProcessor(multiplexer);
                if (gameScreenModel.getOpening_rule() == 1) {
                    gameScreenModel.change_player_colour();
                }
                //Openingrule 2 not neccesary beceause of else


            }

        };
        dialog.text(gameScreenModel.getCurrent_player().getName() + " choose your option to continue: ");
        dialog.button("Option 1", 1);
        dialog.button("Option 2", 2);
        dialog.button("Option 3", 3);
        dialog.show(stage_);

        Gdx.input.setInputProcessor(stage_);

    }


    private void show_set_name_dialog() {

        //show Namedialog
        Dialog dialog_name = new Dialog("Set your Playername", skin_) {
            protected void result(Object obj) {
                if ((boolean) obj) {
                    Player.ONE.setName(dialog_player_input1.getText());
                    Player.TWO.setName(dialog_player_input2.getText());

                }
                System.out.println(Player.ONE.getName() + "--------" + Player.TWO.getName());
                System.out.println("result " + obj);
                Gdx.input.setInputProcessor(multiplexer);
            }
        };

        dialog_player_input1 = new TextField(Player.ONE.getName(), skin_);
        dialog_name.text("Players Name: ");
        dialog_name.button("OK", true);
        dialog_name.button("Cancel", false);
        dialog_name.key(Input.Keys.ENTER, true);
        dialog_name.key(Input.Keys.ESCAPE, false);
        dialog_name.getContentTable().row();
        dialog_name.getContentTable().add(dialog_player_input1).width(135);

        dialog_player_input2 = new TextField(Player.TWO.getName(), skin_);
        dialog_name.getContentTable().row();
        dialog_name.getContentTable().add(dialog_player_input2).width(135);
        dialog_name.show(stage_);
        stage_.setKeyboardFocus(dialog_player_input2);
        stage_.unfocusAll();
        Gdx.input.setInputProcessor(stage_);


    }

    private void show_winner_dialog() {
        //show winner dialog
        Dialog dialog = new Dialog("        Winner!         ", skin_) {
            protected void result(Object obj) {
                Gdx.input.setInputProcessor(multiplexer);
                if ((boolean)obj) {
                    change_screen_to_menu();
                }else {

                }
            }
        };
        dialog.text(gameScreenModel.getCurrent_player().getName() + " has won!");
        dialog.button("Back to Menu", true);
        dialog.show(stage_);

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
        // draw background graphic
        // note: it is not necessary to use two SpriteBatch blocks
        // the background rendering is separated from the ParticleEffect rendering
        // for the sake of clarity
        sprite_batch_.begin();
        sprite_batch_.draw(background_texture_, 0, 0, viewport_.getScreenWidth(), viewport_.getScreenHeight());
        sprite_batch_.end();

        // update camera
        camera_.update();

        // update the current SpriteBatch
        sprite_batch_.setProjectionMatrix(camera_.combined);

        // gather necessary information for grid drawing
        float screen_width = Gdx.graphics.getWidth();
        float screen_height = Gdx.graphics.getHeight();
        float column_height = screen_height - 2.f * padding;
        float row_width = column_height;
        float offset = row_width / ((float) grid_size_ - 1.f);
        float top_left_x = screen_width / 2.f - row_width / 2.f;

        // draw grid
        shape_renderer_.begin(ShapeType.Filled);
        for (int i = 0; i < grid_size_; i++) {
            float fraction = (float) (i + 1) / (float) grid_size_;
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

        gamestone_positions = gameScreenModel.getGamestone_positions();
        visualize_gamestones(gamestone_positions);

        player_label2.setText(gameScreenModel.getCurrent_player().getName());

        // update the Stage
        stage_.act(delta);
        // draw the Stage
        stage_.draw();


    }
    private void visualize_gamestones(Player[][] gamestone_positions) {
        for (int x = 0; x < grid_size_; x++) {
            for (int y = 0; y < grid_size_; y++) {
                if (gamestone_positions[x][y] != null) {
                    Tuple pixel = gameScreenModel.findPixels(x, y);
                    Player p = gamestone_positions[x][y];
                    setGamestone(p.getColour(), (int) pixel.first, (int) pixel.second);

                }
            }
        }
    }


    private void setGamestone(Color colour, int x_pixel, int y_pixel) {
        shape_renderer_.setColor(colour);
        shape_renderer_.begin(ShapeType.Filled);
        shape_renderer_.circle(x_pixel, y_pixel, 14);
        shape_renderer_.end();
    }

    public GameScreenModel getGameScreenModel() {
        return gameScreenModel;
    }

    /**
     * This method is called if the window gets resized.
     *
     * @param width  new window width
     * @param height new window height
     * @author Dennis Jehle
     * @see ApplicationListener#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {
        // could be ignored because you cannot resize the window at the moment
    }

    /**
     * This method is called if the application lost focus.
     *
     * @author Dennis Jehle
     * @see ApplicationListener#pause()
     */
    @Override
    public void pause() {

    }

    /**
     * This method is called if the applaction regained focus.
     *
     * @author Dennis Jehle
     * @see ApplicationListener#resume()
     */
    @Override
    public void resume() {

    }

    /**
     * Called when this screen is no longer the current screen for a {@link Game}.
     *
     * @author Dennis Jehle
     */
    @Override
    public void hide() {

    }

    /**
     * Called when this screen should release all resources.
     *
     * @author Dennis Jehle
     */
    @Override
    public void dispose() {
        background_texture_.dispose();
        skin_.dispose();
        stage_.dispose();
        sprite_batch_.dispose();
    }
}
