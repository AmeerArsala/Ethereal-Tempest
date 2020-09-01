/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.parse;

import battle.Battle;
import battle.Battle.ImpactType;
import battle.JobClass;
import com.google.gson.Gson;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author night
 */
public class AttackConfig {
    private String spritesheet;
    private int percentWidth;
    private int percentHeight;
    private int percentX;
    private int percentY;
    private AttackTypeColumn[] attack;
    private AttackTypeColumn[] attack_and_followup;
    private AttackTypeColumn[] critical;
    private AttackTypeColumn[] finisher;
    
    public AttackConfig(String spritesheet, int percentWidth, int percentHeight, int percentX, int percentY, AttackTypeColumn[] attack, AttackTypeColumn[] attack_and_followup, AttackTypeColumn[] critical, AttackTypeColumn[] finisher) {
        this.spritesheet = spritesheet;
        this.percentWidth = percentWidth;
        this.percentHeight = percentHeight;
        this.percentX = percentX;
        this.percentY = percentY;
        this.attack = attack;
        this.attack_and_followup = attack_and_followup;
        this.critical = critical;
        this.finisher = finisher;
    }
    
    public String getSpritesheet() { return spritesheet; }
    public int getPercentWidth() { return percentWidth; }
    public int getPercentHeight() { return percentHeight; }
    public int getPercentX() { return percentX; }
    public int getPercentY() { return percentY; }
    public AttackTypeColumn[] getAttack() { return attack; }
    public AttackTypeColumn[] getAttackAndFollowup() { return attack_and_followup; }
    public AttackTypeColumn[] getCritical() { return critical; }
    public AttackTypeColumn[] getFinisher() { return finisher; }
    
    /* The order of types will ALWAYS be:
        1.) attack
        2.) attack_and_followup
        3.) critical
        4.) finisher
    */
    
    private int greatestColumnLengthInFrames(AttackTypeColumn[] ATCs) {
        int max = 0;
        for (AttackTypeColumn ATC : ATCs) {
            if (ATC.getFrames() > max) {
                max = ATC.getFrames();
            }
        }
        
        return max;
    }
    
    public int getRowCount() { // row count = greatest number of frames in any column
        int[] possibleMaxes = {
            greatestColumnLengthInFrames(attack),
            greatestColumnLengthInFrames(attack_and_followup),
            greatestColumnLengthInFrames(critical),
            greatestColumnLengthInFrames(finisher)
        };
        
        int actualMax = 0;
        for (int possible : possibleMaxes) {
            if (possible > actualMax) {
                actualMax = possible;
            }
        }
        
        return actualMax;
    }
    
    public int getColumnCount() {
        return attack.length + attack_and_followup.length + critical.length + finisher.length;
    }
    
    public AttackTypeColumn[] getColumnsByName(String name) {
        switch (name) {
            case "attack":
                return attack;
            case "attack_and_followup":
                return attack_and_followup;
            case "critical":
                return critical;
            case "finisher":
                return finisher;
        }
        return null;
    }
    
    public int sumOfAllFramesByType(AttackTypeColumn[] ATC) {
        int amt = 0;
        for (AttackTypeColumn type : ATC) {
            amt += type.getFrames();
        }
        
        return amt;
    }
    
    public int framesToFirstFullImpactByType(AttackTypeColumn[] ATC, int typeIndex) {
        int lengths = 0;
        
        for (AttackTypeColumn type : ATC) {
            lengths += type.getFullImpacts().length;
        }
        
        if (lengths > 1) {
            int closest = 9999, i = CurrentColumnIndex(ATC, typeIndex), freezeIndex = 0;
            for (int k = 0; k < ATC[i].getFullImpacts().length; k++) {
                if (ATC[i].getFullImpacts()[k] - typeIndex > 0 && ATC[i].getFullImpacts()[k] - typeIndex < closest - typeIndex) {
                    closest = ATC[i].getFullImpacts()[k];
                    freezeIndex = k;
                }
            }
            return closest == ATC[i].getFullImpacts()[ATC[i].getFullImpacts().length - 1] ? sumOfAllFramesByType(ATC) : ATC[i].getFreezeFrames()[freezeIndex] + 1;
        }
        
        return sumOfAllFramesByType(ATC);
    }
    
    public int typeIndexToGridPosition(int ti, AttackTypeColumn[] type) { // { position, row }
        List<AttackTypeColumn[]> fullSheet = Arrays.asList(attack, attack_and_followup, critical, finisher);
        int[] columnLengths = new int[getColumnCount()];
        int ind = 0;
        int row = -1, column = -1;
        boolean columnSet = false;
        for (AttackTypeColumn[] section : fullSheet) {
            if (section == type && !columnSet) {
                column = ind + CurrentColumnIndex(type, ti);
                columnSet = true;
            }
            for (AttackTypeColumn col : section) {
                columnLengths[ind] = col.getFrames();
                
                if (ind == column && columnSet) {
                    int cindex = ti, ccin = CurrentColumnIndex(type, ti);
                    if (ccin > 0) {
                        cindex -= sumOfArraySector(columnLengths, ind - ccin, ind - 1);
                    }
                    row = cindex;
                }
                
                ind++;
            }
        }
        
        return (column + (row * getColumnCount()));
    }
    
    private static int sumOfArraySector(int[] arr, int start, int end) {
        int sum = 0;
        for (int i = start; i <= end; i++) {
            sum += arr[i];
        }
        return sum;
    }
    
    public ImpactType setSpriteFrame(Material mat, ImpactType current, String mode, int index) {
        
        AttackTypeColumn[] ATC = getColumnsByName(mode);
        
        //index is already a TypeIndex so we don't have to worry about that for now
        //System.out.println(index + " --> " + typeIndexToGridPosition(index, ATC));
        
        mat.setFloat("Position",typeIndexToGridPosition(index, ATC));
        
        if (current != null) {
            if (ArrayHas(ATC[CurrentColumnIndex(ATC, index)].getFullImpacts(), index)) {
                //System.out.println("full impact!");
                return ImpactType.All;
            } else if (ArrayHas(ATC[CurrentColumnIndex(ATC, index)].getSoundOnlyImpacts(), index)) {
                //System.out.println("sound only!");
                return ImpactType.SoundOnly;
            }
        }
        
        return current;
    }
        
    public static boolean ArrayHas(int[] arr, int target) {
        for (int a : arr) {
            if (a == target) {
                return true;
            }
        }
        return false;
    }
    
    private int TypeIndex(int overallIndex) {
        if (overallIndex >= 0) {
            List<AttackTypeColumn[]> fullSheet = Arrays.asList(attack, attack_and_followup, critical, finisher);
            int building = 0;
            for (AttackTypeColumn[] type : fullSheet) {
                int sum = 0;
                for (AttackTypeColumn col : type) { sum += col.getFrames(); }
            
                if (overallIndex - building < sum) {
                    return overallIndex - building; 
                }
            
                building += sum;
            }
        }
        return -1;
    }
    
    //the method below is used to get the index of the preferred AttackTypeColumn in the array based on the TypeIndex
    private static int CurrentColumnIndex(AttackTypeColumn[] type, int index) { //parameter index = TypeIndex(overallIndex)
        if (index >= 0) {
            for (int i = 0; i < type.length; i++) {
                if (index < type[i].getFrames()) {
                    return i;
                }
                index -= type[i].getFrames();
            }
        }
        return -1;
    }
    
    private class AttackTypeColumn {
        private int column;
        private int frames;
        private int[] SoundOnlyImpacts;
        private int[] FullImpacts;
        private int[] FreezeFrames;
    
        public AttackTypeColumn(int column, int frames, int[] SoundOnlyImpacts, int[] FullImpacts, int[] FreezeFrames) {
            this.column = column;
            this.frames = frames;
            this.SoundOnlyImpacts = SoundOnlyImpacts;
            this.FullImpacts = FullImpacts;
            this.FreezeFrames = FreezeFrames;
        }
        
        public int getColumn() { return column; }
        public int getFrames() { return frames; }
        
        public int[] getSoundOnlyImpacts() { return SoundOnlyImpacts; }
        public int[] getFullImpacts() { return FullImpacts; }
        public int[] getFreezeFrames() { return FreezeFrames; }
        
        @Override
        public String toString() {
            return 
                    "     column: " + column + "\n  "
                    + "   frames: " + frames + "\n  "
                    + "   SoundOnlyImpacts: " + Arrays.toString(SoundOnlyImpacts) + "\n  "
                    + "   FullImpacts: " + Arrays.toString(FullImpacts) + "\n  "
                    + "   FreezeFrames: " + Arrays.toString(FreezeFrames);
        }
    }
    
    @Override
    public String toString() {
        return  "spritesheet: " + spritesheet + "\n"
                + "attack:\n     "
                + Arrays.toString(attack) + "\nattack_and_followup:\n     " 
                + Arrays.toString(attack_and_followup) + "\ncritical:\n     " 
                + Arrays.toString(critical) + "\nfinisher:\n     "
                + Arrays.toString(finisher);
    }
}