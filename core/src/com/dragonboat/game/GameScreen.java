package com.dragonboat.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.Random;

public class GameScreen implements Screen {
    // ENVIRONMENT VARIABLES:
    private Random rnd;

    // game
    private DragonBoatGame game;
    private Player player;
    private Course course;
    private Lane[] lanes;
    private ProgressBar progressBar;
    private Opponent[] opponents;
    private boolean started = false;

    // screen
    private OrthographicCamera camera;
    private Viewport viewport;

    // graphics
    private SpriteBatch batch;
    private Texture background;

    // timing
    private int backgroundOffset;
    private float totalDeltaTime = 0;

    // global parameters
    private final int WIDTH = 1080;
    private final int HEIGHT = 720;


    public GameScreen(DragonBoatGame game) {
        rnd = new Random();

        this.game = game;
        player = this.game.player;
        course = this.game.course;
        lanes = this.game.lanes;
        progressBar = this.game.progressBar;
        opponents = this.game.opponents;


        camera = new OrthographicCamera();
        viewport = new StretchViewport(WIDTH,HEIGHT,camera);

        // texture setting

        background = course.getTexture();
        backgroundOffset = 0;
        batch = new SpriteBatch();
    }
    @Override
    public void render(float deltaTime) {
        /*
        Main rendering function. backgroundOffset determines which portion of the background is shown.
        this should be set to the player's y position each frame
        totalDeltaTime is the total time passed.
         */
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        totalDeltaTime += started ? deltaTime : 0;
        for (int i = 0; i < course.getNoLanes(); i++) {
            if(!started) break;
            for (int j = 0; j < this.game.noOfObstacles; j++) {
                if (this.game.obstacleTimes[i][j] - totalDeltaTime < 0.0001f) {
                    String[] obstacleTypes = {"Goose", "Log"};
                    // spawn an obstacle in lane i.
                    int xCoord = lanes[i].GetLeftBoundary() + rnd.nextInt(lanes[i].GetRightBoundary() - lanes[i].GetLeftBoundary());
                    lanes[i].SpawnObstacle(xCoord, HEIGHT + 100, obstacleTypes[rnd.nextInt(obstacleTypes.length)]);
                    // make sure obstacle is only spawned once.
                    // might implement this as an ordered list if it impacts the performance.
                    this.game.obstacleTimes[i][j] = 9999999f;
                }
            }
        }

        player.GetInput();
        player.MoveForward();
        if(player.getCurrentSpeed() > 0)
        {
            // detect start of game (might change this to a countdown)
            started = true;
            progressBar.StartTimer();
        }

        // Until the player is at half of the window height, don't move the background
        // Then move the background so the player is centered.

        backgroundOffset = player.getY() + player.getHeight() / 2 > HEIGHT / 2 ? player.getY() + player.getHeight() / 2 - HEIGHT / 2 : 0;

        batch.begin();

        // display background
        batch.draw(background, 0, 0, 0, background.getHeight() - HEIGHT - backgroundOffset, WIDTH, HEIGHT);

        // display and move obstacles
        for (int i = 0; i < lanes.length; i++) {
            if(!started) break;
            for (int j = 0; j < lanes[i].obstacles.size(); j++) {
                Obstacle o = lanes[i].obstacles.get(j);
                // if the background hasn't started moving yet, move obstacle at set speed.
                // else add the player speed to the obstacle speed.
                o.Move(0.4f + (backgroundOffset > 0 ? player.getCurrentSpeed() : 0));
                if (o.getY() < -o.getHeight()) {
                    lanes[i].RemoveObstacle(o);
                }
                batch.draw(o.getTexture(), o.getX(), o.getY());
            }
        }

        // display player
        batch.draw(player.texture, player.getX(), player.getY() - backgroundOffset);

        // display progress bar
        batch.draw(progressBar.getTexture(), WIDTH - progressBar.getTexture().getWidth() - 60, HEIGHT - progressBar.getTexture().getHeight() - 20);
        // get progress for each boat
        float[] progress = progressBar.getProgress(course.getTexture().getHeight());
        // draw icon for player, respective to the players progress
        batch.draw(progressBar.getPlayerIcon(),WIDTH - progressBar.getTexture().getWidth() - 50 + progress[0] * (progressBar.getTexture().getWidth() - 190), HEIGHT - progressBar.getTexture().getHeight() / 2 - 10);
        for(int i = 1; i < progress.length; i++) {  // draw icon for each opponent, respective to their progress
            batch.draw(progressBar.getOpponentIcon(), WIDTH - progressBar.getTexture().getWidth() - 50 + progress[i] * (progressBar.getTexture().getWidth()-190), HEIGHT - progressBar.getTexture().getHeight() / 2 - 10);
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height,true);
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        background.dispose();
        player.texture.dispose();
        for(int i = 0; i < lanes.length; i++) {
            for(int j = 0; j < lanes[i].obstacles.size(); j++) {
                lanes[i].obstacles.get(j).getTexture().dispose();
            }
        }
        progressBar.getTexture().dispose();
        progressBar.getOpponentIcon().dispose();
        progressBar.getPlayerIcon().dispose();
    }

    @Override
    public void show() {

    }
}
