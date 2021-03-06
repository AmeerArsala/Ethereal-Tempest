/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.info;

import battle.Combatant;
import battle.Combatant.BattleRole;
import com.jme3.asset.AssetManager;
import etherealtempest.info.DataStructure;
import java.util.ArrayList;
import java.util.List;
import maps.flow.Objective;
import maps.layout.Coords;
import maps.layout.occupant.Cursor;
import maps.layout.occupant.MapEntity;
import maps.layout.occupant.TangibleUnit;

/**
 *
 * @author night
 */
public class Conveyer extends DataStructure {
        private TangibleUnit user, enemy, other; //at least one of these is usually required
        private Combatant initiator, receiver; //usually required in mapFlow
        private ArrayList<TangibleUnit> allUnits; //required in mapFlow
        private List<MapEntity> mapEntities; //required in mapFlow
        private AssetManager assetmanager;
        private Cursor cursor; //required in mapFlow
        private int turnNumber; //required in mapFlow
        private Objective currentObjective; //required in mapFlow
        private Coords coords;
        private int layer;
        
        public Conveyer() {}
        
        public Conveyer(TangibleUnit unit) {
            user = unit;
        }
        
        public void swapUnits() {
            TangibleUnit temp = user;
            user = enemy;
            enemy = user;
        }
        
        public Conveyer createCombatants() {
            initiator = new Combatant(user, BattleRole.Initiator);
            receiver = new Combatant(enemy, BattleRole.Receiver);
            
            return this;
        }
        
        public BattleRole battleRoleFor(TangibleUnit tu) {
            if (initiator != null && tu.is(initiator.getUnit())) {
                return initiator.battle_role;
            }
            
            if (receiver != null && tu.is(receiver.getUnit())) {
                return receiver.battle_role;
            }
            
            return null;
        }
        
        public Combatant getCombatantByUnit(TangibleUnit tu) {
            if (initiator != null && tu.is(initiator.getUnit())) {
                return initiator;
            }
            
            return receiver != null && tu.is(receiver.getUnit()) ? receiver : null;
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
        
        public Conveyer setUnit(TangibleUnit playerUnit) {
            user = playerUnit;
            return this;
        }
        
        public Conveyer setEnemyUnit(TangibleUnit enemyUnit) {
            enemy = enemyUnit;
            return this;
        }
        
        public Conveyer setOtherUnit(TangibleUnit otherUnit) {
            other = otherUnit;
            return this;
        }
        
        public Conveyer setInitiator(Combatant atkr) {
            initiator = atkr;
            return this;
        }
        
        public Conveyer setReceiver(Combatant opsr) {
            receiver = opsr;
            return this;
        }
        
        public Conveyer setAllUnits(ArrayList<TangibleUnit> units) {
            allUnits = units;
            return this;
        }
        
        public Conveyer setMapEntities(List<MapEntity> entities) {
            mapEntities = entities;
            return this;
        }
        
        public Conveyer setAssetManager(AssetManager am) {
            assetmanager = am;
            return this;
        }
        
        public Conveyer setCursor(Cursor C) {
            cursor = C;
            return this;
        }
        
        public Conveyer setCurrentTurn(int current) {
            turnNumber = current;
            return this;
        }
        
        public Conveyer setObjective(Objective obje) {
            currentObjective = obje;
            return this;
        }
        
        public Conveyer setCoords(Coords cds) {
            coords = cds;
            return this;
        }
        
        public Conveyer setLayer(int l) {
            layer = l;
            return this;
        }
}
