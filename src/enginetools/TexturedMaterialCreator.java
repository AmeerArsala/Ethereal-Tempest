/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enginetools;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;

/**
 *
 * @author night
 */
public class TexturedMaterialCreator extends MaterialCreator {
    private final String textureParam;
    private final String texturePath;
    
    public TexturedMaterialCreator(String textureParam, String texturePath) {
        this.textureParam = textureParam;
        this.texturePath = texturePath;
    }
    
    public TexturedMaterialCreator(String textureParam, String texturePath, MaterialParamsProtocol paramsProtocol) {
        super(MaterialCreator.UNSHADED, paramsProtocol);
        this.textureParam = textureParam;
        this.texturePath = texturePath;
    }
    
    public TexturedMaterialCreator(String materialPath, String textureParam, String texturePath, MaterialParamsProtocol paramsProtocol) {
        super(materialPath, paramsProtocol);
        this.textureParam = textureParam;
        this.texturePath = texturePath;
    }
    
    public String getTextureParam() { return textureParam; }
    public String getTexturePath() { return texturePath; }
    
    @Override
    protected void initialization(AssetManager assetManager, Material mat) {
        mat.setTexture(textureParam, assetManager.loadTexture(texturePath));
    }
    
}
