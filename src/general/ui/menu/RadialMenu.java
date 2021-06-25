/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.ui.menu;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import enginetools.MaterialCreator;
import general.math.function.ParametricFunction;
import general.visual.animation.Animation;
import general.visual.animation.TransitionSet.TransitionType;

/**
 *
 * @author night
 * @param <DATA> the type of the data holder (e.g. Conveyor)
 */
public abstract class RadialMenu<DATA> extends Menu<RadialMenuOption<DATA>, DATA> {
    
    public static class Settings extends Menu.Settings {
        private final boolean directionsInverted;
        private final ParametricFunction idleMovementFunction; //do NOT pass in null; instead, pass in ParametricFunction.ZERO for no movement
        private final boolean idleMotionOnlyOnHoveredOption; //if not, then it applies to all
        
        private Settings
        (
            boolean autoSelectOnOneOption, boolean hoverCurrentIndexWhenNothingElseIsHovered,
            boolean transitionsOnSelectAndDeselectAreTheSameButReversed,
            Animation[] menuTransitionInOnSelect, Animation[] menuTransitionOutOnSelect,
            Animation[] menuTransitionInOnDeselect, Animation[] menuTransitionOutOnDeselect,
            Runnable closeMenuProtocol,
            boolean directionsInverted,
            ParametricFunction idleMovementFunction, boolean idleMotionOnlyOnHoveredOption
            
        ) 
        {
            super(autoSelectOnOneOption, hoverCurrentIndexWhenNothingElseIsHovered, transitionsOnSelectAndDeselectAreTheSameButReversed, menuTransitionInOnSelect, menuTransitionOutOnSelect, menuTransitionInOnDeselect, menuTransitionOutOnDeselect, closeMenuProtocol);
            this.directionsInverted = directionsInverted;
            this.idleMovementFunction = idleMovementFunction;
            
            if (idleMovementFunction == ParametricFunction.ZERO) {
                this.idleMotionOnlyOnHoveredOption = false;
            } else {
                this.idleMotionOnlyOnHoveredOption = idleMotionOnlyOnHoveredOption;
            }
        }
        
        public boolean isDirectionsInverted() { return directionsInverted; }
        public ParametricFunction getIdleMovementFunction() { return idleMovementFunction; }
        public boolean isIdleMotionOnlyOnHoveredOption() { return idleMotionOnlyOnHoveredOption; }
        
        public static SettingsBuilder builder() {
            return new SettingsBuilder();
        }
        
        public static class SettingsBuilder extends Menu.Settings.SettingsBuilder<SettingsBuilder> {
            private boolean directionsInverted = false;
            private ParametricFunction idleMovementFunction = ParametricFunction.ZERO;
            private boolean idleMotionOnlyOnHoveredOption = false;
            
            private SettingsBuilder() {}
            
            @Override
            protected SettingsBuilder returnSelf() {
                return this;
            }
            
            public SettingsBuilder directionsInverted(boolean directionsInverted) {
                this.directionsInverted = directionsInverted;
                return this;
            }
            
            public SettingsBuilder idleMovementFunction(ParametricFunction idleMovementFunction) {
                this.idleMovementFunction = idleMovementFunction;
                return this;
            }
            
            public SettingsBuilder idleMotionOnlyOnHoveredOption(boolean idleMotionOnlyOnHoveredOption) {
                this.idleMotionOnlyOnHoveredOption = idleMotionOnlyOnHoveredOption;
                return this;
            }
            
            @Override
            public Settings build() {
                return new Settings(autoSelectOnOneOption, hoverCurrentIndexWhenNothingElseIsHovered, transitionsOnSelectAndDeselectAreTheSameButReversed, menuTransitionInOnSelect, menuTransitionOutOnSelect, menuTransitionInOnDeselect, menuTransitionOutOnDeselect, closeMenuProtocol, directionsInverted, idleMovementFunction, idleMotionOnlyOnHoveredOption);
            }
        }
    }
    
    
    private final ParametricFunction idleGraph; //this will determine its actual extent, not the direction it heads
    private final boolean idleMovementOnlyOnHoveredOption;
    
    private PointerLine line;
    
    private int deltaIncrement = 1; //will move once per up-down input; is 1 by default
    
    private float speed = 0.5f;
    private int amount = 1;
    
    private float amountRotated = 0;
    
    private float dTheta;
    private boolean rotating = false;
    
    private final boolean usesDefaultOptionTransitions; //for menuOptions
    private Animation[] onSelectedOptionTransitionsForHoveredOption = null;
    private Animation[] onSelectedOptionTransitionsForNotHoveredOptions = null;

    private RadialMenu(String name, RadialMenu.Settings settings, boolean useDefaultTransitions) { 
        super(name, settings);
        setDirectionsInverted(settings.isDirectionsInverted());
        idleGraph = settings.getIdleMovementFunction();
        idleMovementOnlyOnHoveredOption = settings.isIdleMotionOnlyOnHoveredOption();
        usesDefaultOptionTransitions = useDefaultTransitions;
    }
    
    public RadialMenu(String name, RadialMenu.Settings settings) {
        this(name, settings, true);
    }
    
    public RadialMenu(String name, Animation[] onSelectedTransitions, Animation[] onNotSelectedTransitions, RadialMenu.Settings settings) {
        this(name, settings, false);
        onSelectedOptionTransitionsForHoveredOption = onSelectedTransitions;
        onSelectedOptionTransitionsForNotHoveredOptions = onNotSelectedTransitions;
    }
    
    @Override
    protected void reset() {
        Quaternion rot = new Quaternion();
        rot.fromAngles(0, 0, 0);
        optionsNode.setLocalRotation(rot);
        amountRotated = 0;
    }
    
    @Override
    protected void optionsInitializationIteration(RadialMenuOption<DATA> option, AssetManager assetManager, int i, int size) {
        dTheta = FastMath.TWO_PI / size;
        option.initializeAssets(assetManager, idleGraph.newInstance(), idleMovementOnlyOnHoveredOption, FastMath.PI - (i * dTheta), size);
    }
    
    @Override
    protected void finishOptionsInitialization(AssetManager assetManager) {
        if (usesDefaultOptionTransitions) {
            onSelectedOptionTransitionsForHoveredOption = new Animation[] { Animation.MoveDirection2D(FastMath.PI, RadialMenuOption.SIZE), Animation.ZoomIn().setInitialAndEndVals(1, 2), Animation.DissolveIn().setLength(0.25f) };
            onSelectedOptionTransitionsForNotHoveredOptions = new Animation[] { Animation.DissolveOut() };
        }
        
        line = new PointerLine((RadialMenuOption.SIZE / 2f) + RadialMenuOption.calculateRadius(availableOptions.size()), 7.5f, assetManager); //7.5f thickness
        line.move(0, 0.5f * (RadialMenuOption.SIZE - RadialMenuOption.icoSizeDim), 0);
        
        availableOptions.get(0).setHovered(true);
    }
    
    public int getCurrentIndex() { return currentIndex; }
    
    public ParametricFunction getIdleMovementFunction() { return idleGraph; }
    
    public float getSpeed() { return speed; }
    public boolean isRotating() { return rotating; }
    
    public int getDeltaIncrement() { return deltaIncrement; }
    public boolean directionsAreInverted() { return deltaIncrement < 0; }
    
    public final void setDirectionsInverted(boolean inverted) {
        if (inverted) {
            deltaIncrement = -Math.abs(deltaIncrement);
        } else {
            deltaIncrement = Math.abs(deltaIncrement);
        }
    }
    
    public void setDeltaIncrement(int dInc) { 
        deltaIncrement = dInc; 
    }
    
    public void setSpeed(float spd) {
        speed = spd;
    }
    
    @Override
    protected void incrementCurrentIndex(int num) {
        amount = num; //this decides the angular direction of the rotation (positive (up arrow) = counter clockwise, negative (down arrow) = clockwise)
        
        if (currentIndex + num < 0) { //this is gonna turn out as -1 every time
            currentIndex = availableOptions.size() - 1;
        } else { //currentIndex + num >= 0
            currentIndex = Math.abs(currentIndex + num) % availableOptions.size();
        }
        
        if (usesDefaultOptionTransitions) {
            onSelectedOptionTransitionsForHoveredOption[0] = Animation.MoveDirection2D(availableOptions.get(currentIndex).getAngle(), RadialMenuOption.SIZE);
        }
    }
    
    public void rotate(float angle) {
        optionsNode.rotate(0, 0, angle);
        availableOptions.forEach((option) -> { //balance the icon rotations out so they don't look tilted
            option.rotate(-angle);
        });
        
        //line.rotate(-angle);
    }
    
    private void adjustRotation() {
        Quaternion rotation = new Quaternion();
        rotation.fromAngles(0, 0, amountRotated);
        optionsNode.setLocalRotation(rotation);
        
        Quaternion optionRotation = new Quaternion();
        optionRotation.fromAngles(0, 0, -amountRotated);
        availableOptions.forEach((option) -> {
            option.getNode().setLocalRotation(optionRotation);
        });
    }
    
    private float accumulatedFactor = 0f;
    
    @Override
    public void updateCustom(float tpf) {
        if (rotating) { // is rotating?
            float factor = tpf * 60f < dTheta ? tpf * 60f : dTheta;
            
            rotate(amount * speed * factor);
            
            if (speed * accumulatedFactor >= dTheta) {
                amountRotated = (amountRotated + (dTheta * amount)) % FastMath.TWO_PI; //calculates equivalent reference angle
                adjustRotation();
                
                rotating = false;
                accumulatedFactor = 0f;
                availableOptions.get(currentIndex).setHovered(true);
            }
            
            accumulatedFactor += factor;
        }
    }
    
    @Override
    protected void onStartTransitioningOut(TransitionType type) {
        for (int i = 0; type == TransitionType.OnSelect && i < availableOptions.size(); ++i) {
            if (i == currentIndex) {
                availableOptions.get(i).getTransitionEvent().beginTransitions(onSelectedOptionTransitionsForHoveredOption);
            } else {
                availableOptions.get(i).getTransitionEvent().beginTransitions(onSelectedOptionTransitionsForNotHoveredOptions);
            }
        }
    }
    
    @Override
    protected void onStartTransitioningIn(TransitionType type, Node masterNode) {
        super.onStartTransitioningIn(type, masterNode);
        
        if (type == TransitionType.OnDeselect) {
            Animation[] onDeselectedTransitionsForHoveredOption = new Animation[onSelectedOptionTransitionsForHoveredOption.length];
            for (int i = 0; i < onDeselectedTransitionsForHoveredOption.length; ++i) { 
                onDeselectedTransitionsForHoveredOption[i] = Animation.reverse(onSelectedOptionTransitionsForHoveredOption[i]);
            }
            
            Animation[] onDeselectedTransitionsForNotHoveredOptions = new Animation[onSelectedOptionTransitionsForNotHoveredOptions.length];
            for (int i = 0; i < onDeselectedTransitionsForNotHoveredOptions.length; ++i) { 
                onDeselectedTransitionsForNotHoveredOptions[i] = Animation.reverse(onSelectedOptionTransitionsForNotHoveredOptions[i]);
            }
            
            for (int i = 0; i < availableOptions.size(); ++i) {
                if (i == currentIndex) {
                    availableOptions.get(i).getTransitionEvent().beginTransitions(onDeselectedTransitionsForHoveredOption);
                } else {
                    availableOptions.get(i).getTransitionEvent().beginTransitions(onDeselectedTransitionsForNotHoveredOptions);
                }
            }
        }
    }
    
    @Override
    protected void onUpdateTransition(float tpf) {
        availableOptions.forEach((option) -> { option.getTransitionEvent().update(tpf); });
    }
    
    @Override
    protected void onMoveYStart() {
        rotating = true;
        accumulatedFactor = 0f;
    }
    
    public void jumpSideways(int num) {
        moveY(num * availableOptions.size() / 2);
    }
    
    //OVERRIDE IF NECESSARY to add more inputs, and call super.resolveInput(name, keyPressed, tpf);
    @Override
    protected void resolveInput(String name, boolean keyPressed, float tpf) {
        if (name.equals("move up")) {
            moveY(deltaIncrement);
        }
        if (name.equals("move down")) {
            moveY(-deltaIncrement);
        }
        if (name.equals("move left")) {
            jumpSideways(-deltaIncrement);
        }
        if (name.equals("move right")) {
            jumpSideways(deltaIncrement);
        }
    }
    
    @Override
    protected boolean ignoreInputs() {
        return rotating;
    }
    
    @Override
    protected void onMouseEnteredOption() {
        /*optionsNode.attachChild(line);
        line.rotateTo(findHoveredOption().getAngle() + FastMath.PI);*/
    }
    
    @Override 
    protected void onMouseExitedOption() {
        //optionsNode.detachChild(line);
    }
}

class PointerLine extends Node {
    private final Geometry rectangle;
    private final float width, length;
    
    private float standardAngle = 0;
    
    public PointerLine(float radius, float thickness, AssetManager assetManager) {
        width = radius;
        length = thickness;
        
        rectangle = new Geometry("rectangle", new Quad(width, length));
        rectangle.setMaterial(new Material(assetManager, MaterialCreator.UNSHADED));
        rectangle.getMaterial().setColor("Color", ColorRGBA.White);
        attachChild(rectangle);
    }
    
    public void setLocalRotation(float radians) {
        Quaternion rotation = new Quaternion();
        rotation.fromAngles(0, 0, radians);
        rectangle.setLocalRotation(rotation);
    }
    
    public Spatial rotate(float zAngle) {
        standardAngle += zAngle;
        return super.rotate(0, 0, zAngle);
    }
    
    public Spatial rotateTo(float angle) {
        return rotate(standardAngle + angle);
    }
    
    public float getStandardAngle() { return standardAngle; }
}
