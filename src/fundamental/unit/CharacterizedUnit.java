/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.unit;

import com.google.gson.annotations.Expose;
import com.jme3.math.FastMath;
import com.jme3.texture.Texture;
import fundamental.stats.BaseStat;
import java.util.Arrays;

/**
 *
 * @author night
 */
public class CharacterizedUnit extends Unit {
    private final Info info;
    private final StatisticsModifier unitStatisticsModifier; 
    
    //copies fields
    public CharacterizedUnit(Unit X, Info unitInfo) {
        super(X.getName(), X.getJobClass(), X.getRawBaseStats(), X.getGrowthRates(), X.getInventory(), X.getFormulaManager(), X.getTalentManager(), X.getSkillManager(), X.getAbilityManager(), X.getFormationManager());
        info = unitInfo;
        
        unitStatisticsModifier = new StatisticsModifier() {
            @Override
            public void incrementWins() {
                ++info.wins;
            }
        
            @Override
            public void incrementLosses() {
                ++info.losses;
                growthRates.addToAllPityGrowthRates(4, Arrays.asList(BaseStat.Level, BaseStat.CurrentHP, BaseStat.CurrentTP, BaseStat.Adrenaline));
            }
            
            @Override
            public void incrementFights() {
                ++info.fights;
                growthRates.addToAllPityGrowthRates(1, Arrays.asList(BaseStat.Level, BaseStat.CurrentHP, BaseStat.CurrentTP, BaseStat.Adrenaline));
            }
            
            @Override
            public void addTotalDamageDone(int dmg) {
                info.totalDamageDone += dmg;
            }
            
            @Override
            public void addTotalDamageTaken(int dmg) {
                info.totalDamageTaken += dmg;
                onAddTotalDamageTaken(dmg);
            }
            
            @Override
            public void addTotalTPlost(int tp) {
                info.totalTPlost += tp;
                onAddTotalTPlost(tp);
            }
            
            @Override
            public void addTotalExpGained(int exp) {
                info.totalExpGained += exp;
            }
            
            @Override
            public void addTotalHitsDodged(int dodges) {
                info.totalHitsDodged += dodges;
                onAddTotalHitsDodged(dodges);
            }
            
            @Override
            public void addTotalCriticals(int crits) {
                info.totalCriticals += crits;
                onAddTotalCriticals(crits);
            }
            
            @Override
            public void addTotalDurabilityUsed(float used) {
                info.totalDurabilityUsed += used;
                onAddTotalDurabilityUsed(used);
            }
        };
    }
    
    public Info getUnitInfo() {
        return info;
    }

    public StatisticsModifier getUnitStatisticsModifier() {
        return unitStatisticsModifier;
    }
    
    public static class Info {
        @Expose(deserialize = false) private Texture portraitTexture;
        private String portraitTexturePath;     // example: "Textures/portraits/Morva.png"
        private String battleOverlayConfigName; // example: "morva.json"
        
        int wins = 0, losses = 0, fights = 0;
        int totalDamageDone = 0, totalDamageTaken = 0, totalTPlost = 0, totalExpGained = 0, totalHitsDodged = 0, totalCriticals = 0;
        float totalDurabilityUsed = 0f;
        
        public Info(String portraitTexturePath, String battleOverlayConfigName) {
            this.portraitTexturePath = portraitTexturePath;
            this.battleOverlayConfigName = battleOverlayConfigName;
        }
        
        public Info(String portraitTexturePath) {
            this.portraitTexturePath = portraitTexturePath;
        }
    
        public Info() {}
        
        public void reset() {
            wins = 0;
            losses = 0;
            fights = 0;
            totalDamageDone = 0;
            totalDamageTaken = 0;
            totalTPlost = 0;
            totalExpGained = 0;
            totalHitsDodged = 0;
            totalCriticals = 0;
            totalDurabilityUsed = 0f;
        }
        
        public String getPortraitTexturePath() { return portraitTexturePath != null ? portraitTexturePath : "Textures/portraits/anonymous.png"; }
        public String getBattleOverlayConfigName() { return battleOverlayConfigName != null ? battleOverlayConfigName : "no_overlay.json"; } //overlay texture name is the same across all classes
        
        public Texture getPortraitTexture() { return portraitTexture; }
        public void setPortraitTexture(Texture tex) { portraitTexture = tex; }
        
        public boolean hasPortrait() { return portraitTexturePath != null; }
        public boolean hasBattleOverlayConfig() { return battleOverlayConfigName != null; }
        
        public int getWins() { return wins; }
        public int getLosses() { return losses; }
        public int getFights() { return fights; }
        
        public int getTotalDamageDone() { return totalDamageDone; }
        public int getTotalDamageTaken() { return totalDamageTaken; }
        public int getTotalTPlost() { return totalTPlost; }
        public int getTotalExpGained() { return totalExpGained; }
        public int getTotalHitsDodged() { return totalHitsDodged; }
        public int getTotalCriticals() { return totalCriticals; } 
        public float getTotalDurabilityUsed() { return totalDurabilityUsed; }
        
        public void setPortraitTexturePath(String fileName) { // use to change portrait
            portraitTexturePath = fileName;
        }
    
        public void setBattleOverlayConfigName(String overlayName) { // use for like: Morva to Morvanael
            battleOverlayConfigName = overlayName;
        }
    }
    
    public static interface StatisticsModifier {
        public void incrementWins();
        public void incrementLosses();
        public void incrementFights();
        public void addTotalDamageDone(int dmg);
        public void addTotalDamageTaken(int dmg);
        public void addTotalTPlost(int tp);
        public void addTotalExpGained(int exp);
        public void addTotalHitsDodged(int dodges);
        public void addTotalCriticals(int crits);
        public void addTotalDurabilityUsed(float used);
    }
    
    private void onAddTotalDamageTaken(int dmg) {
        float dmgRatio = ((float)dmg) / getMaxHP();
        
        int def = getDEF();
        int rsl = getRSL();
        
        BaseStat pityStat;
        if (dmgRatio > 0.5f) {
            //take the lower of the 2 defensive stats and use it as pity
            if (def <= rsl) {
                pityStat = BaseStat.Defense;
            } else {
                pityStat = BaseStat.Resilience;
            }
        } else {
            //take the higher of the 2 defensive stats and use it as pity
            if (rsl >= def) {
                pityStat = BaseStat.Resilience;
            } else {
                pityStat = BaseStat.Defense;
            }
        }
        
        growthRates.addToPityGrowthRate(pityStat, (int)(15f * dmgRatio));
        growthRates.addToPityGrowthRate(BaseStat.MaxHP, (int)(17.5f * dmgRatio));
    }
    
    private void onAddTotalTPlost(int tp) {
        growthRates.addToPityGrowthRate(BaseStat.MaxTP, (int)FastMath.clamp(tp / 2f, 1, 10));
    }
    
    private void onAddTotalHitsDodged(int dodges) {
        growthRates.addToPityGrowthRate(BaseStat.Agility, dodges * 5);
    }
    
    private void onAddTotalCriticals(int crits) {
        growthRates.addToPityGrowthRate(BaseStat.Dexterity, crits * 7);
    }
    
    private void onAddTotalDurabilityUsed(float used) {
        growthRates.addToPityGrowthRate(BaseStat.Physique, (int)FastMath.clamp(used, 1, 5));
    }
}
