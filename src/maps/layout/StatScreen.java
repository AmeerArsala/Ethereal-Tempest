/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout;

import battle.item.ConsumableItem;
import battle.Toll;
import battle.Toll.Exchange;
import battle.item.Weapon;
import com.atr.jme.font.TrueTypeBMP;
import com.atr.jme.font.TrueTypeFont;
import com.atr.jme.font.asset.TrueTypeKeyBMP;
import com.atr.jme.font.shape.TrueTypeNode;
import com.atr.jme.font.util.StringContainer;
import com.atr.jme.font.util.Style;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.TabbedPanel;
import com.simsilica.lemur.TabbedPanel.Tab;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.component.TbtQuadBackgroundComponent;
import com.simsilica.lemur.core.GuiComponent;
import com.simsilica.lemur.style.Attributes;
import com.simsilica.lemur.style.Styles;
import edited.CustomProgressBar;
import edited.EditedLabel;
import edited.EditedTextField;
import edited.LoadedCircle;
import java.util.ArrayList;
import java.util.List;
import etherealtempest.FSM;
import etherealtempest.FSM.EntityState;
import etherealtempest.FsmState;
import etherealtempest.MasterFsmState;
import fundamental.StatBundle;
import general.visual.RadialProgressBar;

/**
 *
 * @author night
 */
public class StatScreen extends Node {
    private AssetManager assetManager;
    protected RadialProgressBar[] statProgress = new RadialProgressBar[9];
    protected RadialProgressBar expbar;
    protected Container menu, specialNode, info;
    
    private final Quad special = new Quad(130f, 130f), transitionAnimation = new Quad(254f, 335.5f);
    protected Geometry specialGeo = new Geometry("special", special), transitionAnim = new Geometry("transitionAnimation", transitionAnimation);
    private TrueTypeNode strikesTillParry;
    
    QuadBackgroundComponent nothing, cursorColor;
    Material sword;
    ColorRGBA defaultBGColor;
    
    private EditedTextField stuff;
    
    private ArrayList<ArrayList<Cosa>> namedElements;
    private ArrayList<Cosa> switchedElements;
    
    private int currentX = 0, currentY = 0, switchedX = 0, switchedY = 0; //switchedX represents the block, switchedY represents the item #
    
    private boolean tabsSwitched = false;
    private TabbedPanel itemsOrSkills, formulasOrAbilities;
    
    private final FSM fsm = new FSM() {
        @Override
        public void setNewStateIfAllowed(FsmState st) {
            state = st; //change later maybe
        }
    
    };
    
    enum TransitionState {
        Transition,
        TransitionBack,
        Standby,
        FinishedForward,
        Off
    }
    
    TransitionState currentTransitionState = TransitionState.Off;
    
    public StatScreen(AssetManager AM) {
        assetManager = AM;
        fsm.setNewStateIfAllowed(new FsmState(EntityState.GuiClosed));
    }
    
    public FsmState getState() {
        return fsm.getState();
    }
    
    public void setStateIfAllowed(FsmState newState) {
        fsm.setNewStateIfAllowed(newState);
    }
    
    public void forceState(FsmState newState) {
        fsm.forceState(newState);
    }
    
    public void resolveInput(String name, float tpf) {
        System.out.println(fsm.getState().getEnum());
        if (name.equals("deselect")) {
            if (fsm.getState().getEnum() == EntityState.StatScreenOpened) {
                //expbar.reset();
                expbar.getChildrenNode().detachAllChildren();
                expbar.removeFromParent();
                
                for (RadialProgressBar RPB : statProgress) {
                    RPB.setLocalTranslation(0, 0, 0);
                    RPB.getChildrenNode().detachAllChildren();
                    RPB.removeFromParent();
                }
                
                detachChild(menu);
                fsm.setNewStateIfAllowed(new FsmState(EntityState.GuiClosed));
            } else if (fsm.getState().getEnum() == EntityState.StatScreenSelecting) {
                //do the closing animation of side tab (reverse of open)
                currentTransitionState = TransitionState.TransitionBack;
                
                tryToggling(false);
                //fsm.setNewStateIfAllowed(new FsmState(EntityState.StatScreenOpened));
                info.clearChildren();
                //((QuadBackgroundComponent)info.getBackground()).setTexture(assetManager.loadTexture("Interface/GUI/general_ui/nothing.png"));
                //info.setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/general_ui/nothing.png")));
                
            }
        } 
        if (name.equals("open unit info menu")) {
            if (fsm.getState().getEnum() != EntityState.StatScreenOpened || fsm.getState().getEnum() != EntityState.StatScreenSelecting) {
                fsm.setNewStateIfAllowed(new FsmState(EntityState.StatScreenOpened));
            }
        }
        if (name.equals("select") && fsm.getState().getEnum() != EntityState.StatScreenSelecting) {
            //do the opening animation of side tab
            currentTransitionState = TransitionState.Transition;
            
            resetPos();
            fsm.setNewStateIfAllowed(new FsmState(EntityState.StatScreenSelecting));
            //((QuadBackgroundComponent)info.getBackground()).setTexture(assetManager.loadTexture("Interface/GUI/general_ui/tab.png"));
            //info.setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/general_ui/tab.png")));
            stuff.setText(namedElements.get(currentX).get(currentY).getDescription());
            
            info.addChild(stuff);
            
            moveY(0);
        }
        if (name.equals("move up") && fsm.getState().getEnum() == EntityState.StatScreenSelecting) {
            moveY(-1);
            //moveY(0);
            stuff.setText(namedElements.get(currentX).get(currentY).getDescription());
        }
        if (name.equals("move down") && fsm.getState().getEnum() == EntityState.StatScreenSelecting) {
            moveY(1);
            //moveY(0);
            stuff.setText(namedElements.get(currentX).get(currentY).getDescription());
        }
        if (name.equals("move left") && fsm.getState().getEnum() == EntityState.StatScreenSelecting) {
            moveX(-1);
            //moveY(0);
            stuff.setText(namedElements.get(currentX).get(currentY).getDescription());
        }
        if (name.equals("move right") && fsm.getState().getEnum() == EntityState.StatScreenSelecting) {
            moveX(1);
            //moveY(0);
            stuff.setText(namedElements.get(currentX).get(currentY).getDescription());
        }
        if ((name.equals("bump left") || name.equals("bump right")) && (fsm.getState().getEnum() == EntityState.StatScreenOpened || fsm.getState().getEnum() == EntityState.StatScreenSelecting)) {
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
            
            //swapValues(namedElements.get(1), switchedElements);
            ArrayList<Cosa> temp = (ArrayList<Cosa>)namedElements.get(1).clone();
            namedElements.set(1, (ArrayList<Cosa>)switchedElements.clone());
            switchedElements = temp;
            
            if (fsm.getState().getEnum() == EntityState.StatScreenSelecting) {
                moveY(0);
                stuff.setText(namedElements.get(currentX).get(currentY).getDescription());
            }
        }
        System.out.println("(" + currentX + ", " + currentY + ")");
    }
    
    int frameTrans = 0;
    
    public void update(float tpf) {
        //System.out.println(currentTransitionState);
        //System.out.println("switchedX = " + switchedX + ", switchedY = " + switchedY);
        //System.out.println("currentX = " + currentX + ", currentY = " + currentY);
        
        if (currentTransitionState == TransitionState.TransitionBack) {
            if (frameTrans > 0) {
                sword.setTexture("ColorMap", assetManager.loadTexture("Interface/GUI/general_ui/transition/" + frameTrans + ".png"));
                frameTrans -= 3;
            } else {
                frameTrans = 0;
                fsm.setNewStateIfAllowed(new FsmState(EntityState.StatScreenOpened));
                currentTransitionState = TransitionState.Standby;
            }
        }
        
        switch (fsm.getState().getEnum()) {
            case StatScreenOpened: 
            {
                specialNode.rotate(0, 0, (FastMath.PI / -240f));
                sword.setTexture("ColorMap", assetManager.loadTexture("Interface/GUI/general_ui/transition/default.png"));
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
                        sword.setTexture("ColorMap", assetManager.loadTexture("Interface/GUI/general_ui/transition/" + frameTrans + ".png"));
                        frameTrans += 3;
                        break;
                    case Standby:
                        sword.setTexture("ColorMap", assetManager.loadTexture("Interface/GUI/general_ui/transition/default.png"));
                        break;
                    case FinishedForward:
                        sword.setTexture("ColorMap", assetManager.loadTexture("Interface/GUI/general_ui/transition/done.png"));
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
        for (ArrayList<Cosa> column : namedElements) {
            for (int x = 0; x < column.size() * 2; x++) {
                moveY(1);
                moveY(0);
            }
            moveX(1);
        }
        resetPos();
    }
    
    private int translationYfromX(int direction) {
        if (currentX == 1 && currentY > 1) {
            if (direction > 0) {
                return 1;
            } else if (direction < 0) {
                if (currentY <= 1) {
                    return 0;
                } else if (currentY <= 3) {
                    if (
                            (currentY == 2 && namedElements.get(1).get(2).getBlock().equals("i0")) 
                            || (currentY == 3 && namedElements.get(1).get(3).getBlock().equals("i1"))
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
                if (currentY == 0) {
                    return 2;
                }
                if (currentY == 1) {
                    if (namedElements.get(1).size() >= 4) {
                        if (namedElements.get(1).size() >= 5) {
                            return 4;
                        }
                        return 3;
                    }
                    return 2;
                }
                if (currentY == 2) {
                    return namedElements.get(1).size() - 1;
                }
            } 
            if (direction < 0) {
                if (currentY == 0) {
                    return 0;
                }
                if (currentY == 1) {
                    return 2;
                }
                return 5;
            }
        }
        if (currentX == 4) {
            if (direction > 0) {
                if (currentY <= 4) {
                    if (currentY <= 2) {
                        return 0;
                    } else { return 1; }
                } else { return 2; }
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
    
    public Cosa currentElement() {
        return namedElements.get(currentX).get(currentY);
    }
    
    public void toggleCursorAtCurrentPos(boolean using) {
        if (using) {
            if (currentX != 1 || currentY > 1) {
                try {
                    ((QuadBackgroundComponent)currentElement().getContainer().getBackground()).setTexture(assetManager.loadTexture("Interface/GUI/general_ui/hovered.png"));
                }
                catch (Exception e) {
                    ((TbtQuadBackgroundComponent)currentElement().getContainer().getBackground()).setTexture(assetManager.loadTexture("Interface/GUI/general_ui/hovered.png"));
                }
            } else {
                ((TbtQuadBackgroundComponent)currentElement().getContainer().getBackground()).setColor(new ColorRGBA(1, 1, 1, 1));
            }
        } else {
            if (currentX != 1 || currentY > 1) {
                try {
                    currentElement().getContainer().setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/general_ui/nothing.png")));
                }
                catch (Exception e) {
                    try {
                        ((QuadBackgroundComponent)currentElement().getContainer().getBackground()).setTexture(assetManager.loadTexture("Interface/GUI/general_ui/nothing.png"));
                    }
                    catch (Exception e2) {
                        ((TbtQuadBackgroundComponent)currentElement().getContainer().getBackground()).setTexture(assetManager.loadTexture("Interface/GUI/general_ui/nothing.png"));
                    }
                }
            } else {
                //it's the hp or tp bar
                try {
                    ((QuadBackgroundComponent)currentElement().getContainer().getBackground()).setColor(ColorRGBA.Black);
                }
                catch (Exception e3) {
                    ((TbtQuadBackgroundComponent)currentElement().getContainer().getBackground()).setColor(ColorRGBA.Black);
                }
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
        
        //exp circle
        expbar = new RadialProgressBar(52.5f, 63.75f, new ColorRGBA(0.012f, 0.58f, 0.988f, 1f), 2);
        expbar.move(90f, -125f, 0);
        /*TrueTypeKeyBMP xp = new TrueTypeKeyBMP("Interface/Fonts/Neuton-Italic.ttf", Style.Plain, 28);
        TrueTypeFont xpfont = (TrueTypeBMP)assetManager.loadAsset(xp);
        
        TrueTypeKeyBMP fake = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular.ttf", Style.Bold, 28);
        TrueTypeFont fakefont = (TrueTypeBMP)assetManager.loadAsset(fake);
        
        expbar = new LoadedCircle(70.0f, 85.0f);
        expbar.move(90f, -125f, 0);
        
        expbar.setFont(fakefont);
        expbar.setFont2(xpfont);*/
        
        //stat circles
        for (int i = 0; i < statProgress.length; i++) {
            TrueTypeKeyBMP bmp = new TrueTypeKeyBMP("Interface/Fonts/Montaga-Regular.ttf", Style.Plain, (35 + i));
            TrueTypeFont ttf = (TrueTypeBMP)assetManager.loadAsset(bmp);
            ttf.setScale(28f / (35f + i));
            
            statProgress[i] = new RadialProgressBar(20.7f, 24f, new ColorRGBA(0.98f, 0.557f, 0f, 1f), 1);
            //statProgress[i].getChildrenNode().attachChild()
            /*statProgress[i] = new LoadedCircle(23f, 25f);
            statProgress[i].setForegroundColor(new ColorRGBA(0.98f, 0.557f, 0f, 1f));
            statProgress[i].setFont(ttf);
            statProgress[i].setSingleText("");*/
        }
        
        TrueTypeKeyBMP amountLeft = new TrueTypeKeyBMP("Interface/Fonts/Cinzel/Cinzel-Bold.ttf", Style.Plain, 50);
        TrueTypeFont parryFont = (TrueTypeBMP)assetManager.loadAsset(amountLeft);
        
        strikesTillParry = parryFont.getText("3", 3, ColorRGBA.White); //CHANGE LATER
        strikesTillParry.move(0, 0, 5f);
        Material spMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        spMat.setTexture("ColorMap", assetManager.loadTexture("Interface/GUI/general_ui/specialborder.png"));
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
        
        nothing = new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/general_ui/nothing.png"), 0, 0);
        cursorColor = new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/general_ui/hovered.png"));
        
        sword = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        sword.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        transitionAnim.setMaterial(sword);
        //attachChild(transitionAnim);
        sword.setTexture("ColorMap", assetManager.loadTexture("Interface/GUI/general_ui/nothing.png"));
        transitionAnim.move(150f, -760f, 5f);
        
        info = new Container();
        info.setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/general_ui/tab.png")));
        info.move(80, -300, 1f);
        
        defaultBGColor = ((TbtQuadBackgroundComponent)new Container().getBackground()).getColor();
        //info.setPreferredSize(new Vector3f(0.3f, 0.5f, 1f));
    }
    
    //TODO: make all object declarations declared in the constructor or initialization or something
    public void startUnitViewGUI(TangibleUnit tu) {
        currentX = 0;
        currentY = 0;
        //load styling
        //Styles styles = GuiGlobals.getInstance().getStyles();
        //Attributes atts;
        
        //currentUnit = tu;
        namedElements = new ArrayList<>();
        ArrayList<Cosa> 
                col1 = new ArrayList<>(), 
                col2 = new ArrayList<>(),
                col3 = new ArrayList<>(),
                col4 = new ArrayList<>(),
                col5 = new ArrayList<>();
        
        switchedElements = new ArrayList<>();
        
        TrueTypeKeyBMP bitmap = new TrueTypeKeyBMP("Interface/Fonts/Montaga-Regular.ttf", Style.Plain, 28);
        TrueTypeFont ttf = (TrueTypeBMP)assetManager.loadAsset(bitmap);

        //Create a panel for menu
        menu = new Container(new BoxLayout(Axis.X, FillMode.None));
        //TbtQuadBackgroundComponent qb = new TbtQuadBackgroundComponent(new TbtQuad(1100f, 643.9f), assetManager.loadTexture("Textures/gui/unitwindowbg.jpg"));
        QuadBackgroundComponent qb = new QuadBackgroundComponent(assetManager.loadTexture("Textures/gui/altbg.png"), 0, 0);
        qb.setAlpha(0.88f);
        QuadBackgroundComponent qbtrans = new QuadBackgroundComponent(assetManager.loadTexture("Textures/gui/unitwindowbg.jpg"), 0, 0);
        qbtrans.setAlpha(0f);
        //qb.setZOffset(-3f);
        menu.setBackground(qbtrans);
        
        //horizontal layout
        BoxLayout hl = new BoxLayout(Axis.X, FillMode.None); 
        
        //Create a container
        Container unitWindowX = new Container(hl);
        unitWindowX.setBackground(qb);
        
        //horizonal layout attach
        menu.addChild(unitWindowX);
        
        Container dontstretchportrait = new Container(new BoxLayout(Axis.Y, FillMode.None));
        dontstretchportrait.setInsets(new Insets3f(0, 0, 0, 35));
        
        Panel portrait = new Panel(241.46f, 241.46f);
        portrait.setBorder(new QuadBackgroundComponent(assetManager.loadTexture("Textures/gui/portraitbg.png")));
        portrait.setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Textures/portraits/" + tu.getName() + ".png")));
        portrait.setInsets(new Insets3f(0.1f, 0.1f, 0.1f, 0.1f));
        dontstretchportrait.addChild(portrait);
        dontstretchportrait.setBackground(qbtrans);
        
        TrueTypeKeyBMP statusload = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular.ttf", Style.Plain, 30);
        TrueTypeFont statusfont = (TrueTypeBMP)assetManager.loadAsset(statusload);
        statusfont.setScale(0.6f);
        EditedLabel extraInfo = new EditedLabel("    Class: " + tu.clName() + "\n    Status: " + tu.ustatus, statusfont);
        Container unitInfoEX = new Container(new BoxLayout(Axis.Y, FillMode.None));
        unitInfoEX.addChild(extraInfo);
        unitInfoEX.addChild(new Label(" "));
        unitInfoEX.setInsets(new Insets3f(20, 15, 0, 15));
        
        ((TbtQuadBackgroundComponent)unitInfoEX.getBackground()).setTexture(assetManager.loadTexture("Interface/GUI/general_ui/nothing.png"));
        
        dontstretchportrait.addChild(unitInfoEX);
        col1.add(new Cosa(unitInfoEX, "00", "The user's class determines the weapons they can wield, which stats are more likely to increase during a level up, possible skills, possible talents, possible abilities, has an effect on stats, and the user's general role in combat.\nTheir status takes status effects into account (such as being poisoned) and will display them accordingly."));
        
        
        Container bstats = new Container(new BoxLayout(Axis.Y, FillMode.None));
        
        TrueTypeKeyBMP qrbmp2 = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Bold.ttf", Style.Plain, 44);
        TrueTypeFont qrbmpfont2 = (TrueTypeBMP)assetManager.loadAsset(qrbmp2);
        qrbmpfont2.setScale(30f / 44f);
        EditedLabel battleStatTitle = new EditedLabel("Battle Stats", qrbmpfont2);
        bstats.addChild(battleStatTitle);
        battleStatTitle.text.getTTFNode().move(15f, -15f, 0f);
         
        TrueTypeKeyBMP qrbmp3 = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular.ttf", Style.Plain, 43);
        TrueTypeFont qrbmpfont3 = (TrueTypeBMP)assetManager.loadAsset(qrbmp3);
        qrbmpfont3.setScale(25f / 43f);
        EditedLabel battleStats = new EditedLabel(
                  "ATK PWR: " + tu.getATK() + "\n"
                + "ACC: " + tu.getAccuracy() + "\n"
                + "EVA: " + tu.getEvasion() + "\n"
                + "CRIT: " + tu.getCrit() + "\n"
                + "ADRENALINE: " + tu.getADRENALINE(), qrbmpfont3);
        bstats.addChild(battleStats);
        battleStats.text.getTTFNode().move(15f, -29.5f, 0f);
       
       Label klabel = new Label(" ");
       klabel.setInsets(new Insets3f(150f, 0, 0, 0));
       bstats.addChild(klabel);
       
       bstats.setInsets(new Insets3f(15f, 3.5f, 0, 0));
       //bstats.setBackground(nothing);
       ((TbtQuadBackgroundComponent)bstats.getBackground()).setTexture(assetManager.loadTexture("Interface/GUI/general_ui/nothing.png"));
       bstats.setBorder(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/general_ui/default.png")));
       
       dontstretchportrait.addChild(bstats);
       col1.add(new Cosa(bstats, "01", "ATK PWR: if attacking physically, calculated by STR + equippped weapon's Pow. If attacking with ether, calculated by ETHER + equipped formula's Pow.\nACC: base accuracy\nEVA: base evasion.\nCRIT: base chance of dealing a critical hit against an enemy.\nADRENALINE: slightly increases CRIT but mainly affects crit damage"));
        
        Container formationPanel = new Container(new BoxLayout(Axis.Y, FillMode.None));
        formationPanel.setBackground(nothing);
        formationPanel.setBorder(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/general_ui/default.png")));
        
        TrueTypeKeyBMP titleformation = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Bold.ttf", Style.Plain, 51);
        TrueTypeFont formationttf = (TrueTypeBMP)assetManager.loadAsset(titleformation);
        formationttf.setScale(30f / 51f);
        EditedLabel formationLabel = new EditedLabel("Formation", formationttf);
        formationLabel.text.getTTFNode().move(37.5f, -15f, 0f);
        formationPanel.addChild(formationLabel);
        
        dontstretchportrait.addChild(formationPanel);
        formationPanel.setInsets(new Insets3f(15f, 5f, 0f, 0f));
        if (tu.getFormations().get(0).doesExist()) {
            Container equippedFormation = new Container();
            //equippedFormation.setBackground(nothing);
            ((TbtQuadBackgroundComponent)equippedFormation.getBackground()).setTexture(assetManager.loadTexture("Interface/GUI/general_ui/nothing.png"));
            equippedFormation.setBorder(new QuadBackgroundComponent(assetManager.loadTexture(tu.getFormations().get(0).getPath())));
            equippedFormation.addChild(new Label("\n\n\n\n\n\n"));
            
            formationPanel.addChild(equippedFormation);
            equippedFormation.setInsets(new Insets3f(30f, 12, 0, 6));
            formationPanel.addChild(new Label("\n"));
            
            col1.add(new Cosa(equippedFormation, "fm", tu.getFormations().get(0).getDescription()));
        } else { formationPanel.addChild(new Label("\n\n\n\n\n\n\n\n\n\n")); }
        
        unitWindowX.addChild(dontstretchportrait);
        
        Container cellB = new Container(new BoxLayout(Axis.Y, FillMode.None));
        cellB.setBackground(qbtrans);
        
        TrueTypeKeyBMP immortal = new TrueTypeKeyBMP("Interface/Fonts/Cinzel_Decorative/CinzelDecorative-Bold.ttf", Style.Plain, 28);
        TrueTypeFont immortalfont = (TrueTypeBMP)assetManager.loadAsset(immortal);
        
        TrueTypeKeyBMP immortal2 = new TrueTypeKeyBMP("Interface/Fonts/Cinzel_Decorative/CinzelDecorative-Bold.ttf", Style.Plain, 30);
        TrueTypeFont immortalfont2 = (TrueTypeBMP)assetManager.loadAsset(immortal2);
        
        EditedLabel nametag = new EditedLabel(tu.getName(), immortalfont, ColorRGBA.White);
        EditedLabel nametagOutline = new EditedLabel(tu.getName(), immortalfont2, ColorRGBA.Black, 0);
        
        Panel nmtg = new Panel(62.6f, 216f / 2.3f);
        nmtg.setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Textures/gui/emptyname.png")));
        nmtg.attachChild(nametag);
        nmtg.attachChild(nametagOutline);
        float dx = 140f - (nametag.text.getTTFNode().getWidth() / 2f), dx2 = 140f - (nametagOutline.text.getTTFNode().getWidth() / 2f);
        float dHeight = (nametagOutline.text.getTTFNode().getHeight() - nametag.text.getTTFNode().getHeight()) / 2f, dWidth = (nametagOutline.text.getTTFNode().getWidth() - nametag.text.getTTFNode().getWidth()) / 2f;
        nametag.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Center);
        nametagOutline.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Center);
        nametag.text.getTTFNode().move(dx2 + dWidth, -28f - dHeight, 0.1f);
        nametagOutline.text.getTTFNode().move(dx2, -28f, 0.05f);
        nametag.setFontSize(20f);
        
        cellB.addChild(nmtg);
        
        TrueTypeKeyBMP bars = new TrueTypeKeyBMP("Interface/Fonts/Montaga-Regular.ttf", Style.Bold, 28);
        TrueTypeFont barsfont = (TrueTypeBMP)assetManager.loadAsset(bars);
        barsfont.setScale(0.7f);
        CustomProgressBar 
                hpBar = new CustomProgressBar(barsfont), //new ElementId("HP")
                tpBar = new CustomProgressBar(barsfont); //new ElementId("TP")
        
                hpBar.setMessage("HP: " + tu.currentHP + "/" + tu.getMaxHP(), ColorRGBA.Black);
                hpBar.setProgressPercent(((double)tu.currentHP / tu.getMaxHP()));
                
                tpBar.setMessage("TP: " + tu.currentTP + "/" + tu.getMaxTP(), ColorRGBA.Black);
                tpBar.setProgressPercent(((double)tu.currentTP / tu.getMaxTP()));
                
                hpBar.setBarColor(new ColorRGBA(0, 0.76f, 0, 1));
                tpBar.setBarColor(new ColorRGBA(0.85f, 0.36f, 0.83f, 1f));
        
        //atts = styles.getSelector("progress", "label", "glass");
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
        Label nothing2 = new Label("  ");
        nothing2.setFontSize(3f);
        cellB.addChild(nothing2);
        cellB.addChild(tpborder);
        
        //inventory/items
        Container itemsPanel = new Container(new BoxLayout(Axis.Y, FillMode.None));
        ((TbtQuadBackgroundComponent)itemsPanel.getBackground()).setColor(new ColorRGBA(1f, 1f, 1f, 0f));
        itemsPanel.setBorder(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/general_ui/invborder3.png")));
        
        addItems(tu, itemsPanel, col2);
        
        //skills
        Container skillsPanel = new Container(new BoxLayout(Axis.Y, FillMode.None));
        ((TbtQuadBackgroundComponent)skillsPanel.getBackground()).setColor(new ColorRGBA(1f, 1f, 1f, 0f));
        skillsPanel.setBorder(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/general_ui/skillsbackdrop2.png")));
        addSkills(tu, skillsPanel, switchedElements);
        
        itemsOrSkills = new TabbedPanel();
        itemsOrSkills.addTab("                   \n                     ", itemsPanel);
        itemsOrSkills.getSelectedTab().getTitleButton().setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/general_ui/itemslogo.png")));
        itemsOrSkills.getSelectedTab().getTitleButton().setBorder(new Container().getBackground());
        itemsOrSkills.getSelectedTab().getTitleButton().setInsets(new Insets3f(15, 45, 3, 15));
        
        //change to skillsPanel
        itemsOrSkills.addTab("                       \n                       ", skillsPanel);
        itemsOrSkills.getTabs().get(1).getTitleButton().setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/general_ui/skillslogo.png")));
        itemsOrSkills.getTabs().get(1).getTitleButton().setBorder(new Container().getBackground());
        itemsOrSkills.getTabs().get(1).getTitleButton().setInsets(new Insets3f(15, 30, 3, 0));
        
        ((TbtQuadBackgroundComponent)itemsOrSkills.getSelectedTab().getTitleButton().getBorder()).setColor(new ColorRGBA(0, 0.839f, 0.871f, 1));

        cellB.addChild(itemsOrSkills);
        
        //formulas
        Container formulasPanel = new Container(new BoxLayout(Axis.Y, FillMode.None));
        ((TbtQuadBackgroundComponent)formulasPanel.getBackground()).setColor(new ColorRGBA(1f, 1f, 1f, 0f));
        formulasPanel.setBorder(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/general_ui/formulaborder.png")));
        
        addFormulas(tu, formulasPanel, col2);
        
        //abilities
        Container abilitiesPanel = new Container(new BoxLayout(Axis.Y, FillMode.None));
        ((TbtQuadBackgroundComponent)abilitiesPanel.getBackground()).setColor(new ColorRGBA(1f, 1f, 1f, 0f));
        abilitiesPanel.setBorder(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/general_ui/default.png")));
        
        addAbilities(tu, abilitiesPanel, switchedElements);
        
        formulasOrAbilities = new TabbedPanel();
        formulasOrAbilities.addTab("                        \n                                     ", formulasPanel);
        formulasOrAbilities.getSelectedTab().getTitleButton().setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/general_ui/formulaslogo.png")));
        formulasOrAbilities.getSelectedTab().getTitleButton().setBorder(new Container().getBackground());
        formulasOrAbilities.getSelectedTab().getTitleButton().setInsets(new Insets3f(15, 5, 3, 15));
        
        //change to abilitiesPanel
        formulasOrAbilities.addTab("                               \n                             ", abilitiesPanel);
        formulasOrAbilities.getTabs().get(1).getTitleButton().setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/general_ui/abilitieslogo.png")));
        formulasOrAbilities.getTabs().get(1).getTitleButton().setBorder(new Container().getBackground());
        formulasOrAbilities.getTabs().get(1).getTitleButton().setInsets(new Insets3f(15, 30, 3, 0));
        
        ((TbtQuadBackgroundComponent)formulasOrAbilities.getSelectedTab().getTitleButton().getBorder()).setColor(new ColorRGBA(0, 0.839f, 0.871f, 1));
        
        cellB.addChild(formulasOrAbilities);
        
        unitWindowX.addChild(cellB);
        
        Container cellX = new Container(new BoxLayout(Axis.Y, FillMode.None));
        cellX.setBackground(qbtrans);
        
        ttf.setScale(0.77f);

            EditedLabel stats = new EditedLabel(
                             ("        STR: " + "\n\n" +
                              "        ETHER: " + "\n\n" + 
                              "        AGI: " + "\n\n" +
                              "        COMP: " + "\n\n" + 
                              "        DEX: " + "\n\n" + 
                              "        DEF: " + "\n\n" +
                              "        RSL: " + "\n\n" +
                              "        MOBILITY: " + "\n\n" +
                              "        PHYSIQUE: "), ttf);
                stats.text.getTTFNode().move(-4f, -9f, 0f);
                stats.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Left);
                stats.setTextHAlignment(HAlignment.Right);
                styleStats(tu, stats.text.getTTFNode());
                
                Container statsCont = new Container();
                statsCont.addChild(stats);
                ((TbtQuadBackgroundComponent)statsCont.getBackground()).setTexture(assetManager.loadTexture("Interface/GUI/general_ui/nothing.png"));
        
        TrueTypeKeyBMP xp = new TrueTypeKeyBMP("Interface/Fonts/Neuton-Italic.ttf", Style.Plain, 25);
        TrueTypeFont xpfont = (TrueTypeBMP)assetManager.loadAsset(xp);
        TrueTypeNode xptext = xpfont.getText(" EXP:\n" + tu.currentEXP +"/100", 2, ColorRGBA.White);
        xptext.move(-40f, 35f, 3);
        expbar.getChildrenNode().attachChild(xptext);
        expbar.setCirclePercent(tu.currentEXP / 100f);
        
        Container expCont = new Container();
        //expCont.setBackground(nothing);
        ((TbtQuadBackgroundComponent)expCont.getBackground()).setTexture(assetManager.loadTexture("Interface/GUI/general_ui/nothing.png"));
        expCont.attachChild(expbar);
        
        col3.add(new Cosa(expCont, "xp", "The User's experience value. Once it reaches 100 the user will level up in which they gain stats and learn possible techniques."));
        
        TrueTypeKeyBMP lvl = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular.ttf", Style.Plain, 40);
        TrueTypeFont lvlfont = (TrueTypeBMP)assetManager.loadAsset(lvl);
        lvlfont.setScale(0.7f);
        TrueTypeNode levelLabel = lvlfont.getText("LVL " + tu.getLVL(), 3, ColorRGBA.White);
        unitWindowX.attachChild(levelLabel);
        levelLabel.move(605f, -20f, 5f);
        
        cellX.addChild(expCont);
        expCont.addChild(new Label("                          \n\n\n\n\n\n\n\n\n                                             ")); //used to be cellX
        expCont.setInsets(new Insets3f(20f, 10f, 20f, 25f));
        cellX.addChild(statsCont);
        statsCont.addChild(new Label(" \n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n")); //used to be cellX
        statsCont.setInsets(new Insets3f(5f, 5f, 5f, 135f));
        unitWindowX.addChild(cellX);
        
        col3.add(new Cosa(statsCont, "stats", "STR: physical strength.\nETHER: ether production.\nAGI: agility.\nCOMP: comprehension.\nDEX: dexterity.\nRSL: resilience (defense against ether)\nMOBILITY: how far the user can move\nPHYSIQUE: the user's available inventory space will decrease by 1 for every point of the inventory's combined weight - Physique."));
        
        Container strikesCont = new Container();
        strikesCont.attachChild(strikesTillParry);
        strikesCont.addChild(new Label("                                     \n\n\n\n\n\n                               "));
        ((TbtQuadBackgroundComponent)strikesCont.getBackground()).setTexture(assetManager.loadTexture("Interface/GUI/general_ui/nothing.png"));
        
        col4.add(new Cosa(strikesCont, "pm", "The user's parry cooldown. Once this number reaches 0 the user will automatically parry the next attack they receive such that they only receive 1 damage, even if it is a critical hit. The lower the ratio of the amount of enemy units that start on the map to the unit's COMP stat, the lower the cooldown will be for parrying."));
        
        Container cellStrikes = new Container(new BoxLayout(Axis.Y, FillMode.None));
        cellStrikes.setBackground(qbtrans);
        cellStrikes.addChild(strikesCont);
        strikesCont.setInsets(new Insets3f(62.5f, 0f, 30f, 0f));
        unitWindowX.addChild(cellStrikes);
        
        Container cellSave = new Container(new BoxLayout(Axis.Y, FillMode.None));
        cellSave.setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/general_ui/talentborder2.png")));
        Label control = new Label("                      ");
        control.setInsets(new Insets3f(3f, 80f, 0f, 0f));
        cellSave.setInsets(new Insets3f(3f, 10f, 0f, 0f)); //used to be 300f on the left inset
        
        cellSave.addChild(control);
        
        TrueTypeKeyBMP tlns = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Bold.ttf", Style.Plain, 48);
        TrueTypeFont tals = (TrueTypeBMP)assetManager.loadAsset(tlns);
        tals.setScale(5f/8f);
        EditedLabel talentslabel = new EditedLabel("  Talents", tals);
        talentslabel.text.getTTFNode().move(0, 5, 0);
        cellSave.addChild(talentslabel);
        addTalents(tu, cellSave, col5);
        
        unitWindowX.addChild(cellSave);
        
        namedElements.add(col1);
        namedElements.add(col2);
        namedElements.add(col3);
        namedElements.add(col4);
        namedElements.add(col5);
        
        attachChild(menu);
        cellSave.attachChild(transitionAnim);
        
        menu.addChild(info);
        
        stuff = new EditedTextField(namedElements.get(currentX).get(currentY).getDescription());
        stuff.setColor(ColorRGBA.White);
        stuff.setFont(assetManager.loadFont("Interface/Fonts/greco2.fnt"));
        stuff.setFontSize(25f);
        stuff.setSingleLine(false);
        ((QuadBackgroundComponent)stuff.getBackground()).setTexture(assetManager.loadTexture("Interface/GUI/general_ui/nothing.png"));
        stuff.setPreferredWidth(240f); //used to be 240f
        stuff.setInsets(new Insets3f(40f, 35f, 0, 30));
        stuff.getTextEntryComponent().getTextComponent().setLineWrapMode(LineWrapMode.Word);
        //stuff.setTextHAlignment(HAlignment.Center);
        
        initializeSizes();
        
        tryToggling(false);
    }
    
    protected void addAbilities(TangibleUnit tu, Container abilitiesPanel, ArrayList<Cosa> block2) {
        for (int i = 0; i < tu.getAbilities().size(); i++) {
            if (!tu.getAbilities().get(i).doesExist()) {
                Label placehold = new Label("                                                                                   ");
                placehold.setFontSize(11f - (2 * tu.getAmountExistingFormulas())); //the moment a unit gets 6 or more abilities, this will fail. Make it a scrolling view later
                abilitiesPanel.addChild(placehold);
                
                if (i == tu.getAbilities().size() - 1) {
                    placehold.setInsets(new Insets3f(160f, 0, 0, 0));
                }
                
            } else {
                TrueTypeKeyBMP bmp = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular3.ttf", Style.Plain, (35 + i));
                TrueTypeFont ttf = (TrueTypeBMP)assetManager.loadAsset(bmp);
                ttf.setScale((20f / (35f + i)));
            
                EditedLabel abilityName = new EditedLabel(tu.getInventory().getItems().get(i).getName(), ttf);
                abilityName.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Center);
                float xDisplace = 130f - (abilityName.text.getTTFNode().getWidth() / 2f);
                abilityName.text.getTTFNode().move(xDisplace, 0f, 0); //the width of the container is 230f
            
                Container master = new Container();
                ((TbtQuadBackgroundComponent)master.getBackground()).setTexture(assetManager.loadTexture("Interface/GUI/general_ui/nothing.png"));
                master.addChild(abilityName);
                if (i == 0) {
                    master.setInsets(new Insets3f(26f, 10, 0, 10));
                } else {
                    master.setInsets(new Insets3f(8.5f, 10, 0, 10));
                }
            
                block2.add(new Cosa(master, "aI" + i, tu.getInventory().getItems().get(i).getDescription()));
            
                abilitiesPanel.addChild(master);
            }
        }
    }
    
    protected void addSkills(TangibleUnit tu, Container skillsPanel, ArrayList<Cosa> block1) {
        for (int i = 0; i < tu.getSkills().size(); i++) {
            if (tu.getSkills().get(i).doesExist()) {
                TrueTypeKeyBMP bmp = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular3.ttf", Style.Plain, (46 + i));
                TrueTypeFont ttf = (TrueTypeBMP)assetManager.loadAsset(bmp);
                ttf.setScale((20f / (46f + i)));
                
                EditedLabel skillName = new EditedLabel(tu.getSkills().get(i).getName(), ttf);
                skillName.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Center);
                float xDisplace = 130f - (skillName.text.getTTFNode().getWidth() / 2f);
                skillName.text.getTTFNode().move(xDisplace, 0f, 0); //the width of the container is 230f
                
                Container master = new Container();
                ((TbtQuadBackgroundComponent)master.getBackground()).setTexture(assetManager.loadTexture("Interface/GUI/general_ui/nothing.png"));
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
            } else {
                Label placehold = new Label("                                                                                   ");
                skillsPanel.addChild(placehold);
                
                if (i == tu.getSkills().size() - 1) {
                    placehold.setInsets(new Insets3f(140f, 0, 0, 0));
                }
            }
        }
    }
    
    protected void addTalents(TangibleUnit tu, Container talentPanel, ArrayList<Cosa> col5) {
        for (int i = 0; i < tu.getTalents().size(); i++) {
            Container talentIcon = new Container();
             if (tu.getTalents().get(i).doesExist()) {
                talentIcon.setBorder(new QuadBackgroundComponent(assetManager.loadTexture(tu.getTalents().get(i).getIconPath())));
            } else {
                talentIcon.setBorder(new Container().getBackground());
            }
            Label placeholder = new Label(" ");
            talentIcon.addChild(placeholder);
            placeholder.setInsets(new Insets3f(62f, 0, 0, 0));
            ((TbtQuadBackgroundComponent)talentIcon.getBackground()).setTexture(assetManager.loadTexture("Interface/GUI/general_ui/nothing.png"));
            talentPanel.addChild(talentIcon);
            talentIcon.setInsets(new Insets3f(17.5f, 35f, 17.5f, 35f));
            
            col5.add(new Cosa(talentIcon, "t" + i, tu.getTalents().get(i).getDescription()));
        }
    }
    
    protected void addItems(TangibleUnit tu, Container itemPanel, ArrayList<Cosa> col2) {
        for (int i = 0; i < tu.getInventory().getItems().size(); i++) {
            TrueTypeKeyBMP bmp = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular.ttf", Style.Plain, (35 + i));
            TrueTypeFont ttf = (TrueTypeBMP)assetManager.loadAsset(bmp);
            ttf.setScale((20f / (35f + i)));
            
            EditedLabel itemName = new EditedLabel(tu.getInventory().getItems().get(i).getName(), ttf);
            itemName.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Center);
            float xDisplace = 130f - (itemName.text.getTTFNode().getWidth() / 2f);
            itemName.text.getTTFNode().move(xDisplace, 0f, 0); //the width of the container is 230f
            
            Container master = new Container();
            ((TbtQuadBackgroundComponent)master.getBackground()).setTexture(assetManager.loadTexture("Interface/GUI/general_ui/nothing.png"));
            master.addChild(itemName);
            if (i == 0) {
                master.setInsets(new Insets3f(26f, 10, 0, 10));
            } else {
                master.setInsets(new Insets3f(8.5f, 10, 0, 10));
            }
            
            if (tu.getInventory().getItems().get(i).doesExist()) { 
                col2.add(new Cosa
                    (master, "i" + i, 
                        tu.getInventory().getItems().get(i) instanceof Weapon ? ((Weapon)tu.getInventory().getItems().get(i)).getLoreDescription()
                        : tu.getInventory().getItems().get(i).getDescription()
                    )
                ); 
            }
            
            itemPanel.addChild(master);
            
            if (tu.getInventory().getItems().get(i) instanceof Weapon) {
                Panel icon = new Panel(24f, 24f);
                icon.setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/general_icons/" + ((Weapon)tu.getInventory().getItems().get(i)).getWeaponData().getType() + ".png")));
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
                icon.setBackground(new QuadBackgroundComponent(assetManager.loadTexture(((ConsumableItem)tu.getInventory().getItems().get(i)).getPath())));
                icon.move(25 + xDisplace * -1f, 5f, 0);
                itemName.text.getTTFNode().attachChild(icon);
                
                TrueTypeKeyBMP bmp2 = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular2.ttf", Style.Plain, (25 + i));
                TrueTypeFont ttf2 = (TrueTypeBMP)assetManager.loadAsset(bmp2);
                ttf2.setScale((18f / (25f + i)));
                TrueTypeNode uses = ttf2.getText("" + ((ConsumableItem)tu.getInventory().getItems().get(i)).getCurrentUses(), 3, ColorRGBA.White);
                uses.move((xDisplace * -1f) + 210, -2.5f, 0);
                itemName.text.getTTFNode().attachChild(uses);
            }
        }
        
        //Label la = new Label("                                                                                   ");
        //itemPanel.addChild(la);
    }
    
    protected void addFormulas(TangibleUnit tu, Container formulaPanel, ArrayList<Cosa> col2) {
        for (int i = 0; i < tu.getFormulas().size(); i++) {
            
            if (tu.getFormulas().get(i).doesExist()) {
                TrueTypeKeyBMP bmp = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular.ttf", Style.Plain, (46 + i));
                TrueTypeFont ttf = (TrueTypeBMP)assetManager.loadAsset(bmp);
                ttf.setScale((20f / (46f + i)));
                
                EditedLabel formulaName = new EditedLabel(tu.getFormulas().get(i).getName(), ttf);
                formulaName.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Center);
                float xDisplace = 130f - (formulaName.text.getTTFNode().getWidth() / 2f);
                formulaName.text.getTTFNode().move(xDisplace, 0f, 0); //the width of the container is 230f
                
                Container master = new Container();
                ((TbtQuadBackgroundComponent)master.getBackground()).setTexture(assetManager.loadTexture("Interface/GUI/general_ui/nothing.png"));
                master.addChild(formulaName);
                
                col2.add(new Cosa(master, "f" + i, tu.getFormulas().get(i).getDescription()));
                
                if (i == 0) {
                    master.setInsets(new Insets3f(30f, 10, 0, 10));
                } else {
                    master.setInsets(new Insets3f(8.5f, 10, 0, 10));
                }
                
                formulaPanel.addChild(master);
            
                Panel icon = new Panel(20f, 20f);
                icon.setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/general_icons/" + (tu.getFormulas().get(i)).getFormulaData().getType() + ".png")));
                icon.move(25f + xDisplace * -1f, 0, 0);
                formulaName.text.getTTFNode().attachChild(icon);
                
                TrueTypeKeyBMP bmp2 = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular2.ttf", Style.Plain, (36 + i));
                TrueTypeFont ttf2 = (TrueTypeBMP)assetManager.loadAsset(bmp2);
                ttf2.setScale((20f / (36f + i)));
                
                ColorRGBA colorType;
                int cost;
                if (tu.getFormulas().get(i).getTPUsage() > 0) {
                    colorType = new ColorRGBA(0.85f, 0.36f, 0.83f, 1f);
                    cost = tu.getFormulas().get(i).getTPUsage();
                } else {
                    colorType = new ColorRGBA(1, 0.76f, 0, 1);
                    cost = tu.getFormulas().get(i).getHPUsage();
                }
                
                TrueTypeNode costValue = ttf2.getText("" + cost, 3, colorType);
                costValue.move(formulaName.text.getTTFNode().getWidth() + 15, 0f, 0);
                formulaName.text.getTTFNode().attachChild(costValue);
                
            } else {
                Label placehold = new Label("                                                                                   ");
                placehold.setFontSize(11f - (2 * tu.getAmountExistingFormulas())); //the moment a unit gets 6 or more formulas, this will fail. Make it a scrolling view later
                formulaPanel.addChild(placehold);
            }
            
        }
    }
    
    protected void styleStats(TangibleUnit tu, TrueTypeNode ttfnode) {
        String statNames[] = {"STR", "ETHER", "AGI", "COMP", "DEX", "DEF", "RSL", "MOBILIT", "PHYSIQU"};
        //int[] stats = tu.getAllBaseStats();
        for (int i = 0; i < statNames.length; i++) {
            float strWidth = (((statNames[i].length() + 3f) / 10f) * ttfnode.getWidth()), strHeight = ttfnode.getHeight() / 10f;
            /*statProgress[i].setSingleText("" + tu.getRawBaseStats()[i + 1]);
            statProgress[i].setRotationPercent(100 * tu.getRawBaseStats()[i + 1] / ((double)tu.reorderStatsToFitGUI(tu.ClassMaxStats())[i + 1]));
            statProgress[i].birthTranslation(strWidth + 30f, i * (-1.168f * strHeight) - 12f);*/
            
            TrueTypeKeyBMP bmp = new TrueTypeKeyBMP("Interface/Fonts/Montaga-Regular.ttf", Style.Plain, (35 + i));
            TrueTypeFont ttf = (TrueTypeBMP)assetManager.loadAsset(bmp);
            ttf.setScale(26.5f / (35f + i));
            
            StatBundle sb = tu.getRawBaseStats().get(i + 1);
            
            TrueTypeNode ttn = ttf.getText("" + sb.getValue(), 2, ColorRGBA.White);
            ttn.move(-8.5f, 15f, 0);
            if (sb.getValue() >= 10) {
                ttn.move(-6f, 0, 0);
            }
            statProgress[i].move(strWidth + 30f, i * (-1.168f * strHeight) - 12f, 0);
            statProgress[i].getChildrenNode().attachChild(ttn);
            statProgress[i].setCirclePercent(sb.getValue() / ((float)tu.ClassMaxStats().get(sb.getWhichBaseStat())));
            
            ttfnode.attachChild(statProgress[i]);
            
            //attach stat icons
            Panel icon = new Panel(42f * (135f / 128f), 42f * (135f / 128f));
            icon.setBackground(new QuadBackgroundComponent(assetManager.loadTexture("Interface/GUI/stat_icons/" + statNames[i] + ".png")));
            icon.move(8.25f, i * (-1.168f * strHeight) + 8f, 0f);
            ttfnode.attachChild(icon);
            
            //attach bonuses
            if (tu.getAllRawBonuses()[i] > 0) {
                TrueTypeKeyBMP bonusbmp = new TrueTypeKeyBMP("Interface/Fonts/Montaga-Regular.ttf", Style.Plain, (46 + i));
                TrueTypeFont bonusttf = (TrueTypeBMP)assetManager.loadAsset(bonusbmp);
                bonusttf.setScale(27f / (46f + i));
                
                TrueTypeNode bonusText = bonusttf.getText(" +" + tu.getAllRawBonuses()[i], 5, new ColorRGBA(0.008f, 0.788f, 0.153f, 1f));
                ttfnode.attachChild(bonusText);
                bonusText.move(strWidth + 48.5f, i * (-1.168f * strHeight) + 4f, 0f);
            } else if (tu.getAllRawBonuses()[i] < 0) {
                TrueTypeKeyBMP bonusbmp = new TrueTypeKeyBMP("Interface/Fonts/Montaga-Regular.ttf", Style.Plain, (46 + i));
                TrueTypeFont bonusttf = (TrueTypeBMP)assetManager.loadAsset(bonusbmp);
                bonusttf.setScale(27f / (46f + i));
                
                TrueTypeNode bonusText = bonusttf.getText(" " + tu.getAllRawBonuses()[i], 5, new ColorRGBA(0.922f, 0.027f, 0.027f, 1f));
                ttfnode.attachChild(bonusText);
                bonusText.move(strWidth + 48.5f, i * (-1.168f * strHeight) + 4f, 0f);
            }
            
        }
    }
    
    public static List<Container> getItems(TangibleUnit tu, AssetManager ass) {
        List<Container> itms = new ArrayList<>();
        for (int i = 0; i < tu.getInventory().getItems().size(); i++) {
            if (tu.getInventory().getItems().get(i).doesExist()) {
                TrueTypeKeyBMP bmp = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular.ttf", Style.Plain, (35 + i));
                TrueTypeFont ttf = (TrueTypeBMP)ass.loadAsset(bmp);
                ttf.setScale((20f / (35f + i)));
            
                EditedLabel itemName = new EditedLabel(tu.getInventory().getItems().get(i).getName(), ttf);
                itemName.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Center);
                float xDisplace = 85f - (itemName.text.getTTFNode().getWidth() / 2f);
                itemName.text.getTTFNode().move(xDisplace, 0f, 0); //the width of the container is 230f
                
                itemName.scale(0.45f);
                
            
                Container master = new Container();
                ((TbtQuadBackgroundComponent)master.getBackground()).setTexture(ass.loadTexture("Interface/GUI/general_ui/nothing.png"));
                master.addChild(itemName);
                
                master.scale(0.8f);
            
                if (tu.getInventory().getItems().get(i) instanceof Weapon) {
                    Panel icon = new Panel(24f, 24f);
                    icon.setBackground(new QuadBackgroundComponent(ass.loadTexture("Interface/GUI/general_icons/" + ((Weapon)tu.getInventory().getItems().get(i)).getWeaponData().getType() + ".png")));
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
                    icon.setBackground(new QuadBackgroundComponent(ass.loadTexture(((ConsumableItem)tu.getInventory().getItems().get(i)).getPath())));
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
        }
        return itms;
    }
    
    public static List<Container> getFormulas(TangibleUnit tu, AssetManager ass) {
        List<Container> itms = new ArrayList<>();
        for (int i = 0; i < tu.getFormulas().size(); i++) {
            if (tu.getFormulas().get(i).doesExist()) {
                TrueTypeKeyBMP bmp = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular.ttf", Style.Plain, (46 + i));
                TrueTypeFont ttf = (TrueTypeBMP)ass.loadAsset(bmp);
                ttf.setScale((20f / (46f + i)));
            
                EditedLabel formulaName = new EditedLabel(tu.getFormulas().get(i).getName(), ttf);
                formulaName.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Center);
                float xDisplace = 115f - (formulaName.text.getTTFNode().getWidth() / 2f);
                formulaName.text.getTTFNode().move(xDisplace, 0f, 0); //the width of the container is 230f
                
                formulaName.scale(0.45f);
            
                Container master = new Container();
                ((TbtQuadBackgroundComponent)master.getBackground()).setTexture(ass.loadTexture("Interface/GUI/general_ui/nothing.png"));
                master.addChild(formulaName);
                
                master.scale(0.8f);

                Panel icon = new Panel(20f, 20f);
                icon.setBackground(new QuadBackgroundComponent(ass.loadTexture("Interface/GUI/general_icons/" + (tu.getFormulas().get(i)).getFormulaData().getType() + ".png")));
                icon.move(5f + xDisplace * -1f, 0, 0);
                formulaName.text.getTTFNode().attachChild(icon);
                
                TrueTypeKeyBMP bmp2 = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular2.ttf", Style.Plain, (36 + i));
                TrueTypeFont ttf2 = (TrueTypeBMP)ass.loadAsset(bmp2);
                ttf2.setScale((20f / (36f + i)));
                
                ColorRGBA colorType;
                int cost;
                if (tu.getFormulas().get(i).getTPUsage() > 0) {
                    colorType = new ColorRGBA(0.85f, 0.36f, 0.83f, 1f);
                    cost = tu.getFormulas().get(i).getTPUsage();
                } else {
                    colorType = new ColorRGBA(1, 0.76f, 0, 1);
                    cost = tu.getFormulas().get(i).getHPUsage();
                }
                
                TrueTypeNode costValue = ttf2.getText("" + cost, 3, colorType);
                costValue.move(formulaName.text.getTTFNode().getWidth() + 15, 0f, 0);
                formulaName.text.getTTFNode().attachChild(costValue);
            
                itms.add(master);
            }
        }
        return itms;
    }
    
    public static List<Container> getSkills(TangibleUnit tu, AssetManager ass) {
        List<Container> skls = new ArrayList<>();
        for (int i = 0; i < tu.getSkills().size(); i++) {
            if (tu.getSkills().get(i).doesExist()) {
                TrueTypeKeyBMP bmp = new TrueTypeKeyBMP("Interface/Fonts/Quattrocento-Regular3.ttf", Style.Plain, (46 + i));
                TrueTypeFont ttf = (TrueTypeBMP)ass.loadAsset(bmp);
                ttf.setScale((20f / (46f + i)));
                
                EditedLabel skillName = new EditedLabel(tu.getSkills().get(i).getName(), ttf);
                skillName.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Center);
                float xDisplace = 130f - (skillName.text.getTTFNode().getWidth() / 2f);
                skillName.text.getTTFNode().move(xDisplace, 0f, 0); //the width of the container is 230f
                
                skillName.scale(0.45f);
                
                Container master = new Container();
                ((TbtQuadBackgroundComponent)master.getBackground()).setTexture(ass.loadTexture("Interface/GUI/general_ui/nothing.png"));
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
        }
        return skls;
    }
    
}

class Cosa {
    private String block, description;
    private Container cont;
    private Geometry geo;
    
    private GuiComponent defaultBG;
    
    public Cosa(Container C, String bl, String desc) {
        cont = C;
        block = bl;
        description = desc;
        defaultBG = C.getBackground();
    }
    
    public Cosa(Geometry G, String tag, String desc) {
        geo = G;
        block = tag;
        description = desc;
    }
    
    public Container getContainer() { return cont; }
    public Geometry getGeometry() { return geo; }
    public String getBlock() { return block; }
    public String getDescription() { return description; }
    
    public GuiComponent getDefaultBG() { return defaultBG; }
    
    public Object retrieveItem() {
        return (cont != null) ? cont : ((geo != null) ? geo : null);
    }
    
}
