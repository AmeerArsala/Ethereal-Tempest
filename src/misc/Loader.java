/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package misc;

import com.jme3.math.ColorRGBA;
import com.simsilica.lemur.ProgressBar;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import general.visual.VisualTransition.Progress;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class Loader {
    private Progress progress = Progress.Fresh;
    
    private boolean[] barsComplete;
    private int[] partsFinished;
    
    private final ProgressBar display;
    private final List<Runnable[]> tasks;
    private final List<ColorRGBA> barColors;
    
    public Loader(List<Runnable[]> tasks) {
        this.tasks = tasks;
        
        barColors = new ArrayList<>();
        barsComplete = new boolean[tasks.size()];
        partsFinished = new int[tasks.size()];
        for (int i = 0; i < barsComplete.length; i++) {
            barsComplete[i] = false;
            partsFinished[i] = 0;
            barColors.add(ColorRGBA.randomColor());
        }
        
        display = new ProgressBar();
        display.setProgressPercent(0);
        display.setMessage("0%");
        
        progress = Progress.Progressing;
    } 
    
    public void update(float tpf) {
        if (progress == Progress.Progressing) {
            int currentTask = currentTaskIndex();

            //body
            tasks.get(currentTask)[partsFinished[currentTask]].run();
            
            //closing to body
            partsFinished[currentTask]++;
            
             //update progress display
             ((QuadBackgroundComponent)display.getValueIndicator().getBackground()).setColor(barColors.get(currentTask));
             double percentComplete = ((double)partsFinished[currentTask]) / tasks.get(currentTask).length;
             display.setProgressPercent(percentComplete);
             display.setMessage(((int)(100 * percentComplete)) + "%");
            
            if (percentComplete >= 1.0) {
                barsComplete[currentTask] = true;
            }
            
            //tail
            if (currentTaskIndex() == -1) {
                progress = Progress.Finished;
            }
            
            display.updateLogicalState(tpf);
            display.updateGeometricState();
        }
    }
    
    private int currentTaskIndex() {
        for (int i = 0; i < barsComplete.length; i++) {
            if (!barsComplete[i]) { return i; }
        }
        return -1;
    }
    
    public ProgressBar getBar() { return display; }
    public List<Runnable[]> getTasks() { return tasks; }
    
}
