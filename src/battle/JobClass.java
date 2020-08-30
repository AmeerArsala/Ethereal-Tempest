/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle;

import battle.Combatant.BaseStat;
import battle.Combatant.BattleStat;
import battle.parse.AttackConfig;
import com.google.gson.Gson;
import com.jme3.asset.AssetManager;
import com.jme3.texture.Texture;
import fundamental.StatBundle;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import misc.CustomAnimationSegment;

/**
 *
 * @author Ameer Arsala
 */
public class JobClass {
    protected String jobname = "";
    protected AttackConfig attackAnimation;

    private final int tier;
    protected final int Resolve;
    
    private final HashMap<BaseStat, Integer> bonusStats;
    private final HashMap<BaseStat, Integer> maxStats;
    private final HashMap<BattleStat, Integer> battleBonus; //acc, avo, crit, crit avo
    
    private final List<String> wieldableWeaponTypes;
    private final List<String> mobilityTypes;
    
    protected HashMap<String, CustomAnimationSegment> customSkillAnimations = new HashMap<>();
    
    private Texture combatSheet;
    
    public JobClass(String jobname, List<String> mobilityTypes, List<String> wieldableWeaponTypes, List<StatBundle> bonusStats, List<StatBundle> battleBonus, List<StatBundle> maxStats, int tier) {
        this.jobname = jobname;
        this.mobilityTypes = mobilityTypes;
        this.wieldableWeaponTypes = wieldableWeaponTypes;
        this.tier = tier;
        
        this.bonusStats = StatBundle.createBaseStatsFromBundles(bonusStats);
        this.battleBonus = StatBundle.createBattleStatsFromBundles(battleBonus);
        this.maxStats = StatBundle.createBaseStatsFromBundles(maxStats);
        
        if (MovementType().contains("armored")) {
            Resolve = 15;
        } else if (MovementType().contains("monster")) {
            Resolve = 14;
        } else if (MovementType().contains("infantry") || MovementType().contains("infantry")) {
            Resolve = 13;
        } else if (MovementType().contains("mechanism")) {
            Resolve = 12;
        } else { //cavalry
            Resolve = 11;
        }
    }
    
    public JobClass(String jobname, List<String> mobilityTypes, List<String> wieldableWeaponTypes, HashMap<BaseStat, Integer> bonusStats, HashMap<BattleStat, Integer> battleBonus, HashMap<BaseStat, Integer> maxStats, int tier) {
        this.jobname = jobname;
        this.mobilityTypes = mobilityTypes;
        this.wieldableWeaponTypes = wieldableWeaponTypes;
        this.tier = tier;
        
        this.bonusStats = bonusStats;
        this.battleBonus = battleBonus;
        this.maxStats = maxStats;
        
        if (MovementType().contains("armored")) {
            Resolve = 15;
        } else if (MovementType().contains("monster")) {
            Resolve = 14;
        } else if (MovementType().contains("infantry") || MovementType().contains("infantry")) {
            Resolve = 13;
        } else if (MovementType().contains("mechanism")) {
            Resolve = 12;
        } else { //cavalry
            Resolve = 11;
        }
    }
    
    public String clName() { return jobname; }
    public int clTier() { return tier; }

    public List<String> UsableWeapons() { return wieldableWeaponTypes; } // {"sword", "axe", "polearm", "knife", "bow", "whip", "monster", "pi ether", "gamma ether", "delta ether", "omega ether"}
    public List<String> MovementType() { return mobilityTypes; } // infantry, armored, cavalry, flier, mechanism, morph, monster
    public final HashMap<BaseStat, Integer> ClassStatBonus() { return bonusStats; }
    public final HashMap<BaseStat, Integer> ClassMaxStats() { return maxStats; }
    public final HashMap<BattleStat, Integer> ClassBattleBonus() { return battleBonus; } // Acc, Avo, Crit, CritAvo, AS, ATK, En, EtherDef
    
    private AttackConfig deserializeFromJSON() {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\Models\\Sprites\\battle\\" + jobname + "\\config.json"));
            return gson.fromJson(reader, AttackConfig.class);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public void initializeFrames(AssetManager AM) {
        attackAnimation = deserializeFromJSON();
        combatSheet = AM.loadTexture(attackAnimation.getSpritesheet());
        System.out.println(attackAnimation.toString());
    }
    
    public AttackConfig getBattleConfig() { return attackAnimation; }
    public Texture getCombatSheet() { return combatSheet; }
    
    public JobClass addCustomSkillAnimation(String skillName, CustomAnimationSegment values) {
        customSkillAnimations.put(skillName, values);
        return this;
    }
    
    public HashMap<String, CustomAnimationSegment> getCustomSkillAnimations() {
        return customSkillAnimations;
    }
    
    protected void setCustomSkillAnimations(HashMap<String, CustomAnimationSegment> anims) {
        customSkillAnimations = anims;
    }
    
    public int getResolve() { //hidden stat
        return Resolve;
    }
    
    @Override
    public String toString() { return jobname; }
}