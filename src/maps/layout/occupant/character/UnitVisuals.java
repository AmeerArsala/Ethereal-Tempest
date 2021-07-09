/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.occupant.character;

import com.google.gson.Gson;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.MagFilter;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.LayerComparator;
import com.simsilica.lemur.ProgressBar;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.component.TbtQuadBackgroundComponent;
import enginetools.math.Vector2F;
import etherealtempest.Main;
import etherealtempest.geometry.GeometricBody;
import etherealtempest.gui.broad.BasicProgressBar;
import general.procedure.functional.SimpleProcedure;
import general.procedure.ProcedureGroup;
import maps.layout.occupant.character.Spritesheet.AnimationState;
import general.utils.helpers.GameUtils;
import general.visual.DeserializedParticleEffect;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import maps.data.MapTextures;
import maps.layout.MapCoords;
import maps.layout.tile.Tile;
import maps.layout.tile.TileFoundation;
import maps.layout.tile.move.Path;

/**
 *
 * @author night
 */
public class UnitVisuals {
    protected final Node node;
    
    private final GeometricBody<Quad> spriteBody;
    private final GeometricBody<Quad> outlineBody;
    
    private ColorRGBA baseOutlineColor;
    
    private final Node hpNode, tpNode;
    private final BasicProgressBar hpBar, tpBar;
    
    private final LinkedList<DeserializedParticleEffect> effectQueue = new LinkedList<>();
    private final ProcedureGroup queue = new ProcedureGroup();
    private final AssetManager assetManager;
    
    private final Spritesheet spritesheetInfo;
    private AnimationState animState = AnimationState.Idle;
    
    public UnitVisuals(String name, String jobClassName, ColorRGBA baseOutlineColor, AssetManager assetManager) {
        this.baseOutlineColor = baseOutlineColor;
        this.assetManager = assetManager;
        
        node = new Node(name + ": visual node");
        
        hpNode = new Node(name + ": HP Bar");
        tpNode = new Node(name + ": TP Bar");
        
        //deserialize spritesheets
        spritesheetInfo = deserializeSpritesheet(name, jobClassName);
        String folderPath = "Sprites/Map/" + jobClassName + "/" + spritesheetInfo.getFolderName() + "/";
        
        //create rotation
        Quaternion rotation = new Quaternion();
        rotation.fromAngles((FastMath.PI / -3f), (FastMath.PI / -2f), 0);
        
        float size = (25f / 16f) * Tile.LENGTH;
        
        //initialize sprite
        Quad spriteQuad = new Quad(size, size);
        Geometry sprite = new Geometry(name + " map sprite Geometry", spriteQuad);
        Material spriteMat = new Material(assetManager, "MatDefs/Spritesheet.j3md");
        Texture tex = assetManager.loadTexture(folderPath + spritesheetInfo.getSheet());
        
        initializeSprite(sprite, spriteMat, tex, rotation);
        spriteBody = new GeometricBody<>(sprite, spriteQuad, spriteMat);
        
        //initialize outline
        Quad outlineQuad = new Quad(size, size);
        Geometry outline = new Geometry("outline", outlineQuad);
        Material outlineMat = new Material(assetManager, "MatDefs/Spritesheet.j3md");
        Texture outlineTexture = assetManager.loadTexture(folderPath + spritesheetInfo.getOutlineSheet());
        
        initializeSprite(outline, outlineMat, outlineTexture, rotation);
        outlineBody = new GeometricBody<>(outline, outlineQuad, outlineMat);
        
        //create bars
        float width = Tile.LENGTH / 1.675f;
        Vector2f barDims = new Vector2f(width, width * (217f / 1045f));
        hpBar = new BasicProgressBar(barDims, MapTextures.GUI.HealthBar, false, GameUtils.HP_COLOR_GREEN, assetManager);
        tpBar = new BasicProgressBar(Vector2F.invert(barDims).multLocal(new Vector2f(1, 0.75f)), MapTextures.GUI.VerticalProgressBar, true, GameUtils.TP_COLOR_PINK, assetManager);
        
        //hpBar.setTextureRange(0.1617f, 0.977f);
        //tpBar.setTextureRange(0.0f, 1.0f);
        
        //initialize and manipulate positions of the bars
        float halfTile = Tile.LENGTH / 2f;
        float tpDeltaXY = Tile.LENGTH * (5f / 8f);
        hpBar.getBarNode().move(Tile.LENGTH / -9f, halfTile, halfTile);
        tpBar.getBarNode().move(tpDeltaXY / 2f, 0, tpDeltaXY * 2);
        
        hpBar.getBarNode().setLocalRotation(rotation);
        tpBar.getBarNode().setLocalRotation(rotation);
        
        hpNode.attachChild(hpBar.getBarNode());
        tpNode.attachChild(tpBar.getBarNode());
        
        //set layers
        LayerComparator.setLayer(spriteBody.getGeometry(), 6);  //middle 
        LayerComparator.setLayer(outlineBody.getGeometry(), 5); //bottom
        LayerComparator.setLayer(hpNode, 7); //top
        LayerComparator.setLayer(tpNode, 7); //top
        
        //attach to node
        node.attachChild(spriteBody.getGeometry());
        node.attachChild(outlineBody.getGeometry());
    }
    
    private void initializeSprite(Geometry sprite, Material spriteMat, Texture tex, Quaternion rotation) {
        tex.setMagFilter(MagFilter.Nearest);
        
        spriteMat.setTexture("ColorMap", tex);
        spriteMat.setFloat("SizeX", spritesheetInfo.getMaxColumnCount());
        spriteMat.setFloat("SizeY", spritesheetInfo.getRowCount());
        spriteMat.setFloat("Position", 0f);
        spriteMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        spriteMat.getAdditionalRenderState().setDepthWrite(false);
        sprite.setQueueBucket(Bucket.Transparent);
        sprite.setLocalRotation(rotation);
    } 
    
    public Node getNode() { return node; }
    
    public GeometricBody<Quad> getSpriteBody() { return spriteBody; }
    public GeometricBody<Quad> getOutlineBody() { return outlineBody; }
    
    public Node getHPNode() { return hpNode; }
    public Node getTPNode() { return tpNode; }
    
    public Spritesheet getSpritesheetInfo() { return spritesheetInfo; }
    public AnimationState getAnimationState() { return animState; }
    
    public int getEffectQueueSize() { return effectQueue.size(); }
    
    public void addToEffectQueue(DeserializedParticleEffect particleEffect) {
        particleEffect.onEffectStart((tpf) -> {
            Quaternion rotation = new Quaternion();
            rotation.fromAngles((FastMath.PI / -3f), (FastMath.PI / -2f), 0);
            particleEffect.getModelRootNode().setLocalRotation(rotation);
                
            node.attachChild(particleEffect.getModelRootNode());
        });
        
        particleEffect.onEffectFinish((tpf) -> {
            node.detachChild(particleEffect.getModelRootNode());
            effectQueue.removeFirst();
        });
        
        effectQueue.add(particleEffect);
    }
    
    public void addToQueue(SimpleProcedure procedure) {
        queue.add(procedure);
    }
    
    public void detachBars() {
        hpBar.getBarNode().removeFromParent();
        tpBar.getBarNode().removeFromParent();
    }
    
    public void setBaseOutlineColor(ColorRGBA base) {
        baseOutlineColor = base;
    }
    
    public void setAnimationState(AnimationState state) {
        animState = state;
    }
    
    public boolean hasExtraIdle() { // has 6th row
        return spritesheetInfo.hasAnimation(AnimationState.Idle2);
    }
    
    public void setIdealIdle() {
        animState = hasExtraIdle() ? AnimationState.Idle2 : AnimationState.Idle;
    }
    
    private int calculateSpritesheetPosition() { //value is row
        return (spritesheetInfo.getStartingPosition(animState) + (((int)(Main.GameFlow.getFrame() * 0.1)) % spritesheetInfo.getColumnCount(animState)));
    }
    
    private void updateOutline() { //pass in allegiance.getAssociatedColor()
        float outlineGradient = 0.075f * FastMath.cos(0.08f * Main.GameFlow.getFrame()) + 0.925f;
        outlineBody.getMaterial().setFloat("Position", calculateSpritesheetPosition());
        outlineBody.getMaterial().setColor("Color", baseOutlineColor.mult(outlineGradient));
    }
    
    protected void updateHP(float currrentToMaxHPratio) {
        hpBar.setPercent(currrentToMaxHPratio);
    }
    
    protected void updateTP(float currentToMaxTPratio) {
        tpBar.setPercent(currentToMaxTPratio);
    }
    
    protected void updateSprite() {
        spriteBody.getMaterial().setFloat("Position", calculateSpritesheetPosition());
        updateOutline();
    }
    
    protected void updateEffects(float tpf) {
        effectQueue.getFirst().update(tpf);
    }
    
    protected void update(float tpf, Camera cam) {
        //update queue
        queue.update(tpf);
        
        //update hp and tp bar positions
        rotateNodeWithCamera(hpNode, cam);
        rotateNodeWithCamera(tpNode, cam);
    }
    
    private void rotateNodeWithCamera(Node point, Camera cam) { //rotates health bars
        float constant = 0.2f; //0.25078946f
        
        Quaternion rot = new Quaternion();
        float A = point.getWorldTranslation().x - cam.getLocation().x;
        float B = point.getWorldTranslation().z - cam.getLocation().z + 15;
        float deltaTheta = FastMath.atan(B / A) / -2.5f;
        
        if (deltaTheta < 0) { constant = 0.05f; }
        
        if (FastMath.abs(deltaTheta) > constant) {
            float sign = deltaTheta / (FastMath.abs(deltaTheta));
            deltaTheta = constant * sign;
        }
      
        rot.fromAngles(0, deltaTheta, 0);
        point.setLocalRotation(rot);
    }
    
    public Movement createMovement(MapCoords start, MapCoords end, int moveCapacity) {
        return new Movement(
            new Path(start, end, moveCapacity),
            (deltaXY, deltaPosition) -> {
                animState = AnimationState.directionalValueOf(deltaXY);
                node.move(deltaPosition);
            }
        );
    }
    
    public Movement birthMovement(MapCoords[] tilePath, MapCoords startPos) {
        return new Movement(
            startPos,
            tilePath, 
            (deltaXY, deltaPosition) -> {
                animState = AnimationState.directionalValueOf(deltaXY);
                node.move(deltaPosition);
            }
        );
    }
    
    public static Spritesheet deserializeSpritesheet(String name, String jobClassName) {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\Sprites\\Map\\" + jobClassName + "\\" + name + "\\config.json"));
            
            return gson.fromJson(reader, Spritesheet.class).setFolderName(name).setAnimations();
        }
        catch (IOException ex) {
            return deserializeGenericSpritesheet(jobClassName); //will use generic if name not found in folders
        }
    }
    
    public static Spritesheet deserializeGenericSpritesheet(String jobClassName) {
        String folderName = "generic";
        
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\Sprites\\Map\\" + jobClassName + "\\" + folderName + "\\config.json"));
            return gson.fromJson(reader, Spritesheet.class).setFolderName(folderName).setAnimations();
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
