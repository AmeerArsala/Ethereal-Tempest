/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.visual;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Panel;
import general.ResetProtocol;
import general.ui.Submenu;
import general.ui.Submenu.TransitionType;
import java.util.List;

/**
 *
 * @author night
 */
public class VisualTransition {
    private Spatial focus;
    private List<Transition> appliedTransitions;
    private Progress transitionProgress = Progress.Fresh;
    private TransitionType transitionDirection = TransitionType.None;
    private Vector3f birth;
    private ResetProtocol resetSequence;
    
    private static float dX, dY;
    
    public enum Progress {
        Fresh,
        Progressing,
        Finished
    }
    
    //for first parameter: either do Geometry, Panel, or Container
    public VisualTransition(Spatial S, List<Transition> transitions) { //use Arrays.asList(...) for the 2nd parameter
        focus = S;
        appliedTransitions = transitions;
    }
    
    public VisualTransition(Spatial S) {
        focus = S;
    }
    
    public static void setDimensions(float dimensionX, float dimensionY) {
        dX = dimensionX;
        dY = dimensionY;
    }
    
    public void setBirthTranslation(Vector3f pos) {
        birth = pos;
    }
    
    public void updateTransitions() {
        if (transitionProgress == Progress.Progressing) {
            int done = 0;
            for (Transition applied : appliedTransitions) {
                if (applied.getProgress() == Progress.Progressing) {
                    applied.update(focus);
                    if ((applied.getID() == ZoomIn().getID() || applied.getID() == ZoomOut().getID()) && birth != null) {
                        focus.move(birth.x * applied.getNextScale(), birth.y * applied.getNextScale(), birth.z * applied.getNextScale());
                    }
                } else if (applied.getProgress() == Progress.Finished) {
                    done++;
                }
            }
            if (done == appliedTransitions.size()) {
                transitionProgress = Progress.Finished;
                transitionDirection = TransitionType.None;
                System.out.println("Transition Finished");
                if (resetSequence != null) {
                    resetSequence.execute();
                }
            }
        }
    }
    
    public void beginTransitions() {
        transitionProgress = Progress.Progressing;
        for (Transition tr : appliedTransitions) {
            tr.setProgress(Progress.Progressing);
        }
    }
    
    public void beginTransitions(List<Transition> transitions) {
        appliedTransitions = transitions;
        beginTransitions();
    }
    
    public Spatial getFocus() { return focus; }
    public List<Transition> getTransitions() { return appliedTransitions; }
    
    public TransitionType getTransitionType() { return transitionDirection; }
    public Progress getTransitionProgress() { return transitionProgress; }
    
    public void setTransitionType(TransitionType TT) {
        transitionDirection = TT;
    }
    
    public void setTransitions(List<Transition> ts) {
        appliedTransitions = ts;
    }
    
    public void setResetProtocol(ResetProtocol RP) {
        resetSequence = RP;
    }
    
    public static Transition DissolveIn() {
        return new Transition() {
            @Override
            public int getID() { return 0; }
            
            @Override
            public void update() {
                coefficient++;
                if (target instanceof Geometry) {
                    ((Geometry)target).getMaterial().setColor("Color", new ColorRGBA(1, 1, 1, coefficient * (1f / maxFrame)));
                } else if (target instanceof Container || target instanceof Submenu) {
                    ((Container)target).setAlpha(coefficient * (1f / maxFrame));
                } else if (target instanceof Panel) {
                    ((Panel)target).setAlpha(coefficient * (1f / maxFrame));
                } else if (target instanceof Node) {
                    for (Spatial child : ((Node)target).getChildren()) {
                        if (child instanceof Geometry) {
                            ((Geometry)child).getMaterial().setColor("Color", new ColorRGBA(1, 1, 1, coefficient * (1f / maxFrame)));
                        } else if (child instanceof Container|| child instanceof Submenu) {
                            ((Container)child).setAlpha(coefficient * (1f / maxFrame));
                        } else if (child instanceof Panel) {
                            ((Panel)child).setAlpha(coefficient * (1f / maxFrame));
                        }
                    } 
                }
            }
        }.setStartingIndexScale(0);
    }
    
    public static Transition DissolveOut() {
        return new Transition() {
            @Override
            public int getID() { return 1; }
            
            @Override
            public void update() {
                coefficient--;
                if (target instanceof Geometry) {
                    ((Geometry)target).getMaterial().setColor("Color", new ColorRGBA(1, 1, 1, coefficient * (1f / maxFrame)));
                } else if (target instanceof Container || target instanceof Submenu) {
                    ((Container)target).setAlpha(coefficient * (1f / maxFrame));
                } else if (target instanceof Panel) {
                    ((Panel)target).setAlpha(coefficient * (1f / maxFrame));
                } else if (target instanceof Node) {
                    for (Spatial child : ((Node)target).getChildren()) {
                        if (child instanceof Geometry) {
                            ((Geometry)child).getMaterial().setColor("Color", new ColorRGBA(1, 1, 1, coefficient * (1f / maxFrame)));
                        } else if (child instanceof Container || child instanceof Submenu) {
                            ((Container)child).setAlpha(coefficient * (1f / maxFrame));
                        } else if (child instanceof Panel) {
                            ((Panel)child).setAlpha(coefficient * (1f / maxFrame));
                        }
                    } 
                }
            }
        }.setStartingIndexScale(1);
    }
    
    public static Transition ZoomIn() {
        return new Transition() {
            @Override
            public int getID() { return 2; }
            
            @Override
            public void update() {
                coefficient++;
                
                try {
                    target.setLocalScale(getNextScale());

                    if (target instanceof Geometry) {
                        BoundingBox box = (BoundingBox)target.getWorldBound();
                        float diffX = box.getXExtent() * (1f - getNextScale()),
                        diffY = box.getYExtent() * (1f - getNextScale());
                        Vector3f stagnate = target.worldToLocal(new Vector3f(target.getWorldTranslation().x + diffX, target.getWorldTranslation().y + diffY, target.getWorldTranslation().z), null);
                        target.setLocalTranslation(stagnate);
                    } else if (target instanceof Submenu) {
                        target.move((-1f / maxFrame) * ((Submenu)target).getWidth() * 0.5f, (1f / maxFrame) * ((Submenu)target).getHeight() * 0.5f, 0);
                    } else if (target instanceof Panel) {
                        target.move((-1f / maxFrame) * 180 * 0.5f, (1f / maxFrame) * 180 * 0.5f, 0);
                        //target.move(getNextScale() * -6 * scaled, getNextScale() * scaled * 6, 0);
                    }
                    
                }
                catch (Exception e) {
                    System.out.println("exception!");
                }
            }
        }.setStartingIndexScale(0);
    }
    
    public static Transition ZoomOut() {
        return new Transition() {
            @Override
            public int getID() { return 3; }
            
            @Override
            public void update() {
                coefficient--;
                
                try {
                    target.setLocalScale(getNextScale());

                    if (target instanceof Geometry) {
                        BoundingBox box = (BoundingBox)target.getWorldBound();
                        float diffX = box.getXExtent() * (1f - getNextScale()),
                        diffY = box.getYExtent() * (1f - getNextScale());
                        Vector3f stagnate = target.worldToLocal(new Vector3f(target.getWorldTranslation().x + diffX, target.getWorldTranslation().y + diffY, target.getWorldTranslation().z), null);
                        target.setLocalTranslation(stagnate);
                    } else if (target instanceof Submenu) {
                        target.move((1f / maxFrame) * ((Submenu)target).getWidth() * 0.5f, (-1f / maxFrame) * ((Submenu)target).getHeight() * 0.5f, 0);
                    } else if (target instanceof Panel) {
                        target.move((1f / maxFrame) * 180 * 0.5f, (-1f / maxFrame) * 180 * 0.5f, 0);
                    }
                }
                catch (Exception e) {
                    System.out.println("exception!");
                }
            }
        }.setStartingIndexScale(1);
    }
    
    public static Transition SpinZ() {
        return new Transition() {
            @Override
            public int getID() { return 4; }
            
            @Override
            public void update() {
                coefficient++;
                target.rotate(0, 0, coefficient * ((2f * FastMath.PI) / maxFrame));
            }
        }.setStartingIndexScale(0);
    }
    
}

/*class InsetCreator {
    public float top = 0, left = 0, bottom = 0, right = 0;
    
    public InsetCreator(float t, float l, float b, float r) {
        top = t;
        left = l;
        bottom = b;
        right = r;
    }
    
    static InsetCreator fromTranslation(float deltaX, float deltaY) {
        float TOP = 0, LEFT = 0, BOTTOM = 0, RIGHT = 0;
        if (deltaX > 0) {
            LEFT = deltaX;
        } else if (deltaX < 0) {
            RIGHT = FastMath.abs(deltaX);
        }
        if (deltaY > 0) {
            BOTTOM = deltaY;
        } else if (deltaY < 0) {
            TOP = FastMath.abs(deltaY);
        }
        
        return new InsetCreator(TOP, LEFT, BOTTOM, RIGHT);
    }
}*/
