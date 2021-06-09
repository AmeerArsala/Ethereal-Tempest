/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.jobclass;

import fundamental.jobclass.JobClass.MobilityType;
import fundamental.item.weapon.WeaponType;
import fundamental.jobclass.animation.ActionDecider;
import fundamental.stats.BaseStat;
import fundamental.stats.BattleStat;
import fundamental.stats.StatBundle;
import fundamental.stats.StatBundleDeserialization;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author night
 */
public class JobClassDeserialization {
    private String name;
    private String desc;
    private int tier;
    private StatBundleDeserialization[] bonusBaseStats;
    private StatBundleDeserialization[] bonusBattleStats;
    private StatBundleDeserialization[] maxStats;
    private WeaponType[] wieldableWeaponTypes;
    private MobilityType[] mobilityTypes;
    private ClassBattleAnimationSetDeserialization[] battleAnimationConfigurations;
    
    public JobClassDeserialization
    (
        String name, String desc, int tier,
        StatBundleDeserialization[] bonusBaseStats, StatBundleDeserialization[] bonusBattleStats, StatBundleDeserialization[] maxStats,
        WeaponType[] wieldableWeaponTypes, MobilityType[] mobilityTypes,
        ClassBattleAnimationSetDeserialization[] battleAnimationConfigurations
    ) 
    {
        this.name = name;
        this.desc = desc;
        this.tier = tier;
        this.bonusBaseStats = bonusBaseStats;
        this.bonusBattleStats = bonusBattleStats;
        this.maxStats = maxStats;
        this.wieldableWeaponTypes = wieldableWeaponTypes;
        this.mobilityTypes = mobilityTypes;
        this.battleAnimationConfigurations = battleAnimationConfigurations;
    }
    
    private class ClassBattleAnimationSetDeserialization {
        private WeaponType weaponType;
        private ActionDecider animationConfigurations;
        
        public ClassBattleAnimationSetDeserialization(WeaponType weaponType, ActionDecider animationConfigurations) {
            this.weaponType = weaponType;
            this.animationConfigurations = animationConfigurations;
        }
        
        public WeaponType getWeaponType() { return weaponType; }
        public ActionDecider getAnimationConfig() { return animationConfigurations; }
    }
    
    public JobClass constructJobClass() {
        List<StatBundle<BaseStat>> baseStatBonuses = StatBundleDeserialization.constructBaseStatBundleGroup(bonusBaseStats);
        List<StatBundle<BattleStat>> battleStatBonuses = StatBundleDeserialization.constructBattleStatBundleGroup(bonusBattleStats);
        List<StatBundle<BaseStat>> maxBaseStats = StatBundleDeserialization.constructBaseStatBundleGroup(maxStats);
        
        List<WeaponType> usableWeaponTypes = new ArrayList<>();
        usableWeaponTypes.addAll(Arrays.asList(wieldableWeaponTypes));
        
        List<MobilityType> movementTypes = new ArrayList<>();
        movementTypes.addAll(Arrays.asList(mobilityTypes));
        
        HashMap<WeaponType, ActionDecider> battleAnimationConfigs = new HashMap<>();
        for (ClassBattleAnimationSetDeserialization CBASD : battleAnimationConfigurations) {
            CBASD.getAnimationConfig().deserializeAll();
            battleAnimationConfigs.put(CBASD.getWeaponType(), CBASD.getAnimationConfig());
        }
        
        return new JobClass(name, desc, tier, movementTypes, usableWeaponTypes, baseStatBonuses, battleStatBonuses, maxBaseStats, battleAnimationConfigs);
    }
}
