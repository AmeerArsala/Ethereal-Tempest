/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.talent;

import fundamental.Associated;
import fundamental.Tool.ToolType;

/**
 *
 * @author night
 */
public class Talent extends Associated {
    private String loredesc = "";
    private String imagePath = "";
    private ToolType type;
    private TalentConcept body;
    
    protected TalentConcept[] additionalEffects;
    
    public Talent(String talentname, ToolType t_type, String lore, String description, String imgPath) {
        super(talentname, description);
        type = t_type;
        loredesc = lore;
        imagePath = imgPath;
    }
    
    public Talent(String talentname, ToolType t_type, String lore, String description, String imgPath, TalentConcept tc) {
        super(talentname, description);
        type = t_type;
        loredesc = lore;
        imagePath = imgPath;
        body = tc;
    }
    
    public Talent (boolean ex) {
        super(ex);
    }
    
    public ToolType getToolType() { return type; }
 
    public String getLoreDescription() { return loredesc; }
    public String getIconPath() { return imagePath; }
    
    public TalentConcept[] getAdditionalEffects() { return additionalEffects; }
    
    public Talent setAdditionalEffects(TalentConcept[] tal) {
        additionalEffects = tal;
        return this;
    }
    
    public TalentConcept getBody() { return body; }
    
    @Override
    public String toString() { return name; }
    
    
}
