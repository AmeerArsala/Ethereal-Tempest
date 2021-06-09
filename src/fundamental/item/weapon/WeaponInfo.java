/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.item.weapon;

import com.google.gson.Gson;
import fundamental.jobclass.JobClass.MobilityType;
import fundamental.item.BasicItem;
import fundamental.stats.BaseStat;
import fundamental.stats.RawBroadBonus;
import fundamental.tool.DamageTool;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author night
 */
public class WeaponInfo {
    private BasicItem template;
    private float durability;
    private int requiredLevel;
    private String prf;
    
    private int pow;
    private int accuracy;
    private int crit;
    private int[] range;
    private BaseStat damageMeasuredAgainstStat;
    private WeaponType weaponType;
    private WeaponAttribute attribute;
    private MobilityType[] effectiveAgainst;
    
    public WeaponInfo(
            BasicItem template, float durability, int requiredLevel, String prf, 
            int pow, int accuracy, int crit, int[] range, boolean doesEtherDmg,
            BaseStat damageMeasuredAgainstStat, WeaponType weaponType, WeaponAttribute attribute, MobilityType[] effectiveAgainst) {
        this.template = template;
        this.durability = durability;
        this.requiredLevel = requiredLevel;
        this.prf = prf;
        this.pow = pow;
        this.accuracy = accuracy;
        this.crit = crit;
        this.range = range;
        this.damageMeasuredAgainstStat = damageMeasuredAgainstStat;
        this.weaponType = weaponType;
        this.attribute = attribute;
        this.effectiveAgainst = effectiveAgainst;
    }
    
    //public Item getTemplate() { return template.construct(); }
    public double getDurability() { return durability; }
    public int getRequiredLevel() { return requiredLevel; }
    public String getPRF() { return prf != null ? prf : "None"; }
    
    public int getPow() { return pow; }
    public int getAcc() { return accuracy; }
    public int getCrit() { return crit; }
    public BaseStat getDamageMeasuredAgainstStat() { return damageMeasuredAgainstStat; }
    public WeaponType getWPNType() { return weaponType; }
    public WeaponAttribute getWPNAttribute() { return attribute; }
    
    public ArrayList<MobilityType> getEffectiveAgainst() {
        ArrayList<MobilityType> effs = new ArrayList<>();
        if (effectiveAgainst == null) { return effs; }
        
        effs.addAll(Arrays.asList(effectiveAgainst));
        
        return effs;
    }
    
    public List<Integer> getRange() {
        if (range == null) {
            return new ArrayList<>();
        }
        
        List<Integer> ranges = new ArrayList<>();
        for (int i = 0; i < range.length; i++) {
            ranges.add(range[i]);
        }
        
        return ranges;
    }
    
    //onEquip is onEquip
    public DamageTool constructTool(RawBroadBonus onEquip) {
        return new DamageTool(pow, accuracy, crit, getRange(), weaponType, attribute, damageMeasuredAgainstStat, getEffectiveAgainst(), onEquip);
    }
    
    public Weapon constructWeapon(RawBroadBonus onEquip, RawBroadBonus passive) {
        return new Weapon(template.construct(passive), constructTool(onEquip), durability, requiredLevel, prf);
    }
    
    public Weapon constructWeapon(RawBroadBonus passive) {
        return new Weapon(template.construct(passive), constructTool(null), durability, requiredLevel, prf);
    }
    
    public static Weapon deserialize(String jsonName, RawBroadBonus passive, RawBroadBonus onEquip) {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\GameInfo\\EntityPresets\\items\\weapons\\" + jsonName));
            
            return gson.fromJson(reader, WeaponInfo.class).constructWeapon(onEquip, passive);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static Weapon deserialize(String jsonName, RawBroadBonus passive) {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\GameInfo\\EntityPresets\\items\\weapons\\" + jsonName));
            
            return gson.fromJson(reader, WeaponInfo.class).constructWeapon(passive);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
