package io.swapastack.gomoku;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


public class RankingScreen implements Screen {
    // Alle wichtigen globalen Variablen. Größtenteils aus anderen Screens übernommen.
    private final Gomoku parent_;
    private final OrthographicCamera camera_;
    private final Viewport viewport_;
    private final Stage stage_;
    private final SpriteBatch sprite_batch_;
    private final ParticleEffect particle_effect_;
    private final Skin skin_;
    private final FreeTypeFontGenerator bitmap_font_generator_;
    private final Texture background_texture_;
    private final Music background_music_;
    public List list;
    private SimpleClient client;

    /**
     * @author Ibtsam Ali Mahmood
     * @param parent
     * Rankingscreen Konstruktur
     * Alle wichtigen Initialisierungen der globalen Varibalen sind hier.
     * Grafische Dinge wurden ebenso hier aufgerufen; Buttons, Hintergrundbild, Hintergrundmusik,
     * Rankingliste und dessen Scrollpanel etc.
     */
    public RankingScreen(Gomoku parent) {
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
        // initialize and configure ParticleEffect
        particle_effect_ = new ParticleEffect();
        particle_effect_.load(Gdx.files.internal("slowbuzz.p"), Gdx.files.internal(""));
        particle_effect_.start();
        particle_effect_.setPosition(640.f, 460.f);
        // initialize the Skin
        skin_ = new Skin(Gdx.files.internal("ShadeUI/shadeui/uiskin.json"));

        // create string for BitmapFont and Label creation
        String gomoku_string = "Ranking ";

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

        // load background texture
        background_texture_ = new Texture("texture/wood.jpg");

        // generate BitmapFont with FreeTypeFontGenerator and FreeTypeFontParameter specification
        BitmapFont japanese_latin_font = bitmap_font_generator_.generateFont(bitmap_font_parameter);

        // create a LabelStyle object to specify Label font
        Label.LabelStyle japanese_latin_label_style = new Label.LabelStyle();
        japanese_latin_label_style.font = japanese_latin_font;

        // create a Label with the main menu title string
        Label gomoku_label = new Label(gomoku_string, japanese_latin_label_style);
        gomoku_label.setFontScale(1, 1);
        gomoku_label.setPosition(
                (float) client_area_dimensions.first / 2.f - gomoku_label.getWidth() / 2.f
                , (float) client_area_dimensions.second / 2.f - gomoku_label.getHeight() / 2.f+200.f
        );

        // add main menu title string Label to Stage
        stage_.addActor(gomoku_label);

        // load background music
        // note: every game should have some background music
        //       feel free to exchange the current wav with one of your own music files
        //       but you must have the right license for the music file
        background_music_ = Gdx.audio.newMusic(Gdx.files.internal("piano/fallout.mp3"));
        background_music_.setLooping(true);
        background_music_.play();

        // create leave Ranking button
        Button leave_ranking_button = new TextButton("Leave Ranking", skin_, "round"); // "small");
        leave_ranking_button.setPosition(25.f, 25.f);
        // add InputListener to Button, and close app if B utton is clicked
        leave_ranking_button.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                client.close();
                parent_.change_screen(ScreenEnum.MENU);

            }
        });
        //Aufruf zum Senden und Empfangen der Rankingliste vom Server.
        //Verzögerung von 500ms für den Verbindungsaufbau vom Server.
        //Catch für den Abfang einer Fehlers.
        try {
            client = new SimpleClient(new URI(String.format("ws://%s:%d", MainMenuScreen.host, MainMenuScreen.port)));
            client.connect();
            Thread.sleep(500);
            client.send_history_get_all();
            Thread.sleep(500);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        //Die Liste, für das Ranking.
        list = new List<String>(skin_, "dimmed");
        list.setItems(ranking_list(client.getPlayerAndScoreMap()).toArray());

        //Die Liste wird dem ScrollPane übergeben zur besseren Darstellung.
        //Inspiriert von: https://www.reddit.com/r/libgdx/comments/5ln5gs/how_do_i_use_scrollpane/
        ScrollPane scrollPane = new ScrollPane(list);
        scrollPane.setSize(260, 300);
        scrollPane.setPosition((float) client_area_dimensions.first / 2.f - scrollPane.getWidth() / 2.f
                , (float) client_area_dimensions.second / 2.f - scrollPane.getHeight() / 2.f);

        // create Reload button
        Button winner_list_button = new TextButton("Reload", skin_, "round"); // "small");
        winner_list_button.setPosition(570.f, 150.f);
        // add InputListener to Button, and close app if Button is clicked
        winner_list_button.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                client.send_history_get_all();
                try {
                    Thread.sleep(500);
                    list.setItems(ranking_list(client.getPlayerAndScoreMap()).toArray());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        // add exit button to Stage
        stage_.addActor(leave_ranking_button);
        stage_.addActor(winner_list_button);
        stage_.addActor(scrollPane);



    }
    //HashMap https://www.w3schools.com/java/java_hashmap.asp
    //Der Namen- und Punkte Eintrag für das Ranking
    //Es wird eine Arraylist erstellt und eine Map. Der vorteil einer Arraylist gegenüber einem Array ist, dass durch
    //die add dunkrion immer belieibig die Liste erweitert werden kann ebenso keine vordefinierte größe nötog ist, was in
    //in diesem Fall praktischer ist. Die Map wurde gewählt, da man Paare speichern kann wie in diesem Fall: Name & Score.
    //Es wird über die Map iteriert und die Rankings angepasst d.h. der mit der höchsten Punktzahl steht ganz oben.
    //Durch eine temporäre Variable werden die Werte getauscht.
    //Am ende wird die sortierte Liste zurückgegeben.
    public ArrayList<String> ranking_list(Map<String, PlayerAndScore> playerAndScoreMap) {
        ArrayList<String> winner_list = new ArrayList();
        if (playerAndScoreMap != null) {
            Collection<PlayerAndScore> playerAndScoreCollection =  playerAndScoreMap.values();
            PlayerAndScore[] playerAndScoreArray = playerAndScoreCollection.toArray(new PlayerAndScore[playerAndScoreCollection.size()]);
            for (int i = 0; i < playerAndScoreArray.length; i++) {
                for (int j = 0; j < playerAndScoreArray.length; j++) {
                    if (playerAndScoreArray[i].getScore() > playerAndScoreArray[j].getScore()) {
                        PlayerAndScore temp = playerAndScoreArray[i];
                        playerAndScoreArray[i] = playerAndScoreArray[j];
                        playerAndScoreArray[j] = temp;
                    }
                }
            }

            for (int i = 1; i <= playerAndScoreArray.length; i++) {
                winner_list.add(i + ". " + playerAndScoreArray[i - 1].getPlayer_name() + " | " + playerAndScoreArray[i-1].getScore());
            }
        }
        return winner_list;
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
     * @param width  new window width
     * @param height new window height
     * @author Dennis Jehle
     * @see ApplicationListener#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {

    }

    /**
     * This method gets called if the application lost focus.
     *
     * @author Dennis Jehle
     * @see ApplicationListener#pause()
     */
    @Override
    public void pause() {
    }

    /**
     * This method gets called if the application regained focus.
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
        background_music_.stop();
    }

    /**
     * Called when this screen should release all resources.
     *
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
