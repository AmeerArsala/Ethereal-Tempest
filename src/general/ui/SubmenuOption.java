/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.ui;

import etherealtempest.info.Conveyer;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Panel;

/**
 *
 * @author night
 */
public abstract class SubmenuOption extends Container {
    protected int index;
    protected Submenu parentsb, child;
    protected Conveyer info;
    protected Container possibleContents;
    
    public float width, height;
    
    public SubmenuOption(float width, float height) {
        //super(width, height);
        super();
        setPreferredSize(new Vector3f(width, height, 0));
        this.width = width;
        this.height = height;
    }
    
    public SubmenuOption(float width, float height, int pos) {
        //super(width, height);
        super();
        setPreferredSize(new Vector3f(width, height, 0));
        this.width = width;
        this.height = height;
        index = pos;
    }
    
    public abstract void interpretData();
    public abstract void select();
    public abstract void initialize();
    
    public boolean hasSubmenu() {
        return child != null;
    }
    
    public void setSubmenu(Submenu sb) {
        child = sb;
    }
    
    public boolean isSubmenuActive() {
        return child.active;
    }
    
    public void putData(Conveyer C) {
        info = C;
        interpretData();
    }
    
    public void selectOption(Conveyer C) {
        putData(C);
        select();
    }
    
    public void scaleOption(float factor) {
        width *= factor;
        height *= factor;
        scale(factor);
    }
    
    public void initializeAll(Conveyer C) {
        putData(C);
        initialize();
    }
    
    public SubmenuOption obtainChild(Container container) {
        possibleContents = container;
        addChild(possibleContents);
        return this;
    }
    
    public void catchSubmenuInput(String name, float tpf, Conveyer C) {
        child.catchInput(name, tpf, C);
    }
    
    public SubmenuOption setParentSubmenu(Submenu sb) {
        parentsb = sb;
        return this;
    }
    
    public Submenu getParentSubmenu() { return parentsb; }
    
}
