/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.item;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author night
 */
public class WeaponInfo {
    private BasicItem template;
    private double durability;
    private int requiredLevel;
    private String prf;
    
    public WeaponInfo(BasicItem template, double durability, int requiredLevel, String prf) {
        this.template = template;
        this.durability = durability;
        this.requiredLevel = requiredLevel;
        this.prf = prf;
    }
    
    public Item getTemplate() { return template.construct(); }
    public double getDurability() { return durability; }
    public int getRequiredLevel() { return requiredLevel; }
    public String getPRF() { return prf != null ? prf : "None"; }
    
    public static WeaponInfo deserialize(String jsonName) {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\GameInfo\\items\\weapons\\" + jsonName));
            
            return gson.fromJson(reader, WeaponInfo.class);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
