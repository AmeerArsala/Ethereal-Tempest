/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.tile.move;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.shader.VarType;
import com.simsilica.lemur.LayerComparator;
import enginetools.math.SpatialOperator;
import etherealtempest.fsm.MasterFsmState;
import maps.data.MapTextures;
import maps.layout.Coords;
import maps.layout.MapCoords;
import maps.layout.MapLevel;
import maps.layout.tile.Tile;
import maps.layout.tile.TileFoundation;

/**
 *
 * @author night
 */
public class MoveArrowTile extends TileFoundation {
    public static final int HEAD = 0, STEM = 1, TURN = 2;
    
    private final Node node;
    private final Material mat;
    private int index;
    
    public MoveArrowTile(MapCoords mapCoords, AssetManager assetManager) {
        this(mapCoords.getX(), mapCoords.getY(), mapCoords.getLayer(), assetManager);
    }
    
    public MoveArrowTile(int posX, int posY, int layer, AssetManager assetManager) {
        super(posX, posY, layer);
        patchMesh = createMesh();
        tgeometry = new Geometry("movement arrow: " + coords.toString(), patchMesh);
        node = new Node("movement arrow node: " + coords.toString());
        //anchor = new SpatialOperator(tgeometry, new Vector3f(Tile.SIDE_LENGTH, 0, Tile.SIDE_LENGTH), new Vector3f(0.5f, 0, 0.5f), SpatialOperator.ORIGIN_TOP_LEFT);
        index = STEM; //most will be stems so the index starts at STEM as a micro-optimization 
        
        mat = new Material(assetManager, "MatDefs/custom/ArrayTexture.j3md");
        mat.setTextureParam("TexArray", VarType.TextureArray, MapTextures.Tiles.MoveArrowTextures);
        mat.setInt("TexArrayIndex", index);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.getAdditionalRenderState().setDepthWrite(false);
        
        tgeometry.setQueueBucket(RenderQueue.Bucket.Transparent);
        tgeometry.setMaterial(mat);
        
        node.attachChild(tgeometry);
        LayerComparator.setLayer(node, 3);
    }
    
    public Node getNode() { return node; }
    public Vector3f getWorldTranslation() { return node.getWorldTranslation(); }
    public Vector3f getLocalTranslation() { return node.getLocalTranslation(); }
    
    public int getIndex() {
        return index;
    }
    
    public void setColor(ColorRGBA color) {
        mat.setColor("Color", color);
    }
    
    public void setIndex(int nextIndex) {
        index = nextIndex;
        mat.setInt("TexArrayIndex", index);
    }
    
    public void attachToCurrentMapIfNotAlready() {
        node.setLocalTranslation(coords.getY() * Tile.SIDE_LENGTH, coords.getLayer() * MapLevel.LAYER_Y_DEVIATION, coords.getX() * Tile.SIDE_LENGTH);
        Node tileNode = MasterFsmState.getCurrentMap().getMiscNode();
        if (!tileNode.hasChild(node)) {
            tileNode.attachChild(node);
        }
    }
    
    public void adjust(MoveArrowTile previous, MoveArrowTile next) {
        Coords prev, nex;
        if (previous == null) {
            prev = null;
        } else {
            prev = previous.coords.getCoords();
        }
        
        if (next == null) {
            nex = null;
        } else {
            nex = next.coords.getCoords();
        }
        
        adjust(prev, nex);
    }
    
    private void adjust(Coords previous, Coords next) {
        Coords me = coords.getCoords();
        Coords deltaPrev = me.subtract(previous);
        int theta;
        if (next == null) {
            theta = (int)(FastMath.RAD_TO_DEG * deltaPrev.toPolar().y) + 360;
            setIndex(HEAD);
            changeTexCoords(false, false, theta);
        } else {
            Coords deltaSecant = next.subtract(previous);
            theta = (int)(FastMath.RAD_TO_DEG * deltaSecant.toPolar().y) + 360;
            if (previous.x == next.x) {
                setIndex(STEM);
                changeTexCoords(false, false, theta);
            } else if (previous.y == next.y) {
                setIndex(STEM);
                changeTexCoords(false, false, theta);
            } else {
                setIndex(TURN);
                
                int id = deltaPrev.add(deltaSecant).sum();
                if (deltaPrev.y != 0) {
                    final int UP_LEFT = 1;
                    final int UP_RIGHT = 3;
                    final int DOWN_LEFT = -3;
                    final int DOWN_RIGHT = -1;
                    
                    switch (id) {
                        case UP_LEFT:
                            changeTexCoords(false, true, 0);
                            break;
                        case UP_RIGHT:
                            //default
                            changeTexCoords(false, false, 0);
                            break;
                        case DOWN_LEFT:
                            changeTexCoords(true, true, 0);
                            break;
                        case DOWN_RIGHT:
                            changeTexCoords(true, false, 0);
                            break;
                    }
                } else { //deltaPrev.y == 0, so deltaPrev.x != 0 is true because these coords are one space away from 'me'
                    final int LEFT_UP = -1;
                    final int RIGHT_UP = 3;
                    final int LEFT_DOWN = -3;
                    final int RIGHT_DOWN = 1;
                    
                    switch (id) {
                        case LEFT_UP:
                            changeTexCoords(true, true, 270);
                            break;
                        case RIGHT_UP:
                            changeTexCoords(true, false, 90);
                            break;
                        case LEFT_DOWN:
                            changeTexCoords(false, true, 90);
                            break;
                        case RIGHT_DOWN:
                            changeTexCoords(false, false, 270);
                            break;
                    }
                }
            }
        }
    }
    
    @Override
    public void setLocalTranslation(Vector3f translation) {
        node.setLocalTranslation(translation);
    }
    
    @Override
    public void setLocalTranslation(float x, float y, float z) {
        node.setLocalTranslation(x, y, z);
    }
    
    public void changeTexCoords(boolean mirrorX, boolean mirrorY, int rotationInDegrees) {
        Vector2f[] texCoords = texCoordsFor(mirrorX, mirrorY, rotationInDegrees);
        patchMesh.setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]
        {
            texCoords[0].x, texCoords[0].y,
            texCoords[1].x, texCoords[1].y,
            texCoords[2].x, texCoords[2].y,
            texCoords[3].x, texCoords[3].y
        });
    }
    
    private Vector2f[] texCoordsFor(boolean mirrorX, boolean mirrorY, int degreesRotated) {
        Vector2f[] texCoords = new Vector2f[4];
        int startIndex = (degreesRotated / 90) % texCoords.length;
        
        float[] x;
        float[] y;
        
        if (mirrorX) {
            x = new float[]{ 1, 0, 0, 1 }; //mirrored; clockwise / x values inverted (0 goes to 1 and 1 goes to 0)
        } else {
            x = new float[]{ 0, 1, 1, 0 }; //not mirrored; counter clockwise / x values not inverted 
        }
        
        if (mirrorY) {
            y = new float[]{ 1, 1, 0, 0 }; //mirrored; clockwise / y values inverted (0 goes to 1 and 1 goes to 0)
        } else {
            y = new float[]{ 0, 0, 1, 1 }; //not mirrored; counter clockwise / y values not inverted
        }
        
        for (int i = startIndex; i < texCoords.length; i++) { // i: [startIndex, texCoords.length)
            texCoords[i - startIndex] = new Vector2f(x[i], y[i]); //sets: [0, texCoords.length - startIndex)
        }
        
        for (int i = texCoords.length - startIndex; i < texCoords.length; i++) { // i: [0, startIndex)
            int c = i - (texCoords.length - startIndex);
            texCoords[i] = new Vector2f(x[c], y[c]); //sets: [0, startIndex)
        }
        
        return texCoords;
    }
}
