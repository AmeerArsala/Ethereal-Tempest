/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.item;

import battle.Combatant;
import etherealtempest.info.Conveyer;
import fundamental.ability.AbilityEffect;
import fundamental.stats.Toll;
import fundamental.stats.Toll.Exchange;
import fundamental.talent.TalentEffect;
import fundamental.tool.Tool.ToolType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public abstract class ItemEffect { //when used
    private ToolType type;
    
    private final int radius;
    private final int desirability;
    
    protected TalentEffect effect = null;
    protected AbilityEffect effect2 = null;
    
    private ItemEffect(ToolType type, int desirability) {
        this.type = type;
        this.desirability = desirability;
        
        radius = 0;
    }
    
    private ItemEffect(ToolType type, int desirability, int radius) {
        this.type = type;
        this.desirability = desirability;
        this.radius = radius;
    }
    
    public ItemEffect(ToolType type, int desirability, TalentEffect effect) {
        this(type, desirability);
        this.effect = effect;
    }
    
    public ItemEffect(ToolType type, int desirability, int radius, TalentEffect effect) {
        this(type, desirability, radius);
        this.effect = effect;
    }
    
    public ItemEffect(ToolType type, int desirability, AbilityEffect effect2) {
        this(type, desirability);
        this.effect2 = effect2;
    }
    
    public ItemEffect(ToolType type, int desirability, int radius, AbilityEffect effect2) {
        this(type, desirability, radius);
        this.effect2 = effect2;
    }
    
    public ItemEffect(ToolType type, int desirability, TalentEffect effect, AbilityEffect effect2) {
        this(type, desirability);
        this.effect = effect;
        this.effect2 = effect2;
    }
    
    public ItemEffect(ToolType type, int desirability, int radius, TalentEffect effect, AbilityEffect effect2) {
        this(type, desirability, radius);
        this.effect = effect;
        this.effect2 = effect2;
    }
    
    public abstract boolean canBeUsed(Conveyer C);
    
    public void executeEffect(Conveyer C) {
        if (effect != null) {
            effect.enactEffect(C);
        }
        
        if (effect2 != null) {
            effect2.enactEffect(C);
        }
    }
    
    public ToolType getType() { return type; }
    
    public int getDesirability() { return desirability; }
    
    public TalentEffect getTalentEffect() { return effect; }
    public AbilityEffect getAbilityEffect() { return effect2; }
    
    public List<Integer> getRadius() {
        List<Integer> radiusList = new ArrayList<>();
        
        for (int i = 1; i <= radius; i++) {
            radiusList.add(i);
        }
        
        return radiusList;
    }
    
    public void setType(ToolType toolType) { //for alchemy talent
        type = toolType;
    }
    
    
    public static ItemEffect Restore(Exchange stat, int value) {
        return new ItemEffect(ToolType.SupportSelf, 100, TalentEffect.Heal(new Toll(stat, value))) {
            @Override
            public boolean canBeUsed(Conveyer C) {
                return C.getUnit().getStat(Combatant.BaseStat.currentHP) < C.getUnit().getStat(Combatant.BaseStat.maxHP);
            }
        };
    }
    
}
