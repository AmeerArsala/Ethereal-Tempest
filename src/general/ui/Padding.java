/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.ui;

/**
 *
 * @author night
 */
public class Padding {
    public float top = 0f, left = 0f, bottom = 0f, right = 0f;
    
    public Padding(float top, float left, float bottom, float right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }
    
    public Padding() {}
    
    public float getTop() { return top; }
    public float getLeft() { return left; }
    public float getBottom() { return bottom; }
    public float getRight() { return right; }
    
    public float getTotalVerticalPadding() { return top + bottom; }
    public float getTotalHorizontalPadding() { return left + right; }
}
