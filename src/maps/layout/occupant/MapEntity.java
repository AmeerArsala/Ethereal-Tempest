/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.occupant;

import fundamental.Entity;

/**
 *
 * @author night
 */
public class MapEntity extends Entity { //use gson
    //add more to this class later
    public enum DamageLevel {
        None,
        Moderate,
        Severe,
        Broken; //Dead
        
        public static DamageLevel calculateDamageLevel(int current_hp, int maxHP) {
            float ratio = current_hp / ((float)maxHP);
            
            if (ratio == 1) {
                return None;
            }
            
            if (ratio == 0) {
                return Broken;
            }
            
            if (ratio <= (1f / 3f)) {
                return Severe;
            }
            
            return Moderate;
        }
        
        
    }
    
    public static final int DEFAULT_HP_VALUE = 3;
    public static final int UNBREAKABLE_STRUCTURE = 0;

    private int posX;
    private int posY;
    private int elevation;
    private int MaxHP;
    
    private int currentHP;
    private DamageLevel damageState = DamageLevel.None;
    
    public MapEntity(String name, int posX, int posY, int elevation, int MaxHP) {
        super(name);
        this.posX = posX;
        this.posY = posY;
        this.elevation = elevation;
        this.MaxHP = MaxHP;
        
        updateDamageState();
    }
    
    public void damage() { //by default, breakable structures can have 3 HP; no matter the stats of the enemy, they can only do 1 damage to it normally
        if (MaxHP > 0) {
            currentHP--;
        }
        updateDamageState();
    }
    
    public void damage(int val) { //...unless they have a Talent that gives them extra damage towards structures
        if (MaxHP > 0) {
            currentHP -= val;
        }
        updateDamageState();
    }
    
    public boolean isDamageable() { //MaxHP being <= means it is an unbreakable structure
        return MaxHP > 0;
    }
    
    private void updateDamageState() {
        if (MaxHP <= UNBREAKABLE_STRUCTURE) { return; }
        
        damageState = DamageLevel.calculateDamageLevel(currentHP, MaxHP);
    }
    
    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    public int getElevation() { return elevation; }
    
    public int getMaxHP() { return MaxHP; }
    public int getCurrentHP() { return currentHP; }
    
    public DamageLevel getDamageLevel() { return damageState; }
}
