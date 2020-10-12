/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.tile;

import com.google.gson.annotations.SerializedName;
import maps.layout.occupant.TangibleUnit.UnitStatus;

/**
 *
 * @author night
 */
public class TileOptionData {
    //all types can have extra options if need be, like a ballista (TODO: add List<TileOption> to tile)
    public enum TileType {
        @SerializedName("Normal") Normal, 
        @SerializedName("Transporter") Transport,
        @SerializedName("Annexable") Annex,
        @SerializedName("Escape") Escape,
        @SerializedName("Save") Save
    }
    
    private TileType type;
    private UnitStatus[] forAllegiances;
        
    public TileOptionData(TileType type, UnitStatus forAllegiances[]) {
        this.type = type;
        this.forAllegiances = forAllegiances;
    }
    
    public TileType getFunctionType() { return type; }
    public UnitStatus[] getEligibleAllegiances() { return forAllegiances; }
    
    public boolean allegianceIsEligible(UnitStatus allegiance) {
        for (UnitStatus st : forAllegiances) {
            if (st == allegiance) {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean alliedAllegianceIsEligible(UnitStatus allegiance) {
        for (UnitStatus st : forAllegiances) {
            if (st.alliedWith(allegiance)) {
                return true;
            }
        }
        
        return false;
    }
}
