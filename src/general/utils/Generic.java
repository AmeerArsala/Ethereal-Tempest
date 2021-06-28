/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.utils;

import java.util.Objects;

/**
 *
 * @author night
 * @param <T> type 
 */
public class Generic<T> {
    private T t;
    private String name;    
    
    public Generic(T t, String name) {
        this.t = t;
        this.name = name;
    }
    
    public T getItem() {
        return t;
    }
    
    public String getName() {
        return name;
    }
    
    public void set(T item, String itemName) {
        t = item;
        name = itemName;
    }
    
    @Override
    public String toString() {
        return name + ": " + t.toString();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.t);
        hash = 47 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Generic<?> other = (Generic<?>) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.t, other.t)) {
            return false;
        }
        return true;
    }
    
}
