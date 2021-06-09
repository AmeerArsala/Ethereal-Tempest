/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.procedure;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author night
 */
public class SimpleQueue {
    private final List<SimpleProcedure> procedures = new LinkedList<>();
    private final ArrayList<SimpleProcedure> finished = new ArrayList<>();
    
    public SimpleQueue() {}
    
    public void update(float tpf) {
        procedures.forEach((procedure) -> {
            boolean done = procedure.update(tpf);
            if (done) {
                finished.add(procedure);
            }
        });
        
        procedures.removeAll(finished);
        finished.clear();
    }
    
    public void addToQueue(SimpleProcedure procedure) {
        procedures.add(procedure);
    }
    
    public List<SimpleProcedure> getProcedures() {
        return procedures;
    }
    
    public boolean isEmpty() {
        return procedures.isEmpty();
    }
    
    public void clear() {
        procedures.clear();
    }
}
