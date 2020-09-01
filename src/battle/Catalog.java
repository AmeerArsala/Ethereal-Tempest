/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle;

import battle.Combatant.BaseStat;
import battle.Combatant.BattleStat;
import battle.item.Item;
import battle.item.ConsumableItem;
import battle.item.ItemEffect;
import battle.item.Weapon;
import battle.ability.Ability;
import battle.formation.Formation;
import battle.formation.FormationTechnique;
import battle.formula.Formula;
import battle.skill.Skill;
import battle.Toll.Exchange;
import battle.formula.Formula.FormulaType;
import battle.skill.SkillEffect;
import battle.talent.BattleTalent;
import battle.talent.BattleTalentEffect;
import battle.talent.PassiveTalent;
import battle.talent.TalentCondition;
import battle.talent.TalentConcept;
import battle.talent.TalentEffect;
import battle.talent.Talent;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import etherealtempest.DataStructure;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import maps.layout.Map;
import maps.layout.TangibleUnit;
import maps.layout.Tile;
import misc.CustomAnimationSegment;
import misc.FrameDelay;
import etherealtempest.MasterFsmState;
import etherealtempest.Request;
import etherealtempest.Request.RequestType;
import fundamental.Bonus;
import fundamental.DamageTool;
import fundamental.FreelyAssociated;
import fundamental.StatBundle;
import general.visual.VisualTransition;
import java.util.HashMap;
import maps.layout.Coords;
import maps.layout.TangibleUnit.UnitStatus;
/**
 *
 * @author night
 */
public class Catalog {
    //referring to weapons
    public static final int[] noStatBonuses = {0, 0, 0, 0, 0, 0, 0};
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
    
    public static List<Item> emptyInventory() { return Arrays.asList(new Item(false), new Item(false), new Item(false), new Item(false), new Item(false), new Item(false), new Item(false)); } //7 items max
    public static List<Talent> emptyTalents() { return Arrays.asList(new Talent(false), new Talent(false), new Talent(false), new Talent(false), new Talent(false), new Talent(false)); } //6 Talents max
    public static List<Skill> emptySkills() { return Arrays.asList(new Skill(false), new Skill(false), new Skill(false), new Skill(false), new Skill(false)); } //5 Skills max
    public static final List<Formation> emptyFormations() { return Arrays.asList(new Formation(false), new Formation(false), new Formation(false)); } //3 formations max
    public static final List<Ability> emptyAbilities() { return Arrays.asList(new Ability(false), new Ability(false), new Ability(false), new Ability(false), new Ability(false), new Ability(false)); } //6 abilities max
    public static final List<Formula> emptyFormulas() { return Arrays.asList(new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula(), new Formula()); } //15 formulas max
    
    //private static Map currentMap;
    
    //public static void setCurrentMap(Map M) { currentMap = M; }
    
    public static boolean[] rangeCreator(int[] UpUntil) {
        boolean[] rc = new boolean[50];
        for (int i = 0; i < rc.length; i++) { rc[i] = false; }
        for (int x : UpUntil) { rc[x] = true; }
        return rc;
    }
    
    public static boolean[] rangeCreator(List<Integer> UpUntil) {
        boolean[] rc = new boolean[50];
        for (int i = 0; i < rc.length; i++) { rc[i] = false; }
        for (int x : UpUntil) { rc[x] = true; }
        return rc;
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
    
    public static Weapon[] WeaponCatalog = 
    { //  Weapon(String name, String description, String type, String attr, int mt, int hit, int wt, int crt, boolean[] rng, double durability, String[] eff, int[] bonus, int requiredLevel, String prf, int worth)
      new Weapon(
              new Item("Firangi", //weapon name
                      "An extremely rare offensive sword of unknown origin. Many are found lying on the ground or at the bottom of a pond.",  //description
                      6, //weight 
                      5000 //worth
              ), 
              new DamageTool(
                      8,  //pow
                      95, //acc
                      15, //crit 
                      Arrays.asList(1), //range
                      Arrays.asList( //bonuses when equipped
                              new Bonus(1, BaseStat.ether),
                              new Bonus(5, BaseStat.comprehension),
                              new Bonus(1, BaseStat.dexterity),
                              new Bonus(1, BaseStat.defense),
                              new Bonus(1, BaseStat.resilience)
                      ),
                      "sword", //type
                      "metal", //attribute
                      new String[]{"cavalry", "mechanism"} //what it is effective against
              ),
              45.0, //durability
              0, //required level
              "Morva" //prf
        ),
      new Weapon(
              new Item("Cutlass", //weapon name
                      "An amateur sword designed for close-quarters fighting that is cheap to make. Favored in naval battles.",  //description
                      5, //weight 
                      500 //worth
              ), 
              new DamageTool(
                      7,  //pow
                      90, //acc
                      0, //crit 
                      Arrays.asList(1), //range
                      Arrays.asList( //bonuses when equipped
                      ),
                      "sword", //type
                      "metal", //attribute
                      effAgainstNothing //what it is effective against
              ),
              40.0, //durability
              0, //required level
              "None" //prf
        ),
      new Weapon(
              new Item("Rebel Pike", //weapon name
                      "A pike that becomes stronger when the user's HP is less than or equal to half.",  //description
                      9, //weight 
                      1000 //worth
              ), 
              new DamageTool(
                      11,  //pow
                      75, //acc
                      5, //crit 
                      Arrays.asList(1), //range
                      Arrays.asList( //bonuses when equipped
                      ),
                      "polearm", //type
                      "metal", //attribute
                      effAgainstNothing //what it is effective against
              ),
              35.0, //durability
              5, //required level
              "None" //prf
        ),
      new Weapon(
              new Item("Copper Shortsword", //weapon name
                      "A basic shortsword designed for quick cuts and maneuvers.",  //description
                      3, //weight 
                      300 //worth
              ), 
              new DamageTool(
                      5,  //pow
                      100, //acc
                      0, //crit 
                      Arrays.asList(1), //range
                      Arrays.asList( //bonuses when equipped
                      ),
                      "sword", //type
                      "metal", //attribute
                      effAgainstNothing //what it is effective against
              ),
              40.0, //durability
              0, //required level
              "None" //prf
        ),
      new Weapon(
              new Item("Steel Broadsword", //weapon name
                      "A strong sword considered an essential by skilled swordsman.",  //description
                      8, //weight 
                      700 //worth
              ), 
              new DamageTool(
                      9,  //pow
                      85, //acc
                      3, //crit 
                      Arrays.asList(1), //range
                      Arrays.asList( //bonuses when equipped
                      ),
                      "sword", //type
                      "metal", //attribute
                      effAgainstNothing //what it is effective against
              ),
              35.0, //durability
              2, //required level
              "None" //prf
        ),
      new Weapon(
              new Item("Svardstav", //weapon name
                      "A lance-like sword designed to combat cavalry foes.",  //description
                      10, //weight 
                      1000 //worth
              ), 
              new DamageTool(
                      8,  //pow
                      75, //acc
                      0, //crit 
                      Arrays.asList(1), //range
                      Arrays.asList( //bonuses when equipped
                      ),
                      "sword", //type
                      "metal", //attribute
                      new String[]{"cavalry"} //what it is effective against
              ),
              30.0, //durability
              5, //required level
              "None" //prf
        ),
      new Weapon(
              new Item("Francisca", //weapon name
                      "An axe that is extremely popular amongst peasants, as it is both cheap and effective.",  //description
                      11, //weight 
                      300 //worth
              ), 
              new DamageTool(
                      9,  //pow
                      70, //acc
                      0, //crit 
                      Arrays.asList(1), //range
                      Arrays.asList( //bonuses when equipped
                      ),
                      "axe", //type
                      "metal", //attribute
                      effAgainstNothing //what it is effective against
              ),
              35.0, //durability
              0, //required level
              "None" //prf
        ),
        new Weapon(
              new Item("Glaive", //weapon name
                      "A powerful lance used for cutting down foes.",  //description
                      9, //weight 
                      1500 //worth
              ), 
              new DamageTool(
                      12,  //pow
                      80, //acc
                      5, //crit 
                      Arrays.asList(1), //range
                      Arrays.asList( //bonuses when equipped
                      ),
                      "polearm", //type
                      "metal", //attribute
                      effAgainstNothing //what it is effective against
              ),
              40.0, //durability
              10, //required level
              "None" //prf
        )
    };
    
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
                        Arrays.asList(), //bonuses
                        "delta ether",
                        "wind",
                        effAgainstNothing
                ),
                FormulaType.Attack,
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
    
    public static BattleTalentEffect[] BattleTalentEffectCatalog() {
        BattleTalentEffect[] D = 
        {
            
        };
        return D;
    } 
    
    public static TalentCondition[] TalentConditionCatalog() {
        TalentCondition[] TC = 
        {
            new TalentCondition() { //always triggers
                @Override
                public void inputData(Conveyer data) {
                
                }

                @Override
                public boolean getCondition() {
                    return true;
                }
            },
            new TalentCondition() { //triggers if user's equipped weapon is powered by an element
                Conveyer convey;
                @Override
                public void inputData(Conveyer data) {
                    convey = data;
                }

                @Override
                public boolean getCondition() {
                    return convey.getUnit().getEquippedWPN() != null && convey.getUnit().getEquippedWPN().poweredByElement.length() > 2;
                }
            }
        };
        return TC;
    }
    
    public static TalentEffect[] TalentEffectCatalog() {
        TalentEffect[] TE = 
        {
            new TalentEffect() { //no parrying
                Conveyer convey;
            
                @Override
                public void inputData(Conveyer data) {
                    convey = data;
                }
            
                @Override
                public void enactEffect() {
                    convey.getEnemyUnit().parryDecider = false;
                }

                @Override
                public Coords userTranslation() { return null; }
                @Override
                public Coords enemyTranslation() { return null; }
                @Override
                public Vector2f enemyAOEDMG() { return null; }
                @Override
                public List<Bonus> Buffs() { return null; }

                @Override
                public void onPhaseBegin(UnitStatus phase) {}
            },
            new TalentEffect() { //aoe damage: 1/2 user's ether stat
                Conveyer convey;
                
                @Override
                public void inputData(Conveyer data) {
                    convey = data;
                }

                @Override
                public Coords userTranslation() { return null; }
                @Override
                public Coords enemyTranslation() { return null; }
                @Override
                public Vector2f enemyAOEDMG() { return new Vector2f((int)(0.5f * convey.getUnit().getETHER()), 3); }
                @Override
                public List<Bonus> Buffs() { return null; }

                @Override
                public void enactEffect() {
                    for (int var = -1; var < 2; var+= 2) {
                        for (int xUsed = 1; xUsed <= 3; xUsed++) {
                            for (int yUsed = 1; yUsed <= 3 && xUsed + yUsed <= 3; yUsed++) {
                                if (
                                    convey.getUnit().unitStatus.getValue() != convey.getMap().fullmap[convey.getEnemyUnit().getElevation()][convey.getEnemyUnit().getPosX() + (xUsed * var)][convey.getEnemyUnit().getPosY() + (yUsed * var)].getOccupier().unitStatus.getValue()
                                    && convey.getUnit().unitStatus.getValue() + convey.getMap().fullmap[convey.getEnemyUnit().getElevation()][convey.getEnemyUnit().getPosX() + (xUsed * var)][convey.getEnemyUnit().getPosY() + (yUsed * var)].getOccupier().unitStatus.getValue() > -1) 
                                { //if they are an enemy
                                    convey.getMap().fullmap[convey.getEnemyUnit().getElevation()][convey.getEnemyUnit().getPosX() + (xUsed * var)][convey.getEnemyUnit().getPosY() + (yUsed * var)].getOccupier().currentHP -= ((int)enemyAOEDMG().x);
                                    if (convey.getMap().fullmap[convey.getEnemyUnit().getElevation()][convey.getEnemyUnit().getPosX() + (xUsed * var)][convey.getEnemyUnit().getPosY() + (yUsed * var)].getOccupier().currentHP <= 0) {
                                        convey.getMap().fullmap[convey.getEnemyUnit().getElevation()][convey.getEnemyUnit().getPosX() + (xUsed * var)][convey.getEnemyUnit().getPosY() + (yUsed * var)].getOccupier().currentHP = 1;
                                    }
                                }
                            }
                        }
                    }
                }

            @Override
            public void onPhaseBegin(UnitStatus phase) {}
            
            }
        };
        return TE;
    }
    
    public static TalentConcept[] TalentConceptCatalog = 
    {
        new TalentConcept("Nullify parries", TalentConditionCatalog()[0], TalentEffectCatalog()[0]),
        new TalentConcept("Does AOE damage to enemies within 3 spaces of attacked enemy: 1/2 of user's ETHER stat", TalentConditionCatalog()[1], TalentEffectCatalog()[1])
    };
    
    public static BattleTalent[] BattleTalentCatalog = 
    {
        
    };
    
    public static PassiveTalent[] PassiveTalentCatalog = 
    {
        new PassiveTalent(
                "Eye of the Storm", 
                "In the midst of fighting, user can witness the flow of battle to see and disrupt enemy parrying as well as unleash the element their weapon is powered by in an area of effect.",
                "Eye of the Storm\n \nEffects:\nEnemies cannot parry against user's attacks. If user's equipped weapon is powered by an element and user initiates combat, does area-of-effect damage = 50% of user's ETHER stat to enemies within 3 spaces of attacked enemy after combat",
                "Interface/GUI/talent_icons/eyeofthestorm.png",
                TalentConceptCatalog[0], TalentConceptCatalog[1])
    };
    
    public static Skill[] SkillCatalog = 
    { // Skill(String name, String desc, String path, Toll info)
        new Skill("Heavy Swing", "A mighty swing.", "Interface/GUI/skill_icons/heavy_swing.png", new Toll(Exchange.TP, 3), 
                new SkillEffect() {
                    @Override
                    public void setBattleStats() {
                        atkpwrBonus = 5;
                        accuracyBonus = 10;
                    }
                    
                    @Override
                    public int extraDamage() {
                        return 1;
                    }
                    
                    @Override
                    public int extraHits() {
                        return 0;
                    }
                }
            )
    };
    
    public static ItemEffect[] ItemEffectCatalog = 
    {
        new ItemEffect() {
            @Override
            public int HPrestoration() {
                return restoredHPValue;
            }

            @Override
            public int TPrestoration() {
                return restoredTPValue;
            }

            @Override
            public int[] tempBonusStats(Conveyer C) {
                return null;
            }

            @Override
            public int[] permanentBonusStats(Unit U) {
                return null;
            }

            @Override
            public void enactEffect(Conveyer C) {
                C.getUnit().currentHP += HPrestoration();
                //do animation on map screen
            } 
        }.setHPrestoration(5)
    };
    
    public static Item[] ItemCatalog = 
    {
        
    };
    
    public static ConsumableItem[] ConsumableItemCatalog = 
    { //    ConsumableItem(String consumableName, String description, int weight, int worth, int maxUses, ItemEffect effect, String iconPath)
        new ConsumableItem("Apple", "Restores 5 health\nWeight: 1", 1, 15, 3, ItemEffectCatalog[0], "apple.png")
    };
    
    public static Ability[] AbilityCatalog = 
    {
    
    };
    
    public static FormationTechnique[] FormationTechniqueCatalog = {
        new FormationTechnique() {
            @Override
            public String getName() { return "Fall Back"; }
                              
            @Override
            public String getDescription() {
                return "Both the user and its targeted ally, who is adjacent to the user, move one space backwards (in other words, one space in the direction of the user)";
            }
                              
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
                            conv.getUnit().getGeometry().move(yDirection / 5f, 0, xDirection / 5f);
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
                            conv.getOtherUnit().getGeometry().move(yDirection / 5f, 0, xDirection / 5f);
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
                 if (!user.isAlliedWith(target)) { return false; }
                 
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
    
    public static Formation[] FormationCatalog = {
        //  Formation(String name, String desc, String type, boolean elite, int stars, String imageName, FormationTechnique[] techniques)
        new Formation("Base Platoon", "A small platoon of guards from the Juclen Orphanage sent by the headmaster", "diamond", false, 2, "base_platoon.png",
                      new FormationTechnique[]{FormationTechniqueCatalog[0]})
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
                Arrays.asList(WeaponCatalog[0].getNewWeaponInstance(), ConsumableItemCatalog[0].newItemInstance(), new Item(false), new Item(false), new Item(false), new Item(false), new Item(false)), //base inventory
                replaceArrSlotWith(emptyFormulas(), FormulaCatalog[0], 0), //base formulas
                replaceArrSlotWith(emptyTalents(), PassiveTalentCatalog[0], 0), //base talents
                emptyAbilities(), //base abilities
                replaceArrSlotWith(emptySkills(), SkillCatalog[0].getNewSkillInstance(), 0), //base skills
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
                Arrays.asList(WeaponCatalog[6].getNewWeaponInstance(), new Item(false), new Item(false), new Item(false), new Item(false), new Item(false), new Item(false)), 
                emptyFormulas(), 
                emptyTalents(), 
                emptyAbilities(), 
                emptySkills(), 
                emptyFormations(), 
                false
        )
    };
    
    //TODO: add an arraylist catalog for maps too


    public static Object[][] FullCatalog = {ClassCatalog, SkillCatalog, ItemCatalog, AbilityCatalog, FormationCatalog, UnitCatalog};
    public static Object[][] ExtendedCatalog = {WeaponCatalog};
    
    public static <T> T findSlotByName(String s, T exampleParam) { //for exampleParam, you MUST instantiate it as a test
            for (Object[] lst : FullCatalog) {
                for (Object slot : lst) {
                    if (s.equalsIgnoreCase(((T)slot).toString())) {
                        return ((T)slot);
                    }
                }
            }
            return exampleParam;
    }
    
    public static <T> T findSlotByName(String s, T[] search) {
        for (T slot : search) {
            if (s.equalsIgnoreCase(slot.toString())) {
                return slot;
            }
        }
        return search[0];
    }
}

