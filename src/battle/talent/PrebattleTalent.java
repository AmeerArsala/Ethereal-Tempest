/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.talent;

import fundamental.Tool.ToolType;

/**
 *
 * @author night
 */
public class PrebattleTalent extends Talent {
    private final TalentCondition condition;
    private final PrebattleTalentEffect effect;
    
    public PrebattleTalent(String talentname, ToolType type, String loredescription, String description, String imgPath, TalentCondition condition, PrebattleTalentEffect effect) {
        super (talentname, type, loredescription, description, imgPath);
        this.condition = condition;
        this.effect = effect;
    }
    
    public TalentCondition getCondition() {
        return condition;
    }
    
    public PrebattleTalentEffect getEffect() {
        return effect;
    }
    
}
