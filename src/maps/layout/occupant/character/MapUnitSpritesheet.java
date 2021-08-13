/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.occupant.character;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import maps.layout.Coords;

/**
 *
 * @author night
 */
public class MapUnitSpritesheet { //this is specifically for TangibleUnit sprites (on map sprites)
    public enum AnimationState {
        @SerializedName("MovingDown") MovingDown(new Coords(0, -1)),
        @SerializedName("MovingRight") MovingRight(new Coords(1, 0)),
        @SerializedName("MovingLeft") MovingLeft(new Coords(-1, 0)),
        @SerializedName("MovingUp") MovingUp(new Coords(0, 1)),
        
        @SerializedName("Idle") Idle(),
        @SerializedName("Idle2") Idle2();
        
        private Coords deltaValues;
        private AnimationState(Coords deltas) {
            deltaValues = deltas;
        }
        
        private AnimationState() {}
        
        public static AnimationState directionalValueOf(Coords deltaDir) {
            for (int i = 0; i < 4; i++) { //only the directions
                AnimationState direction = AnimationState.values()[i];
                if (direction.deltaValues.equals(deltaDir)) {
                    return direction;
                }
            }
            
            return null;
        }
        
        public Coords getDeltas() { return deltaValues; }
    }
    
    private int maxColumns;
    private SheetRow[] rows; //order matters; first item would be the top row on the spritesheet
    
    private String fileName;
    private String outlineSheet;
    
    @Expose(deserialize = false) private String folderName;
    @Expose(deserialize = false) private HashMap<AnimationState, Integer> startingPositions; //the actual positions in terms of material.setFloat("Position", ...);
    
    public MapUnitSpritesheet(SheetRow[] rows, int maxColumns, String fileName, String outlineSheet) {
        this.rows = rows;
        this.maxColumns = maxColumns;
        this.fileName = fileName;
        this.outlineSheet = outlineSheet;
    }
    
    private class SheetRow {
        private int columns = -1;
        private AnimationState animation;
        
        public SheetRow(int columns, AnimationState animation) {
            this.columns = columns;
            this.animation = animation;
        }
    }

    public int getRowCount() { return rows.length; }
    public int getMaxColumnCount() { return maxColumns; }
    
    public int getColumnCount(int row) {
        return rows[row].columns > 0 ? rows[row].columns : maxColumns;
    }
    
    public int getColumnCount(AnimationState anim) {
        return getColumnCount(getRow(anim));
    }
    
    public AnimationState getAnimation(int row) {
        return rows[row].animation;
    }
    
    public String getSheet() { return fileName; }
    public String getOutlineSheet() { return outlineSheet; }
    
    public String getFolderName() { return folderName; } 
    
    public boolean hasAnimation(AnimationState anim) {
        for (SheetRow row : rows) {
            if (row.animation == anim) {
                return true;
            }
        }
        
        return false;
    }
    
    public int getRow(AnimationState anim) {
        for (int i = 0; i < rows.length; i++) {
            if (rows[i].animation == anim) {
                return i;
            }
        }
        
        return -1;
    }
    
    public int retrieveFrameLength(int endRow) { //sums up the frames in the columns of all the rows <= endRow
        int sum = 0;
        for (int i = 0; i <= endRow; i++) {
            sum += getColumnCount(i);
        }
        
        return sum;
    }
    
    public MapUnitSpritesheet setAnimations() {
        startingPositions = new HashMap<>();
        startingPositions.put(rows[0].animation, 0);
        for (int i = 1; i < rows.length; i++) {
            startingPositions.put(rows[i].animation, retrieveFrameLength(i - 1));
        }
        
        return this;
    }
    
    public MapUnitSpritesheet setFolderName(String name) {
        folderName = name;
        return this;
    }
    
    public int getStartingPosition(AnimationState anim) {
        return startingPositions.get(anim);
    }
}
