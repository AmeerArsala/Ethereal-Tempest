/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.tools.input;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author night
 * @param <I> input type; could be an enum, String, etc.
 */
public abstract class InputGroup<I> { //example: arrow keys, WASD, etc.
    private final Map<I, InputReactor> mappings = new HashMap<>();
    private final I[] inputs;
    private final boolean allowHeldTimeToCarryOver;
    
    public InputGroup(I[] inputs, boolean allowHeldTimeToCarryOver) {
        this.inputs = inputs;
        this.allowHeldTimeToCarryOver = allowHeldTimeToCarryOver;
    }
    
    public abstract void protocol(I input, boolean keyPressed);
}
