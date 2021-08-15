/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.utils.wrapper;

/**
 *
 * @author night
 * @param <A> type 1
 * @param <B> type 2
 * @param <C> type 3
 */
public class IdentifiedTrio<A, B, C> {
    public Generic<A> obj1;
    public Generic<B> obj2;
    public Generic<C> obj3;
    
    public IdentifiedTrio(Generic<A> obj1, Generic<B> obj2, Generic<C> obj3) {
        this.obj1 = obj1;
        this.obj2 = obj2;
        this.obj3 = obj3;
    }
    
    public IdentifiedTrio(A a, B b, C c) {
        obj1 = new Generic<>(a, "A");
        obj2 = new Generic<>(b, "B");
        obj3 = new Generic<>(c, "C");
    }
    
    public Generic[] asArray() {
        return new Generic[]{obj1, obj2, obj3};
    }
    
    public Object get(String what) {
        Generic[] arr = asArray();
        for (int i = 0; i < arr.length; ++i) {
            Generic obj = arr[i];
            if (what.equals(obj.getName())) {
                return obj.getItem();
            }
        }
        
        return null;
    }
    
    public A getA() {
        return obj1.getItem();
    }
    
    public B getB() {
        return obj2.getItem();
    }
    
    public C getC() {
        return obj3.getItem();
    }
    
    public String getNameOfA() {
        return obj1.getName();
    }
    
    public String getNameOfB() {
        return obj2.getName();
    }
    
    public String getNameOfC() {
        return obj3.getName();
    }
}
