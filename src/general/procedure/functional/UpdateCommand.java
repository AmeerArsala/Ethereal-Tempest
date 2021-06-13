/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.procedure.functional;

/**
 *
 * @author night
 * @param <T> Object type
 */
public interface UpdateCommand<T> {
    public void execute(T source, float tpf);
}
