/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.ui;

import general.visual.VisualTransition;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import general.ResetProtocol;
import general.ui.Submenu.TransitionState;
import general.ui.Submenu.TransitionType;
import general.visual.VisualTransition.Progress;
import java.util.Arrays;

/**
 *
 * @author night
 */
public class MenuOption {
    private Quad q;
    
    public Geometry geo;
    public Material optionmat;
    public String name = "";
    public boolean isAvailable, isSelected, isShown = true;
    public Texture[] matclassic;
    public Submenu submenu;
    
    public TransitionState transition = TransitionState.Standby;
    //public TransitionType transitionType = TransitionType.None;
    public VisualTransition transitionEffect;
    public ResetProtocol onSelect;
    //private final float MAX_TRANSITION_COEFFICIENT = 30;
    
    protected boolean init = false;
    protected float ogY = 0;
    protected Vector3f birthmove;
    
    public static boolean ogMenuActive = true;
    
    //material format is typically: selectedAvailable, selectedUnavailable, deselectedAvailable, deselectedUnavailable
    public MenuOption(String name, Quad q, boolean isAvailable) {
        this.name = name;
        this.q = q;
        this.isAvailable = isAvailable;
        geo = new Geometry("Quad", this.q);
        //geo.setQueueBucket(Bucket.Translucent);
    }
    
    public MenuOption(String name, Quad q, boolean isAvailable, Submenu submenu) {
        this.name = name;
        this.q = q;
        this.isAvailable = isAvailable;
        this.submenu = submenu;
        geo = new Geometry("Quad", this.q);
        //geo.setQueueBucket(Bucket.Translucent);
    }
    
    public MenuOption(String name, Quad q, boolean isAvailable, ResetProtocol onSelect) {
        this.name = name;
        this.q = q;
        this.isAvailable = isAvailable;
        this.onSelect = onSelect;
        geo = new Geometry("Quad", this.q);
        //geo.setQueueBucket(Bucket.Translucent);
    }
    
    public void deselect() {
        if (name.equals("done")) {
            optionmat.setTexture("ColorMap", matclassic[1]);
            geo.setLocalTranslation(geo.getLocalTranslation().x, geo.getLocalTranslation().y, ogY);
            isSelected = false;
        } else {
            geo.setLocalTranslation(geo.getLocalTranslation().x, geo.getLocalTranslation().y, ogY);
            isSelected = false;
            updateState(0);
        }
    }
    
    public void updateState(int index) {
        if (!init) { 
            optionmat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            init = true;
            geo.setMaterial(optionmat);
            ogY = geo.getLocalTranslation().z;
        }
        
        if (transition == TransitionState.Standby) {
            if (isShown) {
                geo.getMaterial().setColor("Color", new ColorRGBA(1, 1, 1, 1));
                if (isAvailable) {
                    if (isSelected) {
                        optionmat.setTexture("ColorMap", matclassic[0]);
                    } else {
                        if (name.equals("done")) { optionmat.setTexture("ColorMap", matclassic[1]); } else { optionmat.setTexture("ColorMap", matclassic[2]); }
                    }
                } else {
                    if (isSelected) {
                        optionmat.setTexture("ColorMap", matclassic[1]);
                    } else {
                        optionmat.setTexture("ColorMap", matclassic[3]);
                    }
                }
            } else {
                optionmat.setTexture("ColorMap", matclassic[3]);
                geo.getMaterial().setColor("Color", new ColorRGBA(1, 1, 1, 0));
            }
        
            if (isSelected) {
                geo.setLocalTranslation(geo.getLocalTranslation().x, geo.getLocalTranslation().y, ogY + 3f);
            } else {
                geo.setLocalTranslation(geo.getLocalTranslation().x, geo.getLocalTranslation().y, ogY);
            }
        
            if (geo.getMaterial() != optionmat) {
                geo.setMaterial(optionmat);
            }
        } else { //transition is occuring
            if (transitionEffect.getTransitionProgress() != Progress.Finished) {
                transitionEffect.updateTransitions();
            } else {
                //after the transition is over
                if (transition == TransitionState.TransitioningIn) {
                    transition = TransitionState.Standby;
                } else if (transition == TransitionState.TransitioningOut) {
                    //after the outer transition is over
                }
            }
        }
        
        if (submenu != null) {
            submenu.updateDefault();
        }
        
        /*if (submenu != null && (submenu.isActive() || submenu.getTransitionState() != TransitionState.Standby)) {
            submenu.updateDefault();
        }*/
    }
    
    public void selectOption() {
        if (submenu != null && !submenu.isActive() && transition == TransitionState.Standby) {
            if (onSelect != null) {
                onSelect.execute();
            }
            beginDisappearance();
            
            submenu.lightInitialize();
            submenu.setActive(true);
            submenu.setTransitionState(TransitionState.TransitioningIn);
            submenu.getTransitionEvent().setTransitionType(TransitionType.Forward);
            submenu.getTransitionEvent().beginTransitions(submenu.determineTransitions());
            ogMenuActive = false;
            //submenu.setTransitionEvent(new VisualTransition(submenu, Arrays.asList(VisualTransition.DissolveIn(), VisualTransition.ZoomIn().setStartingIndexScale(0))));
            //submenu.getTransitionEvent().beginTransitions(submenu.modifyList(submenu.determineTransitions(), submenu.determineTransitions().get(1).setLength(0.25f), 1));
        }
    }
    
    public void beginDisappearance() {
        if (isShown) {
            transitionEffect = new VisualTransition(geo, Arrays.asList(VisualTransition.DissolveOut().setLength(0.15f), VisualTransition.ZoomIn().setStartingIndexScale(1).setLength(0.15f)));
            transition = TransitionState.TransitioningOut;
            transitionEffect.setTransitionType(TransitionType.Forward);
            transitionEffect.setBirthTranslation(birthmove);
            transitionEffect.beginTransitions();
        }
    }
    
    public void beginReappearance() {
        if (isShown) {
            transitionEffect = new VisualTransition(geo, Arrays.asList(VisualTransition.DissolveIn().setLength(0.15f), VisualTransition.ZoomOut().setStartingIndexScale(2).setLength(0.15f)));
            transition = TransitionState.TransitioningIn;
            transitionEffect.setTransitionType(TransitionType.Backward);
            transitionEffect.setBirthTranslation(birthmove);
            transitionEffect.beginTransitions();
        }
    }
    
    public void setBirthMove(float x, float y, float z) {
        birthmove = new Vector3f(x, y, z);
    }
    
}

class AnimOption extends MenuOption {
    public Texture[] matanimated, matanimatedSelected;
    
    public AnimOption(String name, Quad q, boolean isAvailable) {
        super(name, q, isAvailable);
    }
    
    public AnimOption(String name, Quad q, boolean isAvailable, Submenu submenu) {
        super(name, q, isAvailable, submenu);
    }
    
    @Override
    public void updateState(int index) {
        if (!init) { 
            optionmat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha); 
            init = true;
            geo.setMaterial(optionmat);
            ogY = geo.getLocalTranslation().z;
        }
        
        if (transition == TransitionState.Standby) {
            if (isShown && isAvailable) {
                geo.getMaterial().setColor("Color", new ColorRGBA(1, 1, 1, 1));
                if (isSelected) {
                    optionmat.setTexture("ColorMap", matanimated[index]);
                } else {
                    optionmat.setTexture("ColorMap", matanimatedSelected[index]);;
                }
            } else {
                geo.getMaterial().setColor("Color", new ColorRGBA(1, 1, 1, 0));
            }
        
            if (isSelected) {
                geo.setLocalTranslation(geo.getLocalTranslation().x, geo.getLocalTranslation().y, ogY + 3f);
            } else {
                geo.setLocalTranslation(geo.getLocalTranslation().x, geo.getLocalTranslation().y, ogY);
            }
        } else { //Half second transition
            if (transitionEffect.getTransitionProgress() != Progress.Finished) {
                transitionEffect.updateTransitions();
            } else {
                //after the transition is over
                if (transition == TransitionState.TransitioningIn) {
                    transition = TransitionState.Standby;
                } else if (transition == TransitionState.TransitioningOut) {
                    
                }
            }
        }
        
        if (submenu != null) {
            submenu.updateDefault();
        }
        
    }
    
}

class Centerpiece extends AnimOption {
    
    public Centerpiece(String name, Quad q, boolean isAvailable) {
        super(name, q, isAvailable);
    }
    
    public Centerpiece(String name, Quad q, boolean isAvailable, Submenu submenu) {
        super(name, q, isAvailable, submenu);
    }
    
    @Override
    public void updateState(int index) {
        if (!init) {
            optionmat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            init = true;
            geo.setMaterial(optionmat);
            ogY = geo.getLocalTranslation().z;
        }
        
        if (transition == TransitionState.Standby) {
            if (isShown) {
                geo.getMaterial().setColor("Color", new ColorRGBA(1, 1, 1, 1));
                if (isAvailable) {
                    if (isSelected) {
                        optionmat.setTexture("ColorMap", matanimatedSelected[index]);
                    } else {
                        optionmat.setTexture("ColorMap", matanimated[index]);
                    }
                } else {
                    if (isSelected) {
                        optionmat.setTexture("ColorMap", matclassic[0]); //0 will be selectedUnavailable, 1 will be deselectedUnavailable
                    } else { optionmat.setTexture("ColorMap", matclassic[1]);; }
                }
            } else {
                geo.getMaterial().setColor("Color", new ColorRGBA(1, 1, 1, 0));
            }
            
            if (isSelected) {
                geo.setLocalTranslation(geo.getLocalTranslation().x, geo.getLocalTranslation().y, ogY + 3f);
            } else {
                geo.setLocalTranslation(geo.getLocalTranslation().x, geo.getLocalTranslation().y, ogY);
            }
        } else {
            if (transitionEffect.getTransitionProgress() != Progress.Finished) {
                transitionEffect.updateTransitions();
            } else {
                //after the transition is over
                if (transition == TransitionState.TransitioningIn) {
                    transition = TransitionState.Standby;
                } else if (transition == TransitionState.TransitioningOut) {
                    
                }
            }
        }
        
        if (submenu != null) {
            submenu.updateDefault();
        }
        
    }
    
}
