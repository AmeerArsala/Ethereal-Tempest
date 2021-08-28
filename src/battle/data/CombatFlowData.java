/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.data;

import battle.data.event.Strike;
import battle.data.event.StrikeReel;
import battle.data.event.StrikeTheater;
import battle.data.participant.Combatant;
import battle.environment.BoxMetadata;
import battle.participant.visual.BattleSprite;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import general.utils.wrapper.Duo;
import java.util.function.Supplier;

/**
 *
 * @author night
 */
public class CombatFlowData {
    public final StrikeReel strikeReel;
    private final Representative repA, repB;
    public int minStrikeIndexToReceiveImpact;
    
    public CombatFlowData(StrikeReel reel, Duo<Combatant, Vector2f> participantA_params, Duo<Combatant, Vector2f> participantB_params) {
        strikeReel = reel;
        minStrikeIndexToReceiveImpact = strikeReel.getIndex();
        repA = new Representative(participantA_params.first, participantA_params.second);
        repB = new Representative(participantB_params.first, participantB_params.second);
        
        repA.opponent = repB;
        repB.opponent = repA;
    }
    
    public Representative getRepresentative(Combatant participant) {
        if (repA.getParticipant().equals(participant)) {
            return repA;
        }
        
        if (repB.getParticipant().equals(participant)) {
            return repB;
        }
        
        return null;
    }
    
    public void updatePositions() {
        repA.updatePos();
        repB.updatePos();
    }
    
    public class Representative {
        private final Combatant participant;
        private final Vector2f pos;
        
        private BattleSprite sprite;
        private BoxMetadata battleBoxInfo;
        private Camera cam;
        
        Representative opponent;
        
        public Representative(Combatant participant, Vector2f pos) {
            this.participant = participant;
            this.pos = pos;
        }
        
        public Combatant getParticipant() {
            return participant;
        }
        
        public Vector2f getPos() {
            return pos;
        }
        
        public Representative getOpponent() { //call this to use the opponent's methods
            return opponent;
        }
        
        public StrikeReel getStrikeReel() {
            return strikeReel;
        }
    
        public StrikeTheater.Participant getRoleForStrike(int index) {
            return strikeReel.strikeTheater.getParticipantRole(participant, index);
        }
        
        public StrikeTheater.Participant getRoleForCurrentStrike() {
            return strikeReel.getCurrentParticipantRole(participant);
        }
        
        public int getHPBeforeStrike(int index) {
            return strikeReel.strikeTheater.getParticipantHPBefore(participant, index);
        }
        
        public int getHPAfterStrike(int index) {
            return strikeReel.strikeTheater.getParticipantHPAfter(participant, index);
        }
        
        public int getHPBeforeCurrentStrike() {
            return strikeReel.getParticipantHPBeforeCurrentStrike(participant);
        }
        
        public int getHPAfterCurrentStrike() {
            return strikeReel.getParticipantHPAfterCurrentStrike(participant);
        }
        
        public int getMinStrikeIndexToReceiveImpact() {
            return minStrikeIndexToReceiveImpact;
        }
        
        public boolean canReceiveImpact() {
            return strikeReel.getIndex() >= minStrikeIndexToReceiveImpact;
        }
    
        public void incrementMinStrikeIndexToReceiveImpact() {
            ++minStrikeIndexToReceiveImpact;
        }
        
        public void initialize(BattleSprite battleSprite, BoxMetadata boxMetadata, Camera camera) {
            sprite = battleSprite;
            battleBoxInfo = boxMetadata;
            cam = camera;
        }
        
        public void updatePos() {
            pos.set(sprite.getPercentagePosition());
        }
        
        public BattleSprite getSprite() {
            return sprite;
        }
        
        //TODO: this stuff below
        //the screen edges are not the same as battleBox edges
        public float getScreenLeftEdgePosition() {
            return cam.getWorldCoordinates(new Vector2f(0, 0), cam.getViewToProjectionZ(Math.abs(cam.getLocation().z - sprite.getWorldTranslation().z))).x;
        }
    }
}
