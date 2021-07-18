/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.utils.functional;

/**
 *
 * @author night
 * @param <T> type to pass in as parameter
 */
public interface ToFloatFunction<T> {
    public float apply(T t);
}
