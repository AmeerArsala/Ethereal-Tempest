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
        public static Texture Cursor;
        
        public static Texture OverflowBlendMap;
        public static TextureArray TileTextures;
        public static TextureArray MoveArrowTextures;
        
        //maybe change this later
        public static Texture Blue_Move;
        public static Texture Red_Attack;
    }
    
    public static final class GUI {
        public static Texture RoundedBlackBox; //with white outline
        public static Texture Nametag; //the red one
        public static Texture GlowBox1;
        
        public static final class ActionMenu {
            public static Texture HoveredBG, NotHoveredBG;
            public static Texture ABILITY, ATTACK, ETHER, SKILL, CHAIN_ATTACK, FORMATION, ITEM, TRADE, STANDBY;
        }
        
        public static final class StatMenu {
            public static Texture WindowBG, SideTab;
            public static Texture PortraitBackdrop, PortraitLeafBorder;
            public static Texture ItemsLogo, FormulasLogo, SkillsLogo, AbilitiesLogo;
            public static Texture InventoryBorder, FormulasBorder, SkillsBackdrop;
            public static Texture ClockBorder, TalentsBorder;
        }
        
        public static final class Fighter {
            public static Texture FighterBorder;
            public static Texture HP_Heart, TP_Ball;
            public static Texture LevelUpLogo, LevelUpPage;
            public static Texture Arrow;
        }
        
        public static final class Stat {
            public static Texture STR;
            public static Texture ETHER;
            public static Texture AGI;
            public static Texture COMP;
            public static Texture DEX;
            public static Texture DEF;
            public static Texture RSL;
            public static Texture MOBILITY;
            public static Texture PHYSIQUE;
            
            public static Texture[] asArray() {
                return new Texture[] {
                    STR, ETHER, AGI, COMP, DEX, DEF, RSL, MOBILITY, PHYSIQUE
                };
            }
        }
        
        public static final class FormationType {
            public static Texture Aquarius;
            public static Texture Aries;
            public static Texture Cancer;
            public static Texture Capricorn;
            public static Texture Gemini;
            public static Texture Leo;
            public static Texture Libra;
            public static Texture Pisces;
            public static Texture Sagittarius;
            public static Texture Scorpio;
            public static Texture Taurus;
            public static Texture Virgo;
            
            public static Texture[] asArray() {
                return new Texture[] {
                    Aquarius, Pisces, Leo, Scorpio, Aries, Libra, Gemini, Virgo, Capricorn, Sagittarius, Taurus, Cancer
                };
            }
        }
        
        public static final class ItemAndFormula {
            public static Texture Sword = null;
            public static Texture Axe = null;
            public static Texture Polearm = null;
            public static Texture Knife = null;
            public static Texture Whip = null;
            public static Texture Bow = null;
            public static Texture Crossbow = null;
            public static Texture Hammer = null;
            public static Texture Club = null;
            public static Texture MartialArts = null;
            public static Texture MonsterWeapon = null;
            public static Texture DeltaEther = null;
            public static Texture GammaEther = null;
            public static Texture PiEther = null;
            public static Texture OmegaEther = null;
            public static Texture AlphaEther = null;
        }
        
        //public static final class Skill {}
        //public static final class Talent {}
    }
    
    public static final class Sprites {
        public static TextureArray MapSpritesheetTextures;
        public static TextureArray CombatSpritesheetTextures;
    }
}
