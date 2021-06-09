/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.ui.menu;

import general.visual.animation.Animation;

/**
 *
 * @author night
 * @param <O>
 * @param <DATA> the type of the data holder (e.g. Conveyor)
 * 
 * usage of AbstractMenuOption is completely optional
 */
public abstract class AbstractMenu<O extends MenuOption<DATA>, DATA> extends Menu<O, DATA> {
    public static class Settings extends Menu.Settings {
        
        protected Settings
        (
            boolean autoSelectOnOneOption, boolean hoverCurrentIndexWhenNothingElseIsHovered,
            boolean transitionsOnSelectAndDeselectAreTheSameButReversed,
            Animation[] menuTransitionInOnSelect, Animation[] menuTransitionOutOnSelect,
            Animation[] menuTransitionInOnDeselect, Animation[] menuTransitionOutOnDeselect,
            Runnable closeMenuProtocol
        ) 
        {
            super(autoSelectOnOneOption, hoverCurrentIndexWhenNothingElseIsHovered, transitionsOnSelectAndDeselectAreTheSameButReversed, menuTransitionInOnSelect, menuTransitionOutOnSelect, menuTransitionInOnDeselect, menuTransitionOutOnDeselect, closeMenuProtocol);
        }
        
        public SettingsBuilder builder() {
            return new SettingsBuilder();
        }
        
        public static class SettingsBuilder extends Menu.Settings.SettingsBuilder<SettingsBuilder> {

            @Override
            protected SettingsBuilder returnSelf() {
                return this;
            }

            @Override
            public Settings build() {
                return new Settings(autoSelectOnOneOption, hoverCurrentIndexWhenNothingElseIsHovered, transitionsOnSelectAndDeselectAreTheSameButReversed, menuTransitionInOnSelect, menuTransitionOutOnSelect, menuTransitionInOnDeselect, menuTransitionOutOnDeselect, closeMenuProtocol);
            }
        
        }
    }
    
    
    public AbstractMenu(String title, AbstractMenu.Settings params) {
        super(title, params);
    }
    
}
