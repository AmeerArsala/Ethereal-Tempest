/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.jobclass.animation;

import battle.data.DecisionParams;
import battle.data.StrikeTheater;
import battle.data.StrikeTheater.Participant;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author night
 */
public class ActionDecider {
    private String folderRoot; //folder root of the animation depicting the weapon used (e.g. "Battle\\Freeblade\\offense\\sword\\")
    private Procedure onIdleCalled;
    private Procedure onDashCalled;
    private Procedure onAttackCalled;
    private Procedure onDodgeCalled;
    private Procedure onGotHitCalled;
    private AttributeAnimationMap skillAnimations;
    private AttributeAnimationMap battleTalentAnimations;
    
    public ActionDecider(String folderRoot, Procedure onIdleCalled, Procedure onDashCalled, Procedure onAttackCalled, Procedure onDodgeCalled, Procedure onGotHitCalled, AttributeAnimationMap skillAnimations, AttributeAnimationMap battleTalentAnimations) {
        this.folderRoot = folderRoot;
        this.onIdleCalled = onIdleCalled;
        this.onDashCalled = onDashCalled;
        this.onAttackCalled = onAttackCalled;
        this.onDodgeCalled = onDodgeCalled;
        this.onGotHitCalled = onGotHitCalled;
        this.skillAnimations = skillAnimations;
        this.battleTalentAnimations = battleTalentAnimations;
    }
    
    public String getFolderRoot() { return folderRoot; }
    
    public Procedure getOnIdleCalled() { return onIdleCalled; }
    public Procedure getOnAttackCalled() { return onAttackCalled; }
    public Procedure getOnDashCalled() { return onDashCalled; } //can be null
    
    //returns idle on these 2 if they are null
    public Procedure getOnDodgeCalled() { return onDodgeCalled != null ? onDodgeCalled : onIdleCalled; }
    public Procedure getOnGotHitCalled() { return onGotHitCalled != null ? onGotHitCalled : onIdleCalled; }
    
    public AttributeAnimation getOnSkillAttackCalled(String skillName) {
        return skillAnimations.searchForCall(skillName);
    }
    
    public AttributeAnimation getOnBattleTalentAttackCalled(String bTalentName) {
        return battleTalentAnimations.searchForCall(bTalentName);
    }
    
    public void deserializeAll() {
        onIdleCalled.deserializeAll(folderRoot);
        onAttackCalled.deserializeAll(folderRoot);
        
        if (onDashCalled != null) {
            onDashCalled.deserializeAll(folderRoot);
        }
        
        if (onDodgeCalled != null) {
            onDodgeCalled.deserializeAll(folderRoot);
        }
        
        if (onGotHitCalled != null) {
            onGotHitCalled.deserializeAll(folderRoot);
        }
        
        skillAnimations.deserializeAllAvailable(folderRoot);
        battleTalentAnimations.deserializeAllAvailable(folderRoot);
    }
    
    public static class Procedure {
        private Condition IF;
        private Procedure THEN;
        private Procedure ELSE;
        private Procedure ALSO;
        
        private NextAction[] actions;
        
        public Procedure(Condition IF, Procedure THEN, Procedure ELSE, Procedure ALSO, NextAction[] actions) {
            this.IF = IF;
            this.THEN = THEN;
            this.ELSE = ELSE;
            this.ALSO = ALSO;
            this.actions = actions;
        }
        
        private void deserializeAnimations(String folderRoot) {
            if (actions == null) { return; }
            
            for (NextAction action : actions) {
                action.getAnimation().addFolderRoot(folderRoot);
                action.getAnimation().deserializeIfNotAlready();
            }
        }
        
        public void deserializeAll(String folderRoot) {
            deserializeAnimations(folderRoot);
            
            if (THEN != null) {
                THEN.deserializeAll(folderRoot);
            }
            
            if (ELSE != null) {
                ELSE.deserializeAll(folderRoot);
            }
            
            if (ALSO != null) {
                ALSO.deserializeAll(folderRoot);
            }
        }
        
        private List<NextAction> execute(DecisionParams params) {
            List<NextAction> nextActions = new ArrayList<>();
            
            if (actions != null) {
                nextActions.addAll(Arrays.asList(actions));
            }
            
            if (IF != null && IF.evaluate(params)) {
                nextActions.addAll(THEN.execute(params));
            } else if (ELSE != null) {
                nextActions.addAll(ELSE.execute(params));
            }
            
            if (ALSO != null) {
                nextActions.addAll(ALSO.execute(params));
            }
            
            return nextActions;
        }
        
        public NextActionSequence run(DecisionParams params) {
            return new NextActionSequence(execute(params));
        }
    }
    
    public static class Condition {
        private ActionCondition[] conditions; // array works as an AND
        private Condition AND; // evaluates AND over OR (higher priority)
        private Condition OR;
        private boolean is; // is true or is false
        
        public Condition(ActionCondition[] conditions, Condition AND, Condition OR, boolean is) {
            this.conditions = conditions;
            this.AND = AND;
            this.OR = OR;
            this.is = is;
        }
        
        private boolean evaluateBaseConditions(DecisionParams params) {
            if (conditions == null) {
                return false;
            }
            
            for (ActionCondition condition : conditions) {
                if (condition.test(params) != is) {
                    return false;
                } 
            }
            
            return true;
        }
        
        public boolean evaluate(DecisionParams params) {
            boolean baseConditions = evaluateBaseConditions(params);
            
            if (AND != null) {
                baseConditions = baseConditions && AND.evaluate(params);
            }
            
            if (OR != null) {
                baseConditions = baseConditions || OR.evaluate(params);
            }
            
            return baseConditions;
        }
    }
    
    private class AttributeAnimationMap {
        private AttributeAnimation[] mappings;
        private Procedure defaultAnimation;
        
        public AttributeAnimationMap(AttributeAnimation[] mappings, Procedure defaultAnimation) {
            this.mappings = mappings;
            this.defaultAnimation = defaultAnimation;
        }
        
        public AttributeAnimation[] getMappings() { return mappings; }
        public Procedure getDefaultAnimation() { return defaultAnimation; }
        
        public boolean usesAnimation() { //if it doesn't use an animation, just continues with the normal attack animation
            return (mappings == null || (mappings != null && mappings.length == 0)) && defaultAnimation == null;
        }
        
        public void deserializeAllAvailable(String folderRoot) {
            if (mappings != null) {
                for (AttributeAnimation anim : mappings) {
                    anim.getOnCall().deserializeAll(folderRoot);
                }
            }
            
            if (defaultAnimation != null) {
                defaultAnimation.deserializeAll(folderRoot);
            }
        }
        
        public AttributeAnimation searchForCall(String name) {
            for (AttributeAnimation anim : mappings) {
                if (anim.getName().equals(name)) {
                    return anim;
                }
            }
            
            if (defaultAnimation == null) {
                return new AttributeAnimation("", onAttackCalled, true);
            }
            
            return new AttributeAnimation("", defaultAnimation, true);
        }
    }
    
    public static class AttributeAnimation {
        private String name;
        private Procedure onCall;
        private boolean usesDash;
        
        public AttributeAnimation(String name, Procedure onCall, boolean usesDash) {
            this.name = name;
            this.onCall = onCall;
            this.usesDash = usesDash;
        }
        
        public String getName() { return name; }
        public Procedure getOnCall() { return onCall; }
        public boolean usesDash() { return usesDash; }
    }
    
    public enum ActionCondition {
        @SerializedName("User is Striker") UserIsStriker((params) -> {
            return params.getUserRoleForStrike(params.getStrikeIndex()) == StrikeTheater.Participant.Striker;
        }),
        
        @SerializedName("Strike is Crit") StrikeIsCrit((params) -> {
            return params.getCurrentStrike().isCrit();
        }),
        
        @SerializedName("Strike is Followup") StrikeIsFollowup((params) -> {
            int last = params.getStrikeIndex();
            for (int i = params.getStrikeIndex(); i > 0; --i) {
                StrikeTheater.Participant role = params.getUserRoleForStrike(i);
                boolean lastStrikeWasSpecial = params.strikeGroup.getActualStrike(i).isCrit() || params.strikeGroup.getActualStrike(i).getStriker().triggeredBattleTalent();
                if (role != Participant.Striker || (role == Participant.Striker && lastStrikeWasSpecial)) {
                    break;
                }
                
                last = i;
            }
            
            
            return (params.getStrikeIndex() - last) % 2 == 1;
        }),
        
        @SerializedName("Last Strike was User") LastStrikeWasUser((params) -> {
            if (params.getStrikeIndex() == 0) {
                return false;
            }
            
            Participant previousRole = params.getUserRoleForStrike(params.getStrikeIndex() - 1);
            
            return previousRole == Participant.Striker;
        }),
        
        @SerializedName("Last Strike did Crit") LastStrikeDidCrit((params) -> {
            if (params.getStrikeIndex() + 1 >= params.strikeGroup.getActualStrikes().size()) {
                return false;
            }
            
            return params.strikeGroup.getActualStrike(params.getStrikeIndex() + 1).isCrit();
        }),
        
        @SerializedName("Next Strike is User") NextStrikeIsUser((params) -> {
            if (params.getStrikeIndex() + 1 >= params.strikeGroup.getActualStrikes().size()) {
                return false;
            }
            
            Participant nextRole = params.getUserRoleForStrike(params.getStrikeIndex() + 1);
            
            return nextRole == Participant.Striker;
        }),
        
        @SerializedName("Next Strike Will Hit") NextStrikeWillHit((params) -> {
            if (params.getStrikeIndex() + 1 >= params.strikeGroup.getActualStrikes().size()) {
                return false;
            }
            
            return params.strikeGroup.getActualStrike(params.getStrikeIndex() + 1).isCrit();
        }),
        
        @SerializedName("Next Strike Will Crit") NextStrikeWillCrit((params) -> {
            if (params.getStrikeIndex() + 1 >= params.strikeGroup.getActualStrikes().size()) {
                return false;
            }
            
            return params.strikeGroup.getActualStrike(params.getStrikeIndex() + 1).isCrit();
        }),
        
        @SerializedName("Opponent Will Die") OpponentWillDie((params) -> {
            return params.strikeGroup.getParticipantHP(params.getOpponentRoleForStrike(params.getStrikeIndex()), params.getStrikeIndex()) != 0;
        }),
        
        @SerializedName("User is >= 25% away from the horizontal edges of the fight box") UserIs25PercentAwayFromEdgesOfBox((params) -> {
            return Math.abs(0.5f - params.userPos.x) <= 0.25f;
        }),
        
        @SerializedName("Opponent is >= 25% away from the horizontal edges of the fight box") OpponentIs25PercentAwayFromEdgesOfBox((params) -> {
            return Math.abs(0.5f - params.opponentPos.x) <= 0.25f;
        });
        
        private final ConditionDecision algorithm;
        private ActionCondition(ConditionDecision algorithm) {
            this.algorithm = algorithm;
        }
        
        public boolean test(DecisionParams params) {
            return algorithm.test(params);
        }
    }
}
