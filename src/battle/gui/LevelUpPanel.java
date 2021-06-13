/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.gui;

import com.atr.jme.font.util.StringContainer.Align;
import com.atr.jme.font.util.StringContainer.VAlign;
import com.atr.jme.font.util.StringContainer.WrapMode;
import com.atr.jme.font.util.Style;
import com.jme3.asset.AssetManager;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import enginetools.MaterialCreator;
import fundamental.stats.BaseStat;
import general.procedure.OrdinalQueue;
import general.procedure.functional.UpdateLoop;
import general.ui.GeometryPanel;
import general.ui.text.FontProperties;
import general.ui.text.FontProperties.KeyType;
import general.ui.text.SpecialCharacter;
import general.ui.text.Text2D;
import general.ui.text.TextProperties;
import general.visual.animation.Animation;
import general.visual.animation.VisualTransition;
import general.visual.animation.VisualTransition.Progress;
import java.util.HashMap;
import maps.layout.occupant.character.TangibleUnit;

/**
 *
 * @author night
 */
public class LevelUpPanel {
    private static final float PANEL_WIDTH_PERCENT = 0.4f, PANEL_HEIGHT_PERCENT = 0.9f;
    
    private static final ColumnStat[] firstColumnStatNames = 
    {
        new ColumnStat(" MAX HP:", BaseStat.MaxHP),
        new ColumnStat("        STR:", BaseStat.Strength),
        new ColumnStat("   ETHER:", BaseStat.Ether),
        new ColumnStat("        AGI:", BaseStat.Agility),
        new ColumnStat("    COMP:", BaseStat.Comprehension),
        new ColumnStat("       DEX:", BaseStat.Dexterity)
    };
    
    private static final ColumnStat[] secondColumnStatNames = 
    {
        new ColumnStat("     MAX TP:", BaseStat.MaxTP),
        new ColumnStat("            DEF:", BaseStat.Defense),
        new ColumnStat("            RSL:", BaseStat.Resilience),
        new ColumnStat(" MOBILITY:", BaseStat.Mobility),
        new ColumnStat("PHYSIQUE:", BaseStat.Physique),
        new ColumnStat("INIT. ADR.:", BaseStat.Adrenaline)
    };
    
    private final Node node = new Node("level up panel");
    private final AssetManager assetManager;
    private final Vector3f res; // camera dimensions
    
    private final GeometryPanel panel;
    
    private final TangibleUnit character;
    private final HashMap<BaseStat, Integer> leveledStats;
    private final Text2D[] firstColumnStats = new Text2D[6], secondColumnStats = new Text2D[6];
    
    private GeometryPanel portrait, portraitFrame;
    private Text2D nameText, levelText, jobclassText;
    
    private final OrdinalQueue<StatArrow> arrowQueue = new OrdinalQueue<>(
        (arrow, tpf) -> {
            arrow.update(tpf);
        },
        (arrow) -> {
            return arrow.isFinished();
        }
    );
    
    public LevelUpPanel(TangibleUnit unit, Vector3f cameraDimensions, AssetManager assetManager) {
        this.assetManager = assetManager;
        character = unit;
        leveledStats = character.rollLevelUp();
        res = cameraDimensions;
        
        panel = new GeometryPanel(PANEL_WIDTH_PERCENT * res.x, PANEL_HEIGHT_PERCENT * res.y);
        
        Material mat = new Material(assetManager, MaterialCreator.UNSHADED);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.setTexture("ColorMap", assetManager.loadTexture("Textures/gui/page.jpg"));
        
        panel.setMaterial(mat);
        
        initialize();
        node.attachChild(panel);
    }
    
    public Node getNode() { return node; }
    public GeometryPanel getPanel() { return panel; }
    public HashMap<BaseStat, Integer> getLeveledStats() { return leveledStats; }
    public boolean isQueueFinished() { return arrowQueue.isFinished(); }
    
    public void update(float tpf) {
        arrowQueue.update(tpf);
    }
    
    public void levelUp() {
        for (int i = 0; i < 6; ++i) {
            ColumnStat column1stat = firstColumnStatNames[i], column2stat = secondColumnStatNames[i];
            
            if (leveledStats.get(column1stat.stat) != 0) {
                StatArrow column1statArrow = new StatArrow(column1stat);
                int index = i;
                
                arrowQueue.addToQueue(column1statArrow, 
                    (tpf) -> {
                        if (!column1statArrow.isAttached()) {
                            column1statArrow.attachTo(firstColumnStats[index]); //attach once
                        }
                    },
                    () -> { //onFinish
                        firstColumnStats[index].setTextColor(ColorRGBA.Blue); //set text to blue when complete
                    }
                );
            }
            
            if (leveledStats.get(column2stat.stat) != 0) {
                StatArrow column2statArrow = new StatArrow(column2stat);
                int index = i;
                
                arrowQueue.addToQueue(column2statArrow, 
                    (tpf) -> {
                        if (!column2statArrow.isAttached()) {
                            column2statArrow.attachTo(secondColumnStats[index]); //attach once
                        }
                    },
                    () -> { //onFinish
                        secondColumnStats[index].setTextColor(ColorRGBA.Blue); //set text to blue when complete
                    }
                );
            }
        }
        
        //level up when finished
        arrowQueue.onCurrentQueueFinished(() -> {
            character.levelUp(leveledStats);
        });
    }
    
    private void initialize() {
        initializeNameAndClass();
        initializePortrait();
        initializeStats();
    }
    
    private void initializeNameAndClass() {
        //initialize name
        nameText = createText(
            character.getJobClass().getName(),
            "Interface/Fonts/IMFellDWPica-Regular.ttf",
            30, //fontSize
            Style.Bold,
            Align.Center,
            new Rectangle(0f, 0f, 0.35f * panel.getWidth(), panel.getHeight() / 12f),
            assetManager
        );
        
        //initialize jobclass
        jobclassText = createText(
            character.getJobClass().getName(),
            "Interface/Fonts/IMFellDWPica-Regular.ttf",
            27, //fontSize
            Style.Plain,
            Align.Center,
            new Rectangle(0f, 0f, 0.35f * panel.getWidth(), panel.getHeight() / 18f),
            assetManager
        );
        
        nameText.setLocalTranslation(0.375f * panel.getWidth(), 0.8f * panel.getHeight(), 1f);
        jobclassText.setLocalTranslation(0.375f * panel.getWidth(), panel.getHeight() / 6f, 1f);
        
        panel.attachChild(nameText);
        panel.attachChild(jobclassText);
    }
    
    private void initializePortrait() {
        float portraitDims = panel.getWidth() / 3f;
        
        portrait = new GeometryPanel(portraitDims, portraitDims);
        Material portraitMat = new Material(assetManager, MaterialCreator.UNSHADED);
        portraitMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        portraitMat.setTexture("ColorMap", assetManager.loadTexture("Textures/portraits/" + character.getUnitInfo().getPortraitTextureName()));
        portrait.setMaterial(portraitMat);
        
        portraitFrame = new GeometryPanel(1.15f * portraitDims, 1.15f * portraitDims);
        Material frameMat = new Material(assetManager, MaterialCreator.UNSHADED);
        frameMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        frameMat.setTexture("ColorMap", assetManager.loadTexture("Interface/GUI/ui_borders/framebgtransparent.png"));
        portraitFrame.setMaterial(frameMat);
        
        portraitFrame.setLocalTranslation((portraitDims - portraitFrame.getWidth()) / 2f, (portraitDims - portraitFrame.getHeight()) / 2f, 2f); //center frame
        portrait.attachChild(portraitFrame);

        portrait.setLocalTranslation((panel.getWidth() - portraitDims) / 2f, panel.getHeight() / 2f, 1f); //center portrait
        panel.attachChild(portrait);
    }
    
    private void initializeStats() {
        String fontPath = "Interface/Fonts/Linux Libertine/LinLibertine_R.ttf";
        Style style = Style.Plain;
        
        //initialize level up text
        levelText = createText(
            "LVL " + character.getLVL() + " " + SpecialCharacter.RightArrow + " " + (character.getLVL() + leveledStats.get(BaseStat.Level)),
            fontPath,
            30, //fontSize
            style,
            Align.Center,
            new Rectangle(0f, 0f, 0.3f * panel.getWidth(), panel.getHeight() / 15f),
            assetManager
        );
        
        levelText.setLocalTranslation(0.4f * panel.getWidth(), 0.5f * panel.getWidth(), 1f);
        panel.attachChild(levelText);
        
        //initialize columns
        for (int i = 0; i < 6; ++i) {
            ColumnStat column1stat = firstColumnStatNames[i];
            ColumnStat column2stat = secondColumnStatNames[i];
            
            float columnFontSize = 27f;
            float textBoxWidth = 0.25f * panel.getWidth(), textBoxHeight = panel.getHeight() / 18f;
            
            firstColumnStats[i] = createText(
                column1stat.generateText(character),
                fontPath,
                columnFontSize,
                style,
                Align.Left,
                new Rectangle(0f, 0f, textBoxWidth, textBoxHeight),
                assetManager
            );
            
            secondColumnStats[i] = createText(
                column2stat.generateText(character),
                fontPath,
                columnFontSize,
                style,
                Align.Left,
                new Rectangle(0f, 0f, textBoxWidth, textBoxHeight),
                assetManager
            );
            
            float textYValue = ((0.5f * panel.getHeight()) - textBoxHeight) - (i * (textBoxHeight + (0.01f * panel.getHeight())));
            
            firstColumnStats[i].setLocalTranslation(panel.getWidth() / 9f, textYValue, 1f);
            secondColumnStats[i].setLocalTranslation(panel.getWidth() / 2f, textYValue, 1f);
            
            panel.attachChild(firstColumnStats[i]);
            panel.attachChild(secondColumnStats[i]);
        }
    }
    
    private Text2D createText(String text, String fontPath, float fontSize, Style style, Align hAlign, Rectangle textBox, AssetManager assetManager) {
        TextProperties textParams = 
            TextProperties.builder()
                .horizontalAlignment(hAlign)
                .verticalAlignment(VAlign.Center)
                .kerning(3)
                .wrapMode(WrapMode.NoWrap)
                .textBox(textBox)
                .build();
        
        FontProperties fontParams = new FontProperties(fontPath, KeyType.BMP, style, fontSize);
        
        return new Text2D(text, ColorRGBA.Black, textParams, fontParams, assetManager);
    }
    
    private class StatArrow {
        public final ColumnStat stat;
        public final GeometryPanel arrowPanel;
        
        private final Text2D difference;
        private final VisualTransition transitionUp;
        
        private boolean isAttached = false;
        
        public StatArrow(ColumnStat stat) {
            this.stat = stat;
            
            float height = panel.getHeight() / 16.5f;
            float widthToHeightRatio = 600f / 788f;
            arrowPanel = new GeometryPanel(widthToHeightRatio * height, height);
            
            Material mat = new Material(assetManager, MaterialCreator.UNSHADED);
            mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            mat.setTexture("ColorMap", assetManager.loadTexture("Interface/GUI/common/arrow.png"));
            
            arrowPanel.setMaterial(mat);
            
            difference = createText(
                deltaStatString(),
                "Interface/Fonts/Dalek.ttf",
                18, //fontSize
                Style.Plain,
                Align.Left,
                new Rectangle(0f, 0f, 0.15f * panel.getWidth(), panel.getHeight() / 18f),
                assetManager
            );
            
            difference.setOutlineMaterial(ColorRGBA.White, ColorRGBA.Black);
            
            difference.setLocalTranslation(1.25f * arrowPanel.getWidth(), height / 2f, 2f);
            arrowPanel.attachChild(difference);
            
            float distance = height / 2f;
            
            arrowPanel.setLocalTranslation(0f, -distance, 1.5f);
            
            transitionUp = new VisualTransition(
                arrowPanel, 
                Animation.CleanOpacityShift((GeometryPanel destination, ColorRGBA param) -> {
                    destination.getMaterial().setColor("Color", param);
                }).setLength(0.2f).onFinish(() -> { arrowPanel.attachChild(difference); }),
                Animation.MoveDirection2D(FastMath.HALF_PI, distance)
            );
            
            transitionUp.beginTransitions();
        }
        
        public boolean isAttached() {
            return isAttached;
        }
        
        public void update(float tpf) {
            transitionUp.update(tpf);
        }
        
        public boolean isFinished() {
            return transitionUp.getTransitionProgress() == Progress.Finished;
        }
        
        public void attachTo(Node parent) {
            parent.attachChild(arrowPanel);
        }
        
        public final String deltaStatString() {
            String deltaStat = "";
            int delta = leveledStats.get(stat.stat);
            
            if (delta > 0) {
                deltaStat += "+";
            }
            
            return deltaStat + delta;
        }
    }
    
    private static class ColumnStat {
        public final String name;
        public final BaseStat stat;
        
        public ColumnStat(String name, BaseStat stat) {
            this.name = name;
            this.stat = stat;
        }
        
        public String generateText(TangibleUnit tu) {
            return name + " " + tu.getBaseStat(stat);
        }
    }
}
