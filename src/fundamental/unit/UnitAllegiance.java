/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.unit;

import com.google.gson.annotations.SerializedName;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author night
 */
public enum UnitAllegiance {
    @SerializedName("Player") Player(0, ColorRGBA.Blue),
    @SerializedName("Ally") Ally(-1, ColorRGBA.Yellow),
    @SerializedName("Enemy") Enemy(1, ColorRGBA.Red),
    @SerializedName("ThirdParty") ThirdParty(2, ColorRGBA.Green),
    @SerializedName("FourthParty") FourthParty(3, ColorRGBA.Magenta),
    @SerializedName("FifthParty") FifthParty(4, ColorRGBA.White);
        
    private final int value;
    private final ColorRGBA lightColor;
    private UnitAllegiance(int val, ColorRGBA lightCol) {
        value = val;
        lightColor = lightCol;
    }

    public int getValue() {
        return value;
    }
        
    public boolean alliedWith(UnitAllegiance otherAllegiance) {
        return value == otherAllegiance.getValue() || value + otherAllegiance.getValue() == -1;
    }
    
    public ColorRGBA getLightColor() {
        return lightColor;
    }
        
    public ColorRGBA getAssociatedColor() {
        return getAssociatedColor(value);
    }
        
    public static ColorRGBA getAssociatedColor(int val) {
        ColorRGBA barColor;
        
        switch (val) {
            case 0: //blue (Player)
                barColor = new ColorRGBA(0.012f, 0.58f, 0.988f, 1f);
                break;
            case 1: //red (Enemy)
                barColor = new ColorRGBA(0.839f, 0, 0, 1f);
                break;
            case -1: //yellow (Ally)
                barColor = new ColorRGBA(1f, 0.851f, 0, 1f);
                break;
            case 2: //green (ThirdParty)
                barColor = new ColorRGBA(0, 1f, 0, 1f);
                break;
            case 3: //purple (FourthParty)
                barColor = new ColorRGBA(0.784f, 0, 1f, 1f);
                break;
            case 4: //white (FifthParty)
                barColor = ColorRGBA.White;
                break;
            default:
                barColor = ColorRGBA.randomColor().setAlpha(1f); //random
                break;
        }
        
        return barColor;
    }
}
