/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.utils;

/**
 *
 * @author night
 * @param <T> type
 */
public class WithBool<T> {
    public final T item;
    public final boolean modifierBool;
    
    public WithBool(T item, boolean modifierBool) {
        this.item = item;
        this.modifierBool = modifierBool;
    }
    
    //sets the parameter to item and returns modifierBool. Niche use but has its uses in making code look clean
    public boolean setParameter(T param) {
        param = item;
        return modifierBool;
    }
}
