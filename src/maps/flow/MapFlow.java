/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.flow;

import general.GeneralUtils;
import java.util.List;

/**
 *
 * @author night
 */
public class MapFlow {
    public enum Turn {
        Player,
        Enemy,
        Ally,
        XthParty;
    }
    
    private final List<Turn> partiesInvolved;
    
    private Turn turn; //phase
    private int currentTurn = 1, phaseIndex = 0;
    
    public MapFlow(List<Turn> partiesInvolved) {
        this.partiesInvolved = partiesInvolved;
    }
    
    public Turn getTurn() { return turn; }
    public int getTurnNumber() { return currentTurn; }
    
    public void setTurnNumber(int num) {
        currentTurn = num;
    }
    
    public void goToNextPhase() {
        phaseIndex++;
        turn = partiesInvolved.get(0);
    }
    
    public String getPhaseString() { //Player Turn, Enemy Turn, Ally Turn, 3rd party turn, 4th party turn, 5th party turn... etc.
        return (turn != Turn.XthParty ? turn.name() : ("" + phaseIndex + GeneralUtils.ordinalNumberSuffix(phaseIndex) + "Party")) + " Turn";
    }
}
