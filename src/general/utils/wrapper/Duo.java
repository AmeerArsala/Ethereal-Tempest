/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.utils.wrapper;

/**
 *
 * @author night
 * @param <ONE> type1
 * @param <TWO> type2
 */
public class Duo<ONE, TWO> {
    public final ONE first;
    public final TWO second;
    
    public Duo(ONE first, TWO second) {
        this.first = first;
        this.second = second;
    }
}
