/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.tool;

import etherealtempest.info.Conveyor;
import fundamental.Calculation;
import fundamental.item.weapon.WeaponAttribute;
import fundamental.item.weapon.WeaponType;
import fundamental.stats.RawBroadBonus;
import fundamental.stats.Toll;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class SupportToolInfo { //for deserialization
    private int crit;
    private int[] ranges;
    private WeaponAttribute weaponAttribute;
    private WeaponType weaponType;
    private Toll extraHeals;
    
    public SupportToolInfo() {}
    
    public SupportToolInfo(int crit, int[] ranges, WeaponAttribute weaponAttribute, WeaponType weaponType, Toll extraHeals) {
        this.crit = crit;
        this.ranges = ranges;
        this.weaponAttribute = weaponAttribute;
        this.weaponType = weaponType;
        this.extraHeals = extraHeals;
    }
    
    private List<Integer> toolRanges() {
        List<Integer> toolRanges = new ArrayList<>();
        for (int range : ranges) {
            toolRanges.add(range);
        }
        
        return toolRanges;
    }
    
    public SupportTool constructSupportTool(RawBroadBonus onEquipBonuses, Calculation<Conveyor, Toll> healCalculator) {
        return new SupportTool(crit, toolRanges(), weaponAttribute, weaponType, onEquipBonuses, healCalculator).setExtraHeals(extraHeals);
    }
}
