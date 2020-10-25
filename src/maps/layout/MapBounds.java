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
public interface MapBounds {
    public Coords[][] getBoundsForAllLayers();
    
    public int getXLength(int layer);
    public int getMinimumX(int layer);
    
    public int getYLength(int layer);
    public int getMinimumY(int layer);
    
    public boolean isWithinXBounds(int test, int layer);
    public boolean isWithinYBounds(int test, int layer);
    public boolean isWithinBounds(Coords test, int layer);
    
}
