/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package misc;

import com.jme3.math.Vector3f;

/**
 *
 * @author night
 */
public class FrameDelay {
    private final int index;
    private final float delay;
    
    private Vector3f location = null;
    
    public FrameDelay(int index, float delay) {
        this.index = index;
        this.delay = delay;
    }
    
    public FrameDelay(int index, float delay, Vector3f location) {
        this.index = index;
        this.delay = delay;
        this.location = location;
    }
    
    public int getIndex() {
        return index;
    }
    
    public float getDelay() {
        return delay;
    }
    
    public Vector3f getLocation() {
        return location;
    }
    
    public static FrameDelay[] allIntsFromTo(int start, int end, float del) {
        FrameDelay[] k = new FrameDelay[end - start + 1];
        for (int i = 0; i + start <= end; i++) {
            k[i] = new FrameDelay(i + start, del);
        }
        return k;
    }
    
    public static FrameDelay[] allIntsFromTo(int start, int end, float del, Vector3f loc) {
        FrameDelay[] k = new FrameDelay[end - start + 1];
        for (int i = 0; i + start <= end; i++) {
            k[i] = new FrameDelay(i + start, del, loc);
        }
        return k;
    }
    
    public static FrameDelay[] combineArray(FrameDelay[] values1, FrameDelay[] values2) {
        FrameDelay[] arr = new FrameDelay[values1.length + values2.length];
        System.arraycopy(values1, 0, arr, 0, values1.length);
        System.arraycopy(values2, 0, arr, values1.length, values2.length);
        return arr;
    }
}
