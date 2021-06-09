/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.talent;

import battle.data.Strike;
import battle.participant.Combatant;
import etherealtempest.info.Conveyor;
import fundamental.tool.Tool.ToolType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public abstract class BattleTalent extends Talent { //proc talent
    private final boolean offensive;
    
    //use this for custom description
    public BattleTalent(String talentname, String lore, String description, String imgPath, List<TalentConcept> tc, boolean offensive) {
        super(talentname, ToolType.Attack, lore, description, imgPath, tc);
        this.offensive = offensive;
    }
    
    //use this for auto-generated description
    public BattleTalent(String talentname, ToolType t_type, String lore, String imgPath, List<TalentConcept> tc, boolean offensive) {
        super(talentname, t_type, lore, imgPath, tc);
        this.offensive = offensive;
    }
    
    //used to shorten constructor for BattleTalents that aren't actually classified as them in-game
    public BattleTalent(List<TalentConcept> tc, boolean offensive) {
        super(ToolType.Attack, tc);
        this.offensive = offensive;
    }
    
    public boolean isOffensive() { return offensive; }
    
    public abstract boolean doesTrigger(Conveyor info, Combatant striker, Combatant victim);
    public abstract void applyEffect(Conveyor info, Combatant striker, Combatant victim);
    
    //OVERRIDE THIS WHEN NEEDED
    public int recalculateDamage(int baseDamage, Conveyor info, Combatant striker, Combatant victim) {
        return baseDamage;
    }
    
    //OVERRIDE THIS WHEN NEEDED
    protected int extraHits(Conveyor info, Combatant striker, Combatant victim) { //extra hits on strike
        return 0;
    }
    
    public final List<Strike> calculateExtraHits(Conveyor info, Combatant striker, Combatant victim) {
        List<Strike> extra = new ArrayList<>();
        int extraHits = extraHits(info, striker, victim);
        for (int i = 0; i < extraHits; ++i) {
            extra.add(Strike.SimpleStrike(striker, victim, false));
        }
        
        return extra;
    }
    
}
