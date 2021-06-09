/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.ui;

import fundamental.stats.BaseStat;
import fundamental.item.ConsumableItem;
import fundamental.item.weapon.Weapon;
import com.atr.jme.font.TrueTypeBMP;
import com.atr.jme.font.TrueTypeFont;
import com.atr.jme.font.asset.TrueTypeKeyBMP;
import com.atr.jme.font.shape.TrueTypeNode;
import com.atr.jme.font.util.StringContainer;
import com.atr.jme.font.util.Style;
import com.jme3.asset.AssetManager;
import com.jme3.font.LineWrapMode;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.TabbedPanel;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.component.TbtQuadBackgroundComponent;
import edited.CustomProgressBar;
import edited.EditedLabel;
import edited.EditedTextField;
import java.util.ArrayList;
import java.util.List;
import etherealtempest.FSM;
import etherealtempest.FSM.MapFlowState;
import etherealtempest.FsmState;
import etherealtempest.Main;
import etherealtempest.info.Conveyor;
import fundamental.formation.Formation;
import fundamental.formation.FormationTechnique;
import fundamental.stats.StatBundle;
import etherealtempest.gui.RadialProgressBar;
import fundamental.stats.Bonus.BonusType;
import general.utils.EngineUtils;
import general.utils.EngineUtils.CenterAxis;
import java.util.Arrays;
import maps.layout.occupant.character.TangibleUnit;
import fundamental.talent.TalentCondition.Occasion;
import fundamental.talent.TalentManager;
import fundamental.talent.Talent;

/**
 *
 * @author night
 */
public class StatScreen extends Node {
    private AssetManager assetManager;
    private RadialProgressBar[] statProgress = new RadialProgressBar[9];
    private RadialProgressBar expbar;
    private Container menu, specialNode, info;
    
    private final Quad 
            special = new Quad(130f, 130f), 
            transitionAnimation = new Quad(254f, 335.5f), 
            leaves = new Quad(300f, 300f);
    private Geometry 
            specialGeo = new Geometry("special", special), 
            transitionAnim = new Geometry("transitionAnimation", transitionAnimation), 
            portraitFrame = new Geometry("leaves", leaves);
    private TrueTypeNode strikesTillParry;
    
    private Texture cursorColor;
    private Material sword;
    private ColorRGBA defaultBGColor;
    
    private EditedTextField stuff;
    
    private ArrayList<ArrayList<Cosa>> namedElements;
    private ArrayList<Cosa> switchedElements;
    
    private int currentX = 0, currentY = 0, switchedX = 0, switchedY = 0; //switchedX represents the block, switchedY represents the item #
    
    private boolean tabsSwitched = false;
    private TabbedPanel itemsOrSkills, formulasOrAbilities;
    
    private final FSM<MapFlowState> fsm = new FSM<MapFlowState>() {
        @Override
        public void setNewStateIfAllowed(FsmState<MapFlowState> st) {
            state = st; //change later maybe
        }
    
    };
    
    private enum TransitionState {
        Transition,
        TransitionBack,
        Standby,
        FinishedForward,
        Off
    }
    
    private TransitionState currentTransitionState = TransitionState.Off;
    
    public StatScreen(AssetManager AM) {
        assetManager = AM;
        fsm.setNewStateIfAllowed(new FsmState(MapFlowState.GuiClosed));
    }
    
    public FsmState getState() {
        return fsm.getState();
    }
    
    public void setStateIfAllowed(MapFlowState newState) {
        fsm.setNewStateIfAllowed(newState);
    }
    
    public void forceState(MapFlowState newState) {
        fsm.forceState(newState);
    }
    
    public void resolveInput(String name, float tpf) {
        if (name.equals("deselect")) {
            if (fsm.getState().getEnum() == MapFlowState.StatScreenOpened) {
                expbar.getChildrenNode().detachAllChildren();
                expbar.removeFromParent();
                
                for (RadialProgressBar RPB : statProgress) {
                    RPB.setLocalTranslation(0, 0, 0);
                    RPB.getChildrenNode().detachAllChildren();
                    RPB.removeFromParent();
                }
                
                detachChild(menu);
                fsm.setNewStateIfAllowed(new FsmState(MapFlowState.GuiClosed));
            } else if (fsm.getState().getEnum() == MapFlowState.StatScreenSelecting) {
                //do the closing animation of side tab (reverse of open)
                currentTransitionState = TransitionState.TransitionBack;
                
                tryToggling(false);
                //fsm.setNewStateIfAllowed(new FsmState(EntityState.StatScreenOpened));
                info.clearChildren();
                
            }
        } 
        if (name.equals("C")) {
            if (fsm.getState().getEnum() != MapFlowState.StatScreenOpened && fsm.getState().getEnum() != MapFlowState.StatScreenSelecting) {
                fsm.setNewStateIfAllowed(new FsmState(MapFlowState.StatScreenOpened));
            }
        }
        if (name.equals("select") && fsm.getState().getEnum() != MapFlowState.StatScreenSelecting) {
            //do the opening animation of side tab
            currentTransitionState = TransitionState.Transition;
            
            resetPos();
            fsm.setNewStateIfAllowed(MapFlowState.StatScreenSelecting);

            stuff.setText(namedElements.get(currentX).get(currentY).getDescription());
            
            info.addChild(stuff);
            
            moveY(0);
        }
        if (name.equals("move up") && fsm.getState().getEnum() == MapFlowState.StatScreenSelecting) {
            moveY(-1);
            //moveY(0);
            stuff.setText(namedElements.get(currentX).get(currentY).getDescription());
        }
        if (name.equals("move down") && fsm.getState().getEnum() == MapFlowState.StatScreenSelecting) {
            moveY(1);
            //moveY(0);
            stuff.setText(namedElements.get(currentX).get(currentY).getDescription());
        }
        if (name.equals("move left") && fsm.getState().getEnum() == MapFlowState.StatScreenSelecting) {
            moveX(-1);
            //moveY(0);
            stuff.setText(namedElements.get(currentX).get(currentY).getDescription());
        }
        if (name.equals("move right") && fsm.getState().getEnum() == MapFlowState.StatScreenSelecting) {
            moveX(1);
            //moveY(0);
            stuff.setText(namedElements.get(currentX).get(currentY).getDescription());
        }
        if ((name.equals("bump left") || name.equals("bump right")) && (fsm.getState().getEnum() == MapFlowState.StatScreenOpened || fsm.getState().getEnum() == MapFlowState.StatScreenSelecting)) {
            tryToggling(false);
            
            //do the tab switch
            ((TbtQuadBackgroundComponent)itemsOrSkills.getSelectedTab().getTitleButton().getBorder()).setColor(defaultBGColor);
            ((TbtQuadBackgroundComponent)formulasOrAbilities.getSelectedTab().getTitleButton().getBorder()).setColor(defaultBGColor);
            
            if (itemsOrSkills.getTabs().get(0) != itemsOrSkills.getSelectedTab()) {
                itemsOrSkills.setSelectedTab(itemsOrSkills.getTabs().get(0));
                tabsSwitched = false;
            } else {
                itemsOrSkills.setSelectedTab(itemsOrSkills.getTabs().get(1));
                tabsSwitched = true;
                switchedX = 0;
                switchedY = 0;
            }
            
            if (formulasOrAbilities.getTabs().get(0) != formulasOrAbilities.getSelectedTab()) {
                formulasOrAbilities.setSelectedTab(formulasOrAbilities.getTabs().get(0));
            } else { formulasOrAbilities.setSelectedTab(formulasOrAbilities.getTabs().get(1)); }
            
            ((TbtQuadBackgroundComponent)itemsOrSkills.getSelectedTab().getTitleButton().getBorder()).setColor(new ColorRGBA(0, 0.839f, 0.871f, 1));
            ((TbtQuadBackgroundComponent)formulasOrAbilities.getSelectedTab().getTitleButton().getBorder()).setColor(new ColorRGBA(0, 0.839f, 0.871f, 1));
            
            ArrayList<Cosa> temp = (ArrayList<Cosa>)namedElements.get(1).clone();
            namedElements.set(1, (ArrayList<Cosa>)switchedElements.clone());
            switchedElements = temp;
            
            if (fsm.getState().getEnum() == MapFlowState.StatScreenSelecting) {
                moveY(0);
                stuff.setText(namedElements.get(currentX).get(currentY).getDescription());
            }
        }
        System.out.println("(" + currentX + ", " + currentY + ")");
    }
    
    private int frameTrans = 0;
    
    public void update(float tpf) {
        //System.out.println(currentTransitionState);
        //System.out.println("switchedX = " + switchedX + ", switchedY = " + switchedY);
        //System.out.println("currentX = " + currentX + ", currentY = " + currentY);
        
        if (currentTransitionState == TransitionState.TransitionBack) {
            if (frameTrans > 0) {
                sword.setTexture("ColorMap", assetManager.loadTexture("Interface/GUI/stat_screen/transition/" + frameTrans + ".png"));
                frameTrans -= 3;
            } else {
                frameTrans = 0;
                fsm.setNewStateIfAllowed(new FsmState(MapFlowState.StatScreenOpened));
                currentTransitionState = TransitionState.Standby;
            }
        }
        
        switch (fsm.getState().getEnum()) {
            case StatScreenOpened: 
            {
                specialNode.rotate(0, 0, (FastMath.PI / -240f));
                sword.setTexture("ColorMap", assetManager.loadTexture("Interface/GUI/stat_screen/transition/default.png"));
                break;
            }    
            case GuiClosed: 
            {
                break;
            }
            case StatScreenSelecting: 
            {
                specialNode.rotate(0, 0, (FastMath.PI / -240f));
                
                if (frameTrans >= 12) {
                    currentTransitionState = TransitionState.FinishedForward;
                    frameTrans = 11;
                }
                
                switch (currentTransitionState) {
                    case Transition:
                        sword.setTexture("ColorMap", assetManager.loadTexture("Interface/GUI/stat_screen/transition/" + frameTrans + ".png"));
                        frameTrans += 3;
                        break;
                    case Standby:
                        sword.setTexture("ColorMap", assetManager.loadTexture("Interface/GUI/stat_screen/transition/default.png"));
                        break;
                    case FinishedForward:
                        sword.setTexture("ColorMap", assetManager.loadTexture("Interface/GUI/stat_screen/transition/done.png"));
                        break;
                    default:
                        break;
            }
                
                break;
            }
            default:
                break;
        }
    }
    
    private void initializeSizes() {
        currentX = 0;
        currentY = 0;
        
        for (int c = 0; c < namedElements.size(); c++) {
            for (int r = 0; r < namedElements.get(c).size(); r++) {
                if (c != 1 || r > 1) { //not hp or tp bars
                    namedElements.get(c).get(r).getContainer().setBackground(new QuadBackgroundComponent(cursorColor));
                    ((QuadBackgroundComponent)namedElements.get(c).get(r).getContainer().getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
                }
            }
        }
        
        for (int r = 0; r < switchedElements.size(); r++) {
            if (r > 1) { //not hp or tp bars
                switchedElements.get(r).getContainer().setBackground(new QuadBackgroundComponent(cursorColor));
                ((QuadBackgroundComponent)switchedElements.get(r).getContainer().getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
            }
        }
        
        resetPos();
    }
    
    private int translationYfromX(int direction) {
        if (currentX == 1 && currentY > 1) {
            if (direction > 0) {
                return 1;
            } else if (direction < 0) {
                if (currentY <= 3) {
                    if (
                            (currentY == 2 && namedElements.get(1).get(2).getBlock().equals("i0")) //item 0 (inventory)
                            || (currentY == 3 && namedElements.get(1).get(3).getBlock().equals("i1")) //item 1 (inventory)
                       ) 
                    {
                        return 0;
                    }
                }
                if (namedElements.get(1).get(currentY).getBlock().startsWith("f")) {
                    return 2;
                }
                if (namedElements.get(1).get(currentY).getBlock().startsWith("i")) {
                    return 1;
                }
            }
        } 
        if (currentX == 1 && currentY == 1 && direction > 0) { 
            return 0;
        } 
        if (currentX == 0) {
            if (direction > 0) {
                return 2;
            } 
            
            if (direction < 0) {
                if (currentY == namedElements.get(0).size() - 1) {
                    return 5;
                }

                return 0;
            }
        }
        if (currentX == 4) {
            if (direction > 0) {
                if (currentY == 5) {
                    return 5;
                }
                
                return 0;
            }
            if (direction < 0) {
                if (currentY > 2) {
                    currentX = 3;
                    return 1;
                }
            }
        }
        if (currentX == 2) {
            if (direction < 0) {
                if (currentY == 1) {
                    return 2;
                }
            }
        }
        
        return currentY;
    }
    
    public void resetPos() {
        if ((currentX != 1 || currentY <= 1) || !tabsSwitched) {
            tryToggling(false);
        
            currentX = 1;
            currentY = 2;
        
            tryToggling(true);
        }
    }
    
    private void moveX(int amt) {
        tryToggling(false);
        
        currentY = translationYfromX(amt);

        if (currentX == 2 && currentY == 1 && amt == 1) {
            currentX += 2;
            currentY = 0;
        } else { currentX += amt; }
        
        if (currentX < 0) {
            currentX = namedElements.size() - 1;
        } else if (currentX >= namedElements.size()) {
            currentX = 0;
        }

        tryToggling(true);
    }
    
    private void moveY(int amt) {
        tryToggling(false);
        
        currentY += amt;
        
        if (currentY < 0) {
            currentY = namedElements.get(currentX).size() + amt;
        } else if (currentY >= namedElements.get(currentX).size()) {
            currentY = 0;
        }
        
        tryToggling(true);
    }
    
    private Cosa currentElement() {
        return namedElements.get(currentX).get(currentY);
    }
    
    public void toggleCursorAtCurrentPos(boolean using) {
        if (using) {
            if (currentX != 1 || currentY > 1) { //not hp or tp bars
                ((QuadBackgroundComponent)currentElement().getContainer().getBackground()).setColor(new ColorRGBA(1, 1, 1, 1));
            } else { //hp or tp bar
                ((TbtQuadBackgroundComponent)currentElement().getContainer().getBackground()).setColor(new ColorRGBA(1, 1, 1, 1));
            }
        } else {
            if (currentX != 1 || currentY > 1) { //not hp or tp bars
                try {
                    ((QuadBackgroundComponent)currentElement().getContainer().getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
                }
                catch (Exception e) {
                    ((TbtQuadBackgroundComponent)currentElement().getContainer().getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
                }
            } else {
                //it's the hp or tp bar
                ((TbtQuadBackgroundComponent)currentElement().getContainer().getBackground()).setColor(ColorRGBA.Black);
            }
        }
        
    }
    
    private void tryToggling(boolean using) {
        try {
            toggleCursorAtCurrentPos(using);
        }
        catch(ArrayIndexOutOfBoundsException e) {
            currentY -= 1;
        
            if (currentY < 0) {
                currentY = namedElements.get(currentX).size() - 1;
            } else if (currentY >= namedElements.get(currentX).size()) {
                currentY = 0;
            }
        
            tryToggling(true);
        }
        catch(IndexOutOfBoundsException e2) {
            currentY -= 1;
            
            if (currentY < 0) {
                currentY = namedElements.get(currentX).size() - 1;
            } else if (currentY >= namedElements.get(currentX).size()) {
                currentY = 0;
            }
        
            tryToggling(true);
        }
    }
    
    public void initializeRenders() {
        //set this texture
        CustomProgressBar.setWhiteSquare(assetManager.loadTexture("Textures/gui/whitesquare.png"));
        cursorColor = assetManager.loadTexture("Interface/GUI/stat_screen/hovered.png");
        
        //exp circle
        expbar = new RadialProgressBar(52.5f, 63.75f, new ColorRGBA(0.012f, 0.58f, 0.988f, 1f), 2, assetManager);
        expbar.move(90f, -125f, 0);
        
        //stat circles
        for (int i = 0; i < statProgress.length; i++) {
            TrueTypeKeyBMP bmp = new TrueTypeKeyBMP("Interface/Fonts/Montaga-Regular.ttf", Style.Plain, (35 + i));
            TrueTypeFont ttf = (TrueTypeBMP)assetManager.loadAsset(bmp);
            ttf.setScale(28f / (35f + i));
            
            statProgress[i] = new RadialProgressBar(20.7f, 24f, new ColorRGBA(0.98f, 0.557f, 0f, 1f), 1, assetManager);
        }

        //START PARRY CIRCLE
        TrueTypeKeyBMP amountLeft = new TrueTypeKeyBMP("Interface/Fonts/Cinzel/Cinzel-Bold.ttf", Style.Plain, 50);
        TrueTypeFont parryFont = (TrueTypeBMP)assetManager.loadAsset(amountLeft);
        strikesTillParry = parryFont.getText("3", 3, ColorRGBA.White); //TODO: CHANGE LATER

        strikesTillParry.move(0, 0, 5f);

        //background of strikesTillParry
        Material spMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        spMat.setTexture("ColorMap", assetManager.loadTexture("Interface/GUI/stat_screen/specialborder.png"));
        spMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        specialGeo.setMaterial(spMat);
        
        specialNode = new Container(new BoxLayout(Axis.Y, FillMode.None));
        specialNode.attachChild(specialGeo);
        
        specialNode.addChild(new Label(" "));
        strikesTillParry.attachChild(specialNode);
        specialNode.move(15f, -30f, -1f);
        specialGeo.move(-65, -65, 0);
        ((TbtQuadBackgroundComponent)specialNode.getBackground()).setAlpha(0f);
        strikesTillParry.move(50f, -95f, 1f);
        //END PARRY CIRCLE

        //BEGIN TRANSITION ANIM
        sword = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        sword.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        transitionAnim.setMaterial(sword);
        sword.setTexture("ColorMap", assetManager.loadTexture("Interface/GUI/stat_screen/nothing.png"));
        transitionAnim.move(150f, -760f, 5f);
        //END TRANSITION ANIM
        
        //START LEAF BORDER
        Material leaf = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        leaf.setTexture("ColorMap", assetManager.loadTexture("Interface/GUI/ui_borders/leafborder.png"));
        leaf.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        portraitFrame.setMaterial(leaf);
        portraitFrame.move(-32.5f, -270, 30);
        //END LEAF BORDER
        
        info = new Container();
        info.setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/ui_boxes/tab.png")));
        info.move(80, -300, 1f);
        
        defaultBGColor = ((TbtQuadBackgroundComponent)new Container().getBackground()).getColor();
        
        scale(0.975f);
        move(10, -17.5f, 0);
    }
    
    //TODO: make all object declarations declared in the constructor or initialization or something
    public void startUnitViewGUI(TangibleUnit tu, Conveyor data) {
        currentX = 0;
        currentY = 0;

        namedElements = new ArrayList<>();
        ArrayList<Cosa> 
                col1 = new ArrayList<>(), 
                col2 = new ArrayList<>(),
                col3 = new ArrayList<>(),
                col4 = new ArrayList<>(),
                col5 = new ArrayList<>();
        
        switchedElements = new ArrayList<>();

        //Create a panel for menu
        menu = new Container(new BoxLayout(Axis.X, FillMode.None));

        QuadBackgroundComponent qbtrans = new QuadBackgroundComponent(assetManager.loadTexture("Textures/gui/unitwindowbg.jpg"), 0, 0);
        qbtrans.setAlpha(0f);

        menu.setBackground(qbtrans);
        
        //Create a container
        Container unitWindowX = new Container(new BoxLayout(Axis.X, FillMode.None));

        QuadBackgroundComponent qb = new QuadBackgroundComponent(assetManager.loadTexture("Textures/gui/unitwindowbg2.jpg"), 0, 0);
        qb.setAlpha(0.88f);
        unitWindowX.setBackground(qb);

        menu.addChild(unitWindowX);

        //START COLUMN 1
        Container dontstretchportrait = new Container(new BoxLayout(Axis.Y, FillMode.None));
        dontstretchportrait.setInsets(new Insets3f(0, 0, 0, 35));
        dontstretchportrait.setBackground(qbtrans);

        //START PORTRAIT
        Panel portrait = new Panel(240f, 240f);
        portrait.setBorder(new QuadBackgroundComponent(assetManager.loadTexture("Textures/gui/portraitbg2nd.jpg")));
        portrait.setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Textures/portraits/" + tu.getUnitInfo().getPortraitTextureName())));
        portrait.setInsets(new Insets3f(0.1f, 0.1f, 0.1f, 0.1f));
        portrait.attachChild(portraitFrame);
        dontstretchportrait.addChild(portrait);
        //END PORTRAIT

        //START UNIT CLASS
        TrueTypeKeyBMP classload = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular.ttf", Style.Bold, 30);
        TrueTypeFont classfont = (TrueTypeBMP)assetManager.loadAsset(classload);
        classfont.setScale(0.6f);
        EditedLabel extraInfo = new EditedLabel("    Class: " + tu.getJobClass().getName(), classfont);
        Container unitInfoEX = new Container(new BoxLayout(Axis.Y, FillMode.None));
        unitInfoEX.addChild(extraInfo);
        unitInfoEX.setInsets(new Insets3f(20, 15, 0, 15));
        ((TbtQuadBackgroundComponent)unitInfoEX.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
        
        dontstretchportrait.addChild(unitInfoEX);
        col1.add(new Cosa(unitInfoEX, "00", tu.getJobClass().getDescription() + "\n \n The user's class determines the weapons they can wield, which stats are more likely to increase during a level up, possible skills, possible talents, possible abilities, has an effect on stats, and the user's general role in combat."));
        //END UNIT CLASS
        
        //START STATUS
        TrueTypeKeyBMP statusload = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular4.ttf", Style.Bold, 30);
        TrueTypeFont statusfont = (TrueTypeBMP)assetManager.loadAsset(statusload);
        statusfont.setScale(0.6f);
        EditedLabel extraInfoStatus = new EditedLabel("    Status: " + tu.getStatus(), statusfont);
        Container unitInfoStatus = new Container(new BoxLayout(Axis.Y, FillMode.None));
        unitInfoStatus.addChild(extraInfoStatus);
        unitInfoStatus.setInsets(new Insets3f(2.5f, 15, 0, 15));
        ((TbtQuadBackgroundComponent)unitInfoStatus.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
        
        dontstretchportrait.addChild(unitInfoStatus);
        col1.add(new Cosa(unitInfoStatus, "0.5", "Their status takes status effects into account (such as being poisoned) and will display them accordingly."));
        //END STATUS

        //START BATTLE STATS TITLE
        Container bstats = new Container(new BoxLayout(Axis.Y, FillMode.None));

        TrueTypeKeyBMP qrbmp2 = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Bold.ttf", Style.Plain, 44);
        TrueTypeFont qrbmpfont2 = (TrueTypeBMP)assetManager.loadAsset(qrbmp2);
        qrbmpfont2.setScale(30f / 44f);

        EditedLabel battleStatTitle = new EditedLabel("Battle Stats", qrbmpfont2);
        battleStatTitle.text.getTTFNode().move(15f, -15f, 0f);

        bstats.addChild(battleStatTitle);
        //END BATTLE STATS TITLE

        //START BATTLE STATS
        TrueTypeKeyBMP qrbmp3 = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular.ttf", Style.Plain, 43);
        TrueTypeFont qrbmpfont3 = (TrueTypeBMP)assetManager.loadAsset(qrbmp3);
        qrbmpfont3.setScale(25f / 43f);
        EditedLabel battleStats = new EditedLabel(
                  "ATK PWR: " + tu.getATK() + "\n"
                + "ACC: " + tu.getAccuracy() + "\n"
                + "EVA: " + tu.getEvasion() + "\n"
                + "CRIT: " + tu.getCrit() + "\n"
                + "ADRENALINE: " + tu.getADRENALINE(), qrbmpfont3);
        battleStats.text.getTTFNode().move(15f, -29.5f, 0f);
        battleStats.setInsets(new Insets3f(160f, 0, 0, 0));

        bstats.addChild(battleStats);
        bstats.setInsets(new Insets3f(15f, 3.5f, 0, 0));
        ((TbtQuadBackgroundComponent)bstats.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
        bstats.setBorder(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/ui_boxes/default.png")));

        dontstretchportrait.addChild(bstats);
        col1.add(new Cosa(bstats, "01", "ATK PWR: if attacking physically, calculated by STR + equippped weapon's Pow. If attacking with ether, calculated by ETHER + equipped formula's Pow. \n \nADRENALINE: slightly increases CRIT but mainly affects crit damage as well as Fight or Flight mode"));
        //END BATTLE STATS

        //START FORMATION
        Container formationPanel = new Container(new BoxLayout(Axis.Y, FillMode.None));
        ((TbtQuadBackgroundComponent)formationPanel.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
        formationPanel.setBorder(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/ui_boxes/default.png")));

        //START FORMATION TITLE
        TrueTypeKeyBMP titleformation = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Bold.ttf", Style.Plain, 51);
        TrueTypeFont formationttf = (TrueTypeBMP)assetManager.loadAsset(titleformation);
        formationttf.setScale(30f / 51f);
        EditedLabel formationLabel = new EditedLabel("Formation", formationttf);
        formationLabel.text.getTTFNode().move(37.5f, -15f, 0f);
        formationPanel.addChild(formationLabel);
        //END FORMATION TITLE

        dontstretchportrait.addChild(formationPanel);
        formationPanel.setInsets(new Insets3f(15f, 5f, 0f, 0f));
        if (!tu.getFormations().isEmpty()) {
            addFormation(tu.getFormations().get(0), formationPanel, col1);
        } else { 
            formationPanel.addChild(new Label("\n\n\n\n\n\n\n\n\n\n")); 
        }
        //END FORMATION

        unitWindowX.addChild(dontstretchportrait);
        //END COLUMN 1

        //START COLUMN 2
        Container cellB = new Container(new BoxLayout(Axis.Y, FillMode.None));
        cellB.setBackground(qbtrans);

        //START NAMETAG AND OUTLINE
        TrueTypeKeyBMP immortal = new TrueTypeKeyBMP("Interface/Fonts/Cinzel_Decorative/CinzelDecorative-Bold.ttf", Style.Plain, 28);
        TrueTypeFont immortalfont = (TrueTypeBMP)assetManager.loadAsset(immortal);
        
        TrueTypeKeyBMP immortal2 = new TrueTypeKeyBMP("Interface/Fonts/Cinzel_Decorative/CinzelDecorative-Bold.ttf", Style.Plain, 30);
        TrueTypeFont immortalfont2 = (TrueTypeBMP)assetManager.loadAsset(immortal2);
        
        EditedLabel nametag = new EditedLabel(tu.getName(), immortalfont, ColorRGBA.White);
        EditedLabel nametagOutline = new EditedLabel(tu.getName(), immortalfont2, ColorRGBA.Black, 0);
        
        Panel nmtg = new Panel(62.6f, 216f / 2.3f);
        nmtg.setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/ui_boxes/emptyname.png")));
        nmtg.attachChild(nametag);
        nmtg.attachChild(nametagOutline);
        //float dx = 140f - (nametag.text.getTTFNode().getWidth() / 2f);
        float dx2 = 140f - (nametagOutline.text.getTTFNode().getWidth() / 2f);
        float dHeight = (nametagOutline.text.getTTFNode().getHeight() - nametag.text.getTTFNode().getHeight()) / 2f, dWidth = (nametagOutline.text.getTTFNode().getWidth() - nametag.text.getTTFNode().getWidth()) / 2f;
        nametag.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Center);
        nametagOutline.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Center);
        nametag.text.getTTFNode().move(dx2 + dWidth, -28f - dHeight, 0.1f);
        nametagOutline.text.getTTFNode().move(dx2, -28f, 0.05f);
        nametag.setFontSize(20f);
        
        cellB.addChild(nmtg);
        //END NAMETAG AND OUTLINE

        //START HP AND TP BARS
        TrueTypeKeyBMP bars = new TrueTypeKeyBMP("Interface/Fonts/Montaga-Regular.ttf", Style.Bold, 28);
        TrueTypeFont barsfont = (TrueTypeBMP)assetManager.loadAsset(bars);
        barsfont.setScale(0.7f);
        CustomProgressBar 
                hpBar = new CustomProgressBar(barsfont), //new ElementId("HP")
                tpBar = new CustomProgressBar(barsfont); //new ElementId("TP")
        
                hpBar.setMessage("HP: " + tu.getBaseStat(BaseStat.CurrentHP) + "/" + tu.getMaxHP(), ColorRGBA.Black);
                hpBar.setProgressPercent(((double)tu.getBaseStat(BaseStat.CurrentHP) / tu.getMaxHP()));
                
                tpBar.setMessage("TP: " + tu.getBaseStat(BaseStat.CurrentTP) + "/" + tu.getMaxTP(), ColorRGBA.Black);
                tpBar.setProgressPercent(((double)tu.getBaseStat(BaseStat.CurrentTP) / tu.getMaxTP()));
                
                hpBar.setBarColor(new ColorRGBA(0, 0.76f, 0, 1));
                tpBar.setBarColor(new ColorRGBA(0.85f, 0.36f, 0.83f, 1f));

        //START HP AND TP BORDERS
        Container hpborder = new Container(new BoxLayout(Axis.Y, FillMode.None));
        ((TbtQuadBackgroundComponent)hpborder.getBackground()).setColor(ColorRGBA.Black);
        hpborder.addChild(hpBar);
        hpBar.setInsets(new Insets3f(3f, 3f, 3f, 3f));
        
        Container tpborder = new Container(new BoxLayout(Axis.Y, FillMode.None));
        ((TbtQuadBackgroundComponent)tpborder.getBackground()).setColor(ColorRGBA.Black);
        tpborder.addChild(tpBar);
        tpBar.setInsets(new Insets3f(3f, 3f, 3f, 3f));

        
        col2.add(new Cosa(hpborder, "hp", "The User's HP (Health Points). Once it reaches 0, they are dead."));
        col2.add(new Cosa(tpborder, "tp", "The User's TP (Tempo Points). Once it reaches 0, they are unable to use any formulas or skills that cost TP"));
        
        switchedElements.add(new Cosa(hpborder, "hp", "The User's HP (Health Points). Once it reaches 0, they are dead."));
        switchedElements.add(new Cosa(tpborder, "tp", "The User's TP (Tempo Points). Once it reaches 0, they are unable to use any formulas or skills that cost TP"));
        
        cellB.addChild(hpborder);

        Label nothing2 = new Label("  "); //spacer
        nothing2.setFontSize(3f);
        cellB.addChild(nothing2);

        cellB.addChild(tpborder);
        //END HP AND TP BORDERS
        //END HP AND TP BARS
        
        //START INVENTORY
        Container itemsPanel = new Container(new BoxLayout(Axis.Y, FillMode.None));
        ((TbtQuadBackgroundComponent)itemsPanel.getBackground()).setColor(new ColorRGBA(1f, 1f, 1f, 0f));
        itemsPanel.setBorder(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/ui_boxes/invborder3.png")));
        
        addItems(tu, itemsPanel, col2);
        //END INVENTORY
        
        //START SKILLS
        Container skillsPanel = new Container(new BoxLayout(Axis.Y, FillMode.None));
        ((TbtQuadBackgroundComponent)skillsPanel.getBackground()).setColor(new ColorRGBA(1f, 1f, 1f, 0f));
        skillsPanel.setBorder(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/stat_screen/skillsbackdrop2.png")));
        
        addSkills(tu, skillsPanel, switchedElements);
        //END SKILLS

        //START ITEM-SKILLS TABBEDPANEL
        //itemsPanel
        itemsOrSkills = new TabbedPanel();
        itemsOrSkills.addTab("                   \n                     ", itemsPanel);
        itemsOrSkills.getSelectedTab().getTitleButton().setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/stat_screen/itemslogo.png")));
        itemsOrSkills.getSelectedTab().getTitleButton().setBorder(new Container().getBackground());
        itemsOrSkills.getSelectedTab().getTitleButton().setInsets(new Insets3f(15, 45, 3, 15));
        
        //change to skillsPanel
        itemsOrSkills.addTab("                       \n                       ", skillsPanel);
        itemsOrSkills.getTabs().get(1).getTitleButton().setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/stat_screen/skillslogo.png")));
        itemsOrSkills.getTabs().get(1).getTitleButton().setBorder(new Container().getBackground());
        itemsOrSkills.getTabs().get(1).getTitleButton().setInsets(new Insets3f(15, 30, 3, 0));
        
        ((TbtQuadBackgroundComponent)itemsOrSkills.getSelectedTab().getTitleButton().getBorder()).setColor(new ColorRGBA(0, 0.839f, 0.871f, 1));

        cellB.addChild(itemsOrSkills);
        //END ITEM-SKILLS TABBEDPANEL

        //START FORMULAS
        Container formulasPanel = new Container(new BoxLayout(Axis.Y, FillMode.None));
        ((TbtQuadBackgroundComponent)formulasPanel.getBackground()).setColor(new ColorRGBA(1f, 1f, 1f, 0f));
        formulasPanel.setBorder(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/ui_boxes/formulaborder.png")));
        
        addFormulas(tu, formulasPanel, col2);
        //END FORMULAS
        
        //START ABILITIES
        Container abilitiesPanel = new Container(new BoxLayout(Axis.Y, FillMode.None));
        ((TbtQuadBackgroundComponent)abilitiesPanel.getBackground()).setColor(new ColorRGBA(1f, 1f, 1f, 0f));
        abilitiesPanel.setBorder(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/ui_boxes/default.png")));
        
        addAbilities(tu, abilitiesPanel, switchedElements);
        //END ABILITIES

        //START FORMULAS-ABILITIES TABBEDPANEL
        //formulasPanel
        formulasOrAbilities = new TabbedPanel();
        formulasOrAbilities.addTab("                        \n                                     ", formulasPanel);
        formulasOrAbilities.getSelectedTab().getTitleButton().setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/stat_screen/formulaslogo.png")));
        formulasOrAbilities.getSelectedTab().getTitleButton().setBorder(new Container().getBackground());
        formulasOrAbilities.getSelectedTab().getTitleButton().setInsets(new Insets3f(15, 5, 3, 15));
        
        //change to abilitiesPanel
        formulasOrAbilities.addTab("                               \n                             ", abilitiesPanel);
        formulasOrAbilities.getTabs().get(1).getTitleButton().setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/stat_screen/abilitieslogo.png")));
        formulasOrAbilities.getTabs().get(1).getTitleButton().setBorder(new Container().getBackground());
        formulasOrAbilities.getTabs().get(1).getTitleButton().setInsets(new Insets3f(15, 30, 3, 0));
        
        ((TbtQuadBackgroundComponent)formulasOrAbilities.getSelectedTab().getTitleButton().getBorder()).setColor(new ColorRGBA(0, 0.839f, 0.871f, 1));
        
        cellB.addChild(formulasOrAbilities);
        //END FORMULAS-ABILITIES TABBEDPANEL
        
        unitWindowX.addChild(cellB);
        //END COLUMN 2

        //START COLUMN 3
        Container cellX = new Container(new BoxLayout(Axis.Y, FillMode.None));
        cellX.setBackground(qbtrans);
        
        //START LEVEL LABEL
        TrueTypeKeyBMP lvl = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular.ttf", Style.Plain, 40);
        TrueTypeFont lvlfont = (TrueTypeBMP)assetManager.loadAsset(lvl);
        lvlfont.setScale(0.7f);
        TrueTypeNode levelLabel = lvlfont.getText("LVL " + tu.getLVL(), 3, ColorRGBA.White);
        unitWindowX.attachChild(levelLabel);
        levelLabel.move(605f, -20f, 5f);
        //END LEVEL LABEL

        //START EXP BAR
        TrueTypeKeyBMP xp = new TrueTypeKeyBMP("Interface/Fonts/Neuton-Italic.ttf", Style.Plain, 25);
        TrueTypeFont xpfont = (TrueTypeBMP)assetManager.loadAsset(xp);
        TrueTypeNode xptext = xpfont.getText(" EXP:\n" + tu.currentEXP +"/100", 2, ColorRGBA.White);
        xptext.move(-40f, 35f, 3);
        expbar.getChildrenNode().attachChild(xptext);
        expbar.setCirclePercent(tu.currentEXP / 100f);
        
        Container expCont = new Container();
        ((TbtQuadBackgroundComponent)expCont.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
        expCont.attachChild(expbar);
        expCont.addChild(new Label("                          \n\n\n\n\n\n\n\n\n                                             ")); //used to be cellX
        expCont.setInsets(new Insets3f(20f, 10f, 20f, 25f));
        
        cellX.addChild(expCont);
        col3.add(new Cosa(expCont, "xp", "The User's experience value. Once it reaches 100 the user will level up in which they gain stats and learn possible techniques."));
        //END EXP BAR
        
        //START BASE STATS
        Container statsCont = new Container();
        ((TbtQuadBackgroundComponent)statsCont.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
        statsCont.setInsets(new Insets3f(5f, 5f, 5f, 135f));
        addStats(tu, data, statsCont, col3);
        
        cellX.addChild(statsCont);
        //END BASE STATS

        unitWindowX.addChild(cellX);
        //END COLUMN 3

        //START COLUMN 4
        Container cellStrikes = new Container(new BoxLayout(Axis.Y, FillMode.None));
        cellStrikes.setBackground(qbtrans);

        //START PARRY COUNT
        Container strikesCont = new Container();
        ((TbtQuadBackgroundComponent)strikesCont.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
        strikesCont.attachChild(strikesTillParry);
        strikesCont.addChild(new Label("                                     \n\n\n\n\n\n                               "));
        strikesCont.setInsets(new Insets3f(62.5f, 0f, 30f, 0f));
        
        cellStrikes.addChild(strikesCont);
        col4.add(new Cosa(strikesCont, "pm", "The user's parry cooldown. Once this number reaches 0 the user will automatically parry the next attack they receive such that they only receive 1 damage, even if it is a critical hit. The lower the ratio of the amount of enemy units that start on the map to the unit's COMP stat, the lower the cooldown will be for parrying."));
        //END PARRY COUNT

        unitWindowX.addChild(cellStrikes);
        //END COLUMN 4

        //START COLUMN 5
        Container cellSave = new Container(new BoxLayout(Axis.Y, FillMode.None));
        cellSave.setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/ui_borders/talentborder2.png")));

        //spacing
        Label control = new Label("                      ");
        control.setInsets(new Insets3f(3f, 80f, 0f, 0f));
        cellSave.setInsets(new Insets3f(3f, 10f, 0f, 0f));
        cellSave.addChild(control);

        //START TALENTS
        TrueTypeKeyBMP tlns = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Bold.ttf", Style.Plain, 48);
        TrueTypeFont tals = (TrueTypeBMP)assetManager.loadAsset(tlns);
        tals.setScale(5f / 8f);
        EditedLabel talentslabel = new EditedLabel("  Talents", tals);
        talentslabel.text.getTTFNode().move(0, 5, 0);
        cellSave.addChild(talentslabel);
        addTalents(tu, cellSave, col5);
        //END TALENTS
        
        unitWindowX.addChild(cellSave);
        //END COLUMN 5

        //START CURSOR INITIALIZATION
        namedElements.addAll(Arrays.asList(col1, col2, col3, col4, col5));
        initializeSizes();
        tryToggling(false);
        //END CURSOR INITIALIZATION
        
        //START INFO TAB
        menu.addChild(info);

        stuff = new EditedTextField(namedElements.get(currentX).get(currentY).getDescription());
        stuff.setColor(ColorRGBA.White);
        stuff.setFont(assetManager.loadFont("Interface/Fonts/neuton.fnt")); //used to be greco2.fnt
        stuff.setFontSize(25f);
        stuff.setSingleLine(false);
        ((QuadBackgroundComponent)stuff.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
        stuff.setPreferredWidth(240f);
        stuff.setInsets(new Insets3f(40f, 35f, 0, 30));
        stuff.getTextEntryComponent().getTextComponent().setLineWrapMode(LineWrapMode.Word);
        //END INFO TAB

        cellSave.attachChild(transitionAnim);
        
        setLocalScale(1.25f, 1.25f, 1);
        setLocalTranslation(0.125f * Main.getScreenWidth(), -0.065f * Main.getScreenHeight(), 1f);
        
        attachChild(menu);
    }
    
    //7 items displayed at a time
    private void addItems(TangibleUnit tu, Container itemPanel, ArrayList<Cosa> col2) {
        int i = 0;
        while (i < tu.getInventory().getItems().size()) {
            TrueTypeKeyBMP bmp = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular.ttf", Style.Plain, (35 + i));
            TrueTypeFont ttf = (TrueTypeBMP)assetManager.loadAsset(bmp);
            ttf.setScale((20f / (35f + i)));

            EditedLabel itemName = new EditedLabel(tu.getInventory().getItems().get(i).getName(), ttf);
            itemName.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Center);
            float xDisplace = 130f - (itemName.text.getTTFNode().getWidth() / 2f);
            itemName.text.getTTFNode().move(xDisplace, 0f, 0); //the width of the container is 230f

            Container master = new Container();
            ((TbtQuadBackgroundComponent)master.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
            master.addChild(itemName);
            if (i == 0) {
                master.setInsets(new Insets3f(26f, 10, 0, 10));
            } else {
                master.setInsets(new Insets3f(8.5f, 10, 0, 10));
            }

            col2.add(new Cosa
                    (master, "i" + i,
                            tu.getInventory().getItems().get(i) instanceof Weapon ? ((Weapon)tu.getInventory().getItems().get(i)).getDescription()
                                    : tu.getInventory().getItems().get(i).getDescription()
                    )
            );

            itemPanel.addChild(master);

            if (tu.getInventory().getItems().get(i) instanceof Weapon) {
                Panel icon = new Panel(24f, 24f);
                icon.setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/icons/item_and_formula/" + ((Weapon)tu.getInventory().getItems().get(i)).getWeaponData().getType() + ".png")));
                icon.move(25 + xDisplace * -1f, 5f, 0);
                itemName.text.getTTFNode().attachChild(icon);

                TrueTypeKeyBMP bmp2 = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular2.ttf", Style.Plain, (25 + i));
                TrueTypeFont ttf2 = (TrueTypeBMP)assetManager.loadAsset(bmp2);
                ttf2.setScale((18f / (25f + i)));
                TrueTypeNode durability = ttf2.getText("" + ((Weapon)tu.getInventory().getItems().get(i)).getCurrentDurability(), 3, ColorRGBA.White);
                durability.move((xDisplace * -1f) + 200, -2.5f, 0);
                itemName.text.getTTFNode().attachChild(durability);
            } else if (tu.getInventory().getItems().get(i) instanceof ConsumableItem) {
                Panel icon = new Panel(24f, 24f);
                icon.setBackground(new QuadBackgroundComponent(assetManager.loadTexture(((ConsumableItem)tu.getInventory().getItems().get(i)).getIconPath())));
                icon.move(25 + xDisplace * -1f, 5f, 0);
                itemName.text.getTTFNode().attachChild(icon);

                TrueTypeKeyBMP bmp2 = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular2.ttf", Style.Plain, (25 + i));
                TrueTypeFont ttf2 = (TrueTypeBMP)assetManager.loadAsset(bmp2);
                ttf2.setScale((18f / (25f + i)));
                TrueTypeNode uses = ttf2.getText("" + ((ConsumableItem)tu.getInventory().getItems().get(i)).getCurrentUses(), 3, ColorRGBA.White);
                uses.move((xDisplace * -1f) + 210, -2.5f, 0);
                itemName.text.getTTFNode().attachChild(uses);
            }

            i++;
        }

        while (i < 7) {
            TrueTypeKeyBMP bmp = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular.ttf", Style.Plain, i);
            TrueTypeFont ttf = (TrueTypeBMP)assetManager.loadAsset(bmp);
            ttf.setScale((20f / i));

            EditedLabel itemName = new EditedLabel(" ", ttf);
            itemName.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Center);
            float xDisplace = 130f - (itemName.text.getTTFNode().getWidth() / 2f);
            itemName.text.getTTFNode().move(xDisplace, 0f, 0); //the width of the container is 230f

            Container master = new Container();
            ((TbtQuadBackgroundComponent)master.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
            master.addChild(itemName);

            master.setInsets(new Insets3f(8.5f, 10, 0, 10));
            itemPanel.addChild(master);

            i++;
        }
    }
    
    private void addFormulas(TangibleUnit tu, Container formulaPanel, ArrayList<Cosa> col2) {
        int i = 0;
        while (i < tu.getFormulaManager().getEquipped().size()) {
            TrueTypeKeyBMP bmp = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular.ttf", Style.Plain, (46 + i));
            TrueTypeFont ttf = (TrueTypeBMP)assetManager.loadAsset(bmp);
            ttf.setScale((20f / (46f + i)));

            EditedLabel formulaName = new EditedLabel(tu.getFormulaManager().getEquipped().get(i).getName(), ttf);
            formulaName.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Center);
            float xDisplace = 130f - (formulaName.text.getTTFNode().getWidth() / 2f);
            formulaName.text.getTTFNode().move(xDisplace, 0f, 0); //the width of the container is 230f

            Container master = new Container();
            ((TbtQuadBackgroundComponent)master.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
            master.addChild(formulaName);

            col2.add(new Cosa(master, "f" + i, tu.getFormulaManager().getEquipped().get(i).getDescription()));

            if (i == 0) {
                master.setInsets(new Insets3f(30f, 10, 0, 10));
            } else {
                master.setInsets(new Insets3f(8.5f, 10, 0, 10));
            }

            formulaPanel.addChild(master);

            Panel icon = new Panel(20f, 20f);
            icon.setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/icons/item_and_formula/" + (tu.getFormulaManager().getEquipped().get(i)).getActualFormulaData().getType() + ".png")));
            icon.move(25f + xDisplace * -1f, 0, 0);
            formulaName.text.getTTFNode().attachChild(icon);

            TrueTypeKeyBMP bmp2 = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular2.ttf", Style.Plain, (36 + i));
            TrueTypeFont ttf2 = (TrueTypeBMP)assetManager.loadAsset(bmp2);
            ttf2.setScale((20f / (36f + i)));

            ColorRGBA colorType;
            int cost;
            if (tu.getFormulaManager().getEquipped().get(i).getTPUsage() > 0) {
                colorType = new ColorRGBA(0.85f, 0.36f, 0.83f, 1f);
                cost = tu.getFormulaManager().getEquipped().get(i).getTPUsage();
            } else {
                colorType = new ColorRGBA(1, 0.76f, 0, 1);
                cost = tu.getFormulaManager().getEquipped().get(i).getHPUsage();
            }

            TrueTypeNode costValue = ttf2.getText("" + cost, 3, colorType);
            costValue.move(formulaName.text.getTTFNode().getWidth() + 15, 0f, 0);
            formulaName.text.getTTFNode().attachChild(costValue);

            i++;
        }

        for (int k = i; k < 15; k++) {
            Label placehold = new Label("                                                                                   ");
            placehold.setFontSize(11f - (2 * i)); //the moment a unit gets 6 or more formulas, this will fail. Make it a scrolling view later
            formulaPanel.addChild(placehold);
        }
    }
    
    private void addTalents(TangibleUnit tu, Container talentPanel, ArrayList<Cosa> col5) {
        List<Talent> individualTalents = tu.getIndividualTalents();
        for (int i = 0; i < TalentManager.MAX_EQUIPPED_TALENTS; i++) {
            Container talentIcon = new Container();
            String desc = "";
            if (i < individualTalents.size()) {
                String iconPath = individualTalents.get(i).getIconPath();
                desc += individualTalents.get(i).getDescription();
                talentIcon.setBorder(new QuadBackgroundComponent(assetManager.loadTexture(iconPath)));
            } else {
                talentIcon.setBorder(new Container().getBackground());
            }
            Label placeholder = new Label(" ");
            talentIcon.addChild(placeholder);
            placeholder.setInsets(new Insets3f(62f, 0, 0, 0));
            talentPanel.addChild(talentIcon);
            talentIcon.setInsets(new Insets3f(17.5f, 35f, 17.5f, 35f));
            
            col5.add(new Cosa(talentIcon, "t" + i, desc));
        }
    }

    private void addSkills(TangibleUnit tu, Container skillsPanel, ArrayList<Cosa> block1) {
        int i = 0;
        while (i < tu.getSkills().size()) {
            TrueTypeKeyBMP bmp = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular3.ttf", Style.Plain, (46 + i));
            TrueTypeFont ttf = (TrueTypeBMP)assetManager.loadAsset(bmp);
            ttf.setScale((20f / (46f + i)));

            EditedLabel skillName = new EditedLabel(tu.getSkills().get(i).getName(), ttf);
            skillName.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Center);
            float xDisplace = 130f - (skillName.text.getTTFNode().getWidth() / 2f);
            skillName.text.getTTFNode().move(xDisplace, 0f, 0); //the width of the container is 230f

            Container master = new Container();
            ((TbtQuadBackgroundComponent)master.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
            master.addChild(skillName);

            block1.add(new Cosa(master, tu.getSkills().get(i).getPath(), tu.getSkills().get(i).getEffect().effectDescription()));

            master.setInsets(new Insets3f(11.5f, 10, 0, 10));

            skillsPanel.addChild(master);

            Panel icon = new Panel(35f, 35f);
            icon.setBackground(new QuadBackgroundComponent(assetManager.loadTexture(tu.getSkills().get(i).getPath())));
            icon.move(10f + xDisplace * -1f, 10, 0);
            skillName.text.getTTFNode().attachChild(icon);

            TrueTypeKeyBMP bmp2 = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular3.ttf", Style.Plain, (36 + i));
            TrueTypeFont ttf2 = (TrueTypeBMP)assetManager.loadAsset(bmp2);
            ttf2.setScale((20f / (36f + i)));

            ColorRGBA colorType;
            int cost;
            switch (tu.getSkills().get(i).getToll().getType()) {
                case TP:
                    colorType = new ColorRGBA(0.85f, 0.36f, 0.83f, 1f);
                    break;
                case HP:
                    colorType = new ColorRGBA(1, 0.76f, 0, 1);
                    break;
                default:
                    //weapon durability
                    colorType = ColorRGBA.White;
                    break;
            }
            cost = tu.getSkills().get(i).getToll().getValue();

            TrueTypeNode costValue = ttf2.getText("" + cost, 3, colorType);
            costValue.move(skillName.text.getTTFNode().getWidth() + 15, 0f, 0);
            skillName.text.getTTFNode().attachChild(costValue);

            i++;
        }

        while (i < 5) {
            Label placehold = new Label("                                                                                   ");
            skillsPanel.addChild(placehold);

            if (i == 4) {
                placehold.setInsets(new Insets3f(140f, 0, 0, 0));
            }

            i++;
        }
    }
    
    private void addAbilities(TangibleUnit tu, Container abilitiesPanel, ArrayList<Cosa> block2) {
        int i = 0;
        
        while (i < tu.getAbilities().size()) {
            TrueTypeKeyBMP bmp = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular3.ttf", Style.Plain, (35 + i));
            TrueTypeFont ttf = (TrueTypeBMP)assetManager.loadAsset(bmp);
            ttf.setScale((20f / (35f + i)));

            EditedLabel abilityName = new EditedLabel(tu.getInventory().getItems().get(i).getName(), ttf);
            abilityName.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Center);
            float xDisplace = 130f - (abilityName.text.getTTFNode().getWidth() / 2f);
            abilityName.text.getTTFNode().move(xDisplace, 0f, 0); //the width of the container is 230f

            Container master = new Container();
            ((TbtQuadBackgroundComponent)master.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
            master.addChild(abilityName);
            if (i == 0) {
                master.setInsets(new Insets3f(26f, 10, 0, 10));
            } else {
                master.setInsets(new Insets3f(8.5f, 10, 0, 10));
            }

            block2.add(new Cosa(master, "aI" + i, tu.getInventory().getItems().get(i).getDescription()));

            abilitiesPanel.addChild(master);

            i++;
        }

        for (int k = i; k < 6; k++) {
            Label placehold = new Label("                                                                                   ");
            placehold.setInsets(new Insets3f(20f, 0, 0, 0));
            abilitiesPanel.addChild(placehold);
        }
    }
    
    private void addFormation(Formation forma, Container formationPanel, ArrayList<Cosa> col1) {
        formationPanel.addChild(new Label(" "));
        
        Container equippedFormation = new Container();
        ((TbtQuadBackgroundComponent)equippedFormation.getBackground()).setColor(new ColorRGBA(0, 0, 0, 0f));
        
        Quad dm = new Quad(125f, 125f);
        Geometry symbol = new Geometry("dimensions", dm);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Interface/GUI/icons/formation_type/" + forma.getFormationType() + ".png"));
        mat.setColor("Color", new ColorRGBA(1, 1, 1, 0.5f));
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        symbol.setMaterial(mat);
        symbol.rotate(0, 0, FastMath.PI / -8f);
        symbol.move(40, -110, 1);
        equippedFormation.attachChild(symbol);
        
        
        TrueTypeKeyBMP bitmap = new TrueTypeKeyBMP("Interface/Fonts/Merriweather-Regular.ttf", Style.Plain, 28);
        TrueTypeFont ttf = (TrueTypeBMP)assetManager.loadAsset(bitmap);
        ttf.setScale(18f / 28f);
        
        EditedLabel formationTitle = new EditedLabel(forma.getName(), ttf, new ColorRGBA(0.561f, 0.898f, 1, 1));
        formationTitle.text.getTTFNode().move(
                    EngineUtils.centerTTFNode(
                        formationTitle.text.getTTFNode(), 
                        new Vector3f(210, 0, 0), //230 width for container
                        Arrays.asList(CenterAxis.X)
                    )
        );
        formationTitle.text.getTTFNode().move(0, 0, 5);
        equippedFormation.addChild(formationTitle);
        equippedFormation.setInsets(new Insets3f(17.5f, 12, 5f, 12));
        
        col1.add(new Cosa(equippedFormation, "fmTitle", forma.toString()));
        formationPanel.addChild(equippedFormation);
        
        int i = 0;
        while (i < forma.getTechniques().size()) {
            FormationTechnique tech = forma.getTechniques().get(i);
            
            Container cont = new Container();
            ((TbtQuadBackgroundComponent)cont.getBackground()).setColor(new ColorRGBA(0, 0, 0, 0f));
            cont.setInsets(new Insets3f(0f, 12, 0, 12));
            
            TrueTypeKeyBMP bmp = new TrueTypeKeyBMP("Interface/Fonts/Merriweather-Italic.ttf", Style.Plain, 28 + i);
            TrueTypeFont techTTF = (TrueTypeBMP)assetManager.loadAsset(bmp);
            techTTF.setScale(16f / (28f + i));
            
            EditedLabel text = new EditedLabel(tech.getName(), techTTF);
            text.text.getTTFNode().move(
                    EngineUtils.centerTTFNode(
                        text.text.getTTFNode(), 
                        new Vector3f(210, 0, 0), //230 width for container
                        Arrays.asList(CenterAxis.X)
                    )
            );
            text.text.getTTFNode().move(0, -1f, 5);
            
            cont.addChild(text);
            
            col1.add(new Cosa(cont, "tec" + i, tech.getDescription()));
            formationPanel.addChild(cont);
            
            i++;
        }
        
        for (int k = i; k < 3; k++) { //max: 3 formation techniques
            formationPanel.addChild(new Label("\n"));
        }
        
        formationPanel.addChild(new Label("\n"));
    }
    
    private class OrderedRawStats {
        public final TangibleUnit tu;
        public final List<StatBundle<BaseStat>> rawBaseStatsWithTempBuffs;
        public final List<StatBundle<BaseStat>> rawBaseStats;
        public final int[] rawBonuses;
        
        public OrderedRawStats(TangibleUnit tu, Conveyor data) {
            this.tu = tu;
            
            rawBaseStats = Arrays.asList(
                new StatBundle<>(BaseStat.Level, tu.getRawStat(BaseStat.Level)), //level
                new StatBundle<>(BaseStat.Strength, tu.getRawStat(BaseStat.Strength) + tu.getJobClass().getBaseStatBonuses().get(BaseStat.Strength)), //str
                new StatBundle<>(BaseStat.Ether, tu.getRawStat(BaseStat.Ether) + tu.getJobClass().getBaseStatBonuses().get(BaseStat.Ether)), //ether
                new StatBundle<>(BaseStat.Agility, tu.getRawStat(BaseStat.Agility) + tu.getJobClass().getBaseStatBonuses().get(BaseStat.Agility)), //agi
                new StatBundle<>(BaseStat.Comprehension, tu.getRawStat(BaseStat.Comprehension) + tu.getJobClass().getBaseStatBonuses().get(BaseStat.Comprehension)), //comp
                new StatBundle<>(BaseStat.Dexterity, tu.getRawStat(BaseStat.Dexterity) + tu.getJobClass().getBaseStatBonuses().get(BaseStat.Dexterity)), //dex
                new StatBundle<>(BaseStat.Defense, tu.getRawStat(BaseStat.Defense) + tu.getJobClass().getBaseStatBonuses().get(BaseStat.Defense)), //def
                new StatBundle<>(BaseStat.Resilience, tu.getRawStat(BaseStat.Resilience) + tu.getJobClass().getBaseStatBonuses().get(BaseStat.Resilience)), //rsl
                new StatBundle<>(BaseStat.Mobility, tu.getRawStat(BaseStat.Mobility) + tu.getJobClass().getBaseStatBonuses().get(BaseStat.Mobility)), //mobility
                new StatBundle<>(BaseStat.Physique, tu.getRawStat(BaseStat.Physique) + tu.getJobClass().getBaseStatBonuses().get(BaseStat.Physique)) //physique
            );
            
            int[] tempBuffs = getTotalBonuses(data, BonusType.Raw, false);
            
            rawBaseStatsWithTempBuffs = Arrays.asList(
                new StatBundle<>(BaseStat.Level, tu.getLVL()), //level
                new StatBundle<>(BaseStat.Strength, rawBaseStats.get(1).getValue() + tempBuffs[0]), //str
                new StatBundle<>(BaseStat.Ether, rawBaseStats.get(2).getValue() + tempBuffs[1]), //ether
                new StatBundle<>(BaseStat.Agility, rawBaseStats.get(3).getValue() + tempBuffs[2]), //agi
                new StatBundle<>(BaseStat.Comprehension, rawBaseStats.get(4).getValue() + tempBuffs[3]), //comp
                new StatBundle<>(BaseStat.Dexterity, rawBaseStats.get(5).getValue() + tempBuffs[4]), //dex
                new StatBundle<>(BaseStat.Defense, rawBaseStats.get(6).getValue() + tempBuffs[5]), //def
                new StatBundle<>(BaseStat.Resilience, rawBaseStats.get(7).getValue() + tempBuffs[6]), //rsl
                new StatBundle<>(BaseStat.Mobility, rawBaseStats.get(8).getValue() + tempBuffs[7]), //mobility
                new StatBundle<>(BaseStat.Physique, rawBaseStats.get(9).getValue() + tempBuffs[8]) //physique
            );
            
            rawBonuses = getTotalBonuses(data, BonusType.Raw, true);
        }
        
        private int[] getTotalBonuses(Conveyor data, BonusType filterBy, boolean inclusion) {
            return new int[] {
                tu.getTotalBonus(BaseStat.Strength, Occasion.Indifferent, data, filterBy, inclusion),      //str
                tu.getTotalBonus(BaseStat.Ether, Occasion.Indifferent, data, filterBy, inclusion),         //ether
                tu.getTotalBonus(BaseStat.Agility, Occasion.Indifferent, data, filterBy, inclusion),       //agi
                tu.getTotalBonus(BaseStat.Comprehension, Occasion.Indifferent, data, filterBy, inclusion), //comp
                tu.getTotalBonus(BaseStat.Dexterity, Occasion.Indifferent, data, filterBy, inclusion),     //dex
                tu.getTotalBonus(BaseStat.Defense, Occasion.Indifferent, data, filterBy, inclusion),       //def
                tu.getTotalBonus(BaseStat.Resilience, Occasion.Indifferent, data, filterBy, inclusion),    //rsl
                tu.getTotalBonus(BaseStat.Mobility, Occasion.Indifferent, data, filterBy, inclusion),      //mobility
                tu.getTotalBonus(BaseStat.Physique, Occasion.Indifferent, data, filterBy, inclusion)       //physique
            };
        }
    }
    
    private void addStats(TangibleUnit tu, Conveyor data, Container statsCont, ArrayList<Cosa> col3) {
        String[] statLabels = 
        {
            "        STR: ",
            "        ETHER: ",
            "        AGI: ",
            "        COMP: ",
            "        DEX: ",
            "        DEF: ",
            "        RSL: ",
            "        MOBILITY: ",
            "        PHYSIQUE: "
        };

        String[] statDescriptions = {
            "Physical strength. Directly contributes to the ATK PWR of a unit, in addition to the Pow of their equipped Weapon",
            "Ether production. Directly contributes to the ATK PWR of a unit, in addition to the Pow of their equipped Formula",
            "Agility. Has various effects. Contributes to the EVA of a unit",
            "Comprehension. Partially contributes to the frequency of parrying. Higher COMP leads to enemies having lower critical hit rates against the unit. Has various other effects",
            "Dexterity. Contributes to a unit's ACC and CRIT stats",
            "Physical defense. Directly contributes to a unit's survivability against physical attacks",
            "Resilience. Directly contributes to a unit's survivability against ether attacks",
            "Mobility. How far a unit can normally move",
            "Physique. If the combined weight of unit's inventory > PHYSIQUE, Available inventory space will decrease by the difference"
        };
        
        OrderedRawStats ORS = new OrderedRawStats(tu, data);
        
        for (int i = 0; i < statLabels.length; i++) {
            Container statContainer = new Container();
            ((TbtQuadBackgroundComponent)statContainer.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
        
            TrueTypeKeyBMP bitmap = new TrueTypeKeyBMP("Interface/Fonts/Montaga-Regular.ttf", Style.Plain, 28 + i);
            TrueTypeFont ttf = (TrueTypeBMP)assetManager.loadAsset(bitmap);
            ttf.setScale(21.56f / (28 + i));
            
            EditedLabel stat = new EditedLabel(statLabels[i], ttf);        
            stat.text.getTTFNode().move(-4f, (-35f * i) - 9f, 0f);
            stat.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Left);
            
            statContainer.addChild(stat);
            styleStat(ORS, stat.text.getTTFNode(), col3, statDescriptions[i], i);
            
            statsCont.addChild(statContainer);
        }
    }
    
    private void styleStat(OrderedRawStats ORS, TrueTypeNode individualStat, ArrayList<Cosa> col3, String statDescription, int i) {
        final String statNames[] = {"STR", "ETHER", "AGI", "COMP", "DEX", "DEF", "RSL", "MOBILIT", "PHYSIQU"};
        
        StatBundle<BaseStat> sb = ORS.rawBaseStatsWithTempBuffs.get(i + 1);
        StatBundle<BaseStat> baseSB = ORS.rawBaseStats.get(i + 1);
        
        TrueTypeKeyBMP bmp = new TrueTypeKeyBMP("Interface/Fonts/Montaga-Regular.ttf", Style.Plain, (37 + i));
        TrueTypeFont ttf = (TrueTypeBMP)assetManager.loadAsset(bmp);
        ttf.setScale(26.5f / (37f + i));
        
        ColorRGBA numberColor;
        int difference = sb.getValue() - baseSB.getValue();
        if (difference > 0) { //buff
            numberColor = new ColorRGBA(0, 0.97f, 1, 1);
            statDescription += " \nBonus: +" + difference;
        } else if (difference < 0) { //debuff
            numberColor = new ColorRGBA((217f / 255f), 0, 0, 1);
            statDescription += " \nBonus: " + difference;
        } else { //nothing
            numberColor = ColorRGBA.White;
        }
            
        TrueTypeNode ttn = ttf.getText("" + sb.getValue(), 2, numberColor);
        if (sb.getValue() >= 10) {
            ttn.move(-14.5f, 15f, 0);
        } else if (sb.getValue() == 1) {
            ttn.move(-6.5f, 15f, 0);
        } else {
            ttn.move(-8.5f, 15f, 0);
        }
        
        //statProgress[i].move(strWidth + 30f, i * (-1.168f * strHeight) - 12f, 0);
        statProgress[i].move(individualStat.getWidth() + 30f, -12f, 0);
        statProgress[i].getChildrenNode().attachChild(ttn);
        statProgress[i].setCirclePercent(sb.getValue() / ((float)ORS.tu.getJobClass().getMaxStats().get(sb.getStat())));
            
        individualStat.attachChild(statProgress[i]);
            
        //attach stat icons
        Panel icon = new Panel(42f * (135f / 128f), 42f * (135f / 128f));
        icon.setBorder(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/icons/base_stat/" + statNames[i] + ".png")));
        icon.move(8.25f, 8f, 0f);
        individualStat.attachChild(icon);
        
        col3.add(new Cosa(icon, "stat" + i, statDescription));
        
        int rawBonus = ORS.rawBonuses[i];
            
        //attach bonuses
        if (rawBonus > 0) {
            TrueTypeKeyBMP bonusbmp = new TrueTypeKeyBMP("Interface/Fonts/Montaga-Regular.ttf", Style.Plain, (46 + i));
            TrueTypeFont bonusttf = (TrueTypeBMP)assetManager.loadAsset(bonusbmp);
            bonusttf.setScale(27f / (46f + i));
                
            TrueTypeNode bonusText = bonusttf.getText(" +" + rawBonus, 5, new ColorRGBA(0.008f, 0.788f, 0.153f, 1f));
            bonusText.move(individualStat.getWidth() + 48.5f, 4f, 0f);
            individualStat.attachChild(bonusText);
        } else if (rawBonus < 0) {
            TrueTypeKeyBMP bonusbmp = new TrueTypeKeyBMP("Interface/Fonts/Montaga-Regular.ttf", Style.Plain, (46 + i));
            TrueTypeFont bonusttf = (TrueTypeBMP)assetManager.loadAsset(bonusbmp);
            bonusttf.setScale(27f / (46f + i));
                
            TrueTypeNode bonusText = bonusttf.getText(" " + rawBonus, 5, new ColorRGBA(0.922f, 0.027f, 0.027f, 1f));
            bonusText.move(individualStat.getWidth() + 48.5f, 4f, 0f); 
            individualStat.attachChild(bonusText);
        }
    }
    
    public static List<Container> getItems(TangibleUnit tu, AssetManager ass) {
        List<Container> itms = new ArrayList<>();
        for (int i = 0; i < tu.getInventory().getItems().size(); i++) {
                TrueTypeKeyBMP bmp = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular.ttf", Style.Plain, (35 + i));
                TrueTypeFont ttf = (TrueTypeBMP)ass.loadAsset(bmp);
                ttf.setScale((20f / (35f + i)));
            
                EditedLabel itemName = new EditedLabel(tu.getInventory().getItems().get(i).getName(), ttf);
                itemName.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Center);
                float xDisplace = 85f - (itemName.text.getTTFNode().getWidth() / 2f);
                itemName.text.getTTFNode().move(xDisplace, 0f, 0); //the width of the container is 230f
                
                itemName.scale(0.45f);
            
                Container master = new Container();
                ((TbtQuadBackgroundComponent)master.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
                master.addChild(itemName);
                
                master.scale(0.8f);
            
                if (tu.getInventory().getItems().get(i) instanceof Weapon) {
                    Panel icon = new Panel(24f, 24f);
                    icon.setBackground(new QuadBackgroundComponent(ass.loadTexture("Interface/GUI/icons/item_and_formula/" + ((Weapon)tu.getInventory().getItems().get(i)).getWeaponData().getType() + ".png")));
                    icon.move(5 + xDisplace * -1f, 5f, 0);
                    itemName.text.getTTFNode().attachChild(icon);
                
                    TrueTypeKeyBMP bmp2 = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular2.ttf", Style.Plain, (25 + i));
                    TrueTypeFont ttf2 = (TrueTypeBMP)ass.loadAsset(bmp2);
                    ttf2.setScale((18f / (25f + i)));
                    TrueTypeNode durability = ttf2.getText("" + ((Weapon)tu.getInventory().getItems().get(i)).getCurrentDurability(), 3, ColorRGBA.White);
                    durability.move((xDisplace * -1f) + 150, -2.5f, 0);
                    itemName.text.getTTFNode().attachChild(durability);
                } else if (tu.getInventory().getItems().get(i) instanceof ConsumableItem) {
                    Panel icon = new Panel(24f, 24f);
                    icon.setBackground(new QuadBackgroundComponent(ass.loadTexture(((ConsumableItem)tu.getInventory().getItems().get(i)).getIconPath())));
                    icon.move(5 + xDisplace * -1f, 5f, 0);
                    itemName.text.getTTFNode().attachChild(icon);
                
                    TrueTypeKeyBMP bmp2 = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular2.ttf", Style.Plain, (25 + i));
                    TrueTypeFont ttf2 = (TrueTypeBMP)ass.loadAsset(bmp2);
                    ttf2.setScale((18f / (25f + i)));
                    TrueTypeNode uses = ttf2.getText("" + ((ConsumableItem)tu.getInventory().getItems().get(i)).getCurrentUses(), 3, ColorRGBA.White);
                    uses.move((xDisplace * -1f) + 160, -2.5f, 0);
                    itemName.text.getTTFNode().attachChild(uses);
                }
            
                itms.add(master);
        }
        return itms;
    }
    
    public static List<Container> getFormulas(TangibleUnit tu, AssetManager ass) {
        List<Container> itms = new ArrayList<>();
        for (int i = 0; i < tu.getFormulaManager().getEquipped().size(); i++) {
                TrueTypeKeyBMP bmp = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular.ttf", Style.Plain, (46 + i));
                TrueTypeFont ttf = (TrueTypeBMP)ass.loadAsset(bmp);
                ttf.setScale((20f / (46f + i)));
            
                EditedLabel formulaName = new EditedLabel(tu.getFormulaManager().getEquipped().get(i).getName(), ttf);
                formulaName.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Center);
                float xDisplace = 115f - (formulaName.text.getTTFNode().getWidth() / 2f);
                formulaName.text.getTTFNode().move(xDisplace, 0f, 0); //the width of the container is 230f
                
                formulaName.scale(0.45f);
            
                Container master = new Container();
                ((TbtQuadBackgroundComponent)master.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
                master.addChild(formulaName);
                
                master.scale(0.8f);

                Panel icon = new Panel(20f, 20f);
                icon.setBackground(new QuadBackgroundComponent(ass.loadTexture("Interface/GUI/icons/item_and_formula/" + (tu.getFormulaManager().getEquipped().get(i)).getActualFormulaData().getType() + ".png")));
                icon.move(5f + xDisplace * -1f, 0, 0);
                formulaName.text.getTTFNode().attachChild(icon);
                
                TrueTypeKeyBMP bmp2 = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular2.ttf", Style.Plain, (36 + i));
                TrueTypeFont ttf2 = (TrueTypeBMP)ass.loadAsset(bmp2);
                ttf2.setScale((20f / (36f + i)));
                
                ColorRGBA colorType;
                int cost;
                if (tu.getFormulaManager().getEquipped().get(i).getTPUsage() > 0) {
                    colorType = new ColorRGBA(0.85f, 0.36f, 0.83f, 1f);
                    cost = tu.getFormulaManager().getEquipped().get(i).getTPUsage();
                } else {
                    colorType = new ColorRGBA(1, 0.76f, 0, 1);
                    cost = tu.getFormulaManager().getEquipped().get(i).getHPUsage();
                }
                
                TrueTypeNode costValue = ttf2.getText("" + cost, 3, colorType);
                costValue.move(formulaName.text.getTTFNode().getWidth() + 15, 0f, 0);
                formulaName.text.getTTFNode().attachChild(costValue);
            
                itms.add(master);
        }
        return itms;
    }
    
    public static List<Container> getSkills(TangibleUnit tu, AssetManager ass) {
        List<Container> skls = new ArrayList<>();
        for (int i = 0; i < tu.getSkills().size(); i++) {
                TrueTypeKeyBMP bmp = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular3.ttf", Style.Plain, (46 + i));
                TrueTypeFont ttf = (TrueTypeBMP)ass.loadAsset(bmp);
                ttf.setScale((20f / (46f + i)));
                
                EditedLabel skillName = new EditedLabel(tu.getSkills().get(i).getName(), ttf);
                skillName.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Center);
                float xDisplace = 130f - (skillName.text.getTTFNode().getWidth() / 2f);
                skillName.text.getTTFNode().move(xDisplace, 0f, 0); //the width of the container is 230f
                
                skillName.scale(0.45f);
                
                Container master = new Container();
                ((TbtQuadBackgroundComponent)master.getBackground()).setColor(new ColorRGBA(1, 1, 1, 0));
                master.addChild(skillName);
                
                master.scale(0.8f);
            
                Panel icon = new Panel(35f, 35f);
                icon.setBackground(new QuadBackgroundComponent(ass.loadTexture(tu.getSkills().get(i).getPath())));
                icon.move(10f + xDisplace * -1f, 7.5f, 0);
                skillName.text.getTTFNode().attachChild(icon);
                
                TrueTypeKeyBMP bmp2 = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular3.ttf", Style.Plain, (36 + i));
                TrueTypeFont ttf2 = (TrueTypeBMP)ass.loadAsset(bmp2);
                ttf2.setScale((20f / (36f + i)));
                
                ColorRGBA colorType;
                int cost;
                switch (tu.getSkills().get(i).getToll().getType()) {
                    case TP:
                        colorType = new ColorRGBA(0.85f, 0.36f, 0.83f, 1f);
                        break;
                    case HP:
                        colorType = new ColorRGBA(1, 0.76f, 0, 1);
                        break;
                    default:
                        //weapon durability
                        colorType = ColorRGBA.White;
                        break;
                }
                cost = tu.getSkills().get(i).getToll().getValue();
                
                TrueTypeNode costValue = ttf2.getText("" + cost, 3, colorType);
                costValue.move(skillName.text.getTTFNode().getWidth() + 15, 0f, 0);
                skillName.text.getTTFNode().attachChild(costValue);
                
                skls.add(master);
        }
        
        return skls;
    }
 
    private class Cosa {
        private final String block, description;
        private final Panel cont;
        
        public Cosa(Panel C, String bl, String desc) {
            cont = C;
            block = bl;
            description = desc;
        }
    
        public Panel getContainer() { return cont; }
        public String getBlock() { return block; }
        public String getDescription() { return description; }
    }
}
