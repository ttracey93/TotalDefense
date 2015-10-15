package com.mygdx.totaldefense.screens;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.totaldefense.TotalDefense;
import com.mygdx.totaldefense.collision.CollisionListener;
import com.mygdx.totaldefense.factories.CameraFactory;
import com.mygdx.totaldefense.factories.PlayerFactory;
import com.mygdx.totaldefense.managers.Sounds;
import com.mygdx.totaldefense.managers.Triggers;
import com.mygdx.totaldefense.systems.*;
import com.mygdx.totaldefense.util.IConversions;
import com.mygdx.totaldefense.util.IMapPath;
import com.mygdx.totaldefense.factories.LevelFactory;
import com.mygdx.totaldefense.world.Level;

/**
 * Created by dubforce on 9/29/15.
 */
public class PlayScreen extends ScreenAdapter {
    private TotalDefense game;

    // Cameras
    private OrthographicCamera guiCam;

    // Entity System
    private PooledEngine engine;

    // Box2D world
    private World world;
    private RayHandler rayHandler;
    private Level level;

    // Collision listener
    private CollisionListener collisionListener;

    // Entities
    private Entity player;
    private Entity camera;
    private Entity firstEnemy;

    public PlayScreen(TotalDefense game) {
        this.game = game;

        guiCam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        guiCam.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);

        engine = new PooledEngine();

        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new CollisionListener(world));
        Triggers.setWorld(world);

        rayHandler = new RayHandler(world);

        level = LevelFactory.from(this, IMapPath.test2, world);

        engine.addSystem(new CameraSystem());
        engine.addSystem(new BodySystem());
        engine.addSystem(new ControllerSystem());
        engine.addSystem(new RenderingSystem(game.batch, level, rayHandler));
        engine.addSystem(new ProjectileSystem(engine));
        engine.addSystem(new AISystem());
        engine.addSystem(new LightSystem());

        player = PlayerFactory.player(engine, world, rayHandler);
        camera = CameraFactory.camera(engine, player);

        // add light to world
        PointLight testLight = new PointLight(rayHandler, 500);
        testLight.setDistance(10f);
        testLight.setColor(Color.WHITE);
        testLight.setPosition(
                500 * IConversions.PPM,
                400 * IConversions.PPM
        );
    }

    @Override
    public void render(float delta) {
        update(delta);
        drawUI();
    }

    private void update(float delta) {
        world.step(delta, 6, 2);
        engine.update(delta);
        Triggers.update();
    }

    private void drawUI() {

    }


    public void setLevel(String levelName) {
        // Dispose of all triggers
        Triggers.dispose();

        // remove all entities from the engine
        engine.removeAllEntities();

        // clear world of all bodies
        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);

        for(Body body : bodies) {
            world.destroyBody(body);
        }

        // remove all lights from the world
        rayHandler.removeAll();

        // generate new level
        level = LevelFactory.from(this, "maps/" + levelName, world);

        // set level in rendering system
        engine.getSystem(RenderingSystem.class).setLevel(level);

        // create player and camera
        player = PlayerFactory.player(engine, world, rayHandler);
        camera = CameraFactory.camera(engine, player);
    }

    @Override
    public void dispose() {
        super.dispose();

        // dispose of all triggers
        Triggers.dispose();
    }
}
