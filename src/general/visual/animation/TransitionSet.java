/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.visual.animation;

import com.jme3.scene.Spatial;
import java.util.HashMap;
import java.util.Objects;

/**
 *
 * @author night
 */
public class TransitionSet {
    public enum TransitionState {
        Standby,
        TransitioningOut,
        TransitioningIn,
    }
    
    public enum TransitionType {
        OnSelect,
        OnDeselect,
        None
    }
    
    private class TransitionKey {
        public TransitionState state;
        public TransitionType type;
        
        public TransitionKey(TransitionState tState, TransitionType tType) {
            state = tState;
            type = tType;
        }
        
        @Override
        public boolean equals(Object o) {
            if (o instanceof TransitionKey) {
                return super.equals(o) || (state == ((TransitionKey)o).state && type == ((TransitionKey)o).type);
            }
            
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 17 * hash + Objects.hashCode(this.state);
            hash = 17 * hash + Objects.hashCode(this.type);
            return hash;
        }
    }
    
    //private final boolean transitionsOnSelectAndDeselectAreTheSameButReversed; //zoom in for forward = zoom out for backward
    //private final Animation[] transitionInOnSelect; //example: select an option and this menu pops up
    //private final Animation[] transitionOutOnSelect; //example: select an option and this menu disappears
    //private final Animation[] transitionInOnDeselect; //example: deselect and this menu pops up
    //private final Animation[] transitionOutOnDeselect; //example: deselect and this menu goes away
    
    private final HashMap<TransitionState, VisualTransition> transitionEventMap = new HashMap<>();
    private final HashMap<TransitionKey, Animation[]> transitionMap = new HashMap<>();
    private final TransitionKey key = new TransitionKey(TransitionState.Standby, TransitionType.None);
    
    public TransitionSet(
        Animation[] transitionInOnSelect, Animation[] transitionOutOnSelect,
        Animation[] transitionInOnDeselect, Animation[] transitionOutOnDeselect
    ) {
        transitionMap.put(new TransitionKey(TransitionState.TransitioningIn, TransitionType.OnSelect), transitionInOnSelect);
        transitionMap.put(new TransitionKey(TransitionState.TransitioningOut, TransitionType.OnSelect), transitionOutOnSelect);
        transitionMap.put(new TransitionKey(TransitionState.TransitioningIn, TransitionType.OnDeselect), transitionInOnDeselect);
        transitionMap.put(new TransitionKey(TransitionState.TransitioningOut, TransitionType.OnDeselect), transitionOutOnDeselect);
    }
    
    public TransitionSet(Animation[] transitionInOnSelect, Animation[] transitionOutOnSelect) {
        
        Animation[] transitionInOnDeselect = new Animation[transitionInOnSelect.length];
        for (int i = 0; i < transitionInOnDeselect.length; ++i) {
            transitionInOnDeselect[i] = Animation.reverse(transitionInOnSelect[i]);
        }
        
        Animation[] transitionOutOnDeselect = new Animation[transitionOutOnSelect.length];
        for (int i = 0; i < transitionOutOnDeselect.length; ++i) {
            transitionOutOnDeselect[i] = Animation.reverse(transitionOutOnSelect[i]);
        }
        
        transitionMap.put(new TransitionKey(TransitionState.TransitioningIn, TransitionType.OnSelect), transitionInOnSelect);
        transitionMap.put(new TransitionKey(TransitionState.TransitioningOut, TransitionType.OnSelect), transitionOutOnSelect);
        transitionMap.put(new TransitionKey(TransitionState.TransitioningIn, TransitionType.OnDeselect), transitionInOnDeselect);
        transitionMap.put(new TransitionKey(TransitionState.TransitioningOut, TransitionType.OnDeselect), transitionOutOnDeselect);
        
        //transitionsOnSelectAndDeselectAreTheSameButReversed = true;
    }
    
    public void initializeEvents(Spatial focus) {
        transitionEventMap.put(TransitionState.TransitioningIn, new VisualTransition(focus));
        transitionEventMap.put(TransitionState.TransitioningOut, new VisualTransition(focus));
    }
    
    public void onFinish(TransitionState state, Runnable onFinish) {
        transitionEventMap.get(state).onFinishTransitions(() -> {
                onFinish.run();
                key.state = TransitionState.Standby;
                key.type = TransitionType.None;
            }
        );
    }
    
    public Animation[] getTransitions(TransitionState state, TransitionType type) {
        return transitionMap.get(new TransitionKey(state, type));
    }
    
    public boolean transitionInProgress() { return key.state != TransitionState.Standby; }
    
    public void begin(TransitionState state, TransitionType type) {
        key.state = state;
        key.type = type;
        transitionEventMap.get(state).beginTransitions(transitionMap.get(key));
    }
    
    public void update(float tpf) {
        if (transitionInProgress()) {
            transitionEventMap.get(key.state).update(tpf);
        }
    }
    
    public void forceEndAll() {
        for (VisualTransition VT : transitionEventMap.values()) {
            VT.forceEnd();
        }
        
        key.state = TransitionState.Standby;
        key.type = TransitionType.None;
    }
}
