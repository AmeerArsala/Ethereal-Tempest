/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.formation;

import battle.formation.FormationTechnique;

/**
 *
 * @author night
 */
public class Formation {
    private String name = "", desc = "", imagePath = "Interface/GUI/formation_infographics/";
    private String formationType = "";
    
    public boolean isElite;
    public boolean exists = true;
    
    private int stars;
    private FormationTechnique[] techniques;
    
    public Formation(String name, String desc, String type, boolean elite, int stars, String imageName, FormationTechnique[] techniques) {
        this.name = name;
        this.desc = desc;
        this.stars = stars;
        this.techniques = techniques;
        imagePath += imageName;
        formationType = type;
        isElite = elite;
    }
    
    public String getFormationType() { return formationType; }
    public int getStars() { return stars; }
    public String getDescription() { return desc; }
    public String getPath() { return imagePath; }
    
    public Formation(boolean ex) { exists = ex; }
    
    public double formationCoefficient() {
        if (isElite) {
            if (formationType.equals("diamond")) {
                return (5.0/30.0);
            } else { return 0.15; }
        }
        return 0.1;
    }
    
    @Override
    public String toString() { return name; }
    
}

    

