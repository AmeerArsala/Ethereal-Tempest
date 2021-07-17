/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.jobclass.animation;

import battle.data.CombatFlowData;
import battle.data.event.StrikeTheater;
import battle.data.event.StrikeTheater.Participant;
import com.google.gson.annotations.SerializedName;
import java.util.function.Predicate;

/**
 *
 * @author night
 */
public enum ActionCondition {
    @SerializedName("User is Striker") UserIsStriker((rep) -> {
        return rep.getRoleForCurrentStrike() == Participant.Striker;
    }),
    
    @SerializedName("Strike is Crit") StrikeIsCrit((rep) -> {
        return rep.getStrikeReel().getCurrentStrike().isCrit();
    }),
    
    @SerializedName("Strike is Followup") StrikeIsFollowup((rep) -> {
        //A Followup animation is used iff the last non-special Strike was made by the user
        //A Strike is considered 'special' if it is either a crit or triggers a BattleTalent
        //Fun Fact: If you think about it, a followup Strike will always occur on an odd index
        for (int i = rep.getStrikeReel().getIndex(); i > 0; --i) {
            Participant role = rep.getRoleForStrike(i);
            
            if (role != Participant.Striker) {
                return false;
            }
            
            boolean strikeIsSpecial = 
                rep.getStrikeReel().strikeTheater.getActualStrike(i).isCrit() || 
                rep.getStrikeReel().strikeTheater.getActualStrike(i).getStriker().triggeredBattleTalent();
            
            if (!strikeIsSpecial) { //role == Participant.Striker evaluates to true
                return true;
            }
        }
        
        
        return false;
    }),
    
    @SerializedName("Last Strike was User") LastStrikeWasUser((rep) -> {
        int i = rep.getStrikeReel().getIndex();
        if (i == 0) {
            return false;
        }
        
        Participant previousRole = rep.getRoleForStrike(i - 1);
        
        return previousRole == Participant.Striker;
    }),
    
    @SerializedName("Last Strike did Crit") LastStrikeDidCrit((rep) -> {
        int i = rep.getStrikeReel().getIndex();
        if (i == 0) {
            return false;
        }
        
        return rep.getStrikeReel().strikeTheater.getActualStrike(i - 1).isCrit();
    }),
    
    @SerializedName("Next Strike is User") NextStrikeIsUser((rep) -> {
        int i = rep.getStrikeReel().getIndex();
        if (i + 1 >= rep.getStrikeReel().size()) {
            return false;
        }
        
        Participant nextRole = rep.getRoleForStrike(i + 1);
        
        return nextRole == Participant.Striker;
    }),
    
    @SerializedName("Next Strike Will Hit") NextStrikeWillHit((rep) -> {
        int i = rep.getStrikeReel().getIndex();
        if (i + 1 >= rep.getStrikeReel().size()) {
            return false;
        }
        
        return rep.getStrikeReel().strikeTheater.getActualStrike(i + 1).didHit();
    }),
    
    @SerializedName("Next Strike Will Crit") NextStrikeWillCrit((rep) -> {
        int i = rep.getStrikeReel().getIndex();
        if (i + 1 >= rep.getStrikeReel().size()) {
            return false;
        }
        
        return rep.getStrikeReel().strikeTheater.getActualStrike(i + 1).isCrit();
    }),
    
    @SerializedName("Opponent Will Die") OpponentWillDie((rep) -> {
        return rep.getOpponent().getHPAfterCurrentStrike() == 0;
    }),
    
    @SerializedName("User is >= 25% away from the horizontal edges of the fight box") UserIs25PercentAwayFromEdgesOfBox((rep) -> {
        return Math.abs(0.5f - rep.getPos().x) <= 0.25f;
    }),
    
    @SerializedName("Opponent is >= 25% away from the horizontal edges of the fight box") OpponentIs25PercentAwayFromEdgesOfBox((rep) -> {
        return Math.abs(0.5f - rep.getOpponent().getPos().x) <= 0.25f;
    });
    
    private final Predicate<CombatFlowData.Representative> algorithm;
    private ActionCondition(Predicate<CombatFlowData.Representative> algorithm) {
        this.algorithm = algorithm;
    }
    
    public boolean test(CombatFlowData.Representative flowRep) {
        return algorithm.test(flowRep);
    }
}
