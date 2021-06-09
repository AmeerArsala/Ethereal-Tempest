/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle;

import battle.Battle.ImpactType;
import etherealtempest.info.Conveyer;
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
import fundamental.tool.DamageTool;
import general.visual.VisualTransition;
import general.visual.VisualTransition.Progress;
import general.visual.RadialProgressBar;
import java.util.Arrays;
import java.util.HashMap;
import maps.layout.occupant.TangibleUnit;

/**
 *
 * @author night
 */
public class Combatant {
    public int BP = 1000;
    public BattleRole battle_role;
    
    ShownCombatant figure;
    
    private final AttackType attackType;
    
    private final HashMap<BaseStat, Integer> combatBaseStats = new HashMap<>(); //raw base stats
    private final HashMap<BattleStat, Integer> combatBattleStats = new HashMap<>();
    
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
    
    public enum BattleRole {
        Initiator,
        Receiver;
        
        private BattleRole opponent;
        
        static {
            Initiator.setOpponent(Receiver);
            Receiver.setOpponent(Initiator);
        }
        
        public void setOpponent(BattleRole br) { opponent = br; }
        public BattleRole getOpponent() { return opponent; }
    }
    
    public enum BaseStat {
        @SerializedName("Level") level(0, 'x', "Level"),
        @SerializedName("Max HP") maxHP(1, 'a', "Max HP"),
        @SerializedName("Strength") strength(2, 'b', "STR"),
        @SerializedName("Ether") ether(3, 'c', "ETHER"),
        @SerializedName("Agility") agility(4, 'd', "AGI"),
        @SerializedName("Comprehension") comprehension(5, 'e', "COMP"),
        @SerializedName("Dexterity") dexterity(6, 'f', "DEX"),
        @SerializedName("Defense") defense(7, 'h', "DEF"),
        @SerializedName("Resilience") resilience(8, 'i', "RSL"),
        @SerializedName("Mobility") mobility(9, 'j', "MOBILITY"),
        @SerializedName("Physique") physique(10, 'k', "PHYSIQUE"),
        @SerializedName("Base Adrenaline") adrenaline(11, 'l', "INIT. ADR"),
        currentHP(12, 'y', "Current HP"),
        currentTP(13, 'z', "Current TP"),
        @SerializedName("Max TP") maxTP(14, 'g', "Max TP");
        
        private final int value;
        private final char id;
        private final String name;
        
        private static HashMap map = new HashMap<>();
        private BaseStat(int val, char identifier, String sname) {
            value = val;
            id = identifier;
            name = sname;
        }

        public int getValue() {
            return value;
        }
        
        public String getName() {
            return name;
        }
        
        public char getID() { return id; }
    }
    
    public enum BattleStat {
        @SerializedName("AttackPower") AttackPower(0, "ATK PWR"),
        @SerializedName("Accuracy") Accuracy(1, "ACC"),
        @SerializedName("Evasion") Evasion(2, "EVA"),
        @SerializedName("Crit") Crit(3, "CRIT"),
        @SerializedName("CritEvasion") CritEvasion(4, "CRIT EVA"),
        @SerializedName("AttackSpeed") AttackSpeed(5, "SPD");
        
        private final int value;
        private final String name;
        private BattleStat(int val, String sname) {
            value = val;
            name = sname;
        }
        
        public String getName() {
            return name;
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
        
        initializeCombatant();
    }
    
    public Combatant(TangibleUnit unit, BattleRole role) {
        BP = 1000;
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
        combatBaseStats.put(BaseStat.level, tu.getLVL());
        combatBaseStats.put(BaseStat.maxHP, tu.getMaxHP());
        combatBaseStats.put(BaseStat.currentHP, tu.getStat(BaseStat.currentHP));
        combatBaseStats.put(BaseStat.currentTP, tu.getStat(BaseStat.currentTP));
        combatBaseStats.put(BaseStat.maxTP, tu.getMaxTP());
        combatBaseStats.put(BaseStat.strength, tu.getSTR());
        combatBaseStats.put(BaseStat.ether, tu.getETHER());
        combatBaseStats.put(BaseStat.agility, tu.getAGI());
        combatBaseStats.put(BaseStat.comprehension, tu.getCOMP());
        combatBaseStats.put(BaseStat.dexterity, tu.getDEX());
        combatBaseStats.put(BaseStat.defense, tu.getDEF());
        combatBaseStats.put(BaseStat.resilience, tu.getRSL());
        
        combatBaseStats.put(BaseStat.mobility, tu.getMobility());
        combatBaseStats.put(BaseStat.physique, tu.getPHYSIQUE());
        
        combatBaseStats.put(BaseStat.adrenaline, tu.getADRENALINE());
        
        combatBattleStats.put(BattleStat.AttackPower, tu.getATK());
        combatBattleStats.put(BattleStat.Evasion, tu.getEvasion());
        combatBattleStats.put(BattleStat.Crit, tu.getCrit());
        combatBattleStats.put(BattleStat.CritEvasion, tu.getCritEvasion());
        combatBattleStats.put(BattleStat.AttackSpeed, tu.getAS());
        combatBattleStats.put(BattleStat.Accuracy, tu.getAccuracy());
    }
    
    public void prebattleInitialization() {
        int extradmg = ((DamageTool)tu.getEquippedTool()).extraDamage;
        if (tu.getToUseSkill() != null) {
            tu.getToUseSkill().getEffect().applyEffectsOnCombat(this);
            extradmg += tu.getToUseSkill().getEffect().extraDamage();
        }
        
        setExtraDamage(extradmg);
    }
    
    public TangibleUnit getUnit() { return tu; }
    
    public AttackType getAttackType() { return attackType; } 

    public int getBaseStat(BaseStat stat) {
        return combatBaseStats.get(stat);
    }
    
    public int getBattleStat(BattleStat stat) {
        return combatBattleStats.get(stat);
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
        combatBaseStats.replace(stat, combatBaseStats.get(stat) + value);
    }
    
    public void appendToBattleStat(BattleStat stat, int value) {
        combatBattleStats.replace(stat, combatBattleStats.get(stat) + value);
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
        tu.setStat(BaseStat.level, combatBaseStats.get(BaseStat.level));
        tu.setStat(BaseStat.adrenaline, combatBaseStats.get(BaseStat.adrenaline));
        
        tu.setStat(BaseStat.currentHP, combatBaseStats.get(BaseStat.currentHP));
        tu.setStat(BaseStat.currentTP, combatBaseStats.get(BaseStat.currentTP));
    }

    public void initializeExpCircle(Node actualGuiNode) {
        if (tu.getStat(BaseStat.currentHP) > 0) {
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
        levelUpTransition = new VisualTransition(lvlupgeom, Arrays.asList(VisualTransition.ZoomIn().setLength(0.1f)));
        levelUpTransition.beginTransitions();
    }
    
    public boolean attemptLevelUpTransition(float tpf) {
        if (levelUpTransition != null) {
            levelUpTransition.updateTransitions(tpf);
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
                figure.impactStatus = ImpactType.All;
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
    
    void EtherInitialize() {
        setHPtoSubtract(tu.getEquippedFormula().getHPUsage());
        setTPtoSubtract(tu.getEquippedFormula().getTPUsage());
   
        setEffectControl(tu.getEquippedFormula().getControl());
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
    
    @Override
    public String toString() {
        return 
                  tu + "\n"
                + "Max HP: " + combatBaseStats.get(BaseStat.maxHP) + "\n"
                + "Max TP: " + combatBaseStats.get(BaseStat.maxTP) + "\n"
                + "Current HP: " + combatBaseStats.get(BaseStat.currentTP) + "\n"
                + "Current TP: " + combatBaseStats.get(BaseStat.currentTP) + "\n"
                + "STR: " + combatBaseStats.get(BaseStat.strength) + "\n"
                + "ETHER: " + combatBaseStats.get(BaseStat.ether) + "\n"
                + "AGI: " + combatBaseStats.get(BaseStat.agility) + "\n"
                + "COMP: " + combatBaseStats.get(BaseStat.comprehension) + "\n"
                + "DEX: " + combatBaseStats.get(BaseStat.dexterity) + "\n"
                + "DEF: " + combatBaseStats.get(BaseStat.defense) + "\n"
                + "RSL: " + combatBaseStats.get(BaseStat.resilience) + "\n"
                + "MOBILITY: " + combatBaseStats.get(BaseStat.mobility) + "\n"
                + "PHYSIQUE: " + combatBaseStats.get(BaseStat.physique) + "\n"
                + "Initial Adrenaline: " + combatBaseStats.get(BaseStat.adrenaline) + "\n"
                + "ATK PWR: " + combatBattleStats.get(BattleStat.AttackPower) + "\n"
                + "ACC: " + combatBattleStats.get(BattleStat.Accuracy) + "\n"
                + "EVA: " + combatBattleStats.get(BattleStat.Evasion) + "\n"
                + "CRIT: " + combatBattleStats.get(BattleStat.Crit) + "\n"
                + "CRIT EVA: " + combatBattleStats.get(BattleStat.CritEvasion) + "\n"
                + "AS: " + combatBattleStats.get(BattleStat.AttackSpeed) + "\n";
                
    }
    
}