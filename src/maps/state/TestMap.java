/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.state;

import battle.Battle;
import battle.Battle.BattleState;
import battle.Catalog;
import battle.Conveyer;
import battle.JobClass;
import battle.Unit;
import battle.Unit.UnitStatus;
import battle.item.Weapon;

import com.atr.jme.font.asset.TrueTypeLoader;
import com.destroflyer.jme3.effekseer.renderer.EffekseerControl;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.input.FlyByCamera;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import com.jme3.font.BitmapFont;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.Light;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import static com.jme3.material.RenderState.BlendMode.Color;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;

import com.jme3.post.SceneProcessor;
import com.jme3.profile.AppProfiler;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.control.LodControl;

import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;

import com.jme3.terrain.Terrain;
import com.jme3.texture.FrameBuffer;
import com.simsilica.lemur.Axis;

import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;
import edited.FlyCamera;
import edited.state.FlyCamTrueAppState;

import general.ActionMenu;
import general.MenuState;
import general.VisualTransition;
import general.VisualTransition.Progress;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import jme3tools.optimize.LodGenerator;

import jme3tools.savegame.SaveGame;
import maps.layout.Cursor;
import maps.layout.Map;
import maps.layout.MoveState;
import maps.layout.StatScreen;
import maps.layout.TangibleUnit;
import maps.layout.Tile;
import misc.DirFileExplorer;
import misc.Loader;
import misc.ViewPortAnimation;
import etherealtempest.FSM;
import etherealtempest.FSM.EntityState;
import etherealtempest.FsmState;
import etherealtempest.Main;
import etherealtempest.MasterFsmState;
import general.visual.RadialProgressBar;

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
    private Camera cam, fightCam;
    private AssetManager assetManager;
    private AppStateManager stManager;
    private RenderManager renderManager;
    private ViewPort screenView;
    
    private FlyByCamera flCam;
    private TerrainQuad terrain;
    private TerrainQuad mob, atkrange, mapscene;
    private Map map00;
    private ArrayList<TangibleUnit> units = new ArrayList<>();
    private Node battleScene;

    private float tileOpacity = 0;
    
    Material mat_terrain;
    
    protected Cursor pCursor;
    protected ActionMenu postAction;
    protected StatScreen stats;
    protected Battle currentBattle;
    protected ViewPortAnimation transitionToFight;
    protected Vector3f worldUpVector = new Vector3f(0, 1, 0);
    
    private Savable savestate;
    
    private final FSM fsm = new FSM() {
        @Override
        public void setNewStateIfAllowed(FsmState st) {
            state = st; //maybe change this if needed
            if (st.getEnum() == EntityState.PostActionMenuOpened && postAction.getState().getEnum() == EntityState.GuiClosed) {
                localGuiNode.setLocalTranslation(10, 760, 0);
                localGuiNode.attachChild(postAction);
                postAction.resetPos();
                
                //postAction.getMenuNode().move(50, 100, 10);
                //postAction.setLocalTranslation((pCursor.selectedUnit.getPosX() / ((float)map00.getTilesX())) * cam.getWidth(), FastMath.pow((pCursor.selectedUnit.getPosY() / ((float)map00.getTilesY())) * cam.getHeight(), -1), postAction.getNode().getLocalTranslation().z);
                postAction.setLocalTranslation((cam.getWidth() / 8) + 600, -150, postAction.getNode().getLocalTranslation().z);
                postAction.setStateIfAllowed(
                        new MenuState(st.getEnum()).setConveyer(
                                new Conveyer(pCursor.selectedUnit)
                                        .setMap(map00)
                                        .setAllUnits(units)
                                        .setCursor(pCursor)
                                        .setAssetManager(assetManager)
                        )
                );
                postAction.initializeTier1Submenus(
                    new Conveyer(pCursor.selectedUnit)
                            .setMap(map00)
                            .setAllUnits(units)
                            .setCursor(pCursor)
                            .setAssetManager(assetManager)
                );
                state = new MasterFsmState().setAssetManager(assetManager);
            } 
        }
        
    };
    
    public TestMap(SimpleApplication app, Camera cm, FlyByCamera fyCam) {
        app0 = app;
        
        rootNode = app.getRootNode();
        guiNode = app.getGuiNode();
        
        assetManager = app.getAssetManager();
        inputManager = app.getInputManager();
        renderManager = app.getRenderManager();
        
        cam = cm;
        cam.setViewPort(0.0f, 1.0f, 0.0f, 1.0f);
        flCam = fyCam;
        
        fsm.setNewStateIfAllowed(new MasterFsmState().setAssetManager(assetManager));
        stats = new StatScreen(app.getAssetManager());
        //mapcatalog = new Catalog(app.getAssetManager(), new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md"));
        //audioRenderer = app.getAudioRenderer();
        
        //initialize gui
        GuiGlobals.initialize(app0);
        
        //load glass style
        BaseStyles.loadGlassStyle();
        //default style is glass for now
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
        GuiGlobals.getInstance().setCursorEventsEnabled(false);
        
        assetManager.registerLoader(TrueTypeLoader.class, "ttf");
        
        this.analogListener = new AnalogListener() {
            @Override
            public void onAnalog(String name, float value, float tpf) {
                
            }
        
        };
        
        this.alU = new ActionListener() {
            @Override
            public void onAction(String name, boolean keyPressed, float tpf) {
                
                if (stats.getState().getEnum() != EntityState.GuiClosed && keyPressed) {
                    stats.resolveInput(name, tpf);
                    if (stats.getState().getEnum() == EntityState.GuiClosed) { 
                        fsm.setNewStateIfAllowed(new MasterFsmState().setAssetManager(assetManager));
                        pCursor.setStateIfAllowed(new FsmState(EntityState.CursorDefault));
                    }
                }
                if (pCursor.getState().getEnum() != EntityState.Idle && stats.getState().getEnum() == EntityState.GuiClosed && postAction.getState().getEnum() != EntityState.PostActionMenuOpened) {
                    pCursor.resolveInput(name, tpf, keyPressed);
                }
                if (postAction.getState().getEnum() == EntityState.PostActionMenuOpened && keyPressed) { //postActionMenu
                    if (name.equals("select")) {
                        pCursor.forceState(new FsmState(EntityState.Idle));
                    }
                    
                    MasterFsmState change = postAction.resolveInput(name, tpf);
                    if (change != null) {
                        fsm.setNewStateIfAllowed(change);
                    }
                }
                if (fsm.getState().getEnum() == EntityState.DuringBattle) {
                    currentBattle.resolveInput(name, tpf, keyPressed);
                }
                
                int fps = (int)(1 / tpf);
                
                if (name.equals("open unit info menu") && keyPressed) {
                    if (stats.getState().getEnum() != EntityState.StatScreenOpened && map00.fullmap[pCursor.getElevation()][pCursor.pX][pCursor.pY].getOccupier() != null) {
                        fsm.setNewStateIfAllowed(new MasterFsmState(EntityState.Idle).setAssetManager(assetManager));
                        stats.forceState(new FsmState(EntityState.StatScreenOpened));
                        localGuiNode.setLocalTranslation(10, 760, 0);
                        flCam.setEnabled(false);
                        stats.startUnitViewGUI(map00.fullmap[pCursor.getElevation()][pCursor.pX][pCursor.pY].getOccupier());
                        pCursor.forceState(new FsmState(EntityState.Idle));
                    }
                }
                
                //all the below inputs are for testing purposes
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
            
        Node S = (Node)assetManager.loadModel("Scenes/testscene.j3o");
        
        Catalog.initializeCatalogImages(assetManager);
        
        mob = (TerrainQuad)(S.getChild("movement"));
        mapscene = (TerrainQuad)(S.getChild("map"));
        localRootNode.attachChild(S);
        
        localGuiNode.attachChild(stats);
        stats.initializeRenders();
        
        Catalog.FormulaCatalog[0].initializeAnimation(assetManager);
        
        initMap();
        initMappers();
    }   
    
    public void initMap() {
        TerrainQuad[] mobilitysquares = {mob};
        TerrainQuad[] mapsquares = {mapscene};
        map00 = new Map(16, 16, 1, assetManager, mapsquares, mobilitysquares, "terrain-testscene");
        
        //add units
        units.add(new TangibleUnit(Catalog.UnitCatalog[0], new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md").clone()));
        units.get(0).unitStatus = UnitStatus.Player;
        units.get(0).hasStashAccess = true;
        
        units.add(new TangibleUnit(Catalog.UnitCatalog[1], new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md").clone()));
        units.get(1).unitStatus = UnitStatus.Enemy;
        
        units.add(new TangibleUnit(Catalog.UnitCatalog[1], new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md").clone()));
        units.get(2).unitStatus = UnitStatus.Enemy;
       
        units.add(new TangibleUnit(Catalog.UnitCatalog[1], new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md").clone()));
        units.get(3).unitStatus = UnitStatus.Enemy;
        
        for (int k = 0; k < units.size(); k++) {
            localRootNode.attachChild(units.get(k).getGeometry());
            units.get(k).remapPositions((int)(1 + (8 * Math.random())), (int)(1 + (10 * Math.random())), 0, map00);
        }
        
        pCursor = new Cursor(new Quad(24f, 24f));
        Material pcs = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        pcs.setTexture("ColorMap", assetManager.loadTexture("Models/Sprites/unfinished/Map/tpCursor.png"));
        pcs.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        pCursor.geometry.setMaterial(pcs);
        pCursor.init();
        pCursor.setPosition(5, 2, 0, map00); //change position later
        localRootNode.attachChild(pCursor.geometry);
        
        MasterFsmState.setCurrentDefaultMap(map00);
        
        initializePostMoveMenu();
        
        cam.setLocation(new Vector3f(pCursor.geometry.getWorldTranslation().x + 8f - 32f, pCursor.geometry.getWorldTranslation().y + 200f, pCursor.geometry.getWorldTranslation().z + 8f));
        cam.lookAt(pCursor.geometry.getWorldTranslation(), worldUpVector);
        Quaternion cameraRotation = new Quaternion();
        cameraRotation.fromAngles(FastMath.PI / 2, FastMath.PI / 2, 0);
        cam.setRotation(cameraRotation);
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
                chainDeselectedAvailable, assetManager.loadTexture("Interface/GUI/general_ui/nothing.png")
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
    
    float accumulatedTPF = 0;
    
    @Override
    public void update(float tpf) {
        int fps = (int)(1 / tpf);
        
        postAction.update(1f / 60f);
        
        if (accumulatedTPF >= (1f / 60f)) {
            fullUpdate(tpf);
            accumulatedTPF = 0;
        }
        
        accumulatedTPF += tpf;
    }
    
    private void fullUpdate(float tpf) {
        updateAI(1f / 60f);
        
        stats.update(1f / 60f);
        
        for (TangibleUnit tu : units) {
            pCursor.update(1f / 60f, tu, ((MasterFsmState)fsm.getState()));
            tu.update(1f / 60f, fsm);
        }
    }
    
    int fc = 0;
    float amassedTPF = 0;
    
    public void updateAI(float tpf) {
        //System.out.println(fsm.getState().getEnum());
        if (fc > 1000) { fc = 0; }
        
        if (fsm.getState().getEnum() != EntityState.Idle) {
            cam.setLocation(new Vector3f(pCursor.geometry.getWorldTranslation().x + 8f, cam.getLocation().y, pCursor.geometry.getWorldTranslation().z + 8f));
            
            switch (fsm.getState().getEnum()) {
                case MapDefault:
                    break;
                case PostBattle:
                    //modify later
                    if (pCursor.selectedUnit.currentHP <= 0) {
                        localRootNode.detachChild(pCursor.selectedUnit.getGeometry());
                        map00.fullmap[pCursor.getElevation()][pCursor.selectedUnit.getPosX()][pCursor.selectedUnit.getPosY()].resetOccupier();
                        pCursor.selectedUnit.getFSM().forceState(new FsmState(EntityState.Dead));
                    } else {
                        pCursor.selectedUnit.getFSM().setNewStateIfAllowed(new FsmState(EntityState.Done));
                    }
                    
                    if (pCursor.receivingEnd.currentHP <= 0 && pCursor.receivingEnd.getFSM().getState().getEnum() != EntityState.Dead) { 
                        localRootNode.detachChild(pCursor.receivingEnd.getGeometry());
                        map00.fullmap[pCursor.getElevation()][pCursor.receivingEnd.getPosX()][pCursor.receivingEnd.getPosY()].resetOccupier();
                        pCursor.receivingEnd.getFSM().forceState(new FsmState(EntityState.Dead));
                    }
                
                    fsm.setNewStateIfAllowed(new MasterFsmState().setAssetManager(assetManager));
                    pCursor.setStateIfAllowed(new FsmState(EntityState.CursorDefault));
                    pCursor.resetState(map00);
                    break;
                case DuringBattle:
                    
                    /*if (fc % 2 == 0 || fc % 3 == 0 || fc % 5 == 0 || fc % 7 == 0 || fc % 9 == 0) {
                        currentBattle.update(tpf);
                    }*/
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
                        fsm.setNewStateIfAllowed(new MasterFsmState(EntityState.PostBattle).setAssetManager(assetManager));
                        currentBattle = null;
                    }
                    break;
                case PreBattle:
                    //pCursor.setStateIfAllowed(new FsmState(EntityState.CursorDefault));
                    fightCam = cam.clone();
                    fightCam.setViewPort(0.0f, 1.0f, 0.0f, 1.0f);
                    screenView = renderManager.createMainView("Fight", fightCam);
                    screenView.setClearFlags(true, true, true);
                    
                    battleScene = (Node)assetManager.loadModel("Scenes/Battle/battletest5.j3o");
                    Node master = new Node("master"), gui = new Node("battlegui");
                    master.attachChild(battleScene);
                    master.attachChild(gui);
                    
                    screenView.attachScene(master);
                    
                    fightCam.setLocation(new Vector3f(battleScene.getChild("FullPlane").getWorldTranslation().x, battleScene.getChild("FullPlane").getWorldTranslation().y + 2.5f, battleScene.getChild("FullPlane").getWorldTranslation().z + 13.25f));
                    Quaternion cameraRotation = new Quaternion();
                    cameraRotation.fromAngles(0, FastMath.PI, 0);
                    fightCam.setRotation(cameraRotation);
                    
                    setTextures(
                            Arrays.asList((Geometry)battleScene.getChild("treeStump"), (Geometry)battleScene.getChild("treeTrunk"), (Geometry)battleScene.getChild("treeBranches"), (Geometry)battleScene.getChild("battleTerrain"), (Geometry)battleScene.getChild("rightRock"), (Geometry)battleScene.getChild("leftRock")),
                            Arrays.asList(assetManager.loadTexture("Textures/battle/test/branches.png"), assetManager.loadTexture("Textures/battle/test/branches.png"), assetManager.loadTexture("Textures/battle/test/branches.png"), assetManager.loadTexture("Textures/battle/test/cliff.png"), assetManager.loadTexture("Textures/battle/test/cliff.png"), assetManager.loadTexture("Textures/battle/test/cliff.png")),
                            "LightMap",
                            new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md")
                    );
                    
                    battleScene.setCullHint(CullHint.Never);

                    currentBattle = new Battle(((MasterFsmState)fsm.getState()).getConveyer(), 1, fightCam, pCursor.getPurpose());
                    currentBattle.initializeVisuals(master, battleScene, gui, assetManager, guiNode);
                    gui.scale(0.005f);
                    gui.move(0, 4, 10);
                    
                    master.updateLogicalState(tpf);
                    master.updateGeometricState();
                    
                    rootNode.detachChild(localRootNode);
                    amassedTPF = 0;
                    //transitionToFight = ViewPortAnimation.cutOpen(fightCam);
                    //transitionToFight.beginTransitions();
                    
                    pCursor.setStateIfAllowed(new FsmState(EntityState.Idle));
                    fsm.setNewStateIfAllowed(new MasterFsmState(EntityState.DuringBattle).setAssetManager(assetManager));
                    break;
                default:
                    break;
            }
        }
        fc++;
    }
    
    protected void setTextures(List<Geometry> stuff, List<Texture> textures, String texType, Material template) {
        for (int i = 0; i < stuff.size(); i++) {
            Material M = template.clone();
            M.setTexture(texType, textures.get(i));
            stuff.get(i).setMaterial(M);
        }
    }
    
    public void createT1() {
        /** 1. Create terrain material and load four textures into it. */
        mat_terrain = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");

    /** 1.1) Add ALPHA map (for red-blue-green coded splat textures) */
        mat_terrain.setTexture("LightMap", assetManager.loadTexture("Textures/grassland0.jpg"));

    /** 1.2) Add GRASS texture into the red layer (Tex1). */
    //Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
    //grass.setWrap(WrapMode.Repeat);
    //mat_terrain.setTexture("Tex1", grass);
    //mat_terrain.setFloat("Tex1Scale", 64f);

    /** 1.3) Add DIRT texture into the green layer (Tex2) */
    //Texture dirt = assetManager.loadTexture("Textures/dirt.jpg");
    //dirt.setWrap(Texture.WrapMode.Repeat);
    //mat_terrain.setTexture("Tex1", dirt);
    //mat_terrain.setFloat("Tex1Scale", 64f);

    /** 1.4) Add ROAD texture into the blue layer (Tex3) */
    //Texture rock = assetManager.loadTexture("Textures/Terrain/splat/road.jpg");
    //rock.setWrap(WrapMode.Repeat);
    //mat_terrain.setTexture("Tex3", rock);
    //mat_terrain.setFloat("Tex3Scale", 128f);

    /** 2. Create the height map */
    AbstractHeightMap heightmap = null;
    Texture heightMapImage = assetManager.loadTexture("Textures/unfinished/aaa Height Map (Merged).png");
    heightmap = new ImageBasedHeightMap(heightMapImage.getImage());
    heightmap.load();
    

    /** 3. We have prepared material and heightmap.
     * Now we create the actual terrain:
     * 3.1) Create a TerrainQuad and name it "my terrain".
     * 3.2) A good value for terrain tiles is 64x64 -- so we supply 64+1=65.
     * 3.3) We prepared a heightmap of size 512x512 -- so we supply 512+1=513.
     * 3.4) As LOD step scale we supply Vector3f(1,1,1).
     * 3.5) We supply the prepared heightmap itself.
     */
    int patchSize = 65;
    terrain = new TerrainQuad("my terrain", patchSize, 513, heightmap.getHeightMap());

    /** 4. We give the terrain its material, position & scale it, and attach it. */
    terrain.setMaterial(mat_terrain);
    terrain.setLocalTranslation(0, -100, 0);
    terrain.setLocalScale(2f, 1f, 2f);
    rootNode.attachChild(terrain);
    
     TerrainLodControl control = new TerrainLodControl(terrain, cam);
     terrain.addControl(control);
    
    }
    
    
}
