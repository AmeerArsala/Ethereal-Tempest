/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle;

import battle.data.CombatFlowData;
import battle.data.event.StrikeReel;
import battle.data.forecast.PrebattleForecast;
import battle.data.forecast.SingularForecast;
import battle.data.participant.Combatant;
import battle.participant.Fighter;
import battle.participant.Fighter.CommonParams;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import general.utils.wrapper.Duo;

/**
 *
 * @author night
 */
public class Combat {
    private final Node node = new Node("Fight Node");
    private final PrebattleForecast forecast;
    private final CommonParams common;
    private final CombatFlowData combatData;
    private final Fighter initiator, receiver;
    
    private float flowSpeed = 1f;
    
    @SuppressWarnings("Convert2Diamond")
    public Combat(PrebattleForecast forecast, Fighter.CommonParams common) {
        this.forecast = forecast;
        this.common = common;
        
        SingularForecast initiatorForecast = forecast.getInitiatorForecast(), receiverForecast = forecast.getReceiverForecast();
        
        combatData = new CombatFlowData(
            new StrikeReel(forecast.createStrikeEvents(), 0),
            new Duo<Combatant, Vector2f>(initiatorForecast.getCombatant(), new Vector2f(0, 0)),
            new Duo<Combatant, Vector2f>(receiverForecast.getCombatant(), new Vector2f(0, 0))
        );
        
        initiator = new Fighter(initiatorForecast, combatData, common, false); // mirror == false
        receiver = new Fighter(receiverForecast, combatData, common, true);    // mirror == true
        
        initiator.setOpponent(receiver);
        receiver.setOpponent(initiator);

        initiator.attachGUI();
        receiver.attachGUI();
        
        //must be called BEFORE attemptStrike(), because the sprites need parent nodes
        node.attachChild(initiator.getSprite());
        node.attachChild(receiver.getSprite());
        
        initiator.attemptStrike();
        receiver.attemptStrike();
    }
    
    public Node getNode() { return node; }
    public PrebattleForecast getForecast() { return forecast; }
    public CommonParams getCommonParams() { return common; }
    public CombatFlowData getCombatFlowData() { return combatData; }
    public Fighter getInitiator() { return initiator; }
    public Fighter getReceiver() { return receiver; }
    
    public boolean isFinished() {
        return initiator.getInfoVisualizer().fightIsFullyDone() && receiver.getInfoVisualizer().fightIsFullyDone();
    }
    
    public void detachGUI() {
        initiator.detachGUI();
        receiver.detachGUI();
    }
    
    public void end() {
        detachGUI();
        //TODO: add more here
    }
    
    public void update(float tpf) {
        if (!isFinished()) {
            initiator.update(tpf * flowSpeed);
            receiver.update(tpf * flowSpeed);
            
            //update positions after BOTH the initiator and receiver have updated
            combatData.updatePositions();
        }
    }
    
    public Fight.State resolveInput(String name, float tpf, boolean keyPressed, Fight.State currentState) {
        if (currentState != Fight.State.InProgress) {
            return currentState;
        }
        
        if (name.equals("select") || name.equals("deselect")) {
            if (initiator.canFinishWithAnInput() && receiver.canFinishWithAnInput()) {
                //end fight
                return Fight.State.TransitioningOut;
            }
        }
        
        if (name.equals("select")) {
            if (keyPressed) {
                flowSpeed = 2;
            } else { //onRelease
                flowSpeed = 1;
            }
        }
        
        return currentState;
    }
}
