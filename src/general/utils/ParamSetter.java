/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.utils;

/**
 *
 * @author night
 * @param <T> thing with the material
 * @param <P> parameter
 */
public interface ParamSetter<T, P> {
    public void set(T destination, P param);
}
