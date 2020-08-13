/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.talent;

import battle.Unit;

/**
 *
 * @author night
 */
public class BattleTalent extends Talent { //proc talent
    private Differentiate btalent;
    
    public BattleTalent(String talentname, String lore, String description, String imgPath, Differentiate D) {
        super(talentname, lore, description, imgPath);
        btalent = D;
    }
    
    public Differentiate getEffect() {
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