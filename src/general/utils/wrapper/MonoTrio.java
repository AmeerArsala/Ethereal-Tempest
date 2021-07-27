/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.utils.wrapper;

/**
 *
 * @author night
 * @param <T> type
 */
public class MonoTrio<T> extends Trio<T, T, T> {
    public MonoTrio(T first, T second, T third) {
        super(first, second, third);
    }
}
