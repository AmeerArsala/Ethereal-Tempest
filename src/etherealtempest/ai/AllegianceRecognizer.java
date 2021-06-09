/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.ai;

import maps.layout.occupant.character.TangibleUnit;

/**
 *
 * @author night
 */
public abstract class AllegianceRecognizer {
    private AllegianceRecognizer linked = null;
    
    public abstract boolean allows(TangibleUnit tu);
    
    public AllegianceRecognizer addExtra(AllegianceRecognizer AR) {
        linked = AR;
        return this;
    }
    
    public boolean passesTest(TangibleUnit tu) {
        if (linked != null) {
            return allows(tu) && linked.passesTest(tu);
        }
        
        return allows(tu);
    }
}
