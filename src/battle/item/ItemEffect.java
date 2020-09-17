/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.item;

import fundamental.Toll;
import battle.participants.Unit;
import etherealtempest.info.Conveyer;
import fundamental.Bonus;
import fundamental.Tool.ToolType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public abstract class ItemEffect {
    private ToolType type;
    private int radius = 0;
    private final int desirability;
    
    public ItemEffect(ToolType type, int desirability) {
        this.type = type;
        this.desirability = desirability;
    }
    
    public ItemEffect setRadius(int rad) {
        radius = rad;
        return this;
    }
    
    public abstract List<Toll> restoration(Unit unit);
    public abstract List<Bonus> bonuses();
    
    public abstract void enactEffect(Conveyer C); //this is also for learning a skill or talent or formula or ability, also make requests on here
    public abstract boolean canBeUsed(Conveyer C);
    
    public void executeEffect(Conveyer C) {
        Unit character = C.getUnit();
        
        restoration(character).forEach((part) -> {
            character.restore(part);
        });
        
        character.getBonuses().addAll(bonuses());
        
        enactEffect(C);
    }
    
    public ToolType getType() { return type; }
    
    public void setType(ToolType toolType) { //for alchemy talent
        type = toolType;
    }
    
    public List<Integer> getRadius() {
        List<Integer> radiusList = new ArrayList<>();
        
        for (int i = 1; i <= radius; i++) {
            radiusList.add(i);
        }
        
        return radiusList;
    }
    
    public int getDesirability() {
        return desirability;
    }
    
}
