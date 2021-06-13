/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.participant.visual;

import battle.data.Strike;
import battle.gui.CombatantUI;
import com.atr.jme.font.util.StringContainer;
import com.atr.jme.font.util.Style;
import com.jme3.asset.AssetManager;
import com.jme3.font.Rectangle;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import enginetools.ParamSetter;
import etherealtempest.FSM;
import etherealtempest.FSM.FighterState;
import etherealtempest.FsmState;
import etherealtempest.Globals;
import fundamental.stats.BaseStat;
import fundamental.tool.DamageTool;
import general.GameTimer;
import general.math.function.CartesianFunction;
import general.ui.text.FontProperties;
import general.ui.text.Text2D;
import general.ui.text.TextProperties;
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
        public void setNewStateIfAllowed(FsmState<FighterState> st) {
            if (state != st) {
                state = st;
                onStateChanged();
            }
        }
        
        private void onStateChanged() {
            switch (state.getEnum()) {
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
                    gui.getForecast().finishFight(); //applies stats to unit and then updates unit data
                    break;
            }
        }
    };
    
    private final BattleSprite sprite;
    private final CombatantUI gui;
    private final Vector2f battleBoxDimensions;
    
    public FighterInfoVisualizer(BattleSprite sprite, CombatantUI gui, Vector2f battleBoxDimensions) {
        this.sprite = sprite;
        this.gui = gui;
        this.battleBoxDimensions = battleBoxDimensions;
    }
    
    public CombatantUI getGUI() { return gui; }
    public FSM getFSM() { return fsm; }
    
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
                if (counter.getTime() >= 1.5f) { //1.5 seconds to die
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
    
    public FighterAnimationController.AnimationParams onReceiveImpact(Strike currentStrike) {
        VisualTransition animation = hpImpact(currentStrike);
        
        return new FighterAnimationController.AnimationParams(
            (tpf) -> {
                //damage number rising
                animation.update(tpf); 
            },
            (enemySprite) -> {
                //return true if damage number is finished showing up and hp is finished draining
                if (currentStrike.didHit()) {
                    return animation.getTransitionProgress() == Progress.Finished && gui.getHPHeart().isQueueEmpty();
                }
                
                return animation.getTransitionProgress() == Progress.Finished;
            }
        );
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
    
    private VisualTransition hpImpact(Strike currentStrike) {
        Text2D text = generateDamageText(
            currentStrike.getDamage(),
            currentStrike.didHit(),
            false, //not tp
            gui.getAssetManager(),
            battleBoxDimensions
        );
        
        VisualTransition animation = createRisingText(text, 0.75f);
        
        BaseStat defensiveStat = ((DamageTool)currentStrike.getStriker().combatant.getUnit().getEquippedTool()).getDamageMeasuredAgainstStat();
        
        gui.getHPHeart().queueToPercent(
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
        gui.getHPHeart().queueToPercent(
            gui.getCombatant().getCurrentToMaxHPRatio(),
            seconds
        );
        
        int dmg = gui.getCombatant().getBaseStat(BaseStat.CurrentHP) - gui.getHPHeart().getCurrentNumber();
        
        Text2D text = generateDamageText(
            dmg,
            gui.getAssetManager(),
            battleBoxDimensions
        );
        
        return createRisingText(text, 0.75f);
    }
    
    private VisualTransition modifyTP() {
        if (gui.getCombatant().getCurrentToMaxTPRatio() == gui.getTPBall().getPercent()) {
            return null;
        }
        
        float seconds = 0.25f;
        gui.getTPBall().queueToPercent(gui.getCombatant().getCurrentToMaxTPRatio(), seconds);
        
        int dmg = gui.getCombatant().getBaseStat(BaseStat.CurrentHP) - gui.getHPHeart().getCurrentNumber();
        
        Text2D text = generateTPText(
            dmg,
            gui.getAssetManager(),
            battleBoxDimensions
        );
        
        return createRisingText(text, 0.4f);
    }
    
    private VisualTransition createRisingText(Text2D text, float seconds) {
        VisualTransition animation = new VisualTransition(text);
        animation.beginTransitions(
            Animation.CleanOpacityShift((Text2D destination, ColorRGBA param) -> {
                destination.setTextColor(param);
            }).setFunction(new CartesianFunction() {
                @Override
                protected float f(float x) {
                    return -8f * FastMath.pow(x - 0.5f, 2f) + 2f; // -8(x - 0.5)^(2) + 2
                }
            }),
            Animation.MoveDirection2D(FastMath.HALF_PI, 0.175f * battleBoxDimensions.y).setLength(seconds)
        );
        
        animation.setResetProtocol(() -> { sprite.getParent().detachChild(text); });
        
        sprite.getParent().attachChild(text);
        
        Vector2f relativeTranslation = sprite.vectorInPositiveDirection(sprite.getDamageNumberLocation(), false);
        text.setLocalTranslation(sprite.getLocalTranslation().add(relativeTranslation.x, relativeTranslation.y, 0));
        
        return animation;
    }
    
    
    public static Text2D generateDamageText(int dmg, AssetManager assetManager, Vector2f battleBoxDimensions) {
        return generateDamageText(dmg, false, false, assetManager, battleBoxDimensions);
    }
    
    public static Text2D generateTPText(int dmg, AssetManager assetManager, Vector2f battleBoxDimensions) {
        return generateDamageText(dmg, false, true, assetManager, battleBoxDimensions);
    }
    
    public static Text2D generateMissText(AssetManager assetManager, Vector2f battleBoxDimensions) {
        return generateDamageText(0, true, false, assetManager, battleBoxDimensions);
    }
    
    public static Text2D generateBlockedText(AssetManager assetManager, Vector2f battleBoxDimensions) {
        return generateDamageText(0, false, false, assetManager, battleBoxDimensions);
    }
    
    private static Text2D generateDamageText(int dmg, boolean isMiss, boolean isTP, AssetManager assetManager, Vector2f battleBoxDimensions) {
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
        
        TextProperties textParams = TextProperties.builder()
                .horizontalAlignment(StringContainer.Align.Left)
                .verticalAlignment(StringContainer.VAlign.Center)
                .kerning(3)
                .wrapMode(StringContainer.WrapMode.Clip)
                .textBox(new Rectangle(0f, 0f, 0.15f * battleBoxDimensions.x, 0.06f * battleBoxDimensions.y))
                .build();
        
        float fontSize = 40;
        
        FontProperties fontParams = new FontProperties("Interface/Fonts/Montaga-Regular.ttf", FontProperties.KeyType.BMP, Style.Plain, fontSize);
        
        Text2D dmgText = new Text2D(text, textColor, textParams, fontParams, assetManager);
        dmgText.setOutlineMaterial(textColor, ColorRGBA.Black);
        
        return dmgText;
    }
}
