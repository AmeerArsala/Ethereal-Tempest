/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.item;

import com.atr.jme.font.util.StringContainer;
import com.atr.jme.font.util.Style;
import com.jme3.asset.AssetManager;
import com.jme3.font.Rectangle;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import enginetools.MaterialCreator;
import etherealtempest.info.Conveyor;
import fundamental.Gear;
import fundamental.stats.RawBroadBonus;
import general.math.function.RGBAFunction;
import general.ui.Padding;
import general.ui.menu.BasicMenu;
import general.ui.menu.BasicMenu.Orientation;
import general.ui.menu.BasicMenuOption;
import general.ui.menu.RadialMenuOption;
import general.ui.text.FontProperties;
import general.ui.text.FontProperties.KeyType;
import general.ui.text.TextProperties;
import general.visual.animation.Animation;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public abstract class Item extends Gear {
    protected int weight;
    protected int worth;
    
    protected ItemEffect useEffect = null;
    
    public Item(String name, String desc, int weight, int worth, RawBroadBonus passive) {
        super(name, desc, passive);
        this.weight = weight;
        this.worth = worth;
    }
    
    public Item(String name, String desc, int weight, int worth, RawBroadBonus passive, ItemEffect useEffect) {
        super(name, desc, passive);
        this.weight = weight;
        this.worth = worth;
        this.useEffect = useEffect;
    }
    
    public abstract String getIconPath();
    
    public int getWeight() { return weight; }
    public int getWorth() { return worth; }
    
    public ItemEffect getItemEffect() { return useEffect; }
    
    protected String statDesc() {
        String statDesc =   
            "Weight: " + weight + "\n" +
            "Worth: " + worth + "\n";
        
        if (passive != null) {
            statDesc += passive.toString();
        }
        
        return statDesc;
    }
    
    public BasicMenu<Conveyor> createSubmenu(Conveyor data, AssetManager assetManager) {
        float moveDistance = 25f;
        
        BasicMenu.Settings submenuSettings = 
            BasicMenu.Settings.builder()
                .autoSelectOnOneOption(false)
                .hoverCurrentIndexWhenNothingElseIsHovered(false)
                .uniformFontProperties(new FontProperties("Interface/Fonts/Montaga-Regular.ttf", KeyType.BMP, Style.Plain, 16, 1.75f))
                .textBoxPadding(new Padding(2.5f, 2.5f, 2.5f, 2.5f))
                .optionMaterialCreator(new MaterialCreator(MaterialCreator.UNSHADED))
                .hoveredColorFunction(new RGBAFunction(RadialMenuOption.HoveredOrange))
                .notHoveredColorFunction(new RGBAFunction(ColorRGBA.Black))
                .menuOrientation(Orientation.Vertical)
                .paddingBetweenOptions(0f)
                .menuTransitionInOnSelect(new Animation[] { Animation.DissolveIn().setLength(0.25f), Animation.MoveDirection2D(0, moveDistance) })
                .menuTransitionOutOnDeselect(new Animation[] { Animation.DissolveOut().setLength(0.25f), Animation.MoveDirection2D(FastMath.PI, moveDistance) }) 
                .build();
        
        BasicMenu<Conveyor> sub = new BasicMenu<Conveyor>(name, submenuSettings) {
            @Override
            protected void updateExtra(float tpf) {}

            @Override
            protected void initialize(Conveyor data) {}

            @Override
            protected void onDetach() {}
        };
        
        sub.fullyInitialize(data, onSelectOptions(data), assetManager);
        
        return sub;
    }
    
    protected List<BasicMenuOption<Conveyor>> onSelectOptions(Conveyor data) {
        List<BasicMenuOption<Conveyor>> options = new ArrayList<>();
        
        float rectangleX = 0f;
        float rectangleY = 0f;
        float rectangleWidth = 50f;
        float rectangleHeight = 25f;
        
        if (useEffect != null) { //use is an option
            boolean useIsAvailable = useEffect.canBeUsed(data);
           
            Rectangle rect = new Rectangle(rectangleX, rectangleY, rectangleWidth, rectangleHeight);
            TextProperties useProperties =
                TextProperties.builder()
                    .kerning(3)
                    .horizontalAlignment(StringContainer.Align.Center)
                    .verticalAlignment(StringContainer.VAlign.Center)
                    .wrapMode(StringContainer.WrapMode.Clip)
                    .textBox(rect)
                    .build();
           
            ColorRGBA useTextColor;
            if (useIsAvailable) {
               useTextColor = ColorRGBA.White;
            } else {
                useTextColor = new ColorRGBA(176f / 255f, 176f / 255f, 176f / 255f, 1f); //gray
            }
           
            BasicMenuOption<Conveyor> useOption = new BasicMenuOption<Conveyor>("Use", useProperties, useTextColor) {
                @Override
                protected void initialize(AssetManager assetManager, Conveyor data) {
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
                
                @Override
                protected void onSelect() {
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            };
            
            options.add(useOption);
        }
        
        Rectangle rect = new Rectangle(rectangleX, rectangleY, rectangleWidth, rectangleHeight);
        TextProperties discardProperties =
            TextProperties.builder()
                .kerning(3)
                .horizontalAlignment(StringContainer.Align.Center)
                .verticalAlignment(StringContainer.VAlign.Center)
                .wrapMode(StringContainer.WrapMode.Clip)
                .textBox(rect)
                .build();
        
        BasicMenuOption<Conveyor> discardOption = new BasicMenuOption<Conveyor>("Discard", discardProperties, ColorRGBA.White) {
            @Override
            protected void initialize(AssetManager assetManager, Conveyor data) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            
            @Override
            protected void onSelect() {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        
        options.add(discardOption);
        
        return options;
    }
}
