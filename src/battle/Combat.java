/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle;

import battle.data.forecast.PrebattleForecast;
import battle.participant.visual.Fighter;
import com.jme3.scene.Node;

/**
 *
 * @author night
 */
public class Combat {
    private final Node node = new Node("Fight Node");
    private final Fighter.CommonParams common;
    private final Fighter initiator, receiver;
    
    private float flowSpeed = 1;
    
    public Combat(Fighter.CommonParams common, PrebattleForecast forecast) {
        this.common = common;
        
        initiator = new Fighter(forecast.getInitiatorForecast(), common, false); // mirror == false
        receiver = new Fighter(forecast.getReceiverForecast(), common, true);    // mirror == true
        
        Fighter.match(initiator, receiver);

        initiator.attachGUI();
        receiver.attachGUI();
        
        //must be called BEFORE attemptStrike(), because the sprites need parent nodes
        node.attachChild(initiator.getSprite());
        node.attachChild(receiver.getSprite());
        
        initiator.attemptStrike();
        receiver.attemptStrike();
    }
    
    public Node getNode() { return node; }
    public Fighter.CommonParams getCommonParams() { return common; }
    public Fighter getInitiator() { return initiator; }
    public Fighter getReceiver() { return receiver; }
    
    public boolean isFinished() {
        return initiator.getInfoVisualizer().fightIsFullyDone() && receiver.getInfoVisualizer().fightIsFullyDone();
    }
    
    public void detachGUI() {
        initiator.detachGUI();
        receiver.detachGUI();
    }
    
    public void update(float tpf) {
        if (!isFinished()) {
            //initiator.preUpdate();
            //receiver.preUpdate();
            
            initiator.update(tpf * flowSpeed);
            receiver.update(tpf * flowSpeed);
            
            //post update because they can only happen after BOTH the initiator and receiver have updated
            initiator.updatePosData();
            receiver.updatePosData();
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
