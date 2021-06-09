/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.info;

import etherealtempest.characters.Unit;
import etherealtempest.characters.JobClass;
import battle.Combatant.BaseStat;
import battle.Combatant.BattleStat;
import fundamental.stats.Toll;
import fundamental.item.Item;
import fundamental.item.ConsumableItem;
import fundamental.item.Weapon;
import fundamental.formation.Formation;
import fundamental.formation.FormationTechnique;
import fundamental.formula.Formula;
import fundamental.skill.Skill;
import fundamental.stats.Toll.Exchange;
import fundamental.skill.SkillEffect;
import fundamental.talent.Talent;
import java.util.Arrays;
import java.util.List;
import maps.layout.occupant.TangibleUnit;
import maps.layout.tile.Tile;
import misc.CustomAnimationSegment;
import misc.FrameDelay;
import etherealtempest.MasterFsmState;
import etherealtempest.info.Request.RequestType;
import fundamental.stats.Bonus;
import fundamental.tool.DamageTool;
import fundamental.FreelyAssociated;
import fundamental.stats.RawBroadBonus;
import fundamental.stats.StatBundle;
import fundamental.tool.Tool.ToolType;
import java.util.HashMap;
/**
 *
 * @author night
 */
public class Catalog {
    //referring to weapons
    public static final String[] effAgainstNothing = {"None"};
    
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
                new StatBundle(BaseStat.physique, 10),
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
                new StatBundle(BaseStat.physique, 0),
                new StatBundle(BaseStat.adrenaline, 0)
        );
    }
    
    public static List<StatBundle> noBattleBonus() {
        return Arrays.asList(
                new StatBundle(BattleStat.Accuracy, 0),
                new StatBundle(BattleStat.AttackPower, 0),
                new StatBundle(BattleStat.AttackSpeed, 0),
                new StatBundle(BattleStat.Crit, 0),
                new StatBundle(BattleStat.CritEvasion, 0),
                new StatBundle(BattleStat.Evasion, 0)
        );
    }
    
    //public static List<Item> emptyInventory() { return Arrays.asList(new Item(false), new Item(false), new Item(false), new Item(false), new Item(false), new Item(false), new Item(false)); } //7 items max
    public static List<Talent> emptyTalents() { return Arrays.asList(new Talent(false), new Talent(false), new Talent(false), new Talent(false), new Talent(false), new Talent(false)); } //6 Talents max
    //public static List<Skill> emptySkills() { return Arrays.asList(new Skill(false), new Skill(false), new Skill(false), new Skill(false), new Skill(false)); } //5 Skills max
    //public static final List<Formation> emptyFormations() { return Arrays.asList(new Formation(false), new Formation(false), new Formation(false)); } //3 formations max
    //public static final List<Ability> emptyAbilities() { return Arrays.asList(new Ability(false), new Ability(false), new Ability(false), new Ability(false), new Ability(false), new Ability(false)); } //6 abilities max
    //public static final List<Formula> emptyFormulas() { return Arrays.asList(new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula()); } //15 formulas max
    
    public static <K> List<K> fillWith(List<K> arr, List<K> replacements) {
        for (int i = 0; i < replacements.size(); i++) {
            arr.set(i, replacements.get(i));
        }
        
        return arr;
    }
    
    public static <K> List<K> replaceArrSlotWith(List<K> arr, K replacement, int index) {
        List<K> arr2 = arr;
        arr2.set(index, replacement);
        return arr2;
    }
    
    public static <K> K[] replaceArraySlotWith(K[] arr, K replacement, int index) {
        K[] arr2 = arr;
        arr2[index] = replacement;
        return arr2;
    }
    
    public static int furthestTrue(boolean[] range) {
        int furthest = 0;
        for (int i = 1; i < range.length; i++) {
            if (i > furthest && range[i]) { furthest = i; }
        }
        
        return furthest;
    }
    
    public static Formula[] FormulaCatalog = 
    { //    Formula(String name, String description, FormulaType FT, String type, String attr, int mt, int hit, int wt, int crt, boolean[] rng, String[] eff, int[] bonus, int requiredLevel, int hpUsage, int tpUsage)
      //    Formula(String name, String description, FormulaType FT, String type, String attr, int mt, int hit, int wt, int crt, boolean[] rng, String[] eff, int[] bonus, int requiredLevel, int hpUsage, int tpUsage, Talent extraEffect)  
        new Formula(
                new FreelyAssociated(
                        "Anemo Schism", //name
                        "Basic formula comprised of delta ether; condenses ether around target to cut a rift in the air and fill the diffusing wind with dense delta ether"
                ),
                new DamageTool(
                        9, //pow
                        80, //acc
                        5, //crit
                        Arrays.asList(1, 2), //range
                        "delta ether",
                        "wind",
                        effAgainstNothing,
                        null
                ),
                ToolType.Attack,
                new Toll(Exchange.TP, 5)
        )
    };
    
    //WHEN TALKING ABOUT STATS, IT IS ALWAYS {MaxHP, Str, Ether, Agi, Dex, Comp, Def, Rsl, Mobility, Physique, Charisma}
    //WHEN TALKING ABOUT WEAPON STAT BONUSES IT IS ALWAYS { STR, ETHER, AGI, DEX, COMP, DEF, RSL }
    
    public static final JobClass[] ClassCatalog = 
    {   //  JobClass(String jobname, List<String> mobilityTypes, List<String> wieldableWeaponTypes, List<StatBundle> bonusStats, List<StatBundle> battleBonus, List<StatBundle> maxStats, int tier)
        new JobClass("Cowboy", Arrays.asList("cavalry"), Arrays.asList("whip", "knife"), baseCav(), noBattleBonus(),
                Arrays.asList( //max stats
                    new StatBundle(BaseStat.maxHP, 32),
                    new StatBundle(BaseStat.maxTP, 50),
                    new StatBundle(BaseStat.strength, 20),
                    new StatBundle(BaseStat.ether, 14),
                    new StatBundle(BaseStat.agility, 16),
                    new StatBundle(BaseStat.dexterity, 15),
                    new StatBundle(BaseStat.comprehension, 14),
                    new StatBundle(BaseStat.defense, 15),
                    new StatBundle(BaseStat.resilience, 12),
                    new StatBundle(BaseStat.mobility, 10),
                    new StatBundle(BaseStat.physique, 20),
                    new StatBundle(BaseStat.adrenaline, 30)
                ), 
                "A rogue who is skilled in the art of the whip",
                1 //tier
        ),
        new JobClass("Knight", Arrays.asList("cavalry"), Arrays.asList("sword", "polearm"), baseCav(), noBattleBonus(), 
                Arrays.asList( //max stats
                    new StatBundle(BaseStat.maxHP, 33),
                    new StatBundle(BaseStat.maxTP, 60),
                    new StatBundle(BaseStat.strength, 22),
                    new StatBundle(BaseStat.ether, 15),
                    new StatBundle(BaseStat.agility, 14),
                    new StatBundle(BaseStat.dexterity, 13),
                    new StatBundle(BaseStat.comprehension, 11),
                    new StatBundle(BaseStat.defense, 18),
                    new StatBundle(BaseStat.resilience, 12),
                    new StatBundle(BaseStat.mobility, 10),
                    new StatBundle(BaseStat.physique, 20),
                    new StatBundle(BaseStat.adrenaline, 30)
                ), 
                "One who has underwent military training to get this far",
                1 //tier
        ),
        new JobClass("Marauder", Arrays.asList("infantry"), Arrays.asList("axe"), tier0Bonuses(), noBattleBonus(), 
                Arrays.asList( //max stats
                    new StatBundle(BaseStat.maxHP, 35),
                    new StatBundle(BaseStat.maxTP, 30),
                    new StatBundle(BaseStat.strength, 28),
                    new StatBundle(BaseStat.ether, 10),
                    new StatBundle(BaseStat.agility, 13),
                    new StatBundle(BaseStat.dexterity, 11),
                    new StatBundle(BaseStat.comprehension, 9),
                    new StatBundle(BaseStat.defense, 13),
                    new StatBundle(BaseStat.resilience, 7),
                    new StatBundle(BaseStat.mobility, 10),
                    new StatBundle(BaseStat.physique, 15),
                    new StatBundle(BaseStat.adrenaline, 20)
                ), 
                "A peasant who has turned to the ways of violence due to their less fortunate status",
                0 //tier
        ),
        new JobClass("Freeblade", Arrays.asList("infantry"), Arrays.asList("sword"), tier0Bonuses(), noBattleBonus(), 
                Arrays.asList( //max stats
                    new StatBundle(BaseStat.maxHP, 31),
                    new StatBundle(BaseStat.maxTP, 50),
                    new StatBundle(BaseStat.strength, 20),
                    new StatBundle(BaseStat.ether, 17),
                    new StatBundle(BaseStat.agility, 17),
                    new StatBundle(BaseStat.dexterity, 17),
                    new StatBundle(BaseStat.comprehension, 19),
                    new StatBundle(BaseStat.defense, 17),
                    new StatBundle(BaseStat.resilience, 15),
                    new StatBundle(BaseStat.mobility, 10),
                    new StatBundle(BaseStat.physique, 15),
                    new StatBundle(BaseStat.adrenaline, 40)
                ), 
                "A youth who is unconventionally skilled with a blade not bound by a contract.",
                0 //tier
        )
            .addCustomSkillAnimation("Heavy Swing", 
                new CustomAnimationSegment(
                    "attack_and_followup",
                    FrameDelay.combineArray(
                        FrameDelay.allIntsFromTo(0, 15, 0), 
                        FrameDelay.combineArray(
                            FrameDelay.allIntsFromTo(18, 21, 0.02f), 
                            FrameDelay.combineArray(
                                new FrameDelay[] {new FrameDelay(22, 0.2f)},
                                FrameDelay.combineArray(
                                    new FrameDelay[] {new FrameDelay(23, 0.1f)}, 
                                    FrameDelay.combineArray(
                                        FrameDelay.allIntsFromTo(24, 28, 0.01f), 
                                        FrameDelay.allIntsFromTo(29, 47, 0)
                                    )
                                )
                            )
                        )
                    )
                )
            )
    };
    
    public static final Skill[] SkillCatalog = 
    { // Skill(String name, String desc, String path, Toll info)
        new Skill("Heavy Swing", "A mighty swing.", "heavy_swing.png", ToolType.Attack, new Toll(Exchange.TP, 3), 
                new SkillEffect(0, Arrays.asList(new StatBundle(BattleStat.Accuracy, 10), new StatBundle(BattleStat.AttackPower, 5))) {
                    @Override
                    public int extraDamage() {
                        return 1;
                    }
                }
            )
    };
    
    public static final FormationTechnique[] FormationTechniqueCatalog = {
        new FormationTechnique
            (
                "Fall Back", 
                "Both the user and its targeted ally, who is adjacent to the user, move one space backwards (in other words, one space in the direction of the user)",
                230 //priority for AI
            ) 
        {                 
            @Override
            public void useTechnique(Conveyer data) {
                TangibleUnit user = data.getUnit(), target = data.getOtherUnit();
                
                int xDirection = user.getPosX() - target.getPosX();
                int yDirection = user.getPosY() - target.getPosY();
                    
                int allyPosX = target.getPosX(), allyPosY = target.getPosY();
                
                user.getRequestDealer().getRequests().add(
                    new Request(RequestType.Ordinal, 5, false) {
                        private float xDistance = 0, yDistance = 0;
                        
                        @Override
                        protected boolean update(DataStructure data, float tpf) {
                            Conveyer conv = (Conveyer)data;
                            conv.getUnit().getNode().move(yDirection / 5f, 0, xDirection / 5f);
                            xDistance += xDirection / 5f;
                            yDistance += yDirection / 5f;
                            
                            return xDistance == xDirection && yDistance == yDirection;
                        }

                        @Override
                        protected void onFinish(DataStructure data) {
                            TangibleUnit user = ((Conveyer)data).getUnit();
                            user.remapPositions(user.getPosX() + xDirection, user.getPosY() + yDirection, user.getElevation(), MasterFsmState.getCurrentMap());
                        }
                    }
                );
                
                target.getRequestDealer().getRequests().add(
                    new Request(RequestType.Ordinal, 5, false) {
                        private float xDistance = 0, yDistance = 0;
                        
                        @Override
                        protected boolean update(DataStructure data, float tpf) {
                            Conveyer conv = (Conveyer)data;
                            conv.getOtherUnit().getNode().move(yDirection / 5f, 0, xDirection / 5f);
                            xDistance += xDirection / 5f;
                            yDistance += yDirection / 5f;
                            
                            return xDistance == xDirection && yDistance == yDirection;
                        }

                        @Override
                        protected void onFinish(DataStructure data) {
                            TangibleUnit target = ((Conveyer)data).getOtherUnit();
                            target.remapPositions(allyPosX + xDirection, allyPosY + yDirection, target.getElevation(), MasterFsmState.getCurrentMap());
                        }
                    }
                );
            }
            
            @Override
            public boolean getCondition(Conveyer data) {
                 TangibleUnit user = data.getUnit(), target = data.getOtherUnit();
                 if (!user.isAlliedWith(target.unitStatus)) { return false; }
                 
                 for (int i = 0; i < 2; i++) {
                     Tile possibilityX = MasterFsmState.getCurrentMap().fullmap[user.getElevation()][user.getPosX() + ((int)Math.cos(Math.PI * i))][user.getPosY()];
                     Tile possibilityY = MasterFsmState.getCurrentMap().fullmap[user.getElevation()][user.getPosX()][user.getPosY() + ((int)Math.cos(Math.PI * i))];
                     if ((possibilityX.getOccupier() != null) || (possibilityY.getOccupier() != null)) {
                         return true;
                     }
                 }
                 return false;
            }
        }
    };
    
    public static final Formation[] FormationCatalog = {
        //  Formation(String name, String desc, int tier, RawBroadBonus bonus, ToolType toolType, List<Integer> ranges, List<FormationTechnique> techniques)
        new Formation(
                "Trigonal Planar", 
                "A basic 3-point formation",
                1, //tier 1
                new StatBundle(BaseStat.mobility, 1), //boosts mobility by 1; Sagittarius
                ToolType.SupportAlly,
                Arrays.asList(1), //1 range
                Arrays.asList(FormationTechniqueCatalog[0])
        )
    };
    
    public static Unit[] UnitCatalog = 
         // baseStats = {LVL, MAXHP, STR, ETHER, AGI, DEX, COMP, DEF, RSL, MOBILITY, PHYSIQUE, CHARISMA}   
    {    // Unit(String name, JobClass jc, int[] baseStats, Item[] inventory, Talent[] talents, Ability[] abilities, Skill[] skills, Formation[] formations, boolean isBoss, String desc)
        new Unit(
                "Morva", //name
                ClassCatalog[3], //class
                Arrays.asList //base stats
                (
                    new StatBundle(BaseStat.level, 1),  //level
                    new StatBundle(BaseStat.maxHP, 28), //max hp
                    new StatBundle(BaseStat.maxTP, 0), //max tp
                    new StatBundle(BaseStat.strength, 8),  //strength
                    new StatBundle(BaseStat.ether, 5),  //ether
                    new StatBundle(BaseStat.agility, 16), //agility
                    new StatBundle(BaseStat.dexterity, 5),  //dexterity
                    new StatBundle(BaseStat.comprehension, 6),  //comprehension
                    new StatBundle(BaseStat.defense, 5),  //defense
                    new StatBundle(BaseStat.resilience, 2),  //resilience
                    new StatBundle(BaseStat.mobility, 4),  //mobility
                    new StatBundle(BaseStat.physique, 9),  //physique
                    new StatBundle(BaseStat.adrenaline, 10)  //adrenaline
                ),
                Arrays.asList //growth rates
                (
                    new StatBundle(BaseStat.level, 100),  //level
                    new StatBundle(BaseStat.maxHP, 60), //max hp
                    new StatBundle(BaseStat.maxTP, 10), //max tp
                    new StatBundle(BaseStat.strength, 55),  //strength
                    new StatBundle(BaseStat.ether, 45),  //ether
                    new StatBundle(BaseStat.agility, 45), //agility
                    new StatBundle(BaseStat.dexterity, 55),  //dexterity
                    new StatBundle(BaseStat.comprehension, 45),  //comprehension
                    new StatBundle(BaseStat.defense, 40),  //defense
                    new StatBundle(BaseStat.resilience, 30),  //resilience
                    new StatBundle(BaseStat.mobility, 5),  //mobility
                    new StatBundle(BaseStat.physique, 15),  //physique
                    new StatBundle(BaseStat.adrenaline, 50)  //adrenaline
                ),
                Arrays.asList(Weapon.Firangi(), ConsumableItem.Apple()), //base inventory
                Arrays.asList(FormulaCatalog[0]), //base formulas
                fillWith(emptyTalents(), Arrays.asList(Talent.EyeOfTheStorm(), Talent.Opportunist(), Talent.Optimism())), //base talents
                Arrays.asList(), //base abilities
                Arrays.asList(SkillCatalog[0]), //base skills
                Arrays.asList(FormationCatalog[0]), //base formations
                false //isBoss
            ),
        new Unit(
                "Pillager", 
                ClassCatalog[2],
                Arrays.asList //base stats
                (
                    new StatBundle(BaseStat.level, 1),  //level
                    new StatBundle(BaseStat.maxHP, 18), //max hp
                    new StatBundle(BaseStat.maxTP, 0), //max tp
                    new StatBundle(BaseStat.strength, 9),  //strength
                    new StatBundle(BaseStat.ether, 0),  //ether
                    new StatBundle(BaseStat.agility, 2), //agility
                    new StatBundle(BaseStat.dexterity, 2),  //dexterity
                    new StatBundle(BaseStat.comprehension, 1),  //comprehension
                    new StatBundle(BaseStat.defense, 4),  //defense
                    new StatBundle(BaseStat.resilience, 0),  //resilience
                    new StatBundle(BaseStat.mobility, 4),  //mobility
                    new StatBundle(BaseStat.physique, 12),  //physique
                    new StatBundle(BaseStat.adrenaline, 0)  //adrenaline
                ),
                ((HashMap<BaseStat, Integer>)Unit.DEFAULT_ENEMY_GROWTH_RATES.clone()),
                Arrays.asList(Weapon.Francisca()), 
                Arrays.asList(), 
                emptyTalents(), 
                Arrays.asList(), 
                Arrays.asList(), 
                Arrays.asList(), 
                false
        )
    };
}

