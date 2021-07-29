/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.formula;

import battle.animation.config.EntityAnimation;
import etherealtempest.info.Conveyor;
import fundamental.Calculation;
import fundamental.Gear;
import fundamental.item.weapon.WeaponInfo;
import fundamental.RawBroadBonus;
import fundamental.stats.alteration.Toll;
import fundamental.tool.DamageTool;
import fundamental.tool.SupportTool;
import fundamental.tool.SupportToolInfo;
import fundamental.tool.Tool.ToolType;

/**
 *
 * @author night
 */
public class FormulaDeserialization {
    private String name;
    private String desc;
    private WeaponInfo offensiveFormulaInfo;
    private SupportToolInfo supportFormulaInfo;
    private ToolType toolType;
    private Toll cost;
    private String animationJson;
    
    public FormulaDeserialization() {}
    
    public FormulaDeserialization(String name, String desc, WeaponInfo offensiveFormulaInfo, SupportToolInfo supportFormulaInfo, ToolType toolType, Toll cost, String animationJson) {
        this.name = name;
        this.desc = desc;
        this.offensiveFormulaInfo = offensiveFormulaInfo;
        this.supportFormulaInfo = supportFormulaInfo;
        this.toolType = toolType;
        this.cost = cost;
        this.animationJson = animationJson;
    }
    
    public Formula<DamageTool> constructOffensiveFormula(RawBroadBonus onEquipBonus) {
        Gear FA = new Gear(name, desc);
        return constructOffensiveFormula(FA, offensiveFormulaInfo.constructTool(onEquipBonus));
    }
    
    public Formula<DamageTool> constructOffensiveFormula(RawBroadBonus onEquipBonus, RawBroadBonus passiveBonus) {
        Gear FA = new Gear(name, desc, passiveBonus);
        return constructOffensiveFormula(FA, offensiveFormulaInfo.constructTool(onEquipBonus));
    }
    
    public Formula<SupportTool> constructSupportFormula(RawBroadBonus onEquipBonus, Calculation<Conveyor, Toll> healCalculator) {
        Gear FA = new Gear(name, desc);
        return constructSupportFormula(FA, supportFormulaInfo.constructSupportTool(onEquipBonus, healCalculator));
    }
    
    public Formula<SupportTool> constructSupportFormula(RawBroadBonus onEquipBonus, RawBroadBonus passiveBonus, Calculation<Conveyor, Toll> healCalculator) {
        Gear FA = new Gear(name, desc, passiveBonus);
        return constructSupportFormula(FA, supportFormulaInfo.constructSupportTool(onEquipBonus, healCalculator));
    }
    
    //these are what are ultimately called
    private Formula<DamageTool> constructOffensiveFormula(Gear FA, DamageTool DT) {
        return new Formula<>(FA, DT, toolType, cost, animationJson);
    }
    
    private Formula<SupportTool> constructSupportFormula(Gear FA, SupportTool ST) {
        return new Formula<>(FA, ST, toolType, cost, animationJson);
    }
}
