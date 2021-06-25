/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.data;

import battle.participant.Combatant;
import etherealtempest.info.Conveyor;
import fundamental.skill.Skill;
import fundamental.stats.BaseStat;
import fundamental.stats.BattleStat;
import fundamental.stats.Toll;
import fundamental.stats.Toll.Exchange;
import fundamental.unit.CharacterUnitInfo;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class Strike {
    private final StrikeParticipant striker, victim;
    private Conveyor info;
    
    private float displayedAccuracy, actualAccuracy;
    private boolean didHit, didCrit;
    private final boolean isSkill;
    private int damage = 0;
    
    private final List<Strike> extraStrikes = new ArrayList<>();
    
    //strikePoints = BP lost = max BP / amount of strikes that will occur at first glance
    //strikePoints lost = max strikePoints / amount of strikes within a strike
    
    public Strike(Combatant striker, Combatant victim, Conveyor info, boolean isSkill) {
        this.striker = new StrikeParticipant(striker);
        this.victim = new StrikeParticipant(victim);
        this.info = info;
        this.isSkill = isSkill;
        
        calculateData(false);
    }
    
    private Strike(Combatant striker, Combatant victim, boolean isSkill) {
        this.striker = new StrikeParticipant(striker);
        this.victim = new StrikeParticipant(victim);
        this.isSkill = isSkill;
        
        calculateData(true);
    }
    
    public StrikeParticipant getStriker() { return striker; }
    public StrikeParticipant getVictim() { return victim; }
    
    public float getDisplayedAccuracy() { return displayedAccuracy; }
    public float getActualAccuracy() { return actualAccuracy; }
    
    public boolean didHit() { return didHit; }
    public boolean isCrit() { return didCrit; }
    public boolean isSkill() { return isSkill; }
    
    public int getDamage() { return damage; }
    
    public List<Strike> getExtraStrikes() { return extraStrikes; }
    
    public List<Strike> getAllStrikesFromThis() {
        List<Strike> all = new ArrayList<>();
        all.add(this);
        all.addAll(extraStrikes);
        
        return all;
    }
    
    public void apply() {
        victim.applyLosses();
        striker.applyLosses();
        
        Combatant cStriker = striker.combatant, cVictim = victim.combatant;
        cStriker.damageDone += damage;
        
        if (didCrit) {
            ++cStriker.numOfCrits;
        }
        
        if (!didHit) {
            ++cVictim.hitsDodged;
        }
    }
    
    private void applyBattleTalents() {
        damage = striker.applyBattleTalents(damage, victim, info, extraStrikes, true);
        damage = victim.applyBattleTalents(damage, striker, info, extraStrikes, false);
    }
    
    private void applySkill() {
        Combatant cStriker = striker.combatant, cVictim = victim.combatant;
        
        Skill skill = cStriker.getUnit().getToUseSkill();
        extraStrikes.addAll(skill.getEffect().calculateExtraStrikes(cStriker, cVictim));
        
        //TODO: do more here
    }
    
    private void calculateData(boolean simple) {
        Combatant cStriker = striker.combatant, cVictim = victim.combatant;
        
        displayedAccuracy = cStriker.getBattleStat(BattleStat.Accuracy) - cVictim.getBattleStat(BattleStat.Evasion);
        actualAccuracy = 10 * displayedAccuracy;
        int RN = (int)(1 + Math.random() * 1000);
        if (displayedAccuracy >= 60) {
            actualAccuracy = (float)((displayedAccuracy * 100) + ((40/3.0) * displayedAccuracy * Math.sin((0.017 * displayedAccuracy) - 1)));
        }
        
        int critRN = (int)(1 + (Math.random() * 100));
        
        if (isSkill && !simple) {
            applySkill();
        }
        
        if (RN <= actualAccuracy) {
            //it hit
            didHit = true;
            striker.durabilityChange -= 1.0f;
            
            damage = cStriker.getDefaultDamage();
            
            if (!simple) {
                //IN BETWEEN: FACTOR IN BATTLE TALENTS OR SKILLS BUT NOT BOTH AT ONCE
                applyBattleTalents();
            }
            
            if (critRN <= cStriker.getBattleStat(BattleStat.Crit)) {
                didCrit = true;
                damage *= 2; //crit modifier
            } else { 
                didCrit = false; 
            }
        } else {
            //it missed
            didHit = false;
            didCrit = false;
            damage = 0;
            striker.durabilityChange -= 0.5f;
        }
        
        victim.hpLoss += damage;
        //TODO: do more here
    }
    
    
    //does not take into account skills and talents
    public static Strike SimpleStrike(Combatant striker, Combatant victim, boolean isSkill) {
        return new Strike(striker, victim, isSkill);
    }
    
    public static void strikelog(Strike strike) {
        String info = "";
        
        info += strike.getStriker().combatant.getUnit().getName() + " attacks!\n" + strike.getStriker().combatant.getUnit().getName();
        
        if (strike.didHit()) {
            info += " hits!\n";
            
            if (strike.isCrit()) {
                info += strike.getStriker().combatant.getUnit().getName() + " crits!\n";
            } else {
                info += strike.getStriker().combatant.getUnit().getName() + " doesn't crit\n";
            }
            
            strike.getVictim().combatant.getUnit().addToll(new Toll(Exchange.HP, -strike.getDamage()));
            
            info += strike.getStriker().combatant.getUnit().getName() + " does " + strike.getDamage() + " damage!\n";
        } else {
            info += " misses!\n";
        }
        
        info += strike.getVictim().combatant.getUnit().getName() + " has " + strike.getVictim().combatant.getUnit().getBaseStat(BaseStat.CurrentHP) + " HP remaining!\n";
        
        System.out.println(info);
    }
    
}
