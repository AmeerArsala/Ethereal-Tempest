/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.talent;

/**
 *
 * @author night
 */
public class Talent {
    private String name = "", desc = "", loredesc = "";
    private String imagePath = "";
    private TalentConcept body;
    
    public boolean exists = true;
    
    protected TalentConcept[] additionalEffects;
    
    public Talent(String talentname, String lore, String description, String imgPath) {
        name = talentname;
        loredesc = lore;
        desc = description;
        imagePath = imgPath;
    }
    
    public Talent(String talentname, String lore, String description, String imgPath, TalentConcept tc) {
        name = talentname;
        loredesc = lore;
        desc = description;
        imagePath = imgPath;
        body = tc;
    }
    
    public Talent (boolean ex) { exists = ex; }
 
    public String getName() { return name; }
    public String getLoreDescription() { return loredesc; }
    public String getDescription() { return desc; }
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
