/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class Coords {
        private int xcoord, ycoord;
        
        private Integer range = null;
        
        public Coords(int x, int y) {
            xcoord = x;
            ycoord = y;
        }
        
        public int getX() { return xcoord; }
        public int getY() { return ycoord; }
        
        public int getRange() { return range; }
        
        public void setX(int x) { xcoord = x; }
        public void setY(int y) { ycoord = y; }
        
        public void setRange(int r) {
            range = r;
        }
        
        public void setCoords(int x, int y) {
            xcoord = x;
            ycoord = y;
        }
        
        public void setCoords(Coords otro) {
            xcoord = otro.getX();
            ycoord = otro.getY();
        }
        
        public void addX(int x) { xcoord += x; }
        public void addY(int y) { ycoord += y; }
        
        public Coords combine(Coords other) {
            return new Coords(xcoord + other.xcoord, ycoord + other.ycoord);
        }
        
        public Coords multiply(float x, float y) {
            return new Coords((int)(xcoord * x), (int)(ycoord * y));
        }
        
        public int difference(Coords other) {
            return (int)(Math.abs(other.xcoord - xcoord) + Math.abs(other.ycoord + ycoord));
        }
        
        public boolean equals(Coords other) {
            return xcoord == other.xcoord && ycoord == other.ycoord;
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
        
        @Override
        public boolean equals(Object o) {
            if (o instanceof Coords) {
                return equals((Coords)o);
            }
            
            return this == o;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 53 * hash + this.xcoord;
            hash = 53 * hash + this.ycoord;
            return hash;
        }
        
        @Override
        public String toString() {
            return "(" + xcoord + ", " + ycoord + ") ";
        }
}
