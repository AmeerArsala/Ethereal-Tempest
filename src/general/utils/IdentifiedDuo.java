/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.utils;

//import com.google.gson.annotations.SerializedName;

/**
 *
 * @author night
 * @param <A> type 1
 * @param <B> type 2
 */
public class IdentifiedDuo<A, B> {
    public Generic<A> obj1;
    public Generic<B> obj2;
    
    public IdentifiedDuo(Generic<A> obj1, Generic<B> obj2) {
        this.obj1 = obj1;
        this.obj2 = obj2;
    }
    
    public IdentifiedDuo(A a, B b) {
        obj1 = new Generic<>(a, "A");
        obj2 = new Generic<>(b, "B");
    }
    
    public Object get(String what) {
        if (what.equals(obj1.getName())) {
            return obj1.getItem();
        }
        
        if (what.equals(obj2.getName())) {
            return obj2.getItem();
        }
        
        return null;
    }
    
    public A getA() {
        return obj1.getItem();
    }
    
    public B getB() {
        return obj2.getItem();
    }
    
    public String getNameOfA() {
        return obj1.getName();
    }
    
    public String getNameOfB() {
        return obj2.getName();
    }
}
