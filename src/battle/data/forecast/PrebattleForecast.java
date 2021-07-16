/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.data.forecast;

import battle.data.event.StrikeTheater;
import battle.data.participant.Combatant;
import battle.data.participant.BattleRole;
import etherealtempest.info.Conveyor;

/**
 *
 * @author night
 */
public class PrebattleForecast extends Forecast<SingularForecast> {
    //Conveyor only needs initiator and receiver in this case
    public PrebattleForecast(Conveyor context) {
        this(new Combatant(context, BattleRole.Initiator), new Combatant(context, BattleRole.Receiver), context);
    }
    
    public PrebattleForecast(Combatant initiator, Combatant receiver, Conveyor data) {
        super(
                (RANGE) -> { return new SingularForecast(initiator, receiver, RANGE); }, 
                (RANGE) -> { return new SingularForecast(receiver, initiator, RANGE); }, 
                data, 
                false
        );
    }
    
    private PrebattleForecast(Combatant initiator, Combatant receiver, Conveyor data, int customRange) {
        super(
                new SingularForecast(initiator, receiver, customRange), 
                new SingularForecast(receiver, initiator, customRange), 
                data, 
                false, 
                customRange
        );
    }
    
    @Override
    public int calculateDesirabilityToInitiate() { //desirability to be initiator
        return initiatorForecast.calculateDesirabilityToInitiateAgainst(receiverForecast);
    }
    
    public StrikeTheater createStrikeEvents() {
        SingularForecast initiator, receiver;
        if (initiatorForecast.isInitiator()) {
            initiator = initiatorForecast;
            receiver = receiverForecast;
        } else { // some vantage happened and the roles are switched
            initiator = receiverForecast;
            receiver = initiatorForecast;
        }
        
        return new StrikeTheater(initiator, receiver, data);
    }
    
    
    public static PrebattleForecast createSimulatedForecast(Conveyor info, int fromRange) { //simulated battle
        Combatant initiator = new Combatant(info, BattleRole.Initiator), receiver = new Combatant(info, BattleRole.Receiver);
        
        return new PrebattleForecast(initiator, receiver, info, fromRange);
    }
}
