/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.procedure.functional;

import java.util.function.Consumer;

/**
 *
 * @author night
 * @param <T> parameter
 */
public abstract class NamedProcess<T> {
    private final String name;
    
    public NamedProcess(String name) {
        this.name = name;
    }
    
    public abstract void run(T t);
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return "Process " + name + "; Object String: " + super.toString();
    }
    
    public static <T> NamedProcess<T> fromConsumer(String name, Consumer<T> procedure) {
        return new NamedProcess<T>(name) {
            @Override
            public void run(T t) {
                procedure.accept(t);
            }
        };
    }
}
