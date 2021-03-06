/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.talent;

import com.google.gson.annotations.SerializedName;
import etherealtempest.FSM.MapFlowState;
import etherealtempest.info.Conveyer;

/**
 *
 * @author night
 */
public abstract class TalentCondition {
    public enum Occasion {
        @SerializedName("BeforeCombat") BeforeCombat("At the start of combat, "),
        @SerializedName("AfterCombat") AfterCombat("After combat, "),
        @SerializedName("DuringCombat") DuringCombat("During combat, "),
        @SerializedName("StartOfTurn") StartOfTurn("At the start of their turn, "), 
        @SerializedName("StartOfEnemyTurn") StartOfEnemyTurn("At the start of an enemy turn, "),
        @SerializedName("Indifferent") Indifferent("");
        
        private final String descr;
        private Occasion(String description) {
            descr = description;
        }
        
        public String getDesc() { return descr; }
        
        public static Occasion correspondingOccasion(MapFlowState MFS) {
            switch (MFS) {
                case PreBattle:
                    return BeforeCombat;
                case DuringBattle:
                    return DuringCombat;
                case PostBattle:
                    return AfterCombat;
                case BeginningOfTurn:
                    return StartOfTurn;
                default:
                    return Indifferent;
            }
        }
    }
    
    private String desc;
    private Occasion occasion;
    
    public TalentCondition(String desc, Occasion occasion) {
        this.desc = occasion.getDesc() + desc;
        this.occasion = occasion;
    }
    
    protected abstract boolean getCondition(Conveyer data);
    
    public Occasion getOccasion() { return occasion; }
    
    public TalentCondition occasion(Occasion O) { //set occasion
        occasion = O;
        desc = occasion.getDesc() + desc;
        return this;
    }
    
    public boolean checkCondition(Conveyer data, Occasion currentOccasion) {
        if (occasion == currentOccasion || occasion == Occasion.Indifferent) {
            return getCondition(data);
        } 
        
        return false;
    }
    
    @Override
    public String toString() {
        return desc;
    }
    
    public static final TalentCondition ALWAYS_TRIGGERS = new TalentCondition("", Occasion.Indifferent) {
        @Override
        protected boolean getCondition(Conveyer data) {
            return true;
        }
    };
    
    public static final TalentCondition AlwaysTriggersOnOccasion(Occasion o) { 
        return new TalentCondition("", o) {
            @Override
            protected boolean getCondition(Conveyer data) {
                return true;
            }
        };
    }
    
    //triggers if user's equipped weapon is powered by an element after combat
    public static final TalentCondition POWERED_BY_ELEMENT = new TalentCondition("if user's equipped weapon is powered by an element, ", Occasion.AfterCombat) {
        @Override
        public boolean getCondition(Conveyer data) {
            return data.getUnit().getEquippedWPN() != null && data.getUnit().getEquippedWPN().poweredByElement.length() > 2;
        }
    };
    
    
}
