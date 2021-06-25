/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.talent;

import com.google.gson.annotations.SerializedName;
import etherealtempest.fsm.FSM.MapFlowState;
import etherealtempest.info.Conveyor;

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
    
    private String desc = "";
    private Occasion occasion;
    
    public TalentCondition(String desc, Occasion occasion) {
        this.desc = desc;
        this.occasion = occasion;
    }
    
    //no description
    public TalentCondition(Occasion occasion) {
        this.occasion = occasion;
    }
    
    protected abstract boolean getCondition(Conveyor data);
    
    public Occasion getOccasion() { return occasion; }
    
    public TalentCondition occasion(Occasion O) { //set occasion
        occasion = O;
        return this;
    }
    
    public boolean checkCondition(Conveyor data, Occasion currentOccasion) {
        if (occasion == currentOccasion || occasion == Occasion.Indifferent) {
            return getCondition(data);
        } 
        
        return false;
    }
    
    public void setDescription(String description) {
        desc = description;
    }
    
    @Override
    public String toString() {
        return occasion.getDesc() + desc;
    }
    
    
    public static final TalentCondition ALWAYS_TRIGGERS = new TalentCondition("", Occasion.Indifferent) {
        @Override
        protected boolean getCondition(Conveyor data) {
            return true;
        }
    };
    
    public static final TalentCondition AlwaysTriggersOnOccasion(Occasion o) { 
        return new TalentCondition(o) {
            @Override
            protected boolean getCondition(Conveyor data) {
                return true;
            }
        };
    }
    
    //triggers if user's equipped weapon is powered by an element after combat
    public static final TalentCondition POWERED_BY_ELEMENT = new TalentCondition("if user's equipped weapon is powered by an element, ", Occasion.AfterCombat) {
        @Override
        public boolean getCondition(Conveyor data) {
            return data.getUnit().getEquippedWPN() != null && data.getUnit().getEquippedWPN().poweredByElement.length() > 2;
        }
    };
    
    
}
