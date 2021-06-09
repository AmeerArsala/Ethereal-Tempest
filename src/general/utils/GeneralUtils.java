/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class GeneralUtils {
    
    public static boolean contain(String k, String s) {
        String str = s.toLowerCase();
        String j = k.toLowerCase();
        if (str.contains(j)) {
            if (str.indexOf(j) == 0) {
                return true;
            } else {
               if (str.charAt(str.indexOf(j) - 1) == ' ') {
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
    
    public static <T> boolean compareLists(List<T> listA, List<T> listB) {
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
    
    public static <T> boolean compareArrays(T[] a1, T[] a2) {
        if (a1.length != a2.length) { return false; }
        
        for (int i = 0; i < a1.length; i++) {
            if (a1[i] != a2[i] && !a1[i].equals(a2[i])) {
                return false;
            }
        }
        
        return true;
    }
    
    public static <T> List<T> cloneList(List<T> input) {
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
    
    public static<T> T[] setAllInArray(T[] arr, T value) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = value;
        }
        
        return arr;
    }
    
    public static int[] toIntArray(final List<Integer> list) {
        int[] ret = new int[list.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = list.get(i);
        }
        return ret;
    }
    
    public static double lowest(List<Double> vals) {
        double least = vals.get(0);
        for (Double d : vals) {
            if (d < least) {
                least = d;
            }
        }
        
        return least;
    }
    
    public static double highest(List<Double> vals) {
        double most = vals.get(0);
        for (Double d : vals) {
            if (d > most) {
                most = d;
            }
        }
        
        return most;
    }
    
    public static int highestInt(List<Integer> vals) {
        int most = vals.get(0);
        for (Integer d : vals) {
            if (d > most) {
                most = d;
            }
        }
        
        return most;
    }
    
    public static int highestInt(int[] vals) {
        int most = vals[0];
        for (int d : vals) {
            if (d > most) {
                most = d;
            }
        }
        
        return most;
    }
    
    public static double closest(double num, List<Double> vals) {
        double closest = vals.get(0);
        for (Double d : vals) {
            if (Math.abs(num - d) < Math.abs(num - closest)) {
                closest = d;
            }
        }
        
        return closest;
    }
    
    public static int closestIndex(double num, List<Float> vals) {
        int closest = 0;
        for (int i = 0; i < vals.size(); i++) {
            if (Math.abs(num - vals.get(i)) < Math.abs(num - vals.get(closest))) {
                closest = i;
            }
        }
        
        return closest;
    }
    
    public static int closestIndex(double num, float[] vals) {
        int closest = 0;
        for (int i = 0; i < vals.length; i++) {
            if (Math.abs(num - vals[i]) < Math.abs(num - vals[closest])) {
                closest = i;
            }
        }
        
        return closest;
    }
    
    public static <T> List<T> createListFromElements(List<Integer> indexes, T[] ts) {
        List<T> creation = new ArrayList<>(indexes.size());
        for (int i = 0; i < indexes.size(); i++) {
            creation.set(indexes.get(i), ts[indexes.get(i)]);
        }
        
        return creation;
    }
    
    public static float[] createFloatArrayFromElements(List<Integer> indexes, float[] ts) {
        float[] creation = new float[indexes.size()];
        for (int i = 0; i < indexes.size(); i++) {
            creation[i] = ts[indexes.get(i)];
        }
        
        return creation;
    }
    
    public static <T> int getCountOfArray(T[] arr, T val) {
        int f = 0;
        for (T x : arr) {
            if (x == val || x.equals(val)) { f++; }
        }
        
        return f;
    }
    
    public static <K> List<K> fillWith(List<K> arr, List<K> replacements) {
        for (int i = 0; i < replacements.size(); i++) {
            arr.set(i, replacements.get(i));
        }
        
        return arr;
    }
    
    public static String valueDifference(int val) {
        String str = "";
        if (val > 0) {
            str += "+";
        }
        
        return str + val;
    }
    
    public static boolean isEven(float num) {
        float modular = 2f;
        while(num - ((int)num) != 0f) {
            num *= 10;
            modular *= 10;
        }
        
        return Math.abs(num) % modular == 0;
    }
    
    public static boolean isEven(double num) {
        double modular = 2.0;
        while(num - ((int)num) != 0f) {
            num *= 10;
            modular *= 10;
        }
        
        return Math.abs(num) % modular == 0;
    }
    
    public static <T> void transferListItem(List<T> origin, List<T> destination, T item) {
        origin.remove(item);
        destination.add(item);
    }
}
