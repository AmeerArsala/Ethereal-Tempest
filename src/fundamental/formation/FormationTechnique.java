/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.formation;

import etherealtempest.info.Conveyer;

/**
 *
 * @author night
 */
public abstract class FormationTechnique {
    private final String name, description;
    private final int desirability;
    
    public FormationTechnique(String name, String description, int desirability) {
        this.name = name;
        this.description = description;
        this.desirability = desirability;
    }
    
    public abstract boolean getCondition(Conveyer data);
    public abstract void useTechnique(Conveyer data);
    
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getDesirability() { return desirability; }
}
