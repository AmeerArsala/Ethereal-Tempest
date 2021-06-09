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
    @SerializedName("Player") Player(0),
    @SerializedName("Ally") Ally(-1),
    @SerializedName("Enemy") Enemy(1),
    @SerializedName("ThirdParty") ThirdParty(2),
    @SerializedName("FourthParty") FourthParty(3),
    @SerializedName("FifthParty") FifthParty(4);
        
    private final int value;
    private UnitAllegiance(int val) {
        value = val;
    }

    public int getValue() {
        return value;
    }
        
    public boolean alliedWith(UnitAllegiance otherAllegiance) {
        return value == otherAllegiance.getValue() || value + otherAllegiance.getValue() == -1;
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
                barColor = new ColorRGBA(0.012f, 0.58f, 0.988f, 1f); //blue
                break;
        }
        
        return barColor;
    }
}
