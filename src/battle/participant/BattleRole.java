/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.participant;

/**
 *
 * @author night
 */
public enum BattleRole {
    Initiator,
    Receiver;
        
    private BattleRole opponent;
        
    static {
        Initiator.setOpponent(Receiver);
        Receiver.setOpponent(Initiator);
    }
        
    public void setOpponent(BattleRole br) { opponent = br; }
    public BattleRole getOpponent() { return opponent; }
}
