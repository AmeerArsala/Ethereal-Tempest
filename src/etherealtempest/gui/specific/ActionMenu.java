/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.gui.specific;

import etherealtempest.info.Conveyor;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.simsilica.lemur.Panel;
import etherealtempest.fsm.FSM;
import etherealtempest.fsm.FSM.MapFlowState;
import etherealtempest.fsm.MasterFsmState;
import general.math.FloatPair;
import general.math.function.MathFunction;
import general.math.function.ParametricFunction;
import general.math.function.RandomizedPiecewiseFunction;
import general.ui.menu.RadialMenu;

/**
 *
 * @author night
 */
public class ActionMenu {
    private static final FloatPair SHAKE_DOMAIN = new FloatPair(0f, Float.POSITIVE_INFINITY); // Domain: [0f, infinity)
    private static final FloatPair SHAKE_RANGE = new FloatPair(-2f, 2f); // Range: [-2f, 2f] 
    public static final RandomizedPiecewiseFunction SHAKE_PARAM = new RandomizedPiecewiseFunction(SHAKE_DOMAIN, SHAKE_RANGE, MathFunction.CONSTANT(1f), false);  //1-second partitions
    public static final RandomizedPiecewiseFunction SHAKE_PARAM2 = new RandomizedPiecewiseFunction(SHAKE_DOMAIN, SHAKE_RANGE, MathFunction.CONSTANT(1f), false); //1-second partitions
    public static final ParametricFunction DEFAULT_SHAKING = new ParametricFunction(SHAKE_PARAM, SHAKE_PARAM2).setInstanceGenType(ParametricFunction.FRESH);
    
    
    private final Node menuNode = new Node("post move menu");
    private final Runnable onCloseMenu;
    
    private Panel geoBackdrop;
    
    private boolean isOpen = false;
    
    private final RadialMenu<Conveyor> rootMenu = new RadialMenu<Conveyor>
    (
        "Action", 
        RadialMenu.Settings.builder()
            .autoSelectOnOneOption(false)
            .hoverCurrentIndexWhenNothingElseIsHovered(true)
            .directionsInverted(false)
            .idleMovementFunction(DEFAULT_SHAKING)
            .idleMotionOnlyOnHoveredOption(false)
            .closeMenuProtocol(() -> {
                closeMenu();
            })
            .build()
    )
    {
        private Conveyor conv;
        
        @Override
        protected void initialize(Conveyor data) { //this is where you put the text, etc.
            conv = data;
        }
        
        @Override
        protected void onDeselect() {
            conv.getCursor().goBackFromMenu();
        }

        @Override
        public void onDetach() {
            
        }
    };
    
    public ActionMenu(AssetManager assetManager) {
        this(assetManager, () -> {});
    }
    
    public ActionMenu(AssetManager assetManager, Runnable onCloseMenu) {
        this.onCloseMenu = onCloseMenu;
        menuNode.attachChild(rootMenu.getNode());
        rootMenu.setMenuRootNode(menuNode);
        
        //do stuff with the panel here
        
        setOpen(false);
    }
    
    public Node getNode() { 
        return menuNode; 
    }
    
    public boolean isOpen() { 
        return isOpen; 
    }
    
    public final void setOpen(boolean open) {
        isOpen = open;
    }
    
    protected void closeMenu() {
        setOpen(false);
        menuNode.removeFromParent();
        onCloseMenu.run();
    }
    
    public void initialize(Conveyor C) {
        rootMenu.fullyInitialize(C, C.getUnit().determineOptions(C).getAvailableOptions(), C.getAssetManager());
        rootMenu.setActive(true);
    }
    
    public void update(float tpf) {
        if (isOpen) {
            rootMenu.update(tpf);
        }
    }
    
    public MasterFsmState resolveInput(String name, boolean keyPressed, float tpf) {
        boolean rootIsActive = rootMenu.catchInput(name, keyPressed, tpf);
        
        if (rootIsActive &&
            ((name.equals("select") && rootMenu.getCurrentOption().getName().equals("Standby")) || name.equals("deselect"))) //these cancel out the menu
        {
            return new MasterFsmState(MapFlowState.MapDefault);
        }
        
        return null;
    }
}
