/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.data.participant;

import fundamental.unit.CharacterUnitInfo;

/**
 *
 * @author night
 */
public class CombatantStatistics {
    public static final int MAX_EXP_VALUE = 100;
    
    public int expGained;
    public int damageDone, damageTaken, tpLost, hitsDodged, numOfCrits;
    public float durabilityUsed;
    
    public CombatantStatistics() {
        expGained = 0;
        damageDone = 0;
        damageTaken = 0;
        tpLost = 0;
        hitsDodged = 0;
        numOfCrits = 0;
        durabilityUsed = 0.0f;
    }
    
    public CombatantStatistics(int expGained, int damageDone, int damageTaken, int tpLost, int hitsDodged, int numOfCrits, float durabilityUsed) {
        this.expGained = expGained;
        this.damageDone = damageDone;
        this.damageTaken = damageTaken;
        this.tpLost = tpLost;
        this.hitsDodged = hitsDodged;
        this.numOfCrits = numOfCrits;
        this.durabilityUsed = durabilityUsed;
    }
    
    void apply(CharacterUnitInfo info) {
        info.addTotalExpGained(expGained);
        info.addTotalDamageDone(damageDone);
        info.addTotalDamageTaken(damageTaken);
        info.addTotalDurabilityUsed(durabilityUsed);
        info.addTotalTPlost(tpLost);
        info.addTotalHitsDodged(hitsDodged);
        info.addTotalCriticals(numOfCrits);
    }
}
