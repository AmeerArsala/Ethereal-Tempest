/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation.config.action;

import battle.participant.visual.BattleParticleEffect;
import battle.participant.visual.BattleSprite;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Spatial;
import general.math.ParametricFunctionStrings3f;
import general.math.ParametricFunctionStrings4f;
import general.math.function.CartesianFunction;
import general.math.function.MathFunction;
import general.math.function.RGBAFunction;
import general.utils.wrapper.IdentifiedDuo;

/**
 *
 * @author night
 */
public class Changes {
    //use the arrays as piecewise
    private Change3f[] velocities; //velocity; units: localTranslation/frame
    private Change3f[] thetaVelocities; //angular velocity in degrees; units: degrees/frame 
    private Change3f[] localScales; //not a velocity function
    private Change4f[] colors; //not a velocity function
    private FlashColor flashColor; //flashes this color
    private Vector2f centerPoint; //point of rotation (pivot)
    
    public Changes() {}
    
    public Changes(Change3f[] velocities, Change3f[] thetaVelocities, Change3f[] localScales, Change4f[] colors, FlashColor flashColor, Vector2f centerPoint) {
        this.velocities = velocities;
        this.thetaVelocities = thetaVelocities;
        this.localScales = localScales;
        this.colors = colors;
        this.flashColor = flashColor;
        this.centerPoint = centerPoint;
    }
    
    Change3f[] getVelocities() { return velocities; }
    Change3f[] getAngularVelocities() { return thetaVelocities; }
    Change3f[] getLocalScales() { return localScales; }
    Change4f[] getColors() { return colors; }
    FlashColor getFlashColor() { return flashColor; }
    
    public Vector2f getCenterPoint() { return centerPoint; }
    
    static <S> IdentifiedDuo<Vector2f, Vector3f> obtainVariableChanges(S root) {
        Vector2f percentagePos;
        Vector3f localAngle;
        
        if (root instanceof BattleSprite) {
            BattleSprite sprite = (BattleSprite)root;
            percentagePos = sprite.getPercentagePosition();
            localAngle = sprite.getLocalAngle();
        } else { // root instanceof BattleParticleEffect.ParticleRootNode == true
            BattleParticleEffect.ParticleRootNode particleRoot = (BattleParticleEffect.ParticleRootNode)root;
            percentagePos = particleRoot.getPercentagePosition();
            localAngle = particleRoot.getLocalAngle();
        }
        
        return new IdentifiedDuo<>(percentagePos, localAngle);
    }
    
    /**
     * 
     * @param framesSinceActionFrame
     * @param user     do NOT pass in something that is an instance of Spatial; either BattleSprite or BattleParticleEffect.ParticleRootNode
     * @param opponent do NOT pass in something that is an instance of Spatial; either BattleSprite or BattleParticleEffect.ParticleRootNode
     * @param centerPointDefault default centerPoint in percents
     * @return a ChangePack
     */
    public ChangePack generateChangePack(int framesSinceActionFrame, Spatial user, Spatial opponent, Vector2f centerPointDefault) {
        IdentifiedDuo<Vector2f, Vector3f> userVariableChanges = obtainVariableChanges(user);         // A is percentagePos, B is localAngle
        IdentifiedDuo<Vector2f, Vector3f> opponentVariableChanges = obtainVariableChanges(opponent); // A is percentagePos, B is localAngle
        
        ColorRGBA rgba = null;
        String colorMatParam = null;
        
        if (user instanceof BattleSprite) {
            BattleSprite userSprite = (BattleSprite)user;
            ColorRGBA opponentColor = opponent instanceof BattleSprite ? ((BattleSprite)opponent).getColor() : new ColorRGBA(1, 1, 1, 1);
            
            ParametricFunctionStrings4f colorStrings = getColorStrings(framesSinceActionFrame);
            if (colorStrings != null) {
                rgba = colorStrings.outputColor(framesSinceActionFrame, ConstantsDealer.ColorRGBA_Constants(userSprite.getColor(), opponentColor));
                colorMatParam = colorStrings.getColorMatParam();
            }
        }
        
        return new ChangePack(
            centerPoint != null ? centerPoint : centerPointDefault,
            getVelocity(framesSinceActionFrame, userVariableChanges.getA(), opponentVariableChanges.getA()),
            getAngularVelocity(framesSinceActionFrame, userVariableChanges.getB(), opponentVariableChanges.getB()),
            getLocalScale(framesSinceActionFrame, user.getLocalScale(), opponent.getLocalScale()),
            rgba,
            colorMatParam, //corresponds to the parameter 'rgba'
            flashColor
        );
    }
    
    private static int getChangeIndex(DomainParse[] arr, int framesSinceActionFrame, String name) {
        //System.out.println("[framesSinceActionFrame: " + framesSinceActionFrame + "]: " + name);
        for (int i = 0; i < arr.length; ++i) {
            boolean infinite = arr[i].isInfinite();
            //System.out.println(name + "[" + i + "].isInfinite() == " + infinite);
            if (infinite || (!infinite && arr[i].frameWithinSpecifiedBounds(framesSinceActionFrame))) {
                return i;
            }
        }
        
        return -1;
    }
    
    public Vector3f getVelocity(int framesSinceActionFrame, Vector2f userPos, Vector2f opponentPos) {
        int index = getChangeIndex(velocities, framesSinceActionFrame, "velocity");

        return index == -1 ? null : velocities[index].getStrFunc().outputVector(
            framesSinceActionFrame,
            ConstantsDealer.Vec3f_Constants(
                new Vector3f(userPos.x, userPos.y, 0),        // userPos 
                new Vector3f(opponentPos.x, opponentPos.y, 0) // opponentPos
            )
        );
    }
    
    public Vector3f getAngularVelocity(int framesSinceActionFrame, Vector3f userAngle, Vector3f opponentAngle) {
        int index = getChangeIndex(thetaVelocities, framesSinceActionFrame, "thetaVelocity");
        return index == -1 ? null : thetaVelocities[index].getStrFunc().outputVector(
            framesSinceActionFrame,
            ConstantsDealer.Vec3f_Constants(userAngle, opponentAngle)
        ).multLocal(FastMath.DEG_TO_RAD);
    }
    
    public Vector3f getLocalScale(int framesSinceActionFrame, Vector3f userScale, Vector3f opponentScale) {
        int index = getChangeIndex(localScales, framesSinceActionFrame, "localScale");
        return index == -1 ? null : localScales[index].getStrFunc().outputVector(
            framesSinceActionFrame,
            ConstantsDealer.Vec3f_Constants(userScale, opponentScale)
        );
    }
    
    public ParametricFunctionStrings4f getColorStrings(int framesSinceActionFrame) {
        int index = getChangeIndex(colors, framesSinceActionFrame, "color");
        return index == -1 ? null : colors[index].getStrFunc();
    }
    
    public void initializeAll() {
        char[] constants = ConstantsDealer.EXPRESSION_CONSTANTS;
        for (Change3f velocity : velocities) {
            velocity.getStrFunc().initialize(constants);
        }
        
        for (Change3f thetaVelocity : thetaVelocities) {
            thetaVelocity.getStrFunc().initialize(constants);
        }
        
        for (Change3f localScale : localScales) {
            localScale.getStrFunc().initialize(constants);
        }
        
        for (Change4f color : colors) {
            color.getStrFunc().initialize(constants);
        }
    }
}

class DomainParse {
    private String domain;
    
    public DomainParse(String domain) { //something like "1, 2" or "0, ?" which is 0 to infinity
        this.domain = domain;
    }
    
    public int getStartFrame() { return Integer.parseUnsignedInt(domain.substring(0, 1)); } //parse first character
    public int getEndFrame() { return Integer.parseUnsignedInt(domain.substring(domain.length() - 1)); } //can fail if infinite so check first
    public boolean isInfinite() { return domain.charAt(domain.length() - 1) == '?'; }
    
    public boolean frameWithinSpecifiedBounds(int frame) {
        return frame >= getStartFrame() && frame <= getEndFrame();
    }
}

class Change3f extends DomainParse {
    private ParametricFunctionStrings3f strFunc;
    
    public Change3f(ParametricFunctionStrings3f strFunc, String domain) {
        super(domain);
        this.strFunc = strFunc;
    }
    
    public ParametricFunctionStrings3f getStrFunc() { return strFunc; }
}

class Change4f extends DomainParse {
    private ParametricFunctionStrings4f strFunc;
    
    public Change4f(ParametricFunctionStrings4f strFunc, String domain) {
        super(domain);
        this.strFunc = strFunc;
    }
    
    public ParametricFunctionStrings4f getStrFunc() { return strFunc; }
}
