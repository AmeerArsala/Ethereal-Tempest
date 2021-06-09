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
    private int x, y;
    private boolean xSet, ySet;
    
    public IntPair(int x, int y) {
        this.x = x;
        this.y = y;
        
        xSet = true;
        ySet = true;
    }
    
    public IntPair() {
        xSet = false;
        ySet = false;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public boolean isXSet() {
        return xSet;
    }
    
    public boolean isYSet() {
        return ySet;
    }
    
    public IntPair setX(int xval) {
        x = xval;
        xSet = true;
        return this;
    }
    
    public IntPair setY(int yval) {
        y = yval;
        ySet = true;
        return this;
    }
    
    public void removeX() { xSet = false; }
    public void removeY() { ySet = false; }
}
