/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.data;

import battle.data.event.StrikeTheater;
import battle.data.event.Strike;
import battle.data.participant.Combatant;
import com.jme3.math.Vector2f;

/**
 *
 * @author night
 */
public class DecisionParams {
    public final Vector2f userPos, opponentPos;
    public final StrikeTheater strikeGroup;
    
    private final Combatant user;
    
    private int strikeGroupIndex;
    
    /**
     *
     * @param userPos user position in fight box. Values are from 0.0f to 1.0f
     * @param opponentPos opponent position in fight box. Values are from 0.0f to 1.0f
     * @param strikeGroup StrikeTheater of Strikes in the strike group. Basically it is part of 1 strike and is made up of the strike + extraStrikes, like how brave weapons in FE strike twice every hit
     * @param strikeGroupIndex strike index of the strikeGroup list
     * @param user the user's Combatant object
     * 
     */
    public DecisionParams(Vector2f userPos, Vector2f opponentPos, StrikeTheater strikeGroup, int strikeGroupIndex, Combatant user) {
        this.userPos = userPos;
        this.opponentPos = opponentPos;
        this.strikeGroup = strikeGroup;
        this.strikeGroupIndex = strikeGroupIndex;
        this.user = user;
    }
    
    public int getStrikeIndex() {
        return strikeGroupIndex;
    }
    
    public boolean isFightOver() {
        return strikeGroupIndex >= strikeGroup.getActualStrikes().size();
    }
    
    public void setStrikeIndex(int index) {
        strikeGroupIndex = index;
    }
    
    public void incrementStrikeIndex() {
        ++strikeGroupIndex;
    }
    
    public Strike getCurrentStrike() {
        return strikeGroup.getActualStrike(strikeGroupIndex);
    }
    
    public StrikeTheater.Participant getOpponentRoleForStrike(int index) {
        return strikeGroup.getParticipantRole(user, index).getOpposite();
    }
    
    public StrikeTheater.Participant getUserRoleForStrike(int index) {
        return strikeGroup.getParticipantRole(user, index);
    }
}
