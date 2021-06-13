/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.procedure;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 *
 * @author night
 * @param <I> input type
 */
public class ReactorGroup<I> { //all procedures react to an input
    private final List<Function<I, Boolean>> procedures = new LinkedList<>();
    private final ArrayList<Function<I, Boolean>> finished = new ArrayList<>();
    
    public ReactorGroup() {}
    
    public void update(I input) {
        procedures.forEach((procedure) -> {
            boolean done = procedure.apply(input);
            if (done) {
                finished.add(procedure);
            }
        });
        
        procedures.removeAll(finished);
        finished.clear();
    }
    
    public void add(Function<I, Boolean> func) {
        procedures.add(func);
    }
    
    public List<Function<I, Boolean>> getProcedures() {
        return procedures;
    }
}
