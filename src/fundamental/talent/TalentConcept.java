/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.talent;

/**
 *
 * @author night
 */
public class TalentConcept {
    private TalentCondition condition;
    private TalentEffect effect;
    
    public TalentConcept(TalentCondition cond, TalentEffect eff) {
        condition = cond;
        effect = eff;
    }
    
    public TalentCondition getTalentCondition() { return condition; }
    public TalentEffect getTalentEffect() { return effect; }
    
    @Override
    public String toString() {
        return condition.toString() + effect.toString();
    }
}
