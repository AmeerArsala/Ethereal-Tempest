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
public class BattleTalent extends Talent { //proc talent
    private BattleTalentEffect btalent;
    
    public BattleTalent(String talentname, String lore, String description, String imgPath, BattleTalentEffect D) {
        super(talentname, ToolType.Attack, lore, description, imgPath);
        btalent = D;
    }
    
    public BattleTalentEffect getEffect() {
        return btalent;
    }
    
    /*public void callProperty(Unit[] A) {
        btalent.effect(A);
    }*/
    
    /*public boolean talentDoesTrigger(Unit[] A) {
        return btalent.doesTrigger(A);
    }*/
    
    /*public void tryTalent(Unit[] A) {
        if (talentDoesTrigger(A)) { callProperty(A); }
    }*/
    
    //public double tryTalentExtraHits(Unit[] A) { return talentDoesTrigger(A) ? btalent.extraHits(A) : 1.0; } //this will return a BP subtraction coefficient
    
    //public int[] getBonuses(Unit[] A) { return btalent.bonusStats(A); }
    
}
