/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.ui.menu;

import com.jme3.asset.AssetManager;
import com.jme3.input.MouseInput;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.event.MouseListener;
import general.visual.animation.TransitionSet.TransitionType;

/**
 *
 * @author night
 * @param <DATA> the type of the data holder (e.g. Conveyor)
 */
public abstract class MenuOption<DATA> {
    protected final String name;
    protected final Node optionNode = new Node();
    
    protected int index;
    
    protected Menu parentMenu;
    protected Menu submenu = null;
    protected boolean hasSubmenu;
    
    private Runnable closeMenuProtocol;
    
    protected boolean closeMenuCalled = false;
    protected boolean hovered = false;
    
    public MenuOption(String optionName) {
        name = optionName;
    }
    
    public <O extends MenuOption<DATA>> void initializeOption(int index, AssetManager assetManager, Menu<O, DATA> parent, Runnable closeMenuProtocol, DATA data) {
        this.index = index;
        this.closeMenuProtocol = closeMenuProtocol;
        
        parentMenu = parent;
        
        initialize(assetManager, data);
        
        hasSubmenu = submenu != null;
        if (hasSubmenu) {
            submenu.setParentOption(this);
        }
    }
    
    public int getIndex() { return index; }
    public String getName() { return name; }
    
    public Node getNode() { return optionNode; }
    
    public <O extends MenuOption<DATA>> Menu<O, DATA> getParentMenu() { return parentMenu; }
    public <O extends MenuOption<DATA>> Menu<O, DATA> getSubmenu() { return submenu; }
    public boolean hasSubmenu() { return hasSubmenu; }
    public boolean isSubmenuActive() { return hasSubmenu && submenu.isActive(); }
    
    public boolean isHovered() { return hovered; }
    
    protected void closeEntireMenu() {
        closeMenuProtocol.run();
        closeMenuCalled = true;
    }
    
    private float time = 0f;
    public void update(float tpf) {
        if (Float.MAX_VALUE - time <= 3f) { time = 0f; }
        
        updateCustom(tpf, time);
        
        time += tpf;
    }
    
    public void select() {
        onSelect();
        
        if (!closeMenuCalled) {
            Node masterNode = parentMenu.getRootMenu().getNode().getParent();
        
            parentMenu.transitionOut(TransitionType.OnSelect);
        
            if (hasSubmenu) {
                submenu.transitionIn(TransitionType.OnSelect, masterNode);
            }
        }
        
        closeMenuCalled = false;
    }
    
    //returns whether the hover state was changed or not
    public boolean setHovered(boolean hov) {
        boolean changed = (hov != hovered);
        
        if (changed) {
            setHover(hov);
            hovered = hov;
        }
        
        return changed;
    }
    
    protected MouseListener createMouseListener() {
        return new MouseListener() {
            private boolean hasEntered = false;
            
            @Override
            public void mouseButtonEvent(MouseButtonEvent mbe, Spatial sptl, Spatial sptl1) {
                if (mbe.getButtonIndex() == MouseInput.BUTTON_LEFT && hasEntered) { //if left click and has entered spatial
                    select();
                }
            }

            @Override
            public void mouseEntered(MouseMotionEvent mme, Spatial sptl, Spatial sptl1) {
                hasEntered = true;
                
                parentMenu.setAllOptionsUnhovered();
                setHovered(true);
                parentMenu.onMouseEnteredOption();
            }

            @Override
            public void mouseExited(MouseMotionEvent mme, Spatial sptl, Spatial sptl1) {
                hasEntered = false;
                
                setHovered(false);
                
                if (parentMenu.hoverCurrentIndexWhenNothingElseIsHovered()) {
                    parentMenu.getCurrentOption().setHovered(true);
                }
                
                parentMenu.onMouseExitedOption();
            }

            @Override
            public void mouseMoved(MouseMotionEvent mme, Spatial sptl, Spatial sptl1) {}
        };
    }
    
    protected abstract void setHover(boolean hov); //implement in child class
    
    protected abstract void updateCustom(float tpf, float time); //implement in either child or grandchild, but usually child class
    
    protected abstract void initialize(AssetManager assetManager, DATA data); //implement in grandchild class OR child class, but usually grandchild
    
    protected abstract void onSelect(); //implement in grandchild class
    
    
}
