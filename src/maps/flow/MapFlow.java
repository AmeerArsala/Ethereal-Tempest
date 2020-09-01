/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.flow;

import etherealtempest.FSM;
import etherealtempest.FsmState;
import general.GeneralUtils;
import java.util.ArrayList;
import java.util.List;
import maps.layout.TangibleUnit;

/**
 *
 * @author night
 */
public class MapFlow { //eventually make this the map controller
    
    public enum Turn {
        Player,
        Enemy,
        Ally,
        XthParty;
    }
    
    private final List<Turn> partiesInvolved;
    
    private ArrayList<TangibleUnit> units = new ArrayList<>();
    private Turn turn; //phase
    private int currentTurn = 1, phaseIndex = 0;
    
    private final FSM fsm = new FSM() {
        @Override
        public void setNewStateIfAllowed(FsmState st) {
            state = st;
        }
    };
    
    public MapFlow(List<Turn> partiesInvolved) {
        this.partiesInvolved = partiesInvolved;
    }
    
    public FSM getFSM() { return fsm; }
    
    public void initialize(UnitPlacementInitiation init) {
        init.initiation(units);
    }
    
    public void update(float tpf, FSM mapFSM) { //use this for the phase switching animations and stuff
        units.forEach((tu) -> {
            tu.update(1f / 60f, mapFSM);
        });
    }
    
    public Turn getTurn() { return turn; }
    public int getTurnNumber() { return currentTurn; }
    
    public ArrayList<TangibleUnit> getUnits() { return units; }
    
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
