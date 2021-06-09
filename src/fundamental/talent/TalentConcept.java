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
    private String desc;
    
    public TalentConcept(TalentCondition cond, TalentEffect eff) {
        condition = cond;
        effect = eff;
        desc = condition.toString() + effect.toString();
    }
    
    public TalentConcept(String description, TalentCondition cond, TalentEffect eff) {
        condition = cond;
        effect = eff;
        desc = description;
    }
    
    public TalentCondition getTalentCondition() { return condition; }
    public TalentEffect getTalentEffect() { return effect; }
    
    @Override
    public String toString() {
        return desc;
    }
}
