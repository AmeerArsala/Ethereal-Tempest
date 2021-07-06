/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author night
 */
public class MapCoords {
    private final Coords coords;
    private int layer;
    
    public MapCoords(Coords coords, int layer) {
        this.coords = coords;
        this.layer = layer;
    }
    
    //only use this when you will set the layer later
    public MapCoords(Coords coords) {
        this.coords = coords;
    }
    
    //only use this when you will set the coords later
    public MapCoords(int layer) {
        this.layer = layer;
        coords = new Coords();
    }
    
    //only use this when you will set the coords and layer later
    public MapCoords() {
        coords = new Coords();
    }
    
    public Coords getCoords() {
        return coords;
    }
    
    public int getX() {
        return coords.x;
    }
    
    public int getY() {
        return coords.y;
    }
    
    public int getLayer() {
        return layer;
    }
    
    public MapCoords set(MapCoords cds) {
        coords.setCoords(cds.coords);
        layer = cds.layer;
        return this;
    }
    
    public void setCoords(Coords cds) {
        coords.setCoords(cds);
    }
    
    public void setCoords(int x, int y) {
        coords.setCoords(x, y);
    }
    
    public void setLayer(int lyr) {
        layer = lyr;
    }
    
    public void setPosition(Coords cds, int lyr) {
        coords.setCoords(cds);
        layer = lyr;
    } 
    
    public void setPosition(int x, int y, int lyr) {
        coords.setCoords(x, y);
        layer = lyr;
    }
    
    public void setPosition(MapCoords mpcds) {
        coords.setCoords(mpcds.coords);
        layer = mpcds.layer;
    }
    
    public int spacesFrom(MapCoords other) {
        if (layer != other.layer) {
            throw new ArithmeticException();
        }
        
        return coords.nonDiagonalDistanceFrom(other.coords);
    }
    
    public MapCoords addLocal(int dx, int dy) {
        coords.x += dx;
        coords.y += dy;
        
        return this;
    }
    
    public MapCoords addLocal(Coords dxy) {
        coords.addLocal(dxy);
        return this;
    }
    
    public MapCoords subtractLocal(int dx, int dy) {
        coords.x -= dx;
        coords.y -= dy;
        
        return this;
    }
    
    public MapCoords subtractLocal(Coords dxy) {
        coords.subtractLocal(dxy);
        return this;
    }
    
    //the methods below all assume that layer is already set
    public MapCoords add(int dx, int dy) {
        return new MapCoords(coords.add(dx, dy), layer);
    }
    
    public MapCoords add(Coords dxy) {
        return new MapCoords(coords.add(dxy), layer);
    }
    
    public MapCoords subtract(int dx, int dy) {
        return new MapCoords(coords.subtract(dx, dy), layer);
    }
    
    public MapCoords subtract(Coords dxy) {
        return new MapCoords(coords.subtract(dxy), layer);
    }
    
    public MapCoords shallowDuplicate() {
        return new MapCoords(coords, layer);
    }
    
    public MapCoords deepDuplicate() {
        return new MapCoords(coords.duplicate(), layer);
    }
    
    public <T> T getRowXColYfrom(List<T[][]> elementGrids) {
        return elementGrids.get(layer)[coords.x][coords.y];
    }
    
    public <T> T getRowXColYfrom(T[][][] elementGrids) {
        return elementGrids[layer][coords.x][coords.y];
    }
    
    public <T> T getRowYColXfrom(List<T[][]> elementGrids) {
        return elementGrids.get(layer)[coords.y][coords.x];
    }
    
    public <T> T getRowYColXfrom(T[][][] elementGrids) {
        return elementGrids[layer][coords.y][coords.x];
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof MapCoords) {
            MapCoords other = (MapCoords)o;
            return coords.equals(other.coords) && layer == other.layer;
        }
        
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.coords);
        hash = 97 * hash + this.layer;
        return hash;
    }
    
    @Override
    public String toString() {
        return coords.toString() + " layer = " + layer; 
    }
}
