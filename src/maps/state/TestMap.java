/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.state;

import battle.BattleBox;
import battle.Fight;
import battle.data.forecast.PrebattleForecast;
import etherealtempest.info.Catalog;
import etherealtempest.info.Conveyor;
import com.atr.jme.font.asset.TrueTypeLoader;
import com.jme.effekseer.EffekseerRenderer;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.system.AppSettings;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.export.Savable;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.texture.Texture;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;
import etherealtempest.gui.ActionMenu;
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
import com.jme3.texture.Image;
import com.jme3.texture.TextureArray;
import com.jme3.util.SkyFactory;
import com.jme3.util.SkyFactory.EnvMapType;
import maps.layout.occupant.Cursor;
import maps.layout.MapLevel;
import maps.ui.StatScreen;
import maps.layout.occupant.character.TangibleUnit;
import etherealtempest.FSM;
import etherealtempest.FSM.CursorState;
import etherealtempest.FSM.MapFlowState;
import etherealtempest.FSM.UnitState;
import etherealtempest.FsmState;
import etherealtempest.Globals;
import etherealtempest.Main;
import etherealtempest.MasterFsmState;
import fundamental.unit.UnitAllegiance;
import fundamental.formula.Formula;
import fundamental.unit.CharacterUnitInfo;
import general.procedure.ProcedureGroup;
import java.util.HashMap;
import maps.flow.MapFlow;
import maps.flow.MapFlow.Turn;
import maps.layout.MapData;
import maps.layout.occupant.MapEntity;
import fundamental.unit.PositionedUnitParameters;
import maps.layout.Coords;
import maps.layout.MapCoords;
import etherealtempest.GameProtocols;
import maps.layout.tile.Tile;

/**
 *
 * @author night
 */
public class TestMap extends AbstractAppState {
    private final SimpleApplication app0;
    private final Node rootNode, guiNode;
    private final Node localRootNode = new Node("Default 01"), localGuiNode = new Node("Map GUI");
    private final InputManager inputManager;
    private final ActionListener actionListener;
    private final AnalogListener analogListener;
    private final AppSettings settings;
    
    private Camera cam, fightCam;
    private AssetManager assetManager;
    private AppStateManager stManager;
    private RenderManager renderManager;
    private ViewPort screenView;
     
    private EffekseerRenderer effekseerRenderer;
    private FlyByCamera flCam;
    private Savable savestate;
    
    //private Node battleScene;
    protected MapLevel map00;
    protected MapFlow mapFlow;
    
    //GUI
    protected ActionMenu postAction;
    protected StatScreen stats;
    
    protected final Vector3f worldUpVector = new Vector3f(0, 1, 0);
    protected final ProcedureGroup queue = new ProcedureGroup();
    protected final FSM<MapFlowState> fsm = new FSM<MapFlowState>() {
        @Override
        public void setNewStateIfAllowed(FsmState<MapFlowState> st) {
            state = st; //maybe change this if needed
            if (st.getEnum() != MapFlowState.GuiClosed) {
                mapFlow.getFSM().setNewStateIfAllowed(st);
                
                switch (st.getEnum()) {
                    case PreBattle:
                        initializeBattle();
                        break;
                    default:
                        break;
                }
            }
        }
        
    };
    
    public TestMap(SimpleApplication app, Camera cm, FlyByCamera fyCam, AppSettings appSettings) {
        app0 = app;
        
        rootNode = app.getRootNode();
        guiNode = app.getGuiNode();
        
        assetManager = app.getAssetManager();
        inputManager = app.getInputManager();
        renderManager = app.getRenderManager();
        
        settings = appSettings;
        
        cam = cm;
        cam.setViewPort(0.0f, 1.0f, 0.0f, 1.0f);
        
        flCam = fyCam;
        
        flCam.setEnabled(false);

        //audioRenderer = app.getAudioRenderer();
        
        //initialize gui
        GuiGlobals.initialize(app0);
        
        //load glass style
        BaseStyles.loadGlassStyle();
        
        //default style is glass for now
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
        //GuiGlobals.getInstance().setCursorEventsEnabled(false);
        
        assetManager.registerLoader(TrueTypeLoader.class, "ttf");
        
        stats = new StatScreen(assetManager);
        postAction = new ActionMenu(assetManager);
        
        this.analogListener = new AnalogListener() {
            @Override
            public void onAnalog(String name, float value, float tpf) {
                int fps = (int)(1 / tpf);
                Vector2f click2D = inputManager.getCursorPosition();
            }
        };
        
        this.actionListener = new ActionListener() {
            @Override
            public void onAction(String name, boolean keyPressed, float tpf) {
                int fps = (int)(1 / tpf);
                
                //stat screen action
                if (stats.getState().getEnum() != MapFlowState.GuiClosed && keyPressed) {
                    stats.resolveInput(name, tpf);
                    if (stats.getState().getEnum() == MapFlowState.GuiClosed) { 
                        fsm.setNewStateIfAllowed(new MasterFsmState(MapFlowState.MapDefault).setAssetManager(assetManager));
                        mapFlow.getCursor().setStateIfAllowed(CursorState.CursorDefault);
                    }
                }
                
                //opening stat screen
                if (name.equals("C") && keyPressed) {
                    if (stats.getState().getEnum() != MapFlowState.StatScreenOpened && stats.getState().getEnum() != MapFlowState.StatScreenSelecting && mapFlow.getCursor().getCurrentTile(map00).getOccupier() != null) {
                        fsm.setNewStateIfAllowed(new MasterFsmState(MapFlowState.MapDefault).setAssetManager(assetManager));
                        stats.forceState(MapFlowState.StatScreenOpened);
                        stats.startUnitViewGUI(mapFlow.getCursor().getCurrentTile(map00).getOccupier(), mapFlow.constructConveyor());
                        mapFlow.getCursor().forceState(CursorState.Idle);
                        return;
                    }
                }
                
                if (mapFlow.getCursor().getState().getEnum() != CursorState.Idle && stats.getState().getEnum() == MapFlowState.GuiClosed && !postAction.isOpen()) {
                    //cursor action
                    MasterFsmState test = mapFlow.getCursor().resolveInput(name, tpf, keyPressed);
                    if (test != null) {
                        fsm.setNewStateIfAllowed(test.setAssetManager(assetManager));
                    }
                } else if (postAction.isOpen() && keyPressed) {
                    //postActionMenu action
                    if (name.equals("select")) {
                        mapFlow.getCursor().forceState(CursorState.Idle);
                    }
                    
                    MasterFsmState change = postAction.resolveInput(name, keyPressed, tpf);
                    if (change != null) {
                        fsm.setNewStateIfAllowed(change.setAssetManager(assetManager));
                    }
                }
                
                //testing purposes w/ camera during battle
                if (fsm.getState().getEnum() == MapFlowState.DuringBattle) {
                    mapFlow.getCurrentFight().resolveInput(name, tpf, keyPressed);
                }
                
                //all the below inputs are for testing purposes
                if (name.equals("F")) {
                    flCam.setEnabled(keyPressed);
                }
                
                if (name.equals("S") && keyPressed) {
                    if (fightCam != null) {
                        fightCam.setLocation(fightCam.getLocation().add(0, 0, 1));
                    }
                }
                if (name.equals("A") && keyPressed) {
                    if (fightCam != null) {
                        fightCam.setLocation(fightCam.getLocation().add(-1, 0, 0));
                    }
                }
                if (name.equals("W") && keyPressed) {
                    if (fightCam != null) {
                        fightCam.setLocation(fightCam.getLocation().add(0, 0, -1));
                    }
                }
                if (name.equals("D") && keyPressed) {
                    if (fightCam != null) {
                        fightCam.setLocation(fightCam.getLocation().add(1, 0, 0));
                    }
                }
                if (name.equals("spacebar") && keyPressed) {
                    if (fightCam != null) {
                        fightCam.setLocation(fightCam.getLocation().add(0, 1, 0));
                    }
                }
                if (name.equals("lshift") && keyPressed) {
                    if (fightCam != null) {
                        fightCam.setLocation(fightCam.getLocation().add(0, -1, 0));
                    }
                }
            }
        };
    }
    
    public Node getLocalRootNode() { return localRootNode; }
    public Node getLocalGuiNode() { return localGuiNode; }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        stManager = stateManager;
        
        rootNode.attachChild(localRootNode);
        guiNode.attachChild(localGuiNode);
        
        /*Texture up = assetManager.loadTexture("Textures/skybox/top.png");
        Texture down = assetManager.loadTexture("Textures/skybox/bottom.png");
        Texture north = assetManager.loadTexture("Textures/skybox/north.png");
        Texture south = assetManager.loadTexture("Textures/skybox/south.png");
        Texture east = assetManager.loadTexture("Textures/skybox/east.png");
        Texture west = assetManager.loadTexture("Textures/skybox/west.png");
        rootNode.attachChild(SkyFactory.createSky(assetManager, west, east, north, south, up, down));*/
        
        /*Texture skyboxTex = assetManager.loadTexture("Textures/skybox/skybox2.png");
        
        Spatial skybox = SkyFactory.createSky(assetManager, skyboxTex, EnvMapType.CubeMap);
        skybox.setQueueBucket(RenderQueue.Bucket.Sky);
        skybox.setCullHint(CullHint.Never);
        rootNode.attachChild(skybox);*/
        
        localGuiNode.setLocalTranslation(0, Globals.getScreenHeight(), 0);
        localGuiNode.attachChild(stats);
        stats.initializeRenders();
        postAction.getNode().setLocalTranslation(Globals.getScreenWidth() / 2.07f, Globals.getScreenHeight() / -1.7f, postAction.getNode().getLocalTranslation().z);
        
        effekseerRenderer = EffekseerRenderer.addToViewPort(stManager, app0.getViewPort(), assetManager, settings.isGammaCorrection());
        
        GameProtocols.setOpenPostActionMenu(() -> {
            localGuiNode.attachChild(postAction.getNode());
            postAction.setOpen(true);
            postAction.initialize(mapFlow.constructConveyor().setUnit(mapFlow.getCursor().selectedUnit));
        });
        
        initializeMappings();
        initializeMap();
        
       mapFlow.getCursor().setPosition(mapFlow.getUnits().get(0).getPos()); //change position later
    }
    
    public void initializeMap() {
        MapData mapData = MapData.deserializePreset("TestMap");
        
        map00 = new MapLevel("test map", 16, 16, 1, mapData, assetManager);
        localRootNode.attachChild(map00.getMiscNode());
        localRootNode.attachChild(map00.getTileNode());
        MasterFsmState.setCurrentDefaultMap(map00);
        
        mapFlow = new MapFlow(Arrays.asList(Turn.Player, Turn.Enemy), mapData.retrieveObjective(), localRootNode, localGuiNode, cam, assetManager);
        
        map00.generateWeather(assetManager, mapData, mapFlow.getCursor());
        localRootNode.attachChild(mapFlow.getCursor());
        
        //initialize what's going on in the map
        mapFlow.initialize((ArrayList<TangibleUnit> units, List<MapEntity> mapEntities) -> {
            
            units.add
            (new TangibleUnit
                (
                    Catalog.UNIT_Morva(),
                    new CharacterUnitInfo("Morva.png"),
                    new PositionedUnitParameters().hasStashAccess(true).isLeader(true),
                    UnitAllegiance.Player,
                    assetManager
                )
            );
            
            //units.add(new TangibleUnit(Catalog.UNIT_Pillager(), new CharacterUnitInfo("Pillager.png"), new PositionedUnitParameters(), UnitAllegiance.Player, assetManager));
            
            //units.add(new TangibleUnit(Catalog.UNIT_Pillager(), new CharacterUnitInfo("Pillager.png"), new PositionedUnitParameters(), UnitAllegiance.Enemy, assetManager));
            
            //units.add(new TangibleUnit(Catalog.UNIT_Pillager(), new CharacterUnitInfo("Pillager.png"), new PositionedUnitParameters(), UnitAllegiance.Enemy, assetManager));
            
            //units.add(new TangibleUnit(Catalog.UNIT_Pillager(), new CharacterUnitInfo("Pillager.png"), new PositionedUnitParameters(), UnitAllegiance.Enemy, assetManager));
            
            units.add(new TangibleUnit(Catalog.UNIT_EvilMorva(), new CharacterUnitInfo("RedTintedMorva.png"), new PositionedUnitParameters(), UnitAllegiance.Enemy, assetManager));
            
            int layer = 0;
            for (int k = 0; k < units.size(); k++) {
                localRootNode.attachChild(units.get(k).getNode());
                
                MapCoords coords = new MapCoords(layer);
                
                do {
                    coords.setCoords((int)(Tile.LENGTH * Math.random()), (int)(Tile.LENGTH * Math.random()));
                } while (map00.getTileAt(coords).isOccupied); //no spawning in the same tile
                
                units.get(k).remapPosition(coords, map00);
            }
        });
        
        cam.setLocation(new Vector3f(mapFlow.getCursor().getWorldTranslation().x - 20f, mapFlow.getCursor().getWorldTranslation().y + 160f, mapFlow.getCursor().getWorldTranslation().z + 8f));
        cam.lookAt(mapFlow.getCursor().getWorldTranslation(), worldUpVector);
        Quaternion cameraRotation = new Quaternion();
        cameraRotation.fromAngles(FastMath.PI / 3, FastMath.PI / 2, 0);
        cam.setRotation(cameraRotation);
        
        fsm.setNewStateIfAllowed(new MasterFsmState(MapFlowState.MapDefault));
        mapFlow.goToNextPhase();
    }
    
    public void initializeMappings() {
            inputManager.addMapping("move up", new KeyTrigger(KeyInput.KEY_UP));
            inputManager.addMapping("move down", new KeyTrigger(KeyInput.KEY_DOWN));
            inputManager.addMapping("move left", new KeyTrigger(KeyInput.KEY_LEFT));
            inputManager.addMapping("move right", new KeyTrigger(KeyInput.KEY_RIGHT));
            inputManager.addMapping("select", new KeyTrigger(KeyInput.KEY_X));
            inputManager.addMapping("deselect", new KeyTrigger(KeyInput.KEY_Z));
            inputManager.addMapping("C", new KeyTrigger(KeyInput.KEY_C));
            inputManager.addMapping("saveState", new KeyTrigger(KeyInput.KEY_K));
            inputManager.addMapping("loadLastState", new KeyTrigger(KeyInput.KEY_L));
            inputManager.addMapping("bump left", new KeyTrigger(KeyInput.KEY_Q));
            inputManager.addMapping("bump right", new KeyTrigger(KeyInput.KEY_E));
            inputManager.addMapping("W", new KeyTrigger(KeyInput.KEY_W));
            inputManager.addMapping("A", new KeyTrigger(KeyInput.KEY_A));
            inputManager.addMapping("S", new KeyTrigger(KeyInput.KEY_S));
            inputManager.addMapping("D", new KeyTrigger(KeyInput.KEY_D));
            inputManager.addMapping("spacebar", new KeyTrigger(KeyInput.KEY_SPACE));
            inputManager.addMapping("lshift", new KeyTrigger(KeyInput.KEY_LSHIFT));
            inputManager.addMapping("F", new KeyTrigger(KeyInput.KEY_F));
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
            
            inputManager.addListener(actionListener, "select");
            inputManager.addListener(actionListener, "deselect");
            inputManager.addListener(actionListener, "C");
            inputManager.addListener(actionListener, "saveState");
            inputManager.addListener(actionListener, "loadLastState");
            inputManager.addListener(actionListener, "bump left"); //lb
            inputManager.addListener(actionListener, "bump right"); //rb
            
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
        int fps = (int)(1 / tpf);
        
        postAction.update(tpf);
        mapFlow.update(tpf);
        queue.update(tpf);
        
        if (accumulatedTPF >= (1f / 60f)) {
            syncUpdate(accumulatedTPF);
            accumulatedTPF = 0;
        }
        
        accumulatedTPF += tpf;
    }
    
    private void syncUpdate(float tpf) {
        stats.update(tpf); //stat screen
        
        Main.GameFlow.update(tpf);
    }
    
    private void initializeBattle() {
        Conveyor battleContext = ((MasterFsmState)fsm.getState()).getConveyor();
        PrebattleForecast battleForecast = new PrebattleForecast(battleContext);
        
        HashMap<String, String> childToTextureMap = new HashMap<>();
        childToTextureMap.put("treeStump", "Textures/battle/test/stump.png");
        childToTextureMap.put("treeTrunk", "Textures/battle/test/trunk.png");
        childToTextureMap.put("treeBranches", "Textures/battle/test/branches.png");
        childToTextureMap.put("battleTerrain", "Textures/battle/test/cliff.png");
        childToTextureMap.put("leftRock", "Textures/battle/test/cliff.png");
        childToTextureMap.put("rightRock", "Textures/battle/test/cliff.png");
        
        BattleBox battleBox = new BattleBox(
            "Scenes/Battle/battletest5.j3o",
            new Vector2f(50f, 20f), //battleBoxDimensions
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
                screenView,
                renderManager
            )
        );
        
        fight.onFinish(() -> {
            rootNode.attachChild(localRootNode);
            mapFlow.setLastStrikes(fight.getStrikeTheater().getActualStrikes());
            
            queue.add((tpf) -> {
                //check if deaths are being applied
                //TODO: also check if any other effects are being applied
                for (TangibleUnit unit : mapFlow.getUnits()) {
                    if (unit.getFSM().getEnumState() == UnitState.Dying) {
                        return false;
                    }
                }
                
                //if no more deaths are being applied, go back to normal
                mapFlow.getCursor().setStateIfAllowed(CursorState.CursorDefault);
                fsm.setNewStateIfAllowed(new MasterFsmState(MapFlowState.PostBattle).setAssetManager(assetManager));
                return true;
            });
        });
        
        mapFlow.setCurrentFight(fight);
        
        /*
        fightCam.setLocation(new Vector3f(battleScene.getChild("FullPlane").getWorldTranslation().x, battleScene.getChild("FullPlane").getWorldTranslation().y + 2.5f, battleScene.getChild("FullPlane").getWorldTranslation().z + 13.25f));
        Quaternion cameraRotation = new Quaternion();
        cameraRotation.fromAngles(0, FastMath.PI, 0);
        fightCam.setRotation(cameraRotation);
        */
        
        rootNode.detachChild(localRootNode);
        fsm.setNewStateIfAllowed(new MasterFsmState(MapFlowState.DuringBattle).setAssetManager(assetManager));
    }
    
}
