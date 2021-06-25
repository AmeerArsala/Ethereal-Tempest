/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.tools.input;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 * @param <I> input type; could be an enum, String, etc.
 */
public abstract class AdvancedInputReader<I> {
    private final List<InputGroup> groups = new ArrayList<>();
    
    public AdvancedInputReader(List<InputGroup> inputGroups) {
        groups.addAll(inputGroups);
    }
    
}
