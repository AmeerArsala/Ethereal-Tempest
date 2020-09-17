/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.ui;

import etherealtempest.info.ActionInfo;
import general.visual.VisualTransition;
import etherealtempest.info.Conveyer;
import battle.item.Weapon;
import com.jme3.asset.AssetManager;
import com.jme3.font.LineWrapMode;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.component.TbtQuadBackgroundComponent;
import edited.EditedTextField;
import general.ui.Submenu.TransitionState;
import general.ui.Submenu.TransitionType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import maps.layout.Cursor.Purpose;
import maps.layout.Map;
import maps.ui.StatScreen;
import maps.layout.TangibleUnit;
import etherealtempest.FSM;
import etherealtempest.FSM.EntityState;
import etherealtempest.FsmState;
import etherealtempest.MasterFsmState;
import fundamental.Tool.ToolType;
import general.ResetProtocol;
import etherealtempest.info.ActionInfo.PostMoveAction;
import maps.layout.Coords;

/**
 *
 * @author night
 */
public class ActionMenu extends Container {
    private Node menuNode = new Node("menu"), forwarder;
    
    //animated options that won't always be there unless it's the centerpiece or backdrop
    //private Quad backdrop = new Quad(60f, 60f);
    //public Geometry geoBackdrop = new Geometry("Quad", backdrop);
    
    private Texture transparentbg;
    private static VisualTransition windowChanger;
    
    /*Quad test = new Quad(90f, 90f);
    Geometry tst = new Geometry("test", test);*/
    
    public Panel geoBackdrop;
    private Material matbd;
    private Texture[] bd;
                       //mat_ability, mat_aid, mat_attack, mat_formation, mat_inventory, mat_skill, mat_trade, //length 4 for this row
                       //mat_annex, mat_escape, mat_talk, //length 6 for this row (animated)
                       //mat_done, mat_chainUnavailable, //length 2 bc done and both cases of unavailable (selected and unselected)
    
    private FSM fsm = new FSM() {
        @Override
        public void setNewStateIfAllowed(FsmState st) {
            state = st;
            if (state.getEnum() == EntityState.PostActionMenuOpened) {
                initialize(((MenuState)state).getConveyer());
            }
        }
    
    };
    
    private int currentX = 0, currentY = 0;
    
    //public boolean stashAvailable = false;
    
    public MenuOption 
            attack = new MenuOption("attack", new Quad(41.115f, 12f), true),
            
            trade = new MenuOption("trade", new Quad(34.272f, 12f), false),
            
            inventory = new MenuOption("inventory", new Quad(30f, 12f), true, 
                    new Submenu() {
                        TangibleUnit unit;
                        Map map;
                        
                        @Override
                        public void retrieveData() {
                            //set the stuff from Conveyer
                            unit = data.getUnit();
                            map = data.getMap();
                        }
                        
                        @Override
                        public void initialize() {
                            menuNode.attachChild(this);
                            this.setLocalTranslation(25, 10, 0);
                            //this.setLocalTranslation(90, -90, 0);
                            //this.move(-65, 100, 0);
                            //System.out.println(this.getLocalTranslation());
                            ((TbtQuadBackgroundComponent)this.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
                            //((TbtQuadBackgroundComponent)this.getBackground()).setAlpha(0);
                        }

                        @Override
                        public List<SubmenuOption> options() {
                            List<SubmenuOption> s_options = new ArrayList<>();
                            s_options.add
                            (
                                new SubmenuOption(85f, 30f, 0) { //personal inventory option
                                    TangibleUnit tu;
                                    Map current;
                                    Label text;
                                    
                                    @Override
                                    public void initialize() {
                                        this.setBackground(new QuadBackgroundComponent(((MenuState)ActionMenu.this.fsm.getState()).getConveyer().getAssetManager().loadTexture("Interface/GUI/postmovemenu/cursorframes/0.png")));
                                        
                                        text = new Label("Inventory");
                                        text.setFont(((MenuState)ActionMenu.this.fsm.getState()).getConveyer().getAssetManager().loadFont("Interface/Fonts/imfelldwpica.fnt"));
                                        text.setFontSize(20f);
                                        text.setInsets(new Insets3f(1, 1, 1, 1));
                                        text.setColor(ColorRGBA.White);
                                        text.setTextHAlignment(HAlignment.Center);
                                        this.addChild(text);
                                        
                                        child = ActionMenu.getInventoryContents(menuNode, this, info);
                                        child.partialInitialize(info);
                                    }
                                
                                    @Override
                                    public void interpretData() {
                                        tu = info.getUnit();
                                        current = info.getMap();
                                    }

                                    @Override
                                    public void select() {
                                        //TODO: open up another submenu here for their inventory
                                        windowChanger.beginTransitions(Arrays.asList(VisualTransition.ZoomIn().setLength(1f / 6f).setStartingIndexScale(1f).setEndVal(1.2f)));
                                        child.lightInitialize();
                                        child.transitionIn(TransitionType.Forward);
                                    }
                                }.setParentSubmenu(this)
                            );
                            
                            if (unit.hasStashAccess) {
                                s_options.add
                                (
                                    new SubmenuOption(85f, 30f, 1) { //stash option
                                        TangibleUnit tu;
                                        Map current;
                                        Label text;
                                        
                                        @Override
                                        public void initialize() {
                                            this.setBackground(new QuadBackgroundComponent(((MenuState)ActionMenu.this.fsm.getState()).getConveyer().getAssetManager().loadTexture("Interface/GUI/postmovemenu/cursorframes/0.png")));
                                            ((QuadBackgroundComponent)this.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
                                            text = new Label("Stash");
                                            text.setFont(((MenuState)ActionMenu.this.fsm.getState()).getConveyer().getAssetManager().loadFont("Interface/Fonts/imfelldwpica.fnt"));
                                            text.setFontSize(20f);
                                            text.setInsets(new Insets3f(1, 1, 1, 1));
                                            text.setColor(ColorRGBA.White);
                                            text.setTextHAlignment(HAlignment.Center);
                                            this.addChild(text);
                                        }
                                        
                                        @Override
                                        public void interpretData() {
                                            tu = info.getUnit();
                                            current = info.getMap();
                                        }

                                        @Override
                                        public void select() {
                                            //TODO: open up another submenu here for stash
                                        }
                                    }.setParentSubmenu(this)
                                );
                            }
                            
                            return s_options;
                        }

                        @Override
                        public void resolveInput(String name, float tpf) {
                            //TODO
                            if (active && state == TransitionState.Standby) {
                                switch (name) {
                                    case "select":
                                        menuOptions.get(currentIndex).select();
                                        if (menuOptions.get(currentIndex).hasSubmenu()) {
                                            transitionOut(TransitionType.Forward);
                                        }
                                        break;
                                    case "deselect":
                                        //go to previous menu
                                        transitionOut(TransitionType.Backward);
                                        for (int x = -1; x <= 1; x++) {
                                            for (int y = -2; y <= 2; y++) {
                                                try {
                                                        getOptionByCoordinates(x, y).beginReappearance();
                                                    }
                                                catch (NullPointerException e) {}
                                            }
                                        }
                                        MenuOption.ogMenuActive = true;
                                        break;
                                    case "move up":
                                        moveUp();
                                        break;
                                    case "move down":
                                        moveDown();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                        
                        int count = 0;
                        int getIndex() {
                            float c = count % 14;
                            return (int)(0.35f * ((-1 * FastMath.abs(c - 7)) + 7));
                        }
                        
                        @Override
                        public void update() {
                            //do the cosmetic updates here
                            if (count == Integer.MAX_VALUE) { count = 0; }
                            
                            if (active) {
                                ((QuadBackgroundComponent)menuOptions.get(currentIndex).getBackground())
                                        .setTexture(((MenuState)ActionMenu.this.fsm.getState()).getConveyer()
                                        .getAssetManager().loadTexture("Interface/GUI/postmovemenu/cursorframes/" + getIndex() + ".png"));
                            } else {
                            
                            }
                            
                            count++;
                        }
                        
                        @Override
                        public void reset() {
                            menuNode.detachChild(this);
                        }
                        
                    }
            ),
            
            formation = new MenuOption("formation", new Quad(37.023f, 12f), false),
            skill = new MenuOption("skill", new Quad(30f, 12f), false, 
                    new ResetProtocol() {
                        @Override
                        public void execute() {
                            windowChanger.beginTransitions(Arrays.asList(VisualTransition.ZoomIn().setLength(1f / 6f).setStartingIndexScale(1f).setEndVal(1.2f)));
                        }
                    }
            ),
            aid = new MenuOption("aid", new Quad(35.9f, 12f), false, 
                    new ResetProtocol() {
                        @Override
                        public void execute() {
                            windowChanger.beginTransitions(Arrays.asList(VisualTransition.ZoomIn().setLength(1f / 6f).setStartingIndexScale(1f).setEndVal(1.2f)));
                        }
                    }
            ),
            ability = new MenuOption("ability", new Quad(41.042f, 12f), false),
            done = new MenuOption("done", new Quad(32.9f, 12f), true),
            annex = new AnimOption("annex", new Quad(30f, 12f), false),
            escape = new AnimOption("escape", new Quad(30f, 12f), false),
            talk = new AnimOption("talk", new Quad(30f, 12f), false),
            eye = new Centerpiece("chain attack", new Quad(48.849f, 27f), false);
    
    //material format is typically: selectedAvailable, selectedUnavailable, deselectedAvailable, deselectedUnavailable
    public ActionMenu(Material defMat,
                      Texture[] backdrop,
                      Texture[] attack,
                      Texture[] ability, 
                      Texture[] aid, 
                      Texture[] formation, 
                      Texture[] inventory, 
                      Texture[] skill, 
                      Texture[] trade, 
                      Texture[] annexAnimated,
                      Texture[] annexSelectedAnimated,
                      Texture[] escapeAnimated,
                      Texture[] escapeSelectedAnimated,
                      Texture[] talkAnimated,
                      Texture[] talkSelectedAnimated,
                      Texture[] done,
                      Texture[] chainUnavailable,
                      Texture[] chainSelectedAvailable,
                      Texture[] chainDeselectedAvailable,
                      Texture nothing) {
        
        super();
        
        bd = backdrop;
        geoBackdrop = new Panel(180f, 180f);
        VisualTransition.setDimensions(180f, 180f);
        //geoBackdrop = new Container();
        geoBackdrop.setBackground(new QuadBackgroundComponent(bd[0]));
        //((QuadBackgroundComponent)geoBackdrop.getBackground()).setAlpha(0);
        this.ability.matclassic = ability;
        this.aid.matclassic = aid;
        this.formation.matclassic = formation;
        this.inventory.matclassic = inventory;
        this.skill.matclassic = skill;
        this.trade.matclassic = trade;
        this.done.matclassic = done;
        this.attack.matclassic = attack;
        
        ((AnimOption)annex).matanimated = annexAnimated;
        ((AnimOption)annex).matanimatedSelected = annexSelectedAnimated;
        ((AnimOption)escape).matanimated = escapeAnimated;
        ((AnimOption)escape).matanimatedSelected = escapeSelectedAnimated;
        ((AnimOption)talk).matanimated = talkAnimated;
        ((AnimOption)talk).matanimatedSelected = talkSelectedAnimated;
        
        ((Centerpiece)eye).matclassic = chainUnavailable;
        ((Centerpiece)eye).matanimatedSelected = chainSelectedAvailable;
        ((Centerpiece)eye).matanimated = chainDeselectedAvailable;
        
        this.ability.optionmat = defMat.clone();
        this.aid.optionmat = defMat.clone();
        this.formation.optionmat = defMat.clone();
        this.inventory.optionmat = defMat.clone();
        this.skill.optionmat = defMat.clone();
        this.trade.optionmat = defMat.clone();
        this.attack.optionmat = defMat.clone();
        this.done.optionmat = defMat.clone();
        escape.optionmat = defMat.clone();
        talk.optionmat = defMat.clone();
        eye.optionmat = defMat.clone();
        annex.optionmat = defMat.clone();
        matbd = defMat.clone();
        
        //annex.isShown and escape.isShown cannot both be true, but they can both be false
        annex.isShown = false;
        annex.isAvailable = false;
        
        talk.isShown = false;
        talk.isAvailable = false;
        
        escape.isShown = false;
        escape.isAvailable = false;
        
        eye.isSelected = true;
        eye.isAvailable = true;
        
        this.aid.isAvailable = true;
        this.skill.isAvailable = true;
        
        eye.geo.move(1f, 0, 3f);
        eye.setBirthMove(1f, 0, 3f);
        
        this.inventory.geo.move(50f, 5f, 2);
        this.inventory.setBirthMove(50f, 5f, 2);
        
        this.done.geo.move(8f, -35, 2);
        this.done.setBirthMove(8f, -35, 2);
        
        this.attack.geo.move(6f, 45, 2);
        this.attack.setBirthMove(6f, 45, 2);
        
        this.aid.geo.move(-30, 5, 2);
        this.aid.setBirthMove(-30, 5, 2);
        
        this.ability.geo.move(-20f, -15, 2);
        this.ability.setBirthMove(-20f, -15, 2);
        
        this.skill.geo.move(-20f, 25, 2);
        this.skill.setBirthMove(-20f, 25, 2);
        
        this.formation.geo.move(30, -15, 2);
        this.formation.setBirthMove(30, -15, 2);
        
        this.trade.geo.move(40, 25, 2);
        this.trade.setBirthMove(40, 25, 2);
        
        this.talk.geo.move(41f - 16.283f, 37, 2f);
        this.talk.setBirthMove(41 - 16.283f, 37, 2f);
        
        this.escape.geo.move(41f - 16.283f, 18, 2f);
        this.escape.setBirthMove(41 - 16.283f, 18, 2f);
        
        updateState(0);
        
        addChild(geoBackdrop);
        
        forwarder = new Node();
        
        //menuNode.attachChild(geoBackdrop);
        forwarder.attachChild(eye.geo);
        forwarder.attachChild(this.annex.geo);
        forwarder.attachChild(this.attack.geo);
        forwarder.attachChild(this.escape.geo);
        forwarder.attachChild(this.talk.geo);
        forwarder.attachChild(this.trade.geo);
        forwarder.attachChild(this.formation.geo);
        forwarder.attachChild(this.skill.geo);
        forwarder.attachChild(this.ability.geo);
        forwarder.attachChild(this.aid.geo);
        forwarder.attachChild(this.done.geo);
        forwarder.attachChild(this.attack.geo);
        forwarder.attachChild(this.inventory.geo);
        
        menuNode.attachChild(forwarder);
        geoBackdrop.attachChild(menuNode);
        
        menuNode.move(65, -100, 30);
        setBackground(new QuadBackgroundComponent(nothing));
        transparentbg = nothing.clone();
        
        scale(2f);
        windowChanger = new VisualTransition(geoBackdrop);
        
        fsm.setNewStateIfAllowed(new FsmState(EntityState.GuiClosed));
    }
    
    public int getPositionX() { return currentX; }
    public int getPositionY() { return currentY; }
    
    private void initialize(Conveyer C) {
        initializeTier1Submenus(C);
        
        //initialize position from presets
        ActionInfo info = C.getUnit().determineOptions(C);
        for (PostMoveAction enabled : info.getAvailableActions()) {
            switch (enabled) {
                case Attack:
                    attack.isAvailable = true;
                    break;
                case Ether:
                    aid.isAvailable = true;
                    break;
                case Item:
                    inventory.isAvailable = true;
                    break;
                case Skill:
                    skill.isAvailable = true;
                    break;
                case Trade:
                    trade.isAvailable = true;
                    break;
                case ChainAttack:
                    eye.isAvailable = true;
                    break;
                case Formation:
                    formation.isAvailable = true;
                    break;
                case Ability:
                    ability.isAvailable = true;
                    break;
                case Talk:
                    talk.isShown = true;
                    talk.isAvailable = true;
                    break;
                default:
                    break;
            }
        }
        
        setPos(info.getStartingPosition());
    }
    
    private void initializeTier1Submenus(Conveyer C) {
        inventory.submenu.partialInitialize(C);
        
        aid.submenu = getFormulaContents(menuNode, C);
        aid.submenu.partialInitialize(C);
        
        skill.submenu = getSkillContents(menuNode, C);
        skill.submenu.partialInitialize(C);
    }
    
    public MenuOption getOptionByCoordinates(int x, int y) {
        if (x == 0 && y == 0) { return eye; } // (0, 0)
        if (x == 1 && y == 0) { return inventory; } // (1, 0)
        if (x == 0 && y == 2) { return attack; } // (0, 2)
        if (x == 0 && y == -2) { return done; } // (0, -2)
        if (x == -1 && y == 0) { return aid; } // (-1, 0)
        if (x == -1 && y == -1) { return ability; } // (-1, 1)
        if (x == -1 && y == 1) { return skill; } // (-1, 1)
        if (x == 1 && y == -1) { return formation; } // (1, -1)
        if (x == 1 && y == 1) { return trade; } // (1, 1)
        if (x == 0 && y == 1) { return talk; } // (0, 1)
        if (x == 0 && y == -1) { // (0, -1)
            if (annex.isShown) { return annex; }
        }
        return null;
    }
    
    public MenuOption getSelectedOption() {
        return getOptionByCoordinates(currentX, currentY);
    }
    
    public void updateState(int univIndex) {
        int frame = (int)(((univIndex) + 103) % 103);
        int frame2 = (int)(((univIndex * 0.1) + 5) % 5);
        
        ((QuadBackgroundComponent)geoBackdrop.getBackground()).setTexture(bd[(int)frame]);
        eye.updateState(frame2);
        inventory.updateState(frame2);
        attack.updateState(frame2);
        done.updateState(frame2);
        aid.updateState(frame2);
        ability.updateState(frame2);
        skill.updateState(frame2);
        formation.updateState(frame2);
        trade.updateState(frame2);
        talk.updateState(frame2);
        annex.updateState(frame2);
        escape.updateState(frame2);
        
        if (windowChanger != null) {
            windowChanger.updateTransitions();
        }
    }
    
    //do the regular move stuff but replace the previous selected material with the deselected material
    public void moveY(int direction) { //1 for up, -1 for down
        //first make the current point unselected
        getOptionByCoordinates(currentX, currentY).isSelected = false;
        
        if (Math.abs(currentY + direction) > getYThreshold()) {
            currentY *= -1;
        } else if (currentX == 0 && ((talk.isShown && direction == 1) || ((escape.isShown || annex.isShown) && direction == -1))) {
            currentY += direction;
        } else {
            currentY += direction * getYThreshold();
        }
        
        if (!eye.isAvailable && currentX == 0 && currentY == 0) {
            currentY += direction;
        }
        
        getOptionByCoordinates(currentX, currentY).isSelected = true;
    }
    
    public void moveX(int direction) { //1 for right, -1 for left
        //first make the current point unselected
        getOptionByCoordinates(currentX, currentY).isSelected = false;
        
        if (Math.abs(currentX + direction) > 1) { //1 is the X threshold
            currentX *= -1;
        } else if ((currentX != 0 && currentY != 0 && Math.abs(currentX) == Math.abs(currentY))) {
            if ((currentY == 1 && talk.isShown) || (currentY == -1 && (escape.isShown || annex.isShown))) {
                currentX += direction;
            } else {
                currentY -= direction * (currentX * currentY);
                currentX += direction;
            }
        } else if (currentX == 0 && currentY == -2) {
            currentY += 1;
            currentX += direction;
        } else {
            if (currentY == getYThreshold()) {
                currentX += direction;
                currentY = getYThreshold();
            } else { currentX += direction; }
        }
        
        if (!eye.isAvailable && currentX == 0 && currentY == 0) {
            currentX += direction;
        }
        
        getOptionByCoordinates(currentX, currentY).isSelected = true;
    }
    
    private int getYThreshold() { return (int)(2 + (-1 * Math.pow(currentX, 2))); }
    
    public Node getNode() { return menuNode; }
    
    public MasterFsmState resolveInput(String name, float tpf) {
        Conveyer conv = ((MenuState)fsm.getState()).getConveyer();
        MasterFsmState returnable = null;
        
        if (getOptionByCoordinates(currentX, currentY).submenu != null && !MenuOption.ogMenuActive) {
            getOptionByCoordinates(currentX, currentY).submenu.catchInput(name, tpf, ((MenuState)fsm.getState()).getConveyer());
            return null;
        }
        
        if (name.equals("move up")) {
            moveY(1);
        }
        if (name.equals("move down")) {
            moveY(-1);
        }
        if (name.equals("move left")) {
            moveX(-1);
        }
        if (name.equals("move right")) {
            moveX(1);
        }
        if (name.equals("select") && getSelectedOption().isAvailable) {
            switch (getSelectedOption().name) {
                case "attack":
                    conv.getUnit().setStateIfAllowed(new FsmState(EntityState.SelectingTarget));
                    conv.getUnit().equip(((Weapon)conv.getUnit().getInventory().getItems().get(0)));
                    conv.getUnit().setToUseSkill(null);
                    fsm.setNewStateIfAllowed(new MenuState(EntityState.GuiClosed));
                    conv.getCursor().setStateIfAllowed(new FsmState(EntityState.AnyoneSelectingTarget));
                    conv.getCursor().setPurpose(Purpose.WeaponAttack);
                    conv.getUnit().incrementOption(Purpose.WeaponAttack);
                    getParent().detachChild(this);
                    break;
                case "done":
                    conv.getCursor().resetState(conv.getMap());
                    returnable = new MasterFsmState();
                    fsm.setNewStateIfAllowed(new MenuState(EntityState.GuiClosed));
                    getParent().detachChild(this);
                    break;
                default:
                    fadeAll();
                    break;
            }
            
        }
        if (name.equals("deselect")) {
            returnable = new MasterFsmState();
            conv.getCursor().goBackFromMenu(conv.getAssetManager());
            fsm.setNewStateIfAllowed(new MenuState(EntityState.GuiClosed));
            getParent().detachChild(this);
        }
        
        return returnable;
    }
    
    private void fadeAll() {
        for (int x = -1; x <= 1; x++) { //with it
            for (int y = -2; y <= 2; y++) {
                if (x != currentX || y != currentY) {
                    try {
                        getOptionByCoordinates(x, y).beginDisappearance();
                    }
                    catch (NullPointerException e) {}
                }
            }
        }
        getSelectedOption().selectOption();
    }
    
    public void setPos(Coords pos) {
        getOptionByCoordinates(currentX, currentY).deselect();
        currentX = pos.getX();
        currentY = pos.getY();
        getOptionByCoordinates(currentX, currentY).isSelected = true;
        getOptionByCoordinates(currentX, currentY).updateState(0);
    }
    
    private int frameCount = 0;
    
    public void update(float tpf) {
        if (frameCount > 1000) { frameCount = 0; }

        updateAI(tpf);
        
        frameCount++;
    }
    
    public void updateAI(float tpf) {
        
        if (fsm.getState().getEnum() == EntityState.PostActionMenuOpened) {
            updateState(frameCount);
        } else if (fsm.getState().getEnum() == EntityState.GuiClosed) {
            
        }
        
    }
    
    public FsmState getState() { return fsm.getState(); }
    
    public void setStateIfAllowed(MenuState newState) {
        fsm.setNewStateIfAllowed(newState);
    }
    
    public void forceState(FsmState newState) {
        fsm.forceState(newState);
    }
    
    public static Submenu getInventoryContents(final Node menu, SubmenuOption parent_option, Conveyer data) {
        return new Submenu() {
            TangibleUnit tu;
            Map currentmap;
            AssetManager am;
            
            Container infoContainer, textContainer;
            EditedTextField infoText;
            Node textNode;
            
            @Override
            public void retrieveData() {
                tu = data.getUnit();
                currentmap = data.getMap();
                am = data.getAssetManager();
            }

            @Override
            public List<SubmenuOption> options() {
                List<SubmenuOption> contents = new ArrayList<>();
                for (Container contained : StatScreen.getItems(tu, am)) {
                    contents.add(new SubmenuOption(85f, 10f) {
                        TangibleUnit unit;
                        Map map;
                        
                        @Override
                        public void interpretData() {
                            unit = info.getUnit();
                            map = info.getMap();
                        }

                        @Override
                        public void select() {
                            //TODO
                            
                        }

                        @Override
                        public void initialize() {
                            //TODO
                            this.setBackground(new QuadBackgroundComponent(am.loadTexture("Interface/GUI/postmovemenu/cursorframes/0.png")));
                            ((QuadBackgroundComponent)this.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
                            //this.setInsets(new Insets3f(10f, 0, 10f, 0));
                            //this.move(0, 30, 0);
                        }
                    }.obtainChild(contained)); 
                    
                }
                return contents;
            }

            @Override
            public void resolveInput(String name, float tpf) {
                if (active && state == TransitionState.Standby) {
                    switch (name) {
                        case "move up":
                            moveUp();
                            break;
                        case "move down":
                            moveDown();
                            break;
                        case "select":
                            /*menuOptions.get(currentIndex).select();
                            if (menuOptions.get(currentIndex).hasSubmenu()) {
                                transitionOutForward();
                            }*/
                            break;
                        case "deselect":
                            windowChanger.beginTransitions(Arrays.asList(VisualTransition.ZoomOut().setLength(1f / 6f).setStartingIndexScale(2.2f).setEndVal(1f / 2.2f)));
                            transitionOut(TransitionType.Backward);
                            getParentOption().getParentSubmenu().lightInitialize();
                            getParentOption().getParentSubmenu().move(-85, 60, 0);
                            getParentOption().getParentSubmenu().transitionIn(TransitionType.Backward);
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void initialize() {
                autoSelectOnOneOption = false;
                menu.attachChild(this);
                this.setLocalTranslation(25, 30, 0);
                ((TbtQuadBackgroundComponent)this.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
                
                textNode = new Node();
                
                infoContainer = new Container();
                ((TbtQuadBackgroundComponent)infoContainer.getBackground()).setColor(ColorRGBA.White);
                
                textContainer = new Container();
                textContainer.setBackground(new QuadBackgroundComponent(am.loadTexture("Interface/GUI/general_ui/default.png")));
                ((QuadBackgroundComponent)textContainer.getBackground()).setColor(new ColorRGBA(1, 1, 1, 1));
                
                String dsc = tu.getInventory().getItems().get(currentIndex).getDescription();
                if (tu.getInventory().getItems().get(currentIndex) instanceof Weapon) {
                    dsc = ((Weapon)tu.getInventory().getItems().get(currentIndex)).getStatDescription();
                }
                infoText = new EditedTextField(dsc);
                infoText.setSingleLine(false);
                textContainer.setPreferredSize(new Vector3f(70, 100, textContainer.getPreferredSize().z));
                infoText.setPreferredWidth(65f);
                infoText.setFontSize(8f);
                infoText.setTextHAlignment(HAlignment.Center);
                infoText.setColor(ColorRGBA.White);
                infoText.getTextEntryComponent().getTextComponent().setLineWrapMode(LineWrapMode.Word);
                
                textContainer.addChild(infoText);
                infoContainer.addChild(textContainer);
                textNode.attachChild(infoContainer);
                
                this.attachChild(textNode);
                textNode.move(-195, 10, 1);
            }
            
            int count = 0;
            int getIndex() {
                float c = count % 14;
                return (int)(0.35f * ((-1 * FastMath.abs(c - 7)) + 7));
            }

            @Override
            public void update() {
                //do the cosmetic updates here
                if (count == Integer.MAX_VALUE) { count = 0; }
                            
                if (active) {
                    ((QuadBackgroundComponent)menuOptions.get(currentIndex).getBackground())
                            .setTexture(am.loadTexture("Interface/GUI/postmovemenu/cursorframes/" + getIndex() + ".png"));
                } else {}
                
                String dsc = tu.getInventory().getItems().get(currentIndex).getDescription();
                if (tu.getInventory().getItems().get(currentIndex) instanceof Weapon) {
                    dsc = ((Weapon)tu.getInventory().getItems().get(currentIndex)).getStatDescription();
                }
                infoText.setText(dsc);
             
                count++;
            }

            @Override
            public void reset() {
                this.detachChild(textNode);
                menu.detachChild(this);
            }
        }.setParentOption(parent_option);
    }
    
    //----------------------------------------------------------------
    
    public Submenu getFormulaContents(final Node menu, Conveyer data) {
        return new Submenu() {
            TangibleUnit tu;
            Map currentmap;
            AssetManager am;
            
            Container infoContainer, textContainer;
            EditedTextField infoText;
            Node textNode;
            
            @Override
            public void retrieveData() {
                tu = data.getUnit();
                currentmap = data.getMap();
                am = data.getAssetManager();
            }

            @Override
            public List<SubmenuOption> options() {
                List<SubmenuOption> contents = new ArrayList<>();
                List<Container> obtainable = StatScreen.getFormulas(tu, am);
                for (int c = 0; c < obtainable.size(); c++) {
                    contents.add(new SubmenuOption(85f, 10f, c) {
                        TangibleUnit unit;
                        Map map;
                        
                        @Override
                        public void interpretData() {
                            unit = info.getUnit();
                            map = info.getMap();
                        }

                        @Override
                        public void select() {
                            //TODO
                            info.getUnit().setStateIfAllowed(new FsmState(EntityState.SelectingTarget));
                            fsm.setNewStateIfAllowed(new MenuState(EntityState.GuiClosed));
                            info.getCursor().setStateIfAllowed(new FsmState(EntityState.AnyoneSelectingTarget));
                            Purpose purpose = info.getUnit().getFormulas().get(index).getFormulaPurpose() == ToolType.Attack ? Purpose.EtherAttack : Purpose.EtherSupport;
                            info.getCursor().setPurpose(purpose);
                            info.getUnit().incrementOption(purpose);
                            info.getUnit().equip(info.getUnit().getFormulas().get(index));
                            ActionMenu.this.getParent().detachChild(ActionMenu.this);
                        }

                        @Override
                        public void initialize() {
                            //TODO
                            this.setBackground(new QuadBackgroundComponent(am.loadTexture("Interface/GUI/postmovemenu/cursorframes/0.png")));
                            ((QuadBackgroundComponent)this.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
                            //this.setInsets(new Insets3f(10f, 0, 10f, 0));
                            //this.move(0, 30, 0);
                        }
                    }.obtainChild(obtainable.get(c))); 
                }
                return contents;
            }

            @Override
            public void resolveInput(String name, float tpf) {
                if (active && state == TransitionState.Standby) {
                    switch (name) {
                        case "move up":
                            moveUp();
                            break;
                        case "move down":
                            moveDown();
                            break;
                        case "select":
                            menuOptions.get(currentIndex).select();
                            windowChanger.beginTransitions(Arrays.asList(VisualTransition.ZoomOut().setLength(1f / 12f).setStartingIndexScale(2.2f).setEndVal(1f / 2.2f)));
                            transitionOut(TransitionType.Backward);
                            reset();
                            for (int x = -1; x <= 1; x++) {
                                for (int y = -2; y <= 2; y++) {
                                    try {
                                            getOptionByCoordinates(x, y).beginReappearance();
                                        }
                                    catch (NullPointerException e) {}
                                }
                            }
                            MenuOption.ogMenuActive = true;
                            break;
                        case "deselect":
                            windowChanger.beginTransitions(Arrays.asList(VisualTransition.ZoomOut().setLength(1f / 6f).setStartingIndexScale(2.2f).setEndVal(1f / 2.2f)));
                            transitionOut(TransitionType.Backward);
                            for (int x = -1; x <= 1; x++) {
                                for (int y = -2; y <= 2; y++) {
                                    try {
                                            getOptionByCoordinates(x, y).beginReappearance();
                                        }
                                    catch (NullPointerException e) {}
                                }
                            }
                            MenuOption.ogMenuActive = true;
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void initialize() {
                autoSelectOnOneOption = false;
                menu.attachChild(this);
                this.setLocalTranslation(25, 30, 0);
                ((TbtQuadBackgroundComponent)this.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
                
                textNode = new Node();
                
                infoContainer = new Container();
                ((TbtQuadBackgroundComponent)infoContainer.getBackground()).setColor(ColorRGBA.White);
                
                textContainer = new Container();
                textContainer.setBackground(new QuadBackgroundComponent(am.loadTexture("Interface/GUI/general_ui/default.png")));
                ((QuadBackgroundComponent)textContainer.getBackground()).setColor(new ColorRGBA(1, 1, 1, 1));
                
                infoText = new EditedTextField(tu.getFormulas().get(currentIndex).getStatDescription());
                infoText.setSingleLine(false);
                textContainer.setPreferredSize(new Vector3f(70, 100, textContainer.getPreferredSize().z));
                infoText.setPreferredWidth(65f);
                infoText.setFontSize(8f);
                infoText.setTextHAlignment(HAlignment.Center);
                infoText.setColor(ColorRGBA.White);
                infoText.getTextEntryComponent().getTextComponent().setLineWrapMode(LineWrapMode.Word);
                
                textContainer.addChild(infoText);
                infoContainer.addChild(textContainer);
                textNode.attachChild(infoContainer);
                
                this.attachChild(textNode);
                textNode.move(-195, 15, 1);
            }
            
            int count = 0;
            int getIndex() {
                float c = count % 14;
                return (int)(0.35f * ((-1 * FastMath.abs(c - 7)) + 7));
            }

            @Override
            public void update() {
                //do the cosmetic updates here
                if (count == Integer.MAX_VALUE) { count = 0; }
                            
                if (active) {
                    ((QuadBackgroundComponent)menuOptions.get(currentIndex).getBackground())
                            .setTexture(am.loadTexture("Interface/GUI/postmovemenu/cursorframes/" + getIndex() + ".png"));
                } else {}
                
                infoText.setText(tu.getFormulas().get(currentIndex).getStatDescription());
                                     
                count++;
            }

            @Override
            public void reset() {
                this.detachChild(textNode);
                menu.detachChild(this);
            }
        };
    }
    
    //-------------------------------------------------------------------------
    
    public Submenu getSkillContents(final Node menu, Conveyer data) {
        return new Submenu() {
            TangibleUnit tu;
            Map currentmap;
            AssetManager am;
            
            Container infoContainer, textContainer;
            EditedTextField infoText;
            Node textNode;
            
            @Override
            public void retrieveData() {
                tu = data.getUnit();
                currentmap = data.getMap();
                am = data.getAssetManager();
            }

            @Override
            public List<SubmenuOption> options() {
                List<SubmenuOption> contents = new ArrayList<>();
                List<Container> obtainable = StatScreen.getSkills(tu, am);
                for (int c = 0; c < obtainable.size(); c++) {
                    contents.add(new SubmenuOption(85f, 10f, c) {
                        TangibleUnit unit;
                        Map map;
                        
                        @Override
                        public void interpretData() {
                            unit = info.getUnit();
                            map = info.getMap();
                        }

                        @Override
                        public void select() {
                            //TODO
                            info.getUnit().setStateIfAllowed(new FsmState(EntityState.SelectingTarget));
                            fsm.setNewStateIfAllowed(new MenuState(EntityState.GuiClosed));
                            info.getCursor().setStateIfAllowed(new FsmState(EntityState.AnyoneSelectingTarget));
                            info.getCursor().setPurpose(Purpose.SkillAttack);
                            info.getUnit().incrementOption(Purpose.SkillAttack);
                            info.getUnit().setToUseSkill(info.getUnit().getSkills().get(index));
                            info.getUnit().equip((Weapon)info.getUnit().getInventory().getItems().get(0));
                            //info.getUnit().setToUseFormula(null);
                            
                            ActionMenu.this.getParent().detachChild(ActionMenu.this);
                        }

                        @Override
                        public void initialize() {
                            //TODO
                            this.setBackground(new QuadBackgroundComponent(am.loadTexture("Interface/GUI/postmovemenu/cursorframes/0.png")));
                            ((QuadBackgroundComponent)this.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
                            //this.setInsets(new Insets3f(10f, 0, 10f, 0));
                            //this.move(0, 30, 0);
                        }
                    }.obtainChild(obtainable.get(c))); 
                }
                return contents;
            }

            @Override
            public void resolveInput(String name, float tpf) {
                if (active && state == TransitionState.Standby) {
                    switch (name) {
                        case "move up":
                            moveUp();
                            break;
                        case "move down":
                            moveDown();
                            break;
                        case "select":
                            menuOptions.get(currentIndex).select();
                            windowChanger.beginTransitions(Arrays.asList(VisualTransition.ZoomOut().setLength(1f / 12f).setStartingIndexScale(2.2f).setEndVal(1f / 2.2f)));
                            transitionOut(TransitionType.Backward);
                            reset();
                            for (int x = -1; x <= 1; x++) {
                                for (int y = -2; y <= 2; y++) {
                                    try {
                                            getOptionByCoordinates(x, y).beginReappearance();
                                        }
                                    catch (NullPointerException e) {}
                                }
                            }
                            MenuOption.ogMenuActive = true;
                            break;
                        case "deselect":
                            windowChanger.beginTransitions(Arrays.asList(VisualTransition.ZoomOut().setLength(1f / 6f).setStartingIndexScale(2.2f).setEndVal(1f / 2.2f)));
                            transitionOut(TransitionType.Backward);
                            for (int x = -1; x <= 1; x++) {
                                for (int y = -2; y <= 2; y++) {
                                    try {
                                            getOptionByCoordinates(x, y).beginReappearance();
                                        }
                                    catch (NullPointerException e) {}
                                }
                            }
                            MenuOption.ogMenuActive = true;
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void initialize() {
                autoSelectOnOneOption = false;
                menu.attachChild(this);
                this.setLocalTranslation(25, 30, 0);
                ((TbtQuadBackgroundComponent)this.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
                
                textNode = new Node();
                
                infoContainer = new Container();
                ((TbtQuadBackgroundComponent)infoContainer.getBackground()).setColor(ColorRGBA.White);
                
                textContainer = new Container();
                textContainer.setBackground(new QuadBackgroundComponent(am.loadTexture("Interface/GUI/general_ui/default.png")));
                ((QuadBackgroundComponent)textContainer.getBackground()).setColor(new ColorRGBA(1, 1, 1, 1));
                
                infoText = new EditedTextField(tu.getSkills().get(currentIndex).getEffect().effectDescription());
                infoText.setSingleLine(false);
                textContainer.setPreferredSize(new Vector3f(70, 100, textContainer.getPreferredSize().z));
                infoText.setPreferredWidth(65f);
                infoText.setFontSize(8f);
                infoText.setTextHAlignment(HAlignment.Center);
                infoText.setColor(ColorRGBA.White);
                infoText.getTextEntryComponent().getTextComponent().setLineWrapMode(LineWrapMode.Word);
                
                textContainer.addChild(infoText);
                infoContainer.addChild(textContainer);
                textNode.attachChild(infoContainer);
                
                this.attachChild(textNode);
                textNode.move(-195, 15, 1);
            }
            
            int count = 0;
            int getIndex() {
                float c = count % 14;
                return (int)(0.35f * ((-1 * FastMath.abs(c - 7)) + 7));
            }

            @Override
            public void update() {
                //do the cosmetic updates here
                if (count == Integer.MAX_VALUE) { count = 0; }
                            
                if (active) {
                    ((QuadBackgroundComponent)menuOptions.get(currentIndex).getBackground())
                            .setTexture(am.loadTexture("Interface/GUI/postmovemenu/cursorframes/" + getIndex() + ".png"));
                } else {}
                
                infoText.setText(tu.getSkills().get(currentIndex).getEffect().effectDescription());
                                     
                count++;
            }

            @Override
            public void reset() {
                this.detachChild(textNode);
                menu.detachChild(this);
            }
        };
    }
    
}
