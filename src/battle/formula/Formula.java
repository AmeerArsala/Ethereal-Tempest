/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.formula;

import fundamental.Toll;
import fundamental.Toll.Exchange;

import com.destroflyer.jme3.effekseer.model.ParticleEffect;
import com.destroflyer.jme3.effekseer.model.ParticleEffectSettings;
import com.destroflyer.jme3.effekseer.reader.EffekseerReader;
import com.destroflyer.jme3.effekseer.renderer.EffekseerControl;
import com.google.gson.Gson;
import com.jme3.asset.AssetManager;
import etherealtempest.MasterFsmState;
import fundamental.DamageTool;
import fundamental.FreelyAssociated;
import fundamental.SupportTool;
import fundamental.Tool;
import fundamental.Tool.ToolType;
import general.visual.ParticleEffectBeta;
import general.visual.ParticleEffectInfoBeta;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import maps.layout.Coords;
import maps.layout.TangibleUnit;
import maps.layout.TangibleUnit.UnitStatus;
import maps.layout.Tile;
import maps.layout.VenturePeek;

/**
 *
 * @author night
 */
public class Formula extends FreelyAssociated {
    private ToolType formulaType;
    private Toll cost;
    private Tool formulaData;
    
    private FormulaAnimation jsonInfo;
    private EffekseerControl control;
    private ParticleEffectBeta testEffect;
    private ParticleEffect particleEffect;
    
    private AssetManager assetM;
    
    private static int IDgen = 0;
    private final int ID;
    
    public Formula(FreelyAssociated template, Tool data, ToolType FT, Toll usage) {
        super(template.getName(), template.getDescription(), template.getExtraTalent(), template.getExtraSkill());
        formulaData = data;
        cost = usage;
        formulaType = FT;
        
        jsonInfo = deserializeFromJSON();
        
        ID = IDgen;
        IDgen++;
    }
    
    private Formula(FreelyAssociated template, Tool data, ToolType FT, Toll usage, int id) {
        super(template.getName(), template.getDescription(), template.getExtraTalent(), template.getExtraSkill());
        formulaData = data;
        cost = usage;
        formulaType = FT;
        
        jsonInfo = deserializeFromJSON();
        
        ID = id;
    }
        
    public Formula() {
        super(false);
        
        ID = IDgen;
        IDgen++;
    }
    
    public Formula(boolean ex) {
        super(ex);
        
        ID = IDgen;
        IDgen++;
    }
    
    public Formula cloneFormulaInstance() {
        return new Formula(this, formulaData, formulaType, cost, ID);
    }
    
    public int getID() { return ID; }
    
    private FormulaAnimation deserializeFromJSON() {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\Models\\Effects\\Formulas\\" + name + ".json"));
            return gson.fromJson(reader, FormulaAnimation.class);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public DamageTool getOffensiveFormulaData() { return formulaType == ToolType.Attack ? ((DamageTool)formulaData) : null; }
    public SupportTool getSupportiveFormulaData() { return formulaType.isSupportive() ? ((SupportTool)formulaData) : null; }
    public Tool getActualFormulaData() { return formulaData; }
    
    public int getHPUsage() { return cost.getType() == Exchange.HP ? cost.getValue() : 0; }
    public int getTPUsage() { return cost.getType() == Exchange.TP ? cost.getValue() : 0; }
        
    public ToolType getFormulaPurpose() {
        return formulaType;
    }
    
    public FormulaAnimation getInfo() {
        return jsonInfo;
    }
    
    public boolean isAvailableAt(Coords pos, int layer, UnitStatus allegiance, int currentHP, int currentTP) {
        if (currentHP < getHPUsage() || currentTP < getTPUsage()) { return false; } 
        
        Tile[][] layerTiles = MasterFsmState.getCurrentMap().fullmap[layer];
        for (Integer range : formulaData.getRange()) {
            for (Coords point : VenturePeek.coordsForTilesOfRange(range, pos, layer)) {
                TangibleUnit occupier = layerTiles[point.getX()][point.getY()].getOccupier();
                if (occupier != null && ((formulaType.isSupportive() && allegiance.alliedWith(occupier.unitStatus)) || (!formulaType.isSupportive() && !allegiance.alliedWith(occupier.unitStatus)))) {
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
    
    public EffekseerControl getControl() {
        return control;
    }
    
    public ParticleEffectBeta getParticleEffect() {
        return testEffect;
    }
        
    public void initializeAnimation(AssetManager assetManager) {
        assetM = assetManager;
        EffekseerReader reader = new EffekseerReader();
        
        particleEffect = reader.read("assets", "assets\\Models\\Effects\\MAGICALxSPIRAL\\" + name + ".efkproj");
        
        ParticleEffectInfoBeta info = new ParticleEffectInfoBeta().quality(1);
        testEffect = new ParticleEffectBeta()
                .directory("assets\\Models\\Effects\\MAGICALxSPIRAL")
                .fileName("anemo schism.efkproj")
                .particleEffect(particleEffect)
                .info(info);
        
        ParticleEffectSettings PES = new ParticleEffectSettings();
        control = new EffekseerControl(particleEffect, PES, assetManager);
        control.setEnabled(true);
    }
    
    public EffekseerControl resetControl() {
        control = new EffekseerControl(particleEffect, new ParticleEffectSettings(), assetM);
        control.setEnabled(true);
        
        return control;
    }
}