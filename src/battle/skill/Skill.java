/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.skill;

import battle.Toll;
import fundamental.Associated;

/**
 *
 * @author night
 */
public class Skill extends Associated {
    private String path = "Interface/GUI/skill_icons/empty.png";
    private Toll info;
    private SkillEffect effect;
    
    public Skill(String name, String desc, String path, Toll info, SkillEffect effect) {
        super(name, desc);
        this.path = path;
        this.info = info; 
        this.effect = effect;
    }
    
    public Skill(boolean exists) {
        super(exists);
    }

    public String getPath() { return path; }
    
    public SkillEffect getEffect() { return effect; }
    
    public Skill getNewSkillInstance() {
        return new Skill(name, desc, path, info, effect);
    }
    
    public Toll getToll() { return info; }
    
    @Override
    public String toString() { return name; }
}
