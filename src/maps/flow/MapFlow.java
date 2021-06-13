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
import etherealtempest.FSM;
import etherealtempest.FSM.MapFlowState;
import etherealtempest.FSM.UnitState;
import etherealtempest.FsmState;
import etherealtempest.Globals;
import etherealtempest.MasterFsmState;
import etherealtempest.info.Conveyor;
import etherealtempest.info.RequestDealer;
import fundamental.talent.TalentCondition.Occasion;
import general.GameTimer;
import general.math.function.MathFunction;
import general.procedure.ProcedureGroup;
import general.ui.text.FontProperties;
import general.ui.text.FontProperties.KeyType;
import general.ui.text.Text2D;
import general.ui.text.TextProperties;
import general.utils.GeneralUtils;
import java.util.ArrayList;
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
    
    public final GameTimer mapGlobals = new GameTimer();
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
        public void setNewStateIfAllowed(FsmState<MapFlowState> st) {
            Occasion occasionState = Occasion.correspondingOccasion(st.getEnum()); //no null will be returned, only Occasion.Indifferent
            
            boolean occasionChanged;
            if (state != null) {
                occasionChanged = occasionState != Occasion.correspondingOccasion(state.getEnum());
            } else { 
                occasionChanged = true; 
            }
            
            if (occasionChanged) { //combatants need to be in conveyor
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
            
            switch(st.getEnum()) {
                case DuringBattle:
                    cursor.forceState(CursorState.Idle);
                    currentFight.begin();
                    System.out.println("FIGHT HAS BEGUN");
                    break;
                case PostBattle:
                    initiator = null;
                    receiver = null;
                    break;
            }
            
            state = st;
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
        
        if (mapGlobals.getTime() >= 1f / 60f) {
            syncUpdate(tpf);
            mapGlobals.setTime(0f);
        }
        
        updateAI(tpf);
        
        mapGlobals.update(tpf);
    }
    
    public void syncUpdate(float tpf) {
        for (TangibleUnit unit : units) {
            unit.update(tpf, cam);
        }
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
        phaseText.setOutlineMaterial(new ColorRGBA(1, 1, 1, 0), new ColorRGBA(0, 0, 0, 0));
        localGuiNode.attachChild(phaseText);
        
        MathFunction alphaFunction = new MathFunction() {
            @Override
            protected float f(float time) {
                return FastMath.clamp(-FastMath.pow(time - FastMath.sqrt(0.5f), 2) + 0.5f, 0f, 1f);
            }
        };
        
        MathFunction xFunction = new MathFunction() {
            @Override
            protected float f(float time) {
                return FastMath.pow(time - 1, 2) / 3f;
            }
        };
        
        float length = 2f; //switching turn will last 1.5 seconds
        GameTimer timer = new GameTimer();
        queue.add((tpf) -> {
            float alpha = alphaFunction.output(timer.getTime());
            phaseText.setOutlineMaterial(new ColorRGBA(1, 1, 1, alpha), new ColorRGBA(0, 0, 0, alpha));
            phaseText.setLocalTranslation(xFunction.output(timer.getTime()) * cam.getWidth(), 0.5f * cam.getHeight(), 1f);
            
            timer.update(tpf); 
            
            if (timer.getTime() >= length) {
                localGuiNode.detachChild(phaseText);
                fsm.setNewStateIfAllowed(MapFlowState.BeginningOfTurn);
                return true;
            }
            
            return false;
        });
    }
    
    public String getPhaseString() { //Player Turn, Enemy Turn, Ally Turn, 3rd party turn, 4th party turn, 5th party turn... etc.
        return (turn != Turn.XthParty ? turn.name() : ("" + phaseIndex + GeneralUtils.ordinalNumberSuffix(phaseIndex) + "Party")) + " Turn";
    }
    
    public Text2D createPhaseText(String text) {
        TextProperties textParams = 
            TextProperties.builder()
                .horizontalAlignment(Align.Left)
                .verticalAlignment(VAlign.Center)
                .kerning(3)
                .wrapMode(WrapMode.Clip)
                .textBox(new Rectangle(0f, 0f, 0.3f * cam.getWidth(), 0.1f * cam.getHeight()))
                .build();
        
        FontProperties fontParams = new FontProperties("Interface/Fonts/IMMORTAL.ttf", KeyType.BMP, Style.Plain, 45f);
        
        Text2D phaseText = new Text2D(text, ColorRGBA.White, textParams, fontParams, assetManager);
        phaseText.setOutlineMaterial(ColorRGBA.White, ColorRGBA.Black);
        
        return phaseText;
    }
}
