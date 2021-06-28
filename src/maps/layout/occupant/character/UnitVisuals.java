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
import etherealtempest.Main;
import etherealtempest.geometry.GeometricBody;
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
import java.util.List;
import maps.layout.Coords;
import maps.layout.MapLevel;
import maps.layout.MapCoords;
import maps.layout.tile.move.Path;
import maps.layout.tile.Tile;

/**
 *
 * @author night
 */
public class UnitVisuals {
    protected final Node node;
    
    protected float MOVE_SPEED = 20f;
    
    private final GeometricBody<Quad> spriteBody;
    private final GeometricBody<Quad> outlineBody;
    
    private ColorRGBA baseOutlineColor;
    
    private final Node hpNode, tpNode;
    private final ProgressBar hpBar, tpBar;
    
    private final LinkedList<DeserializedParticleEffect> effectQueue = new LinkedList<>();
    private final ProcedureGroup queue = new ProcedureGroup();
    
    private final Spritesheet spritesheetInfo;
    private AnimationState animState = AnimationState.Idle;
    
    public UnitVisuals(String name, String jobClassName, ColorRGBA baseOutlineColor, AssetManager assetManager) {
        this.baseOutlineColor = baseOutlineColor;
        
        node = new Node(name + ": visual node");
        
        hpNode = new Node(name + ": HP Bar");
        tpNode = new Node(name + ": TP Bar");
        
        //deserialize spritesheets
        spritesheetInfo = deserializeSpritesheet(name, jobClassName);
        String folderPath = "Sprites/Map/" + jobClassName + "/" + spritesheetInfo.getFolderName() + "/";
        
        //create rotation
        Quaternion rotation = new Quaternion();
        rotation.fromAngles((FastMath.PI / -3f), (FastMath.PI / -2f), 0);
        
        //initialize sprite
        Quad spriteQuad = new Quad(25f, 25f);
        Geometry sprite = new Geometry(name + " map sprite Geometry", spriteQuad);
        Material spriteMat = new Material(assetManager, "MatDefs/Spritesheet.j3md");
        Texture tex = assetManager.loadTexture(folderPath + spritesheetInfo.getSheet());
        
        initializeSprite(sprite, spriteMat, tex, rotation);
        spriteBody = new GeometricBody<>(sprite, spriteQuad, spriteMat);
        
        //initialize outline
        Quad outlineQuad = new Quad(25f, 25f);
        Geometry outline = new Geometry("outline", outlineQuad);
        Material outlineMat = new Material(assetManager, "MatDefs/Spritesheet.j3md");
        Texture outlineTexture = assetManager.loadTexture(folderPath + spritesheetInfo.getOutlineSheet());
        
        initializeSprite(outline, outlineMat, outlineTexture, rotation);
        outlineBody = new GeometricBody<>(outline, outlineQuad, outlineMat);
        outlineBody.getGeometry().move(0, -0.1f, 0);
        
        //create bars
        hpBar = new ProgressBar();
        tpBar = new ProgressBar();
        
        ((QuadBackgroundComponent)hpBar.getValueIndicator().getBackground()).setColor(GameUtils.HP_COLOR_GREEN);
        ((QuadBackgroundComponent)tpBar.getValueIndicator().getBackground()).setColor(GameUtils.TP_COLOR_PINK);
        hpBar.getValueIndicator().setAlpha(1f);
        tpBar.getValueIndicator().setAlpha(1f);
        
        ((TbtQuadBackgroundComponent)hpBar.getBackground()).setColor(ColorRGBA.Black);
        ((TbtQuadBackgroundComponent)tpBar.getBackground()).setColor(ColorRGBA.Black);
        
        hpBar.setInsets(new Insets3f(0.5f, 0.5f, 0.5f, 0.5f));
        tpBar.setInsets(new Insets3f(0.5f, 0.5f, 0.5f, 0.5f));
        
        //using these to initialize and manipulate positions of the bars
        Node hpMidNode = new Node(), tpMidNode = new Node();
        
        hpMidNode.attachChild(hpBar);
        tpMidNode.attachChild(tpBar);
        
        hpMidNode.scale(0.3f);
        tpMidNode.scale(0.3f);
        
        hpMidNode.move(0, 8f, 8f);
        tpMidNode.move(10f, 10, 20f);
        
        tpMidNode.setLocalRotation(rotation);
        
        Quaternion hpRotation = new Quaternion();
        hpRotation.fromAngles((FastMath.PI / -2f), 0, FastMath.PI / 4f); //FastMath.PI / 2f
        hpMidNode.setLocalRotation(hpRotation);
        
        hpNode.attachChild(hpMidNode);
        tpNode.attachChild(tpMidNode);
        
        //set layers
        LayerComparator.setLayer(spriteBody.getGeometry(), 5); //middle 
        LayerComparator.setLayer(outlineBody.getGeometry(), 4); //bottom
        LayerComparator.setLayer(hpNode, 6); //top
        LayerComparator.setLayer(tpNode, 6); //top
        
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
    
    private Spritesheet deserializeSpritesheet(String name, String jobClassName) {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\Sprites\\Map\\" + jobClassName + "\\" + name + "\\config.json"));
            
            return gson.fromJson(reader, Spritesheet.class).setFolderName(name).setAnimations();
        }
        catch (IOException ex) {
            return attemptGenericDeserialization(jobClassName); //will use generic if name not found in folders
        }
    }
    
    private Spritesheet attemptGenericDeserialization(String jobClassName) {
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
        hpBar.removeFromParent();
        tpBar.removeFromParent();
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
        return (spritesheetInfo.startingPositions.get(animState) + (((int)(Main.GameFlow.getFrame() * 0.1)) % spritesheetInfo.getColumnCount(animState)));
    }
    
    private void updateOutline() { //pass in allegiance.getAssociatedColor()
        float outlineGradient = 0.075f * FastMath.cos(0.08f * Main.GameFlow.getFrame()) + 0.925f;
        outlineBody.getMaterial().setFloat("Position", calculateSpritesheetPosition());
        outlineBody.getMaterial().setColor("Color", baseOutlineColor.mult(outlineGradient));
    }
    
    protected void updateHP(float currrentToMaxHPratio) {
        hpBar.setProgressPercent(currrentToMaxHPratio);
    }
    
    protected void updateTP(float currentToMaxTPratio) {
        tpBar.setProgressPercent(currentToMaxTPratio);
    }
    
    protected void updateSprite() {
        spriteBody.getMaterial().setFloat("Position", calculateSpritesheetPosition());
        updateOutline();
    }
    
    protected void updatePathProgress(float tpf, float accumulatedMovTime, Path pathway) {
        //distanceperframe must be a power of 2 and <= 16
        //this would come out to = 160 * tpf, or 160 distance per second
        //MOVE_SPEED * Tile.LENGTH MUST = A MULTIPLE OF 40
        float distanceperframe = MOVE_SPEED * Tile.LENGTH * tpf;
        float accumulatedDistance = MOVE_SPEED * accumulatedMovTime * Tile.LENGTH;
        
        int i = (int)(accumulatedDistance / Tile.LENGTH); //tilesTraversed
        List<Tile> path = pathway.getPath();
        
        Coords last = i > 0 ? path.get(i - 1).getPos().getCoords() : pathway.getInitialPos().getCoords();
            
        //these will always be 1, 0, or -1
        Coords deltaXY = path.get(i).getPos().getCoords().subtract(last);
        
        animState = AnimationState.directionalValueOf(deltaXY);
        node.move(deltaXY.toVector3fZX().multLocal(distanceperframe));
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
}
