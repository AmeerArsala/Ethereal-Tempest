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
    
    private final Path path;
    private final BiConsumer<Coords, Vector3f> deltaReactor; //deltaCoords, deltaPosition
    
    private float tilesTraversed = 0f;
    private float speed = DEFAULT_SPEED; //in percent of tile lengths (moves 'speed' tiles every second)
    
    public Movement(Path pathway, BiConsumer<Coords, Vector3f> reactor) {
        path = pathway;
        deltaReactor = reactor;
    }
    
    public Path getPath() { return path; }
    public int getTilesTraversed() { return (int)tilesTraversed; }
    public float getSpeed() { return speed; }
    public boolean isFinished() { return tilesTraversed >= path.getPathSize(); }
    
    public void update(float tpf) {
        if (!isFinished()) {
            if (keyToIncreaseSpeedPressed) {
                speed = FAST_SPEED;
            } else {
                speed = DEFAULT_SPEED;
            }
            
            List<Tile> tilePath = path.getPath();
            Coords last = tilesTraversed >= 1 ? tilePath.get((int)(tilesTraversed - 1)).getPos().getCoords() : path.getInitialPos().getCoords();
            Coords next = tilePath.get((int)tilesTraversed).getPos().getCoords();
            
            Coords deltaXY = next.subtract(last); //its coordinates will always be 1, 0, or -1
            
            float deltaPercentage = speed * tpf;
            tilesTraversed += deltaPercentage;
            
            Vector3f deltaPosition = deltaXY.toVector3fZX().multLocal(deltaPercentage * Tile.LENGTH);
            
            deltaReactor.accept(deltaXY, deltaPosition);
            
        }
    }
    
}
