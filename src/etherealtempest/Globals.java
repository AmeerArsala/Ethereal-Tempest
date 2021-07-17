/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest;

import com.jme3.app.state.AppStateManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import general.tools.GameTimer;
import general.procedure.ProcedureGroup;
import general.procedure.functional.SimpleProcedure;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 *
 * @author night
 */
public class Globals {
    public static final int STANDARD_FPS = 60;
    
    static final GameTimer timer = new GameTimer();
    static final ProcedureGroup tasks = new ProcedureGroup();
    static Main app;
    
    static void update(float tpf) {
        tasks.update(tpf);
        timer.update(tpf);
    }
    
    public static int frameCount() {
        return timer.getFrame();
    }
    
    public static float time() {
        return timer.getTime();
    }
    
    public static void addTaskToGlobal(SimpleProcedure task) {
        tasks.add(task);
    }
    
    public static <V> Future<V> enqueue(Callable<V> callable) {
        return app.enqueue(callable);
    }
    
    public static void enqueue(Runnable runnable) {
        app.enqueue(runnable);
    }
    
    public static int getScreenWidth() {
        return app.accessSettings().getWidth();
    }
    
    public static int getScreenHeight() {
        return app.accessSettings().getHeight();
    }
    
    public static Vector3f getScreenDimensions() {
        return new Vector3f(app.accessSettings().getWidth(), app.accessSettings().getHeight(), 0);
    }
    
    public static Vector2f getScreenDimensions2D() {
        return new Vector2f(app.accessSettings().getWidth(), app.accessSettings().getHeight());
    }
    
    public static AppStateManager getStateManager() {
        return app.getStateManager();
    }
    
    public static float superRandomFloat() {
        int genMethod = (int)(4 * Math.random());
        
        final int JAVA_RANDOM = 0;
        final int GAME_RANDOM = 1;
        final int JME_RANDOM = 2;
        //COLOR_RANDOM = 3
        
        switch (genMethod) {
            case JAVA_RANDOM:
                return (float)Math.random();
            case GAME_RANDOM:
                return Main.RNG.nextFloat();
            case JME_RANDOM:
                return FastMath.nextRandomFloat();
        }
        
        return ColorRGBA.randomColor().getColorArray()[genMethod]; //COLOR_RANDOM
    }
    
    public static ColorRGBA superRandomColor() {
        ColorRGBA rgba = new ColorRGBA();
        
        rgba.r = superRandomFloat();
        rgba.g = superRandomFloat();
        rgba.b = superRandomFloat();
        rgba.a = superRandomFloat();
        
        return rgba;
    }
    
    public static ColorRGBA superRandomColor2() {
        if (Main.RNG.nextBoolean()) {
            return ColorRGBA.randomColor();
        } else {
            return superRandomColor();
        }
    }
}
