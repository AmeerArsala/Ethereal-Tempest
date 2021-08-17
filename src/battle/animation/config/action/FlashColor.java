/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation.config.action;

import com.jme3.math.ColorRGBA;

/**
 *
 * @author night
 */
public class FlashColor {
    public enum TimeType {
        AUTO_TIME,
        SHADER_TIME,
        FRAMES
    }
    
    private ColorRGBA color;
    private float period;
    private TimeType timeType;
    
    public FlashColor(ColorRGBA color, float period, TimeType timeType) {
        this.color = color;
        this.period = period;
        this.timeType = timeType;
    }
    
    public ColorRGBA getColor() { return color; }
    public float getPeriod() { return period; }
    public TimeType getTimeType() { return timeType; }
}
