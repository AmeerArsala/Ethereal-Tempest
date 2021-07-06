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
    
    private boolean xMirrored = false, yMirrored = false;
    
    public MoveArrowTile(MapCoords mapCoords, AssetManager assetManager) {
        this(mapCoords.getX(), mapCoords.getY(), mapCoords.getLayer(), assetManager);
    }
    
    public MoveArrowTile(int posX, int posY, int layer, AssetManager assetManager) {
        super(posX, posY, layer);
        patchMesh = createMesh();
        tgeometry = new Geometry("movement arrow: " + coords.toString(), patchMesh);
        node = new Node("movement arrow node: " + coords.toString());
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
        node.setLocalTranslation(coords.getY() * Tile.LENGTH, coords.getLayer() * MapLevel.LAYER_Y_DEVIATION, coords.getX() * Tile.LENGTH);
        Node tileNode = MasterFsmState.getCurrentMap().getMiscNode();
        if (!tileNode.hasChild(node)) {
            tileNode.attachChild(node);
        }
    }
    
    public void adjust(ColorRGBA visibleColor, MoveArrowTile previous, MoveArrowTile next) {
        setColor(visibleColor);
        adjust(previous.coords.getCoords(), next.coords.getCoords());
    }
    
    private void adjust(Coords previous, Coords next) {
        if (previous == null) {
            setColor(ColorRGBA.BlackNoAlpha);
            return;
        }
        
        Coords me = coords.getCoords();
        Coords deltaPrev = me.subtract(previous);
        float theta;
        if (next == null) {
            setIndex(HEAD);
            setMirrored(false, false);
            
            theta = deltaPrev.toPolar().y;
            setLocalRotation(0, theta, 0);
        } else {
            Coords deltaSecant = next.subtract(previous);
            theta = deltaSecant.toPolar().y;
            if (previous.x == next.x) {    
                setIndex(STEM);
                setMirrored(false, false);
                setLocalRotation(0, theta, 0);
            } else if (previous.y == next.y) {
                setIndex(STEM);
                setMirrored(false, false);
                setLocalRotation(0, theta, 0);
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
                            setLocalRotation(0, 0, 0);
                            setMirrored(true, false); //put emphasis on this statement, so make it second
                            break;
                        case UP_RIGHT:
                            setMirrored(false, false);
                            setLocalRotation(0, 0, 0);
                            break;
                        case DOWN_LEFT:
                            setMirrored(false, false);
                            setLocalRotation(0, FastMath.PI, 0); //put emphasis on this statement, so make it second
                            break;
                        case DOWN_RIGHT:
                            setLocalRotation(0, 0, 0);
                            setMirrored(false, true); //put emphasis on this statement, so make it second
                            break;
                    }
                } else {
                    //deltaPrev.y == 0, so deltaPrev.x != 0 is true because these coords are one space away from 'me'
                    final int LEFT_UP = -1;
                    final int RIGHT_UP = 3;
                    final int LEFT_DOWN = -3;
                    final int RIGHT_DOWN = 1;
                    
                    switch (id) {
                        case LEFT_UP:
                            setMirrored(false, false);
                            setLocalRotation(0, FastMath.HALF_PI, 0); //put emphasis on this statement, so make it second
                            break;
                        case RIGHT_UP:
                            //both statements have emphasis
                            setLocalRotation(0, FastMath.HALF_PI, 0);
                            setMirrored(true, false);
                            break;
                        case LEFT_DOWN:
                            //both statements have emphasis
                            setLocalRotation(0, FastMath.HALF_PI, 0);
                            setMirrored(false, true);
                            break;
                        case RIGHT_DOWN:
                            setMirrored(false, false);
                            setLocalRotation(0, -FastMath.HALF_PI, 0); //put emphasis on this statement, so make it second
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

    private void setLocalRotation(float x, float y, float z) {
        Quaternion rot = new Quaternion();
        rot.fromAngles(x, y, z);
        node.setLocalRotation(rot);
    }
    
    public void setMirrored(boolean mirrorX, boolean mirrorY) {
        if (mirrorX == xMirrored && mirrorY == yMirrored) return;
        
        xMirrored = mirrorX;
        yMirrored = mirrorY;
        
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
        
        patchMesh.setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]
        {
            x[0], y[0],
            x[1], y[1],
            x[2], y[2],
            x[3], y[3]
        });
    }
}
