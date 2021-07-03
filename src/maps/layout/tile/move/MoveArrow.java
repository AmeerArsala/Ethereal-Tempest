/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.tile.move;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.simsilica.lemur.LayerComparator;
import enginetools.MaterialCreator;
import maps.layout.tile.TileFoundation;

/**
 *
 * @author night
 */
public class MoveArrow extends TileFoundation {
    
    public MoveArrow(int posX, int posY, int layer, AssetManager assetManager) {
        super(posX, posY, layer);
        patchMesh = createMesh();
        tgeometry = new Geometry("movement arrow: " + "(" + posX + ", " + posY + ")", patchMesh);
        
        Material mat = new Material(assetManager, MaterialCreator.UNSHADED);
        //mat.setTexture("")
        //mat.setColor("Color", color);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.getAdditionalRenderState().setDepthWrite(false);
        
        tgeometry.setQueueBucket(RenderQueue.Bucket.Transparent);
        tgeometry.setMaterial(mat);
        
        LayerComparator.setLayer(tgeometry, 3);
    }
    
}
