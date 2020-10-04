/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.info;

import fundamental.ability.Ability;
import fundamental.formation.Formation;
import fundamental.formula.Formula;
import fundamental.item.Item;
import fundamental.item.Weapon;
import fundamental.skill.Skill;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import maps.layout.Coords;
import maps.layout.TangibleUnit;

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
    
    private Coords startingPosition = new Coords(0, 0);
    
    public static final Coords STARTING_POSITION = new Coords(0, 0);
    public static final Coords ATTACK_POSITION = new Coords(0, 2);
    public static final Coords ETHER_POSITION = new Coords(-1, 0);
    public static final Coords SKILL_POSITION = new Coords(-1, 1);
    
    private List<Formula> usableFormulas = null; // Ether
    private List<Ability> usableAbilities = null; // Abilities
    private List<Formation> usableFormations = null; // Formation
    private List<Skill> usableSkills = null; // Skill
    private List<Weapon> usableWeapons = null; // Attack
    private List<Item> usableNonWeaponItems = null; // Item
    private List<TangibleUnit> tradePartners = null; // Trade
    private List<TangibleUnit> talkPartners = null; // Talk
    
    public ActionInfo(List<PostMoveAction> availableActions) {
        this.availableActions = availableActions;
    }
    
    public ActionInfo(
            List<Weapon> usableWeapons, List<Formula> usableFormulas, List<Item> usableNonWeaponItems, List<Skill> usableSkills, 
            List<Ability> usableAbilities, List<Formation> usableFormations, List<TangibleUnit> tradePartners, List<TangibleUnit> talkPartners) {
        
        this.usableWeapons = usableWeapons;
        this.usableFormulas = usableFormulas;
        this.usableNonWeaponItems = usableNonWeaponItems;
        this.usableSkills = usableSkills;
        this.usableAbilities = usableAbilities;
        this.usableFormations = usableFormations;
        this.tradePartners = tradePartners;
        this.talkPartners = talkPartners;

        availableActions = new ArrayList<>();
        
        buildList();
    }
    
    private void buildList() {
        if (usableWeapons != null && usableWeapons.size() > 0) {
            availableActions.add(PostMoveAction.Attack);
        }
        
        if (usableFormulas != null && usableFormulas.size() > 0) {
            availableActions.add(PostMoveAction.Ether);
        }
        
        if (usableNonWeaponItems != null && usableNonWeaponItems.size() > 0) {
            availableActions.add(PostMoveAction.Item);
        }
        
        if (usableSkills != null && usableSkills.size() > 0) {
            availableActions.add(PostMoveAction.Skill);
        }
        
        if (usableAbilities != null && usableAbilities.size() > 0) {
            availableActions.add(PostMoveAction.Ability);
        }
        
        if (usableFormations != null && usableFormations.size() > 0) {
            availableActions.add(PostMoveAction.Formation);
        }
        
        if (tradePartners != null && tradePartners.size() > 0) {
            availableActions.add(PostMoveAction.Trade);
        }
        
        if (talkPartners != null && talkPartners.size() > 0) {
            availableActions.add(PostMoveAction.Talk);
        }
    }
    
    public ActionInfo setStartingPosition(Coords pos) {
        startingPosition = pos;
        return this;
    }
    
    public List<PostMoveAction> getAvailableActions() { return availableActions; }
    
    public Coords getStartingPosition() { return startingPosition; }
    
    public List<Formula> getUsableFormulas() { return usableFormulas; }
    public List<Ability> getUsableAbilities() { return usableAbilities; }
    public List<Formation> getUsableFormations() { return usableFormations; }
    public List<Skill> getUsableSkills() { return usableSkills; }
    public List<Weapon> getUsableWeapons() { return usableWeapons; }
    public List<Item> getUsableItems() { return usableNonWeaponItems; }
    public List<TangibleUnit> getTradePartners() { return tradePartners; }
    public List<TangibleUnit> getTalkPartners() { return talkPartners; }
    
}
