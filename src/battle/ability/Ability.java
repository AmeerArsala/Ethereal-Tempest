/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.ability;

import fundamental.Associated;

/**
 *
 * @author night
 */
public class Ability extends Associated {
    
    public Ability(String name, String desc) {
        super(name, desc);
    }
    
    public Ability(boolean exi) {
        super(exi);
    }
    
}
