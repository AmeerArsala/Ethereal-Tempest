/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.tile.move;

import com.jme3.asset.AssetManager;
import etherealtempest.fsm.MasterFsmState;
import java.util.ArrayList;
import java.util.List;
import maps.data.MapTextures;
import maps.layout.Coords;
import maps.layout.MapLevel;
import maps.layout.MapCoords;
import maps.layout.occupant.character.TangibleUnit;
import maps.layout.occupant.VenturePeek;

/**
 *
 * @author night
 */
public class RangeDisplay {
    private final AssetManager assetManager;
    private final MapLevel map; //maybe take off final later
    private final List<MoveSquare> displayedMovSquares = new ArrayList<>();
    private final List<MoveSquare> displayedAtkSquares = new ArrayList<>();
    
    public RangeDisplay(MapLevel map, AssetManager assetManager) {
        this.map = map;
        this.assetManager = assetManager;
    }
    
    public void displayRange(TangibleUnit tu, float tileOpacity) {
        cancelRange();
        
        List<MapCoords> possibleSpaces = VenturePeek.filledCoordsForTilesOfRange(tu.getMOBILITY(), tu.getPos());
        for (MapCoords possible : possibleSpaces) {
            if (shouldDisplayTile(tu.getPos(), possible, tu.getMOBILITY(), map)) {
                //reveal at specified opacity in blue tile if unit can move to it
                MoveSquare movSquare = map.getMovSquareAt(possible);
                movSquare.setTexture(MapTextures.Tiles.Blue_Move);
                movSquare.setOpacity(tileOpacity);
                displayedMovSquares.add(movSquare);
            }
        }
        
        List<MapCoords> attackTilePositions = calculateAttackTilePositions(tu);
        attackTilePositions.forEach((coordinates) -> {
            MoveSquare tile = map.getMovSquareAt(coordinates);
            tile.setTexture(MapTextures.Tiles.Red_Attack);
            tile.setOpacity(tileOpacity);
            displayedAtkSquares.add(tile);
        });
    }
    
    private List<MapCoords> calculateAttackTilePositions(TangibleUnit tu) {
        List<MapCoords> tileCoordinates = new ArrayList<>();
        
        List<Integer> maxRange = tu.getFullOffensiveRange();
        
        //a bounding box of area
        int furthest = maxRange.get(maxRange.size() - 1);
        int boundingBoxLength = (((tu.getMOBILITY() + furthest) * 2) + 1);
        int centerXY = tu.getMOBILITY() + furthest; //unit position on grid, center of this 2D boolean array
        //int xDiff = tu.getPos().x() - centerXY, yDiff = tu.getPos().y() - centerXY; //same place subtracts from so finds difference
        MapCoords diff = tu.getPos().subtract(centerXY, centerXY); //same place subtracts from so finds difference
        
        for (int x = 0; x < boundingBoxLength; x++) {
            for (int y = 0; y < boundingBoxLength; y++) {
                MapCoords input = diff.add(x, y);
                if (map.isWithinBounds(input) && !displayedMovSquares.contains(map.getMovSquareAt(input))) {
                    //check if the difference is small enough
                    for (Integer range : maxRange) {
                        if (isXSpacesFromMovSquare(input, range)) {
                            tileCoordinates.add(input);
                        }
                    }
                }
            }
        }
        
        return tileCoordinates;
    }
    
    public void cancelRange() {
        displayedMovSquares.forEach((square) -> {
            square.setOpacity(0);
        });
        
        displayedMovSquares.clear();
        
        displayedAtkSquares.forEach((square) -> {
            square.setTexture(MapTextures.Tiles.Blue_Move);
            square.setOpacity(0);
        });
            
        displayedAtkSquares.clear();
    }
    
    private boolean isXSpacesFromMovSquare(MapCoords coords, int X) {
        return displayedMovSquares.stream().anyMatch((square) -> (square.getPos().getCoords().nonDiagonalDistanceFrom(coords.getCoords()) == X));
    }
    
    public boolean isDisplayedMoveSquare(MapCoords coords) {
        return displayedMovSquares.stream().anyMatch((square) -> (coords.equals(square.getPos())));
    }
    
    public boolean isDisplayedAttackSquare(MapCoords coords) {
        return displayedAtkSquares.stream().anyMatch((square) -> (coords.equals(square.getPos())));
    }
    
    public static boolean shouldDisplayTile(Coords start, Coords dest, int layer, int moveCapacity) {
        return shouldDisplayTile(start, dest, layer, moveCapacity, MasterFsmState.getCurrentMap());
    }
    
    public static boolean shouldDisplayTile(Coords start, Coords dest, int layer, int moveCapacity, MapLevel mp) {
        if (start.equals(dest)) {
            return true;
        }
        
        return new Path(start, dest, layer, moveCapacity).wasSuccess();
    }
    
    public static boolean shouldDisplayTile(MapCoords start, MapCoords dest, int moveCapacity, MapLevel mp) {
        if (start.equals(dest)) {
            return true;
        }
        
        return new Path(start, dest, moveCapacity, mp).wasSuccess();
    }
    
    public static boolean shouldDisplayTile(MapCoords start, MapCoords dest, int moveCapacity) {
        if (start.equals(dest)) {
            return true;
        }
        
        return new Path(start, dest, moveCapacity).wasSuccess();
    }
}
