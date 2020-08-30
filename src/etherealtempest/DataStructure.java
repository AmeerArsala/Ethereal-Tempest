/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public abstract class DataStructure {
    private List<Runnable> tasks = new ArrayList<>();
    
    public DataStructure() {}
    
    public List<Runnable> onSelect() {
        return tasks;
    }
    
    public DataStructure setTasks(List<Runnable> RS) {
        tasks = RS;
        return this;
    }
    
    public DataStructure addOnSelect(Runnable task) {
        tasks.add(task);
        return this;
    }
}
