/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.unit.aspect;

/**
 *
 * @author night
 */
public class UnitStatus { //TODO: add more to this
    private final String name;
    
    public UnitStatus(String name) {
        this.name = name;
    }
    
    public String getName() { return name; }
    
    @Override
    public String toString() {
        return name;
    }
    
    
    public static final UnitStatus HEALTHY = new UnitStatus("Healthy");
}
