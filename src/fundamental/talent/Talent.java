/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.talent;

import battle.Combatant;
import battle.Combatant.BaseStat;
import battle.Combatant.BattleRole;
import etherealtempest.info.Conveyer;
import fundamental.Associated;
import fundamental.skill.Skill;
import fundamental.stats.Bonus;
import fundamental.stats.Bonus.BonusType;
import fundamental.stats.Toll.Exchange;
import fundamental.talent.TalentCondition.Occasion;
import fundamental.tool.Tool;
import fundamental.tool.Tool.ToolType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import maps.layout.occupant.TangibleUnit;

/**
 *
 * @author night
 */
public class Talent extends Associated {
    private String loredesc = "";
    private String imagePath = "";
    private ToolType type;
    
    private List<TalentConcept> body = new ArrayList<>();
    
    private boolean actualTalent = true;
    
    public Talent(String talentname, ToolType t_type, String lore, String imgPath, List<TalentConcept> tc) {
        super(talentname, talentname + " \n " + lore + "\n \nEffects: \n" + generateDescription(tc));
        type = t_type;
        loredesc = lore;
        imagePath = "Interface/GUI/talent_icons/" + imgPath;
        body = tc;
    }
    
    protected Talent(String talentname, ToolType t_type, String lore, String description, String imgPath) {
        super(talentname, description);
        type = t_type;
        loredesc = lore;
        imagePath = "Interface/GUI/talent_icons/" + imgPath;
    }
    
    public Talent(boolean ex) {
        super(ex);
    }
    
    public static final String generateDescription(List<TalentConcept> tc) {
        String desc = "";
        
        for (TalentConcept concept : tc) {
            desc += concept.toString() + "\n";
        }
        
        return ("" + desc.charAt(0)).toUpperCase() + desc.substring(1);
    }
    
    public ToolType getToolType() { return type; }
 
    public String getLoreDescription() { return loredesc; }
    public String getIconPath() { return imagePath; }
    
    public List<TalentConcept> getFullBody() { return body; }
    
    public boolean getIsActualTalent() { return actualTalent; }
    
    void setActualTalent(boolean is) { actualTalent = is; }
    
    @Override
    public String toString() { return name; }
    
    /*
     * ToolType.SupportSelf is when it supports yourself
     * ToolType.SupportAlly is when it supports an ally
     * Both of the above are calculated when engaging in healing
     * ToolType.Attack is when a Unit attacks another; the skills, talents, and abilities under this category will be used in attack sequences
     */
    
    public static final Talent Opportunist() { //basically vantage
        return new Talent(
            "Opportunist", 
            ToolType.Attack, 
            "No matter the odds, there is always an opening.", 
            "opportunist.png", 
            Arrays.asList(
                new TalentConcept(
                    new TalentCondition("If user's HP <= 50% and can counterattack against foe, ", Occasion.BeforeCombat) {
                        @Override
                        protected boolean getCondition(Conveyer data) {
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
                    new TalentEffect("unit always attacks first") {
                        @Override
                        public void enactEffect(Conveyer info) {
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
                        50, //%
                        BaseStat.ether, //stat
                        Exchange.HP, //penalty type
                        3 //range
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
                    new TalentCondition("If user's HP >= 50%, ", Occasion.StartOfTurn) {
                        @Override
                        protected boolean getCondition(Conveyer data) {
                            return data.getUnit().getCurrentToMaxHPratio() >= 0.5f;
                        }
                    },
                    new TalentEffect("increases a random stat (excluding HP, TP, and Adrenaline) by 4 points for an entire turn") {
                        @Override
                        protected List<Bonus> userBuffs(Conveyer data) { //buffs to self
                            List<Bonus> bonuses = new ArrayList<>();
                            int length = BaseStat.values().length;
                            
                            BaseStat stat = BaseStat.values()[(int)(length * Math.random())];
                            while (stat == BaseStat.maxHP || stat == BaseStat.currentHP || stat == BaseStat.maxTP || stat == BaseStat.currentTP || stat == BaseStat.level || stat == BaseStat.adrenaline) { //reroll if it is any of these stats
                                stat = BaseStat.values()[(int)(length * Math.random())];
                            }
                            
                            bonuses.add(new Bonus(4, BonusType.FullTurn, stat));
                            return bonuses;
                        }
                    }
                )
            )
        );
    }
    
    public static final Talent TileBonus(int x, int y, int l, List<Bonus> bonuses, Occasion occasion) {
        Talent RTB = new Talent(
            "", ToolType.SupportSelf, "", "",
            Arrays.asList(
                new TalentConcept(
                    new TalentCondition("", occasion) {
                        @Override
                        protected boolean getCondition(Conveyer data) {
                            TangibleUnit unit = data.getUnit();
                            return unit.getPosX() == x && unit.getPosY() == y && unit.getElevation() == l;
                        }
                    },
                    new TalentEffect("") {
                        @Override
                        public List<Bonus> userBuffs(Conveyer data) {
                            return bonuses;
                        }
                        
                        @Override
                        public void enactEffect(Conveyer info) {}
                    }     
                )
            )
        );
        
        RTB.setActualTalent(false);
        
        return RTB;
    }
    
    public static final Talent Bonus(List<Bonus> bonuses, Occasion occasion) {
        return new Talent(
            "", ToolType.SupportSelf, "", "",
            Arrays.asList(
                new TalentConcept(
                    TalentCondition.ALWAYS_TRIGGERS,
                    new TalentEffect("") {
                        @Override
                        public List<Bonus> userBuffs(Conveyer data) {
                            return bonuses;
                        }
                    }     
                )
            )
        );
    }
    
}
