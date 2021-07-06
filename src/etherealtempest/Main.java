package etherealtempest;

import maps.data.MapLevelLoader;
import etherealtempest.fsm.FSM;
import etherealtempest.fsm.FsmState;
import com.jme3.app.DebugKeysAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.audio.AudioListenerState;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import com.jme3.texture.Image;
import com.jme3.texture.TextureArray;
import edited.FlyCamera;
import edited.state.FlyCamTrueAppState;
import etherealtempest.fsm.FSM.GameState;
import etherealtempest.fsm.MasterFsmState;
import general.tools.GameTimer;
import java.util.ArrayList;
import java.util.List;
import maps.data.MapData;
import maps.layout.MapLevel;
import maps.state.TestMap;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    
    public static final FSM<GameState> GameFSM = new FSM<GameState>() {
        @Override
        public boolean stateAllowed(FsmState<GameState> st) {
            return true; //should probably change this later
        }

        @Override
        public void onStateSet(FsmState<GameState> currentState, FsmState<GameState> lastState) {
            //TODO: add stuff here
        }
    };
    
    //this is for the frameCount and time when the tpf is delayed in order for the game not to go too fast or too slow but at a controlled speed
    //this is at the 1f / 60f for tpf in the TestMap class
    public static final GameTimer GameFlow = new GameTimer(); 
    
    public Main() {
        super(new StatsAppState(), new FlyCamTrueAppState(), new AudioListenerState(), new DebugKeysAppState());
    }

    public static void main(String[] args) {
        Globals.app = new Main();
        Globals.app.start();
    }
    
    AppSettings accessSettings() {
        return settings;
    }

    @Override
    public void simpleInitApp() {
       //settings.setFrameRate(120); //cap at 120fps
       debugFlyCam();
       flyCam.setMoveSpeed(350);
       
       String map = "TestMap";
       MapData mapData = MapData.deserialize(map);
       
       MapLevelLoader.loadTileTextures(assetManager, mapData);
       MapLevelLoader.loadMoveArrowTextures(assetManager);
       MapLevelLoader.loadMapGuiTextures(assetManager);
       MapLevelLoader.loadUnitTextures(assetManager);
       
       MapLevel mapLevel = mapData.createMap(assetManager);
       MasterFsmState.setCurrentDefaultMap(mapLevel);
       
       stateManager.attach(new TestMap(this, mapLevel, getCamera(), flyCam, settings));
       
       EnvironmentCamera envCam = new EnvironmentCamera();
       stateManager.attach(envCam);
       envCam.initialize(stateManager, this); //Manually initialize so we can add a probe before the next update happens
    }

    @Override
    public void simpleUpdate(float tpf) {
        Globals.update(tpf);
    }
    
    public void debugFlyCam() {
        if (inputManager != null) {
            // We have to special-case the FlyCamAppState because too
            // many SimpleApplication subclasses expect it to exist in
            // simpleInit().  But at least it only gets initialized if
            // the app state is added.
            if (stateManager.getState(FlyCamTrueAppState.class) != null) {
                flyCam = new FlyCamera(getCamera());
                flyCam.setMoveSpeed(1f); // odd to set this here but it did it before
                stateManager.getState(FlyCamTrueAppState.class).setCamera((FlyCamera)flyCam);
            }
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
