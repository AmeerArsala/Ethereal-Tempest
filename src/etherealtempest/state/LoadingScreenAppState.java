/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.state;

import com.atr.jme.font.util.StringContainer.Align;
import com.atr.jme.font.util.StringContainer.VAlign;
import com.atr.jme.font.util.StringContainer.WrapMode;
import com.atr.jme.font.util.Style;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.simsilica.lemur.LayerComparator;
import enginetools.AssetLoadTask;
import enginetools.MaterialCreator;
import enginetools.math.SpatialOperator;
import etherealtempest.Globals;
import etherealtempest.geometry.GeometricBody;
import etherealtempest.gui.broad.BasicProgressBar;
import etherealtempest.gui.broad.BasicProgressBar2D;
import general.math.FloatPair;
import general.math.function.MathFunction;
import general.math.function.ParametricFunction;
import general.math.function.ParametricFunction3f;
import general.math.function.RGBAFunction;
import general.math.function.RandomizedPiecewiseFunction;
import general.procedure.functional.NamedExecution;
import general.ui.text.FontProperties;
import general.ui.text.FontProperties.KeyType;
import general.ui.text.Text2D;
import general.ui.text.TextProperties;
import general.visual.animation.Animation;
import general.visual.animation.VisualTransition;
import general.visual.animation.VisualTransition.Progress;

/**
 *
 * @author night
 */
public abstract class LoadingScreenAppState extends BaseAppState {
    public static final String BAR_TEXTURE_PATH = "Interface/GUI/common/smoothBar.png";
    public static final int BAR_TEXTURE_WIDTH_PX = 1044, BAR_TEXTURE_HEIGHT_PX = 217;
    
    private static final FloatPair RGB_DOMAIN = new FloatPair(0f, Float.POSITIVE_INFINITY); // Domain: [0f, infinity)
    private static final FloatPair RGB_RANGE = new FloatPair(0f, 1f);                       // Range: [0f, 1f] 
    public static final RandomizedPiecewiseFunction RANDOM_R = new RandomizedPiecewiseFunction(RGB_DOMAIN, RGB_RANGE, MathFunction.CONSTANT(1f), false); //1-second partitions
    public static final RandomizedPiecewiseFunction RANDOM_G = new RandomizedPiecewiseFunction(RGB_DOMAIN, RGB_RANGE, MathFunction.CONSTANT(1f), false); //1-second partitions
    public static final RandomizedPiecewiseFunction RANDOM_B = new RandomizedPiecewiseFunction(RGB_DOMAIN, RGB_RANGE, MathFunction.CONSTANT(1f), false); //1-second partitions
    public static final ParametricFunction3f RANDOM_RGB = new ParametricFunction3f(RANDOM_R, RANDOM_G, RANDOM_B);
    
    static {
        RANDOM_RGB.setInstanceGenType(ParametricFunction.FRESH);
    }
    
    public static final RGBAFunction RANDOM_RGBA = new RGBAFunction(RANDOM_RGB, MathFunction.CONSTANT(1f));
    
    static {
        RANDOM_RGBA.setInstanceGenType(ParametricFunction.FRESH);
    }
    
    public static final class VisualParams {
        private final Material background;
        private final ColorRGBA barColor;
        private final float barWidthPercent;
        private final boolean useRandomColor;
        
        public VisualParams(Material background, ColorRGBA barColor, float barWidthPercent, boolean useRandomColor) {
            this.background = background;
            this.barColor = barColor;
            this.barWidthPercent = barWidthPercent;
            this.useRandomColor = useRandomColor;
        }
        
        public boolean useRandomColor() {
            return useRandomColor;
        }
        
        public GeometricBody<Quad> makeScreenBackground() {
            Quad quad = new Quad(Globals.getScreenWidth(), Globals.getScreenHeight());
            Geometry geom = new Geometry("loading screen background geometry", quad);
            
            return new GeometricBody<>(geom, quad, background);
        }
        
        public BasicProgressBar2D makeLoadingBar(AssetManager assetManager, int processCount) {
            float barWidth = barWidthPercent * Globals.getScreenWidth();
            float barHeight = (((float)BAR_TEXTURE_HEIGHT_PX) / BAR_TEXTURE_WIDTH_PX) * barWidth;
            
            Texture barTexture = assetManager.loadTexture(BAR_TEXTURE_PATH);
            BasicProgressBar2D loadingProgressBar = new BasicProgressBar2D(new Vector2f(barWidth, barHeight), barTexture, barColor, 0.0f, processCount, assetManager);
            loadingProgressBar.setQueueBucket(RenderQueue.Bucket.Gui);
            loadingProgressBar.setBackgroundColor(ColorRGBA.BlackNoAlpha); //bg is transparent
            loadingProgressBar.setOnlyChangeColor(ColorRGBA.White);
            loadingProgressBar.setColor(barColor);
            loadingProgressBar.setTextureRange(new Vector2f(27f / BAR_TEXTURE_WIDTH_PX, 28f / BAR_TEXTURE_HEIGHT_PX), new Vector2f(1015f / BAR_TEXTURE_WIDTH_PX, 192f / BAR_TEXTURE_HEIGHT_PX));
            
            return loadingProgressBar;
        }
        
        public Text2D makeText(AssetManager assetManager) {
            float widthPercent = barWidthPercent;
            float heightPercent = widthPercent * 0.2f;
            
            float height = heightPercent * Globals.getScreenHeight();
            Rectangle rect = new Rectangle(0, 0, widthPercent * Globals.getScreenWidth(), height);
            int kerning = 3;
            
            float fontSize = 0.25f * (height * FastMath.sqrt(2)); //pythagorean triangle of 1-1-sqrt(2)
            
            Text2D text2D = new Text2D(
                "Loading...",
                ColorRGBA.White,
                new TextProperties(rect, kerning, WrapMode.Word, Align.Left, VAlign.Top),
                new FontProperties("Interface/Fonts/courier new light.ttf", KeyType.BMP, Style.Plain, fontSize),
                assetManager
            );
            
            text2D.fitInTextBox(2f);
            return text2D;
        }
    }
    
    private AppStateManager stateManager;
    private Node guiNode;
    private VisualTransition transitionOut;
    private int index = 0; //current index
    
    private final AssetManager assetManager;
    private final NamedExecution[] processes;
    
    private final GeometricBody<Quad> screenBackground;
    private final BasicProgressBar2D loadingBar;
    private final Text2D text;
    private final boolean useRandomColor;
    
    public LoadingScreenAppState(AssetManager assetManager, NamedExecution[] processes, VisualParams visualParams) {
        this.assetManager = assetManager;
        this.processes = processes;
        
        screenBackground = visualParams.makeScreenBackground();
        loadingBar = visualParams.makeLoadingBar(assetManager, processes.length);
        text = visualParams.makeText(assetManager);
        useRandomColor = visualParams.useRandomColor();
    }
    
    public LoadingScreenAppState(AssetManager assetManager, NamedExecution[] processes, float barWidthPercent, ColorRGBA barColor, ColorRGBA backgroundColor, boolean useRandomColor) {
        this(assetManager, processes, new VisualParams(flatColor(assetManager, backgroundColor), barColor, barWidthPercent, useRandomColor));
    }
    
    public LoadingScreenAppState(AssetManager assetManager, NamedExecution[] processes, float barWidthPercent, ColorRGBA barColor, boolean useRandomBGColorSeed) {
        this(assetManager, processes, new VisualParams(julieMaterial(assetManager, null), barColor, barWidthPercent, useRandomBGColorSeed));
    }
    
    public LoadingScreenAppState(AssetManager assetManager, NamedExecution[] processes, Texture screenTexture, float barWidthPercent, ColorRGBA barColor, boolean useRandomBGColorSeed) {
        this(assetManager, processes, new VisualParams(julieMaterial(assetManager, screenTexture), barColor, barWidthPercent, useRandomBGColorSeed));
    }
    
    private static Material flatColor(AssetManager assetManager, ColorRGBA color) {
        Material mat = new Material(assetManager, MaterialCreator.UNSHADED);
        mat.setColor("Color", color);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha); //maybe change this later
        
        return mat;
    }
    
    private static Material julieMaterial(AssetManager assetManager, Texture texture) {
        Material mat = new Material(assetManager, "MatDefs/custom/Julie.j3md");
        mat.setColor("Color", Globals.superRandomColor2());
        
        if (texture != null) {
            mat.setTexture("ColorMap", texture);
        }
        
        return mat;
    }
    
    public Node getGuiNode() {
        return guiNode;
    }
    
    public GeometricBody<Quad> getScreenBackground() {
        return screenBackground;
    }

    public BasicProgressBar2D getLoadingBar() {
        return loadingBar;
    }
    
    public float getProgressPercent() {
        return ((float)index) / processes.length;
    }
    
    protected abstract void onFinish(AppStateManager stateManager);

    @Override
    protected void initialize(Application aplctn) {
        stateManager = aplctn.getStateManager();
        guiNode = ((SimpleApplication)aplctn).getGuiNode();
        
        guiNode.attachChild(screenBackground.getGeometry());
        guiNode.attachChild(loadingBar.getGeometryPanel());
        guiNode.attachChild(text);
        
        LayerComparator.setLayer(screenBackground.getGeometry(), 0);
        LayerComparator.setLayer(loadingBar.getGeometryPanel(), 1);
        LayerComparator.setLayer(text, 2);
        
        //center the loadingBar in the middle of the screen
        loadingBar.getAnchor().getPointInPercents().set(0.5f, 0.5f, 0);
        loadingBar.getAnchor().alignToLocally(Globals.getScreenDimensions().multLocal(0.5f));
        
        SpatialOperator textAnchor = text.createSpatialOperator(0, 1.5f);
        textAnchor.alignToLocally(loadingBar.getGeometryPanel().getLocalTranslation());
        
        //create outward transition
        transitionOut = new VisualTransition(screenBackground.getGeometry(),
            Animation.CleanOpacityShift((Geometry geom, ColorRGBA color) -> {
                geom.getMaterial().setColor("Color", color);
                loadingBar.setColor(color);
                text.setTextColor(color);
            }).setInitialAndEndVals(1f, 0f).setLength(1f)
        );
        
        transitionOut.onFinishTransitions(() -> {
            guiNode.detachChild(text);
            guiNode.detachChild(loadingBar.getGeometryPanel());
            guiNode.detachChild(screenBackground.getGeometry());
        });
        
        transitionOut.beginTransitions();
    }

    @Override
    protected void cleanup(Application aplctn) {
        //TODO: do something here
    }

    @Override
    protected void onEnable() {
        //TODO: do something here
    }

    @Override
    protected void onDisable() {
        //TODO: do something here
    }
    
    @Override
    public void update(float tpf) {
        super.update(tpf);
        
        if (useRandomColor) {
            screenBackground.getMaterial().setColor("Color", RANDOM_RGBA.rgba(Globals.time()));
        }
        
        tick(tpf);
    }
    
    private void tick(float tpf) {
        if (index >= processes.length) {
            transitionOut.update(tpf);
            
            if (transitionOut.getTransitionProgress() == Progress.Finished) {
                onFinish(stateManager);
                stateManager.detach(LoadingScreenAppState.this);
            }
        } else {
            String taskName = processes[index].getName();
            text.setText(taskName);
            System.out.println(taskName);
            
            processes[index++].execute();
            loadingBar.removeAllProceduresFromGroup();
            loadingBar.setPercent(getProgressPercent());
        }
    }
}