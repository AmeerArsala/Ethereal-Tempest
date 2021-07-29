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
import fundamental.stats.BaseStatsDeserialization;
import fundamental.stats.BattleStat;
import fundamental.stats.BattleStatsDeserialization;
import fundamental.unit.Unit;
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
    private BaseStatsDeserialization bonusBaseStats;
    private BattleStatsDeserialization bonusBattleStats;
    private BaseStatsDeserialization bonusGrowthRates;
    private BaseStatsDeserialization maxStats;
    private WeaponType[] wieldableWeaponTypes;
    private MobilityType[] mobilityTypes;
    private ClassBattleAnimationSetDeserialization[] battleAnimationConfigurations;
    
    public JobClassDeserialization
    (
        String name, String desc, int tier,
        BaseStatsDeserialization bonusBaseStats,
        BattleStatsDeserialization bonusBattleStats,
        BaseStatsDeserialization bonusGrowthRates,
        BaseStatsDeserialization maxStats,
        WeaponType[] wieldableWeaponTypes, MobilityType[] mobilityTypes,
        ClassBattleAnimationSetDeserialization[] battleAnimationConfigurations
    ) 
    {
        this.name = name;
        this.desc = desc;
        this.tier = tier;
        this.bonusBaseStats = bonusBaseStats;
        this.bonusBattleStats = bonusBattleStats;
        this.bonusGrowthRates = bonusGrowthRates;
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
        HashMap<BaseStat, Integer> baseStatBonuses = bonusBaseStats.createBaseStatMap(0);
        HashMap<BattleStat, Integer> battleStatBonuses = bonusBattleStats.createBattleStatMap(0);
        HashMap<BaseStat, Integer> growthRateBonuses = bonusGrowthRates.createBaseStatMap(100, 0);
        HashMap<BaseStat, Integer> maxBaseStats = maxStats.createBaseStatsLoadoutMap(Unit.MAX_LEVEL, 0);
        
        List<WeaponType> usableWeaponTypes = new ArrayList<>();
        usableWeaponTypes.addAll(Arrays.asList(wieldableWeaponTypes));
        
        List<MobilityType> movementTypes = new ArrayList<>();
        movementTypes.addAll(Arrays.asList(mobilityTypes));
        
        HashMap<WeaponType, ActionDecider> battleAnimationConfigs = new HashMap<>();
        for (ClassBattleAnimationSetDeserialization CBASD : battleAnimationConfigurations) {
            CBASD.getAnimationConfig().deserializeAll();
            battleAnimationConfigs.put(CBASD.getWeaponType(), CBASD.getAnimationConfig());
        }
        
        return new JobClass(name, desc, tier, movementTypes, usableWeaponTypes, baseStatBonuses, battleStatBonuses, growthRateBonuses, maxBaseStats, battleAnimationConfigs);
    }
}
