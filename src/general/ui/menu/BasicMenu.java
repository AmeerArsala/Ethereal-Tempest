/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.ui.menu;

import com.atr.jme.font.util.Style;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.Command;
import enginetools.MaterialCreator;
import general.math.function.RGBAFunction;
import general.ui.Padding;
import general.ui.text.FontProperties;
import general.ui.text.FontProperties.KeyType;
import general.visual.animation.Animation;
import general.visual.animation.VisualTransition;

/**
 *
 * @author night
 * @param <DATA> the type of the data holder (e.g. Conveyor)
 */
public abstract class BasicMenu<DATA> extends Menu<BasicMenuOption<DATA>, DATA> {
    
    public enum Orientation { Horizontal, Vertical }
    
    public static class Settings extends Menu.Settings {
        private final FontProperties uniformFontProperties;
        private final Padding textBoxPadding;
        private final MaterialCreator optionMaterialCreator;
        private final RGBAFunction hoveredColorFunction;
        private final RGBAFunction notHoveredColorFunction;
        private final Orientation menuOrientation;
        private final float paddingBetweenOptions;
        
        private Settings
        (
            boolean autoSelectOnOneOption, boolean hoverCurrentIndexWhenNothingElseIsHovered,
            boolean transitionsOnSelectAndDeselectAreTheSameButReversed,
            Animation[] menuTransitionInOnSelect, Animation[] menuTransitionOutOnSelect,
            Animation[] menuTransitionInOnDeselect, Animation[] menuTransitionOutOnDeselect,
            Runnable closeMenuProtocol,
            FontProperties uniformFontProperties, Padding textBoxPadding,
            MaterialCreator optionMaterialCreator, RGBAFunction hoveredColorFunction, RGBAFunction notHoveredColorFunction,
            Orientation menuOrientation, float paddingBetweenOptions
        ) {
            super(autoSelectOnOneOption, hoverCurrentIndexWhenNothingElseIsHovered, transitionsOnSelectAndDeselectAreTheSameButReversed, menuTransitionInOnSelect, menuTransitionOutOnSelect, menuTransitionInOnDeselect, menuTransitionOutOnDeselect, closeMenuProtocol);
            this.uniformFontProperties = uniformFontProperties;
            this.textBoxPadding = textBoxPadding;
            this.optionMaterialCreator = optionMaterialCreator;
            this.hoveredColorFunction = hoveredColorFunction;
            this.notHoveredColorFunction = notHoveredColorFunction;
            this.menuOrientation = menuOrientation;
            this.paddingBetweenOptions = paddingBetweenOptions;
        }
        
        public FontProperties getUniformFontProperties() { return uniformFontProperties; }
        public Padding getTextBoxPadding() { return textBoxPadding; }
        public MaterialCreator getOptionMaterialCreator() { return optionMaterialCreator; }
        public RGBAFunction getHoveredColorFunction() { return hoveredColorFunction; }
        public RGBAFunction getNotHoveredColorFunction() { return notHoveredColorFunction; }
        public Orientation getOrientation() { return menuOrientation; }
        public float getPaddingBetweenOptions() { return paddingBetweenOptions; }
        
        public static SettingsBuilder builder() {
            return new SettingsBuilder();
        }
        
        public static class SettingsBuilder extends Menu.Settings.SettingsBuilder<SettingsBuilder> {
            private FontProperties uniformFontProperties = new FontProperties("Interface/Fonts/Montaga-Regular.ttf", KeyType.BMP, Style.Plain, 18, 1.75f);
            private Padding textBoxPadding = new Padding(0f, 0f, 0f, 0f);
            private MaterialCreator optionMaterialCreator = new MaterialCreator(MaterialCreator.UNSHADED);
            private RGBAFunction hoveredColorFunction = new RGBAFunction(RadialMenuOption.HoveredOrange);
            private RGBAFunction notHoveredColorFunction = new RGBAFunction(ColorRGBA.Black);
            private Orientation menuOrientation = Orientation.Vertical;
            private float paddingBetweenOptions = 0f;
            
            private SettingsBuilder() {}
            
            @Override
            protected SettingsBuilder returnSelf() {
                return this;
            }
            
            public SettingsBuilder uniformFontProperties(FontProperties uniformFontProperties) {
                this.uniformFontProperties = uniformFontProperties;
                return this;
            }
            
            public SettingsBuilder textBoxPadding(Padding textBoxPadding) {
                this.textBoxPadding = textBoxPadding;
                return this;
            }
            
            public SettingsBuilder optionMaterialCreator(MaterialCreator optionMaterialCreator) {
                this.optionMaterialCreator = optionMaterialCreator;
                return this;
            }
            
            public SettingsBuilder hoveredColorFunction(RGBAFunction hoveredColorFunction) {
                this.hoveredColorFunction = hoveredColorFunction;
                return this;
            }
            
            public SettingsBuilder notHoveredColorFunction(RGBAFunction notHoveredColorFunction) {
                this.notHoveredColorFunction = notHoveredColorFunction;
                return this;
            }
            
            public SettingsBuilder menuOrientation(Orientation menuOrientation) {
                this.menuOrientation = menuOrientation;
                return this;
            }
            
            public SettingsBuilder paddingBetweenOptions(float paddingBetweenOptions) {
                this.paddingBetweenOptions = paddingBetweenOptions;
                return this;
            }
            
            @Override
            public Settings build() {
                return new Settings(autoSelectOnOneOption, hoverCurrentIndexWhenNothingElseIsHovered, transitionsOnSelectAndDeselectAreTheSameButReversed, menuTransitionInOnSelect, menuTransitionOutOnSelect, menuTransitionInOnDeselect, menuTransitionOutOnDeselect, closeMenuProtocol, uniformFontProperties, textBoxPadding, optionMaterialCreator, hoveredColorFunction, notHoveredColorFunction, menuOrientation, paddingBetweenOptions);
            }
        }
    }
    
    public static class Commands {
        private final Command<BasicMenuOption> setHoveredOn, setHoveredOff;
        
        private Commands(Command<BasicMenuOption> setHoveredOn, Command<BasicMenuOption> setHoveredOff) {
            this.setHoveredOn = setHoveredOn;
            this.setHoveredOff = setHoveredOff;
        }
        
        public void runSetHoveredOn(BasicMenuOption source) { setHoveredOn.execute(source); }
        public void runSetHoveredOff(BasicMenuOption source) { setHoveredOff.execute(source); }
        
        public static CommandCenter creator() {
            return new CommandCenter();
        }
        
        public static class CommandCenter {
            private static final Command<BasicMenuOption> NONE = (source) -> {};
            
            private Command<BasicMenuOption> setHoveredOn = NONE, setHoveredOff = NONE;
            
            public CommandCenter setHoveredOn(Command<BasicMenuOption> setHoveredOn) {
                this.setHoveredOn = setHoveredOn;
                return this;
            }
            
            public CommandCenter setHoveredOff(Command<BasicMenuOption> setHoveredOff) {
                this.setHoveredOff = setHoveredOff;
                return this;
            }
            
            public Commands build() {
                return new Commands(setHoveredOn, setHoveredOff);
            }
        }
    }
    
    private float scaleX = 1, scaleY = 1;
    private float greatestOptionWidth = 0, greatestOptionHeight = 0;
    
    private final VisualTransition menuTransitionEvent = new VisualTransition(optionsNode);
    private final Settings params;
    private final Commands universalOptionCommands;
    
    public BasicMenu(String title, BasicMenu.Settings params, BasicMenu.Commands universalOptionCommands) {
        this(title, 1f, 1f, params, universalOptionCommands);
    }
    
    public BasicMenu(String title, BasicMenu.Settings params) {
        this(title, 1f, 1f, params, Commands.creator().build());
    }
    
    public BasicMenu(String title, float scaleX, float scaleY, BasicMenu.Settings params) {
        this(title, scaleX, scaleY, params, Commands.creator().build());
    }

    public BasicMenu(String title, float scaleX, float scaleY, BasicMenu.Settings params, BasicMenu.Commands universalOptionCommands) {
        super(title, params);
        scale(scaleX, scaleY);
        
        this.params = params;
        this.universalOptionCommands = universalOptionCommands;
    }
    
    public float getXScale() { return scaleX; }
    public float getYScale() { return scaleY; }
    
    public final void scale(float x, float y) {
        scaleX *= x;
        scaleY *= y;
        
        optionsNode.scale(scaleX, scaleY, 1);
    }
    
    public final void scale(float factor) {
        scaleX *= factor;
        scaleY *= factor;
        
        optionsNode.scale(scaleX, scaleY, 1);
    }
    
    public final void setScale(float x, float y) {
        scaleX = x;
        scaleY = y;
        
        optionsNode.setLocalScale(scaleX, scaleY, 1);
    }
    

    @Override
    protected void reset() {}
    
    @Override
    protected void optionsInitializationIteration(BasicMenuOption<DATA> option, AssetManager assetManager, int i, int size) {
        option.setCommands(universalOptionCommands);
        
        Vector3f optionSize = option.initializeText(assetManager, params.getUniformFontProperties(), params.getTextBoxPadding());
            
        if (optionSize.x > greatestOptionWidth) {
            greatestOptionWidth = optionSize.x;
        }
            
        if (optionSize.y > greatestOptionHeight) {
            greatestOptionHeight = optionSize.y;
        }
    }
    
    @Override
    protected void finishOptionsInitialization(AssetManager assetManager) {
        for (BasicMenuOption<DATA> option : availableOptions) {
            Vector3f box = new Vector3f(greatestOptionWidth, greatestOptionHeight, 1);
        
            option.initializeAssets(assetManager, box, params.getOptionMaterialCreator(), params.getHoveredColorFunction(), params.getNotHoveredColorFunction());
            option.correctPanelPosition(params.getPaddingBetweenOptions(), params.getOrientation());
        }
        
        availableOptions.get(0).setHovered(true);
    }
    
    @Override
    protected void updateCustom(float tpf) {
        menuTransitionEvent.update(tpf);
        updateExtra(tpf);
    }
    
    @Override
    protected void incrementCurrentIndex(int num) {
        currentIndex = Math.abs((currentIndex + num) % availableOptions.size());
    }
    
    @Override
    protected void onMoveYStart() {
        availableOptions.get(currentIndex).setHovered(true);
    }
    
    @Override
    public void resolveInput(String name, boolean keyPressed, float tpf) {
        //options are displayed top to bottom, to moving a positive amount would go down the menu
        if (name.equals("move up")) {
            moveY(-1);
        }
        if (name.equals("move down")) {
            moveY(1);
        }
    }
    
    protected abstract void updateExtra(float tpf);
}
