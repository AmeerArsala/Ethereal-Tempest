/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.data.event;

import battle.data.event.Strike;
import battle.data.forecast.SingularForecast;
import battle.data.participant.Combatant;
import etherealtempest.info.Conveyor;
import fundamental.stats.BaseStat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class StrikeTheater {
    public enum Participant {
        Striker(), 
        Victim();
        
        private Participant opposite;
        private Participant() {}
        
        static {
            Striker.opposite = Victim;
            Victim.opposite = Striker;
        }
        
        public Participant getOpposite() { return opposite; }
    }
    
    private final List<Strike> strikes;
    private final List<Strike> actualStrikes; //only contains the strikes that are actually carried out
    private final List<Participant> A_Roles, B_Roles; //A represents "participant A", while B represents "participant B"
    
    private class StrikeEventContributor {
        private final SingularForecast forecast;
        private final Conveyor context;
        private final List<Participant> contributorRoles, opponentRoles;
        private int BP;
        
        public StrikeEventContributor(SingularForecast sfc, Conveyor data, List<Participant> roles, List<Participant> foeRoles, boolean isInitiator) {
            forecast = sfc;
            context = data;
            contributorRoles = roles;
            opponentRoles = foeRoles;
            BP = forecast.getInitialBP(isInitiator);
        }
        
        public void attemptContribution() {
            if (BP > 0) { //this check is NOT redundant
                List<Strike> eventStrikes = forecast.createStrike(context).getAllStrikesFromThis();
                for (Strike strike : eventStrikes) {
                    strikes.add(strike);
                        
                    contributorRoles.add(Participant.Striker);
                    opponentRoles.add(Participant.Victim);
                }
                
                BP -= forecast.getBPcostPerHit();
            }
        }
        
        public boolean shouldContinueFighting() {
            return forecast.continueFightingCondition(BP, context);
        }
    }
    
    public StrikeTheater(SingularForecast initiator, SingularForecast receiver, Conveyor data) {
        strikes = new ArrayList<>();
        actualStrikes = new ArrayList<>();
        A_Roles = new ArrayList<>();
        B_Roles = new ArrayList<>();
        
        StrikeEventContributor initiatorContributor = new StrikeEventContributor(initiator, data, A_Roles, B_Roles, true);
        StrikeEventContributor receiverContributor = new StrikeEventContributor(receiver, data, B_Roles, A_Roles, false);

        for (int k = 0; initiatorContributor.shouldContinueFighting() || receiverContributor.shouldContinueFighting(); k++) {
            if (k % 2 == 0) { //even; initiator's strike
                initiatorContributor.attemptContribution();
            } else { //odd; receiver's strike
                receiverContributor.attemptContribution();
            }
        }
        
        for (int i = 0; i < strikes.size(); ++i) {
            actualStrikes.add(strikes.get(i));
            if (getParticipantHPAfter(Participant.Victim, i) == 0) { //if someone died, stop the loop
                break;
            }
        }
        
        System.out.println("strikes size: " + strikes.size());
        System.out.println("actualStrikes size: " + actualStrikes.size());
    }
    
    public List<Strike> getStrikes() {
        return strikes;
    }
    
    public List<Strike> getActualStrikes() {
        return actualStrikes;
    }
    
    public Strike getStrike(int index) {
        return strikes.get(index);
    }
    
    public Strike getActualStrike(int index) {
        return actualStrikes.get(index);
    }
    
    public Combatant getParticipant(Participant participant, int strikeIndex) {
        if (participant == Participant.Striker) {
            return strikes.get(strikeIndex).getStriker().combatant;
        }
        
        if (participant == Participant.Victim) {
            return strikes.get(strikeIndex).getVictim().combatant;
        }
        
        return null;
    }
    
    public Participant getParticipantRole(Combatant participant, int strikeIndex) {
        Combatant striker = strikes.get(strikeIndex).getStriker().combatant, victim = strikes.get(strikeIndex).getVictim().combatant;
        
        if (participant.equals(striker)) {
            return Participant.Striker;
        }
        
        if (participant.equals(victim)) {
            return Participant.Victim;
        } 
        
        return null;
    }
    
    public List<Strike> getActualStrikesFrom(int strikeIndex) {
        List<Strike> actual = new ArrayList<>();
        int HP = getParticipantHPBefore(Participant.Victim, strikeIndex);
        
        if (HP == 0) {
            return actual;
        }
        
        Strike strike = strikes.get(strikeIndex);
        actual.add(strike);
        for (Strike stk : strike.getExtraStrikes()) {
            actual.add(stk);
            HP -= stk.getDamage();
            if (HP <= 0) {
                break;
            }
        }
        
        return actual;
    }
    
    public final int getParticipantHPBefore(Participant participant, int strikeIndex) {
        if (A_Roles.get(strikeIndex) == participant) {
            return calculateHPOfBefore(A_Roles, strikeIndex);
        }
        
        if (B_Roles.get(strikeIndex) == participant) {
            return calculateHPOfBefore(B_Roles, strikeIndex);
        }
        
        return -1;
    }
    
    public final int getParticipantHPAfter(Participant participant, int strikeIndex) {
        if (A_Roles.get(strikeIndex) == participant) {
            return calculateHPOfAfter(A_Roles, strikeIndex);
        }
        
        if (B_Roles.get(strikeIndex) == participant) {
            return calculateHPOfAfter(B_Roles, strikeIndex);
        }
        
        return -1;
    }
    
    public final int getParticipantHPBefore(Combatant participant, int strikeIndex) {
        return getParticipantHPBefore(getParticipantRole(participant, strikeIndex), strikeIndex);
    }
    
    public final int getParticipantHPAfter(Combatant participant, int strikeIndex) {
        return getParticipantHPAfter(getParticipantRole(participant, strikeIndex), strikeIndex);
    }
     
    //calculates HP AFTER the strikeIndex (after it happens)
    private int calculateHPOfAfter(List<Participant> roles, int strikeIndex) {
        int startingStrikeIndex = 0;
        int HP = getParticipant(roles.get(startingStrikeIndex), startingStrikeIndex).getBaseStat(BaseStat.CurrentHP);
        for (int i = 0; i <= strikeIndex; ++i) {
            if (roles.get(i) == Participant.Victim) {
                HP -= strikes.get(i).getDamage();
            }
        }
        
        return HP < 0 ? 0 : HP;
    }
    
    //calculates HP at the Start of the strikeIndex (before it happens)
    private int calculateHPOfBefore(List<Participant> roles, int strikeIndex) {
        int startingStrikeIndex = 0;
        int HP = getParticipant(roles.get(startingStrikeIndex), startingStrikeIndex).getBaseStat(BaseStat.CurrentHP);
        for (int i = 0; i < strikeIndex; ++i) {
            if (roles.get(i) == Participant.Victim) {
                HP -= strikes.get(i).getDamage();
            }
        }
        
        return HP < 0 ? 0 : HP;
    }
}
