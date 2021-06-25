/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.visual.animation;

import general.math.IntPair;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class DomainDelayMapping {
    //INCLUSIVE
    private int from; //from frame a
    private int to;   //to frame b
    private float delay; //in seconds
    
    public DomainDelayMapping(int from, int to, float delay) {
        this.from = from;
        this.to = to;
        this.delay = delay;
    }
    
    public int getPositionA() {
        return from;
    }
    
    public int getPositionB() {
        return to;
    }
    
    public float getDelay() {
        return delay;
    }
    
    public int length() {
        return Math.abs(to - from) + 1;
    }
    
    //generates an IntPair List with the y-values being the indexes; x-values are reserved for columns
    public List<IntPair> generateIndexedList(int column) {
        List<IntPair> indexedList = new ArrayList<>();
        for (int i = from; i <= to; ++i) {
            indexedList.add(new IntPair(column, i));
        }
        
        return indexedList;
    }
}
