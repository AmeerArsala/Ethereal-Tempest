/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.formula;

import battle.Toll;
import battle.Toll.Exchange;

import com.destroflyer.jme3.effekseer.model.ParticleEffect;
import com.destroflyer.jme3.effekseer.model.ParticleEffectSettings;
import com.destroflyer.jme3.effekseer.reader.EffekseerReader;
import com.destroflyer.jme3.effekseer.renderer.EffekseerControl;
import com.google.gson.Gson;
import com.jme3.asset.AssetManager;
import fundamental.DamageTool;
import fundamental.FreelyAssociated;
import general.visual.ParticleEffectBeta;
import general.visual.ParticleEffectInfoBeta;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author night
 */
public class Formula extends FreelyAssociated {
    private FormulaType formulaType;
    private Toll cost;
    private DamageTool formulaData;
    
    private FormulaAnimation jsonInfo;
    private EffekseerControl control;
    private ParticleEffectBeta testEffect;
    private ParticleEffect particleEffect;
    
    private AssetManager assetM;
        
    public enum FormulaType {
        Attack,
        Support
    }
    
    public Formula(FreelyAssociated template, DamageTool data, FormulaType FT, Toll usage) {
        super(template.getName(), template.getDescription(), template.getExtraTalent(), template.getExtraSkill());
        formulaData = data;
        cost = usage;
        formulaType = FT;
        
        jsonInfo = deserializeFromJSON();
    }
        
    public Formula() {
        super(false);
    }
    
    public Formula(boolean ex) {
        super(ex);
    }
    
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
    
    public DamageTool getFormulaData() { return formulaData; }
        
    public int getHPUsage() { return cost.getType() == Exchange.HP ? cost.getValue() : 0; }
    public int getTPUsage() { return cost.getType() == Exchange.TP ? cost.getValue() : 0; }
        
    public FormulaType getFormulaPurpose() {
        return formulaType;
    }
    
    public FormulaAnimation getInfo() {
        return jsonInfo;
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