/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.jobclass;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import fundamental.Entity;
import fundamental.item.weapon.WeaponType;
import fundamental.jobclass.animation.ActionDecider;
import fundamental.stats.BaseStat;
import fundamental.stats.BattleStat;
import fundamental.stats.StatBundle;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Ameer Arsala
 */
public class JobClass extends Entity {
    public enum MobilityType {
        @SerializedName("Armored") Armored(15),
        @SerializedName("Monster") Monster(14),
        @SerializedName("Mechanism") Mechanism(14),
        @SerializedName("Infantry") Infantry(13),
        @SerializedName("Morph") Morph(13),
        @SerializedName("Cavalry") Cavalry(11),
        @SerializedName("Flier") Flier(-1);
        
        private final int baseResolve;
        private MobilityType(int resolve) {
            baseResolve = resolve;
        }
        
        public int getBaseResolve() { return baseResolve; }
    }
    
    private final String desc; 

    private final int tier;
    private final int Resolve;
    
    private final HashMap<BaseStat, Integer> bonusStats;
    private final HashMap<BaseStat, Integer> maxStats;
    private final HashMap<BattleStat, Integer> battleBonus; //acc, avo, crit, crit avo
    
    private final List<WeaponType> wieldableWeaponTypes;
    private final List<MobilityType> mobilityTypes;
    
    private final HashMap<WeaponType, ActionDecider> battleAnimationConfigurations;
    
    public JobClass(String name, String desc, int tier, List<MobilityType> mobilityTypes, List<WeaponType> wieldableWeaponTypes, List<StatBundle<BaseStat>> bonusStats, List<StatBundle<BattleStat>> battleBonus, List<StatBundle<BaseStat>> maxStats, HashMap<WeaponType, ActionDecider> battleAnimationConfigurations) {
        super(name);
        this.desc = desc;
        this.tier = tier;
        this.mobilityTypes = mobilityTypes;
        this.wieldableWeaponTypes = wieldableWeaponTypes;
        this.bonusStats = StatBundle.createBaseStatsFromBundles(bonusStats);
        this.battleBonus = StatBundle.createBattleStatsFromBundles(battleBonus);
        this.maxStats = StatBundle.createBaseStatsFromBundles(maxStats);
        this.battleAnimationConfigurations = battleAnimationConfigurations;
        
        Resolve = calculateResolve();
    }
    
    public JobClass(String name, String desc, int tier, List<MobilityType> mobilityTypes, List<WeaponType> wieldableWeaponTypes, HashMap<BaseStat, Integer> bonusStats, HashMap<BattleStat, Integer> battleBonus, HashMap<BaseStat, Integer> maxStats, HashMap<WeaponType, ActionDecider> battleAnimationConfigurations) {
        super(name);
        this.desc = desc;
        this.tier = tier;
        this.mobilityTypes = mobilityTypes;
        this.wieldableWeaponTypes = wieldableWeaponTypes;
        this.bonusStats = bonusStats;
        this.battleBonus = battleBonus;
        this.maxStats = maxStats;
        this.battleAnimationConfigurations = battleAnimationConfigurations;
        
        Resolve = calculateResolve();
    }
    
    private int calculateResolve() {
        int basedResolve = -1;
        MobilityType[] types = MobilityType.values();
        for (int i = 0; i < types.length; ++i) {
            if (mobilityTypes.contains(types[i])) {
                basedResolve = types[i].getBaseResolve();
            }
        }
        
        return basedResolve;
    }
    
    public String getDescription() { return desc; }
    public int getTier() { return tier; }
    
    public HashMap<BaseStat, Integer> getBaseStatBonuses() { return bonusStats; }
    public HashMap<BattleStat, Integer> getBattleStatBonuses() { return battleBonus; } // Acc, Avo, Crit, CritAvo, AS, ATK, En, EtherDef
    public HashMap<BaseStat, Integer> getMaxStats() { return maxStats; }
    
    public List<WeaponType> getUsableWeapons() { return wieldableWeaponTypes; }
    public List<MobilityType> getMobilityTypes() { return mobilityTypes; }
    public HashMap<WeaponType, ActionDecider> getBattleAnimationConfigurations() { return battleAnimationConfigurations; }
    
    public int getResolve() { //hidden stat
        return Resolve;
    }
    
    @Override
    public String toString() { return name; }
    
    static JobClassDeserialization deserialize(String jsonName) {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\GameInfo\\EntityPresets\\classes\\" + jsonName));
            return gson.fromJson(reader, JobClassDeserialization.class);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    

    public static final JobClass Freeblade = JobClass.deserialize("freeblade.json").constructJobClass();
    //public static final JobClass Marauder = JobClass.deserialize("marauder.json").constructJobClass();
    //public static final JobClass Cowboy = JobClass.deserialize("cowboy.json").constructJobClass();
    //public static final JobClass Knight = JobClass.deserialize("knight.json").constructJobClass();
}