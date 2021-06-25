/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.utils.helpers;

import com.atr.jme.font.shape.TrueTypeNode;
import com.google.gson.Gson;
import com.jme3.bounding.BoundingBox;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author night
 */
public class EngineUtils {
    
    public static void setLocalScaleFromCenter(Spatial target, float scale) {
        BoundingBox box = (BoundingBox)target.getWorldBound();
        float midpointXPreScale = box.getXExtent() / 2f;
        float midpointYPreScale = box.getYExtent() / 2f;
        float midpointZPreScale = box.getZExtent() / 2f;
        
        target.setLocalScale(scale);
        
        BoundingBox postBox = (BoundingBox)target.getWorldBound();
        float midpointXPostScale = postBox.getXExtent() / 2f;
        float midpointYPostScale = postBox.getYExtent() / 2f;
        float midpointZPostScale = postBox.getZExtent() / 2f;
        
        target.move((midpointXPreScale - midpointXPostScale) / FastMath.sqrt(2f), (midpointYPreScale - midpointYPostScale) / FastMath.sqrt(2f), (midpointZPreScale - midpointZPostScale) / FastMath.sqrt(2f));
    }
    
    public enum CenterAxis {
        X,
        Y,
        Z
    }
    
    public static Vector3f centerEntity(Vector3f entityDimensions, Vector3f backgroundDimensions, List<CenterAxis> centerAxes) {
        float centerX = (backgroundDimensions.x - entityDimensions.x) / 2f;
        float centerY = (backgroundDimensions.y - entityDimensions.y) / 2f;
        float centerZ = (backgroundDimensions.z - entityDimensions.z) / 2f;
        return new Vector3f
        (
            centerAxes.contains(CenterAxis.X) ? centerX : 0f,
            centerAxes.contains(CenterAxis.Y) ? centerY : 0f,
            centerAxes.contains(CenterAxis.Z) ? centerZ : 0f
        );
    }
    
    public static Vector3f deltaToCenter(Vector3f entityPos, Vector3f entityDimensions, Vector3f bgDimensions, Vector3f bgPos, List<CenterAxis> axes) {
        // offset = bgPos - entityPos + ((bgDimensions - entityDimensions) / 2)
        Vector3f offset = bgPos.subtract(entityPos).add((bgDimensions.subtract(entityDimensions)).divide(2f));
        
        return new Vector3f
        (
            axes.contains(CenterAxis.X) ? offset.x : 0f,
            axes.contains(CenterAxis.Y) ? offset.y : 0f,
            axes.contains(CenterAxis.Z) ? offset.z : 0f
        );
    }
    
    public static void center(Spatial entity, Vector3f entityDimensions, Vector3f bgDimensions, Vector3f bgPos, List<CenterAxis> axes) {
        entity.move(deltaToCenter(entity.getLocalTranslation(), entityDimensions, bgDimensions, bgPos, axes));
    }
    
    public static void center(Spatial entity, Spatial bg, Vector3f entityDimensions, Vector3f bgDimensions, List<CenterAxis> axes) {
        Vector3f worldOffset = deltaToCenter(entity.getWorldTranslation(), entityDimensions, bgDimensions, bg.getWorldTranslation(), axes);
        entity.move(entity.worldToLocal(worldOffset, null));
    }
    
    public static Vector3f generateBoundsToCenter(TrueTypeNode label) {
        return new Vector3f
        (
           label.getWidth(),
           label.getHeight(),
           0
        );
    }
    
    public static Vector3f centerTTFNode(TrueTypeNode label, Vector3f backgroundDimensions, List<CenterAxis> centerAxes) {
        return centerEntity(generateBoundsToCenter(label), backgroundDimensions, centerAxes);
    }
    
    public static <T> T deserialize(String jsonPath, Class<T> classOfT) {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get(jsonPath));
            return gson.fromJson(reader, classOfT);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
}
