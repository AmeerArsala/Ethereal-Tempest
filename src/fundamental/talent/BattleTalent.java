/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.talent;

import fundamental.tool.Tool.ToolType;

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
    
}
