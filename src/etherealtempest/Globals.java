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
import general.procedure.ProcedureGroup;
import general.tools.GameTimer;
import general.procedure.functional.SimpleProcedure;
import general.utils.DependentObjectComparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 *
 * @author night
 */
@SuppressWarnings("element-type-mismatch")
public class Globals {
    public static final int STANDARD_FPS = 60;
    public static final Vector3f WORLD_UP_VECTOR = new Vector3f(0, 1, 0); //do not fuck with this unless you set it back to normal after
    public static boolean GameIsFrozen = false;
    
    static final GameTimer timer = new GameTimer();
    static final ProcedureGroup tasks = new ProcedureGroup();
    static Main app;
    
    private static class TaskID {
        public final Object id;
        public final int index;
        
        public TaskID(Object id, int index) {
            this.id = id;
            this.index = index;
        }

        @Override
        public boolean equals(Object o) {
            return id.equals(o);
        }
    }
    
    private static final List<TaskID> taskIDs = new ArrayList<>(); 
    
    static void update(float tpf) {
        if (!GameIsFrozen) {
            tasks.update(tpf);
            timer.update(tpf);
        }
    }
    
    public static int frameCount() {
        return timer.getFrame();
    }
    
    public static float time() {
        return timer.getTime();
    }
    
    public static void addTaskToGlobal(SimpleProcedure task) {
        addTaskToGlobal(timer.getTime(), task);
    }
    
    public static void addTaskToGlobal(Object id, SimpleProcedure task) {
        tasks.add(task, () -> { //onFinish
            taskIDs.remove(new DependentObjectComparator(id)); //removes by id
        });
        
        taskIDs.add(new TaskID(id, tasks.size() - 1));
    }
    
    public static void addTaskToGlobal(double id, SimpleProcedure task) {
        tasks.add(task, () -> { //onFinish
            taskIDs.remove(new DependentObjectComparator(id)); //removes by id
        });
        
        taskIDs.add(new TaskID(id, tasks.size() - 1));
    }
    
    public static void addTaskToGlobal(float id, SimpleProcedure task) {
        tasks.add(task, () -> { //onFinish
            taskIDs.remove(new DependentObjectComparator(id)); //removes by id
        });
        
        taskIDs.add(new TaskID(id, tasks.size() - 1));
    }
    
    public static void addTaskToGlobal(String id, SimpleProcedure task) {
        tasks.add(task, () -> { //onFinish
            taskIDs.remove(new DependentObjectComparator(id)); //removes by id
        });
        
        taskIDs.add(new TaskID(id, tasks.size() - 1));
    }
    
    public static void removeTaskFromGlobal(Object id) {
        taskIDs.remove(new DependentObjectComparator(id)); //removes by id
    }
    
    public static void removeTaskFromGlobal(double id) {
        taskIDs.remove(new DependentObjectComparator(id)); //removes by id
    }
    
    public static void removeTaskFromGlobal(float id) {
        taskIDs.remove(new DependentObjectComparator(id)); //removes by id
    }
    
    public static void removeTaskFromGlobal(String id) {
        taskIDs.remove(new DependentObjectComparator(id)); //removes by id
    }
    
    public static void removeAllTasksFromGlobal(Object id) {
        while (taskIDs.remove(new DependentObjectComparator(id))) {}
    }
    
    public static void removeAllTasksFromGlobal(double id) {
        while (taskIDs.remove(new DependentObjectComparator(id))) {}
    }
    
    public static void removeAllTasksFromGlobal(float id) {
        while (taskIDs.remove(new DependentObjectComparator(id))) {}
    }
    
    public static void removeAllTasksFromGlobal(String id) {
        while (taskIDs.remove(new DependentObjectComparator(id))) {}
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
        //        COLOR_RANDOM = 3
        
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
        return new ColorRGBA(
            superRandomFloat(),  // R
            superRandomFloat(),  // G
            superRandomFloat(),  // B
            superRandomFloat()   // A
        );
    }
    
    public static ColorRGBA superRandomColor2() {
        if (Main.RNG.nextBoolean()) {
            return ColorRGBA.randomColor();
        } else {
            return superRandomColor();
        }
    }
}
