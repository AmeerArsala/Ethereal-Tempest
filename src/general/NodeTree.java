/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author night
 * @param <V>
 */
public class NodeTree<V> {
    private HashMap<String[], V> tree;
    private List<String[]> keys;
    
    public NodeTree() {
        tree = new HashMap<>();
        keys = new ArrayList<>();
    }
    
    public HashMap<String[], V> getTree() {
        return tree;
    }
    
    public void putNew(List<V> origins, V value) {
        List<V> newbranch = GeneralUtils.cloneList(origins);
        newbranch.add(value);
        put(newbranch);
    }
    
    public void put(List<V> directory) {
        String[] identifier = createPath(directory);
        keys.add(identifier);
        tree.put(identifier, directory.get(directory.size() - 1));
    }
    
    public V getValueByOrigins(List<V> origins) {
        String[] key = getEquivalentKey(createPath(origins));
        return key != null ? tree.get(key) : null;
    }
    
    public List<V> getBranchChildren(List<V> origins) {
        List<V> children = new ArrayList<>();
        String[] path = createPath(origins);
        
        keys.stream().filter((key) -> (arrayHasAncestor(key, path, true))).forEachOrdered((key) -> {
            children.add(tree.get(key));
        });
        
        return children;
    }
    
    public List<V> getAllChildren(List<V> origins) {
        List<V> children = new ArrayList<>();
        
        String[] path = createPath(origins);
        
        keys.stream().filter((key) -> (arrayHasAncestor(key, path, false))).forEachOrdered((key) -> {
            children.add(tree.get(key));
        });
        
        return children;
    }
    
    private String[] createPath(List<V> origins) {
        String[] ids = new String[origins.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = origins.get(i).toString();
        }
        
        return ids;
    }
    
    private boolean arrayHasAncestor(String[] key, String[] path, boolean immediate) {
        if (immediate && key.length - path.length != 1) { //immediate children
            return false;
        }
        
        for (int i = 0; i < path.length; i++) {
            if (!path[i].equals(key[i])) {
                return false;
            }
        }
        
        return true;
    }
    
    private String[] getEquivalentKey(String[] key) {
        for (String[] k : keys) {
            if (GeneralUtils.compareArrays(key, k)) {
                return k;
            }
        }
        
        return null;
    }
    
    
}
