/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.talent;

import battle.Conveyer;

/**
 *
 * @author night
 */
public abstract class TalentEffect {
    
    public abstract void inputData(Conveyer data); //call this first
    
    public abstract int[] battleBonusStats(); // {Acc, Eva, Crit, CritAvo, AS, ATK, En, EtherDef, Str, Ether} (permanent as long as talent equipped)
    public abstract int[] rawBonusStats(); // {Str, Ether, Agi, Comp, Dex, Def, Rsl, Mobility, Physique, Charisma} (permanent as long as talent equipped)
    
    public abstract int[] temporaryBuffs(); // {Str, Ether, Agi, Comp, Dex, Def, Rsl, Mobility, Physique}
    public abstract int[] temporaryEnemyDebuffs(); // {Str, Ether, Agi, Comp, Dex, Def, Rsl, Mobility, Physique}
    
    public abstract int[] userTranslation(); // {x, y}
    public abstract int[] enemyTranslation(); // {x, y}
   
    public abstract int[] enemyAOEDMG(); // {damage, range}
    
    public abstract void enactEffect(); //actually do the effect
    
    public int[] retrieveBattleBonusStats(Conveyer data) {
        inputData(data);
        return battleBonusStats();
    }
    
    public int[] retrieveRawBonusStats(Conveyer data) {
        inputData(data);
        return rawBonusStats();
    }
    
    public int[] retrieveTemporaryBuffs(Conveyer data) {
        inputData(data);
        return temporaryBuffs();
    }
    
    public int[] retrieveTemporaryEnemyDebuffs(Conveyer data) {
        inputData(data);
        return temporaryEnemyDebuffs();
    }
    
    public int[] retrieveUserTranslation(Conveyer data) {
        inputData(data);
        return userTranslation();
    }
    
    public int[] retrieveEnemyTranslation(Conveyer data) {
        inputData(data);
        return enemyTranslation();
    }
    
    public int[] retrieveEnemyAOEDMG(Conveyer data) {
        inputData(data);
        return enemyAOEDMG();
    }
    
    public void doEffect(Conveyer data) {
        inputData(data);
        enactEffect();
    }
    
}
