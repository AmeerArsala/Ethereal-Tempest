/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.tools.universal;

import java.util.function.Function;

/**
 *
 * @author night
 * @param <T> array type
 */
public class ArrayManipulator<T> {
    private final Function<Integer, T[]> arrayCreator;
    
    private T[] ts;
    
    public ArrayManipulator(T[] ts, Function<Integer, T[]> arrayCreator) {
        this.ts = ts;
        this.arrayCreator = arrayCreator;
    }
    
    public T[] getArray() {
        return ts;
    }
    
    public void setArray(T[] arr) {
        ts = arr;
    }
    
    public T[] subArray(int i, int i1) {
        T[] store = arrayCreator.apply(i1 - i);
        for (int index = i; index < i1; ++index) {
            store[index] = ts[index];
        }
        
        return store;
    }
    
    public T[] subArray(int i) {
        T[] store = arrayCreator.apply(ts.length - i);
        for (int index = i; index < ts.length; ++index) {
            store[index] = ts[index];
        }
        
        return store;
    }
    
    public ArrayManipulator<T> addFirst(T item) {
        T[] complete = arrayCreator.apply(ts.length + 1);
        complete[0] = item;
        for (int i = 0; i < ts.length; ++i) {
            complete[i + 1] = ts[i];
        }
        
        ts = complete;
        
        return this;
    } 
    
    public ArrayManipulator<T> addLast(T item) {
        T[] complete = arrayCreator.apply(ts.length + 1);
        complete[ts.length] = item;
        for (int i = 0; i < ts.length; ++i) {
            complete[i] = ts[i];
        }
        
        ts = complete;
        return this;
    }
    
    public ArrayManipulator<T> subManipulator(int i, int i1) {
        return new ArrayManipulator<>(subArray(i, i1), arrayCreator);
    }
    
    public ArrayManipulator<T> subManipulator(int i) {
        return new ArrayManipulator<>(subArray(i), arrayCreator);
    }
    
    public ArrayManipulator<T> equivalentInstance() {
        return new ArrayManipulator<>(subArray(0), arrayCreator);
    }
}
