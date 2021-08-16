/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.jobclass.animation;

import battle.data.CombatFlowData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jme3.asset.AssetManager;
import com.jme3.texture.Texture;
import fundamental.jobclass.animation.data.ParticipantMetadataCondition;
import fundamental.jobclass.animation.data.StrikeMetadataCondition;
import general.math.function.ParsedMathFunction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

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
    
    @Expose(deserialize = false) Texture spritesheet;
    
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
    public Texture getSpritesheetTexture() { return spritesheet; }
    
    public Procedure getOnIdleCalled() { return onIdleCalled; }
    public Procedure getOnAttackCalled() { return onAttackCalled; }
    public Procedure getOnDashCalled() { return onDashCalled; } //can be null (e.g. formula casting)
    
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
    
    public void loadSpritesheet(AssetManager assetManager) {
        assetManager.loadTexture("Sprites/" + (folderRoot + "spritesheet.png").replaceAll("\\\\", "/"));
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
            
            if (IF != null) {
                IF.parseConditions();
            }
            
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
        
        private List<NextAction> execute(CombatFlowData.Representative data) {
            List<NextAction> nextActions = new ArrayList<>();
            
            if (actions != null) {
                nextActions.addAll(Arrays.asList(actions));
            }
            
            if (IF != null && IF.evaluate(data)) {
                nextActions.addAll(THEN.execute(data));
            } else if (ELSE != null) {
                nextActions.addAll(ELSE.execute(data));
            }
            
            if (ALSO != null) {
                nextActions.addAll(ALSO.execute(data));
            }
            
            return nextActions;
        }
        
        public NextActionSequence run(CombatFlowData.Representative data) {
            return new NextActionSequence(execute(data));
        }
    }
    
    public static class Condition {
        //private ActionCondition[] conditions; // array works as an AND
        private String[] conditions; // array works as an AND
        
        @Expose(deserialize = false)
        private Predicate<CombatFlowData.Representative>[] computedConditions;
        
        private Condition AND;        // && (AND); evaluates AND over OR (higher priority)
        private Condition OR;         // || (OR)
        private Condition XOR;        // ^ (Exclusive OR aka XOR)
        private Condition BINARY_AND; // & (Binary AND)
        private Condition BINARY_OR;  // | (Binary OR)
        private boolean is; // is true or is false
        
        public Condition(String[] conditions, Condition AND, Condition OR, Condition XOR, Condition BINARY_AND, Condition BINARY_OR, boolean is) {
            this.conditions = conditions;
            this.AND = AND;
            this.OR = OR;
            this.XOR = XOR;
            this.BINARY_AND = BINARY_AND;
            this.BINARY_OR = BINARY_OR;
            this.is = is;
        }
        
        public void parseConditions() {
            if (conditions == null) {
                return;
            }
            
            computedConditions = new Predicate[conditions.length];
            for (int i = 0; i < conditions.length; ++i) {
                computedConditions[i] = parseCondition(conditions[i]);
            }
        }
        
        private boolean evaluateBaseConditions(CombatFlowData.Representative data) {
            if (computedConditions == null) {
                return false;
            }
            
            for (Predicate<CombatFlowData.Representative> condition : computedConditions) {
                if (condition.test(data) != is) {
                    return false;
                } 
            }
            
            return true;
        }
        
        public boolean evaluate(CombatFlowData.Representative data) {
            boolean baseConditions = evaluateBaseConditions(data);
            
            if (AND != null) {
                baseConditions = baseConditions && AND.evaluate(data);
            }
            
            if (OR != null) {
                baseConditions = baseConditions || OR.evaluate(data);
            }
            
            if (XOR != null) {
                baseConditions ^= XOR.evaluate(data);
            }
            
            if (BINARY_AND != null) {
                baseConditions &= BINARY_AND.evaluate(data);
            }
            
            if (BINARY_OR != null) {
                baseConditions |= BINARY_OR.evaluate(data);
            }
            
            return baseConditions;
        }
        
        //Example: "STRIKE[next]:IsCrit#"
        //"next" is the same as "current+1" or "c+1"
        //"last" is the same as "current-1" or "c-1"
        //everything in the square brackets ([]) is a math equation from ParsedMathFunction
        //Example 2: "STRIKE[0]:DoesDmgPercent#<=50.0"
        //Example 3: "PARTICIPANT[current+2]:user@WillDie#"
        //Example 4: "PARTICIPANT[current*2]:opponent@PercentDistanceFromBoxEdge#vertical%> 25.0"
        //Example 5: "PARTICIPANT[current]:user@PercentDistanceFromBoxEdge#horizontal>=25.0"
        public static Predicate<CombatFlowData.Representative> parseCondition(String str) {
            char currentIndexChar = 'c';
            String functionName = "f";
        
            String computedDefinition = str.replaceAll("current", Character.toString(currentIndexChar));
            computedDefinition = computedDefinition.replaceFirst("next", currentIndexChar + "+1").replaceFirst("last", currentIndexChar + "-1");
            
            int endFunctionIndex = computedDefinition.indexOf("]:");
            String functionExpression = computedDefinition.substring(computedDefinition.indexOf("[") + 1, endFunctionIndex);
            
            ParsedMathFunction func = new ParsedMathFunction(functionName, currentIndexChar, functionExpression);
            ToIntFunction<CombatFlowData.Representative> indexRetriever = (rep) -> { 
                return func.output(rep.getStrikeReel().getIndex()).intValue(); 
            };
            
            if (computedDefinition.startsWith("STRIKE")) {
                return StrikeMetadataCondition.parseCondition(computedDefinition.substring(endFunctionIndex + 2), indexRetriever);
            } else if (computedDefinition.startsWith("PARTICIPANT")) {
                return ParticipantMetadataCondition.parseCondition(computedDefinition.substring(endFunctionIndex + 2), indexRetriever);
            }
            
            return null;
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
}
