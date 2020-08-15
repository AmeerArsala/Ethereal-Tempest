/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

import com.atr.jme.font.shape.TrueTypeNode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;
import maps.layout.TangibleUnit;

/**
 *
 * @author night
 */
public class GeneralUtils {
    public enum CenterAxis {
        X,
        Y,
        Z
    }
    
    public static Vector3f centerEntity(Vector3f entityDimensions, Vector3f backgroundDimensions, List<CenterAxis> centerAxes) {
        float centerX = (backgroundDimensions.x - entityDimensions.x) / 2f;
        float centerY = (backgroundDimensions.y - entityDimensions.y) / 2f;
        float centerZ = (backgroundDimensions.z - entityDimensions.z) / 2f;
        return new Vector3f
        (
            centerAxes.contains(CenterAxis.X) ? centerX : 0,
            centerAxes.contains(CenterAxis.Y) ? centerY : 0,
            centerAxes.contains(CenterAxis.Z) ? centerZ : 0
        );
    }
    
    public static Vector3f generateBoundsToCenter(TrueTypeNode label) {
        return new Vector3f
        (
           label.getWidth(),
           label.getHeight(),
           0
        );
    }
    
    public static boolean contain(String k, String s) {
        String str = s.toLowerCase();
        String j = k.toLowerCase();
        if (str.contains(j)) {
            if (str.indexOf(j) == 0) {
                return true;
            } else {
               if(str.charAt(str.indexOf(j) - 1) == ' ') {
                   if (str.indexOf(j) + (j.length() - 1) == str.length() - 1) {
                       return true;
                   } else {
                       if (str.charAt(str.indexOf(j) + j.length()) == ' ' || str.charAt(str.indexOf(j) + j.length()) == '.' || str.charAt(str.indexOf(j) + j.length()) == ',' || str.charAt(str.indexOf(j) + j.length()) == ';') {
                           return true;
                       }
                    }
               }
            }
        }
        return false;
    }
    
    public static ColorRGBA AssociatedColor(TangibleUnit tu) {
        ColorRGBA barColor;
        
        switch (tu.unitStatus) {
            case Player: //blue
                barColor = new ColorRGBA(0.012f, 0.58f, 0.988f, 1f);
                break;
            case Enemy: //red
                barColor = new ColorRGBA(0.839f, 0, 0, 1f);
                break;
            case Ally: //yellow
                barColor = new ColorRGBA(1f, 0.851f, 0, 1f);
                break;
            case ThirdParty: //green
                barColor = new ColorRGBA(0, 1f, 0, 1f);
                break;
            case FourthParty: //purple
                barColor = new ColorRGBA(0.784f, 0, 1f, 1f);
                break;
            case FifthParty: //white
                barColor = ColorRGBA.White;
                break;
            default:
                barColor = new ColorRGBA(0.012f, 0.58f, 0.988f, 1f);
                break;
        }
        
        return barColor;
    }
    
    public static String ordinalNumberSuffix(int num) {
        switch (num) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                String str = "" + num;
                
                if (str.length() == 1) {
                    return "th";
                }
                
                if (str.charAt(str.length() - 2) == '1') { //teens
                    return "th";
                }
                
                return ordinalNumberSuffix(Integer.parseInt(str.substring(str.length() - 1)));
        }
    }
    
    public static<T> boolean compareLists(List<T> listA, List<T> listB) {
        if (listA.size() != listB.size()) {
            return false;
        }
        
        for (int i = 0; i < listA.size(); i++) {
            if (listA.get(i) != listB.get(i)) {
                return false;
            }
        }
        
        return true;
    }
    
    public static<T> List<T> cloneList(List<T> input) {
        List<T> list = new ArrayList<>();
        
        for (T item : input) {
            list.add(item);
        }
        
        return list;
    }
    
    public static<T> List<T> addItem(List<T> template, T item) {
        List<T> temp = cloneList(template);
        temp.add(item);
        
        return temp;
    }
}
