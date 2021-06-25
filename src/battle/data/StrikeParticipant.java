/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.data;

import etherealtempest.info.Conveyor;
import battle.participant.Combatant;
import battle.participant.Combatant.AttackType;
import fundamental.skill.Skill;
import fundamental.stats.BaseStat;
import fundamental.stats.Toll;
import fundamental.talent.BattleTalent;
import fundamental.talent.Talent;
import fundamental.talent.TalentCondition.Occasion;
import java.util.List;
/**
 *
 * @author night
 */
public class StrikeParticipant {
    public final Combatant combatant;
    private final Skill skillBeingUsed;
    private BattleTalent battleTalentTriggered = null;
    int hpLoss = 0, tpLoss = 0;
    float durabilityChange = 0;
    
    public StrikeParticipant(Combatant combatant) {
        this.combatant = combatant;
        skillBeingUsed = combatant.getUnit().getToUseSkill();
    }
    
    public int getHPLoss() { return hpLoss; }
    public int getTPLoss() { return tpLoss; }
    public float getDurabilityChange() { return durabilityChange; }
    
    public boolean usingSkill() { return skillBeingUsed != null; }
    public boolean triggeredBattleTalent() { return battleTalentTriggered != null; }
    public Skill getSkillUsed() { return skillBeingUsed; }
    public BattleTalent getTriggeredBattleTalent() { return battleTalentTriggered; }
    
    public void setHPLoss(int loss) {
        hpLoss = loss;
    }
    
    public void setTPLoss(int loss) {
        tpLoss = loss;
    }
    
    public void setDurabilityChange(float change) {
        durabilityChange = change;
    }
    
    public void applyLosses() {
        combatant.appendToBaseStat(BaseStat.CurrentHP, -hpLoss);
        combatant.appendToBaseStat(BaseStat.CurrentTP, -tpLoss);
        
        combatant.damageTaken += hpLoss;
        combatant.tpLost += tpLoss;
        
        if (combatant.getAttackType() == AttackType.Weapon) {
            combatant.getUnit().getEquippedWPN().addCurrentDurability(durabilityChange);
            combatant.durabilityUsed += Math.abs(durabilityChange);
        }
    }
    
    //recalculates damage and returns the recalculated amount. Also applies BattleTalents by adding the extraStrikes and sets the battleTalent triggered
    int applyBattleTalents(int baseDamage, StrikeParticipant opponent, Conveyor context, List<Strike> extraStrikes, boolean isOffensive) {
        Combatant striker, victim;
        if (isOffensive) {
            striker = combatant;
            victim = opponent.combatant;
            context.setStriker(this).setVictim(opponent);
        } else {
            striker = opponent.combatant;
            victim = combatant;
            context.setStriker(opponent).setVictim(this);
        }
        
        int damage = baseDamage;
        for (Talent talent : combatant.getUnit().getTalents()) {
            if (talent instanceof BattleTalent) {
                BattleTalent btalent = ((BattleTalent)talent);
                if (isOffensive == btalent.isOffensive() && btalent.doesTrigger(context, striker, victim)) {
                    damage = btalent.recalculateDamage(damage, context, striker, victim);
                    
                    if (isOffensive) {
                        extraStrikes.addAll(btalent.calculateExtraHits(context, striker, victim));
                    }
                    
                    applyBattleTalentOtherEffects(btalent, opponent, context);
                    
                    battleTalentTriggered = btalent;
                    return damage;
                }
            }
        }
        
        return damage;
    }
    
    private void applyBattleTalentOtherEffects(BattleTalent btalent, StrikeParticipant opponent, Conveyor context) {
        btalent.getFullBody().forEach((concept) -> {
            if (concept.getTalentCondition().checkCondition(context, Occasion.DuringCombat)) {
                Toll loss = concept.getTalentEffect().calculateLoss(context).get(opponent.combatant.getUnit());
                if (loss != null) {
                    switch (loss.getType()) {
                        case HP:
                            opponent.hpLoss += loss.getValue();
                            break;
                        case TP:
                            opponent.tpLoss += loss.getValue();
                            break;
                        case Durability:
                            opponent.durabilityChange -= loss.getValue();
                            break;
                    }
                }
                
                concept.getTalentEffect().enactEffect(context);
            }
        });
    }
}
