/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.participant;

import battle.animation.BattleAnimation;
import battle.animation.config.AttackSheetConfig;
import battle.animation.config.PossibleConfig;
import battle.animation.config.action.ConstantsDealer;
import battle.data.CombatFlowData;
import battle.data.event.Strike;
import battle.data.event.StrikeTheater;
import battle.data.event.StrikeTheater.Participant;
import battle.data.forecast.IndividualForecast;
import battle.data.forecast.SingularForecast;
import battle.environment.BattleBox;
import battle.environment.BoxMetadata;
import battle.gui.FighterGUI;
import battle.participant.paramswrapper.AnimationArgsWrapper;
import battle.participant.paramswrapper.DashAnimationArgsWrapper;
import battle.participant.visual.BattleSprite;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.texture.Texture;
import enginetools.math.Vector3F;
import etherealtempest.fsm.FSM.FighterState;
import fundamental.unit.CharacterizedUnit;
import general.procedure.functional.UpdateLoop;
import general.utils.helpers.MathUtils;
import general.visual.sprite.OverlaySheetConfig;
import general.visual.sprite.Sprite;

/**
 *
 * @author night
 * 
 * How to use: 
 * Create 2 instances of Fighters to fight each other, and call setOpponent() on each other (they are each other's parameters)
 * For updating, call update on both Fighters, then call combatFlowData.updatePositions()
 * If using a constructor that passes in type CombatFlowData.Representative (repData), make sure both Fighters have their CombatFlowData.Representative from the same CombatFlowData object
 */
public class Fighter {
    private final SingularForecast forecast;
    private final CombatFlowData.Representative decisionData;
    private final CommonParams common;
    
    private final BattleSprite sprite;
    private final FighterAnimationController controller;
    private final FighterInfoVisualizer visualizer;
    
    private final Runnable onRealImpactOccurred, onStrikeEnd;
    
    private Participant currentRole;
    private BattleSprite opponentSprite;
    
    public Fighter(SingularForecast fighterForecast, CombatFlowData combatFlowData, CommonParams commonParams, boolean mirrored) {
        this(fighterForecast, combatFlowData.getRepresentative(fighterForecast.getCombatant()), commonParams, mirrored);
    }
    
    public Fighter(SingularForecast fighterForecast, CombatFlowData.Representative repData, CommonParams commonParams, boolean mirrored) {
        forecast = fighterForecast;
        decisionData = repData;
        common = commonParams;
        
        boolean mirrorUI = !mirrored; //Example: initiator sprite isn't mirrored, but its UI is because it starts on the right
        
        sprite = common.createBattleSprite(forecast, mirrored);
        decisionData.setPosGetter(() -> { return sprite.getPercentagePosition(); });
        
        controller = new FighterAnimationController(sprite, forecast.getActionDecider(), common.assetManager);
        visualizer = new FighterInfoVisualizer(sprite, new FighterGUI(forecast, common.assetManager, common.cam, mirrorUI), common.battleBoxInfo);
        
        visualizer.getFSM().setNewStateIfAllowed(FighterState.Fighting);
        updateStrikeRole();
        
        onRealImpactOccurred = () -> {
            if (currentRole == Participant.Victim) {
                receiveImpact();
            } else { // currentRole == Participant.Striker
                //TODO: do something here
            }
        };
        
        onStrikeEnd = () -> {
            boolean noAttackSegmentsRemaining = (controller.getCurrentAnimationQueue().isEmpty() || controller.getCurrentAnimation().getRemainingAttackBattleSegmentCount() <= 0); 
            if (!decisionData.getStrikeReel().isFinished() && noAttackSegmentsRemaining) { //decisionData.getStrikeReel().isFinished() is the same as the former isFightOver()
                updateStrikeRole();
                attemptStrike();
            }
        };
    }
    
    public void setOpponent(Fighter opponent) {
        opponentSprite = opponent.sprite;
        controller.getCurrentAnimationQueue().onStrikeFinished(
            () -> {
                decisionData.getStrikeReel().incrementIndex(); //only increment once and do it before anything else
                onStrikeEnd.run();
            },
            opponent.onStrikeEnd
        );
        
        controller.getCurrentAnimationQueue().onRealImpactOccurred(onRealImpactOccurred, opponent.onRealImpactOccurred);
    }
    
    public SingularForecast getForecast() { return forecast; }
    public CombatFlowData.Representative getDecisionData() { return decisionData; }
    public Participant getCurrentRole() { return currentRole; }
    
    public BattleSprite getSprite() { return sprite; }
    public FighterAnimationController getAnimationController() { return controller; }
    public FighterInfoVisualizer getInfoVisualizer() { return visualizer; }
    
    public boolean canFinishWithAnInput() {
        return visualizer.fightIsFullyDone() || visualizer.getFSM().getEnumState() == FighterState.LevelUpDone;
    }
    
    public void attachGUI() {
        common.localGuiNode.attachChild(visualizer.getGUI().getNode());
    }
    
    public void detachGUI() {
        common.localGuiNode.detachChild(visualizer.getGUI().getNode());
    }
    
    public final void updateStrikeRole() {
        currentRole = decisionData.getRoleForCurrentStrike();
    }
    
    public void update(float tpf) {
        if (decisionData.getStrikeReel().isFinished()) { //decisionData.getStrikeReel().isFinished() is the same as the former isFightOver()
            visualizer.onFightOver();
        } else {
            nextAnimation();
        }
        
        controller.update(tpf);
        visualizer.update(tpf);
    }
    
    private void receiveImpact() {
        if (!decisionData.canReceiveImpact()) {
            return;
        }
        
        Strike currentStrike = decisionData.getStrikeReel().getCurrentStrike();
        currentStrike.apply();
        
        decisionData.incrementMinStrikeIndexToReceiveImpact();
        
        controller.nextReceiveImpactAnimation(new AnimationArgsWrapper(visualizer.onReceiveImpact(currentStrike), opponentSprite, decisionData));
    }
    
    public void attemptStrike() {
        if (currentRole == Participant.Striker) {
            sprite.divergeFromDefaultZPos(0.01f);
            forecast.getCombatant().applySkillTollIfAny();
            nextAttackAnimation();
            setConstants(ConstantsDealer.USER);
        } else { // currentRole == Participant.Victim
            sprite.revertToDefaultZPos();
            setConstants(ConstantsDealer.OPPONENT);
        }
    }
    
    private void setConstants(ConstantsDealer.ParticipantConstants holder) {
        holder.DIMENSIONS.set(Vector3F.fit(sprite.getPercentageDimensions(), 0f));
        //TODO: set vec4f and color params
    }
    
    public void nextAnimation() {
        BattleAnimation.Queue animQueue = controller.getCurrentAnimationQueue();
        
        //TODO: work on idle here
        
        if (animQueue.getCurrentTask() != null) {
            animQueue.startCurrentAnimationIfNotAlready();
        }
    }
    
    //TODO: finish the onDashUpdate loops
    //assumes this unit is the striker for this strike
    public void nextAttackAnimation() {
        Strike strike = decisionData.getStrikeReel().getCurrentStrike();
        UpdateLoop onDashUpdate;
        FighterAnimationController.AnimationParams animParams = visualizer.onStartPossibleModification();
        
        AnimationArgsWrapper args = new AnimationArgsWrapper(animParams, opponentSprite, decisionData);

        //if using skill, use skill animation. If triggered talent, use that animation. Otherwise, use regular attack animation
        if (strike.isSkill()) { //skill attack
            String skillName = strike.getStriker().getSkillUsed().getName();
            
            onDashUpdate = (tpf) -> {
                //onDashUpdate
            };
            
            controller.nextSkillAttackAnimation(skillName, args.withDashUpdate(onDashUpdate));
        } else if (strike.getStriker().triggeredBattleTalent()) { //battle talent attack
            String triggeredTalentName = strike.getStriker().getTriggeredBattleTalent().getName();
            
            onDashUpdate = (tpf) -> {
                //onDashUpdate
            };
            
            controller.nextBattleTalentAttackAnimation(triggeredTalentName, args.withDashUpdate(onDashUpdate));
        } else { //regular attack
            
            onDashUpdate = (tpf) -> {
                //onDashUpdate
            };
            
            controller.nextAttackAnimation(args.withDashUpdate(onDashUpdate));
        }
    }
    
    
    public static class CommonParams {
        public final AssetManager assetManager;
        public final Camera cam;
        public final Node localGuiNode;
        
        public final BoxMetadata battleBoxInfo;
        public final Vector3f camLocation;
        private final float zLocation; //startPosZ for BattleSprite/Fighter
        
        public CommonParams(AssetManager assetManager, Camera cam, Node localGuiNode, BattleBox battleBox) {
            this.assetManager = assetManager;
            this.cam = cam;
            this.localGuiNode = localGuiNode;
            
            zLocation = battleBox.getViewInfo().getZLocation();
            camLocation = battleBox.getViewInfo().getCameraLocation();
            battleBoxInfo = battleBox.constructMetadata();
        }
        
        public BattleSprite createBattleSprite(IndividualForecast forecast, boolean mirrored) {
            return createBattleSprite(
                forecast.getActionDecider().getFolderRoot(),
                forecast.getActionDecider().getSpritesheetTexture(),
                forecast.getCombatant().getUnit().getUnitInfo(), 
                !forecast.getEquippedTool().getType().isFormula(), //only use hitPoint if it isn't a formula
                mirrored
            );
        }
        
        public BattleSprite createBattleSprite(String folderRoot, Texture spritesheetTexture, CharacterizedUnit.Info unitInfo, boolean usesHitPoint, boolean mirrored) {
            String configJsonPath = "Sprites\\" + folderRoot + "config.json";
            AttackSheetConfig sheetConfig = PossibleConfig.deserialize(configJsonPath).getPossibleSpritesheet().setFileRoot("Sprites\\" + folderRoot);
            OverlaySheetConfig overlayConfig = OverlaySheetConfig.deserialize(folderRoot + "character_overlay\\" + unitInfo.getBattleOverlayConfigName());
            
            boolean hasOverlay = unitInfo.hasBattleOverlayConfig();
            
            OverlaySheetConfig.Scalar scalar = overlayConfig.getScalar();
            
            //calculate dimensions of sprite
            float actualScalar = scalar.getFactor();
            if (scalar.factorIsPercentageOfContainer()) {
                actualScalar *= MathUtils.hypotenuse(battleBoxInfo.getBoxDimensions());
            }
            
            Vector2f spriteDimensions = new Vector2f(overlayConfig.getSpriteWidthToHeightRatio(), 1f).multLocal(actualScalar);
            
            //create sprite and overlay
            BattleSprite sprite = new BattleSprite(spriteDimensions, assetManager, battleBoxInfo, usesHitPoint);
            sprite.setSpritesheetTexture(sheetConfig.getSpritesheetImagePath(), assetManager);
            
            if (hasOverlay) {
                sprite.setOverlay(new Sprite(spriteDimensions, assetManager));
                sprite.getOverlay().setSpritesheetTexture(sheetConfig.getOverlayConfigPath(overlayConfig.getOverlaySpritesheetFileName()), assetManager); //TODO: change this later
                sprite.attachOverlay();
            }
            
            //initialize sprite and overlay
            sprite.setSizeX(sheetConfig.getColumns());
            sprite.setSizeY(sheetConfig.getRows());
            sprite.setSpritesheetPosition(0);
            
            sprite.setXFacing(Sprite.FACING_LEFT); //all spritesheets must have the character facing left in the image
            sprite.setMirrored(mirrored); //only the receiver starts mirrored; all spritesheets must have the character facing left
            
            float horizontalEdge, verticalEdge = battleBoxInfo.getBottomEdgePosition();
            if (sprite.getXFacing() == Sprite.FACING_LEFT) {
                horizontalEdge = battleBoxInfo.getRightEdgePosition();
            } else { //sprite.getXFacing() == Sprite.FACING_RIGHT
                horizontalEdge = battleBoxInfo.getLeftEdgePosition();
            }
            
            sprite.setLocalTranslation(horizontalEdge, verticalEdge, zLocation);
            sprite.setDefaultZPos(zLocation);
            sprite.setCullHint(CullHint.Never);
            
            //sprite.setHitPointIfAllowed(sheetConfig.getHitPoint()); hitpoint is set later
            sprite.setHurtbox(sheetConfig.getHurtbox());
            sprite.setAllowDisplacementTransformationsFromOpponent(sheetConfig.letEnemyChangeTransformationValues());
            sprite.setDamageNumberLocationSpritePercent(sheetConfig.getDamageNumberLocation());
            sprite.setDefaultCenterPoint(sheetConfig.getCenterPoint());
            
            return sprite;
        }
    }
}
