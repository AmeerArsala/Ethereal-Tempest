/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.ability;

/**
 *
 * @author night
 */
public class Ability {
    private String name = "", desc = "";
    public boolean exists = true;
    
    public Ability(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }
    
    public Ability(boolean exi) { exists = exi; }
    
    public String getName() { return name; }
    public String getDescription() { return desc; } 
    
    @Override
    public String toString() { return name; }
    
}
