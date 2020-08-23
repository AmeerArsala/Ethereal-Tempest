/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout;

/**
 *
 * @author night
 */
public class Coords {
        private int xcoord, ycoord;
        
        public Coords(int x, int y) {
            xcoord = x;
            ycoord = y;
        }
        
        public int getX() { return xcoord; }
        public int getY() { return ycoord; }
        
        public void setX(int x) { xcoord = x; }
        public void setY(int y) { ycoord = y; }
        
        public void setCoords(int x, int y) {
            xcoord = x;
            ycoord = y;
        }
        
        public void setCoords(Coords otro) {
            xcoord = otro.getX();
            ycoord = otro.getY();
        }
        
        public boolean equals(Coords other) {
            return xcoord == other.xcoord && ycoord == other.ycoord;
        }
        
        @Override
        public String toString() {
            return "(" + xcoord + ", " + ycoord + ") ";
        }
}
