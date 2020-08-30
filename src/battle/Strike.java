/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle;

import battle.Combatant.AttackType;
import battle.Combatant.BaseStat;
import battle.Combatant.BattleStat;
import battle.skill.Skill;
import battle.talent.BattleTalent;
import battle.talent.Talent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class Strike {
    private Combatant striker, victim;
    private Conveyer info;
    
    private boolean didHit, didCrit, isSkill;
    private int damage = 0;
    private float displayedAccuracy, actualAccuracy, durabilityChange;
    private List<Strike> extraStrikes = new ArrayList<>();
    private BattleTalent talentTriggered = null;
    
    public int strikeToken = 1;
    public boolean occurred = false;
    
    //private String strikeType = "";
    
    //strikePoints = BP lost = max BP / amount of strikes that will occur at first glance
    //strikePoints lost = max strikePoints / amount of strikes within a strike
    
    public Strike(Combatant striker, Combatant victim, Conveyer info, boolean isSkill) {
        this.striker = striker;
        this.victim = victim;
        this.info = info;
        this.isSkill = isSkill;
        
        calculateData();
    }
    
    public Strike(Combatant striker, Combatant victim, boolean isSkill) {
        this.striker = striker;
        this.victim = victim;
        this.isSkill = isSkill;
        
        simpleCalculateData();
    }
    
    public Combatant getStriker() { return striker; }
    public Combatant getVictim() { return victim; }
    
    public boolean strikeDidHit() { return didHit; }
    public boolean strikeIsCrit() { return didCrit; }
    public boolean strikeWasParried() { return false; } //change after
    public boolean strikeIsSkill() { return isSkill; }
    
    public int getDamage() { return damage; }
    
    public float getDisplayedAccuracy() { return displayedAccuracy; }
    public float getActualAccuracy() { return actualAccuracy; }
    
    public float getStrikerDurabilityChange() { return durabilityChange; }
    
    public List<Strike> getExtraStrikes() { return extraStrikes; }
    
    public BattleTalent getTriggeredTalent() { return talentTriggered; }
    
    protected void applyBattleTalents() {
        for (Talent talent : striker.getUnit().getTalents()) {
            if (talent instanceof BattleTalent && ((BattleTalent)talent).getEffect().doesTrigger()) {
                talentTriggered = ((BattleTalent)talent);
                ((BattleTalent)talent).getEffect().inputData(info, striker, victim);
                damage = ((BattleTalent)talent).getEffect().modifyDamage(damage);
                ((BattleTalent)talent).getEffect().applyExtraHits(extraStrikes);
                return;
            }
        }
    }
    
    protected void applySkillExtraEffects() {
        Skill skill = striker.getUnit().getToUseSkill();
        skill.getEffect().setData(info, striker, victim);
        skill.getEffect().applyExtraHits(extraStrikes);
    }
    
    //realtime strike
    private void calculateData() {
        displayedAccuracy = striker.getBattleStat(BattleStat.Accuracy) - victim.getBattleStat(BattleStat.Evasion);
        actualAccuracy = 10 * displayedAccuracy;
        int RN = (int)(1 + Math.random() * 1000);
        if (displayedAccuracy >= 60) {
            actualAccuracy = (float)((displayedAccuracy * 100) + ((40/3.0) * displayedAccuracy * Math.sin((0.017 * displayedAccuracy) - 1)));
        }
        
        int critRN = (int)(1 + (Math.random() * 100));
        
        if (RN <= actualAccuracy) {
            //it hit
            didHit = true;
            durabilityChange = -1.0f;
            
            damage = striker.getDefaultDamage();
            
            if (isSkill) {
                applySkillExtraEffects();
            }
            
            //IN BETWEEN: FACTOR IN BATTLE TALENTS OR SKILLS BUT NOT BOTH AT ONCE
            applyBattleTalents();
            
            if (critRN <= striker.getBattleStat(BattleStat.Crit)) {
                didCrit = true;
                damage *= 2; //crit modifier
            } else { didCrit = false; }
        } else {
            //it missed
            didHit = false;
            didCrit = false;
            durabilityChange = -0.5f;
        }
    }
    

    private void simpleCalculateData() {
        displayedAccuracy = striker.getBattleStat(BattleStat.Accuracy) - victim.getBattleStat(BattleStat.Evasion);
        actualAccuracy = 10 * displayedAccuracy;
        int RN = (int)(1 + Math.random() * 1000);
        if (displayedAccuracy >= 60) {
            actualAccuracy = (float)((displayedAccuracy * 100) + ((40/3.0) * displayedAccuracy * Math.sin((0.017 * displayedAccuracy) - 1)));
        }
        
        int critRN = (int)(1 + (Math.random() * 100));
        
        if (RN <= actualAccuracy) {
            //it hit
            didHit = true;
            durabilityChange = -1.0f;
            
            /*if (striker.getAttackType() == AttackType.Weapon) {
                damage = striker.getBattleStat(BattleStat.AttackPower) 
                    - (striker.getUnit().getEquippedWeapon().getDmgType().equals("ether") ? victim.getBaseStat(BaseStat.resilience) 
                    : victim.getBaseStat(BaseStat.defense));
            } else {
                damage = striker.getBattleStat(BattleStat.AttackPower) - victim.getBaseStat(BaseStat.resilience);
            }*/
            damage = striker.getDefaultDamage();
            
            //simple
            
            if (isSkill) {
                damage += striker.getUnit().getToUseSkill().getEffect().extraDamage();
                applySkillExtraEffects();
            }
            
            if (critRN <= striker.getBattleStat(BattleStat.Crit)) {
                didCrit = true;
                damage *= 2; //crit modifier
            } else { didCrit = false; }
        } else {
            //it missed
            didHit = false;
            didCrit = false;
            durabilityChange = -0.5f;
        }
    }
    
}
