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
public class Attribute {
    protected final String name;
    protected final String desc;
    
    protected BattleVisual fightVisual;
    
    public Attribute(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }
    
    public String getName() { return name; }
    public String getDescription() { return desc; }
    
    public BattleVisual getFightVisual() { return fightVisual; }
    
    public void setFightVisual(BattleVisual visual) {
        fightVisual = visual;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
