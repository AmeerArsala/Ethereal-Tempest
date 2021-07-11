package etherealtempest;

import com.atr.jme.font.asset.TrueTypeLoader;
import maps.data.MapLevelLoader;
import etherealtempest.fsm.FSM;
import etherealtempest.fsm.FsmState;
import com.jme3.app.DebugKeysAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioListenerState;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;
import edited.FlyCamera;
import edited.state.FlyCamTrueAppState;
import etherealtempest.fsm.FSM.GameState;
import etherealtempest.fsm.MasterFsmState;
import etherealtempest.state.LoadingScreenAppState;
import general.procedure.functional.NamedExecution;
import general.tools.GameTimer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import maps.data.MapData;
import maps.layout.MapLevel;
import maps.state.MapLevelAppState;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    public static final Random RNG = new Random(System.currentTimeMillis());
    
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
    //this is at the 1f / 60f for tpf in the MapLevelAppState class
    public static final GameTimer GameFlow = new GameTimer(); 
    
    private final GameContext gameContext = new GameContext();
    
    public Main() {
        super(new StatsAppState(), new FlyCamTrueAppState(), new AudioListenerState(), new DebugKeysAppState());
    }

    public static void main(String[] args) {
        Globals.app = new Main();
        Globals.app.start();
    }
    
    @Override
    public void simpleInitApp() {
        assetManager.registerLoader(TrueTypeLoader.class, "ttf");
        
        //settings.setFrameRate(120); //cap at 120fps
        debugFlyCam();
        flyCam.setMoveSpeed(350);
        
        loadMapLevel("TestMap");
    }

    @Override
    public void simpleUpdate(float tpf) {
        Globals.update(tpf);
    }
    
    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
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

    AppSettings accessSettings() {
        return settings;
    }
    
    void loadMapLevel(String map) {
        gameContext.setMapData(MapData.deserialize(map, assetManager));
        
        NamedExecution baseLoadingTasks = fetchDefaultTasksToLoad();
        NamedExecution[] mapLevelLoadingTasks = fetchMapLevelLoadingTasks();
        
        NamedExecution[] allLoadingTasks = new NamedExecution[mapLevelLoadingTasks.length + 1];
        allLoadingTasks[0] = baseLoadingTasks;
        for (int i = 0; i < mapLevelLoadingTasks.length; ++i) {
            allLoadingTasks[i + 1] = mapLevelLoadingTasks[i];
        }
        
        float barWidthPercent = 0.6f;
        ColorRGBA barColor = ColorRGBA.White;
        boolean useRandomBGColorSeed = true;
        
        LoadingScreenAppState loadingMapScreen = new LoadingScreenAppState(assetManager, allLoadingTasks, barWidthPercent, barColor, useRandomBGColorSeed) {
            @Override
            protected void onFinish(AppStateManager stateManager) {
                MapLevelLoader.setCurrentMapLevelDoneLoading(true);
                gameContext.getMapState().getMapFlow().goToNextPhase(); //start match
            }
        };
        
        stateManager.attach(loadingMapScreen);
    } 
    
    private NamedExecution fetchDefaultTasksToLoad() {
        return new NamedExecution("Initializing...") {
            @Override
            public void execute() {
                //initialize gui
                GuiGlobals.initialize(Main.this);
                
                //load glass style
                BaseStyles.loadGlassStyle();
                
                //default style is glass for now
                GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
                
                EnvironmentCamera envCam = new EnvironmentCamera();
                stateManager.attach(envCam);
                envCam.initialize(stateManager, Main.this); //Manually initialize so we can add a probe before the next update happens
            }
        };
    }
    
    private NamedExecution[] fetchMapLevelLoadingTasks() {
        NamedExecution[] processes = MapLevelLoader.loadingTasksForMapLevel(assetManager, gameContext.getMapData(), 2); //2 extra tasks will be creating the map
        processes[processes.length - 2] = new NamedExecution("Loading Map...") {
            @Override
            public void execute() {
                MapLevel mapLevel = gameContext.getMapData().createMap(assetManager);
                MasterFsmState.setCurrentDefaultMap(mapLevel);
            }
        };
        
        processes[processes.length - 1] = new NamedExecution("Rendering and Initializing Map...") {
            @Override
            public void execute() {
                gameContext.setMapState(new MapLevelAppState(Main.this, cam, flyCam, settings));
                stateManager.attach(gameContext.getMapState());
            }
        };
        
        return processes;
    }
}
