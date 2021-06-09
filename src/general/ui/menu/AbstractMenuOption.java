/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.ui.menu;

/**
 *
 * @author night
 * @param <DATA> the type of the data holder (e.g. Conveyor)
 */
public abstract class AbstractMenuOption<DATA> extends MenuOption<DATA> {
    
    public AbstractMenuOption(String optionName) {
        super(optionName);
    }
    
}
