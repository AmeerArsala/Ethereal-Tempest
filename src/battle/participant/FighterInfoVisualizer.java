/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.participant;

import battle.data.event.Strike;
import battle.environment.BoxMetadata;
import battle.gui.FighterGUI;
import battle.gui.GuiFactory;
import battle.participant.visual.BattleSprite;
import com.simsilica.lemur.Label;
import com.atr.jme.font.util.StringContainer;
import com.atr.jme.font.util.Style;
import com.jme3.asset.AssetManager;
import com.jme3.font.Rectangle;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.scene.shape.Box;
import com.jme3.scene.Geometry;
import com.jme3.material.Material;
import enginetools.MaterialCreator;
import enginetools.math.Vector3F;
import etherealtempest.fsm.FSM;
import etherealtempest.fsm.FSM.FighterState;
import etherealtempest.fsm.FsmState;
import fundamental.stats.BaseStat;
import fundamental.tool.DamageTool;
import general.tools.GameTimer;
import general.math.function.CartesianFunction;
import general.ui.text.FontProperties;
import general.ui.text.Text2D;
import general.ui.text.TextProperties;
import general.ui.text.quickparams.TextDisplacementParams;
import general.ui.text.quickparams.UIFontParams;
import general.visual.animation.Animation;
import general.visual.animation.VisualTransition;
import general.visual.animation.VisualTransition.Progress;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class FighterInfoVisualizer {
    private final GameTimer counter = new GameTimer();
    private final FSM<FighterState> fsm = new FSM<FighterState>() {
        @Override
        public boolean stateAllowed(FsmState<FighterState> st) {
            return getState() != st; //must be a changed state
        }

        @Override
        public void onStateSet(FsmState<FighterState> currentState, FsmState<FighterState> previousState) {
            //onStateChanged
            switch (currentState.getEnum()) {
                case Fighting:
                    break;
                case ApplyingDeath:
                    counter.reset();
                    break;
                case GainingExp:
                    gui.gainExp();
                    break;
                case LevelUp:
                    gui.levelUp();
                    break;
                case LevelUpDone:
                    break;
                case Finished:
                    //AYO THIS IS WHERE EVERYTHING IS APPLIED
                    gui.getForecast().finishFight(); //applies stats to unit and then updates unit data
                    break;
            }
        }
    };
    
    private final BattleSprite sprite;
    private final FighterGUI gui;
    private final BoxMetadata battleBoxInfo;
    
    public FighterInfoVisualizer(BattleSprite sprite, FighterGUI gui, BoxMetadata battleBoxInfo) {
        this.sprite = sprite;
        this.gui = gui;
        this.battleBoxInfo = battleBoxInfo;
    }
    
    public FSM getFSM() { return fsm; }
    public FighterGUI getGUI() { return gui; }
    public BoxMetadata getBattleBoxInfo() { return battleBoxInfo; }
    
    public boolean fightIsFullyDone() {
        return fsm.getEnumState() == FighterState.Finished;
    }
    
    public void update(float tpf) {
        updateAI(tpf);
        gui.update(tpf);
        counter.update(tpf);
    }
    
    public void updateAI(float tpf) {
        switch (fsm.getEnumState()) {
            case Fighting:
                break;
            case ApplyingDeath:
                sprite.getMaterial().setColor("Color", BattleSprite.DIE_FUNCTION.rgba(counter.getTime()));
                if (counter.getTime() >= BattleSprite.DIE_FUNCTION_LENGTH) { //1.5 seconds to die
                    sprite.removeFromParent();
                    fsm.setNewStateIfAllowed(FighterState.Finished);
                }
                break;
            case GainingExp:
                if (gui.getExpBar().isQueueEmpty()) { //finished gaining exp
                    boolean doesLevelUp = gui.getCombatant().subtractEXPifLevelUp();
                    if (doesLevelUp) {
                        fsm.setNewStateIfAllowed(FighterState.LevelUp);
                    } else {
                        fsm.setNewStateIfAllowed(FighterState.Finished);
                    }
                }
                break;
            case LevelUp:
                if (gui.getLevelUpPanel() != null && gui.getLevelUpPanel().isQueueFinished()) {
                    fsm.setNewStateIfAllowed(FighterState.LevelUpDone);
                }
                break;
            case Finished:
                break;
        }
    }
    
    public void onFightOver() { //decisionData.isFightOver(), this is not fightIsFullyDone()
        if (fsm.getEnumState() == FighterState.Fighting && gui.HPandTPdrainsFinished()) {
            if (gui.getCombatant().getBaseStat(BaseStat.CurrentHP) <= 0) { //if no hp, fighter dies
                fsm.setNewStateIfAllowed(FighterState.ApplyingDeath);
            } else {
                fsm.setNewStateIfAllowed(FighterState.GainingExp);
            }
        }
    }
    
    public FighterAnimationController.AnimationParams onStartPossibleModification() {
        List<VisualTransition> animations = new ArrayList<>();
        
        VisualTransition hpAnimation = modifyHP(), tpAnimation = modifyTP();
        if (hpAnimation != null) {
            animations.add(hpAnimation);
        }
        
        if (tpAnimation != null) {
            animations.add(tpAnimation);
        }
        
        return new FighterAnimationController.AnimationParams(
            (tpf) -> {
                for (VisualTransition animation : animations) {
                    animation.update(tpf);
                }
            },
            (enemySprite) -> {
                if (!gui.getHPHeart().isQueueEmpty() || !gui.getTPBall().isQueueEmpty()) {
                    return false;
                }
                
                for (VisualTransition animation : animations) {
                    if (animation.getTransitionProgress() != Progress.Finished) {
                        return false;
                    }
                }
                
                return true;
            }
        );
    }
    
    public FighterAnimationController.AnimationParams onReceiveImpact(Strike currentStrike) {
        VisualTransition animation = hpImpact(currentStrike);
        
        return new FighterAnimationController.AnimationParams(
            (tpf) -> {
                //damage number rising
                animation.update(tpf);
            },
            (enemySprite) -> {
                boolean finished;
                
                //return true if damage number is finished showing up and hp is finished draining
                if (currentStrike.didHit()) {
                    finished = animation.getTransitionProgress() == Progress.Finished && gui.getHPHeart().isQueueEmpty();
                } else {
                    finished = animation.getTransitionProgress() == Progress.Finished;
                }
                
                return finished;
            }
        );
    }
    
    private VisualTransition hpImpact(Strike currentStrike) {
        Text2D text = generateDamageText(
            currentStrike.getDamage(),
            currentStrike.didHit(),
            false, //not tp
            gui.getAssetManager(),
            battleBoxInfo
        );
        
        VisualTransition animation = createRisingText(text, 0.75f);
        
        BaseStat defensiveStat = ((DamageTool)currentStrike.getStriker().combatant.getUnit().getEquippedTool()).getDamageMeasuredAgainstStat();
        
        System.out.println("current to max hp ratio: " + gui.getCombatant().getCurrentToMaxHPRatio());
        
        gui.getHPHeart().proceedToPercent(
            gui.getCombatant().getCurrentToMaxHPRatio(),
            gui.getCombatant().secondsToDrain(
                BaseStat.CurrentHP, 
                defensiveStat, 
                currentStrike.getDamage(), 
                currentStrike.isCrit()
            )
        );
        
        return animation;
    }
    
    private VisualTransition modifyHP() {
        if (gui.getCombatant().getCurrentToMaxHPRatio() == gui.getHPHeart().getPercent()) {
            return null;
        }
        
        float seconds = 0.25f;
        gui.getHPHeart().proceedToPercent(
            gui.getCombatant().getCurrentToMaxHPRatio(),
            seconds
        );
        
        int dmg = gui.getCombatant().getBaseStat(BaseStat.CurrentHP) - gui.getHPHeart().getCurrentNumber();
        
        Text2D text = generateDamageText(
            dmg,
            gui.getAssetManager(),
            battleBoxInfo
        );
        
        return createRisingText(text, 0.75f);
    }
    
    private VisualTransition modifyTP() {
        if (gui.getCombatant().getCurrentToMaxTPRatio() == gui.getTPBall().getPercent()) {
            return null;
        }
        
        float seconds = 0.25f;
        gui.getTPBall().proceedToPercent(gui.getCombatant().getCurrentToMaxTPRatio(), seconds);
        
        int dmg = gui.getCombatant().getBaseStat(BaseStat.CurrentHP) - gui.getHPHeart().getCurrentNumber();
        
        Text2D text = generateTPText(
            dmg,
            gui.getAssetManager(),
            battleBoxInfo
        );
        
        return createRisingText(text, 0.5f);
    }
    
    private VisualTransition createRisingText(Text2D text, float seconds) {
        VisualTransition animation = new VisualTransition(text);
        
        animation.beginTransitions(
            Animation.CleanOpacityShift((Text2D destination, ColorRGBA param) -> {
                //System.err.println("Setting Text Color: " + param.toString());
                destination.setTextAlpha(param.a);
            }).setFunction(new CartesianFunction() {
                @Override
                protected float f(float x) {
                    return -8f * FastMath.pow(x - 0.5f, 2f) + 2f; // -8(x - 0.5)^(2) + 2
                }
            }).setLength(seconds)
            //,
            //Animation.MoveDirection2D(FastMath.HALF_PI, 0.175f * battleBoxInfo.verticalLength()).setLength(seconds).setInitialAndEndVals(0f, 1f)
        );
        
        animation.onFinishTransitions(() -> {
            sprite.getParent().detachChild(text);
        });
        
        sprite.getParent().attachChild(text);
        
        /*
        Vector2f damageNumberPos = battleBoxInfo.boxLengths().multLocal(sprite.getDamageNumberLocationPercent());
        damageNumberPos.x += battleBoxInfo.getLeftEdgePosition();
        damageNumberPos.y += battleBoxInfo.getBottomEdgePosition();
        text.setLocalTranslation(Vector3F.fit(damageNumberPos, 0.011f));
        */
        
        text.setLocalTranslation(sprite.getDamageNumberLocation());
        
        //Vector2f relativeTranslation = sprite.vectorInPositiveDirection(sprite.getDamageNumberLocation(), true);
        //text.move(relativeTranslation.x, relativeTranslation.y, 0.011f);
        
        return animation;
    }
    
    
    public static Text2D generateDamageText(int dmg, AssetManager assetManager, BoxMetadata battleBoxInfo) {
        return generateDamageText(dmg, false, false, assetManager, battleBoxInfo);
    }
    
    public static Text2D generateTPText(int dmg, AssetManager assetManager, BoxMetadata battleBoxInfo) {
        return generateDamageText(dmg, false, true, assetManager, battleBoxInfo);
    }
    
    public static Text2D generateMissText(AssetManager assetManager, BoxMetadata battleBoxInfo) {
        return generateDamageText(0, true, false, assetManager, battleBoxInfo);
    }
    
    public static Text2D generateBlockedText(AssetManager assetManager, BoxMetadata battleBoxInfo) {
        return generateDamageText(0, false, false, assetManager, battleBoxInfo);
    }
    
    private static Text2D generateDamageText(int dmg, boolean isMiss, boolean isTP, AssetManager assetManager, BoxMetadata battleBoxInfo) {
        String text;
        ColorRGBA textColor;
        if (isTP) {
            text = ("" + Math.abs(dmg));
            textColor = new ColorRGBA(0.85f, 0.36f, 0.83f, 1f);
        } else if (dmg > 0) {
            text = "" + dmg;
            textColor = ColorRGBA.Red;
        } else if (dmg < 0) {
            text = ("" + Math.abs(dmg));
            textColor = ColorRGBA.Green;
        } else { // dmg == 0
            if (isMiss) {
                text = "Miss!";
                textColor = new ColorRGBA(0.5f, 1f, 1f, 1f);
            } else {
                text = "Blocked!";
                textColor = ColorRGBA.White;
            }
        }
        
        /*
        Text2D dmgText = GuiFactory.generateText(
            text, 
            textColor, 
            new Rectangle(0f, 0f, 0.3f * battleBoxInfo.horizontalLength(), 0.12f * battleBoxInfo.verticalLength()), 
            new UIFontParams("Interface/Fonts/superstar.ttf", 20f, Style.Plain, 3),
            new TextDisplacementParams(StringContainer.Align.Left, StringContainer.VAlign.Top, StringContainer.WrapMode.CharClip), 
            assetManager
        );
        */
        
        TextProperties textParams = TextProperties.builder()
                .horizontalAlignment(StringContainer.Align.Left)
                .verticalAlignment(StringContainer.VAlign.Top)
                .kerning(3)
                .wrapMode(StringContainer.WrapMode.CharClip)
                //.textBox(new Rectangle(0f, 0f, 0.3f * battleBoxInfo.horizontalLength(), 0.12f * battleBoxInfo.verticalLength()))
                .build();
        
        float fontSize = 75f;
        
        FontProperties fontParams = new FontProperties(
            "Interface/Fonts/superstar.ttf",
            FontProperties.KeyType.BMP,
            Style.Plain,
            fontSize
        );
        
        Text2D dmgText = new Text2D(text, textColor, textParams, fontParams, assetManager);
        //dmgText.fitInTextBox(0f);
        //dmgText.setOutlineMaterial(textColor, ColorRGBA.Black);
        
        dmgText.scale(1 / fontSize);
        
        return dmgText;
    }
}
