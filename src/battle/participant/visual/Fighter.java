/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.participant.visual;

import battle.animation.BattleAnimation;
import battle.animation.config.AttackSheetConfig;
import battle.animation.config.PossibleConfig;
import battle.data.DecisionParams;
import battle.data.Strike;
import battle.data.StrikeTheater;
import battle.data.StrikeTheater.Participant;
import battle.data.forecast.IndividualForecast;
import battle.data.forecast.SingularForecast;
import battle.environment.BattleBox;
import battle.environment.BoxMetadata;
import battle.gui.CombatantUI;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import etherealtempest.fsm.FSM.FighterState;
import fundamental.unit.CharacterUnitInfo;
import general.math.FloatPair;
import general.procedure.functional.UpdateLoop;
import general.utils.helpers.MathUtils;
import general.visual.OverlaySheetConfig;
import general.visual.Sprite;

/**
 *
 * @author night
 * 
 * How to use: 
 * Create 2 instances of Fighters to fight each other, and call giveNotifier() on each other (they are each other's parameters)
 * For updating, call preUpdate() on both Fighters and then update()
 */
public class Fighter {
    private final SingularForecast forecast;
    private final CommonParams common;
    private final DecisionParams decisionData;
    
    private final BattleSprite sprite;
    private final FighterAnimationController controller;
    private final FighterInfoVisualizer visualizer;
    
    private Participant currentRole;
    private Notifier fromSelf, fromOpponent;
    
    public Fighter(SingularForecast forecast, CommonParams common, boolean mirrored) {
        this.forecast = forecast;
        this.common = common;
        
        decisionData = new DecisionParams(
            new Vector2f(0, 0), 
            new Vector2f(0, 0), 
            common.strikeTheater, 
            0, //start index (strikes start at index 0)
            forecast.getCombatant()
        );
        
        boolean mirrorUI = !mirrored; //Example: initiator sprite isn't mirrored, but its UI is because it starts on the right
        
        sprite = common.createBattleSprite(forecast, mirrored);
        controller = new FighterAnimationController(sprite, forecast.getActionDecider(), common.assetManager, decisionData);
        visualizer = new FighterInfoVisualizer(sprite, new CombatantUI(forecast, common.assetManager, common.cam, mirrorUI), common.battleBoxInfo.getBoxDimensions());
        
        visualizer.getFSM().setNewStateIfAllowed(FighterState.Fighting);
        updateStrikeRole();
    }
    
    public void giveNotifier(Fighter opponent) {
        fromSelf = new Notifier(sprite);
        opponent.fromOpponent = fromSelf;
        
        controller.giveNotifier(fromSelf, opponent.controller);
    }
    
    public SingularForecast getForecast() { return forecast; }
    public DecisionParams getDecisionData() { return decisionData; }
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
        currentRole = decisionData.getUserRoleForStrike(decisionData.getStrikeIndex());
    }
    
    public void preUpdate() {
        //update positions of user and opponent
        decisionData.userPos.set(sprite.getPercentagePosition());
        decisionData.opponentPos.set(fromOpponent.getSprite().getPercentagePosition());
        
        //update notifier
        BattleAnimation.Queue animQueue = controller.getCurrentAnimationQueue();
        if (!animQueue.isEmpty()) {
            fromSelf.realImpactOccurred = animQueue.getCurrentTask().realImpactOccurred();
            fromSelf.strikeFinished = animQueue.getCurrentTask().isStrikeFinished();
            
            if (fromSelf.realImpactOccurred) {
                System.err.println("Impact Occurred!");
            }
        } else {
            fromSelf.realImpactOccurred = false;
            fromSelf.strikeFinished = false;
        }
        
        fromSelf.fightFullyDone = visualizer.fightIsFullyDone();
    }
    
    public void update(float tpf) {
        if (decisionData.isFightOver()) {
            visualizer.onFightOver();
        } else {
            //onStrikeFinished
            if (fromSelf.strikeFinished() || fromOpponent.strikeFinished()) {
                onStrikeEnd();
            } else {
                nextAnimation();
            }
        }
        
        controller.update(tpf);
        visualizer.update(tpf);
    }
    
    public void onReceiveImpact() {
        Strike currentStrike = decisionData.getCurrentStrike();
        currentStrike.apply();
        
        controller.nextReceiveImpactAnimation(visualizer.onReceiveImpact(currentStrike));
    }
    
    public void onStrikeEnd() {
        decisionData.incrementStrikeIndex();
        if (!decisionData.isFightOver() && controller.getCurrentAnimation().getRemainingAttackBattleSegmentCount() - 1 <= 0) { //subtracting by 1 since it is inclusive of the current segment index which hasn't been incremented yet
            updateStrikeRole();
            attemptStrike();
        }
    }
    
    public void attemptStrike() {
        if (currentRole == Participant.Striker) {
            forecast.getCombatant().applySkillTollIfAny();
            nextAttackAnimation();
        }
    }
    
    public void nextAnimation() {
        if (currentRole == Participant.Victim && fromOpponent.realImpactOccurred()) {
            onReceiveImpact();
        }

        BattleAnimation.Queue animQueue = controller.getCurrentAnimationQueue();
        
        //TODO: work on idle here
        
        if (animQueue.getCurrentTask() != null) {
            animQueue.startCurrentAnimationIfNotAlready();
        }
    }
    
    //TODO: finish the onDashUpdate loops
    //assumes this unit is the striker for this strike
    public void nextAttackAnimation() {
        Strike strike = decisionData.getCurrentStrike();
        UpdateLoop onDashUpdate;
        FighterAnimationController.AnimationParams animParams = visualizer.onStartPossibleModification();

        //if using skill, use skill animation. If triggered talent, use that animation. Otherwise, use regular attack animation
        if (strike.isSkill()) { //skill attack
            String skillName = strike.getStriker().getSkillUsed().getName();
            
            onDashUpdate = (tpf) -> {
                //onDashUpdate
            };
            
            controller.nextSkillAttackAnimation(skillName, onDashUpdate, animParams);
        } else if (strike.getStriker().triggeredBattleTalent()) { //battle talent attack
            String triggeredTalentName = strike.getStriker().getTriggeredBattleTalent().getName();
            
            onDashUpdate = (tpf) -> {
                //onDashUpdate
            };
            
            controller.nextBattleTalentAttackAnimation(triggeredTalentName, onDashUpdate, animParams);
        } else { //regular attack
            
            onDashUpdate = (tpf) -> {
                //onDashUpdate
            };
            
            controller.nextAttackAnimation(onDashUpdate, animParams);
        }
    }
    
    
    public static class CommonParams {
        public final AssetManager assetManager;
        public final Camera cam;
        public final Node localGuiNode;
        public final StrikeTheater strikeTheater;
        
        public final BoxMetadata battleBoxInfo;
        public final Vector3f camLocation;
        private final float zLocation; //startPosZ for BattleSprite/Fighter
        
        public CommonParams(AssetManager assetManager, Camera cam, Node localGuiNode, StrikeTheater strikeTheater, BattleBox battleBox) {
            this.assetManager = assetManager;
            this.cam = cam;
            this.localGuiNode = localGuiNode;
            this.strikeTheater = strikeTheater;
            
            zLocation = battleBox.getViewInfo().getZLocation();
            camLocation = battleBox.getViewInfo().getCameraLocation();
            battleBoxInfo = battleBox.constructMetadata();
        }
        
        public BattleSprite createBattleSprite(IndividualForecast forecast, boolean mirrored) {
            return createBattleSprite(
                forecast.getActionDecider().getFolderRoot(),
                forecast.getCombatant().getUnit().getUnitInfo(), 
                !forecast.getEquippedTool().getType().isFormula(), //only use hitPoint if it isn't a formula
                mirrored
            );
        }
        
        public BattleSprite createBattleSprite(String folderRoot, CharacterUnitInfo unitInfo, boolean usesHitPoint, boolean mirrored) {
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
                sprite.getOverlay().setSpritesheetTexture(sheetConfig.getOverlayConfigPath(overlayConfig.getOverlaySpritesheetFileName()), assetManager);
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
            sprite.setCullHint(CullHint.Never);
            
            sprite.setHitPointIfAllowed(sheetConfig.getHitPoint());
            sprite.setHurtbox(sheetConfig.getHurtbox());
            sprite.setAllowDisplacementTransformationsFromOpponent(sheetConfig.letEnemyChangeTransformationValues());
            sprite.setDamageNumberLocation(sheetConfig.getDamageNumberLocation());
            
            return sprite;
        }
    }
    
    public static class Notifier {
        private final BattleSprite sprite;
        
        private boolean realImpactOccurred = false;
        private boolean strikeFinished = false;
        private boolean fightFullyDone = false; //this is after all the exp and everything has been gained, so once both the user and enemy are done, it will transition out of the fight
        
        public Notifier(BattleSprite sprite) {
            this.sprite = sprite;
        }
        
        public BattleSprite getSprite() { return sprite; }
        
        public boolean realImpactOccurred() { return realImpactOccurred; }
        public boolean strikeFinished() { return strikeFinished; }
        public boolean fightFullyDone() { return fightFullyDone; }
    }
}
