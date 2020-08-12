/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package misc;

import com.jme3.texture.Texture;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class CustomAnimationSegment {
    private String directory, normalDirectory, mode;
    private String[] dir;
    private int digits;
    
    private int[] indexes;
    private FrameDelay[] special;
    
    private final List<Texture> animations = new ArrayList<>();
    
    private List<String> animation = new ArrayList<>(), addresses = new ArrayList<>();
    
    private int frameOfCasting = -1;
    
    private final FrameUsage decType;
    
    public enum FrameUsage {
        Primitive,
        Specialized,
        None
    }
    
    public CustomAnimationSegment(String directory, String normalDirectory, int digits, int[] indexes) {
        this.directory = directory;
        this.normalDirectory = normalDirectory;
        this.digits = digits;
        this.indexes = indexes;
        
        dir = new File(directory).list();
        setAnimation();
        
        decType = FrameUsage.None;
    }
    
    public CustomAnimationSegment(String mode, int[] indexes) {
        this.mode = mode;
        this.indexes = indexes;
        
        decType = FrameUsage.Primitive;
    }
    
    public CustomAnimationSegment(String mode, FrameDelay[] special) {
        this.mode = mode;
        this.special = special;
        
        decType = FrameUsage.Specialized;
    }
    
    public CustomAnimationSegment(String mode, FrameDelay[] special, int frameOfCasting) {
        this.mode = mode;
        this.special = special;
        this.frameOfCasting = frameOfCasting;
        
        decType = FrameUsage.Specialized;
    }
    
    public FrameUsage getFrameUsageType() {
        return decType;
    }
    
    public int getFrame(int index) {
        if (index < indexes.length) {
            return indexes[index];
        }
        
        return indexes[indexes.length - 1];
    }
    
    public int[] getIndexes() {
        return indexes;
    }
    
    public FrameDelay[] getFrameDs() {
        return special;
    }
    
    public String getMode() {
        return mode;
    }
    
    public int getFrameOfCasting() {
        return frameOfCasting;
    }
    
    private void setAnimation() {
        for (int index : indexes) {
            int zeroes = digits - getBase10(index);
            String str = normalDirectory, address = amountOfNumber(0, zeroes) + index;
            str += dir[index];
            addresses.add(dir[index]);
            animation.add(str);
        }
    }
    
    public static String amountOfNumber(int num, int amt) {
        String s = "";
        for (int i = 0; i < amt; i++) {
            s += num;
        }
        return s;
    }
    
    public static int getBase10(int num) {
        int base = 0;
        
        while (num != 0 && ((int)(num / Math.pow(10, base)) >= 10 || (int)(num / Math.pow(10, base)) <= 0)) {
            base++;
        }
        
        return base;
    }
    
    public List<String> getAnimation() { return animation; }
    public List<String> getAddresses() { return addresses; }
    
    public List<Texture> getTextures() { return animations; }
    
}
