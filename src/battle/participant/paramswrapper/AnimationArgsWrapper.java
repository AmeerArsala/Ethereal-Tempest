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
 */
public class AnimationArgsWrapper {
    public final FighterAnimationController.AnimationParams animParams;
    public final BattleSprite opponent;
    public final CombatFlowData.Representative decisionData;
    
    public AnimationArgsWrapper(FighterAnimationController.AnimationParams animParams, BattleSprite opponent, CombatFlowData.Representative decisionData) {
        this.animParams = animParams;
        this.opponent = opponent;
        this.decisionData = decisionData;
    }
    
    public DashAnimationArgsWrapper withDashUpdate(UpdateLoop onDashUpdate) {
        return new DashAnimationArgsWrapper(animParams, opponent, decisionData, onDashUpdate);
    }
}
