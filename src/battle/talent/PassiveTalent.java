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
public class PassiveTalent extends Talent {
    //default stat bonuses MUST be a list of 0's
    private final TalentConcept wrapper;
    private TalentConcept wrapper2;
    
    public PassiveTalent(String talentname, String loredescription, String description, String imgPath, TalentConcept te) {
        super(talentname, loredescription, description, imgPath);
        wrapper = te;
    }
    
    public PassiveTalent(String talentname, String lore, String description, String imgPath, TalentConcept te, TalentConcept te2) {
        super(talentname, lore, description, imgPath);
        wrapper = te;
        wrapper2 = te2;
    }
    
    public TalentConcept getTalentBody() { return wrapper; }
    public TalentConcept getTalentBody2() { return wrapper2; }
    
    public TalentConcept[] getAllTalentBodies() {
        if (wrapper2 != null) {
            return new TalentConcept[]{wrapper, wrapper2};
        }
        return new TalentConcept[]{wrapper};
    }
    
}
