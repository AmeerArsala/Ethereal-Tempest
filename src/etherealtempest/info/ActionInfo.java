/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.info;

import com.atr.jme.font.util.StringContainer.Align;
import com.atr.jme.font.util.StringContainer.VAlign;
import com.atr.jme.font.util.StringContainer.WrapMode;
import com.atr.jme.font.util.Style;
import com.jme3.asset.AssetManager;
import com.jme3.font.Rectangle;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import enginetools.MaterialCreator;
import etherealtempest.fsm.FSM.CursorState;
import etherealtempest.fsm.FSM.UnitState;
import fundamental.ability.Ability;
import fundamental.formation.Formation;
import fundamental.formula.Formula;
import fundamental.item.Item;
import fundamental.item.weapon.Weapon;
import fundamental.skill.Skill;
import etherealtempest.gui.specific.ActionMenu;
import fundamental.formation.FormationTechnique;
import fundamental.item.Inventory;
import general.math.function.RGBAFunction;
import general.ui.Icon;
import general.ui.Padding;
import general.ui.menu.BasicMenu;
import general.ui.menu.BasicMenu.Orientation;
import general.ui.menu.BasicMenuOption;
import general.ui.menu.RadialMenuOption;
import general.ui.text.FontProperties;
import general.ui.text.FontProperties.KeyType;
import general.ui.text.TextProperties;
import general.visual.animation.Animation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import maps.data.MapTextures;
import maps.layout.occupant.character.TangibleUnit;
import maps.layout.occupant.control.CursorFSM.Purpose;

/**
 *
 * @author night
 */
public class ActionInfo {
    public enum PostMoveAction {
        Attack, // ✓
        Ether, // ✓
        Item, //✓
        Skill, // ✓
        Trade, // ✓
        Ability, // ✓
        Formation, // ✓
        Talk, // ✓
        ChainAttack,
        
        Annex,
        Escape;
    }
    
    public static final List<PostMoveAction> ALL = Arrays.asList(PostMoveAction.Attack, PostMoveAction.Ether, PostMoveAction.Item, PostMoveAction.Skill, PostMoveAction.Ability, PostMoveAction.Trade, PostMoveAction.ChainAttack, PostMoveAction.Formation);
    public static final List<PostMoveAction> NONE = Arrays.asList();
    
    private final List<PostMoveAction> availableActions;
    private List<RadialMenuOption<Conveyor>> availableOptions = null;
    
    private List<Formula> usableFormulas = null; // Ether
    private List<Ability> usableAbilities = null; // Abilities
    private List<FormationTechnique> usableFormationTechniques = null; // Formation
    private List<Skill> usableSkills = null; // Skill
    private List<Weapon> usableWeapons = null; // Attack
    private List<Item> usableNonWeaponItems = null; // Item
    private List<TangibleUnit> tradePartners = null; // Trade
    private List<TangibleUnit> talkPartners = null; // Talk
    
    public ActionInfo(
            List<Weapon> usableWeapons, List<Formula> usableFormulas, List<Item> usableNonWeaponItems, List<Skill> usableSkills, 
            List<Ability> usableAbilities, List<FormationTechnique> usableFormationTechniques, List<TangibleUnit> tradePartners, List<TangibleUnit> talkPartners) {
        
        this.usableWeapons = usableWeapons;
        this.usableFormulas = usableFormulas;
        this.usableNonWeaponItems = usableNonWeaponItems;
        this.usableSkills = usableSkills;
        this.usableAbilities = usableAbilities;
        this.usableFormationTechniques = usableFormationTechniques;
        this.tradePartners = tradePartners;
        this.talkPartners = talkPartners;

        availableActions = new ArrayList<>();
        availableOptions = new ArrayList<>();
        
        buildList();
    }
    
    private void buildList() {
        if (usableWeapons != null && usableWeapons.size() > 0) {
            availableActions.add(PostMoveAction.Attack);
            availableOptions.add(Attack());
        }
        
        if (usableFormulas != null && usableFormulas.size() > 0) {
            availableActions.add(PostMoveAction.Ether);
            availableOptions.add(Ether());
        }
        
        if (usableNonWeaponItems != null && usableNonWeaponItems.size() > 0) {
            availableActions.add(PostMoveAction.Item);
            availableOptions.add(Item());
        }
        
        if (usableSkills != null && usableSkills.size() > 0) {
            availableActions.add(PostMoveAction.Skill);
            availableOptions.add(Skill());
        }
        
        if (usableAbilities != null && usableAbilities.size() > 0) {
            availableActions.add(PostMoveAction.Ability);
            availableOptions.add(Ability());
        }
        
        if (usableFormationTechniques != null && usableFormationTechniques.size() > 0) {
            availableActions.add(PostMoveAction.Formation);
            availableOptions.add(Formation());
        }
        
        if (tradePartners != null && tradePartners.size() > 0) {
            availableActions.add(PostMoveAction.Trade);
            availableOptions.add(Trade());
        }
        
        if (talkPartners != null && talkPartners.size() > 0) {
            availableActions.add(PostMoveAction.Talk);
            //availableOptions.add(Talk());
        }
        
        availableOptions.add(Standby()); //last one
    }
    
    public List<PostMoveAction> getAvailableActions() { return availableActions; }
    public List<RadialMenuOption<Conveyor>> getAvailableOptions() { return availableOptions; }
    
    public List<Formula> getUsableFormulas() { return usableFormulas; }
    public List<Ability> getUsableAbilities() { return usableAbilities; }
    public List<FormationTechnique> getUsableFormationTechniques() { return usableFormationTechniques; }
    public List<Skill> getUsableSkills() { return usableSkills; }
    public List<Weapon> getUsableWeapons() { return usableWeapons; }
    public List<Item> getUsableItems() { return usableNonWeaponItems; }
    public List<TangibleUnit> getTradePartners() { return tradePartners; }
    public List<TangibleUnit> getTalkPartners() { return talkPartners; }
    
    
    private static RadialMenuOption<Conveyor> Attack() {
        return new RadialMenuOption<Conveyor>("Attack", MapTextures.GUI.ActionMenu.ATTACK) { //add a submenu here maybe ?
            private Conveyor conv;
            
            @Override
            protected void initialize(AssetManager assetManager, Conveyor data) { //create a submenu here
                conv = data;
            }

            @Override
            public void onSelect() {
                conv.getUnit().equip(((Weapon)conv.getUnit().getInventory().getFirstItem()));
                conv.getUnit().setToUseSkill(null);
                conv.getUnit().getFSM().setNewStateIfAllowed(UnitState.SelectingTarget);
                conv.getCursor().setPurpose(Purpose.WeaponAttack);
                conv.getCursor().getFSM().setNewStateIfAllowed(CursorState.AnyoneSelectingTarget);
                closeEntireMenu();
            }
        }; 
    }
    
    private static RadialMenuOption<Conveyor> Ether() {
        return new RadialMenuOption<Conveyor>("Ether", MapTextures.GUI.ActionMenu.ETHER) { //add a submenu here
            @Override
            protected void initialize(AssetManager assetManager, Conveyor data) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onSelect() {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        }; 
    }
    
    private static RadialMenuOption<Conveyor> Skill() {
        return new RadialMenuOption<Conveyor>("Skill", MapTextures.GUI.ActionMenu.SKILL) { //add a submenu here
            @Override
            protected void initialize(AssetManager assetManager, Conveyor data) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onSelect() {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        }; 
    }
    
    private static RadialMenuOption<Conveyor> Ability() {
        return new RadialMenuOption<Conveyor>("Ability", MapTextures.GUI.ActionMenu.ABILITY) { //add a submenu here
            @Override
            protected void initialize(AssetManager assetManager, Conveyor data) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onSelect() {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }
    
    private static RadialMenuOption<Conveyor> Item() {
        return new RadialMenuOption<Conveyor>("Item", MapTextures.GUI.ActionMenu.ITEM) {  //add a submenu here
            @Override
            protected void initialize(AssetManager assetManager, Conveyor data) {
                BasicMenu.Settings itemMenuSettings = 
                    BasicMenu.Settings.builder()
                        .autoSelectOnOneOption(false)
                        .hoverCurrentIndexWhenNothingElseIsHovered(false)
                        .uniformFontProperties(new FontProperties("Interface/Fonts/Montaga-Regular.ttf", KeyType.BMP, Style.Plain, 18, 1.75f))
                        .textBoxPadding(new Padding(5f, 5f, 5f, 5f))
                        .optionMaterialCreator(new MaterialCreator(MaterialCreator.UNSHADED))
                        .hoveredColorFunction(new RGBAFunction(RadialMenuOption.HoveredOrange))
                        .notHoveredColorFunction(new RGBAFunction(ColorRGBA.Black))
                        .menuOrientation(Orientation.Vertical)
                        .paddingBetweenOptions(0f)
                        .menuTransitionInOnSelect(new Animation[] { Animation.DissolveIn().setLength(0.25f) })
                        .menuTransitionOutOnDeselect(new Animation[] { Animation.DissolveOut().setLength(0.25f) })
                        .menuTransitionInOnDeselect(new Animation[] { Animation.NOTHING().setLength(0.00001f) })
                        .menuTransitionOutOnSelect(new Animation[] { Animation.NOTHING().setLength(0.00001f) })
                        .build();
                
                submenu = new BasicMenu<Conveyor>("Inventory", itemMenuSettings) {
                    @Override
                    protected void updateExtra(float tpf) {}

                    @Override
                    protected void initialize(Conveyor data) {}

                    @Override
                    protected void onDetach() {}
                    
                    @Override
                    protected void attemptRemoveFromParent() {}
                    
                    @Override
                    protected void retachTo(Node node) {
                        if (!node.hasChild(optionsNode)) {
                            node.attachChild(optionsNode);
                        }
                    }
                };
                
                //((BasicMenu<Conveyor>)submenu).scale(2f);
                
                Inventory unitInventory = data.getUnit().getInventory();
                List<BasicMenuOption<Conveyor>> itemOptions = new ArrayList<>();
                
                float rectangleX = 0f;
                float rectangleY = 0f;
                float rectangleWidth = 100f;
                float rectangleHeight = 25f;
                
                unitInventory.getItems().forEach((item) -> {
                    //create parameters
                    Rectangle rect = new Rectangle(rectangleX, rectangleY, rectangleWidth, rectangleHeight);
                    TextProperties properties =
                        TextProperties.builder()
                            .kerning(3)
                            .horizontalAlignment(Align.Left)
                            .verticalAlignment(VAlign.Center)
                            .wrapMode(WrapMode.Clip)
                            .textBox(rect)
                            .build();
                    
                    //create option
                    BasicMenuOption<Conveyor> itemOption = new BasicMenuOption<Conveyor>(item.getName(), properties, ColorRGBA.White) {
                        @Override
                        protected void initialize(AssetManager assetManager, Conveyor data) {
                            //create a submenu for on selecting a specific item
                            submenu = item.createSubmenu(data, assetManager);
                        }
                        
                        @Override
                        protected void onSelect() {
                            //play sound
                        }
                    };
                    
                    //add icon and other UI elements like durability
                    float iconWidth = 20f;
                    float iconHeight = 20f;
                    Icon icon = new Icon(iconWidth, iconHeight, assetManager.loadTexture(item.getIconPath()), new Padding(0f, 5f, 5f, 0f));
                    itemOption.getNode().attachChild(icon);
                    //TODO: add durability/uses
                    
                    
                    itemOptions.add(itemOption);
                });
                
                submenu.fullyInitialize(data, itemOptions, assetManager);
            }

            @Override
            public void onSelect() {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }
    
    private static RadialMenuOption<Conveyor> Trade() {
        return new RadialMenuOption<Conveyor>("Trade", MapTextures.GUI.ActionMenu.TRADE) { //add a submenu here
            @Override
            protected void initialize(AssetManager assetManager, Conveyor data) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onSelect() {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }
    
    private static RadialMenuOption<Conveyor> Formation() {
        return new RadialMenuOption<Conveyor>("Formation", MapTextures.GUI.ActionMenu.FORMATION) { //add a submenu here
            @Override
            protected void initialize(AssetManager assetManager, Conveyor data) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onSelect() {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }
    
    private static RadialMenuOption<Conveyor> ChainAttack() {
        return new RadialMenuOption<Conveyor>("Chain Attack", MapTextures.GUI.ActionMenu.CHAIN_ATTACK) {  //add a submenu here
            @Override
            protected void initialize(AssetManager assetManager, Conveyor data) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onSelect() {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }
    
    private static RadialMenuOption<Conveyor> Standby() {
        return new RadialMenuOption<Conveyor>("Standby", MapTextures.GUI.ActionMenu.STANDBY) { //no submenu needed here
            private Conveyor conv;
            
            @Override
            protected void initialize(AssetManager assetManager, Conveyor data) {
                conv = data;
            }

            @Override
            public void onSelect() {
                conv.getCursor().resetState();
                closeEntireMenu();
            }
        };
    }
    
    /*private static RadialMenuOption<Conveyor> Talk() {
        return new RadialMenuOption<Conveyor>("Talk", MapTextures.GUI.ActionMenu.TALK) {
            @Override
            protected void initialize(AssetManager assetManager, Conveyor data) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onSelect() {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        }.setDefaultColoring(Green);
    }*/
    
}
