/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.ability;

import etherealtempest.info.Conveyer;

/**
 *
 * @author night
 */
public interface AbilityEffect {
    public boolean mayBeUsed(Conveyer conv);
    public void enactEffect(Conveyer conv);
    public int calculateFavorability(Conveyer conv);
}
