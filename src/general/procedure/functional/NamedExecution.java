/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.procedure.functional;

/**
 *
 * @author night
 */
public abstract class NamedExecution {
    private final String name;
    
    public NamedExecution(String name) {
        this.name = name;
    }
    
    public abstract void execute();
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return "Execution " + name + "; Object String: " + super.toString();
    }
    
    
    public static NamedExecution fromRunnable(String name, Runnable runnable) {
        return new NamedExecution(name) {
            @Override
            public void execute() {
                runnable.run();
            }
        };
    }
}
