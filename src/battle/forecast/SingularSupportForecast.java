/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.forecast;

import battle.Combatant;
import fundamental.stats.Bonus;
import fundamental.stats.Toll;
import java.util.List;

/**
 *
 * @author night
 */
public class SingularSupportForecast extends IndividualForecast {
    public int hpRecovered = 0, tpRecovered = 0, durabilityRecovered = 0;
    
    private List<Bonus> gainedBonuses;
    
    public SingularSupportForecast(Combatant C) {
        super(C);
    }
    
    public SingularSupportForecast(Combatant C, List<Bonus> bonuses) {
        super(C);
        gainedBonuses = bonuses;
    }
    
    public List<Bonus> getBonuses() { return gainedBonuses; }
    
    public void addBonus(Bonus B) { gainedBonuses.add(B); }
    
    public void removeBonus(int i) { gainedBonuses.remove(i); }
    public void removeBonus(Bonus B) { gainedBonuses.remove(B); }
    
    public void addRecovery(Toll recovery) {
        switch (recovery.getType()) {
            case HP:
                hpRecovered += recovery.getValue();
                combatant.setHPtoSubtract(-1 * hpRecovered);
                break;
            case TP:
                tpRecovered += recovery.getValue();
                combatant.setTPtoSubtract(-1 * tpRecovered);
                break;
            case Durability:
                durabilityRecovered += recovery.getValue();
                break;
            default:
                break;
        }
    }
    
}
