/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.environment;

import com.jme3.math.Vector2f;
import general.utils.helpers.MathUtils;

/**
 *
 * @author night
 */
public class BoxMetadata {
    private final Vector2f boxDimensions;
    private final float leftEdgePositionPercent, rightEdgePositionPercent, topEdgePositionPercent, bottomEdgePositionPercent;
    
    public BoxMetadata(Vector2f boxDimensions, float leftEdgePositionPercent, float rightEdgePositionPercent, float topEdgePositionPercent, float bottomEdgePositionPercent) {
        this.boxDimensions = boxDimensions;
        this.leftEdgePositionPercent = leftEdgePositionPercent;
        this.rightEdgePositionPercent = rightEdgePositionPercent;
        this.topEdgePositionPercent = topEdgePositionPercent;
        this.bottomEdgePositionPercent = bottomEdgePositionPercent;
    }
    
    public Vector2f getBoxDimensions() { return boxDimensions; }
    
    public float getLeftEdgePositionPercent() { return leftEdgePositionPercent; }
    public float getRightEdgePositionPercent() { return rightEdgePositionPercent; }
    public float getTopEdgePositionPercent() { return topEdgePositionPercent; }
    public float getBottomEdgePositionPercent() { return bottomEdgePositionPercent; }
    
    public float getLeftEdgePosition() {
        return leftEdgePositionPercent * boxDimensions.x;
    }
    
    public float getRightEdgePosition() {
        return rightEdgePositionPercent * boxDimensions.x;
    }
    
    public float getTopEdgePosition() {
        return topEdgePositionPercent * boxDimensions.y;
    }
    
    public float getBottomEdgePosition() {
        return bottomEdgePositionPercent * boxDimensions.y;
    }
    
    public float getActualLeftEdgePosition() {
        return leftEdgePositionPercent * horizontalLength();
    }
    
    public float getActualRightEdgePosition() {
        return rightEdgePositionPercent * horizontalLength();
    }
    
    public float getActualTopEdgePosition() {
        return topEdgePositionPercent * verticalLength();
    }
    
    public float getActualBottomEdgePosition() {
        return bottomEdgePositionPercent * verticalLength();
    }
    
    public float horizontalLengthPercentage() {
        return rightEdgePositionPercent - leftEdgePositionPercent;
    }
    
    public float verticalLengthPercentage() {
        return topEdgePositionPercent - bottomEdgePositionPercent;
    }
    
    public float horizontalLength() {
        return (rightEdgePositionPercent - leftEdgePositionPercent) * boxDimensions.x;
    }
    
    public float verticalLength() {
        return (topEdgePositionPercent - bottomEdgePositionPercent) * boxDimensions.y;
    }
    
    public float percentDiffFromLeftEdge(float localTranslationX) {
        return percentDiffFromHorizontalEdge(leftEdgePositionPercent, localTranslationX);
    }
    
    public float percentDiffFromRightEdge(float localTranslationX) {
        return percentDiffFromHorizontalEdge(rightEdgePositionPercent, localTranslationX);
    }
    
    public float percentDiffFromTopEdge(float localTranslationY) {
        return percentDiffFromVerticalEdge(topEdgePositionPercent, localTranslationY);
    }
    
    public float percentDiffFromBottomEdge(float localTranslationY) {
        return percentDiffFromVerticalEdge(bottomEdgePositionPercent, localTranslationY);
    }
    
    private float percentDiffFromHorizontalEdge(float posPercent, float localTranslationX) {
        return MathUtils.percentDiffFromEdge(
            posPercent, 
            horizontalLength(), //used to be boxDimensions.x
            localTranslationX
        );
    }
    
    private float percentDiffFromVerticalEdge(float posPercent, float localTranslationY) {
        return MathUtils.percentDiffFromEdge(
            posPercent, 
            verticalLength(), //used to be boxDimensions.y
            localTranslationY
        );
    }
}
