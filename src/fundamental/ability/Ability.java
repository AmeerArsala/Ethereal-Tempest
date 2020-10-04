/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.ability;

import etherealtempest.info.Conveyer;
import fundamental.Associated;
import fundamental.tool.Tool.ToolType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class Ability extends Associated {
    private AbilityEffect effect;
    private List<Integer> ranges; 
    private ToolType type;
    
    public Ability(String name, String desc, int radius, ToolType type, AbilityEffect effect) {
        super(name, desc);
        this.type = type;
        this.effect = effect;
        ranges = new ArrayList<>();
        for (int i = 1; i <= radius; i++) {
            ranges.add(i);
        }
    }
    
    public Ability(boolean exi) {
        super(exi);
    }
    
    public boolean canBeUsed(Conveyer data) {
        return effect.mayBeUsed(data);
    }
    
    public void enactEffect(Conveyer data) {
        effect.enactEffect(data);
    }
    
    public int getDesirability(Conveyer data) {
        return effect.calculateFavorability(data);
    }
    
    public List<Integer> getRadius() {
        return ranges;
    }
    
    public ToolType getType() {
        return type;
    }
    
}
