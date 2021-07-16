/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.info;

import general.procedure.RequestDealer;
import battle.data.event.StrikeParticipant;
import battle.data.participant.Combatant;
import battle.data.participant.BattleRole;
import com.jme3.asset.AssetManager;
import java.util.ArrayList;
import java.util.List;
import maps.data.Objective;
import maps.layout.Coords;
import maps.layout.occupant.control.Cursor;
import maps.layout.occupant.MapEntity;
import maps.layout.occupant.character.TangibleUnit;

/**
 *
 * @author night
 */
public class Conveyor {
        private TangibleUnit user, enemy, other; //at least one of these is usually required
        private Combatant initiator, receiver; //usually required in mapFlow
        private StrikeParticipant striker, victim;
        private ArrayList<TangibleUnit> allUnits; //required in mapFlow
        private List<MapEntity> mapEntities; //required in mapFlow
        private AssetManager assetmanager;
        private Cursor cursor; //required in mapFlow
        private int turnNumber; //required in mapFlow
        private Objective currentObjective; //required in mapFlow
        private Coords coords;
        private int layer;
        private RequestDealer mapFlowRequestTaker;
        
        public Conveyor() {}
        
        public Conveyor(TangibleUnit unit) {
            user = unit;
        }
        
        public void swapUnits() {
            TangibleUnit temp = user;
            user = enemy;
            enemy = user;
        }
        
        public Conveyor createCombatants() {
            initiator = new Combatant(user, BattleRole.Initiator);
            receiver = new Combatant(enemy, BattleRole.Receiver);
            
            return this;
        }
        
        public BattleRole battleRoleFor(TangibleUnit tu) {
            if (initiator != null && tu.equals(initiator.getUnit())) {
                return initiator.battle_role;
            }
            
            if (receiver != null && tu.equals(receiver.getUnit())) {
                return receiver.battle_role;
            }
            
            return null;
        }
        
        public Combatant getCombatantByUnit(TangibleUnit tu) {
            if (initiator != null && tu.equals(initiator.getUnit())) {
                return initiator;
            }
            
            return receiver != null && tu.equals(receiver.getUnit()) ? receiver : null;
        }
        
        public TangibleUnit getUnit() {
            return user;
        }
        
        public TangibleUnit getEnemyUnit() {
            return enemy;
        }
        
        public TangibleUnit getOtherUnit() {
            return other;
        }
        
        public Combatant getInitiator() {
            return initiator;
        }
        
        public Combatant getReceiver() {
            return receiver;
        }
        
        public StrikeParticipant getStriker() {
            return striker;
        }
        
        public StrikeParticipant getVictim() {
            return victim;
        }
        
        public ArrayList<TangibleUnit> getAllUnits() {
            return allUnits;
        }
        
        public List<MapEntity> getMapEntities() {
            return mapEntities;
        }
        
        public AssetManager getAssetManager() {
            return assetmanager;
        }
        
        public Cursor getCursor() {
            return cursor;
        }
        
        public int getCurrentTurn() {
            return turnNumber;
        }
        
        public Objective getObjective() {
            return currentObjective;
        }
        
        public Coords getCoords() {
            return coords;
        }
        
        public int getLayer() {
            return layer;
        }
        
        public RequestDealer getMapFlowRequestTaker() {
            return mapFlowRequestTaker;
        }
        
        public Conveyor setUnit(TangibleUnit playerUnit) {
            user = playerUnit;
            return this;
        }
        
        public Conveyor setEnemyUnit(TangibleUnit enemyUnit) {
            enemy = enemyUnit;
            return this;
        }
        
        public Conveyor setOtherUnit(TangibleUnit otherUnit) {
            other = otherUnit;
            return this;
        }
        
        public Conveyor setInitiator(Combatant atkr) {
            initiator = atkr;
            return this;
        }
        
        public Conveyor setReceiver(Combatant opsr) {
            receiver = opsr;
            return this;
        }
        
        public Conveyor setStriker(StrikeParticipant stkr) {
            striker = stkr;
            return this;
        }
        
        public Conveyor setVictim(StrikeParticipant vctm) {
            victim = vctm;
            return this;
        }
        
        public Conveyor setAllUnits(ArrayList<TangibleUnit> units) {
            allUnits = units;
            return this;
        }
        
        public Conveyor setMapEntities(List<MapEntity> entities) {
            mapEntities = entities;
            return this;
        }
        
        public Conveyor setAssetManager(AssetManager am) {
            assetmanager = am;
            return this;
        }
        
        public Conveyor setCursor(Cursor C) {
            cursor = C;
            return this;
        }
        
        public Conveyor setCurrentTurn(int current) {
            turnNumber = current;
            return this;
        }
        
        public Conveyor setObjective(Objective obje) {
            currentObjective = obje;
            return this;
        }
        
        public Conveyor setCoords(Coords cds) {
            coords = cds;
            return this;
        }
        
        public Conveyor setLayer(int l) {
            layer = l;
            return this;
        }
        
        public Conveyor setMapFlowRequestTaker(RequestDealer rqd) {
            mapFlowRequestTaker = rqd;
            return this;
        }
}
