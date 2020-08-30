/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.formation;

import battle.Conveyer;

/**
 *
 * @author night
 */
public interface FormationTechnique {
    public String getName();
    public String getDescription();
    public void useTechnique(Conveyer data);
    public boolean getCondition(Conveyer data);
}
