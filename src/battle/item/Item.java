/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.item;

import battle.skill.Skill;
import battle.talent.Talent;
import fundamental.FreelyAssociated;

/**
 *
 * @author night
 */
public class Item extends FreelyAssociated {
    protected int Weight;
    protected int worth;
    
    protected ItemEffect useEffect = null;
    
    public Item(String name, String desc, int Weight, int worth) {
        super(name, desc);
        this.Weight = Weight;
        this.worth = worth;
    }
    
    public Item(String name, String desc, int Weight, int worth, ItemEffect useEffect) {
        super(name, desc);
        this.Weight = Weight;
        this.worth = worth;
        this.useEffect = useEffect;
    }
    
    public Item(String name, String desc, int Weight, int worth, Skill extraSkill, Talent extraTalent) {
        super(name, desc, extraTalent, extraSkill);
        this.Weight = Weight;
        this.worth = worth;
    }
    
    public Item(String name, String desc, int Weight, int worth, ItemEffect useEffect, Skill extraSkill, Talent extraTalent) {
        super(name, desc, extraTalent, extraSkill);
        this.Weight = Weight;
        this.worth = worth;
        this.useEffect = useEffect;
    }
    
    public Item(String name, String desc, int Weight, int worth, Skill extraSkill) {
        super(name, desc, extraSkill);
        this.Weight = Weight;
        this.worth = worth;
    }
    
    public Item(String name, String desc, int Weight, int worth, ItemEffect useEffect, Skill extraSkill) {
        super(name, desc, extraSkill);
        this.Weight = Weight;
        this.worth = worth;
        this.useEffect = useEffect;
    }
    
    public Item(String name, String desc, int Weight, int worth, Talent extraTalent) {
        super(name, desc, extraTalent);
        this.Weight = Weight;
        this.worth = worth;
    }
    
    public Item(String name, String desc, int Weight, int worth, ItemEffect useEffect, Talent extraTalent) {
        super(name, desc, extraTalent);
        this.Weight = Weight;
        this.worth = worth;
        this.useEffect = useEffect;
    }
    
    public Item(boolean exists) {
        super(exists);
    }
    
    public int getWeight() { return Weight; }
    public int getWorth() { return worth; }
    
    public ItemEffect getItemEffect() { return useEffect; }
}
