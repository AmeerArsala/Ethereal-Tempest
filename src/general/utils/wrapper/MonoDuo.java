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
public class MonoDuo<T> extends Duo<T, T> {
    public MonoDuo(T first, T second) {
        super(first, second);
    }
}
