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
import fundamental.jobclass.animation.data.ParticipantMetadataCondition;
import fundamental.jobclass.animation.data.StrikeMetadataCondition;
import general.math.function.ParsedMathFunction;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

/**
 *
 * @author night
 */
@Deprecated
public enum ActionCondition {
    @SerializedName("User is Striker") UserIsStriker((rep) -> {
        return rep.getRoleForCurrentStrike() == Participant.Striker;
    }),
    
    @SerializedName("Strike is Crit") StrikeIsCrit((rep) -> {
        return rep.getStrikeReel().getCurrentStrike().isCrit();
    }),
    
    @SerializedName("Strike is Followup") StrikeIsFollowup((rep) -> {
        //A Followup animation is used iff the last non-'special' Strike was made by the user
        //A Strike is considered 'special' if it is either a crit or triggers a BattleTalent or it is a skill
        int i = rep.getStrikeReel().getIndex();
        
        if (i == 0) {
            return false;
        }
        
        CombatFlowData.Representative strikeRep = UserIsStriker.test(rep) ? rep : rep.getOpponent(); //get the striker's representative of this strike
        
        if (strikeRep.getRoleForStrike(i - 1) != Participant.Striker) { //if they weren't the striker the previous strike, return false
            return false;
        }
        
        //check whether the current and previous strikes are 'special'
        boolean currentStrikeIsSpecial =
            strikeRep.getStrikeReel().strikeTheater.getActualStrike(i).isCrit() || 
            strikeRep.getStrikeReel().strikeTheater.getActualStrike(i).getStriker().triggeredBattleTalent();
        
        //maybe remove the prevStrikeWasSpecial in case you prefer it
        boolean prevStrikeWasSpecial =
            strikeRep.getStrikeReel().strikeTheater.getActualStrike(i - 1).isCrit() || 
            strikeRep.getStrikeReel().strikeTheater.getActualStrike(i - 1).getStriker().triggeredBattleTalent();
        
        return !currentStrikeIsSpecial && !prevStrikeWasSpecial; //role == Participant.Striker evaluates to true
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
