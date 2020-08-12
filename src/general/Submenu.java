/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

import battle.Conveyer;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.component.TbtQuadBackgroundComponent;
import general.Submenu.TransitionState;
import general.Submenu.TransitionType;
import general.VisualTransition.Progress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author night
 */
public abstract class Submenu extends Container {
    protected boolean active = false;
    protected Texture currentTexture;
    protected Conveyer data;
    protected TransitionState state = TransitionState.Standby;
    protected int currentIndex = 0;
    protected VisualTransition transitionEvent;
    protected boolean autoSelectOnOneOption = true;
    protected List<SubmenuOption> menuOptions;
    
    private SubmenuOption parentOption;
    
    public boolean usingTransition = true;
    
    public Submenu() {
        super();
    }
    
    public Submenu(BoxLayout Box) {
        super(Box);
        transitionEvent = new VisualTransition(this);
    }
    
    public enum TransitionState {
        Standby,
        TransitioningOut,
        TransitioningIn,
    }
    
    public enum TransitionType {
        Forward,
        Backward,
        None
    }
    
    public abstract void retrieveData();
    public abstract void initialize();
    public abstract List<SubmenuOption> options();
    public abstract void resolveInput(String name, float tpf);
    public abstract void update();
    public abstract void reset();
    
    public Submenu initializeAll(Conveyer C) {
        fullyInitialize(C);
        return this;
    }
    
    public Submenu setAutoSelect(boolean op) {
        autoSelectOnOneOption = op;
        return this;
    }
    
    public void fullyInitialize(Conveyer C) {
        partialInitialize(C);
        lightInitialize();
    }
    
    public void partialInitialize(Conveyer C) {
        detachAllChildren();
        clearChildren();
        
        setData(C);
        menuOptions = new ArrayList<>();
        
        for (SubmenuOption so : options()) {
            so.initializeAll(C);
            addChild(so);
            menuOptions.add(so);
        }
        setAlpha(0f);
        
        initializeBounds();
    }
    
    public void lightInitialize() {
        initialize();
        transitionEvent = new VisualTransition(this);
        
        currentIndex = 0;
        for (SubmenuOption so : menuOptions) {
            ((QuadBackgroundComponent)so.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
        }
        ((QuadBackgroundComponent)menuOptions.get(0).getBackground()).setColor(new ColorRGBA(1, 1, 1, 1));
        
        transitionEvent.setResetProtocol(new ResetProtocol() {
            @Override
            public void execute() {
                if (state == TransitionState.TransitioningOut) {
                    reset();
                }
                state = TransitionState.Standby;
            }
        });
    }
    
    public void setData(Conveyer C) {
        data = C;
        retrieveData();
    }
    
    public List<SubmenuOption> getSubmenuOptions(Conveyer C) {
        setData(C);
        return options();
    }
    
    public void catchInput(String name, float tpf, Conveyer C) {
        setData(C);
        if (active) {
            resolveInput(name, tpf);
        } else {
            catchChildInput(name, tpf, C);
        }
    }
    
    public void catchChildInput(String name, float tpf, Conveyer C) {
        if (menuOptions != null) {
            for (SubmenuOption so : menuOptions) {
                if (so.hasSubmenu() && so.isSubmenuActive()) {
                    so.catchSubmenuInput(name, tpf, C);
                    return;
                }
            }
        }
    }
    
    public void setActive(boolean visible) {
        active = visible;
        /*if (visible) {
            setAlpha(1);
        } else {
            setAlpha(0);
        }*/
    }
    
    public boolean isActive() { //use this as a condition to turn off moving while a submenu is up
        return active;
    }
    
    private List<Submenu> getDescendantSubmenus() {
        List<Submenu> descendants = new ArrayList<>();
        if (menuOptions != null) {
            for (SubmenuOption so : menuOptions) {
                if (so.hasSubmenu()) {
                    descendants.add(so.child);
                }
            }
        }
        return descendants;
    }
    
    public void updateDefault() {
        if (active) {
            if (state == TransitionState.Standby) {
                if (options().size() > 1 || !autoSelectOnOneOption) {
                    update();
                } else if (options().size() == 1 && autoSelectOnOneOption) {
                    options().get(0).selectOption(data);
                }
            } else {
                if (transitionEvent != null && transitionEvent.getTransitionProgress() != Progress.Finished) {
                    transitionEvent.updateTransitions();
                }
            }
        } else if (transitionEvent != null && transitionEvent.getTransitionProgress() == Progress.Progressing) {
            transitionEvent.updateTransitions();
        }
        
        for (Submenu sub : getDescendantSubmenus()) {
            sub.updateDefault();
        }
    }
    
    public List<Transition> determineTransitions() {
        List<Transition> applies = new ArrayList<>();
        if (transitionEvent != null) {
            Transition dissolve, zoom;
            if (state == TransitionState.TransitioningIn) {
                dissolve = VisualTransition.DissolveIn().setLength(0.15f).setStartingIndexScale(0f);
                applies.add(dissolve);
                if (transitionEvent.getTransitionType() == TransitionType.Forward) {
                    zoom = VisualTransition.ZoomIn().setStartingIndexScale(0f).setLength(0.15f);
                    applies.add(zoom);
                } else if (transitionEvent.getTransitionType() == TransitionType.Backward) {
                    zoom = VisualTransition.ZoomOut().setStartingIndexScale(2f).setLength(0.15f);
                    applies.add(zoom);
                }
            } else if (state == TransitionState.TransitioningOut) {
                dissolve = VisualTransition.DissolveOut().setLength(0.15f).setStartingIndexScale(1f);
                applies.add(dissolve);
                if (transitionEvent.getTransitionType() == TransitionType.Forward) {
                    zoom = VisualTransition.ZoomIn().setStartingIndexScale(1f).setLength(0.15f);
                    applies.add(zoom);
                } else if (transitionEvent.getTransitionType() == TransitionType.Backward) {
                    zoom = VisualTransition.ZoomOut().setStartingIndexScale(1f).setLength(0.15f);
                    applies.add(zoom);
                }
            }
            return applies;
        }
        return null;
    }
    
    public <T> List<T> modifyList(List<T> list, T replacement, int index) {
        list.set(index, replacement);
        return list;
    }
    
    public TransitionState getTransitionState() { return state; }
    
    public void setTransitionState(TransitionState st) {
        state = st;
    }
    
    public void setTransitionEvent(VisualTransition VT) {
        transitionEvent = VT;
    }
    
    public void moveUp() {
        ((QuadBackgroundComponent)menuOptions.get(currentIndex).getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
        currentIndex--;
        if (currentIndex < 0) {
            currentIndex = menuOptions.size() - 1;
        }
        ((QuadBackgroundComponent)menuOptions.get(currentIndex).getBackground()).setColor(new ColorRGBA(1, 1, 1, 1));
    }
    
    public void moveDown() {
        ((QuadBackgroundComponent)menuOptions.get(currentIndex).getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
        currentIndex++;
        if (currentIndex >= menuOptions.size()) {
            currentIndex = 0;
        }
        ((QuadBackgroundComponent)menuOptions.get(currentIndex).getBackground()).setColor(new ColorRGBA(1, 1, 1, 1));
    }
    
    public VisualTransition getTransitionEvent() { return transitionEvent; }
    
    public float getHeight() {
        return options().get(0).height * options().size();
    }
    
    public float getWidth() {
        return options().get(0).width;
    }
    
    public void initializeBounds() {
        scale(2.1f / (options().size()));
        for (SubmenuOption option : options()) {
            option.width *= 2f / (options().size());
            option.height *= 2f / (options().size());
        }
        move(90, -90, 0);
    }
    
    public void transitionOut(TransitionType type) {
        transitionEvent.setTransitionType(type);
        state = TransitionState.TransitioningOut;
        transitionEvent.beginTransitions(determineTransitions());
        active = false;
    }
    
    public void transitionIn(TransitionType type) {
        transitionEvent.setTransitionType(type);
        state = TransitionState.TransitioningIn;
        transitionEvent.beginTransitions(determineTransitions());
        active = true;
    }
    
    public SubmenuOption getParentOption() { return parentOption; }
    
    public Submenu setParentOption(SubmenuOption sbo) {
        parentOption = sbo;
        return this;
    }
    
}
