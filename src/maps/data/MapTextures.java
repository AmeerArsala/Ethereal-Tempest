/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.data;

import com.jme3.texture.Texture;
import com.jme3.texture.TextureArray;

/**
 *
 * @author night
 */
public class MapTextures {
    public static final class Tiles {
        public static Texture OverflowBlendMap;
        public static TextureArray TileTextures;
        public static TextureArray MoveTileTextures;
        public static TextureArray MoveArrowTextures;
    }
    
    public static final class GUI {
        public static TextureArray StatScreenTextures;
        public static TextureArray CombatantGuiTextures;
        public static TextureArray ActionMenuTextures;
    }
    
    public static final class Sprites {
        public static TextureArray MapSpritesheetTextures;
        public static TextureArray CombatSpritesheetTextures;
    }
}
