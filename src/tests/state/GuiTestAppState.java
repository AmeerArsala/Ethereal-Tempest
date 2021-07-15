/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests.state;

import com.atr.jme.font.util.StringContainer;
import com.atr.jme.font.util.Style;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.font.Rectangle;
import com.jme3.input.FlyByCamera;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.simsilica.lemur.Label;
import etherealtempest.Globals;
import general.ui.text.FontProperties;
import general.ui.text.FontProperties.KeyType;
import general.ui.text.Text2D;
import general.ui.text.TextProperties;

/**
 *
 * @author night
 */
public class GuiTestAppState extends BaseAppState {
    private Node rootNode, guiNode;
    private FlyByCamera flyCam;
    
    private final AssetManager assetManager;
    private final Text2D guiText;
    private final Text2D text3D;
    private final Label lemurGuiText;
    private final Label lemurText3D;
    
    public GuiTestAppState(AssetManager assetManager) {
        this.assetManager = assetManager;
        
        TextProperties guiText_TextProperties = TextProperties.builder()
            .horizontalAlignment(StringContainer.Align.Left)
            .verticalAlignment(StringContainer.VAlign.Top)
            .wrapMode(StringContainer.WrapMode.Clip)
            .kerning(3)
            //.textBox(new Rectangle(0f, 0f, 0.5f * Globals.getScreenWidth(), 0.1f * Globals.getScreenHeight()))
            .build();
        
        TextProperties text3D_TextProperties = TextProperties.builder()
            .horizontalAlignment(StringContainer.Align.Left)
            .verticalAlignment(StringContainer.VAlign.Top)
            .wrapMode(StringContainer.WrapMode.Clip)
            .kerning(3)
            .build();
        
        FontProperties guiText_FontProperties = new FontProperties("Interface/Fonts/Merriweather-Regular.ttf", KeyType.BMP, Style.Plain, 135f);
        FontProperties text3D_FontProperties = new FontProperties("Interface/Fonts/superstar.ttf", KeyType.BMP, Style.Plain, 24);
        
        guiText = new Text2D("2D guiNode Bitmap TTF Text", ColorRGBA.White, guiText_TextProperties, guiText_FontProperties, assetManager);
        text3D = new Text2D("3D rootNode Bitmap TTF Text", ColorRGBA.White, text3D_TextProperties, text3D_FontProperties, assetManager);
        
        guiText.fitInTextBox(1);
        
        lemurGuiText = new Label("2D guiNode Lemur Text");
        lemurText3D = new Label("3D rootNode Lemur Text");
    }

    @Override
    protected void initialize(Application aplctn) {
        rootNode = ((SimpleApplication)aplctn).getRootNode();
        guiNode = ((SimpleApplication)aplctn).getGuiNode();
        flyCam = ((SimpleApplication)aplctn).getFlyByCamera();
        
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(100);
        
        final float SCREEN_WIDTH = Globals.getScreenWidth();
        final float SCREEN_HEIGHT = Globals.getScreenHeight();
        
        guiNode.attachChild(guiText);
        guiNode.attachChild(lemurGuiText);
        
        rootNode.attachChild(text3D);
        rootNode.attachChild(lemurText3D);
        
        rootNode.addLight(new AmbientLight());
        
        guiText.createSpatialOperator(0.5f, 0.5f).alignToLocally(Globals.getScreenDimensions().multLocal(0.5f));
        lemurGuiText.setLocalTranslation(guiText.getLocalTranslation().subtract(0.25f * SCREEN_WIDTH, 0.25f * SCREEN_HEIGHT, 0f));
    }

    @Override
    protected void cleanup(Application aplctn) {
        //something
    }

    @Override
    protected void onEnable() {
        //something
    }

    @Override
    protected void onDisable() {
        //something
    }
    
}
