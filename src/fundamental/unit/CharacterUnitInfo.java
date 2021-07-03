/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.unit;

/**
 *
 * @author night
 */
public class CharacterUnitInfo {
    private String portraitTextureName;     // example: "Morva.png"
    private String battleOverlayConfigName; // example: "morva.json"

    private int wins = 0, losses = 0, fights = 0;
    private int totalDamageDone = 0, totalDamageTaken = 0, totalTPlost = 0, totalExpGained = 0, totalHitsDodged = 0, totalCriticals = 0;
    private float totalDurabilityUsed = 0f;
    
    public CharacterUnitInfo(String portraitTextureName, String battleOverlayConfigName) {
        this.portraitTextureName = portraitTextureName;
        this.battleOverlayConfigName = battleOverlayConfigName;
    }
    
    public CharacterUnitInfo(String portraitTextureName) {
        this.portraitTextureName = portraitTextureName;
    }
    
    public CharacterUnitInfo() {}
    
    public String getPortraitTextureName() { return portraitTextureName != null ? portraitTextureName : "anonymous.png"; }
    public String getBattleOverlayConfigName() { return battleOverlayConfigName != null ? battleOverlayConfigName : "no_overlay.json"; } //overlay texture name is the same across all classes
    
    public boolean hasPortrait() { return portraitTextureName != null; }
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
        portraitTextureName = fileName;
    }
    
    public void setBattleOverlayConfigName(String overlayName) { // use for like: Morva to Morvanael
        battleOverlayConfigName = overlayName;
    }
    
    public void incrementWins() {
        ++wins;
    }
    
    public void incrementLosses() {
        ++losses;
    }
    
    public void incrementFights() {
        ++fights;
    }
    
    public void addTotalDamageDone(int dmg) {
        totalDamageDone += dmg;
    }
    
    public void addTotalDamageTaken(int dmg) {
        totalDamageTaken += dmg;
    }
    
    public void addTotalTPlost(int tp) {
        totalTPlost += tp;
    }
    
    public void addTotalExpGained(int exp) {
        totalExpGained += exp;
    }
    
    public void addTotalHitsDodged(int dodges) {
        totalHitsDodged += dodges;
    }
    
    public void addTotalCriticals(int crits) {
        totalCriticals += crits;
    }
    
    public void addTotalDurabilityUsed(float used) {
        totalDurabilityUsed += used;
    }
}
