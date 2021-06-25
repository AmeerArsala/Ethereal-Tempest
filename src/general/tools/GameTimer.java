/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.tools;

/**
 *
 * @author night
 */
public class GameTimer {
    private int frameCount = 0;
    private float elapsedTime = 0f; // in seconds
    private boolean paused = false;
    
    public GameTimer() {}
    
    public void update(float tpf) {
        if (!paused) {
            ++frameCount;
            elapsedTime += tpf;
        }
        
        if (frameCount == Integer.MAX_VALUE) {
            frameCount = 0;
        }
        
       if (Float.MAX_VALUE - elapsedTime <= 5f) {
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
    
    public void setPaused(boolean isPaused) {
        paused = isPaused;
    }
    
    public void pause() { 
        paused = true; 
    }
    
    public void resume() { 
        paused = false; 
    }
    
    public void togglePause() {
        paused = !paused;
    }
}
