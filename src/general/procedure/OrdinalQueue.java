/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.procedure;

import general.procedure.functional.UpdateCommand;
import general.procedure.functional.UpdateLoop;
import com.simsilica.lemur.Command;
import java.util.LinkedList;
import java.util.function.Predicate;

/**
 *
 * @author night
 * @param <T> type 
 */
public class OrdinalQueue<T> {
    private class Lock {
        public final Predicate<T> lock;
        public boolean unlocked = false;
        
        public Lock(Predicate<T> lock) {
            this.lock = lock;
        }
        
        public boolean attemptUnlock(T focus) {
            if (unlocked) {
                return true;
            }
            
            unlocked = lock.test(focus);
            
            return unlocked;
        }
    }
    
    private class Task {
        public final T focus;
        public final UpdateLoop onUpdate;
        public final Runnable onFinish;
        public final Lock lock; //will not run task until unlocked
        
        public Task(T focus, UpdateLoop onUpdate, Runnable onFinish, Predicate<T> unlockCondition) {
            this.focus = focus;
            this.onUpdate = onUpdate;
            this.onFinish = onFinish;
            lock = new Lock(unlockCondition);
        }
        
        public boolean update(float tpf) {
            if (!lock.attemptUnlock(focus)) {
                return false;
            }
            
            update.execute(focus, tpf);
            
            if (taskFinished.test(focus)) {
                onFinish.run();
                return true;
            }
            
            return false;
        }
    }
    
    private final LinkedList<Task> tasks = new LinkedList<>();
    private final UpdateCommand<T> update;
    private final Predicate<T> taskFinished;
    
    private Runnable onCurrentQueueFinished = () -> {};
    
    public OrdinalQueue(UpdateCommand<T> update, Predicate<T> taskFinished) {
        this.update = update;
        this.taskFinished = taskFinished;
    }
    
    public boolean isFinished() { 
        return tasks.isEmpty(); 
    }
    
    public T getCurrentTask() {
        return !tasks.isEmpty() ? tasks.getFirst().focus : null;
    }
    
    public void update(float tpf) {
        if (!isFinished()) {
            boolean done = tasks.getFirst().update(tpf);
            
            if (done) {
                tasks.removeFirst();
                
                if (tasks.isEmpty()) {
                    onCurrentQueueFinished.run();
                    onCurrentQueueFinished = () -> {};
                }
            }
        }
    }
    
    public void addToQueue(T task, UpdateLoop onUpdate, Runnable onFinish, Predicate<T> unlockCondition) {
        tasks.add(new Task(task, onUpdate, onFinish, unlockCondition));
    }
    
    public void addToQueue(T task, UpdateLoop onUpdate, Predicate<T> unlockCondition) {
        tasks.add(new Task(task, onUpdate, () -> {}, unlockCondition));
    }
    
    public void addToQueue(T task, UpdateLoop onUpdate, Runnable onFinish) {
        tasks.add(new Task(task, onUpdate, onFinish, (t) -> { return true; }));
    }
    
    public void addToQueue(T task, UpdateLoop onUpdate) {
        tasks.add(new Task(task, onUpdate, () -> {}, (t) -> { return true; }));
    }
    
    public void addToQueue(T task, Predicate<T> unlockCondition) {
        tasks.add(new Task(task, (tpf) -> {}, () -> {}, unlockCondition));
    }
    
    public void addToQueue(T task, Runnable onFinish, Predicate<T> unlockCondition) {
        tasks.add(new Task(task, (tpf) -> {}, onFinish, unlockCondition));
    }
    
    public void addToQueue(T task, Runnable onFinish) {
        tasks.add(new Task(task, (tpf) -> {}, onFinish, (t) -> { return true; }));
    }
    
    public void addToQueue(T task) {
        tasks.add(new Task(task, (tpf) -> {}, () -> {}, (t) -> { return true; }));
    }
    
    public void onCurrentQueueFinished(Runnable action) {
        onCurrentQueueFinished = action;
    }
    
    //removes first occurrence
    public void removeFromQueue(T task) {
        for (int i = 0; i < tasks.size(); ++i) {
            if (tasks.get(i).focus.equals(task)) {
                tasks.remove(i);
                return;
            }
        }
    }
    
    public void removeFromQueue(int i) {
        tasks.remove(i);
    }
    
    public void clearQueue() {
        tasks.clear();
    }
    
    public void applyToAll(Command<T> command) {
        tasks.forEach((task) -> {
            command.execute(task.focus);
        });
    }
    
    public int size() {
        return tasks.size();
    }
    
    public boolean isEmpty() {
        return tasks.isEmpty();
    }
}
