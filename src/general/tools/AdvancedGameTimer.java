/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.tools;

import general.tools.universal.CustomException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author night
 * 
 * use this rarely
 */
public class AdvancedGameTimer {
    private final Map<Object, Integer> frameCounters = new HashMap<>();
    private final Map<Object, Float> elapsedTimeCounters = new HashMap<>();
    
    public AdvancedGameTimer() {}
    
    public Map<Object, Integer> getFrameCounterMap() {
        return frameCounters;
    }
    
    public Map<Object, Float> getElapsedTimerMap() {
        return elapsedTimeCounters;
    }
    
    private <T> AdvancedGameTimer addNew(Object id, T val, Map<Object, T> counterMap) throws CustomException {
        if (counterMap.containsKey(id)) {
            throw new CustomException("ID already exists!");
        }
        
        counterMap.put(id, val);
        return this;
    }
    
    public AdvancedGameTimer addFrameCounter(Object id) throws CustomException {
        return addNew(id, 0, frameCounters);
    }
    
    public AdvancedGameTimer addElapsedTimer(Object id) throws CustomException {
        return addNew(id, 0f, elapsedTimeCounters);
    }
    
    public void removeFrameCounter(Object id) {
        frameCounters.remove(id);
    }
    
    public void removeElapsedTimer(Object id) {
        elapsedTimeCounters.remove(id);
    }
    
    public int getFrame(Object id) {
        return frameCounters.get(id);
    }
    
    public float getTime(Object id) {
        return elapsedTimeCounters.get(id);
    }
    
    public void setFrameCount(Object id, Integer val) {
        frameCounters.replace(id, val);
    }
    
    public void setElapsedTime(Object id, Float val) {
        elapsedTimeCounters.replace(id, val);
    }
    
    public void clear() {
        frameCounters.clear();
        elapsedTimeCounters.clear();
    }
    
    public void update(float tpf, Object... ids) {
        for (Object id : ids) {
            if (frameCounters.containsKey(id)) {
                setFrameCount(id, frameCounters.get(id) + 1);
            }
            
            if (elapsedTimeCounters.containsKey(id)) {
                setElapsedTime(id, elapsedTimeCounters.get(id) + tpf);
            }
        }
    }
    
    public void update(float tpf, List<Object> ids) {
        for (Object id : ids) {
            if (frameCounters.containsKey(id)) {
                setFrameCount(id, frameCounters.get(id) + 1);
            }
            
            if (elapsedTimeCounters.containsKey(id)) {
                setElapsedTime(id, elapsedTimeCounters.get(id) + tpf);
            }
        }
    }
}
