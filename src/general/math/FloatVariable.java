/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.math;

/**
 *
 * @author night
 */
public class FloatVariable {
    private final char name; 
    private float value;
    
    public FloatVariable(char name, float value) {
        this.name = name;
        this.value = value;
    }
    
    public FloatVariable(char name) {
        this.name = name;
        value = 0f;
    }
    
    public char getChar() { return name; }
    public float getVal() { return value; }
    
    public void setVal(float val) {
        value = val;
    }
    
    public void addVal(float add) {
        value += add;
    }
    
    public void multVal(float scalar) {
        value *= scalar;
    }
    
    public void modVal(float factor) {
        value %= factor;
    }
    
    /**
     * 
     * @param cipheredStr
     * @return a new deciphered String
     */
    public String decipher(String cipheredStr) {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0, len = cipheredStr.length(); i < len; ++i) {
            char ch = cipheredStr.charAt(i);
            if (ch == name) {
                sb.append(value);
            } else {
                sb.append(ch);
            }
        }
        
        return sb.toString();
    }
}
