/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enginetools;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.texture.Texture;
import java.util.function.Consumer;

/**
 *
 * @author night
 */
public class TexturedMaterialCreator extends MaterialCreator {
    private final String textureParam;
    private final Texture texture;
    
    public TexturedMaterialCreator(String textureParam, Texture texture) {
        this.textureParam = textureParam;
        this.texture = texture;
    }
    
    public TexturedMaterialCreator(String textureParam, Texture texture, Consumer<Material> paramsProtocol) {
        super(MaterialCreator.UNSHADED, paramsProtocol);
        this.textureParam = textureParam;
        this.texture = texture;
    }
    
    public TexturedMaterialCreator(String materialPath, String textureParam, Texture texture, Consumer<Material> paramsProtocol) {
        super(materialPath, paramsProtocol);
        this.textureParam = textureParam;
        this.texture = texture;
    }
    
    public String getTextureParam() { return textureParam; }
    public Texture getTexture() { return texture; }
    
    @Override
    protected void initialization(AssetManager assetManager, Material mat) {
        mat.setTexture(textureParam, texture);
    }
    
}
