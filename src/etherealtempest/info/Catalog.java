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
import general.utils.GeneralUtils;
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
                "Morva", //name
                JobClass.Freeblade, //class
                Arrays.asList //base stats
                (
                    new StatBundle<>(BaseStat.Level, 1),  //level
                    new StatBundle<>(BaseStat.MaxHP, 28), //max hp
                    new StatBundle<>(BaseStat.MaxTP, 0), //max tp
                    new StatBundle<>(BaseStat.Strength, 8),  //strength
                    new StatBundle<>(BaseStat.Ether, 5),  //ether
                    new StatBundle<>(BaseStat.Agility, 16), //agility
                    new StatBundle<>(BaseStat.Dexterity, 5),  //dexterity
                    new StatBundle<>(BaseStat.Comprehension, 6),  //comprehension
                    new StatBundle<>(BaseStat.Defense, 5),  //defense
                    new StatBundle<>(BaseStat.Resilience, 2),  //resilience
                    new StatBundle<>(BaseStat.Mobility, 4),  //mobility
                    new StatBundle<>(BaseStat.Physique, 9),  //physique
                    new StatBundle<>(BaseStat.Adrenaline, 10)  //adrenaline
                ),
                Arrays.asList //growth rates
                (
                    new StatBundle<>(BaseStat.Level, 100),  //level
                    new StatBundle<>(BaseStat.MaxHP, 60), //max hp
                    new StatBundle<>(BaseStat.MaxTP, 10), //max tp
                    new StatBundle<>(BaseStat.Strength, 55),  //strength
                    new StatBundle<>(BaseStat.Ether, 45),  //ether
                    new StatBundle<>(BaseStat.Agility, 45), //agility
                    new StatBundle<>(BaseStat.Dexterity, 55),  //dexterity
                    new StatBundle<>(BaseStat.Comprehension, 45),  //comprehension
                    new StatBundle<>(BaseStat.Defense, 40),  //defense
                    new StatBundle<>(BaseStat.Resilience, 30),  //resilience
                    new StatBundle<>(BaseStat.Mobility, 5),  //mobility
                    new StatBundle<>(BaseStat.Physique, 15),  //physique
                    new StatBundle<>(BaseStat.Adrenaline, 50)  //adrenaline
                ),
                Arrays.asList(Weapon.Firangi(), ConsumableItem.Apple()), //base inventory
                Arrays.asList(Formula.Anemo_Schism()), //base formulas
                Arrays.asList(Talent.EyeOfTheStorm(), Talent.Opportunist(), Talent.Optimism()), //base talents
                Arrays.asList(Skill.Heavy_Swing), //base skills
                Arrays.asList(), //base abilities
                Arrays.asList(Formation.Trigonal_Planar()) //base formations
            );
    }
    
    public static Unit UNIT_EvilMorva() {
        return new Unit(
                "Morva", //name
                JobClass.Freeblade, //class
                Arrays.asList //base stats
                (
                    new StatBundle<>(BaseStat.Level, 1),  //level
                    new StatBundle<>(BaseStat.MaxHP, 19), //max hp
                    new StatBundle<>(BaseStat.MaxTP, 0), //max tp
                    new StatBundle<>(BaseStat.Strength, 9),  //strength
                    new StatBundle<>(BaseStat.Ether, 2),  //ether
                    new StatBundle<>(BaseStat.Agility, 8), //agility
                    new StatBundle<>(BaseStat.Dexterity, 5),  //dexterity
                    new StatBundle<>(BaseStat.Comprehension, 3),  //comprehension
                    new StatBundle<>(BaseStat.Defense, 5),  //defense
                    new StatBundle<>(BaseStat.Resilience, 2),  //resilience
                    new StatBundle<>(BaseStat.Mobility, 4),  //mobility
                    new StatBundle<>(BaseStat.Physique, 7),  //physique
                    new StatBundle<>(BaseStat.Adrenaline, 15)  //adrenaline
                ),
                Arrays.asList //growth rates
                (
                    new StatBundle<>(BaseStat.Level, 100),  //level
                    new StatBundle<>(BaseStat.MaxHP, 60), //max hp
                    new StatBundle<>(BaseStat.MaxTP, 10), //max tp
                    new StatBundle<>(BaseStat.Strength, 55),  //strength
                    new StatBundle<>(BaseStat.Ether, 45),  //ether
                    new StatBundle<>(BaseStat.Agility, 45), //agility
                    new StatBundle<>(BaseStat.Dexterity, 55),  //dexterity
                    new StatBundle<>(BaseStat.Comprehension, 45),  //comprehension
                    new StatBundle<>(BaseStat.Defense, 40),  //defense
                    new StatBundle<>(BaseStat.Resilience, 30),  //resilience
                    new StatBundle<>(BaseStat.Mobility, 5),  //mobility
                    new StatBundle<>(BaseStat.Physique, 15),  //physique
                    new StatBundle<>(BaseStat.Adrenaline, 50)  //adrenaline
                ),
                Arrays.asList(Weapon.Cutlass()), //base inventory
                Arrays.asList(), //base formulas
                Arrays.asList(Talent.Optimism()), //base talents
                Arrays.asList(), //base skills
                Arrays.asList(), //base abilities
                Arrays.asList()  //base formations
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

