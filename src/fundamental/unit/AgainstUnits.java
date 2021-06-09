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
public enum AgainstUnits {
    Allied((A, B) -> { return A.isAlliedWith(B); }),
    Enemy((A, B) -> { return !A.isAlliedWith(B); }),
    All((A, B) -> { return true; });
    
    private final ApplyCondition condition;
    private AgainstUnits(ApplyCondition condition) {
        this.condition = condition;
    }
    
    private interface ApplyCondition {
        public boolean check(Unit A, Unit B);
    }
    
    public boolean firstShouldApplyToSecond(Unit first, Unit second) {
        return condition.check(first, second);
    }
}
