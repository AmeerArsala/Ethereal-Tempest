/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.gui;

import battle.data.forecast.SingularForecast;
import battle.participant.BattleRole;
import battle.participant.Combatant;
import battle.participant.Combatant.AttackType;
import com.atr.jme.font.util.StringContainer.Align;
import com.atr.jme.font.util.StringContainer.VAlign;
import com.atr.jme.font.util.StringContainer.WrapMode;
import com.atr.jme.font.util.Style;
import com.jme3.asset.AssetManager;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import enginetools.MaterialCreator;
import enginetools.MaterialParamsProtocol;
import etherealtempest.Globals;
import etherealtempest.Main;
import etherealtempest.geometry.Heart;
import etherealtempest.gui.RadialProgressBar;
import fundamental.stats.BaseStat;
import general.math.DomainBox;
import general.ui.GeometryPanel;
import general.ui.GeometryUIElement;
import general.ui.Padding;
import general.ui.text.FontProperties;
import general.ui.text.FontProperties.KeyType;
import general.ui.text.Text2D;
import general.ui.text.TextProperties;
import general.ui.text.quickparams.TextDisplacementParams;
import general.ui.text.quickparams.UIFontParams;
import general.utils.EngineUtils;
import general.utils.EngineUtils.CenterAxis;
import general.utils.GameUtils;
import general.visual.animation.Animation;
import general.visual.animation.VisualTransition;
import java.util.Arrays;

/**
 *
 * @author night
 * 
 */
public class CombatantUI {
    private final Node uiNode = new Node();
    private final boolean mirrorUI;
    
    private final SingularForecast forecast;
    private final AssetManager assetManager;
    private final Camera cam;
    
    private final GeometryPanel portrait, nametag;
    private final GeometryPanel tool, forecastInfo;
    //private final HeartIndicator hpHeart;
    private final ShapeIndicator hpHeart, tpBall;
    
    private ExpIndicator expbar;
    private LevelUpPanel levelUpPanel;
    
    public CombatantUI(SingularForecast forecast, AssetManager assetManager, Camera cam) {
        this.forecast = forecast;
        this.cam = cam;
        this.assetManager = assetManager;
        
        mirrorUI = forecast.getCombatant().battle_role == BattleRole.Initiator;
        
        float portraitDims = 0.2f * Globals.getScreenWidth();
        Vector2f hpHeartDims = new Vector2f(1, 600f / 582f).multLocal(0.16f * Globals.getScreenWidth());
        Vector2f tpBallDims = new Vector2f(1, 581f / 429f).multLocal(0.08f * Globals.getScreenWidth());
        
        portrait = createPortrait(portraitDims, portraitDims);
        nametag = createNametag(new UIFontParams("Interface/Fonts/DIOGENES.ttf", 35f, Style.Plain, 3));
        
        UIFontParams equippedToolFontParams = new UIFontParams("Interface/Fonts/Linux Libertine/LinLibertine_R.ttf", 18f, Style.Plain, 3);
        UIFontParams forecastInfoFontParams = new UIFontParams("Interface/Fonts/Linux Libertine/LinLibertine_R.ttf", 23f, Style.Plain, 3);
        DualPanel equippedToolAndForecastInfo = createEquippedToolAndForecastInfoPanels(equippedToolFontParams, forecastInfoFontParams);
        
        tool = equippedToolAndForecastInfo.panelA;
        forecastInfo = equippedToolAndForecastInfo.panelB;
        
        hpHeart = createIndicator(
            hpHeartDims,
            new UIFontParams("Interface/Fonts/DIOGENES.ttf", 23f, Style.Plain, 3),
            generateHeartMatParams(),
            forecast.getCombatant().getBaseStat(BaseStat.CurrentHP),
            forecast.getCombatant().getBaseStat(BaseStat.MaxHP)
        );
        
        tpBall = createIndicator(
            tpBallDims,
            new UIFontParams("Interface/Fonts/DIOGENES.ttf", 23f, Style.Plain, 3),
            generateTPBallMatParams(),
            forecast.getCombatant().getBaseStat(BaseStat.CurrentTP),
            forecast.getCombatant().getBaseStat(BaseStat.MaxTP)
        );
        
        setLocalTranslationOf(portrait, new Vector3f(0f, Globals.getScreenHeight() - portraitDims, 0f));
        setLocalTranslationOf(nametag, portrait.getLocalTranslation().add(nametag.getWidth() / 2f, nametag.getHeight() / -2f, 1f));
        setLocalTranslationOf(tool, new Vector3f(0.025f * Globals.getScreenWidth(), 0.25f * Globals.getScreenHeight(), 1f));
        setLocalTranslationOf(forecastInfo, new Vector3f(0.025f * Globals.getScreenWidth(), 0.025f * Globals.getScreenHeight(), 0f));
        setLocalTranslationOf(hpHeart.getNode(),
            forecastInfo.getLocalTranslation()
                .mult(new Vector3f(2f, 1f, 1f))
                .add(forecastInfo.getWidth(), 0, 0)
        );
        
        setLocalTranslationOf(tpBall.getNode(), 
            hpHeart.getNode().getLocalTranslation().add(hpHeartDims.x, hpHeartDims.y, 0f).multLocal(1.5f, 0.5f, 0f)
        );
        
        uiNode.attachChild(portrait);
        uiNode.attachChild(nametag);
        uiNode.attachChild(tool);
        uiNode.attachChild(forecastInfo);
        uiNode.attachChild(hpHeart.getNode());
        uiNode.attachChild(tpBall.getNode());
    }
    
    private class DualPanel {
        public final GeometryPanel panelA;
        public final GeometryPanel panelB;
        
        public DualPanel(GeometryPanel panelA, GeometryPanel panelB) {
            this.panelA = panelA;
            this.panelB = panelB;
        }
    }
    
    public Node getNode() { return uiNode; }
    
    public SingularForecast getForecast() { return forecast; }
    public Combatant getCombatant() { return forecast.getCombatant(); }
    public AssetManager getAssetManager() { return assetManager; }
    public Camera getCamera() { return cam; }
    
    public GeometryPanel getPortrait() { return portrait; }
    public GeometryPanel getNametag() { return nametag; }
    
    public GeometryPanel getEquippedPanel() { return tool; }
    public GeometryPanel getForecastInfoPanel() { return forecastInfo; }
    
    //public HeartIndicator getHPHeart() { return hpHeart; }
    public ShapeIndicator getHPHeart() { return hpHeart; }
    public ShapeIndicator getTPBall() { return tpBall; }
    
    public ExpIndicator getExpBar() { return expbar; }
    public LevelUpPanel getLevelUpPanel() { return levelUpPanel; } 
    
    private void setLocalTranslationOf(Spatial spatial, Vector3f localTranslation) {
        if (!mirrorUI) {
            spatial.setLocalTranslation(localTranslation);
            return;
        }
        
        spatial.setLocalTranslation(Globals.getScreenWidth() - localTranslation.x, localTranslation.y, localTranslation.z);
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
        initializeEXPbar();
        
        int currentEXP = forecast.getCombatant().addExpGained(); //add exp gained
        expbar.queueToValue(currentEXP, currentEXP / 100f);
    }
    
    public void levelUp() {
        if (levelUpPanel == null) {
            levelUpPanel = createLevelUpPanel();
        }
        
        float translation = 0.05f * Globals.getScreenHeight();
        float initialX = -levelUpPanel.getPanel().getWidth();
        setLocalTranslationOf(levelUpPanel.getNode(), new Vector3f(initialX, translation, 0.5f));
        
        expbar.levelUp(
            0.1f,   // 0.1 seconds for the "LEVEL UP!" text to expand
            () -> { // onFinish
                uiNode.attachChild(levelUpPanel.getNode());
                
                VisualTransition transition = new VisualTransition(
                    levelUpPanel.getNode(),
                    new Animation() {
                        @Override
                        protected void update(float tpf, float Y, Spatial target, Animation anim) {
                            setLocalTranslationOf(target, target.getLocalTranslation().setX(Y));
                        }
                    }.setInitialAndEndVals(initialX, translation)
                );
                
                transition.setResetProtocol(() -> { //onFinish
                    levelUpPanel.levelUp();
                });
                
                expbar.addTransitionToQueue(transition);
            }
        );
    }
    
    private Text2D generateText(String text, ColorRGBA color, Rectangle rectangle, UIFontParams params, TextDisplacementParams displacementParams) {
        TextProperties textParams = 
            TextProperties.builder()
                .horizontalAlignment(displacementParams.hAlign)
                .verticalAlignment(displacementParams.vAlign)
                .kerning(params.kerning)
                .wrapMode(displacementParams.wrapMode)
                .textBox(rectangle)
                .build();
        
        return new Text2D(text, color, textParams, params.createFontProperties(KeyType.BMP), assetManager);
    }
    
    private GeometryPanel createPortrait(float width, float height) {
        GeometryPanel panel = new GeometryPanel(width, height);
        
        Material mat = new Material(assetManager, MaterialCreator.UNSHADED);
        String texturePath = "Textures/portraits/" + forecast.getCombatant().getUnit().getName() + ".png";
        mat.setTexture("ColorMap", assetManager.loadTexture(texturePath));
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        panel.setMaterial(mat);
        
        return panel;
    }
    
    private GeometryPanel createNametag(UIFontParams params) {
        String text = forecast.getCombatant().getUnit().getName();
        
        Rectangle rect = new Rectangle(
            0f,  // x
            0f,  // y
            text.length() * (params.fontSize + params.kerning), // width
            params.fontSize + params.kerning  // height
        );
        
        TextDisplacementParams displacementParams = new TextDisplacementParams(Align.Center, VAlign.Center, WrapMode.CharClip);
        
        Text2D nameText = generateText(text, ColorRGBA.White, rect, params, displacementParams);
        nameText.setOutlineMaterial(ColorRGBA.White, ColorRGBA.Black);
        
        float width = (626f / 475f) * rect.width;
        float height = (216f / 125f) * rect.height;
        
        GeometryPanel nametagPanel = new GeometryPanel(width, height);
        
        Material mat = new Material(assetManager, MaterialCreator.UNSHADED);
        mat.setTexture("ColorMap", assetManager.loadTexture("Interface/GUI/ui_boxes/emptyname.png"));
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        nametagPanel.setMaterial(mat);
        nametagPanel.attachChild(nameText);
        
        return nametagPanel;
    }
    
    private DualPanel createEquippedToolAndForecastInfoPanels(UIFontParams equippedToolParams, UIFontParams forecastInfoParams) {
        Combatant participant = forecast.getCombatant();
        
        //START equippedTool initialization
            String equippedName, iconPath;
            if (participant.getAttackType() == AttackType.Weapon) {
                equippedName = participant.getUnit().getEquippedWPN().getName();
                iconPath = participant.getUnit().getEquippedWPN().getIconPath();
            } else { // participant.getAttackType() == AttackType.Formula
                equippedName = participant.getUnit().getEquippedFormula().getName();
                iconPath = participant.getUnit().getEquippedFormula().getIconPath();
            }
        //END equippedTool initialization
        
        //START forecastInfo initialization
            String[] lines = {
                "DMG: " + forecast.getDisplayedDamage(),
                "ACC: " + forecast.getDisplayedAccuracy() + "%",
                "CRIT: " + forecast.getDisplayedCrit() + "%"
            };
        
            int longestLength = 0;
            String forecastText = "";
            for (String line : lines) {
                int length = line.length();
                if (length > longestLength) {
                    longestLength = length;
                }
            
                forecastText += line + "\n";
            }
        //END forecastInfo initialization
        
        //compare widths and use the greater one
        float rectangleWidth = Math.max(equippedToolParams.fontSize * (equippedName.length() + 5.5f), forecastInfoParams.fontSize * (longestLength + 6));
        
        //START equippedTool GeometryPanel initialization
            Rectangle equippedToolRectangle = new Rectangle(
                0f, // x
                0f, // y
                rectangleWidth, // width
                equippedToolParams.fontSize + equippedToolParams.kerning // height
            );
        
            TextDisplacementParams equippedToolDisplacementParams = new TextDisplacementParams(Align.Center, VAlign.Center, WrapMode.CharClip);
            Text2D equippedToolText2D = generateText(equippedName, ColorRGBA.White, equippedToolRectangle, equippedToolParams, equippedToolDisplacementParams);
            GeometryPanel equippedToolPanel = createBattlePanelFromText(equippedToolText2D);
        
            //START equippedTool icon initialization
                Vector3f iconDimensions = new Vector3f(equippedToolRectangle.height, equippedToolRectangle.height, 0f); //width is same as height to make it a square
                Vector3f backgroundDimensions = new Vector3f(equippedToolRectangle.width, equippedToolRectangle.height, 0f);
                Padding padding;
                
                if (mirrorUI) {
                    padding = new Padding(0f, 3.5f, 0f, (backgroundDimensions.x / 2f) - iconDimensions.x); // facing left
                } else {
                    padding = new Padding(0f, (backgroundDimensions.x / 2f) - iconDimensions.x, 0f, 3.5f); // facing right
                }
        
                GeometryUIElement equippedIcon = createEquippedIcon(iconPath, iconDimensions, backgroundDimensions, padding);
                equippedIcon.setMirrored(mirrorUI);
                
                equippedToolPanel.attachChild(equippedIcon);
            //END equippedTool icon initialization
            
        //END equippedTool GeometryPanel initialization
        
        //START forecastInfo GeometryPanel initialization
            float forecastInfoRectangleHeight = (lines.length * (forecastInfoParams.fontSize + forecastInfoParams.kerning)) + forecastInfoParams.kerning;
        
            Rectangle forecastInfoRectangle = new Rectangle(
                forecastInfoParams.kerning, // x
                forecastInfoRectangleHeight - forecastInfoParams.kerning, // y
                rectangleWidth, // width
                forecastInfoRectangleHeight // height
            );
        
            TextDisplacementParams forecastInfoDisplacementParams = new TextDisplacementParams(Align.Left, VAlign.Top, WrapMode.CharClip);
            Text2D forecastInfoText2D = generateText(forecastText, ColorRGBA.White, forecastInfoRectangle, forecastInfoParams, forecastInfoDisplacementParams);
            GeometryPanel forecastInfoPanel = createBattlePanelFromText(forecastInfoText2D);
        //END forecastInfo GeometryPanel initialization
        
        return new DualPanel(equippedToolPanel, forecastInfoPanel);
    }
    
    private GeometryPanel createBattlePanelFromText(Text2D panelText) {
        GeometryPanel battlePanel = new GeometryPanel((591f / 559f) * panelText.getTextBoxWidth(), (205f / 176f) * panelText.getTextBoxHeight());
        
        Material mat = new Material(assetManager, MaterialCreator.UNSHADED);
        mat.setTexture("ColorMap", assetManager.loadTexture("Interface/GUI/ui_boxes/battlebox.png"));
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        battlePanel.setMaterial(mat);
        battlePanel.attachChild(panelText);
        
        return battlePanel;
    }
    
    private GeometryUIElement createEquippedIcon(String iconPath, Vector3f iconDimensions, Vector3f backgroundDimensions, Padding padding) {
        Material iconMat = new Material(assetManager, MaterialCreator.UNSHADED);
        iconMat.setTexture("ColorMap", assetManager.loadTexture(iconPath));
        
        GeometryUIElement equippedIcon = new GeometryUIElement(iconDimensions.x, iconDimensions.y, iconMat, padding);
        
        equippedIcon.setLocalTranslation(EngineUtils.centerEntity(iconDimensions, backgroundDimensions, Arrays.asList(CenterAxis.X, CenterAxis.Y)));
        
        return equippedIcon;
    }
    
    /*private HeartIndicator createHeartShape(Heart heart, UIFontParams params) {
        String text = forecast.getCombatant().getBaseStat(BaseStat.CurrentHP) + "/" + forecast.getCombatant().getBaseStat(BaseStat.MaxHP);
        
        Rectangle rect = new Rectangle(
            0f,  // x
            0f,  // y
            heart.getScalar(),            // width
            (2f / 5f) * heart.getScalar() // height
        );
        
        TextDisplacementParams displacementParams = new TextDisplacementParams(Align.Center, VAlign.Center, WrapMode.CharClip);
        
        Text2D hpText = generateText(text, ColorRGBA.White, rect, params, displacementParams);
        hpText.setOutlineMaterial(ColorRGBA.White, ColorRGBA.Black);
        
        return new HeartIndicator(heart, hpText, forecast.getCombatant().getCurrentToMaxHPRatio(), forecast.getCombatant().getBaseStat(BaseStat.MaxHP));
    }*/
    
    private ShapeIndicator createIndicator(Vector2f xyDimensions, UIFontParams params, MaterialParamsProtocol matParams, int current, int max) {
        String text = current + "/" + max;
        
        Rectangle rect = new Rectangle(
            0f,  // x
            0f,  // y
            xyDimensions.x, // width
            (2f / 5f) * xyDimensions.x  // height
        );
        
        TextDisplacementParams displacementParams = new TextDisplacementParams(Align.Center, VAlign.Center, WrapMode.CharClip);
        
        Text2D visibleText = generateText(text, ColorRGBA.White, rect, params, displacementParams);
        visibleText.setOutlineMaterial(ColorRGBA.White, ColorRGBA.Black);
        
        float currentPercent = ((float)current) / max;
        
        return new ShapeIndicator(xyDimensions, matParams, assetManager, visibleText, currentPercent, max);
    }
    
    private MaterialParamsProtocol generateHeartMatParams() {
        return (mat) -> {
            float $75 = 75f / 255f;
            
            //rounded to 5 decimal places
            float hpHeartYStart = 0.03952f;
            float hpHeartYEnd = 0.97251f;
            
            mat.setTexture("ColorMap", assetManager.loadTexture("Interface/GUI/common/heart.png"));
            mat.setColor("Color", ColorRGBA.Red);
            mat.setColor("OnlyChangeColor", ColorRGBA.White);
            mat.setColor("BackgroundColor", new ColorRGBA($75, $75, $75, 1f)); //gray
            mat.setFloat("GradientCoefficient", 1f);
            mat.setFloat("PercentStart", hpHeartYStart);
            mat.setFloat("PercentEnd", hpHeartYEnd);
        };
    }
    
    private MaterialParamsProtocol generateTPBallMatParams() {
        return (mat) -> {
            float $75 = 75f / 255f;
            
            //rounded to 5 decimal places
            float tpBallYStart = 0.36317f;
            float tpBallYEnd = 0.93287f;
            
            mat.setTexture("ColorMap", assetManager.loadTexture("Interface/GUI/common/tpBall.png"));
            mat.setColor("Color", GameUtils.TP_COLOR_PINK);
            mat.setColor("OnlyChangeColor", ColorRGBA.White);
            mat.setColor("BackgroundColor", new ColorRGBA($75, $75, $75, 1f)); //gray
            mat.setFloat("PercentStart", tpBallYStart);
            mat.setFloat("PercentEnd", tpBallYEnd);
        };
    }
    
    private LevelUpPanel createLevelUpPanel() {
        return new LevelUpPanel(forecast.getCombatant().getUnit(), new Vector3f(Globals.getScreenWidth(), Globals.getScreenHeight(), 1f), assetManager);
    }
    
    public void initializeEXPbar() {
        if (expbar != null && uiNode.hasChild(expbar.getExpCircle())) {
            uiNode.detachChild(expbar.getExpCircle());
        }
        
        int specificity = 2;
        ColorRGBA color = forecast.getCombatant().getUnit().getAllegiance().getAssociatedColor();
        float outerRadius = 0.1f * Globals.getScreenWidth();
        float innerToOuterRadiusRatio = 52.5f / 70.75f;
        
        RadialProgressBar expCircle = new RadialProgressBar(innerToOuterRadiusRatio * outerRadius, outerRadius, color, specificity, assetManager);  
        
        String text = "  EXP\n " + forecast.getCombatant().getUnit().currentEXP + "/100";
        
        Text2D expText = generateText(
            text, 
            ColorRGBA.White,
            new Rectangle(0f, 0f, innerToOuterRadiusRatio * outerRadius * 2, innerToOuterRadiusRatio * outerRadius * 2),
            new UIFontParams(
                "Interface/Fonts/Neuton-Regular.ttf",  
                28,
                Style.Plain,
                3
            ),
            new TextDisplacementParams(
                Align.Center,
                VAlign.Center,
                WrapMode.Word
            )
        );
        
        expbar = new ExpIndicator(expCircle, expText, assetManager, forecast.getCombatant().getUnit().currentEXP / 100f, 100);
        
        setLocalTranslationOf(expbar.getExpCircle(), nametag.getLocalTranslation().add(0.05f * Globals.getScreenWidth(), (-2 * outerRadius) - nametag.getHeight(), 0f));
        uiNode.attachChild(expbar.getExpCircle());
    }
    
    
}