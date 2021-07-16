/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.data.forecast;

import battle.data.event.Strike;
import battle.data.participant.Combatant;
import battle.data.participant.BattleRole;
import etherealtempest.fsm.FSM.UnitState;
import etherealtempest.info.Conveyor;
import fundamental.skill.Skill;
import fundamental.stats.BaseStat;
import fundamental.stats.BattleStat;
import fundamental.tool.DamageTool;

/**
 *
 * @author night
 */
public class SingularForecast extends IndividualForecast {
    public static final int DEFAULT_HIT_COST = 1000; //1000 for one strike
    
    public boolean canCounterattack;
    
    private float displayedAccuracy;
    private int displayedDamage;
    
    private int BPcostPerHit = DEFAULT_HIT_COST;
    
    private final Combatant opponent;
    private final int range;
    
    public SingularForecast(Combatant participant, Combatant adversary, int fromRange) {
        super(participant);
        opponent = adversary;
        range = fromRange;
    }
    
    public Combatant getFoe() { return opponent; }
    public int getFromRange() { return range; }
    
    public boolean isUsingSkill() { return combatant.isUsingSkill(); }
    public Skill getSkillBeingUsed() { return combatant.getUnit().getToUseSkill(); }
    public DamageTool getEquippedDamageTool() { return ((DamageTool)getEquippedTool()); }
    
    public float getDisplayedAccuracy() { return displayedAccuracy; }
    public int getDisplayedDamage() { return displayedDamage; }
    public int getBPcostPerHit() { return BPcostPerHit; }
    
    public int getInitialBP(boolean isInitiator) {
        return isInitiator || (!isInitiator && canCounterattack) ? DEFAULT_HIT_COST : 0;
    }
    
    public int getHitCount() { //number of hits
        return DEFAULT_HIT_COST / BPcostPerHit;
    }
    
    public void setHitCount(int hits) {
        BPcostPerHit = DEFAULT_HIT_COST / hits;
    }
    
    public void multiplyHitCountBy(int factor) {
        BPcostPerHit /= factor;
    }
    
    public void setDisplayedDamage(int dmg) {
        displayedDamage = dmg;
        combatant.setDefaultDamage(dmg);
    }
    
    public void forceDisplayedAccuracy(float acc) {
        displayedAccuracy = acc;
    }
    
    public Strike createStrike(Conveyor context) {
        return new Strike(combatant, opponent, context, combatant.isUsingSkill());
    }
    
    public boolean continueFightingCondition(int BP, Conveyor context) {
        if (combatant.isUsingSkill()) {
            return getSkillBeingUsed().getEffect().continueFightingCondition(BP, context, combatant, opponent);
        }
        
        return BP > 0;
    }
    
    public void calculateExpToGain() {
        combatant.calculateEXP(opponent);
    }
    
    public void finishFight() {
        combatant.applyAllStatsToUnit();
        combatant.getUnit().getUnitInfo().incrementFights();
        if (combatant.getBaseStat(BaseStat.CurrentHP) <= 0) { //dead
            combatant.getUnit().getUnitInfo().incrementLosses();
            combatant.getUnit().getFSM().setNewStateIfAllowed(UnitState.Dying);
        } else if (opponent.getBaseStat(BaseStat.CurrentHP) <= 0) {
            opponent.getUnit().getUnitInfo().incrementWins();
        }
    }
    
    @Override
    protected void initialize() {
        //determine whether counterattack is possible
        if (combatant.battle_role == BattleRole.Initiator) {
            canCounterattack = true;
        } else { // is receiver
            canCounterattack = combatant.getUnit().canCounterattackAgainst(range);
        }
        
        int extraDamage = 0;
        Skill skillBeingUsed = getSkillBeingUsed();
        
        //determine hit count
        if (skillBeingUsed != null) {
            setHitCount(skillBeingUsed.getEffect().getHits());
            extraDamage += skillBeingUsed.getEffect().getExtraDamage();
        } else if (combatant.getBattleStat(BattleStat.AttackSpeed) - opponent.getBattleStat(BattleStat.AttackSpeed) >= 3) {
            //doubles
            multiplyHitCountBy(2);
        } else {
            //no double
            setHitCount(1);
        }
        
        //determine displayed hit rate
        displayedAccuracy = combatant.getBattleStat(BattleStat.Accuracy) - opponent.getBattleStat(BattleStat.Evasion);
        if (displayedAccuracy > 100) {
            displayedAccuracy = 100;
        } else if (displayedAccuracy < 0) {
            displayedAccuracy = 0;
        }
        
        //determine displayed crit rate
        displayedCrit = combatant.getBattleStat(BattleStat.Crit) - opponent.getBattleStat(BattleStat.CritEvasion);
        if (displayedCrit > 100) {
            displayedCrit = 100;
        } else if (displayedCrit < 0) {
            displayedCrit = 0;
        }
        
        //determine displayed damage
        int defensiveStat = opponent.getBaseStat(getEquippedDamageTool().getDamageMeasuredAgainstStat());
        setDisplayedDamage(combatant.getBattleStat(BattleStat.AttackPower) - defensiveStat + extraDamage);
    }
    
    protected int calculateDesirabilityToInitiateAgainst(SingularForecast adversary) {
        int desirability = 
                ((displayedDamage * 15) * getHitCount()) //damage and hits
                + (int)(displayedAccuracy + displayedCrit); //accuracy and crit
        
        desirability += (30 - adversary.displayedDamage >= 0 ? 30 - adversary.displayedDamage : 0);
        desirability += 10 - (adversary.displayedAccuracy / 10);
        desirability += 5 - (adversary.displayedCrit / 20);
        
        if (adversary.getCombatant().getBaseStat(BaseStat.CurrentHP) - displayedDamage <= 0) {
            desirability += 500;
        } else if (adversary.getCombatant().getBaseStat(BaseStat.CurrentHP) - (displayedDamage * getHitCount()) <= 0) {
            desirability += 300;
        }
        
        if (!adversary.canCounterattack) { desirability += 1000; }
        
        return desirability;
    }

}
