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
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.texture.Texture;
import com.simsilica.lemur.LayerComparator;
import maps.layout.tile.TileFoundation;

/**
 *
 * @author night
 */
public class MoveSquare extends TileFoundation {
    public static final String MOVE_SQUARE_TEX_PATH = "Textures/tiles/movsquare.png";
    public static final String ATTACK_SQUARE_TEX_PATH = "Textures/tiles/atksquare.png";

    public MoveSquare(int posX, int posY, int layer, AssetManager assetManager) {
        super(posX, posY, layer);
        
        patchMesh = createMesh();
        tgeometry = new Geometry("movesquare: (" + posX + ", " + posY + ")", patchMesh);
        
        Material movsquare = new Material(assetManager, "MatDefs/custom/RangeTile.j3md"); // MatDefs/custom/RangeTile.j3md vs. Common/MatDefs/Misc/Unshaded.j3md
        movsquare.setTexture("ColorMap", assetManager.loadTexture(MOVE_SQUARE_TEX_PATH));
        movsquare.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        movsquare.setColor("Color", new ColorRGBA(1, 1, 1, 0));
        movsquare.setFloat("RequiredOpacityToAnimate", 0.5f);
        movsquare.setFloat("MinimumAmplitude", 1.0f);
        movsquare.setFloat("CoefficientIncrement", 1.5f);
        movsquare.setFloat("Frequency", 1.5f);
        movsquare.setFloat("Thickness", 0.175f);
        movsquare.getAdditionalRenderState().setDepthWrite(false);
        
        tgeometry.setMaterial(movsquare);
        tgeometry.setQueueBucket(RenderQueue.Bucket.Transparent);
        LayerComparator.setLayer(tgeometry, 2);
    }
    
    public void setOpacity(float opacity) {
        tgeometry.getMaterial().setColor("Color", new ColorRGBA(1, 1, 1, opacity));
    }
    
    public void setTexture(Texture tex) {
        tgeometry.getMaterial().setTexture("ColorMap", tex);
    }
}
