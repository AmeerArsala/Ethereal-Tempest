/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental;

/**
 *
 * @author night
 */
public class Associated {
    protected final String name;
    protected final String desc;
    
    private final boolean exists;
    
    public Associated(String name, String desc) {
        this.name = name;
        this.desc = desc;
        exists = true;
    }
    
    public Associated(boolean exists) {
        this.exists = exists;
        name = "";
        desc = "";
    }
    
    protected Associated() {
        this(false);
    }
    
    public String getName() { return name; }
    public String getDescription() { return desc; }
    
    public boolean doesExist() { return exists; }
    
    @Override
    public String toString() {
        return name;
    }
}
