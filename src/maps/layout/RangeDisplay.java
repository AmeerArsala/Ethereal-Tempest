/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout;

import battle.Catalog;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.terrain.geomipmap.TerrainPatch;
import com.jme3.texture.Texture;
import general.GeneralUtils;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class RangeDisplay {
    public float tileOpacity = 0;
    private final Map mp; //maybe take off final later
    private List<Tile> displayedMovSquares = null;
    private List<Geometry> displayedAtkSquares = null;
    
    public RangeDisplay(Map mp) {
        this.mp = mp;
    }
    
    public void displayRange(TangibleUnit tu, int layer, AssetManager assetManager) {
        cancelRange(layer);
        displayedMovSquares = new ArrayList<>();
        displayedAtkSquares = new ArrayList<>();
        
        for (int x = 0; x < mp.fullmap[layer].length; x++) {
            for (int y = 0; y < mp.fullmap[layer][0].length; y++) {
                if (Map.isWithinSpaces(tu.getMOBILITY(), tu.getPosX(), tu.getPosY(), mp.fullmap[layer][x][y].getPosX(), mp.fullmap[layer][x][y].getPosY()) 
                    && shouldDisplayTile(tu, x, y, layer)) {
                    //reveal at specified opacity in blue tile if unit can move to it
                    mp.movSet[layer][x][y].getPatchMaterial().setTexture("ColorMap", assetManager.loadTexture("Textures/tiles/movsquare.png"));
                    mp.movSet[layer][x][y].getPatchMaterial().setColor("Color", new ColorRGBA(1, 1, 1, tileOpacity));
                    displayedMovSquares.add(mp.movSet[layer][x][y]);
                }
            }
        }
        
        List<Coords> attackTilePositions = calculateAttackTilePositions(tu, layer);
        attackTilePositions.forEach((coordinates) -> {
            Geometry atktile = createAttackTile(mp.movSet[layer][coordinates.getX()][coordinates.getY()], assetManager);
            //mp.movSet[layer][coordinates.getX()][coordinates.getY()].getPatchMaterial().setColor("Color", new ColorRGBA(1, 1, 1, tileOpacity));
            //mp.movSet[layer][coordinates.getX()][coordinates.getY()].getPatchMaterial().setTexture("ColorMap", assetManager.loadTexture("Textures/tiles/atksquare.png"));
            mp.getMiscNode().attachChild(atktile);
            atktile.setLocalTranslation(mp.getMiscNode().worldToLocal(mp.movSet[layer][coordinates.getX()][coordinates.getY()].getWorldTranslation(), null));
            displayedAtkSquares.add(atktile);
        });
    }
    
    private List<Coords> calculateAttackTilePositions(TangibleUnit tu, int layer) {
        List<Coords> tileCoordinates = new ArrayList<>();
        
        //a bounding box of area
        int furthest = Catalog.furthestTrue(Catalog.rangeCreator(tu.getEquippedWeapon().getRange()));
        int boundingBoxLength = (((tu.getMobility() + furthest) * 2) + 1);
        int centerXY = tu.getMobility() + furthest; //unit position on grid, center of this 2D boolean array
        int xDiff = tu.getPosX() - centerXY, yDiff = tu.getPosY() - centerXY; //same place subtracts from so finds difference
        
        for (int x = 0; x < boundingBoxLength; x++) {
            for (int y = 0; y < boundingBoxLength; y++) {
                Coords input = new Coords(x + xDiff, y + yDiff);
                if (mp.isWithinBounds(input, layer) && !displayedMovSquares.contains(mp.movSet[layer][x + xDiff][y + yDiff])) {
                    //check if the difference is small enough
                    for (Integer range : tu.getEquippedWeapon().getRange()) {
                        if (isXSpacesFromMovSquare(input, range)) {
                            tileCoordinates.add(input);
                        }
                    }
                }
            }
        }
        
        return tileCoordinates;
    }
    
    private boolean isXSpacesFromMovSquare(Coords cds, int X) {
        return displayedMovSquares.stream().anyMatch((square) -> (Math.abs(square.getPosX() - cds.getX()) + Math.abs(square.getPosY() - cds.getY()) == X));
    }
    
    private Geometry createAttackTile(Tile square, AssetManager assetManager) {
        Geometry attackTile = new Geometry("attackTile: (" + square.getPosX() + ", " + square.getPosY() + ")", square.getTileMesh());
        Material attackMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        attackMat.setTexture("ColorMap", assetManager.loadTexture("Textures/tiles/atksquare.png"));
        attackMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        attackMat.setColor("Color", new ColorRGBA(1, 1, 1, tileOpacity));
        attackTile.setMaterial(attackMat);
        attackTile.setQueueBucket(Bucket.Transparent);
        
        return attackTile;
    }
    
    public boolean shouldDisplayTile(TangibleUnit tu, int x, int y, int layer) {
        return x == tu.getPosX() && y == tu.getPosY() ? true : new Path(mp, tu.getPosX(), tu.getPosY(), x, y, layer).setMaxLength(tu.getMOBILITY()).wasSuccess();
    }
    
    public void cancelRange(int layer) {
        if (displayedMovSquares != null) {
            displayedMovSquares.forEach((square) -> {
                square.getPatchMaterial().setColor("Color", new ColorRGBA(1, 1, 1, 0));
            });
            
            displayedMovSquares = null;
        }
        if (displayedAtkSquares != null) {
            displayedAtkSquares.forEach((square) -> {
                square.getMaterial().setColor("Color", new ColorRGBA(1, 1, 1, 0));
                mp.getMiscNode().detachChild(square);
            });
            
            displayedAtkSquares = null;
        }
    }
}
