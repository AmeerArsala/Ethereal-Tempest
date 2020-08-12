/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.talent;

/**
 *
 * @author night
 */
public class TalentConcept {
    private String name;
    
    private TalentCondition condition;
    private TalentEffect effect;
    
    public TalentConcept(String tname, TalentCondition cond, TalentEffect eff) {
        name = tname;
        condition = cond;
        effect = eff;
    }
    
    public String getName() { return name; }
    
    public TalentCondition getTalentCondition() { return condition; }
    public TalentEffect getTalentEffect() { return effect; }
    
    @Override
    public String toString() { return name; }
}
