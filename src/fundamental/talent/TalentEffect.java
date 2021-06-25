/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.talent;

import battle.data.forecast.IndividualForecast;
import battle.data.forecast.SingularForecast;
import battle.participant.Combatant;
import fundamental.stats.BaseStat;
import battle.participant.BattleRole;
import etherealtempest.fsm.MasterFsmState;
import etherealtempest.info.Conveyor;
import fundamental.stats.Bonus;
import fundamental.stats.Bonus.BonusType;
import fundamental.stats.StatBundle;
import fundamental.stats.Toll;
import fundamental.stats.Toll.Exchange;
import fundamental.unit.AgainstUnits;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import maps.layout.Coords;
import maps.layout.MapLevel;
import maps.layout.MapCoords;
import maps.layout.occupant.VenturePeek;
import maps.layout.occupant.character.TangibleUnit;
import maps.layout.tile.Tile;

/**
 *
 * @author night
 */
public abstract class TalentEffect { //NOTE: ON ALL CONVEYOR, UNIT MUST BE SET TO THE USER
    private String desc = "";
    
    public TalentEffect(String desc) {
        this.desc = desc;
    }
    
    public TalentEffect() {}
    
    //OVERRIDE THIS WHEN NEEDED
    public Coords userTranslation(Conveyor data) {  // (x, y)
        return null;
    }
    
    //OVERRIDE THIS WHEN NEEDED
    protected List<Bonus> userBuffs(Conveyor data) { //buffs to self
        return new ArrayList<>();
    }
    
    //OVERRIDE THIS WHEN NEEDED
    public Toll userRestoration(Conveyor data) {
        return null;
    }
    
    //OVERRIDE THIS WHEN NEEDED
    public HashMap<TangibleUnit, Toll> calculateLoss(Conveyor data) { //hp, tp, or durability loss
        return new HashMap<>();
    }
    
    //OVERRIDE THIS WHEN NEEDED
    public HashMap<TangibleUnit, List<Bonus>> calculateStatModsToOthers(Conveyor data) { //buffs and debuffs; use this for conditional buffs on user if needed
        return new HashMap<>();
    }
    
    //OVERRIDE THIS WHEN NEEDED
    public HashMap<TangibleUnit, Coords> calculateTranslationToOthers(Conveyor data) { //translation; use this for conditional/specific translations on user if needed as well
        return new HashMap<>();
    }
    
    //OVERRIDE THIS WHEN NEEDED
    public void enactEffect(Conveyor info) { //this means visually
    
    }
    
    //OVERRIDE THIS WHEN NEEDED
    public <T extends IndividualForecast> void influenceForecasts(T self, T target, Conveyor data) {
    
    }
    
    public final List<Bonus> retrieveBuffs(Conveyor data) {
        List<Bonus> bonuses = userBuffs(data);
        Bonus.organizeList(bonuses);
        return bonuses;
    }
    
    //returns only the raw buffs
    public final List<StatBundle> getBuffsRaw(Conveyor data) {
        List<Bonus> buffs = retrieveBuffs(data);
        List<StatBundle> raw = new ArrayList<>();
        
        buffs.stream().filter((buff) -> (buff.getType() == BonusType.Raw)).forEachOrdered((buff) -> { //for each buff in buffs, if buff.getType() == BonusType.Raw, then raw.add(buff.toStatBundle());
            raw.add(buff.toStatBundle());
        });
        
        return raw;
    }
    
    public void setDescription(String description) {
        desc = description;
    }
    
    @Override
    public String toString() {
        return desc;
    }
    
    /**
     * AOE D over N spaces on H units = X% of user's Y stat
     * @param percent float with a domain of [-1, 1]. It represents X in the equation above
     * @param stat =BaseStat. It represents Y in the equaiton above
     * @param lossType Exchange. It represents the D in the equation above (damage type)
     * @param range int > 0. It represents the N in the equation above
     * @param applyTo AgainstUnits. It represents the H in the equation above 
     * @return the TalentEffect
     */
    public static TalentEffect PercentageStatBasedAOE(float percent, BaseStat stat, Exchange lossType, int range, AgainstUnits applyTo) {
        String desc = "does area-of-effect ";
        switch (lossType) {
            case HP:
                if (percent < 0) {
                    desc += "healing";
                } else if (percent > 0) {
                    desc += "damage";
                }
                break;
            case TP:
                desc += "TP";
            case Durability:
                desc += "equipped weapon durability";
            default:
                if (percent < 0) {
                    desc += "restoration";
                } else if (percent > 0) {
                    desc += "loss";
                }
                break;
        }
        
        desc += " = " + percent + "% of user's " + stat.getName() + " stat to " + applyTo.toString() + " units within " + range + " spaces of unit.";
        
        return new TalentEffect(desc) {
            @Override
            public HashMap<TangibleUnit, Toll> calculateLoss(Conveyor data) {
                int statValue = new Combatant(data.getUnit(), BattleRole.Initiator).getBaseStat(stat); //using a new Combatant just so i dont have to type all that stuff from the constructor
                Toll penalty = new Toll(lossType, (int)(percent * statValue));
                
                HashMap<TangibleUnit, Toll> loss = new HashMap<>();
                
                List<MapCoords> squares = VenturePeek.filledCoordsForTilesOfRange(range, data.getUnit().getPos());
                MapLevel map = MasterFsmState.getCurrentMap();
                for (MapCoords square : squares) {
                    Tile tile = map.getTileAt(square);
                    if (tile.isOccupied && applyTo.firstShouldApplyToSecond(data.getUnit(), tile.getOccupier())) {
                        loss.put(tile.getOccupier(), penalty);
                    }
                }
                    
                return loss;
            }
        };
    }
    
    public static TalentEffect Heal(Toll value) {
        return new TalentEffect("restores " + value.getValue() + " " + value.getType().toString() + ".") {
            @Override
            public Toll userRestoration(Conveyor data) {
                return value;
            }
        };
    }
    
    public static TalentEffect Bonuses(List<Bonus> bonuses) {
        String effect = "";
        
        List<Integer> skipping = new ArrayList<>();
        for (int i = 0; i < bonuses.size(); i++) {
            if (!skipping.contains(i)) {
                boolean iAdded = false;
                for (int j = i + 1; j < bonuses.size(); j++) {
                    if (!skipping.contains(j) && bonuses.get(j).getValue() == bonuses.get(i).getValue()) {
                        if (!iAdded) {
                            effect += bonuses.get(i).StatAsString() + "/";
                            iAdded = true;
                        }
                    
                        effect += bonuses.get(j).StatAsString() + "/";
                        skipping.add(j);
                    }
                }
            
                if (iAdded) {
                    effect = effect.substring(0, effect.length() - 1);
                    effect += " " + bonuses.get(i).getDeltaValueAsString() + ", ";
                } else {
                    effect += bonuses.get(i).StatBonusAsString() + ", ";
                }
            }
        }
        
        if (!bonuses.isEmpty()) {
            effect = effect.substring(0, effect.length() - 2);
        }
        
        return new TalentEffect(effect) {
            @Override
            public List<Bonus> userBuffs(Conveyor data) {
                return bonuses;
            }
        };
    }
    
    public static TalentEffect FoeCannotCounterattack() {
        return new TalentEffect("foe cannot counterattack.") {
            @Override
            public <T extends IndividualForecast> void influenceForecasts(T self, T target, Conveyor data) {
                if (self instanceof SingularForecast) {
                    ((SingularForecast)target).canCounterattack = false;
                }
            }
        };
    }
    
    public static TalentEffect ExtraDamage(int extraDMG) {
        return new TalentEffect("does +" + extraDMG + " extra damage.") {
            @Override
            public <T extends IndividualForecast> void influenceForecasts(T self, T target, Conveyor data) {
                if (self instanceof SingularForecast) {
                    SingularForecast forecast = ((SingularForecast)self);
                    forecast.setDisplayedDamage(forecast.getDisplayedDamage() + extraDMG);
                }
            }
        };
    }
    
}
