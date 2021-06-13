/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.procedure;

import general.procedure.functional.SimpleProcedure;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author night
 */
public class SimpleOrdinalQueue {
    private final LinkedList<SimpleProcedure> procedures = new LinkedList<>();
    private Runnable onCurrentQueueFinished = () -> {};
    
    public SimpleOrdinalQueue() {
        
    }
    
    public void update(float tpf) {
        if (!procedures.isEmpty()) {
            boolean finished = procedures.getFirst().update(tpf);
            
            if (finished) {
                procedures.removeFirst();
                
                if (procedures.isEmpty()) {
                    onCurrentQueueFinished.run();
                    onCurrentQueueFinished = () -> {};
                }
            }
        }
    }
    
    public List<SimpleProcedure> getProcedures() {
        return procedures;
    }
    
    public SimpleProcedure getCurrentProcedure() {
        return procedures.getFirst();
    }
    
    public boolean isEmpty() {
        return procedures.isEmpty();
    }
    
    public void addToQueue(SimpleProcedure procedure) {
        procedures.add(procedure);
    }
    
    public void onCurrentQueueFinished(Runnable onFinish) {
        onCurrentQueueFinished = onFinish;
    }
    
    public void clear() {
        procedures.clear();
    }
}
