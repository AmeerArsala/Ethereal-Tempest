/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation.config;

import general.math.IntPair;
import general.visual.animation.DomainDelayMapping;
import java.util.List;

/**
 *
 * @author night
 */
public class ColumnDomainDelayMapping {
    private Integer column; //only has to be specified if using a spritesheet animation; otherwise it can be null
    private DomainDelayMapping animationSegment;
    
    public ColumnDomainDelayMapping(Integer column, DomainDelayMapping animationSegment) {
        this.column = column;
        this.animationSegment = animationSegment;
    }
    
    public Integer getColumn() {
        return column;
    }
    
    public DomainDelayMapping getAnimation() {
        return animationSegment;
    }
    
    public List<IntPair> generateIndexedList() {
        return animationSegment.generateIndexedList(column);
    }
    
    //sums frames
    public static int totalFrames(ColumnDomainDelayMapping[] animation) {
        int sum = 0;
        for (ColumnDomainDelayMapping segment : animation) {
            sum += segment.animationSegment.length();
        }
        
        return sum;
    } 
}
