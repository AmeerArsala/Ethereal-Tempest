/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.participant.visual;

import battle.environment.BoxMetadata;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import general.visual.DeserializedParticleEffect;
import general.visual.Sprite;

/**
 *
 * @author night
 * 
 * when deserializing this, the x and y vectors are percentages of the BattleBox
 */
public class BattleParticleEffect extends DeserializedParticleEffect {
    
    public BattleParticleEffect() {
        super();
    }
    
    public BattleParticleEffect(
        DualVector3F translation, DualVector3F angle, DualVector3F scale,
        String effectPath, Integer frames, Boolean useDestroyoflyer, Boolean useCustomDriver, Boolean usePointFollowingSpatialShape,
        Boolean loop, Float initialDelay, Float minimumDelay, Float maximumDelay, Integer maxInstances
    ) {
        super(
            translation, angle, scale,
            effectPath, frames, useDestroyoflyer, useCustomDriver, usePointFollowingSpatialShape,
            loop, initialDelay, minimumDelay, maximumDelay, maxInstances
        );
    }
    
    @Override
    public void createModelRootNode(Node modelRoot) {
        modelRoot = new ParticleRootNode("BattleParticleEffect class: modelRoot");
    }
    
    public ParticleRootNode getParticleRootNode() {
        return (ParticleRootNode)getModelRootNode();
    }
    
    public static class ParticleRootNode extends Node {
        private int xFacing = Sprite.FACING_LEFT;
        private BoxMetadata battleBoxInfo;
        
        public ParticleRootNode() {
            super();
        }
        
        public ParticleRootNode(String name) {
            super(name);
        }
        
        public BoxMetadata getBattleBoxInfo() {
            return battleBoxInfo;
        }
        
        public void setBattleBoxInfo(BoxMetadata info) {
            battleBoxInfo = info;
        }
        
        public void updateOrientation(BattleSprite enemy) {
            updateOrientation(enemy.getXFacing());
        }
        
        public void updateOrientation(int enemyXFacing) {
            xFacing = -enemyXFacing;
        }
        
        public Vector2f getPercentagePosition() {
            Vector3f localTranslation = getLocalTranslation();
            Vector2f unitVectorPos = new Vector2f();
        
            if (xFacing == Sprite.FACING_RIGHT) {
                //left side
                unitVectorPos.x = FastMath.abs(battleBoxInfo.percentDiffFromLeftEdge(localTranslation.x));
            } else { // xFacing == FACING_LEFT
                //right side
                unitVectorPos.x = FastMath.abs(battleBoxInfo.percentDiffFromRightEdge(localTranslation.x));
            }
        
            unitVectorPos.y = FastMath.abs(battleBoxInfo.percentDiffFromBottomEdge(localTranslation.y));
        
            return unitVectorPos;
        }
        
        public Vector3f getLocalAngle() {
            Quaternion rot = getLocalRotation();
            float[] angles = rot.toAngles(null);
        
            return new Vector3f(angles[0], angles[1], angles[2]);
        }
    }
}
