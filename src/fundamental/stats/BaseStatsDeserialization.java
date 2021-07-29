/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.stats;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author night
 */
public class BaseStatsDeserialization {
    private Integer Level;
    private Integer MaxHP;
    private Integer MaxTP;
    private Integer CurrentHP;
    private Integer CurrentTP;
    private Integer Strength;
    private Integer Ether;
    private Integer Agility;
    private Integer Dexterity;
    private Integer Comprehension;
    private Integer Defense;
    private Integer Resilience;
    private Integer Mobility;
    private Integer Physique;
    private Integer BaseAdrenaline;
    
    public BaseStatsDeserialization(Integer Level, Integer MaxHP, Integer MaxTP, Integer CurrentHP, Integer CurrentTP, Integer Strength, Integer Ether, Integer Agility, Integer Dexterity, Integer Comprehension, Integer Defense, Integer Resilience, Integer Mobility, Integer Physique, Integer BaseAdrenaline) {
        this.Level = Level;
        this.MaxHP = MaxHP;
        this.MaxTP = MaxTP;
        this.CurrentHP = CurrentHP;
        this.CurrentTP = CurrentTP;
        this.Strength = Strength;
        this.Ether = Ether;
        this.Agility = Agility;
        this.Dexterity = Dexterity;
        this.Comprehension = Comprehension;
        this.Defense = Defense;
        this.Resilience = Resilience;
        this.Mobility = Mobility;
        this.Physique = Physique;
        this.BaseAdrenaline = BaseAdrenaline;
    }
    
    /*
        BaseStat[] baseStats = {
            BaseStat.Level,
            BaseStat.MaxHP,
            BaseStat.Strength,
            BaseStat.Ether,
            BaseStat.Agility,
            BaseStat.Comprehension,
            BaseStat.Dexterity,
            BaseStat.Defense,
            BaseStat.Resilience,
            BaseStat.Mobility,
            BaseStat.Physique,
            BaseStat.Adrenaline,
            BaseStat.CurrentHP,
            BaseStat.CurrentTP,
            BaseStat.MaxTP
        };
    
        ^^^ equivalent to BaseStat.values()
    */
    
    //in order of the BaseStat.values() enum array; for each stat, defaultVal if null. For CurrentHP and CurrentTP, they become what MaxHP and MaxTP would be respectively. Level becomes defaultLevel if null
    public int[] baseStatValueLoadoutArray(int defaultLevel, int defaultVal) {
        int maxHP = MaxHP != null ? MaxHP : defaultVal;
        int maxTP = MaxTP != null ? MaxTP : defaultVal;
        
        return new int[] {
            Level != null ? Level : defaultLevel,
            maxHP,
            Strength != null ? Strength : defaultVal,
            Ether != null ? Ether : defaultVal,
            Agility != null ? Agility : defaultVal,
            Comprehension != null ? Comprehension : defaultVal,
            Dexterity != null ? Dexterity : defaultVal,
            Defense != null ? Defense : defaultVal,
            Resilience != null ? Resilience : defaultVal,
            Mobility != null ? Mobility : defaultVal,
            Physique != null ? Physique : defaultVal,
            BaseAdrenaline != null ? BaseAdrenaline : defaultVal,
            CurrentHP != null ? CurrentHP : maxHP,
            CurrentTP != null ? CurrentTP : maxTP,
            maxTP
        };
    }
    
    //in order of the BaseStat.values() enum array; for each stat, 0 if null, even for CurrentHP and CurrentTP
    public int[] baseStatValueArray(int defaultLevel, int defaultVal) {
        return new int[] {
            Level != null ? Level : defaultLevel,
            MaxHP != null ? MaxHP : defaultVal,
            Strength != null ? Strength : defaultVal,
            Ether != null ? Ether : defaultVal,
            Agility != null ? Agility : defaultVal,
            Comprehension != null ? Comprehension : defaultVal,
            Dexterity != null ? Dexterity : defaultVal,
            Defense != null ? Defense : defaultVal,
            Resilience != null ? Resilience : defaultVal,
            Mobility != null ? Mobility : defaultVal,
            Physique != null ? Physique : defaultVal,
            BaseAdrenaline != null ? BaseAdrenaline : defaultVal,
            CurrentHP != null ? CurrentHP : defaultVal,
            CurrentTP != null ? CurrentTP : defaultVal,
            MaxTP != null ? MaxTP : defaultVal
        };
    }
    
    public HashMap<BaseStat, Integer> createBaseStatsLoadoutMap() {
        return createBaseStatMap(baseStatValueLoadoutArray(1, 0));
    }
    
    public HashMap<BaseStat, Integer> createBaseStatsLoadoutMap(int defaultLevelVal, int defaultNonLevelStatVal) {
        return createBaseStatMap(baseStatValueLoadoutArray(defaultLevelVal, defaultNonLevelStatVal));
    }
    
    public HashMap<BaseStat, Integer> createBaseStatsLoadoutMap(int defaultStatVal) {
        return createBaseStatMap(baseStatValueLoadoutArray(defaultStatVal, defaultStatVal));
    }
    
    public HashMap<BaseStat, Integer> createBaseStatMap(int defaultLevelVal, int defaultNonLevelStatVal) {
        return createBaseStatMap(baseStatValueArray(defaultLevelVal, defaultNonLevelStatVal));
    }
    
    public HashMap<BaseStat, Integer> createBaseStatMap(int defaultStatVal) {
        return createBaseStatMap(baseStatValueArray(defaultStatVal, defaultStatVal));
    }
    
    private HashMap<BaseStat, Integer> createBaseStatMap(int[] statValues) {
        HashMap<BaseStat, Integer> statMap = new HashMap<>();
        
        BaseStat[] baseStats = BaseStat.values();
        for (int i = 0; i < baseStats.length; i++) {
            statMap.put(baseStats[i], statValues[i]);
        }
        
        return statMap;
    }
}
