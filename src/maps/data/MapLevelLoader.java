/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.data;

import maps.data.MapTextures;
import com.jme3.asset.AssetManager;
import com.jme3.texture.Image;
import com.jme3.texture.TextureArray;
import java.util.ArrayList;
import java.util.List;
import maps.data.MapData;
import maps.layout.MapLevel;

/**
 *
 * @author night
 */
public class MapLevelLoader {
    public static void loadTileTextures(AssetManager assetManager, MapData mapData) {
        String prefix = "Textures/tiles/";
        
        MapTextures.Tiles.TileTextures = loadTextures(prefix, mapData.getTileTextureNamesUsed(), assetManager);
        MapTextures.Tiles.OverflowBlendMap = assetManager.loadTexture("Textures/tiles/BlendMap.png");
        
        //TODO: load move tile textures
    }
    
    public static void loadMapGuiTextures(AssetManager assetManager) {
        //TODO: load this stuff
        //MapTextures.GUI.ActionMenuTextures.get
    }
    
    public static void loadMapArrowTextures(AssetManager assetManager) {
        String prefix = "Textures/tiles/map arrow/";
        String[] names = {"head.png", "stem.png", "turn.png"};
        MapTextures.Tiles.MoveArrowTextures = loadTextures(prefix, names, assetManager);
    }
    
    public static void loadUnitTextures(AssetManager assetManager) {
        //TODO: load this stuff
    }
    
    public static TextureArray loadTextures(String prefix, String[] names, AssetManager assetManager) {
        List<Image> textures = new ArrayList<>();
        for (String name : names) {
            textures.add(assetManager.loadTexture(prefix + name).getImage());
        }
        
        return new TextureArray(textures);
    }
}
