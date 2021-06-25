/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.math;

/**
 *
 * @author night
 */
public class IntPair {
    private Integer x, y;
    
    public IntPair(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public IntPair() {}
    
    public Integer getX() {
        return x;
    }
    
    public Integer getY() {
        return y;
    }
    
    public boolean isXSet() {
        return x != null;
    }
    
    public boolean isYSet() {
        return y != null;
    }
    
    public IntPair setX(int xval) {
        x = xval;
        return this;
    }
    
    public IntPair setY(int yval) {
        y = yval;
        return this;
    }
    
    public void removeX() { 
        x = null; 
    }
    
    public void removeY() { 
        y = null; 
    }
}
