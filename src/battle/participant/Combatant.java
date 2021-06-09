/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.participant;

import com.jme3.math.FastMath;
import etherealtempest.info.Conveyor;
import fundamental.skill.Skill;
import fundamental.stats.BaseStat;
import fundamental.stats.BattleStat;
import fundamental.stats.Toll.Exchange;
import general.math.FloatPair;
import java.util.HashMap;
import maps.layout.occupant.character.TangibleUnit;

/**
 *
 * @author night
 */
public class Combatant {
    public BattleRole battle_role;
    
    private final TangibleUnit tu;
    
    private final AttackType attackType;
    
    private final HashMap<BaseStat, Integer> combatBaseStats = new HashMap<>(); //raw base stats
    private final HashMap<BattleStat, Integer> combatBattleStats = new HashMap<>();
    
    private int defaultDamage;
    
    public int damageDone = 0, damageTaken = 0, tpLost = 0, expGained = 0, hitsDodged = 0, numOfCrits = 0;
    public float durabilityUsed = 0.0f;
    
    private boolean isUsingSkill;
    private Runnable applySkillToll = () -> {}; //also checks and applies any HP, TP, or Durability subtraction costs
    
    /*private final Node effects = new Node();
    
    private ProgressBar hpBar, tpBar;
    private EffekseerControl effectControl;
    private TrueTypeFont expFont;
    
    private VisualTransition levelUpTransition = null;
    private Material levelUpText;
    
    public boolean controlAdded = false;
    public TrueTypeNode expText;*/
    
    public enum AttackType {
        Weapon,
        Formula
    }
    
    public Combatant(Conveyor info, BattleRole role) {
        battle_role = role;
        
        if (role == BattleRole.Initiator) {
            tu = info.getUnit();
        } else {
            tu = info.getEnemyUnit();
        }
        
        if (tu.getEquippedFormula() != null) {
            attackType = AttackType.Formula;
        } else {
            attackType = AttackType.Weapon;
        }
        
        initializeCombatant();
    }
    
    public Combatant(TangibleUnit unit, BattleRole role) {
        battle_role = role;
        tu = unit;
        
        if (tu.getEquippedFormula() != null) {
            attackType = AttackType.Formula;
        } else {
            attackType = AttackType.Weapon;
        }
        
        initializeCombatant();
    }
    
    private void initializeCombatant() {
        combatBaseStats.put(BaseStat.Level, tu.getLVL());
        combatBaseStats.put(BaseStat.MaxHP, tu.getMaxHP());
        combatBaseStats.put(BaseStat.CurrentHP, tu.getBaseStat(BaseStat.CurrentHP));
        combatBaseStats.put(BaseStat.CurrentTP, tu.getBaseStat(BaseStat.CurrentTP));
        combatBaseStats.put(BaseStat.MaxTP, tu.getMaxTP());
        combatBaseStats.put(BaseStat.Strength, tu.getSTR());
        combatBaseStats.put(BaseStat.Ether, tu.getETHER());
        combatBaseStats.put(BaseStat.Agility, tu.getAGI());
        combatBaseStats.put(BaseStat.Comprehension, tu.getCOMP());
        combatBaseStats.put(BaseStat.Dexterity, tu.getDEX());
        combatBaseStats.put(BaseStat.Defense, tu.getDEF());
        combatBaseStats.put(BaseStat.Resilience, tu.getRSL());
        
        combatBaseStats.put(BaseStat.Mobility, tu.getMOBILITY());
        combatBaseStats.put(BaseStat.Physique, tu.getPHYSIQUE());
        
        combatBaseStats.put(BaseStat.Adrenaline, tu.getADRENALINE());
        
        combatBattleStats.put(BattleStat.AttackPower, tu.getATK());
        combatBattleStats.put(BattleStat.Evasion, tu.getEvasion());
        combatBattleStats.put(BattleStat.Crit, tu.getCrit());
        combatBattleStats.put(BattleStat.CritEvasion, tu.getCritEvasion());
        combatBattleStats.put(BattleStat.AttackSpeed, tu.getAS());
        combatBattleStats.put(BattleStat.Accuracy, tu.getAccuracy());
        
        applySkillBuffsIfAny();
    }
    
    private void applySkillBuffsIfAny() {
        Skill skillBeingUsed = tu.getToUseSkill();
        if (skillBeingUsed != null) {
            skillBeingUsed.getEffect().applyBuffsOnCombat(this);
            
            isUsingSkill = true;
        } else {
            isUsingSkill = false;
        }
    }
    
    public void applySkillTollIfAny() {
        if (isUsingSkill) {
            Skill skillInUse = tu.getToUseSkill();
            Exchange costType = skillInUse.getToll().getType();
            if (costType != Exchange.Durability) { // costType == Exchange.HP || costType == Exchange.TP
                appendToBaseStat(costType.getCorrelatingStat(), -skillInUse.getToll().getValue());
            } else {
                //if this skill is being used with a cost of durability, this assumes that the equipped tool is a weapon
                tu.getEquippedWPN().addCurrentDurability(-skillInUse.getToll().getValue());
            }
        }
    }
    
    public TangibleUnit getUnit() { return tu; }
    
    public AttackType getAttackType() { return attackType; } 
    public boolean isUsingSkill() { return isUsingSkill; }

    public int getBaseStat(BaseStat stat) {
        return combatBaseStats.get(stat);
    }
    
    public int getBattleStat(BattleStat stat) {
        return combatBattleStats.get(stat);
    }
    
    public float getCurrentToMaxHPRatio() {
        return ((float)combatBaseStats.get(BaseStat.CurrentHP)) / combatBaseStats.get(BaseStat.MaxHP);
    }
    
    public float getCurrentToMaxTPRatio() {
        return ((float)combatBaseStats.get(BaseStat.CurrentTP)) / combatBaseStats.get(BaseStat.MaxTP);
    }
    
    public int getDefaultDamage() { return defaultDamage; } //full damage on hit including extra damage
    
    public void appendToBaseStat(BaseStat stat, int value) {
        combatBaseStats.replace(stat, combatBaseStats.get(stat) + value);
    }
    
    public void appendToBattleStat(BattleStat stat, int value) {
        combatBattleStats.replace(stat, combatBattleStats.get(stat) + value);
    }
    
    public void setDefaultDamage(int dmg) { 
        defaultDamage = dmg; 
    }
    
    public void applyAllStatsToUnit() {
        tu.setRawStat(BaseStat.Level, combatBaseStats.get(BaseStat.Level));
        
        tu.setRawStat(BaseStat.CurrentHP, combatBaseStats.get(BaseStat.CurrentHP));
        tu.setRawStat(BaseStat.CurrentTP, combatBaseStats.get(BaseStat.CurrentTP));
        
        tu.getUnitInfo().addTotalExpGained(expGained);
        tu.getUnitInfo().addTotalDamageDone(damageDone);
        tu.getUnitInfo().addTotalDamageTaken(damageTaken);
        tu.getUnitInfo().addTotalDurabilityUsed(durabilityUsed);
        tu.getUnitInfo().addTotalTPlost(tpLost);
        tu.getUnitInfo().addTotalHitsDodged(hitsDodged);
        tu.getUnitInfo().addTotalCriticals(numOfCrits);
    }
    
    public float secondsToDrain(BaseStat statToDrain, BaseStat defensiveStat, int dmg, boolean isCrit) {
        FloatPair domain = new FloatPair(0.2f, 1.25f);
        
        float multiplier = 0.5f; //base is 0.5f
        float defense = combatBaseStats.get(defensiveStat);
        float attack = dmg + defense;
        
        if (isCrit) {
            multiplier /= 2f;
        }
        
        float seconds = multiplier * (combatBaseStats.get(statToDrain) / dmg) * (defense / attack);
        
        return domain.bound(seconds);
    }
    
    public void calculateEXP(Combatant opponent) {
        int levelDifference = tu.getLVL() - opponent.tu.getLVL();
        int baseDamageValue = 10; // base damage exp
        int baseKillValue = 40;   // base kill exp (for killing a foe)
        int bountyCoefficient = tu.getParams().isBoss ? 2 : 1;
        FloatPair expDomain = new FloatPair(1, 199 - tu.currentEXP); // no double level ups
        
        float overlevelPreventionCoefficient = 1f;
        if (levelDifference > 0) {
            overlevelPreventionCoefficient = FastMath.pow(levelDifference, -1);
        }
        
        if (opponent.getBaseStat(BaseStat.CurrentHP) <= 0) { //opponent is dead
            float underdogKillCoefficient; //other units can easily catch up in levels if they kill an enemy, but it is extremely hard to overlevel
            if (levelDifference <= 0) { //opponent is a higher level
                underdogKillCoefficient = 1 + (levelDifference * -1);
            } else { // > 0
                underdogKillCoefficient = overlevelPreventionCoefficient;
            }
            
            expGained = (int)expDomain.bound(baseKillValue * bountyCoefficient * underdogKillCoefficient);
        } else {
            expGained = (int)expDomain.bound((damageDone > 0 ? baseDamageValue : 1) * bountyCoefficient * overlevelPreventionCoefficient);
        }
    }
    
    /**
     *
     * @return the new current exp value
     */
    public int addExpGained() {
        tu.currentEXP += expGained;
        return tu.currentEXP;
    }
    
    /**
     *
     * @return whether the unit is eligible to level up or not
     */
    public boolean subtractEXPifLevelUp() {
        if (tu.currentEXP < 100) {
            return false;
        }
        
        tu.currentEXP -= 100;
        return true;
    }
    
    @Override
    public String toString() {
        return 
                  tu + "\n"
                + "Max HP: " + combatBaseStats.get(BaseStat.MaxHP) + "\n"
                + "Max TP: " + combatBaseStats.get(BaseStat.MaxTP) + "\n"
                + "Current HP: " + combatBaseStats.get(BaseStat.CurrentTP) + "\n"
                + "Current TP: " + combatBaseStats.get(BaseStat.CurrentTP) + "\n"
                + "STR: " + combatBaseStats.get(BaseStat.Strength) + "\n"
                + "ETHER: " + combatBaseStats.get(BaseStat.Ether) + "\n"
                + "AGI: " + combatBaseStats.get(BaseStat.Agility) + "\n"
                + "COMP: " + combatBaseStats.get(BaseStat.Comprehension) + "\n"
                + "DEX: " + combatBaseStats.get(BaseStat.Dexterity) + "\n"
                + "DEF: " + combatBaseStats.get(BaseStat.Defense) + "\n"
                + "RSL: " + combatBaseStats.get(BaseStat.Resilience) + "\n"
                + "MOBILITY: " + combatBaseStats.get(BaseStat.Mobility) + "\n"
                + "PHYSIQUE: " + combatBaseStats.get(BaseStat.Physique) + "\n"
                + "Initial Adrenaline: " + combatBaseStats.get(BaseStat.Adrenaline) + "\n"
                + "ATK PWR: " + combatBattleStats.get(BattleStat.AttackPower) + "\n"
                + "ACC: " + combatBattleStats.get(BattleStat.Accuracy) + "\n"
                + "EVA: " + combatBattleStats.get(BattleStat.Evasion) + "\n"
                + "CRIT: " + combatBattleStats.get(BattleStat.Crit) + "\n"
                + "CRIT EVA: " + combatBattleStats.get(BattleStat.CritEvasion) + "\n"
                + "AS: " + combatBattleStats.get(BattleStat.AttackSpeed) + "\n";
    }
    
    /*
    public ProgressBar getHPbar() { return hpBar; }
    public ProgressBar getTPbar() { return tpBar; }
    
    public EffekseerControl getEffectControl() { return effectControl; }
    public Node getEffectsNode() { return effects; }
    
    public TrueTypeFont getExpFont() { return expFont; }
    
    public Material getLevelUpText() {
        return levelUpText;
    }
    
    public void setHPbar(ProgressBar hpb) {
        hpBar = hpb;
    }
    
    public void setTPbar(ProgressBar tpb) {
        tpBar = tpb; 
    }
    
    public void setEffectControl(EffekseerControl efc) {
        effectControl = efc; 
    }
    
    public void setExpFont(TrueTypeFont ttf) {
        expFont = ttf;
    }
    
    public void setLevelUpText(Material txt) {
        levelUpText = txt;
    }

    public void initializeExpCircle(Node actualGuiNode) {
        if (tu.getStat(BaseStat.currentHP) > 0) {
            figure.expbar = new RadialProgressBar(52.5f, 70.75f, tu.getAllegiance().getAssociatedColor(), 2);
            figure.expbar.move(300, 560, 0);

            expText = expFont.getText("  EXP\n " + tu.currentEXP + "/100", 3, ColorRGBA.White);
            expText.move(-45, 45, 0);
            expText.scale(0.7f);
            figure.expbar.getChildrenNode().attachChild(expText);
        
            actualGuiNode.attachChild(figure.expbar);
        }
    }
    
    public void gainExp() {
        figure.expbar.setCirclePercent(tu.currentEXP / 100f);
                        
        String extraSpace = "   ";
        float disX = 0;
        if (tu.currentEXP >= 100) {
            extraSpace = "     ";
            disX = -12.5f;
        }
                        
        figure.expbar.getChildrenNode().detachChild(expText);
        expText = expFont.getText(extraSpace + "EXP\n " + tu.currentEXP + "/100", 3, ColorRGBA.White);
        expText.move(-45 + disX, 45, 0);
        expText.scale(0.7f);
        figure.expbar.getChildrenNode().attachChild(expText);
                        
        //TODO: PLAY A SOUND
                        
        tu.currentEXP++;
        figure.expGained--;
    }
    
    public void attemptInitializeLevelUpVisual(Node actualGuiNode) {
        if (tu.currentEXP >= 100) {
            initializeLevelUpVisual();
        } else if (tu.getStat(BaseStat.currentHP) > 0) {
            actualGuiNode.detachChild(figure.expbar);
        }
    }
    
    private void initializeLevelUpVisual() {
        figure.expbar.getChildrenNode().detachAllChildren();
        figure.expbar.getChildrenNode().move(-10, 50, 0);
        Quad lvlupquad = new Quad(100.5f, 25.5f);
        Geometry lvlupgeom = new Geometry("lvlupQuad", lvlupquad);
        lvlupgeom.setMaterial(levelUpText.clone());
        figure.expbar.getChildrenNode().attachChild(lvlupgeom);
        levelUpTransition = new VisualTransition(lvlupgeom, Animation.ZoomIn().setLength(0.1f));
        levelUpTransition.beginTransitions();
    }
    
    public boolean attemptLevelUpTransition(float tpf) {
        if (levelUpTransition != null) {
            levelUpTransition.update(tpf);
            return levelUpTransition.getTransitionProgress() == Progress.Finished;
        }
        
        return true;
    }
    
    private boolean leveledUp = false;
    
    public boolean attemptLevelUp(float tpf) {
        boolean done = true;
        
        if (tu.currentEXP >= 100) {
            leveledUp = true;
            tu.currentEXP -= 100;
        }
        
        if (leveledUp) {
            done = figure.updateArrows(tpf);
        }
        
        return done;
    }
    
    void updateEffects(float tpf) {
        if (tu.getEquippedFormula() != null && figure.allowEffectUpdate) {
            if (!controlAdded) {
                effects.addControl(effectControl);
                controlAdded = true;
            }
            effectControl.update(tpf);
            if (figure.effIndex == tu.getEquippedFormula().getVisuals().getAnimationInfo().getImpactFrame()) {
                figure.impactStatus = ImpactType.All;
            } else if (figure.effIndex == tu.getEquippedFormula().getVisuals().getAnimationInfo().getFrames()) {
                figure.index++; //take it off freeze
                figure.amassingTPF = 0;
                figure.allowEffectUpdate = false;
                figure.effIndex = 0;
                effects.removeControl(effectControl);
                controlAdded = false;
                effectControl = tu.getEquippedFormula().getVisuals().resetControl();
            }
            figure.effIndex++;
        }
    }
    
    void updateBars(float tpf) {
        updateHP();
        updateTP();
    }
    
    void EtherInitialize() {
        setHPtoSubtract(tu.getEquippedFormula().getHPUsage());
        setTPtoSubtract(tu.getEquippedFormula().getTPUsage());
   
        setEffectControl(tu.getEquippedFormula().getVisuals().getControl());
    }
    
    private void updateHP() {
        if (hpToSubtract != 0) {
            hpBar.setProgressPercent(((double)combatBaseStats.get(BaseStat.currentHP)) / combatBaseStats.get(BaseStat.maxHP)); //currentHP / maxHP
            hpBar.setMessage("HP: " + combatBaseStats.get(BaseStat.currentHP) + "/" + combatBaseStats.get(BaseStat.maxHP));
            
            int modifier;
            if (hpToSubtract > 0) {
                modifier = 1;
            } else {
                modifier = -1;
            }
            
            combatBaseStats.replace(BaseStat.currentHP, combatBaseStats.get(BaseStat.currentHP) - modifier);
            hpToSubtract -= modifier;
        }
    }
    
    private void updateTP() {
        if (tpToSubtract != 0) {
            tpBar.setProgressPercent(((double)combatBaseStats.get(BaseStat.currentTP)) / combatBaseStats.get(BaseStat.maxTP)); //currentTP / maxTP
            tpBar.setMessage("TP: " + combatBaseStats.get(BaseStat.currentTP) + "/" + combatBaseStats.get(BaseStat.maxTP));
            
            int modifier;
            if (tpToSubtract > 0) {
                modifier = 1;
            } else {
                modifier = -1;
            }
            
            combatBaseStats.replace(BaseStat.currentTP, combatBaseStats.get(BaseStat.currentTP) - modifier);
            tpToSubtract -= modifier;
        }
    }
    */
}