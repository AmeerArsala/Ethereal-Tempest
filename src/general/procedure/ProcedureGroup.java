/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.procedure;

import general.procedure.functional.SimpleProcedure;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author night
 */
public class ProcedureGroup extends SimpleProcedureGroup {
    public static abstract class CustomProcedure implements SimpleProcedure {
        public final Runnable onFinish;
        
        public CustomProcedure(Runnable onFinish) {
            this.onFinish = onFinish;
        }
    }
    
    public ProcedureGroup() {}
    
    @Override
    public void update(float tpf) {
        for (int i = 0, len = procedures.size(); i < len; ++i) {
            CustomProcedure procedure = (CustomProcedure)procedures.get(i);
            boolean done = procedure.update(tpf);
            if (done) {
                finished.add(procedure);
                procedure.onFinish.run();
            }
        }
        
        procedures.removeAll(finished);
        finished.clear();
    }
    
    @Override
    public void add(SimpleProcedure procedure) {
        procedures.add(new CustomProcedure(() -> {}) {
            @Override
            public boolean update(float tpf) {
                return procedure.update(tpf);
            }
        });
    }
    
    public void add(SimpleProcedure procedure, Runnable onFinish) {
        procedures.add(new CustomProcedure(onFinish) {
            @Override
            public boolean update(float tpf) {
                return procedure.update(tpf);
            }
        });
    }
    
    public void add(CustomProcedure procedure) {
        procedures.add(procedure);
    }
}
