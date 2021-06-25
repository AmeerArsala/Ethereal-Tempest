/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.environment;

import com.google.gson.Gson;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author night
 * 
 * use gson on this
 */
public class BattleViewInfo {
    private String modelPath;
    
    /**
     * Used for calculating starting positions
     * if the right edge was at 100% of the BattleBox's width, it would be 1.0
     * if the right edge was at 0% of the BattleBox's width, it would be 0.0
     * if the right edge was at -100% of the BattleBox's width, it would be -1.0
     * Same applies to the rest below
     */
    private float rightEdgePositionPercent;
    private float leftEdgePositionPercent;
    private float topEdgePositionPercent;
    private float bottomEdgePositionPercent;

    private float zLocation; // the z value where the fight takes place
    
    //location of camera
    private float cameraX;
    private float cameraY; 
    private float cameraZ;
    
    private float envCamPosZ;
    
    public BattleViewInfo(String modelPath, float rightEdgePositionPercent, float leftEdgePositionPercent, float topEdgePositionPercent, float bottomEdgePositionPercent, float zLocation, float cameraX, float cameraY, float cameraZ, float envCamPosZ) {
        this.modelPath = modelPath;
        this.rightEdgePositionPercent = rightEdgePositionPercent;
        this.leftEdgePositionPercent = leftEdgePositionPercent;
        this.topEdgePositionPercent = topEdgePositionPercent;
        this.bottomEdgePositionPercent = bottomEdgePositionPercent;
        this.zLocation = zLocation;
        this.cameraX = cameraX;
        this.cameraY = cameraY;
        this.cameraZ = cameraZ;
        this.envCamPosZ = envCamPosZ;
    }
    
    public String getModelPath() { return modelPath; }
    
    public float getRightEdgePositionPercent() { return rightEdgePositionPercent; }
    public float getLeftEdgePositionPercent() { return leftEdgePositionPercent; }
    public float getTopEdgePositionPercent() { return topEdgePositionPercent; }
    public float getBottomEdgePositionPercent() { return bottomEdgePositionPercent; }

    public float getZLocation() { return zLocation; }
    
    public float getEnvCamPosZ() { return envCamPosZ; }
    
    public Vector3f getCameraLocation() {
        return new Vector3f(cameraX, cameraY, cameraZ);
    }
    
    
    public static BattleViewInfo deserialize(String jsonPath) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get("assets\\GameInfo\\MapPresets\\BattleTerrains\\" + jsonPath));
            Gson gson = new Gson();
            
            return gson.fromJson(reader, BattleViewInfo.class);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        
        return null;
    }
}
