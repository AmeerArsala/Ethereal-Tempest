/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.gui.broad;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import enginetools.math.SpatialOperator;
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
    
    protected final ProcedureGroup procedures = new ProcedureGroup();
    
    protected final String label;
    protected final Text2D text;
    protected final SpatialOperator textAnchor;
    
    protected final int maxNumber;
    
    protected float percentFull;
    private float percentOnLastUpdate = 0f;
    
    public ValueIndicator(String name, Text2D text2D, float basePercent, int max) {
        this(name, new Node("ValueIndicator: " + name), text2D, basePercent, max);
    }
    
    public ValueIndicator(String name, Node primeNode, Text2D text2D, float basePercent, int max) {
        label = name;
        node = primeNode;
        text = text2D;
        percentFull = basePercent;
        maxNumber = max;
        textAnchor = new SpatialOperator(text, text.getTextBounds(), new Vector3f(0.5f, 0.5f, 0));
    }
    
    protected abstract void updatePercentVisually();
    
    public void showText() {
        node.attachChild(text);
    }
    
    public String getLabel() {
        return label;
    }
    
    public Node getNode() { 
        return node; 
    }
    
    public Text2D getText() {
        return text;
    }
    
    public SpatialOperator getTextAnchor() {
        return textAnchor;
    }
    
    public boolean isQueueEmpty() {
        return procedures.isEmpty();
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
        text.setText(label + ": " + getCurrentNumber() + "/" + maxNumber);
    }
    
    public void updateVisuals() {
        updateText();
        updatePercentVisually();
        percentOnLastUpdate = percentFull;
    }
    
    public void proceedToValue(float value, float seconds) {
        ValueIndicator.this.proceedToPercent(value / maxNumber, seconds);
    }
    
    //for regular use
    public void proceedToPercent(float percent, float seconds) {
        GameTimer local = new GameTimer();
        float slope = (percent - percentFull) / seconds; //seconds = (seconds - 0f)
        procedures.add((tpf) -> {
            percentFull += slope;
            
            local.update(tpf);
            
            if (local.getTime() >= seconds) {
                setPercent(percent); 
                return true;
            }
            
            return false;
        });
    }
    
    //use this if you want it to follow a relative curve
    public void proceedToPercent(ControlledMathFunction percentCurve, FloatPair domain) {
        proceedToPercent((MathFunction)percentCurve.derivative(), domain);
    }
    
    //use this if you have a velocity function
    public void proceedToPercent(MathFunction velocity, FloatPair domain) {
        GameTimer local = new GameTimer();
        float seconds = domain.b - domain.a;
        
        procedures.add((tpf) -> {
            percentFull += velocity.output(domain.a + local.getTime());
            
            local.update(tpf);
            
            if (local.getTime() >= seconds) {
                setPercent(percentFull + velocity.output(domain.b));
                return true;
            }
            
            return false;
        });
    }
    
    public void addTransitionToGroup(VisualTransition transition) {
        transition.beginTransitions();
        procedures.add((tpf) -> {
            transition.update(tpf);
            
            return transition.getTransitionProgress() == Progress.Finished;
        });
    }
    
    public void update(float tpf) {
        procedures.update(tpf);
        
        if (!procedures.isEmpty() && percentOnLastUpdate != percentFull) {
            updateVisuals();
        }
    }
    
    
}
