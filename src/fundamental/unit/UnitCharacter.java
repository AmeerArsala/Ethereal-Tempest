/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.unit;

/**
 *
 * @author night
 */
public class UnitCharacter extends Unit {
    private final CharacterUnitInfo info;
    
    //copies fields
    public UnitCharacter(Unit X, CharacterUnitInfo info) {
        super(X.getName(), X.getJobClass(), X.getRawStats(), X.getGrowthRates(), X.getInventory(), X.getFormulaManager(), X.getTalentManager(), X.getSkillManager(), X.getAbilityManager(), X.getFormationManager());
        this.info = info;
    }
    
    public CharacterUnitInfo getUnitInfo() {
        return info;
    }
}
