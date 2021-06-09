/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental;

import fundamental.skill.Skill;
import fundamental.talent.Talent;

/**
 *
 * @author night
 */
public class FreelyAssociated extends Associated {
    //these are on having said item in your inventory or having learned said formula
    protected Talent extraTalent = null;
    protected Skill extraSkill = null;
    
    public FreelyAssociated(String name, String desc) {
        super(name, desc);
    }
    
    public FreelyAssociated(String name, String desc, Talent extraTalent) {
        super(name, desc);
        this.extraTalent = extraTalent;
    }
    
    public FreelyAssociated(String name, String desc, Skill extraSkill) {
        super(name, desc);
        this.extraSkill = extraSkill;
    }
    
    public FreelyAssociated(String name, String desc, Talent extraTalent, Skill extraSkill) {
        super(name, desc);
        this.extraTalent = extraTalent;
        this.extraSkill = extraSkill;
    }
    
    public FreelyAssociated(boolean exists) {
        super(exists);
    }
    
    public Talent getExtraTalent() { return extraTalent; }
    public Skill getExtraSkill() { return extraSkill; }
    
    public void setExtraTalent(Talent extra) {
        extraTalent = extra;
    }
    
    public void setExtraSkill(Skill extra) {
        extraSkill = extra;
    }
}
