/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.tools.input;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import general.utils.wrapper.Duo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author night
 * @param <E> this is for the type of enum or key you want to call it with; can also be string
 */
public abstract class ComplexInputReader<E extends Enum> {
    private final boolean allowErgonomics;
    
    protected float heldTimeCheckpoint = 1f; //1 second by default
    
    private BiMap<E, String> mapping = HashBiMap.create();
    private final HashMap<E, Float> heldInputs = new HashMap<>();
    
    public ComplexInputReader(boolean allowErgonomics) {
        this.allowErgonomics = allowErgonomics;
    }
    
    public ComplexInputReader(boolean allowErgonomics, float heldTimeCheckpoint) {
        this.allowErgonomics = allowErgonomics;
        this.heldTimeCheckpoint = heldTimeCheckpoint;
    }
    
    public ComplexInputReader(boolean allowErgonomics, BiMap mapping) {
        this.allowErgonomics = allowErgonomics;
        this.mapping = mapping;
    }
    
    public ComplexInputReader(boolean allowErgonomics, float heldTimeCheckpoint, BiMap mapping) {
        this.allowErgonomics = allowErgonomics;
        this.heldTimeCheckpoint = heldTimeCheckpoint;
        this.mapping = mapping;
    }

    public abstract void protocolProcedure(E inputHeld);
    
    public ComplexInputReader link(E pointer, String actual) {
        mapping.put(pointer, actual);
        return this;
    }
    
    public ComplexInputReader link(Duo<E, String>... mappings) {
        for (Duo<E, String> map : mappings) {
            mapping.put(map.first, map.second);
        }
        
        return this;
    }
    
    public String get(E key) {
        return mapping.get(key);
    }
    
    public E getPointer(String key) {
        return mapping.inverse().get(key);
    }
    
    public Float getHeldTime(E key) {
        return heldInputs.get(key);
    }
    
    public Float getHeldTime(String key) {
        return heldInputs.get(mapping.inverse().get(key));
    }
    
    public boolean ergonomicsAllowed() { return allowErgonomics; }
    
    public void obtainInput(String name, float tpf, boolean keyPressed) {
        if (keyPressed) {
            if (!heldInputs.containsKey(mapping.inverse().get(name))) {
                float normal = 0f;
                if (allowErgonomics && anyKeyHeldOver(heldTimeCheckpoint)) { normal = heldTimeCheckpoint; }
                
                E key = mapping.inverse().get(name);
                if (key != null) {
                    heldInputs.put(key, normal);
                }
            }
        } else {
            if (heldInputs.containsKey(mapping.inverse().get(name))) {
                heldInputs.remove(mapping.inverse().get(name));
            }
        }
    }
    
    public void update(float tpf) {
        heldInputs.keySet().forEach((inputHeld) -> {
            heldInputs.replace(inputHeld, heldInputs.get(inputHeld) + tpf);
            protocolProcedure(inputHeld);
        });
    }
    
    public void clear() {
        heldInputs.keySet().forEach((inputHeld) -> {
            heldInputs.replace(inputHeld, 0f);
        });
    }
    
    public boolean anyKeyHeldOver(float seconds) {
        return heldInputs.keySet().stream().anyMatch((pointer) -> (heldInputs.get(pointer) >= seconds));
    }
    
    public boolean anyKeyPressed() { return heldInputs.keySet().size() > 0; }
    public int amountOfKeysPressed() { return heldInputs.keySet().size(); }
    
    public boolean keyIsHeld(E key) {
        return heldInputs.containsKey(key);
    }
    
    public boolean keyIsHeld(String key) {
        return heldInputs.containsKey(mapping.inverse().get(key));
    }
    
    public List<E> getHeldInputs(float seconds) { //gets held inputs that are >= parameter's seconds
        List<E> inputs = new ArrayList<>();
        
        heldInputs.keySet().forEach((pointer) -> {
            if (heldInputs.get(pointer) >= seconds) {
                inputs.add(pointer);
            }
        });
        
        return inputs;
    }
    
    public List<E> getHeldInputsUnder(float seconds) { //gets held inputs that are <= seconds
        List<E> inputs = new ArrayList<>();
        
        heldInputs.keySet().forEach((pointer) -> {
            if (heldInputs.get(pointer) <= seconds) {
                inputs.add(pointer);
            }
        });
        
        return inputs;
    }
    
    public List<String> getHeldInputsAsString(float seconds) {
        List<String> inputs = new ArrayList<>();
        
        getHeldInputs(seconds).forEach((pointer) -> {
            inputs.add(mapping.get(pointer));
        });
        
        return inputs;
    }
    
    public List<String> getHeldInputsUnderAsString(float seconds) {
        List<String> inputs = new ArrayList<>();
        
        getHeldInputsUnder(seconds).forEach((pointer) -> {
            inputs.add(mapping.get(pointer));
        });
        
        return inputs;
    }
    
    public String heldPointers() {
        if (!anyKeyPressed()) { return "None"; }
        E[] keys = ((E[])heldInputs.keySet().toArray());
        
        String str = keys[0].toString() + ": " + heldInputs.get(keys[0]);
        for (int i = 1; i < keys.length; i++) {
            str += ", " + keys[i].toString() + ": " + heldInputs.get(keys[i]);
        }

        return str;
    }
    
    public String heldMappings() {
        if (!anyKeyPressed()) { return "None"; }

        String[] keys = new String[heldInputs.size()];
        E[] pts = ((E[])heldInputs.keySet().toArray());
        
        for (int i = 0; i < pts.length; i++) {
            keys[i] = mapping.get(pts[i]);
        }
        
        String str = keys[0] + ": " + heldInputs.get(pts[0]);
        
        for (int i = 1; i < keys.length; i++) {
            str += ", " + keys[i] + ": " + heldInputs.get(pts[i]);
        }

        return str;
    }
    
}
