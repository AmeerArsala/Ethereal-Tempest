/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.gui;

import com.jme3.scene.Node;
import general.tools.GameTimer;
import general.procedure.ProcedureGroup;
import general.math.FloatPair;
import general.math.function.ControlledMathFunction;
import general.math.function.MathFunction;
import general.ui.text.Text2D;
import general.visual.animation.VisualTransition;
import general.visual.animation.VisualTransition.Progress;

/**
 *
 * @author night
 */
public abstract class ValueIndicator {
    protected final Node node;
    
    protected final ProcedureGroup queue = new ProcedureGroup();
    
    protected final Text2D text;
    protected final int maxNumber;
    
    protected float percentFull;
    private float percentOnLastUpdate = 0f;
    
    public ValueIndicator(Text2D label, float basePercent, int max) {
        node = new Node();
        text = label;
        percentFull = basePercent;
        maxNumber = max;
        node.attachChild(text);
    }
    
    public ValueIndicator(Node primeNode, Text2D label, float basePercent, int max) {
        node = primeNode;
        text = label;
        percentFull = basePercent;
        maxNumber = max;
        node.attachChild(text);
    }
    
    protected abstract void updatePercentVisually();
    
    public Node getNode() { 
        return node; 
    }
    
    public Text2D getText() {
        return text;
    }
    
    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }
    
    public float getPercent() {
        return percentFull;
    }
    
    public int getMaxNumber() {
        return maxNumber;
    }
    
    public int getCurrentNumber() {
        return (int)(percentFull * maxNumber);
    }
    
    public void setPercent(float percent) {
        percentFull = percent;
        updateVisuals();
    }
    
    protected void updateText() {
        text.setText(getCurrentNumber() + "/" + maxNumber);
    }
    
    public void updateVisuals() {
        updateText();
        updatePercentVisually();
        percentOnLastUpdate = percentFull;
    }
    
    public void queueToValue(float value, float seconds) {
        queueToPercent(value / maxNumber, seconds);
    }
    
    //for regular use
    public void queueToPercent(float percent, float seconds) {
        GameTimer local = new GameTimer();
        float slope = (percent - percentFull) / seconds; //seconds = (seconds - 0f)
        queue.add((tpf) -> {
            percentFull += slope;
            
            local.update(tpf);
            
            if (local.getTime() >= seconds) {
                setPercent(percentFull + slope); 
                return true;
            }
            
            return false;
        });
    }
    
    //use this if you want it to follow a relative curve
    public void queueToPercent(ControlledMathFunction percentCurve, FloatPair domain) {
        queueToPercent((MathFunction)percentCurve.derivative(), domain);
    }
    
    //use this if you have a velocity function
    public void queueToPercent(MathFunction velocity, FloatPair domain) {
        GameTimer local = new GameTimer();
        float seconds = domain.b - domain.a;
        
        queue.add((tpf) -> {
            percentFull += velocity.output(domain.a + local.getTime());
            
            local.update(tpf);
            
            if (local.getTime() >= seconds) {
                setPercent(percentFull + velocity.output(domain.b));
                return true;
            }
            
            return false;
        });
    }
    
    public void addTransitionToQueue(VisualTransition transition) {
        transition.beginTransitions();
        queue.add((tpf) -> {
            transition.update(tpf);
            
            return transition.getTransitionProgress() == Progress.Finished;
        });
    } 
    
    public void update(float tpf) {
        queue.update(tpf);
        
        if (!queue.isEmpty() && percentOnLastUpdate != percentFull) {
            updateVisuals();
        }
    }
    
    
}
