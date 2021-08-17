/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.utils;

/**
 *
 * @author night
 */
public class DependentObjectComparator {
    public Object obj;
    
    public DependentObjectComparator(Object obj) {
        this.obj = obj;
    }
    
    @Override
    public boolean equals(Object o) {
        return o.equals(obj);
    }
}
