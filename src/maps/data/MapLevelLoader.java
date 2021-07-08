/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.data;

import maps.data.MapTextures;
import com.jme3.asset.AssetManager;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.TextureArray;
import fundamental.jobclass.JobClass;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import maps.data.MapData;
import maps.layout.MapLevel;
import maps.layout.occupant.character.TangibleUnit;

/**
 *
 * @author night
 */
public class MapLevelLoader {
    public static void loadTileTextures(AssetManager assetManager, MapData mapData) {
        String prefix = "Textures/tiles/";
        
        MapTextures.Tiles.TileTextures = loadTextures(prefix, mapData.getTileTextureNamesUsed(), assetManager);
        MapTextures.Tiles.OverflowBlendMap = assetManager.loadTexture("Textures/tiles/BlendMap.png");
        MapTextures.Tiles.Blue_Move = assetManager.loadTexture("Textures/tiles/movsquare.png");
        MapTextures.Tiles.Red_Attack = assetManager.loadTexture("Textures/tiles/atksquare.png");
    }
    
    public static void loadMoveArrowTextures(AssetManager assetManager) {
        String prefix = "Textures/tiles/map arrow/";
        String[] names = {"head.png", "stem.png", "turn.png"};
        MapTextures.Tiles.MoveArrowTextures = loadTextures(prefix, names, assetManager);
        MapTextures.Tiles.Cursor = assetManager.loadTexture("Textures/gui/cursor.png");
    }
    
    public static void loadMapGuiTextures(AssetManager assetManager) {
        MapTextures.GUI.GlowBox1 = assetManager.loadTexture("Interface/GUI/ui_boxes/box5.png");
        MapTextures.GUI.Nametag = assetManager.loadTexture("Interface/GUI/ui_boxes/emptyname.png");
        MapTextures.GUI.RoundedBlackBox = assetManager.loadTexture("Interface/GUI/ui_boxes/default.png");
        
        MapTextures.GUI.ActionMenu.ABILITY = assetManager.loadTexture("Interface/GUI/action_menu/option_icons/Ability.png");
        MapTextures.GUI.ActionMenu.ATTACK = assetManager.loadTexture("Interface/GUI/action_menu/option_icons/Attack.png");
        MapTextures.GUI.ActionMenu.ETHER = assetManager.loadTexture("Interface/GUI/action_menu/option_icons/Ether.png");
        MapTextures.GUI.ActionMenu.STANDBY = assetManager.loadTexture("Interface/GUI/action_menu/option_icons/Standby.png");
        MapTextures.GUI.ActionMenu.ITEM = assetManager.loadTexture("Interface/GUI/action_menu/option_icons/Item.png");
        MapTextures.GUI.ActionMenu.FORMATION = assetManager.loadTexture("Interface/GUI/action_menu/option_icons/Formation.png");
        MapTextures.GUI.ActionMenu.SKILL = assetManager.loadTexture("Interface/GUI/action_menu/option_icons/Skill.png");
        MapTextures.GUI.ActionMenu.TRADE = assetManager.loadTexture("Interface/GUI/action_menu/option_icons/Trade.png");
        MapTextures.GUI.ActionMenu.CHAIN_ATTACK = assetManager.loadTexture("Interface/GUI/action_menu/option_icons/Chain Attack.png");
        MapTextures.GUI.ActionMenu.HoveredBG = assetManager.loadTexture("Interface/GUI/action_menu/hoveredbg.png");
        MapTextures.GUI.ActionMenu.NotHoveredBG = assetManager.loadTexture("Interface/GUI/action_menu/nothoveredbg.png");
        
        MapTextures.GUI.Fighter.FighterBorder = assetManager.loadTexture("Interface/GUI/ui_boxes/battlePortraitFrame.png");
        MapTextures.GUI.Fighter.HP_Heart = assetManager.loadTexture("Interface/GUI/common/heart.png");
        MapTextures.GUI.Fighter.TP_Ball = assetManager.loadTexture("Interface/GUI/common/tpBall.png");
        MapTextures.GUI.Fighter.Arrow = assetManager.loadTexture("Interface/GUI/common/arrow.png");
        MapTextures.GUI.Fighter.LevelUpLogo = assetManager.loadTexture("Interface/GUI/common/levelup.png");
        MapTextures.GUI.Fighter.LevelUpPage = assetManager.loadTexture("Textures/gui/page.jpg");
        
        MapTextures.GUI.FormationType.Aquarius = assetManager.loadTexture("Interface/GUI/icons/formation_type/Aquarius.png");
        MapTextures.GUI.FormationType.Aries = assetManager.loadTexture("Interface/GUI/icons/formation_type/Aries.png");
        MapTextures.GUI.FormationType.Cancer = assetManager.loadTexture("Interface/GUI/icons/formation_type/Cancer.png");
        MapTextures.GUI.FormationType.Capricorn = assetManager.loadTexture("Interface/GUI/icons/formation_type/Capricorn.png");
        MapTextures.GUI.FormationType.Gemini = assetManager.loadTexture("Interface/GUI/icons/formation_type/Gemini.png");
        MapTextures.GUI.FormationType.Leo = assetManager.loadTexture("Interface/GUI/icons/formation_type/Leo.png");
        MapTextures.GUI.FormationType.Libra = assetManager.loadTexture("Interface/GUI/icons/formation_type/Libra.png");
        MapTextures.GUI.FormationType.Pisces = assetManager.loadTexture("Interface/GUI/icons/formation_type/Pisces.png");
        MapTextures.GUI.FormationType.Sagittarius = assetManager.loadTexture("Interface/GUI/icons/formation_type/Sagittarius.png");
        MapTextures.GUI.FormationType.Scorpio = assetManager.loadTexture("Interface/GUI/icons/formation_type/Scorpio.png");
        MapTextures.GUI.FormationType.Taurus = assetManager.loadTexture("Interface/GUI/icons/formation_type/Taurus.png");
        MapTextures.GUI.FormationType.Virgo = assetManager.loadTexture("Interface/GUI/icons/formation_type/Virgo.png");
        
        MapTextures.GUI.ItemAndFormula.Axe = assetManager.loadTexture("Interface/GUI/icons/item_and_formula/Axe.png");
        MapTextures.GUI.ItemAndFormula.Sword = assetManager.loadTexture("Interface/GUI/icons/item_and_formula/Sword.png");
        MapTextures.GUI.ItemAndFormula.Bow = assetManager.loadTexture("Interface/GUI/icons/item_and_formula/Bow.png");
        MapTextures.GUI.ItemAndFormula.Polearm = assetManager.loadTexture("Interface/GUI/icons/item_and_formula/Polearm.png");
        MapTextures.GUI.ItemAndFormula.Whip = assetManager.loadTexture("Interface/GUI/icons/item_and_formula/Whip.png");
        MapTextures.GUI.ItemAndFormula.Knife = assetManager.loadTexture("Interface/GUI/icons/item_and_formula/Knife.png");
        MapTextures.GUI.ItemAndFormula.DeltaEther = assetManager.loadTexture("Interface/GUI/icons/item_and_formula/DeltaEther.png");
        MapTextures.GUI.ItemAndFormula.GammaEther = assetManager.loadTexture("Interface/GUI/icons/item_and_formula/GammaEther.png");
        MapTextures.GUI.ItemAndFormula.OmegaEther = assetManager.loadTexture("Interface/GUI/icons/item_and_formula/OmegaEther.png");
        MapTextures.GUI.ItemAndFormula.MonsterWeapon = assetManager.loadTexture("Interface/GUI/icons/item_and_formula/MonsterWeapon.png");
        
        MapTextures.GUI.Stat.AGI = assetManager.loadTexture("Interface/GUI/icons/base_stat/AGI.png");
        MapTextures.GUI.Stat.COMP = assetManager.loadTexture("Interface/GUI/icons/base_stat/COMP.png");
        MapTextures.GUI.Stat.DEF = assetManager.loadTexture("Interface/GUI/icons/base_stat/DEF.png");
        MapTextures.GUI.Stat.DEX = assetManager.loadTexture("Interface/GUI/icons/base_stat/DEX.png");
        MapTextures.GUI.Stat.ETHER = assetManager.loadTexture("Interface/GUI/icons/base_stat/ETHER.png");
        MapTextures.GUI.Stat.MOBILITY = assetManager.loadTexture("Interface/GUI/icons/base_stat/MOBILIT.png");
        MapTextures.GUI.Stat.PHYSIQUE = assetManager.loadTexture("Interface/GUI/icons/base_stat/PHYSIQU.png");
        MapTextures.GUI.Stat.RSL = assetManager.loadTexture("Interface/GUI/icons/base_stat/RSL.png");
        MapTextures.GUI.Stat.STR = assetManager.loadTexture("Interface/GUI/icons/base_stat/STR.png");
        
        MapTextures.GUI.StatMenu.AbilitiesLogo = assetManager.loadTexture("Interface/GUI/stat_screen/abilitieslogo.png");
        MapTextures.GUI.StatMenu.FormulasLogo = assetManager.loadTexture("Interface/GUI/stat_screen/formulaslogo.png");
        MapTextures.GUI.StatMenu.ItemsLogo = assetManager.loadTexture("Interface/GUI/stat_screen/itemslogo.png");
        MapTextures.GUI.StatMenu.SkillsLogo = assetManager.loadTexture("Interface/GUI/stat_screen/skillslogo.png");
        MapTextures.GUI.StatMenu.ClockBorder = assetManager.loadTexture("Interface/GUI/stat_screen/specialborder.png");
        MapTextures.GUI.StatMenu.SkillsBackdrop = assetManager.loadTexture("Interface/GUI/stat_screen/skillsbackdrop2.png");
        MapTextures.GUI.StatMenu.InventoryBorder = assetManager.loadTexture("Interface/GUI/ui_boxes/invborder3.png");
        MapTextures.GUI.StatMenu.FormulasBorder = assetManager.loadTexture("Interface/GUI/ui_boxes/formulaborder.png");
        MapTextures.GUI.StatMenu.TalentsBorder = assetManager.loadTexture("Interface/GUI/ui_borders/talentborder2.png");
        MapTextures.GUI.StatMenu.PortraitLeafBorder = assetManager.loadTexture("Interface/GUI/ui_borders/leafborder.png");
        MapTextures.GUI.StatMenu.PortraitBackdrop = assetManager.loadTexture("Textures/gui/portraitbg2nd.jpg");
        MapTextures.GUI.StatMenu.WindowBG = assetManager.loadTexture("Textures/gui/unitwindowbg2.jpg");
        MapTextures.GUI.StatMenu.SideTab = assetManager.loadTexture("Interface/GUI/ui_boxes/tab.png");
    }
    
    public static void loadUnitTextures(AssetManager assetManager, List<TangibleUnit> allUnits) { //unit specific things
        //TODO: load this stuff
        
        List<String> loadedJobClassNames = new ArrayList<>(); //JobClasses are references to a shared instance
        Map<String, Texture> loadedPortraitTextures = new HashMap<>();
        for (TangibleUnit unit : allUnits) {
            JobClass jobClass = unit.getJobClass();
            if (!loadedJobClassNames.contains(jobClass.getName())) {
                jobClass.loadAssets(assetManager);
                loadedJobClassNames.add(jobClass.getName());
            }
            
            String portraitTexturePath = unit.getUnitInfo().getPortraitTexturePath();
            if (!loadedPortraitTextures.containsKey(portraitTexturePath)) {
                Texture tex = assetManager.loadTexture(portraitTexturePath);
                unit.getUnitInfo().setPortraitTexture(tex);
                loadedPortraitTextures.put(portraitTexturePath, tex);
            } else {
                unit.getUnitInfo().setPortraitTexture(loadedPortraitTextures.get(portraitTexturePath));
            }
            
            //TODO: load skill and talent textures
        }
    }
    
    public static TextureArray loadTextures(String prefix, String[] names, AssetManager assetManager) {
        List<Image> textures = new ArrayList<>();
        for (String name : names) {
            textures.add(assetManager.loadTexture(prefix + name).getImage());
        }
        
        return new TextureArray(textures);
    }
}
