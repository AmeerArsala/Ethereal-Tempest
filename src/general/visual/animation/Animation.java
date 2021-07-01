/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.visual.animation;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Panel;
import general.math.Theta3D;
import general.math.function.CartesianFunction;
import general.math.function.MathFunction;
import general.math.function.ParametricFunction3f;
import general.math.function.ParametricFunction4f;
import general.ui.GeometryPanel;
import general.utils.ParamSetter;
import general.utils.helpers.EngineUtils;
import general.visual.animation.VisualTransition.Progress;

/**
 *
 * @author night
 */
public abstract class Animation {
    private boolean customFunction;
    private Progress trProgress = Progress.Fresh;
    
    private float initialValue = 0, endingValue = 1;
    private Vector3f originalTargetPosition;
    private MathFunction curve; //y value is replacement for coefficient
    private Runnable onFinish = () -> {};
    
    private float time = 0; //counts tpf
    private float maxLength = 0.5f; //half a second by default
    
    public Animation() {
        curve = CartesianFunction.pointSlopeLine(initialValue, endingValue, 0, maxLength);
        customFunction = false;
    }
    
    public Animation(MathFunction func) {
        curve = func;
        customFunction = true;
    }
    
    protected abstract void update(float tpf, float Y, Spatial target, Animation anim);
    
    public void updateAndTrack(float tpf, Spatial target) {
        if (trProgress == Progress.Progressing) {
            update(tpf, curve.output(time), target, this);
            
            if (time >= maxLength) {
                trProgress = Progress.Finished;
                onFinish.run();
                return;
            }
            
            time += tpf;
        }
    }
    
    public void resetTime() {
        time = 0;
    }
    
    public Animation setLength(float seconds) {
        maxLength = seconds;
        
        if (!customFunction) {
            curve = CartesianFunction.pointSlopeLine(initialValue, endingValue, 0, maxLength);
        }
        
        return this;
    }
    
    public Animation setFunction(MathFunction func) {
        curve = func;
        customFunction = true;
        return this;
    }
    
    public Animation setInitialVal(float initial) {
        initialValue = initial;
        
        if (!customFunction) {
            curve = CartesianFunction.pointSlopeLine(initialValue, endingValue, 0, maxLength);
        } else {
            curve.add(initial - curve.getShift());
        }
        
        return this;
    }
    
    public Animation setEndVal(float end) { //only does anything if it's not a custom function
        endingValue = end;
        
        if (!customFunction) {
            curve = CartesianFunction.pointSlopeLine(initialValue, endingValue, 0, maxLength);
        }
        
        return this;
    }
    
    public Animation setInitialAndEndVals(float initial, float end) { //combines the above two methods 
        initialValue = initial;
        endingValue = end;
        
        if (!customFunction) {
            curve = CartesianFunction.pointSlopeLine(initialValue, endingValue, 0, maxLength);
        } else {
            curve.add(initial - curve.getShift());
        }
        
        return this;
    }
    
    public Animation onFinish(Runnable finish) {
        onFinish = finish;
        
        return this;
    }
    
    public void setOriginalTargetPosition(Vector3f originalTargetPosition) {
        this.originalTargetPosition = originalTargetPosition;
    }
    
    public void reverseInitialAndEndVals() {
        float temp = initialValue;
        initialValue = endingValue;
        endingValue = temp;
        
        if (!customFunction) {
            curve = CartesianFunction.pointSlopeLine(initialValue, endingValue, 0, maxLength);
        } else {
            curve.add(initialValue - curve.getShift());
        }
    }
    
    public Progress getProgress() { return trProgress; }
    
    public MathFunction getFunction() { return curve; }
    public float getInitialVal() { return curve.output(0); } //initial wildcard; can represent the initial value of scale, rotation, opacity, etc.
    public float getEndVal() { return curve.output(maxLength); } //final wildcard; can represent the final value of scale, rotation, opacity, etc. 
    public Vector3f getOriginalTargetPosition() { return originalTargetPosition; }
    
    public Runnable getOnFinish() { return onFinish; }
    
    public float getTime() { return time; }
    public float getLength() { return maxLength; }
    
    public float getCurrentOutput() {
        return curve.output(time);
    }
    
    public void setProgress(Progress P) {
        trProgress = P;
    }
    
    public Animation newInstance() {
        Animation original = this;
        Animation newInstance = new Animation() {
            @Override
            public void update(float tpf, float Y, Spatial target, Animation anim) {
                original.update(tpf, Y, target, anim);
            }
        }.setInitialAndEndVals(initialValue, endingValue).setLength(maxLength);
        
        if (customFunction) {
            newInstance.setFunction((CartesianFunction)curve);
        }
        
        return newInstance;
    }
    
    
    public static final Animation NOTHING() { 
        return new Animation() {
            @Override
            protected void update(float tpf, float Y, Spatial target, Animation anim) {}
        };
    }
    
    public static Animation reverse(Animation anim) {
        return anim.newInstance().setInitialAndEndVals(anim.endingValue, anim.initialValue);
    }
    
    public static <T extends Spatial> Animation CleanOpacityShift(ParamSetter<T, ColorRGBA> colorSetter) {
        return new Animation() {
            @Override
            protected void update(float tpf, float Y, Spatial target, Animation anim) {
                colorSetter.set((T)target, new ColorRGBA(1, 1, 1, Y));
            }  
        };
    }
    
    public static Animation OpacityShift() {
        return new Animation() {
            
            @Override
            protected void update(float tpf, float Y, Spatial target, Animation anim) {
                if (target instanceof Geometry) {
                    ((Geometry)target).getMaterial().setColor("Color", new ColorRGBA(1, 1, 1, Y));
                } else if (target instanceof Container) {
                    ((Container)target).setAlpha(Y);
                } else if (target instanceof Panel) {
                    ((Panel)target).setAlpha(Y);
                } else if (target instanceof Node) {
                    ((Node)target).getChildren().forEach((child) -> {
                        if (child instanceof Geometry) {
                            ((Geometry)child).getMaterial().setColor("Color", new ColorRGBA(1, 1, 1, Y));
                        } else if (child instanceof Container) {
                            ((Container)child).setAlpha(Y);
                        } else if (child instanceof Panel) {
                            ((Panel)child).setAlpha(Y);
                        }
                    }); 
                }
            }  
        };
    }
    
    public static Animation ZoomShift() {
        return new Animation() {
            
            @Override
            protected void update(float tpf, float Y, Spatial target, Animation anim) {
                EngineUtils.setLocalScaleFromCenter(target, Y);
            }
        };
    }
    
    public static Animation DissolveIn() {
        return OpacityShift().setInitialAndEndVals(0, 1);
    }
    
    public static Animation DissolveOut() {
        return OpacityShift().setInitialAndEndVals(1, 0);
    }
    
    public static Animation ZoomIn() {
        return ZoomShift().setInitialAndEndVals(0, 1);
    }
    
    public static Animation ZoomOut() {
        return ZoomShift().setInitialAndEndVals(1, 0);
    }
    
    public static Animation SpinZ() {
        return new Animation() {
            @Override
            protected void update(float tpf, float Y, Spatial target, Animation anim) {
                target.rotate(0, 0, Y * FastMath.TWO_PI);
            }
        }.setInitialAndEndVals(0, 1);
    }
    
    public static Animation Spin(ParametricFunction3f angularVelocity, boolean inDegrees) {
        return new Animation() {
            @Override
            protected void update(float tpf, float Y, Spatial target, Animation anim) {
                float dxRot = angularVelocity.x(anim.time);
                float dyRot = angularVelocity.y(anim.time);
                float dzRot = angularVelocity.z(anim.time);
                
                if (inDegrees) {
                    dxRot *= FastMath.DEG_TO_RAD;
                    dyRot *= FastMath.DEG_TO_RAD;
                    dzRot *= FastMath.DEG_TO_RAD;
                }
                
                target.rotate(dxRot, dyRot, dzRot);
            }
        }.setInitialAndEndVals(0, 1);
    }
    
    public static Animation MoveDirection2D(float theta, float magnitude) {
        return new Animation() {
            @Override
            protected void update(float tpf, float Y, Spatial target, Animation anim) {
                float dx = magnitude * FastMath.sin(theta);
                float dy = magnitude * FastMath.cos(theta);
                
                target.setLocalTranslation(anim.originalTargetPosition.add(dx, dy, 0).mult(Y / anim.endingValue));
            }
        };
    }
    
    public static Animation MoveDirection3D(Theta3D theta, float magnitude) {
        return new Animation() {
            @Override
            protected void update(float tpf, float Y, Spatial target, Animation anim) {
                float dx = magnitude * FastMath.sin(theta.getXYAngle());
                float dy = magnitude * FastMath.cos(theta.getXYAngle());
                float dz = magnitude * FastMath.sin(theta.getZAngle());
                
                target.setLocalTranslation(anim.originalTargetPosition.add(dx, dy, dz).mult(Y / anim.endingValue));
            }
        };
    }
    
    public static Animation Move(ParametricFunction3f velocityFunc) {
        return new Animation() {
            @Override
            protected void update(float tpf, float Y, Spatial target, Animation anim) {
                float dx = velocityFunc.x(anim.time);
                float dy = velocityFunc.y(anim.time);
                float dz = velocityFunc.z(anim.time);
                
                target.move(dx, dy, dz);
            }
        };
    }
    
    public static Animation setPosition(ParametricFunction3f positionFunc) {
        return new Animation() {
            @Override
            protected void update(float tpf, float Y, Spatial target, Animation anim) {
                float x = positionFunc.x(anim.time);
                float y = positionFunc.y(anim.time);
                float z = positionFunc.z(anim.time);
                
                target.setLocalTranslation(x, y, z);
            }
        };
    }
    
    public static Animation changeToAColor(String colorParam, ColorRGBA desiredColor, ColorRGBA initialColor) {
        return new Animation() {
            private ParametricFunction4f RGBA;
            
            @Override
            public void resetTime() {
                super.resetTime();
                
                float duration = super.maxLength;
                
                RGBA = new ParametricFunction4f(
                    CartesianFunction.pointSlopeLine(initialColor.r, desiredColor.r, 0f, duration), // R
                    CartesianFunction.pointSlopeLine(initialColor.g, desiredColor.g, 0f, duration), // G
                    CartesianFunction.pointSlopeLine(initialColor.b, desiredColor.b, 0f, duration), // B
                    CartesianFunction.pointSlopeLine(initialColor.a, desiredColor.a, 0f, duration)  // A
                );
            }
            
            @Override
            protected void update(float tpf, float Y, Spatial target, Animation anim) {
                float r = RGBA.r(anim.time);
                float g = RGBA.g(anim.time);
                float b = RGBA.b(anim.time);
                float a = RGBA.a(anim.time);
                
                if (target instanceof Geometry) {
                    ((Geometry)target).getMaterial().setColor(colorParam, new ColorRGBA(r, g, b, a));
                } else if (target instanceof GeometryPanel) {
                    ((GeometryPanel)target).getMaterial().setColor(colorParam, new ColorRGBA(r, g, b, a));
                }
            }
        };
    }
    
    public static Animation changeToAColor(String colorParam, ColorRGBA color) {
        return changeToAColor(colorParam, color, ColorRGBA.White);
    }
}
