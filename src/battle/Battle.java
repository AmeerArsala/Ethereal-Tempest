/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle;

import battle.Battle.ImpactType;
import battle.Combatant.BaseStat;
import battle.DamageNumber.VisibilityState;
import battle.StatArrowGroup.ArrowStat;
import battle.Toll.Exchange;
import battle.skill.Skill;
import com.atr.jme.font.TrueTypeBMP;
import com.atr.jme.font.TrueTypeFont;
import com.atr.jme.font.asset.TrueTypeKeyBMP;
import com.atr.jme.font.util.StringContainer;
import com.atr.jme.font.util.Style;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Quad;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.ProgressBar;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.component.TbtQuadBackgroundComponent;
import edited.EditedLabel;
import general.GeneralUtils;
import general.GeneralUtils.CenterAxis;
import general.ResetProtocol;
import general.VisualTransition.Progress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import maps.layout.Cursor.Purpose;
import maps.layout.TangibleUnit;
import maps.layout.TangibleUnit.BattleRole;
import misc.CustomAnimationSegment;
import general.Submenu.TransitionState;
import general.visual.RadialProgressBar;
import misc.FrameDelay;

/**
 *
 * @author night
 */
public class Battle {
    
    public enum ImpactType {
        SoundOnly,
        All,
        None
    }
    
    public enum BattleState {
        Fighting,
        AfterStrikes, //Dying
        ExpInit,
        ExpGain,
        LevelUpInit,
        TransitionToLevelUp,
        LevelUp,
        PostLevelUp,
        TransitionToFullyDone,
        FullyDone;
    }
    
    public boolean allowUpdate = true;
    private BattleState battleState;
    
    private final Conveyer info;
    private final Camera battleCamera;
    private final Purpose battlePurpose;
    private final Node battleArea = new Node("battleNode"), particleNode = new Node("special effects node");
    
    private final PrebattleForecast forecast;
    private final List<Strike> strikes;
    
    private Combatant initiator, receiver;
    private Progress battleProgress;
    
    //private EffekseerControl effectControlInitiator, effectControlReceiver;
    //private boolean initiatorControlAdded = false, receiverControlAdded = false;
    //private final Node initiatorEffects = new Node(), receiverEffects = new Node();
    
    private Node gui, masterNode, actualGuiNode;
    private int strikeIndex = 0;
    private String skillString = null;
    
    private float worldQuadWidth, worldQuadHeight;
    private DamageNumber dmgView = null;
    private TrueTypeFont dmgFont;//, expFont, expFont2;
    //private TrueTypeNode expTextInitiator, expTextReceiver;
    
    public Battle(Conveyer data, int attackRange, Camera cam, Purpose P) {
        battleProgress = Progress.Fresh;
        info = data;
        battleCamera = cam;
        battlePurpose = P;
        
        boolean skill = false;
        Combatant initTemp = new Combatant(info, BattleRole.Initiator), receiverTemp = new Combatant(info, BattleRole.Receiver);
        initTemp.setExtraDamage(initTemp.getUnit().getEquippedWeapon().extraDamage);
        receiverTemp.setExtraDamage(receiverTemp.getUnit().getEquippedWeapon().extraDamage);
        if (battlePurpose == Purpose.SkillAttack) {
            if (info.getUnit().getToUseSkill().getToll().getType() == Exchange.HP) {
                info.getUnit().currentHP -= info.getUnit().getToUseSkill().getToll().getValue();
            } else if (info.getUnit().getToUseSkill().getToll().getType() == Exchange.TP) {
                info.getUnit().currentTP -= info.getUnit().getToUseSkill().getToll().getValue();
            }
            
            initTemp = new Combatant(info, BattleRole.Initiator);
            skill = true;
            initTemp.getUnit().getToUseSkill().getEffect().applyEffectsOnCombat(initTemp);
            initTemp.setExtraDamage(initTemp.getUnit().getToUseSkill().getEffect().extraDamage() + initTemp.getUnit().getEquippedWeapon().extraDamage);
        } else if (battlePurpose == Purpose.EtherAttack) {
            //info.getUnit().currentHP -= info.getUnit().getToUseFormula().getHPUsage();
            //info.getUnit().currentTP -= info.getUnit().getToUseFormula().getTPUsage();
            
            initTemp.setHPtoSubtract(info.getUnit().getToUseFormula().getHPUsage());
            initTemp.setTPtoSubtract(info.getUnit().getToUseFormula().getTPUsage());
            
            initTemp.setEffectControl(info.getUnit().getToUseFormula().getControl());
        }
        
        try {
            receiverTemp.setHPtoSubtract(info.getEnemyUnit().getToUseFormula().getHPUsage());
            receiverTemp.setTPtoSubtract(info.getEnemyUnit().getToUseFormula().getTPUsage());
            
            receiverTemp.setEffectControl(info.getEnemyUnit().getToUseFormula().getControl());
        }
        catch (NullPointerException e) {}
        
        particleNode.attachChild(initTemp.getEffectsNode());
        particleNode.attachChild(receiverTemp.getEffectsNode());
        particleNode.move(0, 0, 5.5f);
        
        forecast = 
                new PrebattleForecast(
                    initTemp,
                    receiverTemp,
                    info,
                    attackRange,
                    initTemp.getUnit().getToUseSkill()
                );
        initiator = forecast.getInitiatorForecast().getCombatant();
        receiver = forecast.getReceiverForecast().getCombatant();
        
        if (!forecast.getReceiverForecast().canCounterattack) {
            receiver.BP = 0;
        }
        
        strikes = new ArrayList<>();
        for (int k = 0; initiator.BP > 0 || receiver.BP > 0; k++) {
            if (k % 2 == 0) { //even; initiator's strike
                if (initiator.BP > 0) {
                    Strike event = new Strike(initiator, receiver, info, skill);
                    strikes.add(event);
                    event.getExtraStrikes().forEach((S) -> { strikes.add(S); });
                    initiator.BP -= forecast.getInitiatorForecast().BPcostPerHit;
                }
            } else { //odd; receiver's strike
                if (receiver.BP > 0) {
                    Strike event = new Strike(receiver, initiator, info, false);
                    strikes.add(event);
                    event.getExtraStrikes().forEach((S) -> { strikes.add(S); });
                    receiver.BP -= forecast.getReceiverForecast().BPcostPerHit;
                }
            }
        }
    }
    
    private int nextIndex() {
        for (int i = 0; i < strikes.size(); i++) {
            if (!strikes.get(i).occurred) {
                return i;
            }
        }
        
        return -1;
    }
    
    public void update(float tpf) {
        if (dmgView != null) {
            dmgView.update(tpf);
            if (battleProgress != Progress.Progressing) {
                if (dmgView.getVisibility() == VisibilityState.Visible) {
                    dmgView.setTransitionState(TransitionState.TransitioningOut);
                } else {
                    gui.detachChild(dmgView);
                    dmgView = null;
                }
            }
        }
        
        if (battleProgress == Progress.Progressing) {
            initiator.updateEffects(tpf);
            receiver.updateEffects(tpf);
        }
        
        if (battleProgress == Progress.Progressing && allowUpdate) {
            if (strikes.get(strikeIndex).occurred || (strikes.get(strikeIndex).getStriker().figure.getProgress() == Progress.Finished && strikes.get(strikeIndex).getVictim().figure.getProgress() == Progress.Finished)) {
                strikes.get(strikeIndex).occurred = true;
                
                if (strikes.get(strikeIndex).getStriker().getUnit().getToUseFormula() == null) {
                    strikes.get(strikeIndex).getStriker().getUnit().getEquippedWeapon().used(strikes.get(strikeIndex).getStrikerDurabilityChange());
                }
                
                strikes.get(strikeIndex).getStriker().figure.totalDmgDone += strikes.get(strikeIndex).getDamage();
                
                strikeIndex++;
                
                if (strikeIndex >= strikes.size() || initiator.getBaseStat(BaseStat.currentHP) <= 0 || receiver.getBaseStat(BaseStat.currentHP) <= 0) {
                    battleProgress = Progress.Finished;
                    battleState = BattleState.AfterStrikes;
                    initiator.applyAllStatsToUnit();
                    receiver.applyAllStatsToUnit();
                    initiator.getUnit().setToUseSkill(null);
                    initiator.figure.allowEffectUpdate = false;
                    receiver.figure.allowEffectUpdate = false;
                    System.out.println("finished");
                } else {
                    strikes.get(strikeIndex).getStriker().figure.getGeometry().setLocalTranslation(0, 0, 0.005f);
                    strikes.get(strikeIndex).getVictim().figure.getGeometry().setLocalTranslation(0, 0, 0f);
                    initiator.figure.getGeometry().move(-0.275f, 0f, 0f);
                    receiver.figure.getGeometry().move(0.275f, 0f, 0f);
                    
                    boolean indexResetS = true;
                    if (strikes.get(strikeIndex).getStriker().getUnit().getID() == initiator.getUnit().getID()) {
                        switch (battlePurpose) {
                            case WeaponAttack:
                                if (strikeIndex + 1 < strikes.size() && strikes.get(strikeIndex + 1).getStriker() == strikes.get(strikeIndex).getStriker() && !strikes.get(strikeIndex + 1).strikeIsCrit()) {
                                    strikes.get(strikeIndex).getStriker().figure.setCombatMode("attack_and_followup");
                                } else if (strikeIndex - 1 >= 0 && strikes.get(strikeIndex - 1).getStriker() == strikes.get(strikeIndex).getStriker() && !strikes.get(strikeIndex - 1).strikeIsCrit()) {
                                    strikes.get(strikeIndex).getStriker().figure.setCombatMode("attack_and_followup");
                                    indexResetS = false;
                                    //strikes.get(strikeIndex).getVictim().figure.sInterlude.maxFrame = 10;
                                    //strikes.get(strikeIndex).getVictim().figure.sInterlude.rate = 5;
                                } else {
                                    if (strikes.get(strikeIndex).strikeIsCrit()) {
                                        strikes.get(strikeIndex).getStriker().figure.setCombatMode("critical");
                                    } else {
                                        strikes.get(strikeIndex).getStriker().figure.setCombatMode("attack");
                                    }
                                }
                                break;
                            case SkillAttack:
                                strikes.get(strikeIndex).getStriker().figure.setCombatMode("finisher");
                                break;
                            case EtherAttack:
                                break;
                            case EtherSupport:
                                break;
                            default:
                                break;
                        }
                    } else {
                        if (strikes.get(strikeIndex).strikeIsCrit()) {
                            strikes.get(strikeIndex).getStriker().figure.setCombatMode("critical");
                        } else {
                            if (strikeIndex + 1 < strikes.size() && strikes.get(strikeIndex + 1).getStriker() == strikes.get(strikeIndex).getStriker() && !strikes.get(strikeIndex + 1).strikeIsCrit()) {
                                strikes.get(strikeIndex).getStriker().figure.setCombatMode("attack_and_followup");
                            } else if (strikeIndex - 1 >= 0 && strikes.get(strikeIndex - 1).getStriker() == strikes.get(strikeIndex).getStriker() && !strikes.get(strikeIndex - 1).strikeIsCrit()) {
                                strikes.get(strikeIndex).getStriker().figure.setCombatMode("attack_and_followup");
                                indexResetS = false;
                                //strikes.get(strikeIndex).getVictim().figure.sInterlude.maxFrame = 10;
                                //strikes.get(strikeIndex).getVictim().figure.sInterlude.rate = 5;
                            } else {
                                strikes.get(strikeIndex).getStriker().figure.setCombatMode("attack");
                            }
                        }
                    }
                    
                    strikes.get(strikeIndex).getStriker().figure.startUpdate(indexResetS);
                    strikes.get(strikeIndex).getVictim().figure.startUpdate(true);
                }
            } else {
                //for striker
                if (!strikes.get(strikeIndex).occurred) {
                    updateStriker(strikeIndex, tpf);
                }
                
                //for victim
                if (strikes.get(strikeIndex).getStriker().figure.impactStatus == ImpactType.All) {
                    //System.out.println(strikes.get(strikeIndex).getVictim().figure.sInterlude.getProgress());
                    strikes.get(strikeIndex).getVictim().figure.sInterlude.setProgressIfAllowed(strikes.get(strikeIndex), Progress.Fresh);
                    strikes.get(strikeIndex).getVictim().figure.sInterlude.setDamageToUnit(strikes.get(strikeIndex).getDamage());
                    
                    if (dmgView == null) {
                        dmgView = new DamageNumber("" + strikes.get(strikeIndex).getDamage(), gui, dmgFont);
                        dmgView.setTransitionState(TransitionState.TransitioningIn);
                        Vector3f localdims = gui.worldToLocal(new Vector3f(worldQuadWidth, worldQuadHeight, 1), null);
                        if (strikes.get(strikeIndex).getVictim().getUnit().getID() == initiator.getUnit().getID()) {
                            dmgView.move(((50 - initiator.getUnit().getBattleConfig().getPercentWidth()) / -100f) * localdims.x, (initiator.getUnit().getBattleConfig().getPercentHeight() / 100f) * localdims.y, 1);
                        } else if (strikes.get(strikeIndex).getVictim().getUnit().getID() == receiver.getUnit().getID()) {
                            dmgView.move(((50 - receiver.getUnit().getBattleConfig().getPercentWidth()) / 100f) * localdims.x, (receiver.getUnit().getBattleConfig().getPercentHeight() / 100f) * localdims.y, 1);
                        }
                        
                        if (!strikes.get(strikeIndex).strikeDidHit()) {
                            dmgView.setColor(ColorRGBA.White);
                            dmgView.setText("Miss!");
                        } else if (strikes.get(strikeIndex).strikeWasParried()) {
                            dmgView.setColor(new ColorRGBA(0.4f, 0.941f, 1f, 1f));
                            dmgView.setText("Parry!");
                        }
                    }
                    
                    if (strikes.get(strikeIndex).strikeDidHit()) {
                        strikes.get(strikeIndex).getVictim().figure.interlude(tpf, null, true, strikes.get(strikeIndex).strikeWasParried());
                    } else {
                        if (strikeIndex + 1 < strikes.size() && strikes.get(strikeIndex + 1).getStriker().getUnit().getToUseFormula() == null && !strikes.get(strikeIndex + 1).strikeIsCrit() && strikes.get(strikeIndex + 1).getStriker() != strikes.get(strikeIndex).getStriker()) {
                            if (strikes.get(strikeIndex).getVictim().figure.sInterlude.getProgress() != Progress.Finished) {
                                strikes.get(strikeIndex).getVictim().figure.interlude(tpf, "dodge", false, false); //miss animation
                            } else {
                                if (!strikes.get(strikeIndex + 1).occurred) {
                                    strikes.get(strikeIndex + 1).getStriker().figure.setOnStrike(() -> {
                                        if (strikeIndex + 1 < strikes.size()) {
                                            strikes.get(strikeIndex + 1).occurred = true;
                                        } 
                                    });
                                }
                                
                                updateStriker(strikeIndex + 1, tpf);
                                updateVictim(strikeIndex + 1, tpf);
                                if (!strikes.get(strikeIndex).occurred) {
                                    updateStriker(strikeIndex, tpf);
                                    //strikes.get(strikeIndex).getVictim().figure.interlude(tpf, null, true, strikes.get(strikeIndex).strikeWasParried());
                                }
                                
                            }
                        } else {
                            strikes.get(strikeIndex).getVictim().figure.interlude(tpf, null, false, false); //miss frame
                        }
                    }
                    //add sound later
                } else {
                    if (dmgView != null) {
                        if (dmgView.getVisibility() == VisibilityState.Visible) {
                            dmgView.setTransitionState(TransitionState.TransitioningOut);
                        } else {
                            gui.detachChild(dmgView);
                            dmgView = null;
                        }
                    }
                }
            }
        }
        
        updateAI(tpf);
        
        masterNode.updateLogicalState(tpf);
        masterNode.updateGeometricState();
    }

    private float anonymousCount = 0, anonCount2 = 0;
    
    private void updateAI(float tpf) {
        switch (battleState) {
            case Fighting:
                initiator.updateBars(tpf);
                receiver.updateBars(tpf);
                break;
            case TransitionToFullyDone:
                if (anonymousCount >= 0.7f) {
                    battleState = BattleState.FullyDone;
                }
                
                anonymousCount += tpf;
                break;
            case AfterStrikes:
                if (initiator.getUnit().currentHP <= 0) {
                    if (anonymousCount >= 1f) {
                        anonymousCount = 0;
                        battleState = BattleState.ExpInit;
                    } else {
                        initiator.figure.getGeometry().getMaterial().setColor("Color", new ColorRGBA(1, 1 - anonymousCount, 1 - anonymousCount, 1 - anonymousCount));
                        anonymousCount += 0.0325f;
                    }
                } else if (receiver.getUnit().currentHP <= 0) {
                    if (anonymousCount >= 1f) {
                        anonymousCount = 0;
                        battleState = BattleState.ExpInit;
                    } else {
                        receiver.figure.getGeometry().getMaterial().setColor("Color", new ColorRGBA(1, 1 - anonymousCount, 1 - anonymousCount, 1 - anonymousCount));
                        anonymousCount += 0.0325f;
                    }
                } else {
                    battleState = BattleState.ExpInit;
                }
                break;
            case ExpInit:
                //initialize expbars
                initiator.initializeExpCircle(actualGuiNode);
                receiver.initializeExpCircle(actualGuiNode);
                calculateEXP();
                
                battleState = BattleState.ExpGain;
                break;
            case ExpGain:
                boolean initiatorFullyDone = false, receiverFullyDone = false; //using these so it doesn't just use the initiator or receiver and ignore the other
                boolean levelUpInit = false;
                if (initiator.getUnit().currentHP > 0) {
                    if (initiator.figure.expGained > 0) {
                        initiator.gainExp();
                    } else if (initiator.getUnit().currentEXP >= 100) {
                        levelUpInit = true;
                    } else {
                        initiatorFullyDone = true;
                    }
                }
                if (receiver.getUnit().currentHP > 0) {
                    if (receiver.figure.expGained > 0) {
                        receiver.gainExp();
                    } else if (receiver.getUnit().currentEXP >= 100) {
                       levelUpInit = true;
                    } else {
                        receiverFullyDone = true;
                    }
                }
                
                if (levelUpInit) {
                    battleState = BattleState.LevelUpInit;
                } else if (initiatorFullyDone || receiverFullyDone) {
                    battleState = BattleState.TransitionToFullyDone;
                }
                break;
            case LevelUpInit:
                if (masterNode.hasChild(gui)) {
                    masterNode.detachChild(gui); //detach the health bars, tp bars, combat stats, etc.
                    if (initiator.getUnit().currentEXP >= 100 || receiver.getUnit().currentEXP >= 100) {
                        //TODO: PLAY LEVEL UP SOUND
                    }
                    
                    initiator.attemptInitializeLevelUpVisual(actualGuiNode);
                    receiver.attemptInitializeLevelUpVisual(actualGuiNode);
                }
                
                boolean initiatorDone, receiverDone;
                initiatorDone = initiator.attemptLevelUpTransition();
                receiverDone = receiver.attemptLevelUpTransition();
                
                if (initiatorDone && receiverDone) {
                    battleState = BattleState.TransitionToLevelUp;
                }
                break;
            case TransitionToLevelUp:
                initiator.figure.attemptUpdateTransitionToLevelUp(actualGuiNode, initiator.getUnit(), new Vector3f(-200, 750, 0), 10); //initiator leveled up
                receiver.figure.attemptUpdateTransitionToLevelUp(actualGuiNode, receiver.getUnit(), new Vector3f(1000, 750, 0), -10); //receiver leveled up
                
                if (anonCount2 >= 20) {
                    battleState = BattleState.LevelUp;
                }
                
                anonCount2++;
                break;
            case LevelUp:
                boolean iniLevelDone, recLevelDone;
                iniLevelDone = initiator.attemptLevelUp(tpf);
                recLevelDone = receiver.attemptLevelUp(tpf);
                
                if (iniLevelDone && recLevelDone) {
                    battleState = BattleState.PostLevelUp;
                }
                break;
            case PostLevelUp:
                break;
            default:
                break;
        }
    }
    
    public void resolveInput(String name, float tpf, boolean keyPressed) {
        if (battleState == BattleState.PostLevelUp && name.equals("select") && keyPressed) {
            battleState = BattleState.FullyDone;
        }
    }
    
    private void calculateEXP() {
        int difference = initiator.getUnit().getLVL() - receiver.getUnit().getLVL();
        int baseDamageValue = 10;
        int baseKillValue = 40;
        int initiatorBountyCoefficient = initiator.getUnit().getIsBoss() ? 2 : 1, receiverBountyCoefficient = receiver.getUnit().getIsBoss() ? 2 : 1;
        if (difference == 0) {
            if (initiator.getUnit().currentHP <= 0) { //initiator is dead (dumbass lol)
                receiver.figure.expGained = baseKillValue;
            } else if (receiver.getUnit().currentHP <= 0) { //receiver is dead
                initiator.figure.expGained = baseKillValue;
            } else { //nobody's dead
                initiator.figure.expGained = initiator.figure.totalDmgDone > 0 ? baseDamageValue : 1;
                receiver.figure.expGained = receiver.figure.totalDmgDone > 0 ? baseDamageValue : 1;
            }
        } else {
            if (difference < 6) {
                float initiatorMultiplier, receiverMultiplier;
                if (difference < 0) {
                    initiatorMultiplier = difference * -1;
                    receiverMultiplier = FastMath.pow(difference, -1) * -1;
                } else { // > 0
                    initiatorMultiplier = FastMath.pow(difference, -1);
                    receiverMultiplier = difference;
                }
                
                if (initiator.getUnit().currentHP <= 0) { //initiator is dead (dumbass lol)
                    receiver.figure.expGained = (int)(baseKillValue * receiverMultiplier);
                } else if (receiver.getUnit().currentHP <= 0) { //receiver is dead
                    initiator.figure.expGained = (int)(baseKillValue * initiatorMultiplier);
                } else { //nobody's dead
                    initiator.figure.expGained = initiator.figure.totalDmgDone > 0 ? ((int)(baseDamageValue * initiatorMultiplier)) : 1;
                    receiver.figure.expGained = receiver.figure.totalDmgDone > 0 ? ((int)(baseDamageValue * receiverMultiplier)) : 1;
                }
            }
        }
        
        initiator.figure.expGained *= initiatorBountyCoefficient;
        receiver.figure.expGained *= receiverBountyCoefficient;
        
        if (initiator.figure.expGained + initiator.getUnit().currentEXP >= 200) { //no leveling up twice
            initiator.figure.expGained = 199 - initiator.getUnit().currentEXP;
        }
        
        if (receiver.figure.expGained + receiver.getUnit().currentEXP >= 200) { //no leveling up twice
            receiver.figure.expGained = 199 - receiver.getUnit().currentEXP;
        }
    }
    
    private void updateStriker(int strikeIndex, float tpf) {
        if (strikes.get(strikeIndex).getStriker().getUnit().getToUseFormula() != null) {
            if (strikes.get(strikeIndex).strikeIsCrit()) {
                strikes.get(strikeIndex).getStriker().figure.update(tpf, "formulaCrit");
            } else {
                strikes.get(strikeIndex).getStriker().figure.update(tpf, "formulaCast");
            }
        } else if (strikeIndex - 1 >= 0 && !strikes.get(strikeIndex).strikeIsCrit() && !strikes.get(strikeIndex - 1).strikeDidHit() && strikes.get(strikeIndex - 1).getStriker() != strikes.get(strikeIndex).getStriker()) {
            strikes.get(strikeIndex).getStriker().figure.update(tpf, "retaliate"); //retaliation
        } else {
            if (strikes.get(strikeIndex).getStriker().getUnit() == initiator.getUnit() && battlePurpose == Purpose.SkillAttack && skillString != null) {
                strikes.get(strikeIndex).getStriker().figure.update(tpf, skillString);
            } else {
                strikes.get(strikeIndex).getStriker().figure.update(tpf, null); //normal attack
            }
        }
    }
    
    private void updateVictim(int strikeIndex, float tpf) {
        if (strikes.get(strikeIndex).getStriker().figure.impactStatus == ImpactType.All) {
            strikes.get(strikeIndex).getVictim().figure.sInterlude.setProgressIfAllowed(strikes.get(strikeIndex), Progress.Fresh);
            strikes.get(strikeIndex).getVictim().figure.sInterlude.setDamageToUnit(strikes.get(strikeIndex).getDamage());
            strikes.get(strikeIndex).getVictim().figure.sInterlude.maxFrame = 25;
            //strikes.get(strikeIndex).getStriker().figure.retaliating = false;
            strikes.get(strikeIndex).getStriker().figure.marker = true;
            
            if (dmgView == null) {
                dmgView = new DamageNumber("" + strikes.get(strikeIndex).getDamage(), gui, dmgFont);
                dmgView.setTransitionState(TransitionState.TransitioningIn);
                Vector3f localdims = gui.worldToLocal(new Vector3f(worldQuadWidth, worldQuadHeight, 1), null);
                if (strikes.get(strikeIndex).getVictim().getUnit().getID() == initiator.getUnit().getID()) {
                    dmgView.move(((50 - initiator.getUnit().getBattleConfig().getPercentWidth()) / -100f) * localdims.x, (initiator.getUnit().getBattleConfig().getPercentHeight() / 100f) * localdims.y, 1);
                } else if (strikes.get(strikeIndex).getVictim().getUnit().getID() == receiver.getUnit().getID()) {
                     dmgView.move(((50 - receiver.getUnit().getBattleConfig().getPercentWidth()) / 100f) * localdims.x, (receiver.getUnit().getBattleConfig().getPercentHeight() / 100f) * localdims.y, 1);
                }
            } else {
                if (strikes.get(strikeIndex).strikeDidHit()) {
                    if (strikes.get(strikeIndex).strikeWasParried()) {
                        dmgView.setColor(new ColorRGBA(0.4f, 0.941f, 1f, 1f));
                        dmgView.setText("Parry!");
                    } else {
                        dmgView.setColor(ColorRGBA.Red);
                        dmgView.setText("" + strikes.get(strikeIndex).getDamage());
                    }
                } else {
                    dmgView.setColor(ColorRGBA.White);
                    dmgView.setText("Miss!");
                }
            }
            
            /*if (dmgView != null) {
                dmgView.update(tpf);
            } else {
                dmgView = new DamageNumber("" + strikes.get(strikeIndex).getDamage(), gui, dmgFont);
                dmgView.setTransitionState(TransitionState.TransitioningIn);
                Vector3f localdims = gui.worldToLocal(new Vector3f(worldQuadWidth, worldQuadHeight, 1), null);
                if (strikes.get(strikeIndex).getVictim().getUnit().getID() == initiator.getUnit().getID()) {
                    dmgView.move(((50 - initiator.getUnit().getBattleConfig().getPercentWidth()) / -100f) * localdims.x, (initiator.getUnit().getBattleConfig().getPercentHeight() / 100f) * localdims.y, 1);
                } else if (strikes.get(strikeIndex).getVictim().getUnit().getID() == receiver.getUnit().getID()) {
                     dmgView.move(((50 - receiver.getUnit().getBattleConfig().getPercentWidth()) / 100f) * localdims.x, (receiver.getUnit().getBattleConfig().getPercentHeight() / 100f) * localdims.y, 1);
                }
                
                if (!strikes.get(strikeIndex).strikeDidHit()) {
                    dmgView.setColor(ColorRGBA.White);
                    dmgView.setText("Miss!");
                } else if (strikes.get(strikeIndex).strikeWasParried()) {
                    dmgView.setColor(new ColorRGBA(0.4f, 0.941f, 1f, 1f));
                    dmgView.setText("Parry!");
                }
            }*/
            
            if (strikes.get(strikeIndex).strikeDidHit()) {
                strikes.get(strikeIndex).getVictim().figure.interlude(tpf, null, true, strikes.get(strikeIndex).strikeWasParried());
            } else {
                if (strikeIndex + 1 < strikes.size() && !strikes.get(strikeIndex + 1).strikeIsCrit() && strikes.get(strikeIndex + 1).getStriker() != strikes.get(strikeIndex).getStriker()) {
                    strikes.get(strikeIndex).getVictim().figure.interlude(tpf, "dodge", false, false); //miss animation
                } else {
                    strikes.get(strikeIndex).getVictim().figure.interlude(tpf, null, false, false); //miss frame
                }
            }
            //add sound later
        } else {
            if (dmgView != null) {
                dmgView.update(tpf);
                if (dmgView.getVisibility() == VisibilityState.Visible) {
                    dmgView.setTransitionState(TransitionState.TransitioningOut);
                } else {
                    gui.detachChild(dmgView);
                    dmgView = null;
                }
            }
        }
    }
    
    public PrebattleForecast getForecast() { return forecast; }
    public List<Strike> getStrikes() { return strikes; }
    
    public Combatant getInitiator() { return initiator; }
    public Combatant getReceiver() { return receiver; }
    
    public Progress getBattleProgress() { return battleProgress; }
    public BattleState getBattleState() { return battleState; }
    
    public void initializeVisuals(Node master, Node terrain, Node guiNode, AssetManager assetManager, Node actualGuiNode) {
        TrueTypeKeyBMP ttk = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular.ttf", Style.Plain, 40);
        dmgFont = (TrueTypeBMP)assetManager.loadAsset(ttk);
        
        TrueTypeKeyBMP bmp = new TrueTypeKeyBMP("Interface/Fonts/Neuton-Regular.ttf", Style.Plain, 40);
        initiator.setExpFont((TrueTypeBMP)assetManager.loadAsset(bmp));
        
        TrueTypeKeyBMP bmp2 = new TrueTypeKeyBMP("Interface/Fonts/Neuton-Regular.ttf", Style.Plain, 41);
        receiver.setExpFont((TrueTypeBMP)assetManager.loadAsset(bmp2));
        receiver.getExpFont().setScale(40f / 41f);
        
        Material levelUpText = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        levelUpText.setTexture("ColorMap", assetManager.loadTexture("Interface/GUI/general_ui/levelup.png"));
        levelUpText.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        initiator.setLevelUpText(levelUpText.clone());
        receiver.setLevelUpText(levelUpText.clone());
        
        initiator.getUnit().initializeFrames(assetManager);
        receiver.getUnit().initializeFrames(assetManager);
        
        masterNode = master;
        this.actualGuiNode = actualGuiNode;
        initializeGUI(guiNode, assetManager);
        
        StrikeInterlude.masterNode = masterNode;
        
        //9.6x5.4
        Quad initiatorQuad = new Quad(9.6f, 5.4f), receiverQuad = new Quad(9.6f, 5.4f);
        
        initiatorQuad.setBuffer(VertexBuffer.Type.TexCoord, 2, 
                new float[]
                    {1, 0,
                     0, 0,
                     0, 1,
                     1, 1}
        );
        
        initiator.figure = new ShownCombatant(
                BattleRole.Initiator,
                initiatorQuad,
                new Geometry("initiatorQ", initiatorQuad), 
                assetManager, 
                "attack", 
                initiator.getUnit(),
                new StrikeInterlude(initiator),
                new CustomAnimationSegment
                (
                        "attack",
                        thickenArray(new int[] {31, 32, 33, 34}, 3)
                ),
                34
        ).addAnimation(
                "retaliate", 
                new CustomAnimationSegment(
                        "finisher",
                        combineArray(allIntsFromTo(7, 30), allIntsFromTo(47, 59))
                )
        ).addAnimation(
                "formulaCast",
                new CustomAnimationSegment(
                        "critical",
                        FrameDelay.combineArray(
                                FrameDelay.allIntsFromTo(30, 38, 0, new Vector3f(5.2f, 0, 0)),
                                FrameDelay.combineArray(
                                        FrameDelay.combineArray(
                                                FrameDelay.allIntsFromTo(18, 19, 0.125f, new Vector3f(0, 0, 0)), 
                                                new FrameDelay[] { new FrameDelay(20, 0.25f, new Vector3f(0, 0, 0)) }
                                        ),
                                        FrameDelay.allIntsFromTo(81, 94, 0, new Vector3f(-5.2f, 0, 0))
                                )
                        ),
                        20
                )
        ).addAnimation(
                "formulaCrit",
                new CustomAnimationSegment(
                        "critical",
                        FrameDelay.combineArray(
                                FrameDelay.allIntsFromTo(30, 38, 0, new Vector3f(5.2f, 0, 0)),
                                FrameDelay.combineArray(
                                        FrameDelay.combineArray(
                                                FrameDelay.allIntsFromTo(18, 19, 0.025f, new Vector3f(0, 0, 0)), 
                                                new FrameDelay[] { new FrameDelay(20, 1f, new Vector3f(0, 0, 0)) }
                                        ),
                                        FrameDelay.allIntsFromTo(81, 94, 0, new Vector3f(-5.2f, 0, 0))
                                )
                        ),
                        20
                )
        );
        
        receiver.figure = new ShownCombatant(
                BattleRole.Receiver,
                receiverQuad,
                new Geometry("receiverQ", receiverQuad),
                assetManager,
                "attack",
                receiver.getUnit(),
                new StrikeInterlude(receiver),
                new CustomAnimationSegment
                (
                        "attack",
                        thickenArray(new int[] {31, 32, 33, 34}, 3)
                ),
                34
        ).addAnimation(
                "retaliate", 
                new CustomAnimationSegment(
                        "finisher",
                        thickenArray(combineArray(allIntsFromTo(7, 30), allIntsFromTo(47, 59)), 8)
                )
        ).addAnimation(
                "formulaCast",
                new CustomAnimationSegment(
                        "critical",
                        FrameDelay.combineArray(
                                FrameDelay.allIntsFromTo(30, 38, 0, new Vector3f(5.2f, 0, 0)),
                                FrameDelay.combineArray(
                                        FrameDelay.combineArray(
                                                FrameDelay.allIntsFromTo(18, 19, 0.125f, new Vector3f(0, 0, 0)), 
                                                new FrameDelay[] { new FrameDelay(20, 0.25f, new Vector3f(0, 0, 0)) }
                                        ),
                                        FrameDelay.allIntsFromTo(81, 94, 0, new Vector3f(-5.2f, 0, 0))
                                )
                        ),
                        20
                )
        ).addAnimation(
                "formulaCrit",
                new CustomAnimationSegment(
                        "critical",
                        FrameDelay.combineArray(
                                FrameDelay.allIntsFromTo(30, 38, 0, new Vector3f(5.2f, 0, 0)),
                                FrameDelay.combineArray(
                                        FrameDelay.combineArray(
                                                FrameDelay.allIntsFromTo(18, 19, 0.025f, new Vector3f(0, 0, 0)), 
                                                new FrameDelay[] { new FrameDelay(20, 1f, new Vector3f(0, 0, 0)) }
                                        ),
                                        FrameDelay.allIntsFromTo(81, 94, 0, new Vector3f(-5.2f, 0, 0))
                                )
                        ),
                        20
                )
        );
        
        //initiator.figure.getGeometry().move(0, 0.5f, 0);
        
        battleArea.attachChild(initiator.figure.getGeometry());
        battleArea.attachChild(receiver.figure.getGeometry());
        battleArea.move(-4.65f, -0.55f, 7f);
        masterNode.attachChild(battleArea);
        
        Vector3f worldCoord = battleArea.localToWorld(new Vector3f(9.6f, 5.4f, 1f), null);
        worldQuadWidth = worldCoord.x;
        worldQuadHeight = worldCoord.y;
        
        //initiator.figure.getGeometry().move(-5.2f, -0.85f, 7f);
        //receiver.figure.getGeometry().move(-4.8f, -0.85f, 7f);
        
        Material sprite = new Material(assetManager, "MatDefs/Spritesheet.j3md");
        sprite.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        sprite.setTexture("ColorMap", initiator.getUnit().getCombatSheet());
        sprite.setFloat("SizeX", initiator.getUnit().getBattleConfig().getColumnCount());
        sprite.setFloat("SizeY", initiator.getUnit().getBattleConfig().getRowCount());
        initiator.figure.getGeometry().setMaterial(sprite);
        
        
        Material sprite2 = new Material(assetManager, "MatDefs/Spritesheet.j3md");
        sprite2.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        sprite2.setTexture("ColorMap", receiver.getUnit().getCombatSheet());
        sprite2.setFloat("SizeX", receiver.getUnit().getBattleConfig().getColumnCount());
        sprite2.setFloat("SizeY", receiver.getUnit().getBattleConfig().getRowCount());
        receiver.figure.getGeometry().setMaterial(sprite2);
        
        
        strikeIndex = 0;
        initiator.figure.index = 0;
        receiver.figure.index = 0;
        
        initiator.figure.setOpponent(receiver.figure);
        receiver.figure.setOpponent(initiator.figure);
        
        initiator.figure.startUpdate(true);
        receiver.figure.startUpdate(true);
        if (strikes.get(0).strikeIsCrit()) {
            strikes.get(0).getStriker().figure.setCombatMode("critical");
        }
        
        strikes.get(0).getStriker().figure.getGeometry().setLocalTranslation(0, 0, 0.005f);
        strikes.get(0).getVictim().figure.getGeometry().setLocalTranslation(0, 0, 0f);
        initiator.figure.getGeometry().move(-0.275f, 0f, 0f);
        receiver.figure.getGeometry().move(0.275f, 0f, 0f);
        
        if (battlePurpose == Purpose.SkillAttack) {
            initiator.figure.setCombatMode("finisher");
            if (initiator.getUnit().getCustomSkillAnimations().containsKey(initiator.getUnit().getToUseSkill().getName())) {
                initiator.figure.addAnimation(initiator.getUnit().getToUseSkill().getName(), initiator.getUnit().getCustomSkillAnimations().get(initiator.getUnit().getToUseSkill().getName()));
                skillString = initiator.getUnit().getToUseSkill().getName();
            }
        } else if (battlePurpose == Purpose.EtherAttack) {
            masterNode.attachChild(particleNode);
            
            Vector3f locToReceiver = initiator.getEffectsNode().worldToLocal(new Vector3f(worldQuadWidth, worldQuadHeight, 1), null);
            initiator.getEffectsNode().move(0.1f, 0, 2.2f);
            initiator.getEffectsNode().move(((100 - receiver.getUnit().getBattleConfig().getPercentX()) / 100f) * locToReceiver.x, (receiver.getUnit().getBattleConfig().getPercentHeight() / 200f) * locToReceiver.y, 0);
            
            Vector3f locToInitiator = receiver.getEffectsNode().worldToLocal(new Vector3f(worldQuadWidth, worldQuadHeight, 1), null);
            receiver.getEffectsNode().move(-0.1f, 0, 2.2f);
            receiver.getEffectsNode().move(((100 - initiator.getUnit().getBattleConfig().getPercentX()) / -100f) * locToInitiator.x, (initiator.getUnit().getBattleConfig().getPercentHeight() / 200f) * locToInitiator.y, 0);
        }
        
        battleProgress = Progress.Progressing;
        battleState = BattleState.Fighting;
    }
    
    private void initializeGUI(Node guiNode, AssetManager assetManager) {
        gui = guiNode;
        
        List<ProgressBar> initiatorBars = BARS(initiator, assetManager, 0);
        List<ProgressBar> receiverBars = BARS(receiver, assetManager, 1);
        
        initiator.setHPbar(initiatorBars.get(0));
        initiator.setTPbar(initiatorBars.get(1));
        
        receiver.setHPbar(receiverBars.get(0));
        receiver.setTPbar(receiverBars.get(1));
        
        Node initiatorBarGUI = createCombatantBarGui(initiator.getHPbar(), initiator.getTPbar(), assetManager);
        Node receiverBarGUI = createCombatantBarGui(receiver.getHPbar(), receiver.getTPbar(), assetManager);
        
        Node initiatorGUI2 = createCombatantNametagGui(initiator, assetManager);
        Node receiverGUI2 = createCombatantNametagGui(receiver, assetManager);
        
        Node initiatorGUI3 = createCombatantStatsGui(initiator, forecast, assetManager);
        Node receiverGUI3 = createCombatantStatsGui(receiver, forecast, assetManager);
        
        initiatorBarGUI.move(-678f, 25, 0);
        receiverBarGUI.move(100, 25, 0);
        
        float 
                iniMovX = -1 * (725 - ((Container)initiatorGUI2).getPreferredSize().x),
                recMovX = 525f - ((Container)receiverGUI2).getPreferredSize().x;
        
        initiatorGUI2.move(iniMovX, 100f, 0);
        receiverGUI2.move(recMovX, 100f, 0);
        
        initiatorGUI3.move(-680, -45, 0);
        receiverGUI3.move(510, -45, 0);
        
        gui.attachChild(initiatorBarGUI);
        gui.attachChild(receiverBarGUI);
        
        gui.attachChild(initiatorGUI2);
        gui.attachChild(receiverGUI2);
        
        gui.attachChild(initiatorGUI3);
        gui.attachChild(receiverGUI3);
        
        if (battlePurpose == Purpose.SkillAttack) {
            Node skillPointer = shownSkill(assetManager);
            skillPointer.move(-175f, -60f, 0);
            gui.attachChild(skillPointer);
        }
    }
    
    public static int[] allIntsFromTo(int start, int end) {
        int[] k = new int[end - start + 1];
        for (int i = 0; i + start <= end; i++) {
            k[i] = i + start;
        }
        return k;
    }
    
    public static int[] combineArray(int[] values1, int[] values2) {
        int[] arr = new int[values1.length + values2.length];
        System.arraycopy(values1, 0, arr, 0, values1.length);
        System.arraycopy(values2, 0, arr, values1.length, values2.length);
        return arr;
    }
    
    public static int[] thickenArray(int[] values, int thickness) {
        int[] arr = new int[values.length * thickness];
        for (int i = 0; i < values.length; i++) {
            for (int t = 0; t < thickness; t++) {
                arr[i + t] = values[i];
            }
        }
        return arr;
    }
    
    private Container shownSkill(AssetManager assM) {
        Container cont = new Container(/*new BoxLayout(Axis.X, FillMode.None)*/);
        
        QuadBackgroundComponent bg = new QuadBackgroundComponent(assM.loadTexture("Interface/GUI/skillbanner.png"));
        //bg.getMaterial().getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        cont.setBackground(bg);
        
        Skill skill = initiator.getUnit().getToUseSkill();
        
        IconComponent skillIcon = new IconComponent(skill.getPath());
        skillIcon.setIconSize(new Vector2f(45, 45));
        skillIcon.setMargin(35, 70);
        skillIcon.setOverlay(true);
        
        TrueTypeKeyBMP ttk = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular3.ttf", Style.Plain, 46);
        TrueTypeFont ttf = (TrueTypeBMP)assM.loadAsset(ttk);
        //ttf.setScale(10f / 23f);
        
        EditedLabel skillName = new EditedLabel(skill.getName(), ttf);
        skillName.setIcon(skillIcon);
        
        float scale = 1.5f;
        while ((400f - (scale * skillName.text.getTTFNode().getWidth())) / 2f <= 0 || (100f - (scale * skillName.text.getTTFNode().getHeight())) / -2f >= 0) {
            scale -= 0.01f;
        }
        
        skillName.text.getTTFNode().setLocalScale(scale);
        skillName.text.getTTFNode().move((400f - (scale * skillName.text.getTTFNode().getWidth())) / 2f, (100f - (scale * skillName.text.getTTFNode().getHeight())) / -2f, 0);
        
        cont.addChild(skillName);
        
        /*Label skName = new Label(skill.getName());
        skName.setFont(assM.loadFont("Interface/Fonts/imfelldwpica.fnt"));
        skName.setTextHAlignment(HAlignment.Center);
        skName.setTextVAlignment(VAlignment.Center);
        skName.setIcon(skillIcon);
        skName.setColor(ColorRGBA.White);
        
        cont.addChild(skName);*/
        
        cont.setPreferredSize(new Vector3f(400, 100, cont.getPreferredSize().z));
        
        return cont;
    }
    
    private static List<ProgressBar> BARS(Combatant C, AssetManager assetManager, int addendum) {
        
        /*TrueTypeKeyBMP bars = new TrueTypeKeyBMP("Interface/Fonts/Montaga-Regular.ttf", Style.Bold, 19 + addendum);
        TrueTypeFont barsfont = (TrueTypeBMP)assetManager.loadAsset(bars);
        //barsfont.setScale(19f/ (600f + addendum));
        barsfont.setScale(19f/ (19f + addendum));
        
        TrueTypeKeyBMP bars2 = new TrueTypeKeyBMP("Interface/Fonts/Montaga-Regular.ttf", Style.Bold, 21 + addendum);
        TrueTypeFont barsfont2 = (TrueTypeBMP)assetManager.loadAsset(bars2);
        //barsfont2.setScale(19f/ (602f + addendum));
        barsfont2.setScale(19f/ (21f + addendum));*/
        
        
        ProgressBar hpBar = new ProgressBar();
        ProgressBar tpBar = new ProgressBar();
        
        hpBar.setMessage("HP: " + C.getBaseStat(BaseStat.currentHP) + "/" + C.getBaseStat(BaseStat.maxHP));
        hpBar.setProgressPercent(((double)C.getBaseStat(BaseStat.currentHP) / C.getBaseStat(BaseStat.maxHP)));
                
        tpBar.setMessage("TP: " + C.getBaseStat(BaseStat.currentTP) + "/" + C.getBaseStat(BaseStat.maxTP));
        tpBar.setProgressPercent(((double)C.getBaseStat(BaseStat.currentTP) / C.getBaseStat(BaseStat.maxTP)));
                
        ((QuadBackgroundComponent)hpBar.getValueIndicator().getBackground()).setColor(new ColorRGBA(0, 0.76f, 0, 1));
        ((QuadBackgroundComponent)tpBar.getValueIndicator().getBackground()).setColor(new ColorRGBA(0.85f, 0.36f, 0.83f, 1f));
        
        hpBar.getLabel().setColor(ColorRGBA.Black);
        hpBar.getLabel().setTextHAlignment(HAlignment.Left);
        //hpBar.getLabel().setFontSize(hpBar.getLabel().getFontSize() + 1);
        
        tpBar.getLabel().setColor(ColorRGBA.Black);
        tpBar.getLabel().setTextHAlignment(HAlignment.Left);
        //tpBar.getLabel().setFontSize(tpBar.getLabel().getFontSize() + 1);
        
        //hpBar.setBarColor(new ColorRGBA(0, 0.76f, 0, 1));
        //tpBar.setBarColor(new ColorRGBA(0.85f, 0.36f, 0.83f, 1f));
                
        return Arrays.asList(hpBar, tpBar);
    }
    
    private static Container createCombatantBarGui(ProgressBar hpBar, ProgressBar tpBar, AssetManager assetManager) {
        Container content = new Container();

        Container hpborder = new Container(new BoxLayout(Axis.Y, FillMode.None));
        ((TbtQuadBackgroundComponent)hpborder.getBackground()).setColor(ColorRGBA.Black);
        hpborder.addChild(hpBar);
        hpBar.setInsets(new Insets3f(3f, 3f, 3f, 3f));
        
        Container tpborder = new Container(new BoxLayout(Axis.Y, FillMode.None));
        ((TbtQuadBackgroundComponent)tpborder.getBackground()).setColor(ColorRGBA.Black);
        tpborder.addChild(tpBar);
        tpBar.setInsets(new Insets3f(3f, 3f, 3f, 3f));
        
        hpborder.setInsets(new Insets3f(0f, 0, 3f, 0));
        tpborder.setInsets(new Insets3f(3f, 0, 0, 0));

        content.setPreferredSize(new Vector3f(600, 63.75f, content.getPreferredSize().z));

        content.addChild(hpborder);
        content.addChild(tpborder);
        
        content.setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/general_ui/nothing.png")));
        
        return content;
    }
    
    private static Container createCombatantNametagGui(Combatant C, AssetManager assetManager) {
        Container content = new Container();
        
        Label name = new Label(C.getUnit().getName());
        name.setFont(assetManager.loadFont("Interface/Fonts/imfelldwpica.fnt"));
        name.setTextHAlignment(HAlignment.Center);
        name.setTextVAlignment(VAlignment.Center);
        name.setFontSize(42f);
        name.setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/general_ui/nothing.png")));
        name.setColor(ColorRGBA.White);
        name.setInsets(new Insets3f(10, 10, 30, 10));
        
        content.setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Textures/gui/battlenametag.png")));

        content.addChild(name);
        content.setPreferredSize(new Vector3f(/*225*/content.getPreferredSize().x + 100, 75, content.getPreferredSize().z));
        
        return content;
    }
    
    private static Container createCombatantStatsGui(Combatant C, PrebattleForecast pbf, AssetManager assetManager) {
        Container content = new Container();
        
        SingularForecast sf = pbf.getSpecifiedForecast(C.battle_role);
        Label bstats = new Label("Acc: " + sf.displayedAccuracy + "%\nDMG: " + sf.displayedDamage + "\nCrit: " + sf.displayedCrit + "%");
        //name.setFont(assetManager.loadFont("Interface/Fonts/imfelldwpica.fnt"));
        bstats.setTextHAlignment(HAlignment.Center);
        bstats.setFontSize(30f);
        bstats.setColor(ColorRGBA.White);
        bstats.setInsets(new Insets3f(10, 10, 10, 10));
        
        content.setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Textures/gui/battlebox.png")));
        
        content.setPreferredSize(new Vector3f(190, 130, content.getPreferredSize().z));
        content.addChild(bstats);
        
        return content;
    }

    
    public static void strikelog(Strike strike) {
        String info = "";
        
        info += strike.getStriker().getUnit().getName() + " attacks!\n" + strike.getStriker().getUnit().getName();
        
        if (strike.strikeDidHit()) {
            info += " hits!\n";
            
            if (strike.strikeIsCrit()) {
                info += strike.getStriker().getUnit().getName() + " crits!\n";
            } else {
                info += strike.getStriker().getUnit().getName() + " doesn't crit\n";
            }
            
            strike.getVictim().getUnit().currentHP -= strike.getDamage();
            if (strike.getVictim().getUnit().currentHP < 0) { strike.getVictim().getUnit().currentHP = 0; }
            
            
            info += strike.getStriker().getUnit().getName() + " does " + strike.getDamage() + " damage!\n";
        } else {
            info += " misses!\n";
        }
        
        info += strike.getVictim().getUnit().getName() + " has " + strike.getVictim().getUnit().currentHP + " HP remaining!\n";
        
        System.out.println(info);
    }

}

class ShownCombatant {
    private final JobClass unitClass;
    private final AssetManager asm;
    private final String className;
    private final CustomAnimationSegment dodge;
    private final BattleRole battleRole;
    
    public final StrikeInterlude sInterlude;
    public final int missFrame;
    
    private Geometry geom;
    private Quad quad;
    private ShownCombatant opponent;
    private HashMap<String, CustomAnimationSegment> extraAnims = new HashMap<>();
    
    private Container lvlUpPanel = null;
    private StatArrowGroup lvlArrowsColumn1, lvlArrowsColumn2;
    private EditedLabel column1, column2;
    private HashMap<BaseStat, Integer> leveledStats;
    
    private Progress prog = Progress.Fresh;
    private String combatMode;
    private ResetProtocol onStrike;
    
    protected boolean update1 = false, update2 = false, retaliating = false, marker = false;
    
    public ImpactType impactStatus = ImpactType.None;
    public int index = 0, maximum = 0, effIndex = 0;
    public int expGained = 1, totalDmgDone = 0;
    public boolean coordsflipped = false, allowEffectUpdate = false;
    public RadialProgressBar expbar;
    
    private List<Vector3f> WaysMoved = new ArrayList<>();
    
    public ShownCombatant(BattleRole br, Quad q, Geometry geo, AssetManager AM, String mode, JobClass job, StrikeInterlude inter, CustomAnimationSegment dodgeAnim, int misframe) {
        battleRole = br;
        quad = q;
        geom = geo;
        asm = AM;
        dodge = dodgeAnim;
        sInterlude = inter;
        missFrame = misframe;
        
        combatMode = mode;
        unitClass = job;
        className = job.clName();
        
        //missFrame = asm.loadTexture("Models/Sprites/battle/" + className + "/defaultdodge.png");
        maximum = unitClass.getBattleConfig().sumOfAllFramesByType(unitClass.getBattleConfig().getColumnsByName(combatMode));
        //maximum = DirFileExplorer.FileCount(new File("assets\\Models\\Sprites\\battle\\" + className + "\\" + combatMode + "\\"), "png");
        
        Material mat = new Material(asm, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", asm.loadTexture("Models/Sprites/battle/" + className + "/" + combatMode + "/0000.png"));
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geom.setMaterial(mat);
        geom.setQueueBucket(RenderQueue.Bucket.Transparent);
        
        extraAnims.put("dodge", dodge);
    }
    
    public ShownCombatant addAnimation(String K, CustomAnimationSegment V) {
        extraAnims.put(K, V);
        return this;
    }
    
    public void setCombatMode(String mode) {
        combatMode = mode;
        maximum = unitClass.getBattleConfig().framesToFirstFullImpactByType(unitClass.getBattleConfig().getColumnsByName(combatMode), index);
        //maximum = unitClass.getBattleConfig().sumOfAllFramesByType(unitClass.getBattleConfig().getColumnsByName(combatMode));
    }
    
    public String getMode() {
        return combatMode;
    }
    
    public void setOnStrike(ResetProtocol rp) {
        onStrike = rp;
    }
    
    public void setOpponent(ShownCombatant sc) {
        opponent = sc;
    }
    
    private void burnProtocol() {
        if (onStrike != null) {
            onStrike.execute();
            onStrike = null;
        }
    }
    
    public Quad getQuad() { return quad; }
    public Geometry getGeometry() { return geom; }
    public HashMap<String, CustomAnimationSegment> getExtraAnimations() { return extraAnims; }
    
    public Progress getProgress() { return prog; }
    public ImpactType getImpactStatus() { return impactStatus; }
    
    public void startUpdate(boolean resetIndex) {
        prog = Progress.Progressing;
        impactStatus = ImpactType.None;
        //currentTexture.setExtra(ImpactType.None);
        if (resetIndex) { index = 0; }
        update1 = false;
        update2 = false;
        retaliating = false;
        marker = false;
        allowEffectUpdate = false;
        amassingTPF = 0;
        effIndex = 0;
        WaysMoved = new ArrayList<>();
    }
    
    float amassingTPF = 0;
    
    //X.update(tpf, X.getExtraAnimations().get("retaliate") != null ? X.getExtraAnimations().get("retaliate") : null);
    public void update(float tpf, String attackType) {
        if (prog == Progress.Progressing) {
            update1 = true;
            //System.out.println(battleRole + "" + index + ", " + maximum);
            
            if (attackType != null && extraAnims.containsKey(attackType)) { //could be "retaliate"
                
                int maxframe;
                
                switch (extraAnims.get(attackType).getFrameUsageType()) {
                    case Primitive:
                        maxframe = extraAnims.get(attackType).getIndexes().length;
                        
                        impactStatus = 
                            unitClass.getBattleConfig().setSpriteFrame(
                                geom.getMaterial(), 
                                impactStatus, 
                                extraAnims.get(attackType).getMode(), 
                                extraAnims.get(attackType).getFrame(index)
                            );
                        index++;
                        break;
                    case Specialized:
                        maxframe = extraAnims.get(attackType).getFrameDs().length;
                        
                        impactStatus = 
                            unitClass.getBattleConfig().setSpriteFrame(
                                geom.getMaterial(), 
                                impactStatus, 
                                extraAnims.get(attackType).getMode(), 
                                extraAnims.get(attackType).getFrameDs()[index].getIndex()
                            );
                        
                        if (extraAnims.get(attackType).getFrameDs()[index].getLocation() != null) {
                            boolean listHas = false;
                            for (Vector3f way : WaysMoved) {
                                if (way == extraAnims.get(attackType).getFrameDs()[index].getLocation()) {
                                    listHas = true;
                                }
                            }
                            
                            if (!listHas) {
                                Vector3f diff = extraAnims.get(attackType).getFrameDs()[index].getLocation().clone();
                                if (battleRole == BattleRole.Initiator) {
                                    diff.x *= -1;
                                }
                                
                                geom.move(diff);
                                //geom.setLocalTranslation(geom.getLocalTranslation().add(diff));
                                WaysMoved.add(extraAnims.get(attackType).getFrameDs()[index].getLocation());
                            }
                        }
                        
                        if (amassingTPF >= extraAnims.get(attackType).getFrameDs()[index].getDelay() && (extraAnims.get(attackType).getFrameDs()[index].getIndex() != extraAnims.get(attackType).getFrameOfCasting() || extraAnims.get(attackType).getFrameOfCasting() == -1)) {
                            index++;
                            amassingTPF = 0;
                        } else if (extraAnims.get(attackType).getFrameOfCasting() != -1 && extraAnims.get(attackType).getFrameDs()[index].getIndex() == extraAnims.get(attackType).getFrameOfCasting() && impactStatus != ImpactType.All && amassingTPF >= extraAnims.get(attackType).getFrameDs()[index].getDelay()) {
                            allowEffectUpdate = true;
                            //index++;
                            //amassingTPF = 0;
                        }
                        
                        amassingTPF += tpf;
                        break;
                    default:
                        maxframe = extraAnims.get(attackType).getIndexes().length;
                        break;
                }
                
                
                if (index >= maxframe) {
                    prog = Progress.Finished;
                    update1 = false;
                    retaliating = false;
                    burnProtocol();
                }

                if (impactStatus == ImpactType.All && coordsflipped && prog == Progress.Progressing && marker && opponent.sInterlude.getBarProgress() == Progress.Finished /*&& opponent.sInterlude.maxFrame == 4 && opponent.sInterlude.getProgress() == Progress.Finished*/) {
                    if (battleRole == BattleRole.Initiator) {
                        quad.setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]
                        {1, 0,
                         0, 0,
                         0, 1,
                         1, 1});
                    } else {
                        quad.setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]
                        {0, 0,
                         1, 0,
                         1, 1,
                         0, 1});
                    }
                    coordsflipped = false;
                    marker = false;
                    retaliating = false;
                    //System.out.println("yes");
                }
            } else {
                
                if (opponent != null && combatMode.equals("critical")) {
                    opponent.sInterlude.rate = 4;
                }
                
                if (opponent != null && !opponent.retaliating) {
                    impactStatus = unitClass.getBattleConfig().setSpriteFrame(geom.getMaterial(), impactStatus, combatMode, index);
                    index++;
                }
                
                if (index >= maximum) {
                    prog = Progress.Finished;
                    update1 = false;
                }
            }
            //System.out.println(currentTexture.texture);
        }
    }
    
    public void interlude(float tpf, String impactType, boolean gotHit, boolean parried) {
        //System.out.println("interlude");
        
        if (gotHit && update2 && !update1 && sInterlude.getBarProgress() == Progress.Finished) {
            sInterlude.forceEnd();
            //System.out.println("wwwwwwwwwwww");
            prog = Progress.Finished;
            update2 = false;
            retaliating = false;
            return;
        }
        
        switch (sInterlude.getProgress()) {
            case Progressing:
                update2 = true;
                
                if (impactType != null && extraAnims.containsKey(impactType) && sInterlude.index < sInterlude.maxFrame) {
                    //geom.getMaterial().setTexture("ColorMap", asm.loadTexture(extraAnims.get(impactType).getAnimation().get(sInterlude.index)));
                    
                    impactStatus = unitClass.getBattleConfig().setSpriteFrame(
                            geom.getMaterial(), 
                            impactStatus, 
                            extraAnims.get(impactType).getMode(), 
                            extraAnims.get(impactType).getFrame(sInterlude.index));
                }
                break;
            case Fresh:
                if (impactType != null && extraAnims.containsKey(impactType)) {
                    if (!gotHit) {
                        sInterlude.maxFrame = extraAnims.get(impactType).getIndexes().length;
                        //flip coords
                        
                        retaliating = true;
                        coordsflipped = true;
                        if (battleRole == BattleRole.Initiator) {
                            quad.setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]
                               {0, 0,
                                1, 0,
                                1, 1,
                                0, 1});
                        } else {
                            quad.setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]
                               {1, 0,
                                0, 0,
                                0, 1,
                                1, 1});
                        }
                    }
                } else if (!gotHit) {
                    //geom.getMaterial().setTexture("ColorMap", missFrame);
                    geom.getMaterial().setFloat("Position", unitClass.getBattleConfig().typeIndexToGridPosition(missFrame, unitClass.getBattleConfig().getAttack()));
                    sInterlude.maxFrame = StrikeInterlude.DEFAULT_MAX_FRAME;
                    
                    float stpos = -3f;
                    if (battleRole == BattleRole.Receiver) {
                        stpos = 3f;
                    }
                    
                    geom.setLocalTranslation(stpos, 0, 0);
                }   
                break;
            case Finished:
                if (update2 && !update1) {
                    System.out.println("will be done");
                    prog = Progress.Finished;
                    update2 = false;
                    retaliating = false;
                    if (!gotHit) {
                        geom.setLocalTranslation(0, 0, 0);
                        geom.move((battleRole == BattleRole.Initiator ? -0.275f : 0.275f), 0, 0);
                        
                        geom.getMaterial().setFloat("Position", 0f);
                    }
                }
                break;
            default:
                break;
        }
        
        sInterlude.update(tpf);
    }
    
    public void generateLevelUpScreen(TangibleUnit character) {
        leveledStats = character.rollLevelUp();
        character.setStat(BaseStat.level, character.getLVL() + 1);
        
        lvlUpPanel = new Container();
        lvlUpPanel.setBackground(new QuadBackgroundComponent(asm.loadTexture("Interface/GUI/levelup_panel/bg.jpg"))); //panel background
        
        Container contents = new Container(new BoxLayout(Axis.Y, FillMode.None));
        contents.setBackground(new QuadBackgroundComponent(new ColorRGBA(1, 1, 1, 0)));
        lvlUpPanel.addChild(contents);
        
        Vector3f lvlBounds = new Vector3f(600, 735, lvlUpPanel.getPreferredSize().z);
        
        TrueTypeKeyBMP namebmp = new TrueTypeKeyBMP("Interface/Fonts/IMFellDWPica-Regular.ttf", Style.Bold, 37);
        TrueTypeFont namettf = (TrueTypeBMP)asm.loadAsset(namebmp);
        EditedLabel nametag = new EditedLabel(
                character.getName(),
                namettf,
                ColorRGBA.Black
        );
        nametag.text.getTTFNode().move(
            GeneralUtils.centerEntity(
                GeneralUtils.generateBoundsToCenter(nametag.text.getTTFNode()), 
                lvlBounds, 
                Arrays.asList(CenterAxis.X)
            )
        );
        nametag.text.getTTFNode().move(0, -50, 0);
        
        contents.addChild(nametag);
        contents.addChild(new Label("\n\n\n\n"));
        
        Container portraitContainer = new Container(new BoxLayout(Axis.Y, FillMode.None));
        portraitContainer.setBackground(new QuadBackgroundComponent(new ColorRGBA(1, 1, 1, 0)));
        Panel portrait = new Panel(180f, 180f);
        portrait.setBackground(new QuadBackgroundComponent(asm.loadTexture(character.portraitString)));
        //portrait.setBorder(new QuadBackgroundComponent(asm.loadTexture("Textures/gui/frame.png")));
        portraitContainer.addChild(portrait);
        portraitContainer.setInsets(new Insets3f(0, 210f, 0, 210f));
        
        Quad portraitFrame = new Quad(211.5f, 211.5f);
        Geometry portraitFrameGeo = new Geometry("frame", portraitFrame);
        Material portraitFrameMat = new Material(asm, "Common/MatDefs/Misc/Unshaded.j3md");
        portraitFrameMat.setTexture("ColorMap", asm.loadTexture("Interface/GUI/levelup_panel/framebg.png"));
        portraitFrameMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        portraitFrameGeo.setMaterial(portraitFrameMat);
        portraitFrameGeo.move(-13.5f, -193.5f, 3);
        portrait.attachChild(portraitFrameGeo);
        
        contents.addChild(portraitContainer);
        
        TrueTypeKeyBMP classbmp = new TrueTypeKeyBMP("Interface/Fonts/IMFellDWPica-Regular.ttf", Style.Plain, 27);
        TrueTypeFont classttf = (TrueTypeBMP)asm.loadAsset(classbmp);
        EditedLabel classname = new EditedLabel(
                character.clName(),
                classttf,
                ColorRGBA.Black
        );
        classname.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Center);
        classname.text.getTTFNode().move(
            GeneralUtils.centerEntity(
                GeneralUtils.generateBoundsToCenter(classname.text.getTTFNode()), 
                lvlBounds, 
                Arrays.asList(CenterAxis.X)
            )
        );
        contents.addChild(classname);
        classname.text.getTTFNode().move(0, -245, 0);
        
        TrueTypeKeyBMP lvlbmp = new TrueTypeKeyBMP("Interface/Fonts/Montaga-Regular.ttf", Style.Plain, 37);
        TrueTypeFont lvlttf = (TrueTypeBMP)asm.loadAsset(lvlbmp);
        EditedLabel lvltag = new EditedLabel(
                "LVL " + (character.getLVL() - 1) + " ->" + character.getLVL(),
                lvlttf,
                ColorRGBA.Black
        );
        lvltag.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Center);
        lvltag.text.getTTFNode().move(
            GeneralUtils.centerEntity(
                GeneralUtils.generateBoundsToCenter(lvltag.text.getTTFNode()), 
                lvlBounds, 
                Arrays.asList(CenterAxis.X)
            )
        );
        lvltag.text.getTTFNode().move(0, 10f, 0);
        contents.addChild(lvltag);
        
        Container allStats = new Container(new BoxLayout(Axis.X, FillMode.None));
        allStats.setBackground(new QuadBackgroundComponent(new ColorRGBA(1, 1, 1, 0)));
        
        Container MaxHpToDex = new Container(new BoxLayout(Axis.Y, FillMode.None));
        Container MaxTpToBaseAdrenaline = new Container(new BoxLayout(Axis.Y, FillMode.None));
        
        MaxHpToDex.setBackground(new QuadBackgroundComponent(new ColorRGBA(1, 1, 1, 0)));
        MaxTpToBaseAdrenaline.setBackground(new QuadBackgroundComponent(new ColorRGBA(1, 1, 1, 0)));
        
        allStats.addChild(MaxHpToDex);
        allStats.addChild(MaxTpToBaseAdrenaline);
        
        TrueTypeKeyBMP bmp = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular.ttf", Style.Plain, 45);
        TrueTypeFont ttf = (TrueTypeBMP)asm.loadAsset(bmp);
        column1 = new EditedLabel(
                  " MAX HP: " + character.getMaxHP() + "\n"
                + "        STR: " + character.getSTR() + "\n"
                + "   ETHER: " + character.getETHER() + "\n"
                + "        AGI: " + character.getAGI() + "\n"
                + "    COMP: " + character.getCOMP() + "\n"
                + "       DEX: " + character.getDEX() + "\n"
                , ttf, ColorRGBA.Black
        );
        column1.text.getTTFNode().scale(0.6f);
        column1.text.getTTFNode().move(0, 18.5f, 0);
        
        lvlArrowsColumn1 = new StatArrowGroup(column1.text.getTTFNode(), 35f, asm);
        lvlArrowsColumn1.move(10f, -40f, 1);
        column1.text.getTTFNode().attachChild(lvlArrowsColumn1);
        
        MaxHpToDex.addChild(column1);

        TrueTypeKeyBMP bmp2 = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular2.ttf", Style.Plain, 45);
        TrueTypeFont ttf2 = (TrueTypeBMP)asm.loadAsset(bmp2);
        column2 = new EditedLabel(
                  "     MAX TP: " + character.getMaxTP() + "\n"
                + "            DEF: " + character.getDEF() + "\n"
                + "            RSL: " + character.getRSL() + "\n"
                + " MOBILITY: " + character.getMOBILITY() + "\n"
                + "PHYSIQUE: " + character.getPHYSIQUE() + "\n"
                + "INIT. ADR.: " + character.getADRENALINE(), //initial adrenaline
                ttf2, ColorRGBA.Black
        );
        column2.text.getTTFNode().scale(0.6f);
        column2.text.getTTFNode().move(0, 18.5f, 0);
        
        lvlArrowsColumn2 = new StatArrowGroup(column1.text.getTTFNode(), 35f, asm);
        lvlArrowsColumn2.move(45f, -40f, 1);
        //lvlArrowsColumn2.getArrow(ArrowStat.BaseADRENALINE).move(195, 0, 0);
        column2.text.getTTFNode().attachChild(lvlArrowsColumn2);
        
        MaxTpToBaseAdrenaline.addChild(column2);
        
        MaxHpToDex.setInsets(new Insets3f(0, 20f, 0, 0));
        MaxTpToBaseAdrenaline.setInsets(new Insets3f(0, 225f, 0, 0));
        
        allStats.setInsets(new Insets3f(25, 50, 0, 0));
        contents.setInsets(new Insets3f(60, 0, 0, 0));
        contents.addChild(allStats);
        lvlUpPanel.setPreferredSize(lvlBounds);
        
        List<ArrowStat> col1stats = Arrays.asList(ArrowStat.MAXHP, ArrowStat.STR, ArrowStat.ETHER, ArrowStat.AGI, ArrowStat.COMP, ArrowStat.DEX);
        List<ArrowStat> col2stats = Arrays.asList(ArrowStat.MAXTP, ArrowStat.DEF, ArrowStat.RSL, ArrowStat.MOBILITY, ArrowStat.PHYSIQUE, ArrowStat.BaseADRENALINE);
        
        col1stats.forEach((arrowStat) -> {
            lvlArrowsColumn1.inputGrowth(leveledStats.get(arrowStat.getMatching()), arrowStat);
        });
        
        col2stats.forEach((arrowStat) -> {
            lvlArrowsColumn2.inputGrowth(leveledStats.get(arrowStat.getMatching()), arrowStat);
        });
        
        character.levelUp(leveledStats);
    }
    
    public boolean updateArrows(float tpf) {
        if (lvlArrowsColumn1.getProgress() != Progress.Finished) {
            lvlArrowsColumn1.update(tpf);
        } else if (lvlArrowsColumn2.getProgress() != Progress.Finished) {
            lvlArrowsColumn2.update(tpf);
        } else {
            return true;
        }
        
        return false;
    }
    
    public void attemptUpdateTransitionToLevelUp(Node actualGuiNode, TangibleUnit unit, Vector3f moveDifference, int velocity) {
        if (actualGuiNode.hasChild(expbar)) { //initiator leveled up
            generateLevelUpScreen(unit);
            lvlUpPanel.move(moveDifference);
            actualGuiNode.attachChild(lvlUpPanel);
            actualGuiNode.detachChild(expbar);
                    
            lvlArrowsColumn1.move(0, -30, 0);
            lvlArrowsColumn2.move(0, -30, 0);
        }
        
        if (lvlUpPanel != null) {
            lvlUpPanel.move(velocity, 0, 0);
        }
    }
    
    public Container getLvlUpPanel() {
        return lvlUpPanel;
    }
    
    public HashMap<BaseStat, Integer> getLeveledStats() {
        return leveledStats;
    }
    
    public StatArrowGroup getArrowGroupColumn1() {
        return lvlArrowsColumn1;
    }
    
    public StatArrowGroup getArrowGroupColumn2() {
        return lvlArrowsColumn2;
    }
    
    public EditedLabel getColumn1Text() {
        return column1;
    }
    
    public EditedLabel getColumn2Text() {
        return column2;
    }
    
}

class StrikeInterlude {
    public static final int DEFAULT_MAX_FRAME = 30;
    
    public static Node masterNode;
    
    private final ProgressBar hpBar, tpBar;
    private final Combatant unit;
    
    private Progress iprog = Progress.Fresh, barProg = Progress.Fresh;
 
    public int index = 0, maxFrame = 30;
    public double rate = 3;
    
    private int dmgToApply = 0, tpToRemove = 0;
    //private float acctpf = 0;
    
    public StrikeInterlude(Combatant tu) {
        hpBar = tu.getHPbar();
        tpBar = tu.getTPbar();
        unit = tu;
    }
    
    public void setDamageToUnit(int dmgamt) {
        dmgToApply = dmgamt;
    } 
    
    public void setRemovedTP(int amt) {
        tpToRemove = amt;
    }
    
    public int getToRemoveTP() {
        return tpToRemove;
    }
    
    public int actualDamage() {
        return dmgToApply > unit.getBaseStat(BaseStat.currentHP) ? unit.getBaseStat(BaseStat.currentHP) : dmgToApply;
    }
    
    private double evenRate(BaseStat currentBar, BaseStat maxBar) {
        double ratio = rate * (((double)unit.getBaseStat(maxBar)) / unit.getBaseStat(currentBar)) * (((double)index) / maxFrame);
        if (ratio > 1) {
            barProg = Progress.Finished;
            return 1;
        }
        return ratio;
    }
    
    public void update(float tpf) {
        if (iprog == Progress.Progressing) {
            /*if (actualDamage() > 0) {
                maxFrame *= (((double)unit.getBaseStat(BaseStat.currentHP)) / unit.getBaseStat(BaseStat.maxHP));
            }*/
            
            double 
                    percentageCurrentHealth = (unit.getBaseStat(BaseStat.currentHP) - (evenRate(BaseStat.currentHP, BaseStat.maxHP) * actualDamage())) / ((double)unit.getBaseStat(BaseStat.currentHP)),
                    percentageCurrentTP = (unit.getBaseStat(BaseStat.currentTP) - (evenRate(BaseStat.currentTP, BaseStat.maxTP) * tpToRemove)) / ((double)unit.getBaseStat(BaseStat.currentTP));
            double 
                    percentageMaxHealth = percentageCurrentHealth * (((double)unit.getBaseStat(BaseStat.currentHP) / unit.getBaseStat(BaseStat.maxHP))),
                    percentageMaxTP = percentageCurrentTP * (((double)unit.getBaseStat(BaseStat.currentTP) / unit.getBaseStat(BaseStat.maxTP)));
            
            hpBar.setProgressPercent(percentageMaxHealth);
            hpBar.setMessage("HP: " + ((int)((percentageMaxHealth >= 0 ? percentageMaxHealth : 0) * unit.getBaseStat(BaseStat.maxHP))) + "/" + unit.getBaseStat(BaseStat.maxHP));
            
            if (percentageMaxHealth <= 0.25) {
                hpBar.getLabel().setColor(ColorRGBA.Red);
            } else if (percentageMaxHealth <= 0.5) {
                hpBar.getLabel().setColor(ColorRGBA.Orange);
            } else {
                hpBar.getLabel().setColor(ColorRGBA.Black);
            }
            
            tpBar.setProgressPercent(percentageMaxTP);
            tpBar.setMessage("TP: " + ((int)(percentageMaxTP * unit.getBaseStat(BaseStat.maxTP))) + "/" + unit.getBaseStat(BaseStat.maxTP));
            
            if (percentageMaxTP <= 0.25) {
                tpBar.getLabel().setColor(ColorRGBA.Red);
            } else if (percentageMaxTP <= 0.5) {
                tpBar.getLabel().setColor(ColorRGBA.Orange);
            } else {
                tpBar.getLabel().setColor(ColorRGBA.Black);
            }
            
            //System.out.println(unit.battle_role + "\n" + percentageCurrentHealth);
            //System.out.println(percentageMaxHealth);
            
            index++;
            
            if (index > maxFrame /*|| acctpf >= 0.75*/) {
                forceEnd();
                /*iprog = Progress.Finished;
                unit.appendToBaseStat(BaseStat.currentHP, -1 * dmgToApply);
                unit.appendToBaseStat(BaseStat.currentTP, -1 * tpToRemove);
                rate = 3;
                maxFrame = DEFAULT_MAX_FRAME;*/
                /*if (actualDamage() > 0) {
                    maxFrame /= (((double)unit.getBaseStat(BaseStat.currentHP)) / unit.getBaseStat(BaseStat.maxHP));
                }*/
            }
            
            masterNode.updateLogicalState(tpf);
            masterNode.updateGeometricState();
            
            //acctpf += tpf;
        } else if (iprog == Progress.Fresh) {
            index = 0;
            //acctpf = 0;
            dmgToApply = 0;
            tpToRemove = 0;
            iprog = Progress.Progressing;
            barProg = Progress.Progressing;
        }
    }
    
    public Combatant getC_Unit() { return unit; }
    public Progress getProgress() { return iprog; }
    public Progress getBarProgress() { return barProg; }
    
    public void forceEnd() {
        iprog = Progress.Finished;
        unit.appendToBaseStat(BaseStat.currentHP, -1 * dmgToApply);
        unit.appendToBaseStat(BaseStat.currentTP, -1 * tpToRemove);
        rate = 3;
        maxFrame = DEFAULT_MAX_FRAME;
    }
    
    public void setProgressIfAllowed(Strike S, Progress P) {
        if (S.strikeToken > 0 && (P != Progress.Fresh || iprog != Progress.Progressing)) {
            iprog = P;
            S.strikeToken--;
        }
    }
}