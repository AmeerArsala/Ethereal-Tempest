/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest;

/**
 *
 * @author night
 * @param <E> enum state
 */
public class FsmState<E> {
    
    protected E state;
    
    public FsmState setEnum(E st) {
        state = st;
        return this;
    }
    
    public FsmState() {}
    
    public FsmState(E e) {
        state = e;
    }
    
    public E getEnum() {
        return state;
    }
    
}
