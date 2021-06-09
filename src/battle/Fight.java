/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle;

import battle.data.forecast.PrebattleForecast;
import battle.data.StrikeTheater;
import battle.participant.visual.Fighter;
import com.jme3.asset.AssetManager;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import etherealtempest.FSM;
import etherealtempest.FsmState;

/**
 *
 * @author night
 */
public class Fight {
    public enum State {
        Initializing,
        TransitioningIn,
        InProgress,
        TransitioningOut,
        Finished
    }
    
    private final FSM<State> fsm = new FSM<State>() {
        @Override
        public void setNewStateIfAllowed(FsmState<State> st) {
            state = st;
            switch (st.getEnum()) {
                case TransitioningIn:
                    break;
                case TransitioningOut:
                    break;
                case Finished:
                    onFinish.run();
                    break;
                default:
                    break;
            }
        }
    };
    
    private final Params data;
    private final Node terrainModel;
    private final Combat combat;
    private final PrebattleForecast forecast;
    private final StrikeTheater strikes;
    
    private Runnable onFinish = () -> {};
    
    public Fight(PrebattleForecast battleForecast, Params params) {
        data = params;
        forecast = battleForecast;
        strikes = forecast.createStrikeEvents();
        System.out.println("strike events created");
        
        combat = new Combat(params.toCommonParams(strikes), forecast);
        System.out.println("combat constructed");
        
        terrainModel = params.createModel();
        System.out.println("terrain created");
        terrainModel.setCullHint(CullHint.Never);
        terrainModel.attachChild(combat.getNode());
        System.out.println("terrain loaded");
        
        fsm.setNewStateIfAllowed(State.Initializing);
    }
    
    public void begin() {
        data.initializeArea();
        data.getScreenViewPort().attachScene(terrainModel);
        fsm.setNewStateIfAllowed(State.TransitioningIn);
    }
    
    public FSM<State> getFSM() { 
        return fsm; 
    }
    
    public Node getNode() {
        return terrainModel;
    }
    
    public Combat getCombat() {
        return combat;
    }
    
    public PrebattleForecast getForecast() {
        return forecast;
    }
    
    public StrikeTheater getStrikeTheater() {
        return strikes;
    }
    
    public void onFinish(Runnable finishProtocol) {
        onFinish = finishProtocol;
    }
    
    public void update(float tpf) {
        switch (fsm.getEnumState()) {
            case TransitioningIn:
                fsm.setNewStateIfAllowed(State.InProgress); //this is just for the time being; add a transition later
                break;
            case InProgress:
                combat.update(tpf);
                
                if (combat.isFinished()) {
                    fsm.setNewStateIfAllowed(State.TransitioningOut);
                }
                break;
            case TransitioningOut:
                fsm.setNewStateIfAllowed(State.Finished); //this is just for the time being; add a transition later
                break;
            default:
                break;
        }
    }
    
    public void resolveInput(String name, float tpf, boolean keyPressed) {
        State state = combat.resolveInput(name, tpf, keyPressed, fsm.getEnumState());
        fsm.setNewStateIfAllowed(state);
    }
    
    public static class Params {
        public final AssetManager assetManager;
        public final Node localGuiNode;
        public final Camera cam;
        
        private final BattleBox battleBox;
        private final RenderManager renderManager;
        private ViewPort screenView;
        
        public Params(AssetManager assetManager, Node localGuiNode, Camera mainCam, BattleBox battleBox, ViewPort screenView, RenderManager renderManager) {
            this.assetManager = assetManager;
            this.localGuiNode = localGuiNode;
            this.battleBox = battleBox;
            this.screenView = screenView;
            this.renderManager = renderManager;
            
            cam = mainCam.clone(); //clone main camera
        }
        
        public Fighter.CommonParams toCommonParams(StrikeTheater strikeTheater) {
            return new Fighter.CommonParams(assetManager, cam, localGuiNode, strikeTheater, battleBox.getDimensions());
        }
        
        public Node createModel() {
            return battleBox.generateModel(assetManager);
        }
        
        public ViewPort getScreenViewPort() {
            return screenView;
        }
        
        public void initializeArea() {
            cam.setViewPort(0.0f, 1.0f, 0.0f, 1.0f);
            screenView = renderManager.createMainView("Fight Sequence", cam);
            screenView.setClearFlags(true, true, true);
        }
    }
}
