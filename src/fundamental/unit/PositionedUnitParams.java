/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.unit;

/**
 *
 * @author night
 */
public class PositionedUnitParams {
    public boolean hasStashAccess = false;
    public boolean isLeader = false;
    public boolean isBoss = false;
    
    public PositionedUnitParams() {}
    
    public PositionedUnitParams(boolean hasStashAccess, boolean isLeader, boolean isBoss) {
        this.hasStashAccess = hasStashAccess;
        this.isLeader = isLeader;
        this.isBoss = isBoss;
    }
    
    public PositionedUnitParams hasStashAccess(boolean hasStashAccess) {
        this.hasStashAccess = hasStashAccess;
        return this;
    }
    
    public PositionedUnitParams isLeader(boolean isLeader) {
        this.isLeader = isLeader;
        return this;
    }
    
    public PositionedUnitParams isBoss(boolean isBoss) {
        this.isBoss = isBoss;
        return this;
    }
    
    /*
    public boolean hasStashAccess() {
    
    }
    
    public boolean isLeader() {
        return isLeader;
    }
    
    public boolean isBoss() {
        return isBoss;
    }
    
    public void setHasStashAccess(boolean access) {
        hasStashAccess = access;
    }
    
    public void setIsLeader(boolean leader) {
        isLeader = leader;
    }
    
    public void setIsBoss(boolean boss) {
        isBoss = boss;
    }
    */
}
