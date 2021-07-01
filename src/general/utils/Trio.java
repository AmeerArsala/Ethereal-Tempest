/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.utils;

/**
 *
 * @author night
 * @param <ONE> type1
 * @param <TWO> type2
 * @param <THREE> type3
 */
public class Trio<ONE, TWO, THREE> {
    public final ONE first;
    public final TWO second;
    public final THREE third;
    
    public Trio(ONE first, TWO second, THREE third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
}
