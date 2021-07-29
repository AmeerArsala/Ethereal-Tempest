/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.unit;

import fundamental.FundamentalSuppliers.AbilitySupplier;
import fundamental.FundamentalSuppliers.FormationSupplier;
import fundamental.FundamentalSuppliers.FormulaSupplier;
import fundamental.FundamentalSuppliers.ItemSupplier;
import fundamental.FundamentalSuppliers.JobClassSupplier;
import fundamental.FundamentalSuppliers.SkillSupplier;
import fundamental.FundamentalSuppliers.TalentSupplier;
import fundamental.ability.Ability;
import fundamental.formation.Formation;
import fundamental.formula.Formula;
import fundamental.item.Item;
import fundamental.item.weapon.Weapon;
import fundamental.skill.Skill;
import fundamental.stats.BaseStatsDeserialization;
import fundamental.talent.Talent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class UnitDeserialization {
    private String name;
    private JobClassSupplier jobClass;
    private BaseStatsDeserialization baseStats;
    private BaseStatsDeserialization growthRates;
    private ItemSupplier[] inventory;
    private FormulaSupplier[] formulas;
    private TalentSupplier[] talents;
    private SkillSupplier[] skills;
    private AbilitySupplier[] abilities;
    private FormationSupplier[] formations;
    
    public UnitDeserialization(
        String name, JobClassSupplier jobClass, BaseStatsDeserialization baseStats, BaseStatsDeserialization growthRates, 
        ItemSupplier[] inventory, FormulaSupplier[] formulas, TalentSupplier[] talents, SkillSupplier[] skills, AbilitySupplier[] abilities, FormationSupplier[] formations
    ) {
        this.name = name;
        this.jobClass = jobClass;
        this.baseStats = baseStats;
        this.growthRates = growthRates;
        this.inventory = inventory;
        this.formulas = formulas;
        this.talents = talents;
        this.abilities = abilities;
        this.formations = formations;
    }
    
    public Unit constructUnit() {
        List<Item> itemList = new ArrayList<>();
        List<Formula> formulaList = new ArrayList<>();
        List<Talent> talentList = new ArrayList<>();
        List<Skill> skillList = new ArrayList<>();
        List<Ability> abilityList = new ArrayList<>();
        List<Formation> formationList = new ArrayList<>();
        
        for (ItemSupplier supplier : inventory) {
            itemList.add(supplier.get());
        }
        
        for (FormulaSupplier supplier : formulas) {
            formulaList.add(supplier.get());
        }
        
        for (TalentSupplier supplier : talents) {
            talentList.add(supplier.get());
        }
        
        for (SkillSupplier supplier : skills) {
            skillList.add(supplier.get());
        }
        
        for (AbilitySupplier supplier : abilities) {
            abilityList.add(supplier.get());
        }
        
        for (FormationSupplier supplier : formations) {
            formationList.add(supplier.get());
        }
        
        return new Unit(name, jobClass.get(), baseStats.createBaseStatsLoadoutMap(), growthRates.createBaseStatMap(100, 0), itemList, formulaList, talentList, skillList, abilityList, formationList);
    }
}
