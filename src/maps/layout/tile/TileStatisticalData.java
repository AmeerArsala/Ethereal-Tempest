/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.tile;

import fundamental.stats.Bonus;
import fundamental.talent.Talent;

/**
 *
 * @author night
 */
public class TileStatisticalData { //USE GSON FOR THIS
    private int tileWeight = 10; //10 is default tileWeight
    private Bonus[] buffsAndOrDebuffs; //gson
    private Talent givenTalent = null; //gives unit a specific talent when standing on tile
    
    public TileStatisticalData(int tileWeight, Bonus[] buffsAndOrDebuffs, Talent givenTalent) {
        this.tileWeight = tileWeight;
        this.buffsAndOrDebuffs = buffsAndOrDebuffs;
        this.givenTalent = givenTalent;
    }
    
    public Bonus[] getBonuses() { return buffsAndOrDebuffs; }
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
    
    public void setTileWeight(int tWeight) {
        tileWeight = tWeight;
    }
    
    public void setBonuses(Bonus[] bonuses) {
        buffsAndOrDebuffs = bonuses;
    }
    
    public void setGivenTalent(Talent tal) {
        givenTalent = tal;
    }
}
