/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation.config.action;

/**
 *
 * @author night
 */
public class ActionFrame {
    private Integer column; //optional
    private int frame;
    private String soundPath;
    private Changes userChanges;
    private Changes opponentChanges;
    
    public ActionFrame(Integer column, int frame, String soundPath, Changes userChanges, Changes opponentChanges) {
        this.column = column;
        this.frame = frame;
        this.soundPath = soundPath;
        this.userChanges = userChanges;
        this.opponentChanges = opponentChanges;
    }
    
    public int getFrame() {
        return frame;
    }
    
    public Integer getColumn() {
        return column;
    }
    
    public String getSoundPath() {
        return soundPath;
    }
    
    public Changes getUserChanges() {
        return userChanges;
    }
    
    public Changes getOpponentChanges() {
        return opponentChanges;
    }
    
    public void initializeAllChanges() {
        if (userChanges != null) {
            userChanges.initializeAll();
        }
        
        if (opponentChanges != null) {
            opponentChanges.initializeAll();
        }
    }
}
