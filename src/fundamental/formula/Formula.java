/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.formula;

import battle.animation.config.EntityAnimation.AnimationSource;
import com.google.gson.Gson;
import fundamental.stats.alteration.Toll;
import fundamental.stats.alteration.Toll.Exchange;
import etherealtempest.fsm.MasterFsmState;
import fundamental.BattleVisual;
import fundamental.unit.aspect.UnitAllegiance;
import fundamental.tool.DamageTool;
import fundamental.Gear;
import fundamental.tool.SupportTool;
import fundamental.tool.Tool;
import fundamental.tool.Tool.ToolType;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import maps.layout.Coords;
import maps.layout.MapLevel;
import maps.layout.MapCoords;
import maps.layout.occupant.character.TangibleUnit;
import maps.layout.occupant.VenturePeek;

/**
 *
 * @author night
 * @param <T> DamageTool or SupportTool
 */
public class Formula<T extends Tool> extends Gear {
    private ToolType formulaType;
    private Toll cost;
    private T formulaData;
    
    public Formula(Gear template, T data, ToolType FT, Toll usage, String animationJson) {
        super(template.getName(), template.getDescription(), template.getPassiveBonusEffect());
        formulaData = data;
        cost = usage;
        formulaType = FT;
        formulaData.setAnimation(new BattleVisual(animationJson, AnimationSource.ParticleEffect));
    }
    
    public ToolType getFormulaPurpose() { return formulaType; }
    
    public DamageTool getOffensiveFormulaData() { return formulaType == ToolType.Attack ? ((DamageTool)formulaData) : null; }
    public SupportTool getSupportiveFormulaData() { return formulaType.isSupportive() ? ((SupportTool)formulaData) : null; }
    public T getActualFormulaData() { return formulaData; }
    
    public int getHPUsage() { return cost.getType() == Exchange.HP ? cost.getValue() : 0; }
    public int getTPUsage() { return cost.getType() == Exchange.TP ? cost.getValue() : 0; }
    
    public String getIconPath() {
        return "Interface/GUI/icons/item_and_formula/" + formulaData.getType() + ".png";
    }
    
    public boolean isAvailableAt(MapCoords pos, UnitAllegiance allegiance, int currentHP, int currentTP) {
        if (currentHP < getHPUsage() || currentTP < getTPUsage()) { return false; } 
        
        MapLevel currentMap = MasterFsmState.getCurrentMap();
        for (Integer range : formulaData.getRange()) {
            for (MapCoords point : VenturePeek.coordsForTilesOfRange(range, pos)) {
                TangibleUnit occupier = currentMap.getTileAt(point).getOccupier();
                if (occupier != null && formulaType.isSupportive() == allegiance.alliedWith(occupier.getAllegiance())) {
                    return true;
                }
            }
        }
        
        return false;
    }
        
    @Override
    public String getDescription() {
        return getStatDescription() + desc;
    }
    
    public String getStatDescription() {
        String st = formulaData.toString();
        if (cost.getType() == Exchange.TP) {
            st += "TP Used: " + cost.getValue() + '\n';
        } else if (cost.getType() == Exchange.HP) {
            st += "HP Used: " + cost.getValue() + '\n';
        }
        
        return st;
    }
    
    private static FormulaDeserialization deserialization(String jsonName) {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\GameInfo\\EntityPresets\\formulas\\" + jsonName));
            return gson.fromJson(reader, FormulaDeserialization.class);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static final Formula Anemo_Schism() {
        return deserialization("Anemo Schism.json").constructOffensiveFormula(null); //no bonus for having this formula, that's why null is there
        //formula.animation.initializeParticleEffects(assetManager); do not call this here because it already gets caleld in VisibleEntityParticleEffectAnimation
    }
}