/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.formation;

import fundamental.RawBroadBonus;
import fundamental.stats.StatBundle;
import fundamental.stats.StatBundleDeserialization;
import fundamental.tool.Tool.ToolType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class FormationDeserialization {
    private String name;
    private String desc;
    private int tier;
    private StatBundleDeserialization statBonus;
    
    public FormationDeserialization() {}
    
    public FormationDeserialization(String name, String desc, int tier, StatBundleDeserialization statBonus) {
        this.name = name;
        this.desc = desc;
        this.tier = tier;
        this.statBonus = statBonus;
    }
    
    public Formation constructFormation(List<FormationTechnique> techniques) {
        return new Formation(name, desc, tier, statBonus.constructStatBundle(), techniques);
    }
    
    public Formation constructFormation(List<FormationTechnique> techniques, RawBroadBonus passive) {
        return new Formation(name, desc, passive, tier, statBonus.constructStatBundle(), techniques);
    }
}
