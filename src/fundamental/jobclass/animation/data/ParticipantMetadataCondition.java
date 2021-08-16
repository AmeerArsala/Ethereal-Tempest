/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.jobclass.animation.data;

import battle.data.CombatFlowData;
import battle.data.event.StrikeTheater;
import battle.data.event.StrikeTheater.Participant;
import battle.data.participant.Combatant;
import fundamental.stats.BaseStat;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

/**
 *
 * @author night
 */
public enum ParticipantMetadataCondition {
    IsStriker((rep, indexRetriever, appliesToUser, restOfString) -> {
        int i = indexRetriever.applyAsInt(rep);
        if (!strikeExists(rep, i)) {
            return false;
        }
        
        if (appliesToUser) {
            return rep.getRoleForStrike(i) == Participant.Striker;
        } else { //applies to opponent
            return rep.getOpponent().getRoleForStrike(i) == Participant.Striker;
        }
    }),
    WillDie((rep, indexRetriever, appliesToUser, restOfString) -> {
        int i = indexRetriever.applyAsInt(rep);
        if (!strikeExists(rep, i)) {
            return false;
        }
        
        if (appliesToUser) {
            return rep.getHPAfterStrike(i) == 0;
        } else { //applies to opponent
            return rep.getOpponent().getHPAfterStrike(i) == 0;
        }
    }),
    PercentHPbefore((rep, indexRetriever, appliesToUser, restOfString) -> {
        int i = indexRetriever.applyAsInt(rep);
        if (!strikeExists(rep, i)) {
            return false;
        }
        
        CombatFlowData.Representative specifiedRep;
        
        if (appliesToUser) {
            specifiedRep = rep;
        } else { //applies to opponent
            specifiedRep = rep.getOpponent();
        }
        
        int comparisonIndex = 1; //right after the '#'
        String comparisonStr = restOfString.substring(comparisonIndex, comparisonIndex + 2);
        String numberStr = restOfString.substring(comparisonIndex + 2);
        float value = Float.parseFloat(numberStr);
        
        float hpBefore = (float)specifiedRep.getHPBeforeStrike(i);
        int maxHP = specifiedRep.getParticipant().getBaseStat(BaseStat.MaxHP);
        
        return parseComparison(comparisonStr, 100 * hpBefore / maxHP, value);
    }),
    PercentHPafter((rep, indexRetriever, appliesToUser, restOfString) -> {
        int i = indexRetriever.applyAsInt(rep);
        if (!strikeExists(rep, i)) {
            return false;
        }
        
        CombatFlowData.Representative specifiedRep;
        
        if (appliesToUser) {
            specifiedRep = rep;
        } else { //applies to opponent
            specifiedRep = rep.getOpponent();
        }
        
        int comparisonIndex = 1; //right after the '#'
        String comparisonStr = restOfString.substring(comparisonIndex, comparisonIndex + 2);
        String numberStr = restOfString.substring(comparisonIndex + 2);
        float value = Float.parseFloat(numberStr);
        
        float hpAfter = (float)specifiedRep.getHPAfterStrike(i);
        int maxHP = specifiedRep.getParticipant().getBaseStat(BaseStat.MaxHP);
        
        return parseComparison(comparisonStr, 100 * hpAfter / maxHP, value);
    }),
    PercentDistanceFromBoxEdge((rep, indexRetriever, appliesToUser, restOfString) -> {
        CombatFlowData.Representative specifiedRep;
        
        if (appliesToUser) {
            specifiedRep = rep;
        } else { //applies to opponent
            specifiedRep = rep.getOpponent();
        }
        
        int comparisonIndex = restOfString.indexOf("%") + 1;
        String edge = restOfString.substring(
            1, //right after the '#'
            comparisonIndex - 1
        );
        
        String comparisonStr = restOfString.substring(comparisonIndex, comparisonIndex + 2);
        String numberStr = restOfString.substring(comparisonIndex + 2);
        float value = Float.parseFloat(numberStr);
        float leftSide;
        
        switch (edge) {
            case "horizontal":
                value *= -1;
                leftSide = -Math.abs(0.5f - specifiedRep.getPos().x);
                break;
            case "vertical":
                value *= -1;
                leftSide = -Math.abs(0.5f - specifiedRep.getPos().y);
                break;
            case "front":
                leftSide = 1.0f - specifiedRep.getPos().x;
                break;
            case "behind":
                leftSide = specifiedRep.getPos().x;
                break;
            case "top":
                leftSide = 1.0f - specifiedRep.getPos().y;
                break;
            case "bottom":
                leftSide = specifiedRep.getPos().y;
                break;
            default:
                return false;
        }
        
        return parseComparison(comparisonStr, 100 * leftSide, value);
    });
    
    //For individual stats, create an enum for each one
    
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
        public boolean test(CombatFlowData.Representative rep, ToIntFunction<CombatFlowData.Representative> indexRetriever, boolean appliesToUser, String restOfString);
    }
    
    private final Algorithm algorithm;
    private ParticipantMetadataCondition(Algorithm algorithm) {
        this.algorithm = algorithm;
    }
    
    public boolean test(CombatFlowData.Representative rep, ToIntFunction<CombatFlowData.Representative> indexRetriever, boolean appliesToUser, String restOfString) {
        return algorithm.test(rep, indexRetriever, appliesToUser, restOfString);
    }
    

    public static Predicate<CombatFlowData.Representative> parseCondition(String str, ToIntFunction<CombatFlowData.Representative> indexRetriever) {
        int atSymbolIndex = str.indexOf("@");
        String owner = str.substring(0, atSymbolIndex);
        
        boolean user = true;
        if (owner.equals("user")) {
            user = true;
        } else if (owner.equals("opponent")) {
            user = false;
        }
        
        final boolean appliesToUser = user; 
        
        int hashtagIndex = str.indexOf("#");
        String nameOfEnum = str.substring(atSymbolIndex + 1, hashtagIndex);
        String restOfString = str.substring(hashtagIndex);
        ParticipantMetadataCondition pmc = valueOf(nameOfEnum);
        
        return (rep) -> {
            return pmc.test(rep, indexRetriever, appliesToUser, restOfString);
        };
    }
}
