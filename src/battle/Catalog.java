/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle;

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
import battle.talent.PassiveTalent;
import battle.talent.TalentCondition;
import battle.talent.TalentConcept;
import battle.talent.TalentEffect;
import battle.talent.Talent;
import battle.talent.Differentiate;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import maps.layout.Map;
import maps.layout.TangibleUnit;
import maps.layout.Tile;
import misc.CustomAnimationSegment;
import misc.FrameDelay;
import etherealtempest.MasterFsmState;
/**
 *
 * @author night
 */
public class Catalog {
    public static final int[] noStatBonuses = {0, 0, 0, 0, 0, 0, 0};
    public static final int[] noBattleBonus = {0, 0, 0, 0, 0, 0, 0, 0};
    public static final String[] effAgainstNothing = {"None"};
                            //    {maxhp, str, ether, agi, dex, comp, def, rsl, mobility, physique, charisma} 
    public static final int[] baseCav = {0, 1, 0, 1, 1, 0, 3, 0, 3, 10, 0};
    public static final int[] tier0Bonuses = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public static final String[] infantry = {"infantry"};
    public static final String[] pureCav = {"cavalry"};
    
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
      new Weapon("Firangi", "An extremely rare offensive sword of unknown origin. Many are found lying on the ground or at the bottom of a pond.", "sword", "metal", 8, 95, 6, 15, rangeCreator(new int[]{1}), 45.0, new String[]{"cavalry", "mechanism"}, new int[]{0, 1, 0, 1, 5, 1, 1}, 0, "Morva", 5000),
      new Weapon("Cutlass", "An amateur sword designed for close-quarters fighting that is cheap to make. Favored in naval battles", "sword", "metal", 7, 90, 5, 0, rangeCreator(new int[]{1}), 40.0, effAgainstNothing, noStatBonuses, 1, "None", 500),
      new Weapon("Rebel Pike", "A pike that becomes stronger when the user's HP is less than or equal to half", "polearm", "metal", 11, 75, 9, 5, rangeCreator(new int[]{1}), 35.0, effAgainstNothing, noStatBonuses, 5, "None", 1000),
      new Weapon("Copper Shortsword", "A basic shortsword designed for quick cuts and maneuvers.", "sword", "metal", 5, 100, 3, 0, rangeCreator(new int[]{1}), 40.0, effAgainstNothing, noStatBonuses, 0, "None", 300),
      new Weapon("Steel Broadsword", "A strong sword considered an essential by skilled swordsman.", "sword", "metal", 9, 85, 8, 3, rangeCreator(new int[]{1}), 35.0, effAgainstNothing, noStatBonuses, 2, "None", 700),
      new Weapon("Svardstav", "A lance-like sword designed to combat cavalry foes.", "sword", "metal", 8, 75, 10, 0, rangeCreator(new int[]{1}), 30.0, new String[]{"cavalry"}, noStatBonuses, 5, "None", 1000),
      new Weapon("Francisca", "An axe that is extremely popular amongst peasants, as it is both cheap and effective", "axe", "metal", 9, 70, 11, 0, rangeCreator(new int[]{1}), 35.0, effAgainstNothing, noStatBonuses, 0, "None", 300),
      new Weapon("Glaive", "A powerful lance used for cutting down foes", "polearm", "metal", 12, 80, 9, 5, rangeCreator(new int[]{1}), 40.0, effAgainstNothing, noStatBonuses, 10, "None", 1500)
    };
    
    public static Formula[] FormulaCatalog = 
    { //    Formula(String name, String description, FormulaType FT, String type, String attr, int mt, int hit, int wt, int crt, boolean[] rng, String[] eff, int[] bonus, int requiredLevel, int hpUsage, int tpUsage)
      //    Formula(String name, String description, FormulaType FT, String type, String attr, int mt, int hit, int wt, int crt, boolean[] rng, String[] eff, int[] bonus, int requiredLevel, int hpUsage, int tpUsage, Talent extraEffect)  
        new Formula(
                "Anemo Schism", 
                "Basic formula comprised of delta ether; condenses ether around target to cut a rift in the air and fill the diffusing wind with dense delta ether", 
                FormulaType.Attack,
                "delta ether", "wind", 9, 80, 5, rangeCreator(new int[]{1, 2}), effAgainstNothing, noStatBonuses, 0, 0, 5)
    };
    
    //WHEN TALKING ABOUT STATS, IT IS ALWAYS {MaxHP, Str, Ether, Agi, Dex, Comp, Def, Rsl, Mobility, Physique, Charisma}
    //WHEN TALKING ABOUT WEAPON STAT BONUSES IT IS ALWAYS { STR, ETHER, AGI, DEX, COMP, DEF, RSL }
    
    public static final JobClass[] ClassCatalog = 
    {   //  JobClass(String jobname, int[] bonusStats, String[] mobilitytype, boolean[] wieldableWeapons, int[] battleBonus, int tier, int[] maxStats)
        new JobClass("Cowboy", baseCav, pureCav, rangeCreator(new int[]{3, 5}), noBattleBonus, 1, new int[]{32, 20, 14, 16, 15, 14, 15, 12, 10, 20, 30}),
        new JobClass("Knight", baseCav, pureCav, rangeCreator(new int[]{0, 2}), noBattleBonus, 1, new int[]{33, 22, 15, 14, 13, 11, 18, 12, 10, 20, 30}),
        new JobClass("Marauder", tier0Bonuses, infantry, rangeCreator(new int[]{1}), noBattleBonus, 0, new int[]{35, 28, 10, 13, 11, 9, 13, 7, 10, 15, 20}),
        new JobClass("Freeblade", tier0Bonuses, infantry, rangeCreator(new int[]{0}), noBattleBonus, 0, new int[]{31, 20, 17, 17, 17, 19, 17, 15, 10, 15, 40})
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
    
    public static Differentiate[] BattleTalentEffectCatalog() {
        Differentiate[] D = 
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
                    return convey.getUnit().getEquippedWeapon().getExistence() && convey.getUnit().getEquippedWeapon().poweredByElement.length() > 2;
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
                public int[] battleBonusStats() { return null; }
                @Override
                public int[] rawBonusStats() { return null; }
                @Override
                public int[] temporaryBuffs() { return null; }
                @Override
                public int[] temporaryEnemyDebuffs() { return null; }
                @Override
                public int[] userTranslation() { return null; }
                @Override
                public int[] enemyTranslation() { return null; }
                @Override
                public int[] enemyAOEDMG() { return null; }
            
                @Override
                public void enactEffect() {
                    convey.getEnemyUnit().parryDecider = false;
                }
            },
            new TalentEffect() { //aoe damage: 1/2 user's ether stat
                Conveyer convey;
                @Override
                public void inputData(Conveyer data) {
                    convey = data;
                }

                @Override
                public int[] battleBonusStats() { return null; }
                @Override
                public int[] rawBonusStats() { return null; }
                @Override
                public int[] temporaryBuffs() { return null; }
                @Override
                public int[] temporaryEnemyDebuffs() { return null; }
                @Override
                public int[] userTranslation() { return null; }
                @Override
                public int[] enemyTranslation() { return null; }
            
                @Override
                public int[] enemyAOEDMG() { 
                    return new int[]{(int)(0.5f * convey.getUnit().getETHER()), 3}; 
                }

                @Override
                public void enactEffect() {
                    for (int var = -1; var < 2; var+= 2) {
                        for (int xUsed = 1; xUsed <= 3; xUsed++) {
                            for (int yUsed = 1; yUsed <= 3 && xUsed + yUsed <= 3; yUsed++) {
                                if (
                                    convey.getUnit().unitStatus.getValue() != convey.getMap().fullmap[convey.getEnemyUnit().getElevation()][convey.getEnemyUnit().getPosX() + (xUsed * var)][convey.getEnemyUnit().getPosY() + (yUsed * var)].getOccupier().unitStatus.getValue()
                                    && convey.getUnit().unitStatus.getValue() + convey.getMap().fullmap[convey.getEnemyUnit().getElevation()][convey.getEnemyUnit().getPosX() + (xUsed * var)][convey.getEnemyUnit().getPosY() + (yUsed * var)].getOccupier().unitStatus.getValue() > -1) 
                                { //if they are an enemy
                                    convey.getMap().fullmap[convey.getEnemyUnit().getElevation()][convey.getEnemyUnit().getPosX() + (xUsed * var)][convey.getEnemyUnit().getPosY() + (yUsed * var)].getOccupier().currentHP -= enemyAOEDMG()[0];
                                    if (convey.getMap().fullmap[convey.getEnemyUnit().getElevation()][convey.getEnemyUnit().getPosX() + (xUsed * var)][convey.getEnemyUnit().getPosY() + (yUsed * var)].getOccupier().currentHP <= 0) {
                                        convey.getMap().fullmap[convey.getEnemyUnit().getElevation()][convey.getEnemyUnit().getPosX() + (xUsed * var)][convey.getEnemyUnit().getPosY() + (yUsed * var)].getOccupier().currentHP = 1;
                                    }
                                }
                            }
                        }
                    }
                }
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
            public void useTechnique(TangibleUnit user, Object[] entitiesInvolved) {
                if (entitiesInvolved.length == 1 && entitiesInvolved[0] instanceof TangibleUnit) {
                    int xDirection = user.getPosX() - ((TangibleUnit)entitiesInvolved[0]).getPosX();
                    int yDirection = user.getPosY() - ((TangibleUnit)entitiesInvolved[0]).getPosY();
                    
                    int allyPosX = ((TangibleUnit)entitiesInvolved[0]).getPosX(), allyPosY = ((TangibleUnit)entitiesInvolved[0]).getPosY();
                    
                    user.remapPositions(user.getPosX() + xDirection, user.getPosY() + yDirection, user.getElevation(), MasterFsmState.getCurrentMap());
                    ((TangibleUnit)entitiesInvolved[0]).remapPositions(allyPosX + xDirection, allyPosY + yDirection, ((TangibleUnit)entitiesInvolved[0]).getElevation(), MasterFsmState.getCurrentMap());
                }
            }
            
            @Override
            public boolean getCondition(TangibleUnit user) {
                 boolean test = false;
                 //Tile[] possibilities = {currentMap.fullmap[user.getElevation()][user.getPosX() + ][user.getPosY()]};
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
                new int[] //base stats
                {
                    1,  //level
                    28, //max hp
                    8,  //strength
                    5,  //ether
                    16, //agility
                    5,  //dexterity
                    6,  //comprehension
                    5,  //defense
                    2,  //resilience
                    4,  //mobility
                    9,  //physique
                    10  //adrenaline
                },
                new int[] //growth rates
                {
                    100, //level
                    60,  //max hp
                    55,  //strength
                    45,  //ether
                    45,  //agility
                    55,  //dexterity
                    45,  //comprehension
                    40,  //defense
                    30,  //resilience
                    5,   //mobility
                    15,  //physique
                    50   //adrenaline
                },
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
                new int[]{1, 18, 9, 0, 2, 2, 1, 4, 0, 4, 12, 0},
                Unit.DEFAULT_ENEMY_GROWTH_RATES.clone(),
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
    
    public static void initializeCatalogImages(AssetManager assetManager) {
        //UnitCatalog[0].initializeFrames(assetManager);
        //UnitCatalog[1].initializeFrames(assetManager);
        UnitCatalog[0].portraitString = "Textures/gui/morvasquare.png";
        UnitCatalog[1].portraitString = "Textures/gui/r.png";
    }
    
}

