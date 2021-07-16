/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.data.event;

import battle.data.participant.Combatant;
import etherealtempest.info.Conveyor;
import fundamental.skill.Skill;
import fundamental.stats.BaseStat;
import fundamental.stats.BattleStat;
import java.io.PrintStream;
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
        cStriker.getStatistics().damageDone += damage;
        
        if (didCrit) {
            ++cStriker.getStatistics().numOfCrits;
        }
        
        if (!didHit) {
            ++cVictim.getStatistics().hitsDodged;
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
    
    public String log() {
        StringBuilder log = new StringBuilder("");
        log.append(striker.combatant.getUnit().getName()).append(" attacks!\n")
           .append(striker.combatant.getUnit().getName());
        
        if (didHit) {
            log.append(" hits!\n");
            
            if (didCrit) {
                log.append(striker.combatant.getUnit().getName()).append(" crits!\n");
            } else {
                log.append(striker.combatant.getUnit().getName()).append(" doesn't crit\n");
            }
            
            log.append(striker.combatant.getUnit().getName()).append(" does ").append(damage).append(" damage!\n");
        } else {
            log.append(" misses!\n");
        }
        
        log.append(victim.combatant.getUnit().getName())
           .append(" has ").append(victim.combatant.getUnit().getBaseStat(BaseStat.CurrentHP)).append(" HP remaining!");
        
        return log.toString();
    }
    
    public String log(PrintStream printStream) {
        String log = log();
        printStream.println(log);
        
        return log;
    }
    
    @Override
    public String toString() {
        return super.toString() + "\nlog: " + log();
    }
    
    
    //does not take into account skills and talents
    public static Strike SimpleStrike(Combatant striker, Combatant victim, boolean isSkill) {
        return new Strike(striker, victim, isSkill);
    }
}
