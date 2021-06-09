/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.ability;

import etherealtempest.info.Conveyor;

/**
 *
 * @author night
 */
public interface AbilityEffect {
    public boolean mayBeUsed(Conveyor conv);
    public void enactEffect(Conveyor conv);
    public int calculateFavorability(Conveyor conv);
}
