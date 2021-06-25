/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation.config;

import general.math.DomainBox;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.jme3.math.Vector2f;
import general.utils.helpers.GeneralUtils;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author night
 */
public class AttackSheetConfig {
    private int[] framesPerColumn;
    private Vector2f damageNumberLocation; //in percentages of the sprite size, going over 100% is ok
    private Vector2f hitpoint;
    private DomainBox hurtbox;
    private boolean letEnemyChangeTransformationValues;
    
    @Expose(deserialize = false) 
    private String fileRoot; 
    
    public AttackSheetConfig(int[] framesPerColumn, Vector2f damageNumberLocation, Vector2f hitpoint, DomainBox hurtbox, boolean letEnemyChangeTransformationValues) {
        this.framesPerColumn = framesPerColumn;
        this.damageNumberLocation = damageNumberLocation;
        this.hitpoint = hitpoint;
        this.hurtbox = hurtbox;
        this.letEnemyChangeTransformationValues = letEnemyChangeTransformationValues;
    }
    
    public AttackSheetConfig setFileRoot(String fileRoot) { //path root
        this.fileRoot = fileRoot;
        return this;
    }
    
    //also replaces all backslashes (\) with forward slashes (/)
    public String getSpritesheetImagePath() {
        return (fileRoot + "spritesheet.png").replaceAll("\\\\", "/");
    }
    
    //also replaces all backslashes (\) with forward slashes (/)
    public String getOverlayConfigPath(String overlayFile) { //path to the overlay config; Example: "morva.json"
        return (fileRoot + "character_overlay\\" + overlayFile).replaceAll("\\\\", "/");
    }
    
    public int getRows() { return GeneralUtils.highestInt(framesPerColumn); }
    public int getColumns() { return framesPerColumn.length; }
    public int[] getFramesPerColumn() { return framesPerColumn; }
    public Vector2f getDamageNumberLocation() { return damageNumberLocation; }
    public Vector2f getHitPoint() { return hitpoint; }
    public DomainBox getHurtbox() { return hurtbox; }
    public boolean letEnemyChangeTransformationValues() { return letEnemyChangeTransformationValues; }
    
    public int convertToIntPosition(int column, int index) {
        return column + (index * getColumns());
    }
    
    //maybe remove this
    public static AttackSheetConfig deserializeManual(String folderRoot) { //folderRoot must end with "\\"
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\Sprites\\" + folderRoot + "config.json"));
            
            AttackSheetConfig sheetConfig = gson.fromJson(reader, AttackSheetConfig.class);
            sheetConfig.fileRoot = "Sprites\\" + folderRoot;
            
            return sheetConfig;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
