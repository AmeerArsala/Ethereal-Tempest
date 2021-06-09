/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class Globals {
    private static final Globals globalInstance = new Globals();
    private static final List<Runnable> globalTasks = new ArrayList<>();
    
    private int frameCount = 0;
    private float elapsedTime = 0f; // in seconds
    private boolean paused = false;
    
    public Globals() {}
    
    public void update(float tpf) {
        if (!paused) {
            ++frameCount;
            elapsedTime += tpf;
        }
        
        if (frameCount == Integer.MAX_VALUE) {
            frameCount = 0;
        }
        
       if (Float.MAX_VALUE - elapsedTime <= 7f) {
           elapsedTime = 0f;
       }
    }
    
    public int getFrame() {
        return frameCount;
    }
    
    public float getTime() {
        return elapsedTime;
    }
    
    public boolean isPaused() {
        return paused;
    }
    
    public void reset() {
        frameCount = 0;
        elapsedTime = 0f;
    }
    
    public void setFrame(int frame) {
        frameCount = frame;
    }
    
    public void setTime(float time) {
        elapsedTime = time;
    }
    
    public void pause() { paused = true; }
    public void resume() { paused = false; }
    
    public void setPaused(boolean isPaused) {
        paused = isPaused;
    }
    
    
    static void updateGlobalInstance(float tpf) {
        globalInstance.update(tpf);
        
        globalTasks.forEach((task) -> {
            task.run();
        });
    }
    
    public static int frameCount() {
        return globalInstance.frameCount;
    }
    
    public static float time() {
        return globalInstance.elapsedTime;
    }
    
    public static void addTaskToGlobal(Runnable task) {
        globalTasks.add(task);
    }
    
    public static void removeTaskFromGlobal(Runnable task) {
        globalTasks.remove(task);
    }
    
}
