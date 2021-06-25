/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.fsm;

/**
 *
 * @author night
 * @param <E> enum state
 */
public class FsmState<E extends Enum> {
    protected E state = null, lastState = null;
    
    public FsmState() {}
    
    public FsmState(E e) {
        state = e;
    }
    
    public FsmState(E e, E l) {
        state = e;
        lastState = l;
    }
    
    public FsmState<E> setEnum(E st) {
        state = st;
        return this;
    }
    
    public FsmState<E> setLastEnum(E last) {
        lastState = last;
        return this;
    } 
    
    public E getEnum() {
        return state;
    }
    
    public E getLastEnum() {
        return lastState;
    }
}
