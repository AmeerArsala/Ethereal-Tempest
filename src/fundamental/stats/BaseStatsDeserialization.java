/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.stats;

import java.util.HashMap;

/**
 *
 * @author night
 */
public class BaseStatsDeserialization {
    private Integer Level;
    private Integer MaxHP;
    private Integer MaxTP;
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
    
    public BaseStatsDeserialization(Integer Level, Integer MaxHP, Integer MaxTP, Integer Strength, Integer Ether, Integer Agility, Integer Dexterity, Integer Comprehension, Integer Defense, Integer Resilience, Integer Mobility, Integer Physique, Integer BaseAdrenaline) {
        this.Level = Level;
        this.MaxHP = MaxHP;
        this.MaxTP = MaxTP;
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
    
    public HashMap<BaseStat, Integer> createBaseStatMap() {
        int statCount = 13;
        HashMap<BaseStat, Integer> statMap = new HashMap<>();
        
        BaseStat[] baseStats = {
            BaseStat.Level,
            BaseStat.MaxHP,
            BaseStat.MaxTP,
            BaseStat.Strength,
            BaseStat.Ether,
            BaseStat.Agility,
            BaseStat.Dexterity,
            BaseStat.Comprehension,
            BaseStat.Defense,
            BaseStat.Resilience,
            BaseStat.Mobility,
            BaseStat.Physique,
            BaseStat.Adrenaline
        };
        
        int[] stats = {
            Level != null ? Level : 0,
            MaxHP != null ? MaxHP : 0,
            MaxTP != null ? MaxTP : 0,
            Strength != null ? Strength : 0,
            Ether != null ? Ether : 0,
            Agility != null ? Agility : 0,
            Dexterity != null ? Dexterity : 0,
            Comprehension != null ? Comprehension : 0,
            Defense != null ? Defense : 0,
            Resilience != null ? Resilience : 0,
            Mobility != null ? Mobility : 0,
            Physique != null ? Physique : 0,
            BaseAdrenaline != null ? BaseAdrenaline : 0
        };
        
        for (int i = 0; i < statCount; i++) {
            statMap.put(baseStats[i], stats[i]);
        }
        
        return statMap;
    }
}
