/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.fsm;

import java.util.Objects;

/**
 *
 * @author night
 * @param <E> enum state
 */
public class FsmState<E extends Enum> {
    protected E state;
    
    public FsmState(E e) {
        state = e;
        if (e == null) {
            throw new NullPointerException("Do not set FsmStates to null");
        }
    }
    
    public FsmState<E> setEnum(E st) {
        state = st;
        return this;
    }
    
    public E getEnum() {
        return state;
    }
    
    //OVERRIDE THIS IN SUBCLASSES
    public FsmState<E> copy() {
        return new FsmState<>(state);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof FsmState) {
            return state.getDeclaringClass() == ((FsmState)o).state.getDeclaringClass() && (super.equals(o) || state == ((FsmState<E>)o).state);
        }
        
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.state);
        return hash;
    }
}
