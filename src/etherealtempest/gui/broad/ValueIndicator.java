/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.gui.broad;

import com.jme3.scene.Node;
import enginetools.math.SpatialOperator;
import general.tools.GameTimer;
import general.procedure.ProcedureGroup;
import general.math.FloatPair;
import general.math.function.CartesianFunction;
import general.math.function.ControlledMathFunction;
import general.math.function.MathFunction;
import general.ui.text.Text2D;
import general.visual.animation.VisualTransition;
import general.visual.animation.VisualTransition.Progress;

/**
 *
 * @author night
 */
public abstract class ValueIndicator extends RangedValue {
    protected final Node node;
    
    protected final String label;
    protected final Text2D text;
    protected final SpatialOperator textAnchor;
    
    public ValueIndicator(String name, Text2D text2D, float basePercent, int max) {
        this(name, new Node("ValueIndicator: " + name), text2D, basePercent, max);
    }
    
    public ValueIndicator(String name, Node primeNode, Text2D text2D, float basePercent, int max) {
        super(basePercent, max);
        label = name;
        node = primeNode;
        text = text2D;
        textAnchor = text.createSpatialOperator(0.5f, 0.5f);
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
    
    protected void updateText() {
        text.setText(label + ": " + getCurrentNumber() + "/" + maxNumber);
    }
    
    @Override
    protected void updateVisuals() {
        updateText();
        updatePercentVisually();
    }
}
