/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle;

import com.atr.jme.font.TrueTypeFont;
import com.atr.jme.font.shape.TrueTypeNode;
import com.destroflyer.jme3.effekseer.renderer.EffekseerControl;
import com.google.gson.annotations.SerializedName;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.simsilica.lemur.ProgressBar;
import general.GeneralUtils;
import general.visual.VisualTransition;
import general.visual.VisualTransition.Progress;
import general.visual.RadialProgressBar;
import java.util.Arrays;
import java.util.HashMap;
import maps.layout.TangibleUnit;
import maps.layout.TangibleUnit.BattleRole;

/**
 *
 * @author night
 */
public class Combatant {
    public int BP = 1000;
    public BattleRole battle_role;
    
    ShownCombatant figure;
    
    private final AttackType attackType;
    
    private int level, maxhp, hp, tp, maxtp, strength, ether, agility, comprehension, dexterity, defense, resilience, mobility, physique, adrenaline; //raw base stats
    private int attackPower, accuracy, evasion, crit, critEvasion, attackSpeed;
    
    private int defaultDamage, extraDamage = 0;
    private int hpToSubtract = 0, tpToSubtract = 0;
    
    private final TangibleUnit tu;
    private final Node effects = new Node();
    
    private ProgressBar hpBar, tpBar;
    private EffekseerControl effectControl;
    private TrueTypeFont expFont;
    
    private VisualTransition levelUpTransition = null;
    private Material levelUpText;
    
    public boolean controlAdded = false;
    public TrueTypeNode expText;
    
    public enum AttackType {
        Weapon,
        Formula
    }
    
    private int[] fullBaseStats = new int[] {
            level, //level
            maxhp, //max hp
            strength, //str
            ether, //ether
            agility, //agi
            comprehension, //comp
            dexterity, //dex
            defense, //def
            resilience, //rsl
            mobility, //mobility
            physique, //physique
            adrenaline, //charisma
            hp, //current hp
            tp, //current tp
            maxtp //max tp
    };
    
    private int[] fullBattleStats = new int[] {
        attackPower,
        accuracy,
        evasion,
        crit,
        critEvasion,
        attackSpeed
    };
    
    public enum BaseStat {
        @SerializedName("Level") level(0, 'x'),
        @SerializedName("Max HP") maxHP(1, 'a'),
        @SerializedName("Strength") strength(2, 'b'),
        @SerializedName("Ether") ether(3, 'c'),
        @SerializedName("Agility") agility(4, 'd'),
        @SerializedName("Comprehension") comprehension(5, 'e'),
        @SerializedName("Dexterity") dexterity(6, 'f'),
        @SerializedName("Defense") defense(7, 'h'),
        @SerializedName("Resilience") resilience(8, 'i'),
        @SerializedName("Mobility") mobility(9, 'j'),
        @SerializedName("Physique") physique(10, 'k'),
        @SerializedName("Base Adrenaline") adrenaline(11, 'l'),
        currentHP(12, 'y'),
        currentTP(13, 'z'),
        @SerializedName("Max TP") maxTP(14, 'g');
        
        private final int value;
        private final char id;
        private static HashMap map = new HashMap<>();
        private BaseStat(int val, char identifier) {
            value = val;
            id = identifier;
        }
        
        static {
            for (BaseStat stat : BaseStat.values()) {
                map.put(stat.value, stat);
            }
        }

        public static BaseStat valueOf(int stat) {
            return (BaseStat) map.get(stat);
        }

        public int getValue() {
            return value;
        }
        
        public char getID() { return id; }
    }
    
    public enum BattleStat {
        @SerializedName("AttackPower") AttackPower(0),
        @SerializedName("Accuracy") Accuracy(1),
        @SerializedName("Evasion") Evasion(2),
        @SerializedName("Crit") Crit(3),
        @SerializedName("CritEvasion") CritEvasion(4),
        @SerializedName("AttackSpeed") AttackSpeed(5);
        
        private final int value;
        private static HashMap map = new HashMap<>();
        private BattleStat(int val) {
            value = val;
        }
        
        static {
            for (BattleStat stat : BattleStat.values()) {
                map.put(stat.value, stat);
            }
        }

        public static BattleStat valueOf(int stat) {
            return (BattleStat) map.get(stat);
        }

        public int getValue() {
            return value;
        }
    }
    
    public Combatant(Conveyer info, BattleRole role) {
        BP = 1000;
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
        
        level = tu.getLVL();
        maxhp = tu.getMaxHP();
        hp = tu.currentHP;
        tp = tu.currentTP;
        maxtp = tu.getMaxTP();
        strength = tu.getSTR();
        ether = tu.getETHER();
        agility = tu.getAGI();
        comprehension = tu.getCOMP();
        dexterity = tu.getDEX();
        defense = tu.getDEF();
        resilience = tu.getRSL();
        mobility = tu.getMOBILITY();
        physique = tu.getPHYSIQUE();
        adrenaline = tu.getADRENALINE();
        fullBaseStats = updateBaseStats();
        
        attackPower = tu.getATK();
        evasion = tu.getEvasion();
        crit = tu.getCrit();
        critEvasion = tu.getCritEvasion(); //change later
        attackSpeed = tu.getAS();
        accuracy = tu.getAccuracy();
        fullBattleStats = updateBattleStats();
    }
    
    private int[] updateBaseStats() {
        return new int[] {
            level, //level
            maxhp, //max hp
            strength, //str
            ether, //ether
            agility, //agi
            comprehension, //comp
            dexterity, //dex
            defense, //def
            resilience, //rsl
            mobility, //mobility
            physique, //physique
            adrenaline, //charisma
            hp, //current hp
            tp, //current tp
            maxtp //max tp
        };
    }
    
    private int[] updateBattleStats() {
        return new int[] {
            attackPower,
            accuracy,
            evasion,
            crit,
            critEvasion,
            attackSpeed
        };
    }
    
    public TangibleUnit getUnit() { return tu; }
    
    public AttackType getAttackType() { return attackType; } 

    public int getBaseStat(BaseStat stat) {
        return fullBaseStats[stat.getValue()];
    }
    
    public int getBattleStat(BattleStat stat) {
        return fullBattleStats[stat.getValue()];
    }
    
    public int getDefaultDamage() { return defaultDamage; } //full damage including extra damage
    public int getExtraDamage() { return extraDamage; }
    
    public int getHPToSubtract() { return hpToSubtract; }
    public int getTPToSubtract() { return tpToSubtract; }
    
    public ProgressBar getHPbar() { return hpBar; }
    public ProgressBar getTPbar() { return tpBar; }
    
    public EffekseerControl getEffectControl() { return effectControl; }
    public Node getEffectsNode() { return effects; }
    
    public TrueTypeFont getExpFont() { return expFont; }
    
    public Material getLevelUpText() {
        return levelUpText;
    }
    
    public void appendToBaseStat(BaseStat stat, int value) {
        fullBaseStats[stat.getValue()] += value;
    }
    
    public void appendToBattleStat(BattleStat stat, int value) {
        fullBattleStats[stat.getValue()] += value;
    }
    
    public void setDefaultDamage(int dmg) { defaultDamage = dmg; }
    public void setExtraDamage(int extra) { extraDamage = extra; }
    
    public void setHPtoSubtract(int amt) { hpToSubtract = amt; } 
    public void setTPtoSubtract(int amt) { tpToSubtract = amt; }
    
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
    
    public void applyAllStatsToUnit() {
        tu.setStat(BaseStat.level, fullBaseStats[0]);
        /*tu.setStats(statName.maxHP, fullBaseStats[1]);
        tu.setStats(statName.strength, fullBaseStats[2]);
        tu.setStats(statName.ether, fullBaseStats[3]);
        tu.setStats(statName.agility, fullBaseStats[4]);
        tu.setStats(statName.comprehension, fullBaseStats[5]);
        tu.setStats(statName.dexterity, fullBaseStats[6]);
        tu.setStats(statName.defense, fullBaseStats[7]);
        tu.setStats(statName.resilience, fullBaseStats[8]);
        tu.setStats(statName.mobility, fullBaseStats[9]);
        tu.setStats(statName.physique, fullBaseStats[10]);*/
        tu.setStat(BaseStat.adrenaline, fullBaseStats[11]); //adrenaline
        
        tu.currentHP = fullBaseStats[12];
        tu.currentTP = fullBaseStats[13];
    }

    public void initializeExpCircle(Node actualGuiNode) {
        if (tu.currentHP > 0) {
            figure.expbar = new RadialProgressBar(52.5f, 70.75f, tu.unitStatus.getAssociatedColor(), 2);
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
        } else if (tu.currentHP > 0) {
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
        levelUpTransition = new VisualTransition(lvlupgeom, Arrays.asList(VisualTransition.ZoomIn().setLength(0.1f)));
        levelUpTransition.beginTransitions();
    }
    
    public boolean attemptLevelUpTransition() {
        if (levelUpTransition != null) {
            levelUpTransition.updateTransitions();
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
            if (figure.effIndex == tu.getEquippedFormula().getInfo().getImpactFrame()) {
                figure.impactStatus = Battle.ImpactType.All;
            } else if (figure.effIndex == tu.getEquippedFormula().getInfo().getFrames()) {
                figure.index++; //take it off freeze
                figure.amassingTPF = 0;
                figure.allowEffectUpdate = false;
                figure.effIndex = 0;
                effects.removeControl(effectControl);
                controlAdded = false;
                effectControl = tu.getEquippedFormula().resetControl();
            }
            figure.effIndex++;
        }
    }
    
    void updateBars(float tpf) {
        updateHP();
        updateTP();
    }
    
    private void updateHP() {
        if (hpToSubtract != 0) {
            hpBar.setProgressPercent(((double)fullBaseStats[12]) / fullBaseStats[1]); //currentHP / maxHP
            hpBar.setMessage("HP: " + fullBaseStats[12] + "/" + fullBaseStats[1]);
            
            int modifier;
            if (hpToSubtract > 0) {
                modifier = 1;
            } else {
                modifier = -1;
            }
            
            fullBaseStats[12] -= modifier;
            hpToSubtract -= modifier;
        }
    }
    
    private void updateTP() {
        if (tpToSubtract != 0) {
            tpBar.setProgressPercent(((double)fullBaseStats[13]) / fullBaseStats[14]); //currentTP / maxTP
            tpBar.setMessage("TP: " + fullBaseStats[13] + "/" + fullBaseStats[14]);
            
            int modifier;
            if (tpToSubtract > 0) {
                modifier = 1;
            } else {
                modifier = -1;
            }
            
            fullBaseStats[13] -= modifier;
            tpToSubtract -= modifier;
        }
    }
    
    
}