/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.data.event;

import battle.data.participant.Combatant;

/**
 *
 * @author night
 */
public class StrikeReel {
    public final StrikeTheater strikeTheater;
    private int index;
    
    public StrikeReel(StrikeTheater strikeGroup, int startIndex) {
        strikeTheater = strikeGroup;
        index = startIndex;
    }
    
    public int getIndex() {
        return index;
    }
    
    public int size() {
        return strikeTheater.getActualStrikes().size();
    }

    public boolean isFinished() {
        return index >= strikeTheater.getActualStrikes().size();
    }
    
    public void setIndex(int i) {
        index = i;
    }
    
    public void incrementIndex() {
        ++index;
    }
    
    public Strike getCurrentStrike() {
        return strikeTheater.getActualStrike(index);
    }
    
    public StrikeTheater.Participant getCurrentParticipantRole(Combatant participant) {
        return strikeTheater.getParticipantRole(participant, index);
    }
    
    public Combatant getCurrentParticipant(StrikeTheater.Participant role) {
        return strikeTheater.getParticipant(role, index);
    }
    
    public int getParticipantHPBeforeCurrentStrike(StrikeTheater.Participant role) {
        return strikeTheater.getParticipantHPBefore(role, index);
    }
    
    public int getParticipantHPAfterCurrentStrike(StrikeTheater.Participant role) {
        return strikeTheater.getParticipantHPAfter(role, index);
    }
    
    public int getParticipantHPBeforeCurrentStrike(Combatant participant) {
        return strikeTheater.getParticipantHPBefore(participant, index);
    }
    
    public int getParticipantHPAfterCurrentStrike(Combatant participant) {
        return strikeTheater.getParticipantHPAfter(participant, index);
    }
}
