/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.unit;

import java.util.function.BiPredicate;

/**
 *
 * @author night
 */
public enum AgainstUnits {
    Allied((A, B) -> { return A.isAlliedWith(B); }),
    Enemy((A, B) -> { return !A.isAlliedWith(B); }),
    All((A, B) -> { return true; });
    
    private final BiPredicate<Unit, Unit> condition;
    private AgainstUnits(BiPredicate<Unit, Unit> condition) {
        this.condition = condition;
    }
    
    public boolean firstShouldApplyToSecond(Unit first, Unit second) {
        return condition.test(first, second);
    }
}
