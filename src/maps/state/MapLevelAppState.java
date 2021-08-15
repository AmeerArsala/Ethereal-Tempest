/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.state;

import battle.environment.BattleBox;
import battle.Fight;
import battle.data.forecast.PrebattleForecast;
import battle.environment.BattleViewInfo;
import etherealtempest.info.Conveyor;
import com.jme.effekseer.EffekseerRenderer;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.system.AppSettings;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import etherealtempest.gui.specific.ActionMenu;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jme3tools.savegame.SaveGame;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector2f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.util.SkyFactory.EnvMapType;
import maps.layout.MapLevel;
import maps.ui.StatScreen;
import maps.layout.occupant.character.TangibleUnit;
import etherealtempest.fsm.FSM;
import etherealtempest.fsm.FSM.CursorState;
import etherealtempest.fsm.FSM.MapFlowState;
import etherealtempest.fsm.FSM.UnitState;
import etherealtempest.fsm.FsmState;
import etherealtempest.Globals;
import etherealtempest.Main;
import etherealtempest.fsm.MasterFsmState;
import general.procedure.ProcedureGroup;
import java.util.HashMap;
import maps.flow.MapFlow;
import maps.flow.MapFlow.Turn;
import maps.data.MapData;
import maps.layout.occupant.MapEntity;
import maps.layout.MapCoords;
import etherealtempest.GameplayProtocols;
import etherealtempest.fsm.FSM.StatScreenState;
import maps.data.MapLevelLoader;
import maps.layout.tile.Tile;
import maps.layout.tile.TileFoundation;

/**
 *
 * @author night
 */
public class MapLevelAppState extends AbstractAppState {
    protected final AppSettings settings;
    
    protected AppStateManager stateManager;
    protected AssetManager assetManager;
    
    protected InputManager inputManager;
    private final ActionListener actionListener;
    private final AnalogListener analogListener;
    
    protected RenderManager renderManager;
    protected AudioRenderer audioRenderer;
    protected EffekseerRenderer effekseerRenderer;
    
    protected Node rootNode, guiNode;
    protected final Node localRootNode = new Node("Map localRootNode"), localGuiNode = new Node("Map localGuiNode");
    
    protected Camera cam;
    protected FlyByCamera flyCam;
    
    protected final MapLevel mapLevel;
    protected MapFlow mapFlow;
    
    //GUI
    private ActionMenu actionMenu; //post move menu
    private StatScreen statsMenu;
    
    protected final ProcedureGroup procedures = new ProcedureGroup();
    protected final FSM<MapFlowState> fsm = new FSM<MapFlowState>() {
        @Override
        public boolean stateAllowed(FsmState<MapFlowState> st) {
            return true; //maybe change this if needed
        }

        @Override
        public void onStateSet(FsmState<MapFlowState> currentState, FsmState<MapFlowState> previousState) {
            mapFlow.getFSM().setNewStateIfAllowed(currentState);
            
            switch (currentState.getEnum()) {
                case PreBattle:
                    initializeBattle();
                    break;
                default:
                    break;
            }
        }
    };
    
    public MapLevelAppState(AppSettings settings) {
        this(MasterFsmState.getCurrentMap(), settings);
    }
    
    @SuppressWarnings("Convert2Lambda")
    public MapLevelAppState(MapLevel mapLevel, AppSettings settings) {
        this.mapLevel = mapLevel;
        this.settings = settings;
        
        analogListener = new AnalogListener() {
            @Override
            public void onAnalog(String name, float value, float tpf) {
                if (!MapLevelLoader.isCurrentMapLevelDoneLoading() || !mapFlow.allowInput()) {
                    return;
                } 
                
                //int fps = (int)(1 / tpf);
                //Vector2f click2D = inputManager.getCursorPosition();
            }
        };
        
        actionListener = new ActionListener() {
            @Override
            public void onAction(String name, boolean keyPressed, float tpf) {
                if (!MapLevelLoader.isCurrentMapLevelDoneLoading() || !mapFlow.allowInput()) {
                    return;
                }
                
                if (name.equals("toggle freeze") && keyPressed) {
                    Globals.gameIsFrozen = !Globals.gameIsFrozen;
                    return;
                }
                
                //testing purposes w/ camera during battle
                if (fsm.getEnumState() == MapFlowState.DuringBattle) {
                    mapFlow.getCurrentFight().resolveInput(name, tpf, keyPressed);
                }
                
                if (Globals.gameIsFrozen) {
                    if (name.equals("advance frame") && keyPressed) {
                        Globals.gameIsFrozen = false;
                        procedures.add((tpf1) -> {
                            Globals.gameIsFrozen = true;
                            return true;
                        });
                    }
                    
                    return;
                }
                
                //int fps = (int)(1 / tpf);
                
                //stat screen action
                if (statsMenu.getFSM().getEnumState() != StatScreenState.Closed && keyPressed) {
                    statsMenu.resolveInput(name, tpf);
                    if (statsMenu.getFSM().getEnumState() == StatScreenState.Closed) {
                        fsm.setNewStateIfAllowed(new MasterFsmState(MapFlowState.MapDefault).setAssetManager(assetManager));
                        mapFlow.getCursor().getFSM().setNewStateIfAllowed(CursorState.CursorDefault);
                    }
                }
                
                //opening stat screen
                if (name.equals("C") && keyPressed) {
                    if (statsMenu.getFSM().getEnumState() != StatScreenState.Opened && statsMenu.getFSM().getEnumState() != StatScreenState.Selecting && mapFlow.getCursor().getCurrentTile(mapLevel).getOccupier() != null) {
                        fsm.setNewStateIfAllowed(new MasterFsmState(MapFlowState.MapDefault).setAssetManager(assetManager));
                        statsMenu.getFSM().setNewStateIfAllowed(StatScreenState.Opened);
                        statsMenu.startUnitViewGUI(mapFlow.getCursor().getCurrentTile(mapLevel).getOccupier(), mapFlow.constructConveyor());
                        mapFlow.getCursor().getFSM().forceState(CursorState.Idle);
                        return;
                    }
                }
                
                if (mapFlow.getCursor().getFSM().getEnumState() != CursorState.Idle && statsMenu.getFSM().getEnumState() == StatScreenState.Closed && !actionMenu.isOpen()) {
                    //cursor action
                    MasterFsmState test = mapFlow.getCursor().resolveInput(name, tpf, keyPressed);
                    if (test != null) {
                        fsm.setNewStateIfAllowed(test.setAssetManager(assetManager));
                    }
                } else if (actionMenu.isOpen() && keyPressed) {
                    //postActionMenu action
                    if (name.equals("select")) {
                        mapFlow.getCursor().getFSM().forceState(CursorState.Idle);
                    }
                    
                    MasterFsmState change = actionMenu.resolveInput(name, keyPressed, tpf);
                    if (change != null) {
                        fsm.setNewStateIfAllowed(change.setAssetManager(assetManager));
                    }
                }
                
                //all the below inputs are for testing purposes
                /*if (name.equals("F")) {
                    flCam.setEnabled(keyPressed);
                }*/
            }
        };
    }
    
    public Node getLocalRootNode() { 
        return localRootNode; 
    }
    
    public Node getLocalGuiNode() { 
        return localGuiNode; 
    }
    
    public MapFlow getMapFlow() {
        return mapFlow;
    }
    
    public ActionMenu getActionMenu() {
        return actionMenu;
    }
    
    public StatScreen getStatScreen() {
        return statsMenu;
    }
    
    @Override
    public void initialize(AppStateManager appStateManager, Application app) {
        super.initialize(appStateManager, app);
        stateManager = appStateManager;
        assetManager = app.getAssetManager();
        inputManager = app.getInputManager();
        renderManager = app.getRenderManager();
        audioRenderer = app.getAudioRenderer();
        rootNode = ((SimpleApplication)app).getRootNode();
        guiNode = ((SimpleApplication)app).getGuiNode();
        effekseerRenderer = EffekseerRenderer.addToViewPort(stateManager, app.getViewPort(), assetManager, settings.isGammaCorrection());
        
        cam = app.getCamera();
        flyCam = ((SimpleApplication)app).getFlyByCamera();
        
        cam.setViewPort(0.0f, 1.0f, 0.0f, 1.0f);
        flyCam.setEnabled(false);
        
        rootNode.attachChild(localRootNode);
        guiNode.attachChild(localGuiNode);
        
        statsMenu = new StatScreen(assetManager);
        actionMenu = new ActionMenu(assetManager, () -> { mapFlow.getCursor().setVisible(true); });
        
        localGuiNode.attachChild(statsMenu);
        statsMenu.initializeRenders();
        actionMenu.getNode().setLocalTranslation(Globals.getScreenWidth() / 2.07f, (7 / 17f) * Globals.getScreenHeight(), actionMenu.getNode().getLocalTranslation().z);
        
        GameplayProtocols.setOpenPostActionMenu(() -> {
            localGuiNode.attachChild(actionMenu.getNode());
            mapFlow.getCursor().setVisible(false);
            actionMenu.setOpen(true);
            actionMenu.initialize(mapFlow.constructConveyor().setUnit(mapFlow.getCursor().selectedUnit));
        });
        
        initializeControlMappings();
        initializeMapLevel();
    }
    
    public void initializeMapLevel() {
        localRootNode.attachChild(mapLevel.getMiscNode());
        localRootNode.attachChild(mapLevel.getTileNode());
        
        mapFlow = new MapFlow(Arrays.asList(Turn.Player, Turn.Enemy), mapLevel.getMapData().retrieveObjective(), localRootNode, localGuiNode, cam, assetManager);
        
        mapLevel.generateWeather(assetManager, mapFlow.getCursor());
        localRootNode.attachChild(mapFlow.getCursor());
        
        //initialize what's going on in the map
        mapFlow.initialize((ArrayList<TangibleUnit> units, List<MapEntity> mapEntities) -> {
            units.addAll(mapLevel.getMapData().getStartingUnits());
            //units.get(1).setRawStat(BaseStat.CurrentHP, 10);
            
            //units.add(new TangibleUnit(Catalog.UNIT_Pillager(), new CharacterUnitInfo("Pillager.png"), new PositionedUnitParams(), UnitAllegiance.Player, assetManager));
            
            //units.add(new TangibleUnit(Catalog.UNIT_Pillager(), new CharacterUnitInfo("Pillager.png"), new PositionedUnitParams(), UnitAllegiance.Enemy, assetManager));
            
            //units.add(new TangibleUnit(Catalog.UNIT_EvilMorva(), new CharacterUnitInfo("RedTintedMorva.png"), new PositionedUnitParams(), UnitAllegiance.Enemy, assetManager));
            
            int layer = 0;
            for (int k = 0; k < units.size(); k++) {
                localRootNode.attachChild(units.get(k).getNode());
                
                MapCoords coords = new MapCoords(layer);
                
                do {
                    coords.setCoords((int)(mapLevel.getTilesX() * Math.random()), (int)(mapLevel.getTilesY() * Math.random()));
                } while (mapLevel.getTileAt(coords).isOccupied); //no spawning in the same tile
                
                units.get(k).remapPosition(coords, mapLevel);
            }
        });
        
        cam.setLocation(mapFlow.getCursor().getWorldTranslation().add(-1 * ((20f / 16f) * Tile.SIDE_LENGTH), Tile.SIDE_LENGTH * 10f, TileFoundation.RADIUS_FOR_SQUARE));
        cam.lookAt(mapFlow.getCursor().getWorldTranslation(), Globals.WORLD_UP_VECTOR);
        cam.setRotation(new Quaternion().fromAngles(FastMath.PI / 3, FastMath.HALF_PI, 0));
        
        mapFlow.getCursor().setPosition(mapFlow.getUnits().get(0).getPos()); //change position later
        
        fsm.setNewStateIfAllowed(new MasterFsmState(MapFlowState.MapDefault));
    }
    
    public void attachSkybox() {
        /*
        Texture up = assetManager.loadTexture("Textures/skybox/top.png");
        Texture down = assetManager.loadTexture("Textures/skybox/bottom.png");
        Texture north = assetManager.loadTexture("Textures/skybox/north.png");
        Texture south = assetManager.loadTexture("Textures/skybox/south.png");
        Texture east = assetManager.loadTexture("Textures/skybox/east.png");
        Texture west = assetManager.loadTexture("Textures/skybox/west.png");
        rootNode.attachChild(SkyFactory.createSky(assetManager, west, east, north, south, up, down));
        */
        
        Texture skyboxTex = assetManager.loadTexture("Textures/skybox/skybox2.png");
        Spatial skybox = SkyFactory.createSky(assetManager, skyboxTex, EnvMapType.CubeMap);
        skybox.setQueueBucket(RenderQueue.Bucket.Sky);
        skybox.setCullHint(CullHint.Never);
        rootNode.attachChild(skybox);
    }
    
    public void initializeControlMappings() {
        inputManager.addMapping("move up", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("move down", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("move left", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("move right", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("select", new KeyTrigger(KeyInput.KEY_X));
        inputManager.addMapping("deselect", new KeyTrigger(KeyInput.KEY_Z));
        inputManager.addMapping("C", new KeyTrigger(KeyInput.KEY_C));
        inputManager.addMapping("K", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("L", new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping("bump left", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping("bump right", new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping("W", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("A", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("S", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("D", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("spacebar", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("lshift", new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping("F", new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping("enter", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addMapping("toggle freeze", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("advance frame", new KeyTrigger(KeyInput.KEY_PERIOD));
        inputManager.addMapping("left click", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("right click", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping("scroll up", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping("scroll down", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        inputManager.addMapping("mouse move left", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("mouse move right", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("mouse move up", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping("mouse move down", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        
        inputManager.addListener(actionListener, "move up");
        inputManager.addListener(actionListener, "move down");
        inputManager.addListener(actionListener, "move left");
        inputManager.addListener(actionListener, "move right");
        
        inputManager.addListener(actionListener, "W");
        inputManager.addListener(actionListener, "A");
        inputManager.addListener(actionListener, "S");
        inputManager.addListener(actionListener, "D");
        inputManager.addListener(actionListener, "spacebar");
        inputManager.addListener(actionListener, "lshift");
        inputManager.addListener(actionListener, "F");
        inputManager.addListener(actionListener, "enter");
        
        inputManager.addListener(actionListener, "toggle freeze");
        inputManager.addListener(actionListener, "advance frame");
        
        inputManager.addListener(actionListener, "select");
        inputManager.addListener(actionListener, "deselect");
        inputManager.addListener(actionListener, "C");
        inputManager.addListener(actionListener, "K");
        inputManager.addListener(actionListener, "L");
        inputManager.addListener(actionListener, "bump left");  // LB
        inputManager.addListener(actionListener, "bump right"); // RB
        
        inputManager.addListener(actionListener, "left click");
        inputManager.addListener(analogListener, "left click");
        
        inputManager.addListener(actionListener, "right click");
        inputManager.addListener(analogListener, "right click");
        
        inputManager.addListener(analogListener, "scroll up");
        inputManager.addListener(analogListener, "scroll down");
        
        inputManager.addListener(analogListener, "mouse move left");
        inputManager.addListener(analogListener, "mouse move right");
        inputManager.addListener(analogListener, "mouse move up");
        inputManager.addListener(analogListener, "mouse move down");
    }
    
    @Override
    public void cleanup() {
        rootNode.detachChild(localRootNode);
        super.cleanup();
    }
    
    private float accumulatedTPF = 0;
    
    @Override
    public void update(float tpf) {
        if (Globals.gameIsFrozen) {
            return;
        }
        
        //int fps = (int)(1 / tpf);
        
        actionMenu.update(tpf);
        mapFlow.update(tpf);
        procedures.update(tpf);
        
        if (accumulatedTPF >= (1f / Globals.STANDARD_FPS)) {
            syncUpdate(accumulatedTPF);
            accumulatedTPF = 0;
        }

        accumulatedTPF += tpf;
    }
    
    private void syncUpdate(float tpf) {
        statsMenu.update(tpf); //stat screen
        
        Main.GameFlow.update(tpf);
    }
    
    private void initializeBattle() {
        Conveyor battleContext = ((MasterFsmState)fsm.getState()).getConveyor();
        PrebattleForecast battleForecast = new PrebattleForecast(battleContext);
        
        //TODO: remove this stuff later
        HashMap<String, String> childToTextureMap = new HashMap<>();
        childToTextureMap.put("treeStump", "Textures/battle/test/stump.png");
        childToTextureMap.put("treeTrunk", "Textures/battle/test/trunk.png");
        childToTextureMap.put("treeBranches", "Textures/battle/test/branches.png");
        childToTextureMap.put("battleTerrain", "Textures/battle/test/cliff.png");
        childToTextureMap.put("leftRock", "Textures/battle/test/cliff.png");
        childToTextureMap.put("rightRock", "Textures/battle/test/cliff.png");
        
        BattleBox battleBox = new BattleBox(
            new Vector2f(15f, 6f), //battleBoxDimensions
            BattleViewInfo.deserialize("forgottenpillar.json"),
            new BattleBox.TextureSettings(
                childToTextureMap,
                "LightMap",
                "Common/MatDefs/Misc/Unshaded.j3md"
            )
        );
        
        Fight fight = new Fight(
            battleForecast, 
            new Fight.Params(
                assetManager,
                localGuiNode,
                cam,
                battleBox,
                renderManager
            )
        );
        
        fight.onFinish(() -> {
            rootNode.attachChild(localRootNode);
            mapFlow.setLastStrikes(fight.getCombat().getCombatFlowData().strikeReel.strikeTheater.getActualStrikes());
            
            procedures.add((tpf) -> {
                //check if deaths are being applied
                //TODO: also check if any other effects are being applied
                for (TangibleUnit unit : mapFlow.getUnits()) {
                    if (unit.getFSM().getEnumState() == UnitState.Dying) {
                        return false;
                    }
                }
                
                //if no more deaths are being applied, go back to normal
                mapFlow.getCursor().setVisible(true);
                mapFlow.getCursor().getFSM().setNewStateIfAllowed(CursorState.CursorDefault);
                fsm.setNewStateIfAllowed(new MasterFsmState(MapFlowState.PostBattle).setAssetManager(assetManager));
                return true;
            });
        });
        
        mapFlow.setCurrentFight(fight);
        mapFlow.getCursor().setVisible(false);
        mapFlow.getCursor().resetCursorPositionFromSelection();
        rootNode.detachChild(localRootNode);
        //flyCam.setEnabled(true);
        fsm.setNewStateIfAllowed(new MasterFsmState(MapFlowState.DuringBattle).setAssetManager(assetManager));
    }
    
}
