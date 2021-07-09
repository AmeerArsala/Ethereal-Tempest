/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.gui.broad;

import general.math.FloatPair;
import general.math.function.CartesianFunction;
import general.math.function.ControlledMathFunction;
import general.math.function.MathFunction;
import general.procedure.ProcedureGroup;
import general.tools.GameTimer;
import general.visual.animation.VisualTransition;

/**
 *
 * @author night
 */
public abstract class RangedValue {
    protected final ProcedureGroup procedures = new ProcedureGroup();
    
    protected final int maxNumber;
    
    protected float percentFull;
    protected float percentOnLastUpdate = 0f;
    
    public RangedValue(float basePercent, int max) {
        percentFull = basePercent;
        maxNumber = max;
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
    
    public void setCurrentValue(float value) {
        setPercent(value / maxNumber);
    }
    
    public void proceedToValue(float value, float seconds) {
        proceedToPercent(value / maxNumber, seconds);
    }
    
    //for regular use
    public void proceedToPercent(float percent, float seconds) {
        GameTimer local = new GameTimer();
        CartesianFunction func = CartesianFunction.pointSlopeLine(percentFull, percent, 0, seconds);
        procedures.add((tpf) -> {
            percentFull = func.output(local.getTime());
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
            
            return transition.getTransitionProgress() == VisualTransition.Progress.Finished;
        });
    }
    
    protected abstract void updateVisuals();
    
    public final void updateState() {
        updateVisuals();
        percentOnLastUpdate = percentFull;
    }
    
    public void update(float tpf) {
        procedures.update(tpf);
        
        if (!procedures.isEmpty() && percentOnLastUpdate != percentFull) {
            updateState();
        }
    }
}
