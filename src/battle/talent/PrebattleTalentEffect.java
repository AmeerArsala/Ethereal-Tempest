/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.talent;

import battle.Combatant;
import battle.Conveyer;
import battle.StatValue;
import java.util.List;

/**
 *
 * @author night
 */
public abstract class PrebattleTalentEffect {
    protected Combatant user, opponent;
    protected Conveyer info;
    
    public void inputData(Conveyer data, Combatant talentUser, Combatant enemy) {
        info = data;
        user = talentUser;
        opponent = enemy;
    }
    
    public abstract List<StatValue> bonuses(); //both battle and base stat bonuses
    
    public abstract void enactExtraEffect();
    
    //Vantage basically
    /*
        new PrebattleTalentEffect() {
            @Override
            public List<StatValue> bonuses() {
                return null;
            }
            
            @Override
            public void enactExtraEffect() {
                user = BattleRole.Initiator;
                opponent = BattleRole.Receiver;
            }
        };
    
    */
    
}
