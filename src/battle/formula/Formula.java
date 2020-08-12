/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.formula;

import battle.item.Weapon;
import battle.talent.Talent;

import com.destroflyer.jme3.effekseer.model.ParticleEffect;
import com.destroflyer.jme3.effekseer.model.ParticleEffectSettings;
import com.destroflyer.jme3.effekseer.reader.EffekseerReader;
import com.destroflyer.jme3.effekseer.renderer.EffekseerControl;
import com.google.gson.Gson;
import com.jme3.asset.AssetManager;
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
public class Formula extends Weapon {
    private FormulaType formulaType;
    private String desc = "";
    private int hpUsage, tpUsage;
    private Talent extraEffect = null;
    
    private final int worth = 0;
    
    private FormulaAnimation jsonInfo;
    private EffekseerControl control;
    private ParticleEffectBeta testEffect;
    private ParticleEffect particleEffect;
    
    private AssetManager assetM;
        
    public enum FormulaType {
        Attack,
        Support
    }
    
    //                                                  type will always be a type of ether
    public Formula(String name, String description, FormulaType FT, String type, String attr, int mt, int hit, int crt, boolean[] rng, String[] eff, int[] bonus, int requiredLevel, int hpUsage, int tpUsage) {
        super(name, description, type, attr, mt, hit, 0, crt, rng, 300.0, eff, bonus, requiredLevel, "None", 0);
        desc = description;
        formulaType = FT;
        
        this.hpUsage = hpUsage;
        this.tpUsage = tpUsage;
        
        jsonInfo = deserializeFromJSON();
    }
        
    public Formula(String name, String description, FormulaType FT, String type, String attr, int mt, int hit, int crt, boolean[] rng, String[] eff, int[] bonus, int requiredLevel, int hpUsage, int tpUsage, Talent extraEffect) {
        super(name, description, type, attr, mt, hit, 0, crt, rng, 300.0, eff, bonus, requiredLevel, "None", 0);
        desc = description;
        formulaType = FT;
        
        this.hpUsage = hpUsage;
        this.tpUsage = tpUsage;
        this.extraEffect = extraEffect;
        
        jsonInfo = deserializeFromJSON();
    }
        
    public Formula() {
        super(false);
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
        
    public int getHPUsage() { return hpUsage; }
    public int getTPUsage() { return tpUsage; }
        
    public FormulaType getFormulaPurpose() {
        return formulaType;
    }
    
    public Talent getExtraEffect() {
        return extraEffect;
    }
    
    public FormulaAnimation getInfo() {
        return jsonInfo;
    }
        
    @Override
    public String getDescription() {
        return desc;
    }
        
    @Override
    public String getStatDescription() {
        String st = 
              "Pow: " + Pow + '\n'
            + "Acc: " + Acc +  '\n'
            + "Crit: " + CRIT + '\n';
        if (tpUsage > 0) {
            st += "TP Used: " + tpUsage;
        } else if (hpUsage > 0) {
            st += "HP Used: " + hpUsage;
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