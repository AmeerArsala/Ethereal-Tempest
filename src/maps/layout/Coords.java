/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class Coords {
    public int x, y;
    private Integer range = null;
    
    public Coords(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public Coords() {} //only use this if you are going to set coords later
        
    public int x() { return x; }
    public int y() { return y; }
        
    public int getRange() { return range != null ? range : 0; }
        
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
        
    public void setRange(int r) {
        range = r;
    }
        
    public void setCoords(int x, int y) {
        this.x = x;
        this.y = y;
    }
        
    public void setCoords(Coords otro) {
        x = otro.x;
        y = otro.y;
    }
        
    public void addX(int dx) { x += dx; }
    public void addY(int dy) { y += dy; }
        
    public Coords multiply(Coords other) {
        return new Coords(x * other.x, y * other.y);
    }
        
    public Coords multiply(float dx, float dy) {
        return new Coords((int)(x * dx), (int)(y * dy));
    }
        
    public Coords multiply(float factor) {
        return new Coords((int)(x * factor), (int)(y * factor));
    }
        
    public Coords add(Coords other) {
        return new Coords(x + other.x, y + other.y);
    }
        
    public Coords add(int dx, int dy) {
        return new Coords(x + dx, y + dy);
    }
    
    public Coords add(int num) {
        return new Coords(x + num, y + num);
    }

    public Coords subtract(Coords other) {
        return new Coords(x - other.x, y - other.y);
    }

    public Coords subtract(int dx, int dy) {
        return new Coords(x - dx, y - dy);
    }
    
    public Coords subtract(int num) {
        return new Coords(x - num, y - num);
    }

    public void multiplyLocal(Coords other) {
        x *= other.x;
        y *= other.y;
    }

    public void multiplyLocal(float x, float y) {
        this.x *= x;
        this.y *= y;
    }

    public void multiplyLocal(float factor) {
        x *= factor;
        y *= factor;
    }
        
    public void addLocal(Coords other) {
        x += other.x;
        y += other.y;
    }
        
    public void addLocal(int x, int y) {
        this.x += x;
        this.y += y;
    }
        
    public void addLocal(int num) {
        x += num;
        y += num;
    }
        
    public void subtractLocal(Coords other) {
        x -= other.x;
        y -= other.y;
    }
        
    public void subtractLocal(int x, int y) {
        this.x -= x;
        this.y -= y;
    }
        
    public void subtractLocal(int num) {
        x -= num;
        y -= num;
    }
    
    public int nonDiagonalDistanceFrom(Coords other) { //x + y distance between 2 Coords
        return Math.abs(other.x - x) + Math.abs(other.y - y);
    }
    
    public Coords signsOf() {
        return new Coords(FastMath.sign(x), FastMath.sign(y));
    }
    
    public Coords duplicate() {
        Coords cds = new Coords(x, y);
        
        if (range != null) {
            cds.setRange(range);
        }
        
        return cds;
    }
    
    public boolean equals(Coords other) {
        return x == other.x && y == other.y;
    }
        
    public boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }
        
    @Override
    public boolean equals(Object o) {
        if (o instanceof Coords) {
            return equals((Coords)o);
        }
        
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.x;
        hash = 53 * hash + this.y;
        return hash;
    }
        
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
    
    public Vector2f toVector2f() {
        return new Vector2f(x, y);
    }
    
    public Vector3f toVector3fXY() {
        return new Vector3f(x, y, 0);
    }
    
    public Vector3f toVector3fZX() {
        return new Vector3f(y, 0, x);
    }
        
    public static List<Coords> purgeDuplicates(List<Coords> seq) {
        List<Coords> nueva = new ArrayList<>();
        for (Coords cds : seq) {
            if (!nueva.contains(cds)) {
                nueva.add(cds);
            }
        }
            
        return nueva;
    }
}
