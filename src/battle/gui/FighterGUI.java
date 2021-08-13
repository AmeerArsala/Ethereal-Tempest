/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.gui;

import etherealtempest.gui.specific.LevelUpPanel;
import etherealtempest.gui.broad.ShapeIndicator;
import etherealtempest.gui.broad.ExpIndicator;
import battle.data.forecast.SingularForecast;
import battle.data.participant.Combatant;
import battle.data.participant.Combatant.AttackType;
import com.atr.jme.font.util.StringContainer.Align;
import com.atr.jme.font.util.StringContainer.VAlign;
import com.atr.jme.font.util.StringContainer.WrapMode;
import com.atr.jme.font.util.Style;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.simsilica.lemur.LayerComparator;
import enginetools.math.SpatialOperator;
import enginetools.math.Vector3F;
import etherealtempest.Globals;
import fundamental.stats.BaseStat;
import general.math.function.MathFunction;
import general.math.function.ParametricFunction4f;
import general.math.function.RGBAFunction;
import general.ui.GeometryPanel;
import general.ui.text.Text2D;
import general.ui.text.quickparams.TextDisplacementParams;
import general.ui.text.quickparams.UIFontParams;
import general.utils.wrapper.Duo;
import general.utils.helpers.GameUtils;
import general.utils.helpers.MathUtils;
import general.visual.animation.Animation;
import general.visual.animation.VisualTransition;
import maps.data.MapTextures;
import general.utils.functional.ToFloatFunction;
import java.util.function.Consumer;

/**
 *
 * @author night
 * 
 */
public class FighterGUI {
    private static final float MIN_BATTLE_PANEL_WIDTH = (591f / 559f) * 322;
    public static final RGBAFunction HEART_COLOR_FUNCTION = new RGBAFunction(new ParametricFunction4f(
        new MathFunction() { // R
            @Override
            protected float f(float hpPercent) {
                if (hpPercent > 0.5f) {
                    return 1f;
                }
                    
                float rgbaValue = MathUtils.pointSlopeForm(
                    hpPercent,   // x (input)
                    0.5f, 0.25f, // x1, x2
                    255f, 128f   // y1, y2
                );
                
                return rgbaValue / 255f;
            }
        },
        new MathFunction() { // G
            @Override
            protected float f(float hpPercent) {
                float rgbaValue = MathUtils.pointSlopeForm(
                    hpPercent,   // x (input)
                    1f, 0.5f,    // x1, x2
                    0f, 221f     // y1, y2
                );
                
                if (hpPercent > 0.5f) {
                    return rgbaValue / 255f;
                }
                
                float rgbaValue2 = MathUtils.pointSlopeForm(
                    hpPercent,   // x (input)
                    0.5f, 0.25f, // x1, x2
                    221f, 0f     // y1, y2
                );
                
                return rgbaValue2 / 255f;
            }
        },
        new MathFunction() { // B
            @Override
            protected float f(float hpPercent) {
                if (hpPercent > 0.5f) {
                    return 0f;
                }
                
                float rgbaValue = MathUtils.pointSlopeForm(
                    hpPercent,   // x (input)
                    0.5f, 0.25f, // x1, x2
                    255f, 0f     // y1, y2
                );
                
                return rgbaValue / 255f;
            }
        },
        new MathFunction() { // A
            @Override
            protected float f(float hpPercent) {
                return 1f;
            }
        }
    ));
    
    private final Node uiNode = new Node();
    private final boolean mirrorUI;
    
    private final SingularForecast forecast;
    private final AssetManager assetManager;
    private final Camera cam;
    
    private final GeometryPanel portrait, nametag, portraitFrame;
    private final GeometryPanel tool, forecastInfo;
    private final ShapeIndicator hpHeart, tpBall;
    private final ExpIndicator expbar;
    
    private LevelUpPanel levelUpPanel;
    
    public FighterGUI(SingularForecast forecast, AssetManager assetManager, Camera cam, boolean mirrorUI) {
        this.forecast = forecast;
        this.cam = cam;
        this.assetManager = assetManager;
        this.mirrorUI = mirrorUI;
        
        final float SCREEN_WIDTH = Globals.getScreenWidth();
        final float SCREEN_HEIGHT = Globals.getScreenHeight();
        
        Vector2f hpHeartDims = new Vector2f(1, 600f / 582f).multLocal(0.1f * SCREEN_WIDTH);
        Vector2f tpBallDims = new Vector2f(1, 581f / 429f).multLocal(0.05f * SCREEN_WIDTH);
        float portraitDims = 0.16f * SCREEN_WIDTH;
        float outerRadius = 0.075f * SCREEN_WIDTH;
        
        hpHeart = GuiFactory.createIndicator(
            "HP",
            hpHeartDims,
            new UIFontParams("Interface/Fonts/DIOGENES.ttf", 23f, Style.Plain, 3),
            heartMatParams(),
            assetManager,
            forecast.getCombatant().getBaseStat(BaseStat.CurrentHP),
            forecast.getCombatant().getBaseStat(BaseStat.MaxHP)
        );
        
        hpHeart.alignTextTo(0.5f, 0.6f);
        hpHeart.onPercentUpdate((mat) -> {
            mat.setColor("Color", HEART_COLOR_FUNCTION.rgba(forecast.getCombatant().getCurrentToMaxHPRatio()));
        });
        
        tpBall = GuiFactory.createIndicator(
            "TP",
            tpBallDims,
            new UIFontParams("Interface/Fonts/DIOGENES.ttf", 23f, Style.Plain, 3),
            tpBallMatParams(),
            assetManager,
            forecast.getCombatant().getBaseStat(BaseStat.CurrentTP),
            forecast.getCombatant().getBaseStat(BaseStat.MaxTP)
        );
        
        expbar = GuiFactory.createExpBar(
            new UIFontParams("Interface/Fonts/Neuton-Regular.ttf", 45f, Style.Plain, 3), 
            new ColorRGBA(0.012f, 0.58f, 0.988f, 1f), //bar color = blue
            ColorRGBA.White, //textColor = white
            outerRadius,
            forecast.getCombatant().getUnit().currentEXP,
            100, //max exp
            assetManager
        );
        
        portrait = GuiFactory.createPanel(
            portraitDims, portraitDims, 
            assetManager, 
            forecast.getCombatant().getUnit().getUnitInfo().getPortraitTexture(),
            ColorRGBA.White, 
            false //don't mirror portrait texture
        );
        
        nametag = GuiFactory.createNametag(
            forecast.getCombatant().getUnit().getName(), 
            new UIFontParams("Interface/Fonts/IMFellDWPica-Regular.ttf", 35f, Style.Plain, 3), 
            assetManager
        );
        
        UIFontParams equippedToolFontParams = new UIFontParams("Interface/Fonts/Linux Libertine/LinLibertine_R.ttf", 18f, Style.Plain, 3);
        UIFontParams forecastInfoFontParams = new UIFontParams("Interface/Fonts/Linux Libertine/LinLibertine_R.ttf", 23f, Style.Plain, 3);
        Duo<GeometryPanel, GeometryPanel> equippedToolAndForecastInfo = createEquippedToolAndForecastInfoPanels(equippedToolFontParams, forecastInfoFontParams);
        
        tool = equippedToolAndForecastInfo.first;
        forecastInfo = equippedToolAndForecastInfo.second;
        
        Vector3f portraitTranslation = new Vector3f(0, SCREEN_HEIGHT - portraitDims, 1f);
        Vector3f nametagTranslation = new Vector3f((portrait.getWidth() - nametag.getWidth()) / 2f, portraitTranslation.y - (nametag.getHeight() / 2f), 1.5f);
        Vector3f forecastInfoTranslation = new Vector3f(0.0125f * SCREEN_WIDTH, 0.0125f * SCREEN_HEIGHT, 0f);
        Vector3f toolTranslation = forecastInfoTranslation.mult(new Vector3f(1f, (10f / 1080) * SCREEN_HEIGHT, 1f));
        Vector3f hpHeartTranslation = new Vector3f(forecastInfoTranslation.x, SCREEN_HEIGHT - forecastInfoTranslation.y - hpHeartDims.y, 0).mult(new Vector3f(2f, 0.925f, 1f)).addLocal((MIN_BATTLE_PANEL_WIDTH / 1920) * SCREEN_WIDTH, 0, 0);
        Vector3f tpBallTranslation = new Vector3f(hpHeartTranslation.x, SCREEN_HEIGHT - hpHeartTranslation.y, 0).multLocal(1.15f, 0.15f, 1f);
        Vector3f expBarTranslation = new Vector3f(nametagTranslation.x, nametagTranslation.y + ((-1 * outerRadius) - (0.25f * nametag.getHeight())), 2f);
        if (!mirrorUI) {
            expBarTranslation.addLocal(outerRadius, 0, 0);
        }
        
        setLocalTranslationOf(portrait, portraitTranslation, portraitDims);
        setLocalTranslationOf(nametag, nametagTranslation, nametag.getWidth());
        setLocalTranslationOf(tool, toolTranslation, tool.getWidth());
        setLocalTranslationOf(forecastInfo, forecastInfoTranslation, forecastInfo.getWidth());
        setLocalTranslationOf(hpHeart.getNode(), hpHeartTranslation, hpHeartDims.x);
        setLocalTranslationOf(tpBall.getNode(), tpBallTranslation, tpBallDims.x);
        setLocalTranslationOf(expbar.getExpCircle(), expBarTranslation, outerRadius);
        
        float frameWidth = portraitDims * 2.25f;
        float frameHeight = (460f / 965f) * frameWidth;
        
        portraitFrame = GuiFactory.createPanel(
            frameWidth, frameHeight,
            assetManager,
            MapTextures.GUI.Fighter.FighterBorder,
            forecast.getCombatant().getUnit().getAllegiance().getAssociatedColor(),
            mirrorUI
        );
        
        setLocalTranslationOf(portraitFrame, portraitTranslation.subtract(0.005f * SCREEN_WIDTH, 0.02f * SCREEN_HEIGHT, 1), frameWidth);
        
        LayerComparator.setLayer(portraitFrame, 0);
        LayerComparator.setLayer(portrait, 1);
        LayerComparator.setLayer(nametag, 2);
        LayerComparator.setLayer(tool, 0);
        LayerComparator.setLayer(forecastInfo, 0);
        LayerComparator.setLayer(hpHeart.getNode(), 3);
        LayerComparator.setLayer(tpBall.getNode(), 3);
        LayerComparator.setLayer(expbar.getExpCircle(), 3);
        
        uiNode.attachChild(portraitFrame);
        uiNode.attachChild(portrait);
        uiNode.attachChild(nametag);
        uiNode.attachChild(tool);
        uiNode.attachChild(forecastInfo);
        uiNode.attachChild(hpHeart.getNode());
        uiNode.attachChild(tpBall.getNode());
        //Do not attach expbar.getExpCircle() yet
    }
    
    public Node getNode() { return uiNode; }
    
    public SingularForecast getForecast() { return forecast; }
    public Combatant getCombatant() { return forecast.getCombatant(); }
    public AssetManager getAssetManager() { return assetManager; }
    public Camera getCamera() { return cam; }
    
    public GeometryPanel getPortrait() { return portrait; }
    public GeometryPanel getPortraitFrame() { return portraitFrame; }
    public GeometryPanel getNametag() { return nametag; }
    
    public GeometryPanel getEquippedPanel() { return tool; }
    public GeometryPanel getForecastInfoPanel() { return forecastInfo; }
    
    //public HeartIndicator getHPHeart() { return hpHeart; }
    public ShapeIndicator getHPHeart() { return hpHeart; }
    public ShapeIndicator getTPBall() { return tpBall; }
    
    public ExpIndicator getExpBar() { return expbar; }
    public LevelUpPanel getLevelUpPanel() { return levelUpPanel; }
    
    /**
     * 
     * @param spatial Spatial to translate
     * @param localTranslation the base desired X (no negative values)
     * @param width width of the Spatial
     * @return the desired x value
     */
    private float setLocalTranslationOf(Spatial spatial, Vector3f localTranslation, float width) {
        float desiredX;
        if (mirrorUI) {
            desiredX = Globals.getScreenWidth() - width - localTranslation.x;
            spatial.setLocalTranslation(desiredX, localTranslation.y, localTranslation.z);
        } else {
            desiredX = localTranslation.x;
            spatial.setLocalTranslation(localTranslation);
        }
        
        return desiredX;
    }
    
    public void update(float tpf) {
        hpHeart.update(tpf);
        tpBall.update(tpf);
        
        if (expbar != null) {
            expbar.update(tpf);
            
            if (levelUpPanel != null) {
                levelUpPanel.update(tpf);
            }
        }
    }
    
    public boolean HPandTPdrainsFinished() {
        return hpHeart.isQueueEmpty() && tpBall.isQueueEmpty();
    }
    
    public void gainExp() {
        forecast.calculateExpToGain(); //sets exp to gain
        if (!uiNode.hasChild(expbar.getExpCircle())) {
            uiNode.attachChild(expbar.getExpCircle());
        }
        
        int previousExpValue = forecast.getCombatant().getUnit().currentEXP;
        int nextExpValue = forecast.getCombatant().addExpGained(); //add exp gained
        float gainedExpRatio = ((nextExpValue - previousExpValue) / ((float)Combatant.MAX_EXP_VALUE));
        expbar.proceedToValue(nextExpValue, 2.5f * gainedExpRatio, 0.5f);
    }
    
    public void levelUp() {
        if (levelUpPanel == null) {
            levelUpPanel = GuiFactory.createLevelUpPanel(forecast, assetManager);
        }
        
        float translation = 0.05f * Globals.getScreenHeight();
        float initialX = -levelUpPanel.getPanel().getWidth();
        setLocalTranslationOf(levelUpPanel.getNode(), new Vector3f(initialX, translation, 0.5f), levelUpPanel.getPanel().getWidth());
        
        expbar.levelUp(
            0.1f,   // 0.1 seconds for the "LEVEL UP!" text to expand
            () -> { // onFinish
                uiNode.attachChild(levelUpPanel.getNode());
                
                VisualTransition transition = new VisualTransition(
                    levelUpPanel.getNode(),
                    new Animation() {
                        @Override
                        protected void update(float tpf, float Y, Spatial target, Animation anim) {
                            setLocalTranslationOf(target, target.getLocalTranslation().setX(Y), levelUpPanel.getPanel().getWidth());
                        }
                    }.setInitialAndEndVals(initialX, translation)
                );
                
                transition.onFinishTransitions(() -> { //onFinish
                    levelUpPanel.levelUp();
                });
                
                System.err.println("level up!!!");
                expbar.addTransitionToGroup(transition);
            }
        );
    }
    
    private Duo<GeometryPanel, GeometryPanel> createEquippedToolAndForecastInfoPanels(UIFontParams equippedToolParams, UIFontParams forecastInfoParams) {
        final float SCREEN_WIDTH = Globals.getScreenWidth();
        final float SCREEN_HEIGHT = Globals.getScreenHeight();
        final float SCREEN_HYPOTENUSE = MathUtils.hypotenuse(SCREEN_WIDTH, SCREEN_HEIGHT);
        final float HYPOTENUSE_1920x1080 = 2202.90717f;
        
        Combatant participant = forecast.getCombatant();
        
        String equippedName;
        Texture iconTex;
        if (participant.getAttackType() == AttackType.Weapon) {
            equippedName = participant.getUnit().getEquippedWeapon().getName();
            iconTex = participant.getUnit().getEquippedWeapon().getWeaponData().getType().getIconTexture();
        } else { // participant.getAttackType() == AttackType.Formula
            equippedName = participant.getUnit().getEquippedFormula().getName();
            iconTex = participant.getUnit().getEquippedFormula().getActualFormulaData().getType().getIconTexture();
        }
        
        String forecastText = 
            "DMG: " + forecast.getDisplayedDamage() + "\n" +
            "ACC: " + forecast.getDisplayedAccuracy() + "%\n" +
            "CRIT: " + forecast.getDisplayedCrit() + "%";
        
        TextDisplacementParams equippedToolDisplacementParams = new TextDisplacementParams(Align.Left, VAlign.Top, WrapMode.CharClip);
        Text2D equippedToolText2D = GuiFactory.generateText(equippedName, ColorRGBA.White, equippedToolParams, equippedToolDisplacementParams, assetManager);
        
        TextDisplacementParams forecastInfoDisplacementParams = new TextDisplacementParams(Align.Left, VAlign.Top, WrapMode.CharClip);
        Text2D forecastInfoText2D = GuiFactory.generateText(forecastText, ColorRGBA.White, forecastInfoParams, forecastInfoDisplacementParams, assetManager);
        
        Vector2f equippedTextDims = new Vector2f(equippedToolText2D.getTextWidth(), equippedToolText2D.getTextHeight());
        Vector2f forecastTextDims = new Vector2f(forecastInfoText2D.getTextWidth(), forecastInfoText2D.getTextHeight());
        
        final float minWidth = (MIN_BATTLE_PANEL_WIDTH / 1920) * SCREEN_WIDTH;
        
        ToFloatFunction<Vector2f> paddingFunction = (dims) -> {
            final float minPadding = ((2f / 3f) / 7.7f) * SCREEN_HEIGHT; //((2f / 3f) / 13.5f) * SCREEN_WIDTH;
            final float maxPadding = SCREEN_WIDTH / 4f;
            
            if (dims.x >= minWidth) {
                return minPadding;
            } else {
                return ((minWidth - dims.x) > maxPadding ? maxPadding : (minWidth - dims.x));
            }
        };

        float equippedPanelPreferredWidth = equippedTextDims.x + paddingFunction.apply(equippedTextDims);
        float forecastPanelPreferredWidth = forecastTextDims.x + paddingFunction.apply(forecastTextDims);
        float panelWidth = Math.max(equippedPanelPreferredWidth, forecastPanelPreferredWidth);
        
        Vector2f equippedPanelPadding = new Vector2f(Math.abs(panelWidth - equippedTextDims.x), (13.125f / 1080) * SCREEN_HEIGHT);
        Vector2f forecastPanelPadding = new Vector2f(Math.abs(panelWidth - forecastTextDims.x), (20.25f / 1080) * SCREEN_HEIGHT);
        
        float forecastTextGlobalKerning = (forecastInfoParams.kerning / HYPOTENUSE_1920x1080) * SCREEN_HYPOTENUSE;
        Vector2f forecastInfoTextInitialTranslation = new Vector2f(forecastTextGlobalKerning, ((81f / 1080) * SCREEN_HEIGHT) - forecastTextGlobalKerning);
        
        //START equippedTool GeometryPanel initialization
        GeometryPanel equippedToolPanel = GuiFactory.createBattlePanelFromText(equippedToolText2D, equippedPanelPadding, assetManager, forecast.getCombatant().getUnit().getAllegiance().getAssociatedColor());
        SpatialOperator toolTextAnchor = equippedToolText2D.createSpatialOperator(0.5f, 0.5f);
        toolTextAnchor.alignTo(equippedToolPanel.getOperator(0.5f, 0.5f));
            
        //START equippedTool icon initialization
        float iconSideLength = (21f / 1080) * SCREEN_HEIGHT;
        Vector3f iconDimensions = Vector3F.fitXY(iconSideLength); //width is same as height to make it a square
                
        GeometryPanel equippedIcon = GuiFactory.createEquippedIcon(iconTex, iconDimensions, equippedToolPanel.getScaledDimensions3D(), assetManager, mirrorUI);
        equippedIcon.setMirrored(mirrorUI);        
        equippedToolPanel.attachChild(equippedIcon);
        //END equippedTool icon initialization
        LayerComparator.setLayer(equippedToolPanel, 1);
        LayerComparator.setLayer(equippedToolText2D, 2);
        LayerComparator.setLayer(equippedIcon, 3);
        //END equippedTool GeometryPanel initialization 
        
        //START forecastInfo GeometryPanel initialization
        GeometryPanel forecastInfoPanel = GuiFactory.createBattlePanelFromText(forecastInfoText2D, forecastPanelPadding, assetManager, forecast.getCombatant().getUnit().getAllegiance().getAssociatedColor());        
        forecastInfoText2D.move(Vector3F.fit(forecastInfoTextInitialTranslation.addLocal(0.1f * forecastInfoPanel.getWidth(), 0.15f * forecastInfoPanel.getHeight())));
        //END forecastInfo GeometryPanel initialization
        
        return new Duo<>(equippedToolPanel, forecastInfoPanel);
    }
    
    private Consumer<Material> heartMatParams() {
        return (mat) -> {
            float $75 = 75f / 255f;
            
            //rounded to 5 decimal places
            float hpHeartYStart = 0.03952f;
            float hpHeartYEnd = 0.97251f;
            float percentFull = forecast.getCombatant().getCurrentToMaxHPRatio();
            
            mat.setTexture("ColorMap", MapTextures.GUI.Fighter.HP_Heart);
            mat.setColor("Color", HEART_COLOR_FUNCTION.rgba(percentFull));
            mat.setColor("OnlyChangeColor", ColorRGBA.White);
            mat.setColor("BackgroundColor", new ColorRGBA($75, $75, $75, 1f)); //gray
            mat.setFloat("GradientCoefficient", 1f);
            mat.setFloat("PercentStart", hpHeartYStart);
            mat.setFloat("PercentEnd", hpHeartYEnd);
            mat.setFloat("PercentFilled", percentFull);
            mat.setBoolean("UsesYAxis", true);
        };
    }
    
    private Consumer<Material> tpBallMatParams() {
        return (mat) -> {
            float $75 = 75f / 255f;
            
            //rounded to 5 decimal places
            float tpBallYStart = 0.36317f;
            float tpBallYEnd = 0.93287f;
            
            mat.setTexture("ColorMap", MapTextures.GUI.Fighter.TP_Ball);
            mat.setColor("Color", GameUtils.TP_COLOR_PINK);
            mat.setColor("OnlyChangeColor", ColorRGBA.White);
            mat.setColor("BackgroundColor", new ColorRGBA($75, $75, $75, 1f)); //gray
            mat.setFloat("PercentStart", tpBallYStart);
            mat.setFloat("PercentEnd", tpBallYEnd);
            mat.setFloat("PercentFilled", forecast.getCombatant().getCurrentToMaxTPRatio());
            mat.setBoolean("UsesGradient", false);
            mat.setBoolean("UsesYAxis", true);
        };
    }
}