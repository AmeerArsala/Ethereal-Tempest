/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.jobclass.animation.data;

import battle.data.CombatFlowData;
import battle.data.event.Strike;
import battle.data.event.StrikeTheater;
import battle.data.event.StrikeTheater.Participant;
import fundamental.stats.BaseStat;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

/**
 *
 * @author night
 */
public enum StrikeMetadataCondition {
    Exists((rep, indexRetriever, restOfString) -> {
        return strikeExists(rep, indexRetriever.applyAsInt(rep));
    }),
    DoesHit((rep, indexRetriever, restOfString) -> {
        int i = indexRetriever.applyAsInt(rep);
        if (!strikeExists(rep, i)) {
            return false;
        }
        
        return rep.getStrikeReel().strikeTheater.getActualStrike(i).didHit();
    }),
    IsCrit((rep, indexRetriever, restOfString) -> {
        int i = indexRetriever.applyAsInt(rep);
        if (!strikeExists(rep, i)) {
            return false;
        }
        
        return rep.getStrikeReel().strikeTheater.getActualStrike(i).isCrit();
    }),
    IsSkill((rep, indexRetriever, restOfString) -> {
        int i = indexRetriever.applyAsInt(rep);
        if (!strikeExists(rep, i)) {
            return false;
        }
        
        return rep.getStrikeReel().strikeTheater.getActualStrike(i).isSkill();
    }),
    IsBattleTalent((rep, indexRetriever, restOfString) -> {
        int i = indexRetriever.applyAsInt(rep);
        if (!strikeExists(rep, i)) {
            return false;
        }
        
        return rep.getStrikeReel().strikeTheater.getActualStrike(i).getStriker().triggeredBattleTalent();
    }),
    IsSpecial((rep, indexRetriever, restOfString) -> {
        int i = indexRetriever.applyAsInt(rep);
        if (!strikeExists(rep, i)) {
            return false;
        }
        
        Strike strike = rep.getStrikeReel().strikeTheater.getActualStrike(i);
        return strike.isCrit() || strike.isSkill() || strike.getStriker().triggeredBattleTalent();
    }),
    IsFollowup((rep, indexRetriever, restOfString) -> {
        //A Followup animation is used iff the last non-'special' Strike was made by the user
        //A Strike is considered 'special' if it is either a crit or triggers a BattleTalent or it is a skill
        int i = indexRetriever.applyAsInt(rep);
        if (i == 0 || !strikeExists(rep, i)) {
            return false;
        }
        
        CombatFlowData.Representative strikeRep = rep.getRoleForStrike(i) == Participant.Striker ? rep : rep.getOpponent(); //get the striker's representative of this strike
        
        if (strikeRep.getRoleForStrike(i - 1) != Participant.Striker) { //if they weren't the striker the previous strike, return false
            return false;
        }
        
        Strike strike = rep.getStrikeReel().strikeTheater.getActualStrike(i);
        Strike prevStrike = rep.getStrikeReel().strikeTheater.getActualStrike(i - 1);
        
        //check whether the current and previous strikes are 'special'
        boolean strikeIsSpecial = strike.isCrit() || strike.isSkill() || strike.getStriker().triggeredBattleTalent();
        
        //maybe remove the prevStrikeWasSpecial in case you prefer it
        boolean prevStrikeWasSpecial = prevStrike.isCrit() || prevStrike.isSkill() || prevStrike.getStriker().triggeredBattleTalent();
        
        return !strikeIsSpecial && !prevStrikeWasSpecial; //role == Participant.Striker evaluates to true
    }),
    IsUser((rep, indexRetriever, restOfString) -> {
        int i = indexRetriever.applyAsInt(rep);
        if (!strikeExists(rep, i)) {
            return false;
        }
        
        return rep.getRoleForStrike(i) == Participant.Striker;
    }),
    DoesDmgPercent((rep, indexRetriever, restOfString) -> {
        int i = indexRetriever.applyAsInt(rep);
        if (!strikeExists(rep, i)) {
            return false;
        }
        
        int comparisonIndex = 1; //right after the '#'
        String comparisonStr = restOfString.substring(comparisonIndex, comparisonIndex + 2);
        String numberStr = restOfString.substring(comparisonIndex + 2);
        float value = Float.parseFloat(numberStr);
        
        CombatFlowData.Representative victimRep;
        if (rep.getRoleForStrike(i) == Participant.Victim) {
            victimRep = rep;
        } else {
            victimRep = rep.getOpponent();
        }
        
        float damageDone = victimRep.getHPBeforeStrike(i) - victimRep.getHPAfterStrike(i);
        int victimMaxHP = victimRep.getParticipant().getBaseStat(BaseStat.MaxHP);
        
        return parseComparison(comparisonStr, 100 * damageDone / victimMaxHP, value);
    });
    
    private static boolean parseComparison(String comparisonStr, float leftSide, float rightSide) {
        switch (comparisonStr) {
            case "==":
                return leftSide == rightSide;
            case "<=":
                return leftSide <= rightSide;
            case ">=":
                return leftSide >= rightSide;
            case "> ":
                return leftSide > rightSide;
            case "< ":
                return leftSide < rightSide;
        }
        
        return false;
    }
    
    private static boolean strikeExists(CombatFlowData.Representative rep, int index) {
        return index >= 0 && index < rep.getStrikeReel().size();
    }
    
    public static interface Algorithm {
        public boolean test(CombatFlowData.Representative rep, ToIntFunction<CombatFlowData.Representative> indexRetriever, String restOfString);
    }
    
    private final Algorithm algorithm;
    private StrikeMetadataCondition(Algorithm algorithm) {
        this.algorithm = algorithm;
    }
    
    public boolean test(CombatFlowData.Representative rep, ToIntFunction<CombatFlowData.Representative> indexRetriever, String restOfString) {
        return algorithm.test(rep, indexRetriever, restOfString);
    }
    
    public static Predicate<CombatFlowData.Representative> parseCondition(String str, ToIntFunction<CombatFlowData.Representative> indexRetriever) {
        int hashtagIndex = str.indexOf("#");
        String nameOfEnum = str.substring(0, hashtagIndex);
        String restOfString = str.substring(hashtagIndex);
        StrikeMetadataCondition smc = valueOf(nameOfEnum);
        
        return (rep) -> {
            return smc.test(rep, indexRetriever, restOfString);
        };
    }
}
