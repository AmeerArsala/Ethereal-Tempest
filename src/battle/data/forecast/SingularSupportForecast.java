/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.data.forecast;

import battle.data.participant.Combatant;
import fundamental.stats.Bonus;
import fundamental.stats.Toll;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class SingularSupportForecast extends IndividualForecast {
    public int hpRecovered = 0, tpRecovered = 0, durabilityRecovered = 0;
    
    private final List<Bonus> gainedBonuses = new ArrayList<>();
    private final Combatant ally;
    private final int fromRange;
    
    public SingularSupportForecast(Combatant participant, Combatant ally, int fromRange) {
        super(participant);
        this.ally = ally;
        this.fromRange = fromRange;
    }
    
    public Combatant getAlly() { return ally; } 
    public int getFromRange() { return fromRange; }
    public List<Bonus> getBonuses() { return gainedBonuses; }
    
    public void addBonus(Bonus B) { gainedBonuses.add(B); }
    
    public void addRecovery(Toll recovery) {
        switch (recovery.getType()) {
            case HP:
                hpRecovered += recovery.getValue();
                break;
            case TP:
                tpRecovered += recovery.getValue();
                break;
            case Durability:
                durabilityRecovered += recovery.getValue();
                break;
            default:
                break;
        }
    }

    @Override
    protected void initialize() {
        //this is called by Forecast.java
    }
    
}
