/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.tile;

import com.google.gson.Gson;
import maps.layout.TangibleUnit.UnitStatus;
import com.google.gson.annotations.SerializedName;
import fundamental.stats.Bonus;
import fundamental.talent.Talent;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class TileData {
    //all types can have extra options if need be, like a ballista (TODO: add List<TileOption> to tile)
    public enum TileType {
        @SerializedName("Normal") Normal, 
        @SerializedName("Transporter") Transport,
        @SerializedName("Annexable") Annex,
        @SerializedName("Escape") Escape,
        @SerializedName("Save") Save
    }
    
    private TileTypeData pivotalFunction;
    private String tileName = "Ground";
    
    private int tileWeight = 10; //10 is default tileWeight
    private List<Bonus> buffsAndOrDebuffs = new ArrayList<>(); //gson?
    private Talent givenTalent = null; //gives unit a specific talent when standing on tile
    
    public TileData(TileTypeData pivotalFunction, List<Bonus> bonuses) {
        this.pivotalFunction = pivotalFunction;
        buffsAndOrDebuffs.addAll(bonuses);
    }
    
    public TileData(TileTypeData pivotalFunction, List<Bonus> bonuses, Talent givenTalent) {
        this.pivotalFunction = pivotalFunction;
        this.givenTalent = givenTalent;
        buffsAndOrDebuffs.addAll(bonuses);
    }
    
    public TileData(TileType type, UnitStatus eligibleAllegiance, List<Bonus> bonuses) {
        pivotalFunction = new TileTypeData(type, eligibleAllegiance);
        buffsAndOrDebuffs.addAll(bonuses);
    }
    
    public TileData(TileType type, UnitStatus eligibleAllegiance, List<Bonus> bonuses, Talent givenTalent) {
        this.givenTalent = givenTalent;
        pivotalFunction = new TileTypeData(type, eligibleAllegiance);
        buffsAndOrDebuffs.addAll(bonuses);
    }
    
    private TileData setTileTypeData(TileTypeData TTD) {
        pivotalFunction = TTD;
        return this;
    }
    
    private class TileTypeData {
        private TileType type;
        private UnitStatus forAllegiance;
        
        public TileTypeData(TileType type, UnitStatus forAllegiance) {
            this.type = type;
            this.forAllegiance = forAllegiance;
        }
    }
    
    public TileType getType() { return pivotalFunction.type; }
    public UnitStatus getElegibleAllegiance() { return pivotalFunction.forAllegiance; }
    
    public String getTileName() { return tileName; }
    
    public List<Bonus> getBonuses() { return buffsAndOrDebuffs; }
    public Talent getGivenTalent() { return givenTalent; }
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
    
    //forest tileWeight = 15
    //private final int INFANTRY_RESOLVE = 13, CAVALRY_RESOLVE = 11, ARMORED_RESOLVE = 15, MONSTER_RESOLVE = 14, MORPH_RESOLVE = 13, MECHANISM_RESOLVE = 12;
    
    public static TileData deriveFromPreset(TileTypeData pivotalFunction, String presetName) {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\GameInfo\\presets\\Tiles\\" + presetName + ".json"));
            return gson.fromJson(reader, TileData.class).setTileTypeData(pivotalFunction);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static TileData generatePreset(String presetName) {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\GameInfo\\presets\\Tiles\\" + presetName + ".json"));
            return gson.fromJson(reader, TileData.class);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
