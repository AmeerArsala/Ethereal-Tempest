/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle;

import battle.Combatant.BaseStat;
import battle.Combatant.BattleStat;
import battle.parse.AttackConfig;
import com.google.gson.Gson;
import com.jme3.asset.AssetManager;
import com.jme3.texture.Texture;
import fundamental.StatBundle;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import misc.CustomAnimationSegment;

/**
 *
 * @author Ameer Arsala
 */
public class JobClass {
    protected String jobname = "";
    protected AttackConfig attackAnimation;

    private final int tier;
    
    private final HashMap<BaseStat, Integer> bonusStats;
    private final HashMap<BaseStat, Integer> maxStats;
    private final HashMap<BattleStat, Integer> battleBonus; //acc, avo, crit, crit avo
    
    private final List<String> wieldableWeaponTypes;
    private final List<String> mobilityTypes;
    
    protected HashMap<String, CustomAnimationSegment> customSkillAnimations = new HashMap<>();
    
    private Texture combatSheet;
    
    public JobClass(String jobname, List<String> mobilityTypes, List<String> wieldableWeaponTypes, List<StatBundle> bonusStats, List<StatBundle> battleBonus, List<StatBundle> maxStats, int tier) {
        this.jobname = jobname;
        this.mobilityTypes = mobilityTypes;
        this.wieldableWeaponTypes = wieldableWeaponTypes;
        this.tier = tier;
        
        this.bonusStats = StatBundle.createBaseStatsFromBundles(bonusStats);
        this.battleBonus = StatBundle.createBattleStatsFromBundles(battleBonus);
        this.maxStats = StatBundle.createBaseStatsFromBundles(maxStats);
    }
    
    public JobClass(String jobname, List<String> mobilityTypes, List<String> wieldableWeaponTypes, HashMap<BaseStat, Integer> bonusStats, HashMap<BattleStat, Integer> battleBonus, HashMap<BaseStat, Integer> maxStats, int tier) {
        this.jobname = jobname;
        this.mobilityTypes = mobilityTypes;
        this.wieldableWeaponTypes = wieldableWeaponTypes;
        this.tier = tier;
        
        this.bonusStats = bonusStats;
        this.battleBonus = battleBonus;
        this.maxStats = maxStats;
    }
    
    public String clName() { return jobname; }
    public int clTier() { return tier; }

    public List<String> UsableWeapons() { return wieldableWeaponTypes; } // {"sword", "axe", "polearm", "knife", "bow", "whip", "monster", "pi ether", "gamma ether", "delta ether", "omega ether"}
    public List<String> MovementType() { return mobilityTypes; } // infantry, armored, cavalry, flier, mechanism, morph, monster
    public final HashMap<BaseStat, Integer> ClassStatBonus() { return bonusStats; }
    public final HashMap<BaseStat, Integer> ClassMaxStats() { return maxStats; }
    public final HashMap<BattleStat, Integer> ClassBattleBonus() { return battleBonus; } // Acc, Avo, Crit, CritAvo, AS, ATK, En, EtherDef
    
    private AttackConfig deserializeFromJSON() {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\Models\\Sprites\\battle\\" + jobname + "\\config.json"));
            return gson.fromJson(reader, AttackConfig.class);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public void initializeFrames(AssetManager AM) {
        attackAnimation = deserializeFromJSON();
        combatSheet = AM.loadTexture(attackAnimation.getSpritesheet());
        System.out.println(attackAnimation.toString());
    }
    
    public AttackConfig getBattleConfig() { return attackAnimation; }
    public Texture getCombatSheet() { return combatSheet; }
    
    public JobClass addCustomSkillAnimation(String skillName, CustomAnimationSegment values) {
        customSkillAnimations.put(skillName, values);
        return this;
    }
    
    public HashMap<String, CustomAnimationSegment> getCustomSkillAnimations() {
        return customSkillAnimations;
    }
    
    protected void setCustomSkillAnimations(HashMap<String, CustomAnimationSegment> anims) {
        customSkillAnimations = anims;
    }
    
    @Override
    public String toString() { return jobname; }
    
    //everything below needs to be fixed
    
    /*public List<Texture> getAttackSheets() { return attack; }
    public List<Texture> getAttackAndFollowupSheets() { return attack_and_followup; }
    public List<Texture> getCriticalSheets() { return critical; }
    public List<Texture> getFinisherSheets() { return finisher; }
    
    public JobClass setCombatFrames(AssetManager AM) {
        attack = attackFrames(AM);
        attack_and_followup = attack_and_followupFrames(AM);
        critical = criticalFrames(AM);
        finisher = finisherFrames(AM);
        return this;
    }
    
    public void setFramesByModeName(String mode, AssetManager AM) {
         switch (mode) {
            case "attack":
                attack = attackFrames(AM);
                break;
            case "attack_and_followup":
                attack_and_followup = attack_and_followupFrames(AM);
                break;
            case "critical":
                critical = criticalFrames(AM);
                break;
            case "finisher":
                finisher = finisherFrames(AM);
                break;
            default:
                break;
        }
    }
    
    public List<Texture> getSheetsByModeName(String mode) {
        switch (mode) {
            case "attack":
                return attack;
            case "attack_and_followup":
                return attack_and_followup;
            case "critical":
                return critical;
            case "finisher":
                return finisher;
            default:
                return null;
        }
    }*/
    
    /*private class Index {
        private int val = 0;
        
        Index(int eger) {
            val = eger;
        }
        
        Index() {}
        
        int getValue() { return val; }
        void setValue(int next) { val = next; }
    }
    
    private Index atk = new Index(), followup = new Index(), crit = new Index(), fin = new Index();
    
    public void setNextFrameByModeName(String mode, AssetManager asm, int digits) {
        Index i;
        List<TexturePlus> target;
        switch (mode) {
            case "attack":
                target = attack;
                i = atk;
                break;
            case "attack_and_followup":
                target = attack_and_followup;
                i = followup;
                break;
            case "critical":
                target = critical;
                i = crit;
                break;
            case "finisher":
                target = finisher;
                i = fin;
                break;
            default:
                target = null;
                i = null;
                break;
        }
        
        int zeroes = digits - 1 - CustomAnimationSegment.getBase10(i.getValue());
        String address = CustomAnimationSegment.amountOfNumber(0, zeroes) + i.getValue();
        Texture tex;
        ImpactType impactStatus = ImpactType.None;
        try {
            tex = asm.loadTexture("Models/Sprites/battle/" + clName() + "/" + mode + "/" + address + ".png");
        }
        catch (AssetNotFoundException e) {
            try {
                tex = asm.loadTexture("Models/Sprites/battle/" + clName() + "/" + mode + "/" + address + "ds.png");
                impactStatus = ImpactType.All;
            }
            catch (AssetNotFoundException e2) {
                tex = asm.loadTexture("Models/Sprites/battle/" + clName() + "/" + mode + "/" + address + "s.png");
                impactStatus = ImpactType.SoundOnly;
            }
        }
            
        target.add(new TexturePlus(tex, impactStatus));
        i.setValue(i.getValue() + 1);
    }
    
    public List<Runnable[]> getLoadableTasks(AssetManager asm, int digits) {
        return 
                Arrays.asList(
                    getPartialTasks("attack", asm, digits), 
                    getPartialTasks("attack_and_followup", asm, digits),
                    getPartialTasks("critical", asm, digits),
                    getPartialTasks("finisher", asm, digits)
                );
        
    }
    
    private Runnable[] getPartialTasks(String mode, AssetManager asm, int digits) {
        Runnable[] partial = new Runnable[DirFileExplorer.FileCount(new File("assets\\Models\\Sprites\\battle\\" + clName() + "\\" + mode +"\\"), "png")];
        for (int i = 0; i < partial.length; i++) {
            partial[i] = new Runnable() {
                @Override
                public void run() {
                    setNextFrameByModeName(mode, asm, digits);
                }
            };
        }
        return partial;
    }*/
    
    /*private List<Texture> attackFrames(AssetManager asm) {
        List<Texture> textures = new ArrayList<>();
        
        for (int i = 0; i < attackAnimation.getAllSheets(attackAnimation.getAttack()).size(); i++) {
            textures.add(asm.loadTexture(attackAnimation.getAllSheets(attackAnimation.getAttack()).get(i).getSheetPath()));
        }
        
        return textures;
    }
    
    private List<Texture> attack_and_followupFrames(AssetManager asm) {
        List<Texture> textures = new ArrayList<>();
        
        for (int i = 0; i < attackAnimation.getAllSheets(attackAnimation.getAttackAndFollowup()).size(); i++) {
            textures.add(asm.loadTexture(attackAnimation.getAllSheets(attackAnimation.getAttackAndFollowup()).get(i).getSheetPath()));
        }
        
        return textures;
    }
    
    private List<Texture> criticalFrames(AssetManager asm) {
        List<Texture> textures = new ArrayList<>();
        
        for (int i = 0; i < attackAnimation.getAllSheets(attackAnimation.getCritical()).size(); i++) {
            textures.add(asm.loadTexture(attackAnimation.getAllSheets(attackAnimation.getCritical()).get(i).getSheetPath()));
        }
        
        return textures;
    }
    
    private List<Texture> finisherFrames(AssetManager asm) {
        List<Texture> textures = new ArrayList<>();
        
        for (int i = 0; i < attackAnimation.getAllSheets(attackAnimation.getFinisher()).size(); i++) {
            textures.add(asm.loadTexture(attackAnimation.getAllSheets(attackAnimation.getFinisher()).get(i).getSheetPath()));
        }
        
        return textures;
    }*/
    
}