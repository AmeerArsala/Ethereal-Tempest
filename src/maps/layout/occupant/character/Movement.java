/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.occupant.character;

import com.jme3.math.Vector3f;
import java.util.List;
import java.util.function.BiConsumer;
import maps.layout.Coords;
import maps.layout.MapCoords;
import maps.layout.tile.Tile;
import maps.layout.tile.move.Path;

/**
 *
 * @author night
 */
public class Movement {
    public static final float DEFAULT_SPEED = 7.5f;
    public static final float FAST_SPEED = 15f;
    
    public static boolean keyToIncreaseSpeedPressed = false;
    
    private final MapCoords initialPos;
    private final MapCoords[] tilePath;
    private final BiConsumer<Coords, Vector3f> deltaReactor; //deltaCoords, deltaPosition
    
    private float tilesTraversed = 0f;
    private float speed = DEFAULT_SPEED; //in percent of tile lengths (moves 'speed' tiles every second)
    
    public Movement(MapCoords initialPosition, MapCoords[] pathway, BiConsumer<Coords, Vector3f> reactor) {
        initialPos = initialPosition;
        tilePath = pathway;
        deltaReactor = reactor;
    }
    
    public Movement(Path pathway, BiConsumer<Coords, Vector3f> reactor) {
        this(pathway.getInitialPos(), pathway.asArray(), reactor);
    }
    
    public MapCoords[] getTilePath() { return tilePath; }
    public int getTilesTraversed() { return (int)tilesTraversed; }
    public float getSpeed() { return speed; }
    public boolean isFinished() { return tilesTraversed >= tilePath.length; }
    
    public MapCoords getInitialPos() { return initialPos; }
    public MapCoords getFinalPos() { return tilePath.length > 0 ? tilePath[tilePath.length - 1] : initialPos; }
    
    public void update(float tpf) {
        if (!isFinished()) {
            if (keyToIncreaseSpeedPressed) {
                speed = FAST_SPEED;
            } else {
                speed = DEFAULT_SPEED;
            }

            Coords last = tilesTraversed < 1 ? initialPos.getCoords() : tilePath[(int)(tilesTraversed - 1)].getCoords();
            Coords next = tilePath[(int)tilesTraversed].getCoords();
            
            Coords deltaXY = next.subtract(last); //its coordinates will always be 1, 0, or -1
            
            float deltaPercentage = speed * tpf;
            tilesTraversed += deltaPercentage;
            
            Vector3f deltaPosition = deltaXY.toVector3fZX().multLocal(deltaPercentage * Tile.SIDE_LENGTH);
            
            deltaReactor.accept(deltaXY, deltaPosition);
            
        }
    }
    
}