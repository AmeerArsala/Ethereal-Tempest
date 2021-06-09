/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.state;

import battle.Battle;
import battle.Battle.BattleState;
import battle.Combatant.BaseStat;
import battle.forecast.PrebattleForecast;
import etherealtempest.info.Catalog;
import etherealtempest.info.Conveyer;

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

import general.ui.ActionMenu;
import general.ui.MenuState;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jme3tools.savegame.SaveGame;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.texture.Image;
import com.jme3.texture.TextureArray;
import com.jme3.util.SkyFactory;
import com.jme3.util.SkyFactory.EnvMapType;

import maps.layout.occupant.Cursor;
import maps.layout.Map;
import maps.ui.StatScreen;
import maps.layout.occupant.TangibleUnit;
import misc.ViewPortAnimation;
import etherealtempest.FSM;
import etherealtempest.FSM.CursorState;
import etherealtempest.FSM.MapFlowState;
import etherealtempest.FSM.UnitState;
import etherealtempest.FsmState;
import etherealtempest.MasterFsmState;
import etherealtempest.characters.Unit.UnitAllegiance;
import maps.flow.MapFlow;
import maps.flow.MapFlow.Turn;
import maps.flow.Objective;
import maps.layout.Coords;
import maps.layout.MapData;
import maps.layout.occupant.MapEntity;

/**
 *
 * @author night
 */
public class TestMap extends AbstractAppState {
    private final SimpleApplication app0;
    private final Node rootNode, guiNode;
    private final Node localRootNode = new Node("Default 01"), localGuiNode = new Node("Map GUI");
    private final InputManager inputManager;
    private final ActionListener alU;
    private final AnalogListener analogListener;
    private final AppSettings settings;
    private Camera cam, fightCam;
    private AssetManager assetManager;
    private AppStateManager stManager;
    private RenderManager renderManager;
    private ViewPort screenView, mapView;
    
    private EffekseerRenderer effekseerRenderer;
    
    private FlyByCamera flCam;
    private Map map00;
    private Node battleScene;
    
    protected Cursor pCursor;
    protected ActionMenu postAction;
    protected StatScreen stats;
    protected Battle currentBattle;
    protected MapFlow mapFlow;
    protected ViewPortAnimation transitionToFight;
    protected Vector3f worldUpVector = new Vector3f(0, 1, 0);
    
    private Savable savestate;
    
    private final FSM<MapFlowState> fsm = new FSM<MapFlowState>() {
        @Override
        public void setNewStateIfAllowed(FsmState<MapFlowState> st) {
            state = st; //maybe change this if needed
            if (st.getEnum() == MapFlowState.PostActionMenuOpened && postAction.getState().getEnum() == MapFlowState.GuiClosed) {
                localGuiNode.setLocalTranslation(10, 760, 0);
                localGuiNode.attachChild(postAction);
                
                postAction.setPos(new Coords(0, 0), 0); //remove later
                postAction.setLocalTranslation((cam.getWidth() / 8) + 600, -150, postAction.getNode().getLocalTranslation().z);
                postAction.setStateIfAllowed(
                        new MenuState(st.getEnum()).setConveyer(
                                new Conveyer(pCursor.selectedUnit)
                                        .setAllUnits(mapFlow.getUnits())
                                        .setCursor(pCursor)
                                        .setAssetManager(assetManager)
                        )
                );
                
                state = new MasterFsmState().setAssetManager(assetManager);
            } else if (st.getEnum() != MapFlowState.PostActionMenuOpened && st.getEnum() != MapFlowState.GuiClosed) {
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
        
        stats = new StatScreen(app.getAssetManager());

        //audioRenderer = app.getAudioRenderer();
        
        //initialize gui
        GuiGlobals.initialize(app0);
        
        //load glass style
        BaseStyles.loadGlassStyle();
        
        //default style is glass for now
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
        GuiGlobals.getInstance().setCursorEventsEnabled(false);
        
        assetManager.registerLoader(TrueTypeLoader.class, "ttf");
        
        /*mapView = renderManager.createMainView("Map View", cam);
        cam.setViewPort(0.0f, 1.0f, 0.0f, 1.0f);
        mapView.setClearFlags(true, true, true);*/
        
        this.analogListener = new AnalogListener() {
            @Override
            public void onAnalog(String name, float value, float tpf) {
                //do something here later
            }
        
        };
        
        this.alU = new ActionListener() {
            @Override
            public void onAction(String name, boolean keyPressed, float tpf) {
                int fps = (int)(1 / tpf);
                
                //stat screen action
                if (stats.getState().getEnum() != MapFlowState.GuiClosed && keyPressed) {
                    stats.resolveInput(name, tpf);
                    if (stats.getState().getEnum() == MapFlowState.GuiClosed) { 
                        fsm.setNewStateIfAllowed(new MasterFsmState().setAssetManager(assetManager));
                        pCursor.setStateIfAllowed(CursorState.CursorDefault);
                    }
                }
                
                //cursor action
                if (pCursor.getState().getEnum() != CursorState.Idle && stats.getState().getEnum() == MapFlowState.GuiClosed && postAction.getState().getEnum() != MapFlowState.PostActionMenuOpened) {
                    MasterFsmState test = pCursor.resolveInput(name, tpf, keyPressed);
                    if (test != null) {
                        fsm.setNewStateIfAllowed(test.setAssetManager(assetManager));
                    }
                }
                
                //post action menu action
                if (postAction.getState().getEnum() == MapFlowState.PostActionMenuOpened && keyPressed) { //postActionMenu
                    if (name.equals("select")) {
                        pCursor.forceState(CursorState.Idle);
                    }
                    
                    MasterFsmState change = postAction.resolveInput(name, tpf);
                    if (change != null) {
                        fsm.setNewStateIfAllowed(change.setAssetManager(assetManager));
                    }
                }
                
                //testing purposes w/ camera during battle
                if (fsm.getState().getEnum() == MapFlowState.DuringBattle) {
                    currentBattle.resolveInput(name, tpf, keyPressed);
                }
                
                //opening stat screen
                if (name.equals("open unit info menu") && keyPressed) {
                    if (stats.getState().getEnum() != MapFlowState.StatScreenOpened && stats.getState().getEnum() != MapFlowState.StatScreenSelecting && map00.fullmap[pCursor.getElevation()][pCursor.pX][pCursor.pY].getOccupier() != null) {
                        fsm.setNewStateIfAllowed(new MasterFsmState().setAssetManager(assetManager));
                        stats.forceState(MapFlowState.StatScreenOpened);
                        stats.startUnitViewGUI(map00.fullmap[pCursor.getElevation()][pCursor.pX][pCursor.pY].getOccupier());
                        pCursor.forceState(CursorState.Idle);
                    }
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
    
    public Node getLRN() { return localRootNode; }
    public Node getLGN() { return localGuiNode; }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        stManager = stateManager;
        
        rootNode.attachChild(localRootNode);
        guiNode.attachChild(localGuiNode);
        guiNode.setLocalScale(cam.getWidth() / 1366f, cam.getHeight() / 768f, 1);
        
        /*Texture up = assetManager.loadTexture("Textures/skybox/top.png");
        Texture down = assetManager.loadTexture("Textures/skybox/bottom.png");
        Texture north = assetManager.loadTexture("Textures/skybox/north.png");
        Texture south = assetManager.loadTexture("Textures/skybox/south.png");
        Texture east = assetManager.loadTexture("Textures/skybox/east.png");
        Texture west = assetManager.loadTexture("Textures/skybox/west.png");
        rootNode.attachChild(SkyFactory.createSky(assetManager, west, east, north, south, up, down));*/
        
        /*Node S = (Node)assetManager.loadModel("Scenes/testscene.j3o");
        mob = (TerrainQuad)(S.getChild("movement"));
        mapscene = (TerrainQuad)(S.getChild("map"));
        localRootNode.attachChild(S);*/
        
        localGuiNode.setLocalTranslation(10, 760, 0);
        localGuiNode.attachChild(stats);
        stats.initializeRenders();
        
        effekseerRenderer = EffekseerRenderer.addToViewPort(stManager, app0.getViewPort(), assetManager, settings.isGammaCorrection());
        
        Catalog.FormulaCatalog[0].initializeAnimation(assetManager); //TODO: FIX THIS
        
        initMappers();
        initMap();
        
        pCursor.setPosition(mapFlow.getUnits().get(0).getPosX(), mapFlow.getUnits().get(0).getPosY(), 0); //change position later
    }
    
    public void initMap() {
        MapData mapData = MapData.deserializePreset("TestMap");
        
        map00 = new Map("test map", 16, 16, 1, mapData, assetManager);
        localRootNode.attachChild(map00.getMiscNode());
        localRootNode.attachChild(map00.getTileNode());
        MasterFsmState.setCurrentDefaultMap(map00);
        
        mapFlow = new MapFlow(Arrays.asList(Turn.Player, Turn.Enemy), mapData.retrieveObjective(), localGuiNode, assetManager);
        
        pCursor = mapFlow.getCursor();
        map00.generateExtra(assetManager, mapData, pCursor);
        localRootNode.attachChild(pCursor);
        
        //initialize what's going on in the map
        mapFlow.initialize((ArrayList<TangibleUnit> units, List<MapEntity> mapEntities) -> {
            units.add(new TangibleUnit(Catalog.UnitCatalog[0], assetManager));
            units.get(0).unitStatus = UnitAllegiance.Player;
            units.get(0).hasStashAccess = true;
            units.get(0).isLeader = true;
            
            units.add(new TangibleUnit(Catalog.UnitCatalog[1], assetManager));
            units.get(1).unitStatus = UnitAllegiance.Enemy;
            
            units.add(new TangibleUnit(Catalog.UnitCatalog[1], assetManager));
            units.get(2).unitStatus = UnitAllegiance.Enemy;
            
            units.add(new TangibleUnit(Catalog.UnitCatalog[1], assetManager));
            units.get(3).unitStatus = UnitAllegiance.Enemy;
            
            units.add(new TangibleUnit(Catalog.UnitCatalog[1], assetManager));
            units.get(4).unitStatus = UnitAllegiance.Player;
            
            for (int k = 0; k < units.size(); k++) {
                localRootNode.attachChild(units.get(k).getNode());
                
                int x = (int)(16 * Math.random());
                int y = (int)(16 * Math.random());
                
                while (map00.fullmap[0][x][y].isOccupied) { //no spawning in the same tile
                    x = (int)(16 * Math.random());
                    y = (int)(16 * Math.random());
                }
                
                units.get(k).remapPositions(x, y, 0, map00);
            }
        });
        
        initializePostMoveMenu();
        
        cam.setLocation(new Vector3f(pCursor.getWorldTranslation().x - 20f, pCursor.getWorldTranslation().y + 160f, pCursor.getWorldTranslation().z + 8f));
        cam.lookAt(pCursor.getWorldTranslation(), worldUpVector);
        Quaternion cameraRotation = new Quaternion();
        cameraRotation.fromAngles(FastMath.PI / 3, FastMath.PI / 2, 0);
        cam.setRotation(cameraRotation);
        
        fsm.setNewStateIfAllowed(new MasterFsmState());
        mapFlow.goToNextPhase();
    }
    
    public void initializePostMoveMenu() {
        Texture[] 
                backdrop = new Texture[104],
                ability = new Texture[4],
                attack = new Texture[4],
                aid = new Texture[4],
                formation = new Texture[4],
                inventory = new Texture[4],
                skill = new Texture[4],
                trade = new Texture[4],
                annexAnimated = new Texture[6],
                annexSelectedAnimated = new Texture[6],
                escapeAnimated = new Texture[6],
                escapeSelectedAnimated = new Texture[6],
                talkAnimated = new Texture[6],
                talkSelectedAnimated = new Texture[6],
                done = new Texture[2],
                chainUnavailable = new Texture[2],
                chainSelectedAvailable = new Texture[6],
                chainDeselectedAvailable = new Texture[6];
        
        classicInit("ability", ability);
        classicInit("aid", aid);
        classicInit("attack", attack);
        classicInit("formation", formation);
        classicInit("inventory", inventory);
        classicInit("skill", skill);
        classicInit("trade", trade);
        classicInit("annex", annexAnimated);
        classicInit("annex/selected", annexSelectedAnimated);
        classicInit("done", done);
        classicInit("talk", talkAnimated);
        classicInit("talk/selected", talkSelectedAnimated);
        classicInit("escape", escapeAnimated);
        classicInit("escape/selected", escapeSelectedAnimated);
        classicInit("centerpiece", chainUnavailable);
        classicInit("centerpiece/selectedavailable", chainSelectedAvailable);
        classicInit("centerpiece/unselectedavailable", chainDeselectedAvailable);
        classicInitMenuBackdrop("backdrop/bd (", backdrop);
        
        postAction = new ActionMenu( new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"),
                backdrop, attack, ability, aid, formation, inventory, skill, trade, annexAnimated, annexSelectedAnimated, escapeAnimated,
                escapeSelectedAnimated, talkAnimated, talkSelectedAnimated, done, chainUnavailable, chainSelectedAvailable,
                chainDeselectedAvailable, assetManager.loadTexture("Interface/GUI/stat_screen/nothing.png")
        );
    }
    
    public void classicInit(String name, Texture[] empty) {
        for (int g = 0; g < empty.length; g++) { 
            empty[g] = assetManager.loadTexture("Interface/GUI/postmovemenu/" + name + "/" + g + ".png");
        }
    }
    
    public void classicInitMenuBackdrop(String name, Texture[] empty) {
        for (int g = 0; g < empty.length; g++) { 
            empty[g] = assetManager.loadTexture("Interface/GUI/postmovemenu/" + name + g + ").png");
        }
    }
    
    public void initMappers() {
            inputManager.addMapping("move up", new KeyTrigger(KeyInput.KEY_UP));
            inputManager.addMapping("move down", new KeyTrigger(KeyInput.KEY_DOWN));
            inputManager.addMapping("move left", new KeyTrigger(KeyInput.KEY_LEFT));
            inputManager.addMapping("move right", new KeyTrigger(KeyInput.KEY_RIGHT));
            inputManager.addMapping("select", new KeyTrigger(KeyInput.KEY_X));
            inputManager.addMapping("deselect", new KeyTrigger(KeyInput.KEY_Z));
            inputManager.addMapping("open unit info menu", new KeyTrigger(KeyInput.KEY_C));
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
            
            inputManager.addListener(alU, "move up");
            inputManager.addListener(analogListener, "move up");
            
            inputManager.addListener(alU, "move down");
            inputManager.addListener(analogListener, "move down");
            
            inputManager.addListener(alU, "move left");
            inputManager.addListener(analogListener, "move left");
            
            inputManager.addListener(alU, "move right");
            inputManager.addListener(analogListener, "move right");
            
            inputManager.addListener(alU, "W");
            inputManager.addListener(alU, "A");
            inputManager.addListener(alU, "S");
            inputManager.addListener(alU, "D");
            inputManager.addListener(alU, "spacebar");
            inputManager.addListener(alU, "lshift");
            
            inputManager.addListener(alU, "F");
            
            inputManager.addListener(alU, "select");
            inputManager.addListener(alU, "deselect");
            inputManager.addListener(alU, "open unit info menu");
            inputManager.addListener(alU, "saveState");
            inputManager.addListener(alU, "loadLastState");
            inputManager.addListener(alU, "bump left"); //lb
            inputManager.addListener(alU, "bump right"); //rb
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
        pCursor.update(tpf, ((MasterFsmState)fsm.getState()));
        
        if (accumulatedTPF >= (1f / 60f)) {
            fullUpdate(tpf);
            accumulatedTPF = 0;
        }
        
        accumulatedTPF += tpf;
    }
    
    private void fullUpdate(float tpf) {
        updateAI(1f / 60f);
        
        stats.update(1f / 60f);
        
        mapFlow.update(1f / 60f, fsm, cam);
    }
    
    private int fc = 0;
    private float amassedTPF = 0;
    
    public void updateAI(float tpf) {
        //System.out.println(fsm.getState().getEnum());
        if (fc > 1000) { fc = 0; }
        
        if (fsm.getState().getEnum() != MapFlowState.Idle) {
            cam.setLocation(new Vector3f(pCursor.getWorldTranslation().x - 70f, cam.getLocation().y, pCursor.getWorldTranslation().z + 8f));
            
            switch (fsm.getEnumState()) {
                case MapDefault:
                    break;
                case PostBattle:
                    //modify later
                    if (pCursor.selectedUnit.getStat(BaseStat.currentHP) <= 0 && pCursor.selectedUnit.getFSM().getState().getEnum() != UnitState.Dead) {
                        localRootNode.detachChild(pCursor.selectedUnit.getNode());
                        map00.fullmap[pCursor.getElevation()][pCursor.selectedUnit.getPosX()][pCursor.selectedUnit.getPosY()].resetOccupier();
                        pCursor.selectedUnit.getFSM().forceState(UnitState.Dead);
                    } else {
                        pCursor.selectedUnit.getFSM().setNewStateIfAllowed(UnitState.Done);
                    }
                    
                    if (pCursor.receivingEnd.getStat(BaseStat.currentHP) <= 0 && pCursor.receivingEnd.getFSM().getState().getEnum() != UnitState.Dead) { 
                        localRootNode.detachChild(pCursor.receivingEnd.getNode());
                        map00.fullmap[pCursor.getElevation()][pCursor.receivingEnd.getPosX()][pCursor.receivingEnd.getPosY()].resetOccupier();
                        pCursor.receivingEnd.getFSM().forceState(UnitState.Dead);
                    }
                
                    fsm.setNewStateIfAllowed(new MasterFsmState().setAssetManager(assetManager));
                    pCursor.setStateIfAllowed(CursorState.CursorDefault);
                    pCursor.resetState();
                    break;
                case DuringBattle:
                    if (amassedTPF >= (1f / 65f)) {
                        currentBattle.allowUpdate = true;
                        amassedTPF = 0;
                    } else { currentBattle.allowUpdate = false; }
                    
                    currentBattle.update(tpf);
                    
                    amassedTPF += tpf;
                    
                    if (currentBattle.getBattleState() == BattleState.FullyDone) {
                        rootNode.attachChild(localRootNode);
                        guiNode.detachAllChildren();
                        guiNode.attachChild(localGuiNode);
                        //screenView = null;
                        fightCam.setViewPort(0.0f, 0.0f, 0.0f, 0.0f);
                        fsm.setNewStateIfAllowed(new MasterFsmState(MapFlowState.PostBattle).setAssetManager(assetManager));
                        pCursor.setStateIfAllowed(CursorState.CursorDefault);
                        mapFlow.setLastStrikes(currentBattle.getActualStrikes());
                        currentBattle = null;
                    }
                    break;
                default:
                    break;
            }
        }
        fc++;
    }
    
    private void initializeBattle() {
        fightCam = cam.clone();
        fightCam.setViewPort(0.0f, 1.0f, 0.0f, 1.0f);
        screenView = renderManager.createMainView("Fight", fightCam);
        screenView.setClearFlags(true, true, true);
                    
        battleScene = (Node)assetManager.loadModel("Scenes/Battle/battletest5.j3o");
        Node master = new Node("master"), gui = new Node("battlegui"); //TODO: fix this
        master.attachChild(battleScene);
        master.attachChild(gui);
        
        screenView.attachScene(master);
        
        fightCam.setLocation(new Vector3f(battleScene.getChild("FullPlane").getWorldTranslation().x, battleScene.getChild("FullPlane").getWorldTranslation().y + 2.5f, battleScene.getChild("FullPlane").getWorldTranslation().z + 13.25f));
        Quaternion cameraRotation = new Quaternion();
        cameraRotation.fromAngles(0, FastMath.PI, 0);
        fightCam.setRotation(cameraRotation);
                    
        //TODO: FIX THIS
        setTextures(
                Arrays.asList((Geometry)battleScene.getChild("treeStump"), (Geometry)battleScene.getChild("treeTrunk"), (Geometry)battleScene.getChild("treeBranches"), (Geometry)battleScene.getChild("battleTerrain"), (Geometry)battleScene.getChild("rightRock"), (Geometry)battleScene.getChild("leftRock")),
                Arrays.asList(assetManager.loadTexture("Textures/battle/test/branches.png"), assetManager.loadTexture("Textures/battle/test/branches.png"), assetManager.loadTexture("Textures/battle/test/branches.png"), assetManager.loadTexture("Textures/battle/test/cliff.png"), assetManager.loadTexture("Textures/battle/test/cliff.png"), assetManager.loadTexture("Textures/battle/test/cliff.png")),
                "LightMap",
                new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md")
        );
        
        battleScene.setCullHint(CullHint.Never);
        
        Conveyer battleData = ((MasterFsmState)fsm.getState()).getConveyer();
        
        currentBattle = new Battle(battleData, PrebattleForecast.createBattleForecast(battleData), fightCam, pCursor.getPurpose());
        currentBattle.initializeVisuals(master, battleScene, gui, assetManager, guiNode);
        gui.scale(0.005f);
        gui.move(0, 4, 10);
        
        master.updateLogicalState(1f / 60f);
        master.updateGeometricState();
        
        rootNode.detachChild(localRootNode);
        amassedTPF = 0;
        //transitionToFight = ViewPortAnimation.cutOpen(fightCam);
        //transitionToFight.beginTransitions();

        pCursor.forceState(CursorState.Idle);
        fsm.setNewStateIfAllowed(new MasterFsmState(MapFlowState.DuringBattle).setAssetManager(assetManager));
    }
    
    protected void setTextures(List<Geometry> stuff, List<Texture> textures, String texType, Material template) {
        for (int i = 0; i < stuff.size(); i++) {
            Material M = template.clone();
            M.setTexture(texType, textures.get(i));
            stuff.get(i).setMaterial(M);
        }
    }
    
}
