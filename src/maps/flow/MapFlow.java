/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.flow;

import battle.Fight;
import battle.data.Strike;
import com.atr.jme.font.util.StringContainer.Align;
import com.atr.jme.font.util.StringContainer.VAlign;
import com.atr.jme.font.util.StringContainer.WrapMode;
import com.atr.jme.font.util.Style;
import com.jme3.asset.AssetManager;
import com.jme3.font.Rectangle;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import enginetools.math.SpatialOperator;
import etherealtempest.fsm.FSM;
import etherealtempest.fsm.FSM.MapFlowState;
import etherealtempest.fsm.FSM.UnitState;
import etherealtempest.fsm.FsmState;
import etherealtempest.Globals;
import etherealtempest.fsm.MasterFsmState;
import etherealtempest.info.Conveyor;
import general.procedure.RequestDealer;
import fundamental.talent.TalentCondition.Occasion;
import general.tools.GameTimer;
import general.math.function.MathFunction;
import general.procedure.ProcedureGroup;
import general.ui.text.FontProperties;
import general.ui.text.FontProperties.KeyType;
import general.ui.text.Text2D;
import general.ui.text.TextProperties;
import general.utils.helpers.EngineUtils;
import general.utils.helpers.EngineUtils.CenterAxis;
import general.utils.helpers.GeneralUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import maps.layout.occupant.Cursor;
import maps.layout.occupant.MapEntity;
import maps.layout.occupant.character.TangibleUnit;

/**
 *
 * @author night
 */
public class MapFlow { //eventually make this the map controller
    public static final String EFFECT_HEALING = "general\\healing.json";
    public static final String EFFECT_ETHER_HEALING = "general\\etherhealing.json";
    public static final String EFFECT_BUFF = "general\\buff.json"; //did this already
    public static final String EFFECT_DEBUFF = "general\\debuff.json";
    public static final String EFFECT_DAMAGE = "general\\aoe_damage.json";
    public static final String EFFECT_WARP = "general\\warp.json";
    public static final String EFFECT_FIGHT_OR_FLIGHT = "general\\fightOrFlight.json";
    public static final String EFFECT_ELEMENTAL_POWERUP(String element) { return "general\\" + element + "_powerup.json"; }
    //custom ones can occur too
    
    public enum Turn {
        Player,
        Enemy,
        Ally,
        XthParty;
    }
    
    public final GameTimer syncTimer = new GameTimer();
    public final ProcedureGroup queue = new ProcedureGroup();
    
    private final List<Turn> partiesInvolved;
    private final RequestDealer<Conveyor> requestTaker = new RequestDealer<>();
    private final Node localRootNode, localGuiNode;
    private final Camera cam;
    
    //Conveyor stuff
    private Fight currentFight = null;
    private List<Strike> lastStrikes; //last strikes that happened; maybe remoce this later
    private TangibleUnit initiator, receiver;
    private final ArrayList<TangibleUnit> units = new ArrayList<>(); //ALL UNITS
    private final List<MapEntity> mapEntities = new ArrayList<>();
    private final Objective mapObjective; //the objective of the map
    private final Cursor cursor; //for the information, not controlling the cursor
    private final AssetManager assetManager;
    
    private Turn turn; //phase
    private int currentTurn = 1, phaseIndex = -1;
    
    private final FSM<MapFlowState> fsm = new FSM<MapFlowState>() {
        @Override
        public boolean stateAllowed(FsmState<MapFlowState> st) {
            return true; //maybe change this later
        }

        @Override
        public void onStateSet(FsmState<MapFlowState> currentState, FsmState<MapFlowState> previousState) {
            Occasion occasionState = Occasion.correspondingOccasion(currentState.getEnum()); //no null will be returned, only Occasion.Indifferent
            
            boolean occasionChanged;
            if (previousState != null) {
                occasionChanged = occasionState != Occasion.correspondingOccasion(previousState.getEnum());
            } else { 
                occasionChanged = true; 
            }
            
            if (occasionChanged) { //combatants need to be in conveyor
                onOccasionChanged(occasionState);
            }
            
            switch(currentState.getEnum()) {
                case DuringBattle:
                    cursor.getFSM().forceState(CursorState.Idle);
                    currentFight.begin();
                    break;
                case PostBattle:
                    initiator = null;
                    receiver = null;
                    break;
            }
        }
        
        private void onOccasionChanged(Occasion occasionState) {
            Conveyor data = constructConveyor();
                
            boolean beforeOrAfterCombat = occasionState == Occasion.BeforeCombat || occasionState == Occasion.AfterCombat;
            if (beforeOrAfterCombat) { //SPECIFICALLY for Before or After Combat combatants
                initiator = cursor.selectedUnit;
                receiver = cursor.receivingEnd;
                data.setUnit(initiator).setEnemyUnit(receiver);
                    
                data.createCombatants();
                initiator.onOccasion(data, occasionState);
                   
                data.swapUnits();
                    
                data.createCombatants();
                receiver.onOccasion(data, occasionState);
            }
                
            //data.setEnemyUnit(null);
                
            for (TangibleUnit unit : units) {
                if (unit.getFSM().getEnumState() != UnitState.Dead) {
                    if (!beforeOrAfterCombat || (beforeOrAfterCombat && !unit.equals(initiator) && !unit.equals(receiver))) {
                        unit.onOccasion(data.setUnit(unit), occasionState);
                    }
                }
            }
        }
    };
    
    public MapFlow(List<Turn> partiesInvolved, Objective mapObjective, Node localRootNode, Node localGuiNode, Camera cam, AssetManager assetManager) {
        this.partiesInvolved = partiesInvolved;
        this.mapObjective = mapObjective;
        this.localRootNode = localRootNode;
        this.localGuiNode = localGuiNode;
        this.cam = cam;
        this.assetManager = assetManager;
        
        cursor = new Cursor(assetManager);
    }
    
    public FSM getFSM() { return fsm; }
    
    public void initialize(UnitPlacementInitiation init) {
        init.initiation(units, mapEntities);
    }
    
    public Turn getTurn() { return turn; }
    public int getTurnNumber() { return currentTurn; }
    
    public Fight getCurrentFight() { return currentFight; }
    
    public Cursor getCursor() { return cursor; }
    public List<Strike> getLastStrikes() { return lastStrikes; }
    public Objective getMapObjective() { return mapObjective; }
    public ArrayList<TangibleUnit> getUnits() { return units; }
    public List<MapEntity> getStructures() { return mapEntities; }
    
    public void setCurrentFight(Fight fight) {
        currentFight = fight;
    }
    
    public void setLastStrikes(List<Strike> strikes) {
        lastStrikes = strikes;
    }
    
    public Conveyor constructConveyor() {
        return new Conveyor()
            .setAllUnits(units)
            .setCursor(cursor)
            .setCurrentTurn(currentTurn)
            .setObjective(mapObjective)
            .setMapEntities(mapEntities)
            .setAssetManager(assetManager)
            .setMapFlowRequestTaker(requestTaker);
    }
    
    public void update(float tpf) {
        queue.update(tpf);
        cursor.update(tpf);
        
        boolean unitsShouldUpdate;
        if (syncTimer.getTime() >= 1f / 60f) {
            unitsShouldUpdate = true;
            syncTimer.reset();
        } else {
            unitsShouldUpdate = false;
        }
        
        for (TangibleUnit unit : units) {
            unit.update(tpf, cam, unitsShouldUpdate);
        }
        
        updateAI(tpf);
        syncTimer.update(tpf);
    }
    
    public void updateAI(float tpf) { //use this for the phase switching animations and stuff  
        if (fsm.getEnumState() != MapFlowState.Idle) {
            cam.setLocation(new Vector3f(cursor.getWorldTranslation().x - 70f, cam.getLocation().y, cursor.getWorldTranslation().z + 8f));
            
            if (requestTaker.hasNext()) {
                requestTaker.attemptToResolveCurrentRequest(tpf, constructConveyor());
            }
            
            switch (fsm.getEnumState()) {
                case MapDefault:
                    break;
                case PostBattle: //after potential deaths have been applied on the map
                    //modify later
                    if (cursor.selectedUnit.getFSM().getEnumState() != UnitState.Dead) {
                        cursor.selectedUnit.getFSM().setNewStateIfAllowed(UnitState.Done);
                    }
                
                    fsm.setNewStateIfAllowed(new MasterFsmState(MapFlowState.MapDefault).setAssetManager(assetManager));
                    cursor.resetState();
                    break;
                case DuringBattle:
                    currentFight.update(tpf);
                    break;
                default:
                    break;
            }
        }
    }
    
    public void goToNextPhase() {
        ++phaseIndex;
        
        if (phaseIndex == partiesInvolved.size()) {
            phaseIndex = 0;
            ++currentTurn;
        }
        
        turn = partiesInvolved.get(phaseIndex);
        
        Text2D phaseText = createPhaseText(getPhaseString());
        
        Vector3f center = new Vector3f(0.5f, 0.5f, 0);
        SpatialOperator anchor = phaseText.createSpatialOperator(center.x, center.y);
        anchor.alignTo(Globals.getScreenDimensions().multLocal(center));
        //phaseText.setLocalTranslation(EngineUtils.centerEntity(phaseText.getTextBounds(), Globals.getScreenDimensions(), Arrays.asList(CenterAxis.X, CenterAxis.Y)));
        //phaseText.setOutlineMaterial(ColorRGBA.White, ColorRGBA.Black);
        
        localGuiNode.attachChild(phaseText);

        final GameTimer timer = new GameTimer();
        queue.add((tpf) -> {
            float alpha = 1.5f * FastMath.sin((FastMath.PI / 4f) * timer.getTime());
            phaseText.setTextAlpha(alpha);
            //phaseText.setTextColor(new ColorRGBA(1, 1, 1, alpha));
            
            if (alpha < 0f) {
                localGuiNode.detachChild(phaseText);
                fsm.setNewStateIfAllowed(MapFlowState.BeginningOfTurn);
                return true;
            }
            
            timer.update(tpf); 
            return false;
        });
    }
    
    public String getPhaseString() { //Player Turn, Enemy Turn, Ally Turn, 3rd party turn, 4th party turn, 5th party turn... etc.
        return (turn != Turn.XthParty ? turn.name() : ("" + phaseIndex + GeneralUtils.ordinalNumberSuffix(phaseIndex) + "Party")) + " Turn";
    }
    
    public Text2D createPhaseText(String text) {
        int kerning = 3;
        float fontSize = 135f;
        
        TextProperties textParams = 
            TextProperties.builder()
                .horizontalAlignment(Align.Left)
                .verticalAlignment(VAlign.Center)
                .kerning(kerning)
                .wrapMode(WrapMode.Clip)
                .textBox(new Rectangle(0f, 0f, 0.5f * Globals.getScreenWidth(), 0.1f * Globals.getScreenHeight()))
                .build();
        
        FontProperties fontParams = new FontProperties("Interface/Fonts/IMMORTAL.ttf", KeyType.BMP, Style.Plain, fontSize);
        
        Text2D phaseText = new Text2D(text, ColorRGBA.White, textParams, fontParams, assetManager);
        //phaseText.setOutlineMaterial(ColorRGBA.White, ColorRGBA.Black);
        
        //phaseText.getTextContainer().setLocalScale(3.0f);
        
        return phaseText;
    }
}
