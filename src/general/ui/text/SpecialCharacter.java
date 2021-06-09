/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.ui.text;

/**
 *
 * @author night
 */
public enum SpecialCharacter {
    Infinity('\u221E'),
    LeftArrow('\u2190'), 
    RightArrow('\u2192'), 
    UpArrow('\u2191'), 
    DownArrow('\u2193'),
    HorizontalBothDirectionsArrow('\u2194'),
    VerticalBothDirectionsArrow('\u2195'),
    LeftAndRightArrows('\u21c4'),
    UpAndDownArrows('\u21c5');
    
    private final char specialChar;
    private SpecialCharacter(char specialChar) {
        this.specialChar = specialChar;
    }
    
    public char getChar() {
        return specialChar;
    }
    
    public static String allCharacters() {
        String all = "";
        for (SpecialCharacter SC : SpecialCharacter.values()) {
            all += SC.getChar();
        }
        
        return all;
    }
}
