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
public class DomainSegment {
    //INCLUSIVE
    private int from;
    private int to;
        
    public DomainSegment(int from, int to) {
        this.from = from;
        this.to = to;
    }
        
    //could mean the position values of a spritesheet
    public int getPositionA() { return from; }
    public int getPositionB() { return to; }
    
    public int length() {
        return Math.abs(to - from) + 1;
    }
}
