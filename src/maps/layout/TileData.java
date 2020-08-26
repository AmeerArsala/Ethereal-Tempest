/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout;

import maps.layout.TangibleUnit.UnitStatus;
import com.google.gson.annotations.SerializedName;
import fundamental.Bonus;

/**
 *
 * @author night
 */
public class TileData {
    //all types can have extra options if need be, like a ballista (TODO: add List<TileOption> to tile)
    public enum TileType {
        @SerializedName("Normal") Normal, 
        @SerializedName("Annex") Annex,
        @SerializedName("Escape") Escape,
        @SerializedName("Save") Save
    }
    
    private TileTypeData pivotalFunction;
    private int tileWeight = 10; //10 is default tileWeight
    private Bonus[] buffsAndOrDebuffs;
    
    public TileData(TileTypeData pivotalFunction) {
        this.pivotalFunction = pivotalFunction;
    }
    
    public TileData(TileType type, UnitStatus eligibleAllegiance) {
        pivotalFunction = new TileTypeData(type, eligibleAllegiance);
    }
    
    private class TileTypeData {
        private TileType type;
        private UnitStatus forAllegiance;
        
        public TileTypeData(TileType type, UnitStatus forAllegiance) {
            this.type = type;
            this.forAllegiance = forAllegiance;
        }
        
        public TileType getType() { return type; }
        public UnitStatus getElegibleAllegiance() { return forAllegiance; }
    }
    
    public TileType getType() { return pivotalFunction.type; }
    public UnitStatus getElegibleAllegiance() { return pivotalFunction.forAllegiance; }
    
    public Bonus[] getBonuses() { return buffsAndOrDebuffs; }
    public int getTileWeight() { return tileWeight; } 
    /* each unit will have a hidden Resolve stat
     * there movement reduction would be MOBILITY - (tileWeight - Resolve)
     * yes, this means if the unit has a higher Resolve than the tileWeight, they will move further
     * but if the unit has a lower Resolve than tileWeight, they won't move as far. This will be used to make forests penalize movement like regular FE
     * the Resolve stat will have baselines for infantry, cavalry, armored, etc.
     * the Resolve stat will NOT be factored in at all for units that are on flying mounts, they can traverse any terrain
     * but that also means they (fliers) don't get bonuses at all, even to mobility
     * like Biorhythms, Resolve won't be constant between some chapters
     * some units will get a bonus or penalty to the stat during them due to a story event
     * standard tileWeight = 10
     */
}
