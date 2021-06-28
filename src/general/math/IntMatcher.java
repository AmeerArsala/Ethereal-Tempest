/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.math;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 * 
 * use for parsing parentheses
 */
public class IntMatcher { //all unique x and y per instance; only 1 x can match with only 1 y
    private final List<IntPair> pairs;
    
    public IntMatcher() {
        pairs = new ArrayList<>();
    }
    
    private IntMatcher(List<IntPair> pairs) {
        this.pairs = pairs;
    }
     
    public boolean containsX(int x) {
        return pairs.stream().anyMatch((IP) -> (IP.isXSet() && IP.getX() == x));
    }
    
    public boolean containsY(int y) {
        return pairs.stream().anyMatch((IP) -> (IP.isYSet() && IP.getY() == y));
    }
    
    public boolean containsXY(int x, int y) {
        Integer yOut = getYByX(x), xOut = getXByY(y);
        return xOut != null && xOut == x && yOut != null && yOut == y;
    }
    
    public Integer getYByX(int x) {
        for (IntPair IP : pairs) {
            if (IP.isXSet() && IP.getX() == x) {
                return IP.isYSet() ? IP.getY() : null;
            }
        }
        
        return null;
    }
    
    public Integer getXByY(int y) {
        for (IntPair IP : pairs) {
            if (IP.isYSet() && IP.getY() == y) {
                return IP.isXSet() ? IP.getX() : null;
            }
        }
        
        return null;
    }
    
    public IntPair getPairByX(int x) {
        for (IntPair IP : pairs) {
            if (IP.isXSet() && IP.getX() == x) {
                return IP;
            }
        }
        
        return null;
    }
    
    public IntPair getPairByY(int y) {
        for (IntPair IP : pairs) {
            if (IP.isYSet() && IP.getY() == y) {
                return IP;
            }
        }
        
        return null;
    }
    
    public IntPair get(int i) {
        return i >= 0 && i < pairs.size() ? pairs.get(i) : null;
    }
    
    public void addPair(IntPair pair) {
        pairs.add(pair);
    }
    
    /**
     *
     * @param x the first integer value of the pair
     * if the pair at the latest index already has had x set, or if the list is empty, adds a new IntPair with the x value set to the parameter x value
     * if the pair at the latest index has not had x set yet, sets it to the parameter x value
     */
    public void addX(int x) {
        if (pairs.isEmpty() || pairs.get(pairs.size() - 1).isXSet()) {
            pairs.add(new IntPair().setX(x));
        } else {
            pairs.get(pairs.size() - 1).setX(x);
        }
    }
    
    /**
     *
     * @param y the first integer value of the pair
     * sets the latest index that has not had y set to the parameter y value
     * if all pairs in the list had already had y set, or if the list is empty, adds a new IntPair with the y value set to the parameter y value
     */
    public void addY(int y) {
        if (!pairs.isEmpty()) {
            for (int i = pairs.size() - 1; i >= 0; --i) {
                if (!pairs.get(i).isYSet()) {
                    pairs.get(i).setY(y);
                    return;
                }
            }
        }
        
        pairs.add(new IntPair().setY(y));
    }
    
    public void removeX(int x) {
        IntPair pair = getPairByX(x);
        if (pair != null) {
            pair.removeX();
        }
    }
    
    public void removeY(int y) {
        IntPair pair = getPairByY(y);
        if (pair != null) {
            pair.removeY();
        }
    }
    
    public void removeXY(int x, int y) {
        IntPair pair = getPairByX(x);
        if (pair != null) {
            pair.removeX();
            pair.removeY();
        }
    }
    
    public void removePair(int x, int y) {
        IntPair pair = getPairByX(x);
        if (pair != null) {
            pairs.remove(pair);
        }
    }
    
    public IntMatcher confine(int x, int y) { // [x, y); like a substring
        List<IntPair> confinedPairs = new ArrayList<>();
        for (int i = x; i < y; ++i) {
            IntPair pair = getPairByX(i);
            if (pair != null) {
                confinedPairs.add(pair.setX(i - x).setY(pair.getY() - x));
            }
        }

        return new IntMatcher(confinedPairs);
    }
    
}
