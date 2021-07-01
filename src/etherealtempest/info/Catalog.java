/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.info;

import fundamental.unit.Unit;
import fundamental.jobclass.JobClass;
import fundamental.stats.BaseStat;
import fundamental.item.ConsumableItem;
import fundamental.item.weapon.Weapon;
import fundamental.formation.Formation;
import fundamental.formula.Formula;
import fundamental.skill.Skill;
import fundamental.talent.Talent;
import java.util.Arrays;
import java.util.List;
import fundamental.stats.StatBundle;
import general.utils.helpers.GeneralUtils;
/**
 *
 * @author night
 */
public class Catalog {
    //referring to weapons
    //public static final ArrayList<MobilityType> effAgainstNothing = new ArrayList<>();
    
    /*
    public static List<StatBundle> baseCav() {
        return Arrays.asList(
                new StatBundle(BaseStat.maxHP, 0),
                new StatBundle(BaseStat.maxTP, 0),
                new StatBundle(BaseStat.strength, 1),
                new StatBundle(BaseStat.ether, 0),
                new StatBundle(BaseStat.agility, 1),
                new StatBundle(BaseStat.dexterity, 1),
                new StatBundle(BaseStat.comprehension, 0),
                new StatBundle(BaseStat.defense, 3),
                new StatBundle(BaseStat.resilience, 0),
                new StatBundle(BaseStat.mobility, 3),
                new StatBundle(BaseStat.Physique, 10),
                new StatBundle(BaseStat.adrenaline, 0)
        );
    }
    
    public static List<StatBundle> tier0Bonuses() {
        return Arrays.asList(
                new StatBundle(BaseStat.maxHP, 0),
                new StatBundle(BaseStat.maxTP, 0),
                new StatBundle(BaseStat.strength, 0),
                new StatBundle(BaseStat.ether, 0),
                new StatBundle(BaseStat.agility, 0),
                new StatBundle(BaseStat.dexterity, 0),
                new StatBundle(BaseStat.comprehension, 0),
                new StatBundle(BaseStat.defense, 0),
                new StatBundle(BaseStat.resilience, 0),
                new StatBundle(BaseStat.mobility, 0),
                new StatBundle(BaseStat.Physique, 0),
                new StatBundle(BaseStat.adrenaline, 0)
        );
    }
    */
    
    //public static List<Item> emptyInventory() { return Arrays.asList(new Item(false), new Item(false), new Item(false), new Item(false), new Item(false), new Item(false), new Item(false)); } //7 items max
    //public static List<Talent> emptyTalents() { return Arrays.asList(new Talent(false), new Talent(false), new Talent(false), new Talent(false), new Talent(false), new Talent(false)); } //6 Talents max
    //public static List<Skill> emptySkills() { return Arrays.asList(new Skill(false), new Skill(false), new Skill(false), new Skill(false), new Skill(false)); } //5 Skills max
    //public static final List<Formation> emptyFormations() { return Arrays.asList(new Formation(false), new Formation(false), new Formation(false)); } //3 formations max
    //public static final List<Ability> emptyAbilities() { return Arrays.asList(new Ability(false), new Ability(false), new Ability(false), new Ability(false), new Ability(false), new Ability(false)); } //6 abilities max
    //public static final List<Formula> emptyFormulas() { return Arrays.asList(new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula()); } //15 formulas max
    
    public static Unit UNIT_Morva() {
        return new Unit(
            "Morva",            // Name
            JobClass.Freeblade, // Class
            Arrays.asList       // Base Stats
            (
                new StatBundle<>(BaseStat.Level, 1),          // Level
                new StatBundle<>(BaseStat.MaxHP, 28),         // Max HP
                new StatBundle<>(BaseStat.MaxTP, 0),          // Max TP
                new StatBundle<>(BaseStat.Strength, 8),       // Strength
                new StatBundle<>(BaseStat.Ether, 5),          // Ether
                new StatBundle<>(BaseStat.Agility, 16),       // Agility
                new StatBundle<>(BaseStat.Dexterity, 5),      // Dexterity
                new StatBundle<>(BaseStat.Comprehension, 6),  // Comprehension
                new StatBundle<>(BaseStat.Defense, 5),        // Defense
                new StatBundle<>(BaseStat.Resilience, 2),     // Resilience
                new StatBundle<>(BaseStat.Mobility, 4),       // Mobility
                new StatBundle<>(BaseStat.Physique, 9),       // Physique
                new StatBundle<>(BaseStat.Adrenaline, 10)     // Adrenaline
            ),
            Arrays.asList       // Growth Rates
            (
                new StatBundle<>(BaseStat.Level, 100),         // Level
                new StatBundle<>(BaseStat.MaxHP, 60),          // Max HP
                new StatBundle<>(BaseStat.MaxTP, 10),          // Max TP
                new StatBundle<>(BaseStat.Strength, 55),       // Strength
                new StatBundle<>(BaseStat.Ether, 45),          // Ether
                new StatBundle<>(BaseStat.Agility, 45),        // Agility
                new StatBundle<>(BaseStat.Dexterity, 55),      // Dexterity
                new StatBundle<>(BaseStat.Comprehension, 45),  // Comprehension
                new StatBundle<>(BaseStat.Defense, 40),        // Defense
                new StatBundle<>(BaseStat.Resilience, 30),     // Resilience
                new StatBundle<>(BaseStat.Mobility, 5),        // Mobility
                new StatBundle<>(BaseStat.Physique, 15),       // Physique
                new StatBundle<>(BaseStat.Adrenaline, 50)      // Adrenaline
            ),
            Arrays.asList(       // Base Inventory
                Weapon.Firangi(),
                ConsumableItem.Apple()
            ),
            Arrays.asList(       // Base Formulas
                Formula.Anemo_Schism()
            ),
            Arrays.asList(       // Base Talents
                Talent.EyeOfTheStorm(),
                Talent.Opportunist(),
                Talent.Optimism()
            ),
            Arrays.asList(       // Base Skills
                Skill.Heavy_Swing
            ),
            Arrays.asList(       // Base Abilities
                
            ),
            Arrays.asList(       // Base Formations
                Formation.Trigonal_Planar()
            )
        );
    }
    
    public static Unit UNIT_EvilMorva() {
        return new Unit(
            "Morva",            // Name
            JobClass.Freeblade, // Class
            Arrays.asList       // Base Stats
            (
                new StatBundle<>(BaseStat.Level, 1),          // Level
                new StatBundle<>(BaseStat.MaxHP, 19),         // Max HP
                new StatBundle<>(BaseStat.MaxTP, 0),          // Max TP
                new StatBundle<>(BaseStat.Strength, 9),       // Strength
                new StatBundle<>(BaseStat.Ether, 2),          // Ether
                new StatBundle<>(BaseStat.Agility, 8),        // Agility
                new StatBundle<>(BaseStat.Dexterity, 5),      // Dexterity
                new StatBundle<>(BaseStat.Comprehension, 3),  // Comprehension
                new StatBundle<>(BaseStat.Defense, 5),        // Defense
                new StatBundle<>(BaseStat.Resilience, 2),     // Resilience
                new StatBundle<>(BaseStat.Mobility, 4),       // Mobility
                new StatBundle<>(BaseStat.Physique, 7),       // Physique
                new StatBundle<>(BaseStat.Adrenaline, 15)     // Adrenaline
            ),
            Arrays.asList       // Growth Rates
            (
                new StatBundle<>(BaseStat.Level, 100),         // Level
                new StatBundle<>(BaseStat.MaxHP, 60),          // Max HP
                new StatBundle<>(BaseStat.MaxTP, 10),          // Max TP
                new StatBundle<>(BaseStat.Strength, 55),       // Strength
                new StatBundle<>(BaseStat.Ether, 45),          // Ether
                new StatBundle<>(BaseStat.Agility, 45),        // Agility
                new StatBundle<>(BaseStat.Dexterity, 55),      // Dexterity
                new StatBundle<>(BaseStat.Comprehension, 45),  // Comprehension
                new StatBundle<>(BaseStat.Defense, 40),        // Defense
                new StatBundle<>(BaseStat.Resilience, 30),     // Resilience
                new StatBundle<>(BaseStat.Mobility, 5),        // Mobility
                new StatBundle<>(BaseStat.Physique, 15),       // Physique
                new StatBundle<>(BaseStat.Adrenaline, 50)      // Adrenaline
            ),
            Arrays.asList(       // Base Inventory
                Weapon.Cutlass()
            ),
            Arrays.asList(       // Base Formulas
                
            ),
            Arrays.asList(       // Base Talents
                Talent.Optimism()
            ),
            Arrays.asList(       // Base Skills
                
            ),
            Arrays.asList(       // Base Abilities
                
            ),
            Arrays.asList(       // Base Formations
                
            )
        );
    }
    
    
    /*public static Unit UNIT_Pillager() {
        return new Unit(
                "Pillager", 
                JobClass.Marauder,
                Arrays.asList //base stats
                (
                    new StatBundle<>(BaseStat.Level, 1),  //level
                    new StatBundle<>(BaseStat.MaxHP, 18), //max hp
                    new StatBundle<>(BaseStat.MaxTP, 0), //max tp
                    new StatBundle<>(BaseStat.Strength, 9),  //strength
                    new StatBundle<>(BaseStat.Ether, 0),  //ether
                    new StatBundle<>(BaseStat.Agility, 2), //agility
                    new StatBundle<>(BaseStat.Dexterity, 2),  //dexterity
                    new StatBundle<>(BaseStat.Comprehension, 1),  //comprehension
                    new StatBundle<>(BaseStat.Defense, 4),  //defense
                    new StatBundle<>(BaseStat.Resilience, 0),  //resilience
                    new StatBundle<>(BaseStat.Mobility, 4),  //mobility
                    new StatBundle<>(BaseStat.Physique, 12),  //physique
                    new StatBundle<>(BaseStat.Adrenaline, 0)  //adrenaline
                ),
                Unit.DEFAULT_ENEMY_GROWTH_RATES_LIST(),
                Arrays.asList(Weapon.Francisca()), //inventory
                Arrays.asList(), //formulas
                Arrays.asList(), //talents
                Arrays.asList(), //skills
                Arrays.asList(), //abilities
                Arrays.asList() //formations
        );
    }*/
    
}

