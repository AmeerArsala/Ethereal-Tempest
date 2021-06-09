/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.procedure;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author night
 * @param <I> input type
 * @param <O> output type
 */
public abstract class FunctionStack<I, O> {
    private final Map<Function<I, O>, Function<O, Boolean>> stackMap = new LinkedHashMap<>();
    
    public FunctionStack() {}
    
    public abstract O combineOutputs(List<O> outputs);
    
    //same as update(I input) except it doesn't remove any functions
    public O test(I input) {
        List<O> outputs = new ArrayList<>();
        stackMap.keySet().forEach((func) -> {
            outputs.add(func.apply(input));
        });
        
        return combineOutputs(outputs);
    }
    
    //tests function then removes it if its job is over
    public O update(I input) {
        O returnValue = test(input);
        
        stackMap.keySet().forEach((func) -> {
            if (stackMap.get(func).apply(returnValue)) {
                stackMap.remove(func); //removing during this forEach might cause error
            }
        });
        
        return returnValue;
    }
    
    public Function<I, O>[] getStack() {
        return (Function<I, O>[])stackMap.keySet().toArray();
    }
    
    /**
     *
     * @param func
     * @param onlyUseOnce if true, this function will only be used once and thrown away. If false, it will never be removed until the stack is cleared
     */
    public void addToStack(Function<I, O> func, boolean onlyUseOnce) {
        Function<O, Boolean> removeCondition = (output) -> { return onlyUseOnce; };
        stackMap.put(func, removeCondition);
    }
    
    public void addToStack(Function<I, O> func, Function<O, Boolean> removeCondition) {
        stackMap.put(func, removeCondition);
    }
    
    public void clear() {
        stackMap.clear();
    }
}
