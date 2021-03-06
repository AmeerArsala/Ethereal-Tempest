/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.talent;

import battle.Combatant;
import battle.Combatant.BaseStat;
import battle.Combatant.BattleRole;
import etherealtempest.MasterFsmState;
import etherealtempest.info.Conveyer;
import fundamental.stats.Bonus;
import fundamental.stats.Bonus.BonusType;
import fundamental.stats.StatBundle;
import fundamental.stats.Toll;
import fundamental.stats.Toll.Exchange;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import maps.layout.Coords;
import maps.layout.occupant.TangibleUnit;
import maps.layout.tile.Tile;

/**
 *
 * @author night
 */
public abstract class TalentEffect { //NOTE: ON ALL CONVEYER: UNIT MUST BE SET TO THE USER
    private final String desc;
    
    public TalentEffect(String desc) {
        this.desc = desc;
    }
    
    //OVERRIDE THIS WHEN NEEDED
    public Coords userTranslation(Conveyer data) {  // (x, y)
        return null;
    }
    
    //OVERRIDE THIS WHEN NEEDED
    protected List<Bonus> userBuffs(Conveyer data) { //buffs to self
        return new ArrayList<>();
    }
    
    //OVERRIDE THIS WHEN NEEDED
    public Toll userRestoration(Conveyer data) {
        return null;
    }
    
    //OVERRIDE THIS WHEN NEEDED
    public HashMap<TangibleUnit, Toll> calculateLoss(Conveyer data) { //hp, tp, or durability loss
        return new HashMap<>();
    }
    
    //OVERRIDE THIS WHEN NEEDED
    public HashMap<TangibleUnit, List<Bonus>> calculateStatModsToOthers(Conveyer data) { //buffs and debuffs; use this for conditional buffs on user if needed
        return new HashMap<>();
    }
    
    //OVERRIDE THIS WHEN NEEDED
    public HashMap<TangibleUnit, Coords> calculateTranslationToOthers(Conveyer data) { //translation; use this for conditional/specific translations on user if needed as well
        return new HashMap<>();
    }
    
    //OVERRIDE THIS WHEN NEEDED
    public void enactEffect(Conveyer info) { //this means visually
    
    }
    
    public final List<Bonus> retrieveBuffs(Conveyer data) {
        List<Bonus> bonuses = userBuffs(data);
        Bonus.organizeList(bonuses);
        return bonuses;
    }
    
    public final List<StatBundle> getBuffsRaw(Conveyer data) {
        List<Bonus> buffs = retrieveBuffs(data);
        List<StatBundle> raw = new ArrayList<>();
        
        buffs.stream().filter((buff) -> (buff.getType() == BonusType.Raw)).forEachOrdered((buff) -> { //for each buff in buffs, if buff.getType() == BonusType.Raw, then raw.add(buff.toStatBundle());
            raw.add(buff.toStatBundle());
        });
        
        return raw;
    }
    
    @Override
    public String toString() {
        return desc;
    }
    
    /**
     * AOE D over N spaces = X% of user's Y stat
     * @param percent this is an int > 0. It represents X in the equation above
     * @param stat this is the BaseStat. It represents Y in the equaiton above
     * @param lossType this is the Exchange. It represents the D in the equation above (damage type)
     * @param range this is an int > 0. It represents the N in the equation above
     * @return the TalentEffect
     */
    public static TalentEffect PercentageStatBasedAOE(int percent, BaseStat stat, Exchange lossType, int range) {
        return new TalentEffect("does area-of-effect damage = " + percent + "% of user's " + stat.getName() + " stat to enemies within " + range + " spaces of the targeted enemy.") {
            @Override
            public HashMap<TangibleUnit, Toll> calculateLoss(Conveyer data) {
                int statValue = new Combatant(data.getUnit(), BattleRole.Initiator).getBaseStat(stat); //using a new Combatant just so i dont have to type all that stuff from the constructor
                Toll penalty = new Toll(lossType, (int)((percent / 100f) * statValue));
                
                HashMap<TangibleUnit, Toll> loss = new HashMap<>();
                for (int var = -1; var < 2; var += 2) {
                    for (int xUsed = 1; xUsed <= 3; xUsed++) {
                        for (int yUsed = 1; yUsed <= 3 && xUsed + yUsed <= 3; yUsed++) {
                            Tile[][] tilesX = MasterFsmState.getCurrentMap().fullmap[data.getEnemyUnit().getElevation()];
                            int enemyPosX = data.getEnemyUnit().getPosX(), enemyPosY = data.getEnemyUnit().getPosY();
                            if (!data.getUnit().unitStatus.alliedWith(tilesX[enemyPosX + (xUsed * var)][enemyPosY + (yUsed * var)].getOccupier().unitStatus)) {
                                //if they are an enemy
                                loss.putIfAbsent(data.getEnemyUnit(), penalty);
                            }
                        }
                    }
                }
                    
                return loss;
            }
        };
    }
    
    public static TalentEffect Heal(Toll value) {
        return new TalentEffect("restores " + value.getValue() + " " + value.getType().toString() + ".") {
            @Override
            public Toll userRestoration(Conveyer data) {
                return value;
            }
        };
    }
    
}
