/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.stats;

import fundamental.stats.Bonus.BonusType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 *
 * @author night
 */
public class BonusHolder {
    private final List<Bonus> bonuses = new ArrayList<>();
    
    public BonusHolder() {}
    
    public List<Bonus> getList() { return bonuses; }
    
    public void addBonus(Bonus bonus) { //apply this ON an Occasion
        bonuses.add(bonus);
        Bonus.organizeList(bonuses);
    }
    
    public void update(int currentTurn) {
        removeBonusesIf((bonus) -> {
            return bonus.getTurnApplied() != currentTurn;
        });
    }
    
    public void updateOnActionCommitted() {
        removeBonusesIf((bonus) -> {
            return bonus.getType() == BonusType.ThroughNextAction;
        });
    }
    
    public void removeBonusesIf(Predicate<Bonus> condition) {
        List<Bonus> toRemove = new ArrayList<>();
        for (Bonus bonus : bonuses) {
            if (condition.test(bonus)) {
                toRemove.add(bonus);
            }
        }
        
        bonuses.removeAll(toRemove);
    }
    
    public int getTotal(BaseStat stat) {
        return getTotal(stat, null, true);
    }
    
    public int getTotal(BattleStat stat) {
        return getTotal(stat, null, true);
    }
    
    public int getTotal(BaseStat stat, BonusType filterBy, boolean include) {
        int bonusSum = 0;
        for (Bonus bonus : bonuses) {
            if (bonus.getBaseStat() == stat && (filterBy == null || ((filterBy == bonus.getType()) == include))) {
                bonusSum += bonus.getValue();
            }
        }
        
        return bonusSum;
    }
    
    public int getTotal(BattleStat stat, BonusType filterBy, boolean include) {
        int bonusSum = 0;
        for (Bonus bonus : bonuses) {
            if (bonus.getBattleStat() == stat && (filterBy == null || ((filterBy == bonus.getType()) == include))) {
                bonusSum += bonus.getValue();
            }
        }
        
        return bonusSum;
    }
}
