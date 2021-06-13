/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.ui.menu;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import general.visual.animation.Animation;
import general.visual.animation.TransitionSet;
import general.visual.animation.TransitionSet.TransitionState;
import general.visual.animation.TransitionSet.TransitionType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ameer Arsala
 * @param <O> menuOption type
 * @param <DATA> the type of the data holder (e.g. Conveyor)
 * 
 * <h1>HOW TO USE THESE MENUS</h1>
 *  <ul>
 *      <li> (0) Use a subclass of Menu or anonymous inner class, whatever suits the occasion, but subclass is better
 *      <li> (1) Use your preferred data holder type and construct with necessary parameters and override all necessary abstract methods
 *      <li> (2) Create a List of MenuOptions of the corresponding MenuOption type (For example, RadialMenu would use a List of RadialMenuOption)
 *      <li> (3) Fill the List of MenuOptions with the possible options the menu will be using; make sure to override all required abstract methods
 *      <li> (4) With the instantiated Menu, call menu.fullyInitialize(data, possibleOptions, assetManager), where possibleOptions is the List created in steps 2-3
 *      <li> (5) Attach the menu's Node. It can be accessed by calling menu.getNode()
 *  </ul>
 */
public abstract class Menu<O extends MenuOption<DATA>, DATA> {
    
    protected static class Settings { //extend this in a subclass
        private final boolean autoSelectOnOneOption;
        private final boolean hoverCurrentIndexWhenNothingElseIsHovered; //it will be "hovered" regardless, but if this is true, it will visibly show it rather than hide it and not visibly show any hover when nothing else is visibly hovered
        private final boolean transitionsOnSelectAndDeselectAreTheSameButReversed; //zoom in for forward = zoom out for backward
        private final Animation[] menuTransitionInOnSelect; //example: select an option and this menu pops up
        private final Animation[] menuTransitionOutOnSelect; //example: select an option and this menu disappears
        private final Animation[] menuTransitionInOnDeselect; //example: deselect and this menu pops up
        private final Animation[] menuTransitionOutOnDeselect; //example: deselect and this menu goes away
        private final Runnable closeMenuProtocol;
        
        protected Settings
        (
            boolean autoSelectOnOneOption, boolean hoverCurrentIndexWhenNothingElseIsHovered,
            boolean transitionsOnSelectAndDeselectAreTheSameButReversed,
            Animation[] menuTransitionInOnSelect, Animation[] menuTransitionOutOnSelect,
            Animation[] menuTransitionInOnDeselect, Animation[] menuTransitionOutOnDeselect,
            Runnable closeMenuProtocol
        ) {
            this.autoSelectOnOneOption = autoSelectOnOneOption;
            this.hoverCurrentIndexWhenNothingElseIsHovered = hoverCurrentIndexWhenNothingElseIsHovered;
            this.transitionsOnSelectAndDeselectAreTheSameButReversed = transitionsOnSelectAndDeselectAreTheSameButReversed;
            this.menuTransitionInOnSelect = menuTransitionInOnSelect;
            this.menuTransitionOutOnSelect = menuTransitionOutOnSelect;
            this.menuTransitionInOnDeselect = menuTransitionInOnDeselect;
            this.menuTransitionOutOnDeselect = menuTransitionOutOnDeselect;
            this.closeMenuProtocol = closeMenuProtocol;
        }
        
        public boolean isAutoSelectOnOneOption() { return autoSelectOnOneOption; }
        public boolean isHoverCurrentIndexWhenNothingElseIsHovered() { return hoverCurrentIndexWhenNothingElseIsHovered; }
        public Runnable getCloseMenuProtocol() { return closeMenuProtocol; }
        
        public TransitionSet createTransitionSet() {
            if (transitionsOnSelectAndDeselectAreTheSameButReversed) {
                return new TransitionSet(menuTransitionInOnSelect, menuTransitionOutOnSelect);
            }
            
            return new TransitionSet(menuTransitionInOnSelect, menuTransitionOutOnSelect, menuTransitionInOnDeselect, menuTransitionOutOnDeselect);
        }
        
        /*public static SettingsBuilder builder() {
            return new SettingsBuilder();
        }*/
        
        public static abstract class SettingsBuilder<B extends SettingsBuilder> {
            protected boolean autoSelectOnOneOption = false;
            protected boolean hoverCurrentIndexWhenNothingElseIsHovered = false;
            protected boolean transitionsOnSelectAndDeselectAreTheSameButReversed = false;
            protected Animation[] menuTransitionInOnSelect = { Animation.DissolveIn().setLength(0.00001f) };
            protected Animation[] menuTransitionOutOnSelect = { Animation.DissolveOut().setLength(0.00001f) };
            protected Animation[] menuTransitionInOnDeselect = { Animation.DissolveIn().setLength(0.00001f) };
            protected Animation[] menuTransitionOutOnDeselect = { Animation.DissolveOut().setLength(0.00001f) };
            protected Runnable closeMenuProtocol = () -> {};
            
            protected abstract B returnSelf();
            
            public B autoSelectOnOneOption(boolean autoSelectOnOneOption) {
                this.autoSelectOnOneOption = autoSelectOnOneOption;
                return returnSelf();
            }
            
            public B hoverCurrentIndexWhenNothingElseIsHovered(boolean hoverCurrentIndexWhenNothingElseIsHovered) {
                this.hoverCurrentIndexWhenNothingElseIsHovered = hoverCurrentIndexWhenNothingElseIsHovered;
                return returnSelf();
            }
            
            public B transitionsOnSelectAndDeselectAreTheSameButReversed(boolean transitionsOnSelectAndDeselectAreTheSameButReversed) {
                this.transitionsOnSelectAndDeselectAreTheSameButReversed = transitionsOnSelectAndDeselectAreTheSameButReversed;
                return returnSelf();
            }
            
            public B menuTransitionInOnSelect(Animation... menuTransitionInOnSelect) {
                this.menuTransitionInOnSelect = menuTransitionInOnSelect;
                return returnSelf();
            }
            
            public B menuTransitionOutOnSelect(Animation... menuTransitionOutOnSelect) {
                this.menuTransitionOutOnSelect = menuTransitionOutOnSelect;
                return returnSelf();
            }
            
            public B menuTransitionInOnDeselect(Animation... menuTransitionInOnDeselect) {
                this.menuTransitionInOnDeselect = menuTransitionInOnDeselect;
                return returnSelf();
            }
            
            public B menuTransitionOutOnDeselect(Animation... menuTransitionOutOnDeselect) {
                this.menuTransitionOutOnDeselect = menuTransitionOutOnDeselect;
                return returnSelf();
            }
            
            public B closeMenuProtocol(Runnable closeMenuProtocol) {
                this.closeMenuProtocol = closeMenuProtocol;
                return returnSelf();
            }
            
            public abstract <S extends Settings> S build();
        }
    }
    
    private final String menuName;
    private final boolean autoSelectOnOneOption;
    private final boolean hoverCurrentIndexWhenNothingElseIsHovered;
    protected final Runnable closeMenuProtocol;
    
    protected O parentOption = null;
    
    protected boolean active = false;
    
    protected final Node optionsNode = new Node();
    protected final ArrayList<O> availableOptions = new ArrayList<>();
    
    protected int currentIndex = 0;
    
    protected final TransitionSet menuTransitions;
    
    public Menu(String title, Settings params) { //use this in subclasses
        menuName = title;
        autoSelectOnOneOption = params.isAutoSelectOnOneOption();
        hoverCurrentIndexWhenNothingElseIsHovered = params.isHoverCurrentIndexWhenNothingElseIsHovered();
        closeMenuProtocol = params.getCloseMenuProtocol();
        menuTransitions = params.createTransitionSet();
        menuTransitions.initializeEvents(optionsNode);
    }
    
    public String getTitle() { return menuName; }
    public Node getNode() { return optionsNode; }
    
    public ArrayList<O> getAvailableOptions() { return availableOptions; }
    public O getCurrentOption() { return availableOptions.get(currentIndex); }
    public O getParentOption() { return parentOption; }
    
    public TransitionSet getMenuTransitions() { return menuTransitions; }
    
    public boolean autoSelectsOnSingleOption() { return autoSelectOnOneOption; }
    public boolean isActive() { return active; }
    public boolean isRootMenu() { return parentOption == null; }
    
    boolean hoverCurrentIndexWhenNothingElseIsHovered() { return hoverCurrentIndexWhenNothingElseIsHovered; }
    
    public void setActive(boolean activo) {
        active = activo;
    }
    
    public void setParentOption(O parent) {
        parentOption = parent;
    }
    
    protected abstract void initialize(DATA data); //implement in grandchild class
    protected abstract void onDetach(); //implement in grandchild class
    
    protected abstract void reset(); //implement in child class
    protected abstract void optionsInitializationIteration(O option, AssetManager assetManager, int i, int size); //implement in child class
    protected abstract void finishOptionsInitialization(AssetManager assetManager); //implement in child class
    
    protected abstract void updateCustom(float tpf); //implement in either immediate child or grandchild, but usually child class
    protected abstract void resolveInput(String name, boolean keyPressed, float tpf); //implement in either immediate child or grandchild, but usually child class
    
    protected abstract void incrementCurrentIndex(int num); //implement in child class
    protected abstract void onMoveYStart(); //implement in child class
    
    /**
     * 
     * @param data data holder such as Conveyor
     * @param possibleOptions list of MenuOptions representing possible options
     * @param assetManager assetManager (self-explanatory)
     * 
     * <h1>Method is split into 3 Phases:</h1>
     * 
     * <h3>Reset Phase</h3>
     *  <ul>
     *      <li> (1) detaches all children from optionsNode
     *      <li> (2) clears availableOptions
     *      <li> (3) currentIndex set to 0
     *      <li> (4) calls reset(), which is a subclass implementation for a custom reset
     *  </ul>
     * 
     * <h3>Transition Initialization Phase</h3>
     *  <ul>
     *      <li> (1) Initializes onFinishTransitioningIn
     *      <li> (2) Initializes onFinishTransitioningOut
     *  </ul>
     * 
     * <h3>Option Initialization Phase</h3>
     *  <b>Loops for all options</b>
     *      <ul>
     *          <li> (1) calls optionsInitializationIteration(menuOption, assetManager, index, numberOfOptions), which is a subclass iteration for option initialization (more customizability)
     *          <li> (2) initializes option through its own class by calling menuOption.initializeOption(assetManager)
     *          <li> (3) adds menuOption to availableOptions
     *      </ul>     
     * 
     * <h3>Finish Phase</h3>
     *  <ul>
     *      <li> (1) calls finishOptionsInitialization(), which is the onFinishOptionsInitialization for the subclass to implement
     *      <li> (2) calls initialize(data), which is a grandchild class implementation of data initialization
     *  </ul>
     */
    public final void fullyInitialize(DATA data, List<O> possibleOptions, AssetManager assetManager) {
        //Reset Phase
        optionsNode.detachAllChildren();
        availableOptions.clear();
        currentIndex = 0;
        reset(); //resets the stuff in the child class
        
        //Transition Initialization Phase
        menuTransitions.onFinish(TransitionState.TransitioningIn, () -> {
            //active = true;
            onFinishTransitioningIn();
        });
        
        menuTransitions.onFinish(TransitionState.TransitioningOut, () -> {
            detach();
            onFinishTransitioningOut();
        });
        
        //Option Initialization Phase
        for (int i = 0; i < possibleOptions.size(); ++i) {
            O option = possibleOptions.get(i);
            
            optionsInitializationIteration(option, assetManager, i, possibleOptions.size());
            option.initializeOption(i, assetManager, this, closeMenuProtocol, data);
            
            optionsNode.attachChild(option.getNode());
            availableOptions.add(option);
        }
        
        //Finish Phase
        finishOptionsInitialization(assetManager);
        initialize(data);
    }
    
    public final void update(float tpf) {
        if (menuTransitions.transitionInProgress()) {
            menuTransitions.update(tpf);
            onUpdateTransition(tpf);
            
            getCurrentlyActiveMenu().update(tpf);
        } else if (active) {
            updateCustom(tpf);
        
            availableOptions.forEach((option) -> {
                option.update(tpf);
            });
        } else {
            getCurrentlyActiveMenu().update(tpf);
        }
    }
    
    //OVERRIDE IF NEEDED
    protected void onStartTransitioningOut(TransitionType type) {}
    
    //OVERRIDE IF NEEDED
    protected void onStartTransitioningIn(TransitionType type, Node masterNode) {
        retachTo(masterNode);
    }
    
    //OVERRIDE IF NEEDED
    protected void onUpdateTransition(float tpf) {}
    
    //OVERRIDE IF NEEDED
    protected void onFinishTransitioningOut() {}
    
    //OVERRIDE IF NEEDED
    protected void onFinishTransitioningIn() {}
    
    public final void transitionIn(TransitionType type, Node masterNode) {
        menuTransitions.begin(TransitionState.TransitioningIn, type);
        onStartTransitioningIn(type, masterNode);
        active = true;
    }
    
    public final void transitionOut(TransitionType type) {
        menuTransitions.begin(TransitionState.TransitioningOut, type);
        onStartTransitioningOut(type);
        active = false;
    }
    
    public final void moveY(int num) {
        availableOptions.get(currentIndex).setHovered(false);
        findHoveredOption().setHovered(false);
        
        incrementCurrentIndex(num);
        onMoveYStart();
    }
    
    protected final void setAllOptionsUnhovered() {
        for (O option : availableOptions) {
            if (option.isHovered()) {
                option.setHovered(false);
            }
        }
    }
    
    //find hovered option, if no hovered option is detected, return the current option
    public final O findHoveredOption() {
        O hoveredOption = availableOptions.get(currentIndex);
        for (int i = 0, size = availableOptions.size(); i < size; ++i) {
            if (availableOptions.get(i).isHovered()) {
                hoveredOption = availableOptions.get(i);
                i = size;
            }
        }
        
        return hoveredOption;
    }
    
    public final void detach() {
        onDetach();
        attemptRemoveFromParent();
    }
    
    //OVERRIDE IF NEEDED
    protected void endTransitions() {
        menuTransitions.forceEndAll();
    }
    
    //OVERRIDE IF NEEDED
    protected void attemptRemoveFromParent() {
        optionsNode.removeFromParent();
    }
    
    //OVERRIDE IF NEEDED
    protected void retachTo(Node node) {
        node.attachChild(optionsNode);
    }
    
    //OVERRIDE IF NEEDED
    protected void onDeselect() {}
    
    //OVERRIDE IF NEEDED
    protected void onMouseEnteredOption() {}
    
    //OVERRIDE IF NEEDED
    protected void onMouseExitedOption() {}
    
    //OVERRIDE IF NEEDED
    protected boolean ignoreInputs() { return false; }
    
    private void goBackToPreviousMenu() {
        Node masterNode = getRootMenu().getNode().getParent();
        
        transitionOut(TransitionType.OnDeselect);
        parentOption.getParentMenu().transitionIn(TransitionType.OnDeselect, masterNode);
    }
    
    private void onInput(String name, boolean keyPressed, float tpf) {
        if (name.equals("select")) {
            availableOptions.get(currentIndex).select();
        } else if (name.equals("deselect")) {
            onDeselect();
        
            if (isRootMenu()) {
                closeMenuProtocol.run();
            } else {
                goBackToPreviousMenu();
            }
        }
        
        resolveInput(name, keyPressed, tpf);
    }
    
    //returns whether this menu was the one that called onInput; in other words, whether it was the active one at the time
    public final boolean catchInput(String name, boolean keyPressed, float tpf) {
        if (ignoreInputs()) {
            return active;
        }
        
        if (active) {
            onInput(name, keyPressed, tpf);
            return true;
        } else {
            getCurrentlyActiveMenu().catchInput(name, keyPressed, tpf);
            return false;
        }
    }
    
    public final Menu getCurrentlyActiveMenu() {
        if (active) {
            return this;
        }
        
        for (O option : availableOptions) {
            if (option.hasSubmenu()) {
                Menu trial = option.getSubmenu().getCurrentlyActiveMenu();
                if (trial != null) {
                    return trial;
                }
            }
        }
        
        return null;
    }
    
    public final Menu getRootMenu() {
        if (isRootMenu()) {
            return this;
        }
        
        return parentOption.getParentMenu().getRootMenu();
    }
}