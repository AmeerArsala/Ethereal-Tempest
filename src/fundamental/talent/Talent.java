/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.talent;

import battle.participant.Combatant;
import fundamental.stats.BaseStat;
import battle.participant.BattleRole;
import etherealtempest.info.Conveyor;
import fundamental.Attribute;
import fundamental.skill.Skill;
import fundamental.stats.Bonus;
import fundamental.stats.Bonus.BonusType;
import fundamental.stats.Toll.Exchange;
import fundamental.talent.TalentCondition.Occasion;
import fundamental.tool.Tool;
import fundamental.tool.Tool.ToolType;
import fundamental.unit.AgainstUnits;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import maps.layout.MapCoords;
import maps.layout.occupant.character.TangibleUnit;

/**
 *
 * @author night
 */
public class Talent extends Attribute {
    private String loredesc = "";
    private String imagePath = "";
    private ToolType type;
    
    private List<TalentConcept> body = new ArrayList<>();
    
    //private boolean actualTalent = true;
    
    //use this constructor
    public Talent(String talentname, ToolType t_type, String lore, String imgPath, List<TalentConcept> tc) {
        super(talentname, talentname + " \n " + lore + "\n \nEffects: \n" + generateDescription(tc));
        type = t_type;
        loredesc = lore;
        imagePath = "Interface/GUI/icons/talent/" + imgPath;
        body = tc;
    }
    
    //use custom description
    public Talent(String talentname, ToolType t_type, String lore, String description, String imgPath, List<TalentConcept> tc) {
        super(talentname, description);
        type = t_type;
        loredesc = lore;
        imagePath = "Interface/GUI/icons/talent/" + imgPath;
    }
    
    //used to shorten constructor for talents that aren't actual talents but provide bonuses to Units such as standing on Tiles
    protected Talent(ToolType t_type, List<TalentConcept> tc) {
        super("", generateDescription(tc));
        type = t_type;
        body = tc;
    }
    
    public static final String generateDescription(List<TalentConcept> tc) {
        String desc = "";
        
        for (TalentConcept concept : tc) {
            desc += concept.toString() + "\n";
        }
        
        return ("" + desc.charAt(0)).toUpperCase() + desc.substring(1); //capitalize first letter
    }
    
    public ToolType getToolType() { return type; }
 
    public String getLoreDescription() { return loredesc; }
    public String getIconPath() { return imagePath; }
    
    public List<TalentConcept> getFullBody() { return body; }
    
    //public boolean getIsActualTalent() { return actualTalent; }
    //void setActualTalent(boolean is) { actualTalent = is; }
    
    @Override
    public String toString() { return name; }
    
    /*
     * ToolType.SupportSelf is when it supports yourself
     * ToolType.SupportAlly is when it supports an ally
     * Both of the above are calculated when engaging in healing
     * ToolType.Attack is when a Unit attacks another; the skills, talents, and abilities under this category will be used in attack sequences
     */
    
    
    public static final Talent createEffect(ToolType t_type, List<TalentConcept> tc) {
        return new Talent(t_type, tc);
    }
    
    public static final Talent Opportunist() { //basically vantage
        return new Talent(
            "Opportunist", 
            ToolType.Attack, 
            "No matter the odds, there is always an opening.", 
            "opportunist.png", 
            Arrays.asList(
                new TalentConcept(
                    "If user's HP <= 50% and can counterattack against foe, user always attacks first during combat.",
                    new TalentCondition(Occasion.BeforeCombat) {
                        @Override
                        protected boolean getCondition(Conveyor data) {
                            Combatant cbt = data.getCombatantByUnit(data.getUnit());
                            
                            if (cbt == null) { return false; }
                            
                            Tool tool = data.getEnemyUnit().getEquippedTool();
                            int range = tool.getRange().get(tool.getRange().size() - 1);
                            Skill skl = data.getEnemyUnit().getToUseSkill();
                            if (skl != null) {
                                List<Integer> actualRanges = skl.getEffect().getTrueRange(tool);
                                range = actualRanges.get(actualRanges.size() - 1);
                            }
                            
                            return cbt.battle_role == BattleRole.Receiver && cbt.getUnit().canCounterattackAgainst(range);
                        }
                    },
                    new TalentEffect() {
                        @Override
                        public void enactEffect(Conveyor info) {
                            info.getInitiator().battle_role = info.getInitiator().battle_role.getOpponent();
                            info.getReceiver().battle_role = info.getReceiver().battle_role.getOpponent();
                        }
                    }
                )
            )
        );
    }
    
    public static final Talent EyeOfTheStorm() {
        return new Talent(
            "Eye of the Storm",
            ToolType.SupportSelf,
            "The user brings a storm with them wherever they go.",
            "eyeofthestorm.png",
            Arrays.asList(
                new TalentConcept(
                    TalentCondition.AlwaysTriggersOnOccasion(Occasion.AfterCombat),
                    TalentEffect.PercentageStatBasedAOE( //50% of ether stat as damage to enemies within 3 range
                        0.5f, //%
                        BaseStat.Ether, //stat
                        Exchange.HP, //penalty type
                        3, //range
                        AgainstUnits.Enemy
                    ) 
                )
            )
        );
    }
    
    public static final Talent Optimism() {
        return new Talent(
            "Optimism",
            ToolType.SupportSelf,
            "The cup is half-filled.",
            "optimism.png",
            Arrays.asList(
                new TalentConcept(
                    new TalentCondition("if user's HP >= 50%, ", Occasion.StartOfTurn) {
                        @Override
                        protected boolean getCondition(Conveyor data) {
                            return data.getUnit().getCurrentToMaxHPratio() >= 0.5f;
                        }
                    },
                    new TalentEffect("increases a random stat (excluding HP, TP, and Adrenaline) by 4 points for an entire turn") {
                        @Override
                        protected List<Bonus> userBuffs(Conveyor data) { //buffs to self
                            List<Bonus> bonuses = new ArrayList<>();
                            int length = BaseStat.values().length;
                            
                            List<BaseStat> blacklistedStats = Arrays.asList(BaseStat.MaxHP, BaseStat.CurrentHP, BaseStat.MaxTP, BaseStat.CurrentTP, BaseStat.Adrenaline);
                            BaseStat stat;
                            
                            do {
                                stat = BaseStat.values()[(int)(length * Math.random())];
                            } while (blacklistedStats.contains(stat));
                            
                            bonuses.add(new Bonus(4, BonusType.FullTurn, stat));
                            return bonuses;
                        }
                    }
                )
            )
        );
    }
    
    public static final Talent TileBonus(MapCoords pos, List<Bonus> bonuses, Occasion occasion) {
        return new Talent(
            ToolType.SupportSelf,
            Arrays.asList(
                new TalentConcept(
                    new TalentCondition(occasion) {
                        @Override
                        protected boolean getCondition(Conveyor data) {
                            return data.getUnit().getPos().equals(pos);
                        }
                    },
                    TalentEffect.Bonuses(bonuses)
                )
            )
        );
    }
    
    public static final Talent Bonus(List<Bonus> bonuses, Occasion occasion) {
        return new Talent(
            ToolType.SupportSelf,
            Arrays.asList(
                new TalentConcept(
                    TalentCondition.ALWAYS_TRIGGERS,
                    TalentEffect.Bonuses(bonuses)     
                )
            )
        );
    }
    
}
