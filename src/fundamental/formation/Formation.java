/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.formation;

import com.google.gson.Gson;
import fundamental.unit.UnitAllegiance;
import fundamental.Gear;
import fundamental.stats.Bonus.StatType;
import fundamental.stats.StatBundle;
import fundamental.stats.BaseStat;
import fundamental.stats.RawBroadBonus;
import fundamental.talent.Talent;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import maps.layout.MapCoords;
import general.utils.wrapper.Duo;

/**
 *
 * @author night
 */
public class Formation extends Gear {
    private static final HashMap<BaseStat, Duo<String, Integer>> typeMap = new HashMap<>();
    
    static {
        typeMap.put(BaseStat.MaxHP, new Duo<>("Aquarius", 0));
        typeMap.put(BaseStat.MaxTP, new Duo<>("Pisces", 1));
        typeMap.put(BaseStat.Strength, new Duo<>("Leo", 2));
        typeMap.put(BaseStat.Ether, new Duo<>("Scorpio", 3));
        typeMap.put(BaseStat.Agility, new Duo<>("Aries", 4));
        typeMap.put(BaseStat.Comprehension, new Duo<>("Libra", 5));
        typeMap.put(BaseStat.Dexterity, new Duo<>("Gemini", 6));
        typeMap.put(BaseStat.Defense, new Duo<>("Virgo", 7));
        typeMap.put(BaseStat.Resilience, new Duo<>("Capricorn", 8));
        typeMap.put(BaseStat.Mobility, new Duo<>("Sagittarius", 9));
        typeMap.put(BaseStat.Physique, new Duo<>("Taurus", 10));
        typeMap.put(BaseStat.Adrenaline, new Duo<>("Cancer", 11));
    }
    
    private int tier;
    private int formationTypeIndex;
    private String formationType;
    private List<FormationTechnique> techniques;
    
    public Formation(String name, String desc, int tier, StatBundle statBonus, List<FormationTechnique> techniques) {
        super(name, desc);
        this.tier = tier;
        this.techniques = techniques;
        
        passive = new RawBroadBonus(statBonus);
        initializeFormationType(statBonus);
    }
    
    public Formation(String name, String desc, RawBroadBonus passive, int tier, StatBundle statBonus, List<FormationTechnique> techniques) {
        super(name, desc, passive);
        this.tier = tier;
        this.techniques = techniques;
        
        Talent toTalent = new RawBroadBonus(statBonus).getBonusTalent();
        passive.getBonusTalent().getFullBody().addAll(toTalent.getFullBody());
        
        initializeFormationType(statBonus);
    }
    
    private void initializeFormationType(StatBundle statBonus) {
        if (statBonus != null && statBonus.getStatType() == StatType.Base) {
            Duo<String, Integer> type = typeMap.get((BaseStat)statBonus.getStat());
            formationType = type.first;
            formationTypeIndex = type.second;
        } else {
            formationType = "Wildcard";
        }
    }
    
    public int getTier() { return tier; }
    public int getFormationTypeIndex() { return formationTypeIndex; }
    public String getFormationType() { return formationType; }
    public List<FormationTechnique> getTechniques() { return techniques; }
    
    public FormationTechnique getMostDesiredTechnique() {
        FormationTechnique highest = techniques.get(0);
        for (FormationTechnique tech : techniques) {
            if (tech.getDesirability() > highest.getDesirability()) {
                highest = tech;
            }
        }
        
        return highest;
    }
    
    public List<Integer> getFullRange() {
        List<Integer> ranges = new ArrayList<>();
        for (FormationTechnique tech : techniques) {
            for (Integer range : tech.getRanges()) {
                if (!ranges.contains(range)) {
                    ranges.add(range);
                }
            }
        }
        
        return ranges;
    }
    
    public List<Integer> getPartialRange(boolean supportive) {
        List<Integer> ranges = new ArrayList<>();
        for (FormationTechnique tech : techniques) {
            if (tech.getToolType().isSupportive() == supportive) {
                for (Integer range : tech.getRanges()) {
                    if (!ranges.contains(range)) {
                        ranges.add(range);
                    }
                }
            }
        }
        
        return ranges;
    }
    
    public boolean isAvailableAt(MapCoords pos, UnitAllegiance userAllegiance) { 
        for (FormationTechnique tech : techniques) {
            if (tech.isAvailableAt(pos, userAllegiance)) {
                return true;
            }
        }
        
        return false;
    }
    
    public List<FormationTechnique> techniquesAvailableAt(MapCoords pos, UnitAllegiance userAllegiance) {
        List<FormationTechnique> techs = new ArrayList<>();
        
        for (FormationTechnique tech : techniques) {
            if (tech.isAvailableAt(pos, userAllegiance)) {
                techs.add(tech);
            }
        }
        
        return techs;
    }
    
    @Override
    public String toString() {
        return name + "\n \n" + desc + "\n \n";
    }
    
    
    public static final Formation Trigonal_Planar() {
        return deserializeFormation("Trigonal Planar.json", Arrays.asList(FormationTechnique.FallBack()));
    }
    
    private static Formation deserializeFormation(String jsonName, List<FormationTechnique> techniques) {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\GameInfo\\EntityPresets\\formations\\" + jsonName));
            
            return gson.fromJson(reader, FormationDeserialization.class).constructFormation(techniques); 
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    private static Formation deserializeFormation(String jsonName, List<FormationTechnique> techniques, RawBroadBonus extraPassive) {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\GameInfo\\EntityPresets\\formations\\" + jsonName));
            
            return gson.fromJson(reader, FormationDeserialization.class).constructFormation(techniques, extraPassive); 
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

    

