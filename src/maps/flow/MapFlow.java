/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.flow;

import battle.Strike;
import com.atr.jme.font.TrueTypeBMP;
import com.atr.jme.font.TrueTypeFont;
import com.atr.jme.font.asset.TrueTypeKeyBMP;
import com.atr.jme.font.shape.TrueTypeNode;
import com.atr.jme.font.util.Style;
import com.jme3.asset.AssetManager;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import etherealtempest.FSM;
import etherealtempest.FSM.MapFlowState;
import etherealtempest.FsmState;
import etherealtempest.info.Conveyer;
import fundamental.talent.TalentCondition.Occasion;
import general.GeneralUtils;
import java.util.ArrayList;
import java.util.List;
import maps.layout.occupant.Cursor;
import maps.layout.occupant.MapEntity;
import maps.layout.occupant.TangibleUnit;

/**
 *
 * @author night
 */
public class MapFlow { //eventually make this the map controller
    public static final String EFFECT_HEALING = "General\\healing.json";
    public static final String EFFECT_ETHER_HEALING = "General\\etherhealing.json";
    public static final String EFFECT_BUFF = "General\\buff.json"; //did this already
    public static final String EFFECT_DEBUFF = "General\\debuff.json";
    public static final String EFFECT_DAMAGE = "General\\aoe_damage.json";
    public static final String EFFECT_WARP = "General\\warp.json";
    public static final String EFFECT_FIGHT_OR_FLIGHT = "General\\fightOrFlight.json";
    public static final String EFFECT_ELEMENTAL_POWERUP(String element) { return "General\\" + element + "_powerup.json"; }
    //custom ones can occur too
    
    public enum Turn {
        Player,
        Enemy,
        Ally,
        XthParty;
    }
    
    public static int frame = 0;
    private float time = 0f;
    
    private final List<Turn> partiesInvolved;
    private final Node localGuiNode;
    private final TrueTypeFont ttf;
    
    //Conveyer stuff
    private Objective mapObjective; //the objective of the map
    private Cursor cursor; //for the information, not controlling the cursor
    private List<Strike> lastStrikes; //last strikes that happened; maybe remoce this later
    private TangibleUnit initiator, receiver;
    private ArrayList<TangibleUnit> units = new ArrayList<>(); //ALL UNITS
    private List<MapEntity> mapEntities = new ArrayList<>();
    private AssetManager assetManager;
    
    private Turn turn; //phase
    private int currentTurn = 1, phaseIndex = -1;
    
    private TrueTypeNode phaseText;
    
    private final FSM<MapFlowState> fsm = new FSM<MapFlowState>() {
        @Override
        public void setNewStateIfAllowed(FsmState<MapFlowState> st) {
            Occasion occasionState = Occasion.correspondingOccasion(st.getEnum()); //no null will be returned, only Occasion.Indifferent
            
            boolean occasionChanged;
            if (state != null) {
                occasionChanged = occasionState != Occasion.correspondingOccasion(state.getEnum());
            } else { occasionChanged = true; }
            
            if (occasionChanged) { //combatants need to be in conveyer
                Conveyer data = 
                        new Conveyer()
                            .setAllUnits(units)
                            .setCursor(cursor)
                            .setCurrentTurn(currentTurn)
                            .setObjective(mapObjective)
                            .setMapEntities(mapEntities)
                            .setAssetManager(assetManager);
                
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
                        if (!beforeOrAfterCombat || (beforeOrAfterCombat && !unit.is(initiator) && !unit.is(receiver))) {
                            unit.onOccasion(data.setUnit(unit), occasionState);
                        }
                    }
                }
            }
            
            if (st.getEnum() == MapFlowState.PostBattle) {
                initiator = null;
                receiver = null;
            }
            
            state = st;
        }
    };
    
    public MapFlow(List<Turn> partiesInvolved, Objective mapObjective, Node localGuiNode, AssetManager assetManager) {
        this.partiesInvolved = partiesInvolved;
        this.mapObjective = mapObjective;
        this.localGuiNode = localGuiNode;
        this.assetManager = assetManager;
        
        cursor = new Cursor(assetManager);
        
        TrueTypeKeyBMP bitmap = new TrueTypeKeyBMP("Interface/Fonts/IMMORTAL.ttf", Style.Plain, 45);
        ttf = (TrueTypeBMP)assetManager.loadAsset(bitmap);
    }
    
    public FSM getFSM() { return fsm; }
    
    public void initialize(UnitPlacementInitiation init) {
        init.initiation(units, mapEntities);
    }
    
    public Turn getTurn() { return turn; }
    public int getTurnNumber() { return currentTurn; }
    
    public Cursor getCursor() { return cursor; }
    public List<Strike> getLastStrikes() { return lastStrikes; }
    public Objective getMapObjective() { return mapObjective; }
    public ArrayList<TangibleUnit> getUnits() { return units; }
    public List<MapEntity> getStructures() { return mapEntities; }
    
    public void setLastStrikes(List<Strike> strikes) {
        lastStrikes = strikes;
    }
    
    private float phaseAlphaValue() {
        float literal = -FastMath.pow(time - FastMath.sqrt(0.5f), 2) + 0.5f;
        if (literal < 0) {
            literal = 0;
        }
        
        return literal <= 1f ? literal : 1f;
    }
    
    public void update(float tpf, FSM mapFSM, Camera cam) {
        if (frame == Integer.MAX_VALUE) { frame = 0; }
        if (Float.MAX_VALUE - time <= 5f) { time = 0f; }
        
        units.forEach((tu) -> {
            tu.update(1f / 60f, mapFSM, cam);
        });
        
        updateAI(tpf);
        
        frame++;
        time += tpf;
    }
    
    public void updateAI(float tpf) { //use this for the phase switching animations and stuff
        if (fsm.getEnumState() == MapFlowState.SwitchingTurn) {
            float alpha = phaseAlphaValue();
            phaseText.getMaterial().setColor("Color", new ColorRGBA(1, 1, 1, alpha));
            phaseText.move(3f, 0, 0);
            if (alpha == 0f) {
                localGuiNode.detachChild(phaseText);
                fsm.setNewStateIfAllowed(MapFlowState.BeginningOfTurn);
            }
        }
    }
    
    public void goToNextPhase() {
        phaseIndex++;
        
        if (phaseIndex == partiesInvolved.size()) {
            phaseIndex = 0;
            currentTurn++;
        }
        
        turn = partiesInvolved.get(phaseIndex);
        time = 0.000001f;
        
        phaseText = ttf.getText(getPhaseString(), 3, ColorRGBA.White);
        phaseText.getMaterial().getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        phaseText.setLocalTranslation(-30f, 0, 0);
        localGuiNode.attachChild(phaseText);
        
        fsm.setNewStateIfAllowed(MapFlowState.SwitchingTurn);
    }
    
    public String getPhaseString() { //Player Turn, Enemy Turn, Ally Turn, 3rd party turn, 4th party turn, 5th party turn... etc.
        return (turn != Turn.XthParty ? turn.name() : ("" + phaseIndex + GeneralUtils.ordinalNumberSuffix(phaseIndex) + "Party")) + " Turn";
    }
}
