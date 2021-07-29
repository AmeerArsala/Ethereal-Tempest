/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.unit.aspect;

import fundamental.stats.BaseStat;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author night
 */
public class GrowthRates {
    private final HashMap<BaseStat, Integer> personalGrowthRates;
    private final HashMap<BaseStat, Integer> jobClassGrowthRates;
    
    private final HashMap<BaseStat, Integer> baseGrowthRates = new HashMap<>();
    private final HashMap<BaseStat, Integer> pityDeltaGrowthRates = BaseStat.canvas(0);
    
    public GrowthRates(HashMap<BaseStat, Integer> personalGrowthRates, HashMap<BaseStat, Integer> jobClassGrowthRates) {
        this.personalGrowthRates = personalGrowthRates;
        this.jobClassGrowthRates = jobClassGrowthRates;
        
        BaseStat[] stats = BaseStat.values();
        for (BaseStat stat : stats) {
            baseGrowthRates.put(stat, personalGrowthRates.get(stat) + jobClassGrowthRates.get(stat));
        }
    }

    public HashMap<BaseStat, Integer> getBaseGrowthRates() {
        return baseGrowthRates;
    }

    public HashMap<BaseStat, Integer> getPityDeltaGrowthRates() {
        return pityDeltaGrowthRates;
    }

    public HashMap<BaseStat, Integer> getPersonalGrowthRates() {
        return personalGrowthRates;
    }

    public HashMap<BaseStat, Integer> getJobClassGrowthRates() {
        return jobClassGrowthRates;
    }
    
    public int getGrowthRate(BaseStat stat) {
        return baseGrowthRates.get(stat) + pityDeltaGrowthRates.get(stat);
    }
    
    public void addToPityGrowthRate(BaseStat stat, int delta) {
        pityDeltaGrowthRates.replace(stat, pityDeltaGrowthRates.get(stat) + delta);
    }
    
    public void addToAllPityGrowthRates(int delta, List<BaseStat> except) {
        BaseStat[] stats = BaseStat.values();
        for (BaseStat stat : stats) {
            if (!except.contains(stat)) {
                addToPityGrowthRate(stat, delta);
            }
        }
    }
    
    public void flushPityDeltaGrowthRate(BaseStat stat) {
        pityDeltaGrowthRates.replace(stat, 0);
    }
}
