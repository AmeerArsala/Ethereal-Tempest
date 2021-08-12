/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.participant.paramswrapper;

import battle.data.CombatFlowData;
import battle.participant.FighterAnimationController;
import battle.participant.visual.BattleSprite;
import general.procedure.functional.UpdateLoop;

/**
 *
 * @author night
 * 
 * There are 2 constructors for convenience purposes (differed by order)
 */
public class DashAnimationArgsWrapper extends AnimationArgsWrapper {
    public final UpdateLoop onDashUpdate;

    public DashAnimationArgsWrapper(FighterAnimationController.AnimationParams animParams, BattleSprite opponent, CombatFlowData.Representative decisionData, UpdateLoop onDashUpdate) {
        super(animParams, opponent, decisionData);
        this.onDashUpdate = onDashUpdate;
    }
    
    public DashAnimationArgsWrapper(UpdateLoop onDashUpdate, FighterAnimationController.AnimationParams animParams, BattleSprite opponent, CombatFlowData.Representative decisionData) {
        super(animParams, opponent, decisionData);
        this.onDashUpdate = onDashUpdate;
    }
}
