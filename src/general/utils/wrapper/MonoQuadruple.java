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
public class MonoQuadruple<T> extends Quadruple<T, T, T, T> {
    public MonoQuadruple(T first, T second, T third, T fourth) {
        super(first, second, third, fourth);
    }
}
