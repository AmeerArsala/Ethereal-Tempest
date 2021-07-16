/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle;

import battle.environment.BattleBox;
import battle.data.forecast.PrebattleForecast;
import battle.data.event.StrikeTheater;
import battle.environment.BattleEnvironment;
import battle.participant.Fighter;
import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import etherealtempest.fsm.FSM;
import etherealtempest.fsm.FsmState;

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
        public boolean stateAllowed(FsmState<State> st) {
            return true;
        }

        @Override
        public void onStateSet(FsmState<State> currentState, FsmState<State> previousState) {
            switch (currentState.getEnum()) {
                case TransitioningIn:
                    break;
                case TransitioningOut:
                    break;
                case InProgress:
                    break;
                case Finished:
                    System.out.println("Fight finished");
                    onFinish.run();
                    combat.end();
                    data.finish();
                    break;
                default:
                    break;
            }
        }
    };
    
    private final Params data;
    private final BattleEnvironment environment;
    private final Combat combat;
    
    private Runnable onFinish = () -> {};
    
    public Fight(PrebattleForecast battleForecast, Params params) {
        data = params;
        
        combat = new Combat(battleForecast, params.toCommonParams());
        
        environment = params.createEnvironment();
        environment.getScene().attachChild(combat.getNode());
        
        fsm.setNewStateIfAllowed(State.Initializing);
    }
    
    public void begin() {
        data.initializeArea();
        data.getScreenViewPort().attachScene(environment.getScene());
        
        //initialize camera position
        data.cam.setLocation(environment.getTerrainModel().getWorldTranslation().add(data.getRelativeCameraPos()));
        //data.cam.setLocation(environment.getTerrainModel().getChild("FullPlane").getWorldTranslation().add(0, 2.5f, 13.25f));
        Quaternion cameraRotation = new Quaternion();
        cameraRotation.fromAngles(0, FastMath.PI, 0);
        data.cam.setRotation(cameraRotation);
        
        fsm.setNewStateIfAllowed(State.TransitioningIn);
        System.out.println("\nFIGHT BEGIN\n");
    }
    
    public FSM<State> getFSM() { 
        return fsm; 
    }
    
    public Node getNode() {
        return environment.getScene();
    }
    
    public BattleEnvironment getEnvironment() {
        return environment;
    }
    
    public Combat getCombat() {
        return combat;
    }
    
    public void onFinish(Runnable finishProtocol) {
        onFinish = finishProtocol;
    }
    
    public void update(float tpf) {
        switch (fsm.getEnumState()) {
            case TransitioningIn:
                //TODO: add a transition
                fsm.setNewStateIfAllowed(State.InProgress); //this is just for the time being; add a transition later
                break;
            case InProgress:
                combat.update(tpf);
                
                if (combat.isFinished()) {
                    fsm.setNewStateIfAllowed(State.TransitioningOut);
                }
                break;
            case TransitioningOut:
                //TODO: add a transition
                fsm.setNewStateIfAllowed(State.Finished); //this is just for the time being; add a transition later
                break;
            default:
                break;
        }
        
        environment.getScene().updateGeometricState();
    }
    
    public void resolveInput(String name, float tpf, boolean keyPressed) {
        data.resolveInput(name, tpf, keyPressed);
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
        
        public Params(AssetManager assetManager, Node localGuiNode, Camera mainCam, BattleBox battleBox, RenderManager renderManager) {
            this.assetManager = assetManager;
            this.localGuiNode = localGuiNode;
            this.battleBox = battleBox;
            this.renderManager = renderManager;
            
            cam = mainCam.clone(); //clone main camera
        }
        
        public Fighter.CommonParams toCommonParams() {
            return new Fighter.CommonParams(assetManager, cam, localGuiNode, battleBox);
        }
        
        public BattleEnvironment createEnvironment() {
            return battleBox.generateBattleEnvironment(assetManager);
        }
        
        public Vector3f getRelativeCameraPos() {
            return battleBox.getViewInfo().getCameraLocation();
        }
        
        public ViewPort getScreenViewPort() {
            return screenView;
        }
        
        public void initializeArea() {
            cam.setViewPort(0.0f, 1.0f, 0.0f, 1.0f);
            screenView = renderManager.createMainView("Fight Sequence", cam);
            screenView.setClearFlags(true, true, true);
        }
        
        public void finish() {
            cam.setViewPort(0.0f, 0.0f, 0.0f, 0.0f);
        }
        
        public void resolveInput(String name, float tpf, boolean keyPressed) {
            if (name.equals("S") && keyPressed) {
                cam.setLocation(cam.getLocation().add(0, 0, 1));  // z + 1
                System.out.println(cam.getLocation());
            }
            
            if (name.equals("A") && keyPressed) {
                cam.setLocation(cam.getLocation().add(-1, 0, 0)); // x - 1
                System.out.println(cam.getLocation());
            }
            
            if (name.equals("W") && keyPressed) {
                cam.setLocation(cam.getLocation().add(0, 0, -1)); // z - 1
                System.out.println(cam.getLocation());
            }
            
            if (name.equals("D") && keyPressed) {
                cam.setLocation(cam.getLocation().add(1, 0, 0));  // x + 1
                System.out.println(cam.getLocation());
            }
            
            if (name.equals("spacebar") && keyPressed) {
                cam.setLocation(cam.getLocation().add(0, 1, 0));  // y + 1
                System.out.println(cam.getLocation());
            }
            
            if (name.equals("lshift") && keyPressed) {
                cam.setLocation(cam.getLocation().add(0, -1, 0)); // y - 1
                System.out.println(cam.getLocation());
            }
        }
    }
}
