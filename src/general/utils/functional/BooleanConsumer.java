/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.utils.functional;

/**
 *
 * @author night
 */
public interface BooleanConsumer {
    public void accept(boolean value);
    
    public default BooleanConsumer andThen(BooleanConsumer after) {
        return (value) -> {
            accept(value);
            after.accept(value);
        };
    }
}
