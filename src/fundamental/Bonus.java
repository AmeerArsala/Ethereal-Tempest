/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental;

import battle.Combatant.BaseStat;
import battle.Combatant.BattleStat;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author night
 */
public class Bonus {
    public enum BonusType {
        @SerializedName("Raw") Raw, //always active
        @SerializedName("StartOfPlayerTurn") StartOfPlayerTurn,
        @SerializedName("StartOfEnemyTurn") StartOfEnemyTurn
    }
    
    private int value;
    private BonusType bonusType;
    private BaseStat baseStatBonus = null;
    private BattleStat battleStatBonus = null;
    
    public Bonus(int value, BonusType bonusType, BaseStat baseStatBonus, BattleStat battleStatBonus) {
        this.value = value;
        this.bonusType = bonusType;
        this.baseStatBonus = baseStatBonus;
        this.battleStatBonus = battleStatBonus;
    }
    
    public Bonus(int value, BonusType bonusType, BaseStat baseStatBonus) {
        this.value = value;
        this.bonusType = bonusType;
        this.baseStatBonus = baseStatBonus;
    }
    
    public Bonus(int value, BonusType bonusType, BattleStat battleStatBonus) {
        this.value = value;
        this.bonusType = bonusType;
        this.battleStatBonus = battleStatBonus;
    }
    
    public int getValue() { return value; }
    public BonusType getType() { return bonusType; }
    public BaseStat getBaseStat() { return baseStatBonus; }
    public BattleStat getBattleStat() { return battleStatBonus; }
}
