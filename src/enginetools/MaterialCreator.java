/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enginetools;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;
import com.simsilica.lemur.core.GuiMaterial;
import java.util.function.Consumer;

/**
 *
 * @author night
 */
public class MaterialCreator {
    public static final String UNSHADED = "Common/MatDefs/Misc/Unshaded.j3md";
    
    private String materialPath = "Common/MatDefs/Misc/Unshaded.j3md";
    private Consumer<Material> paramsProtocol = (mat) -> {};
    
    public MaterialCreator() {}
    
    public MaterialCreator(String materialPath) {
        this.materialPath = materialPath;
    }
    
    public MaterialCreator(String materialPath, Consumer<Material> paramsProtocol) {
        this.materialPath = materialPath;
        this.paramsProtocol = paramsProtocol;
    }
    
    public String getMaterialPath() { return materialPath; }
    
    public Material createMaterial(AssetManager assetManager) {
        Material mat = new Material(assetManager, materialPath);
        
        initialization(assetManager, mat);
        
        paramsProtocol.accept(mat);
        
        return mat;
    }
    
    //OVERRIDE IF NEEDED IN SUBCLASSES
    protected void initialization(AssetManager assetManager, Material mat) {
    
    }
}
