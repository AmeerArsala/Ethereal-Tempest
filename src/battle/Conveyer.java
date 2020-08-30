/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle;

import com.jme3.asset.AssetManager;
import etherealtempest.DataStructure;
import java.util.ArrayList;
import java.util.List;
import maps.layout.Cursor;
import maps.layout.Map;
import maps.layout.MapEntity;
import maps.layout.TangibleUnit;

/**
 *
 * @author night
 */
public class Conveyer extends DataStructure {
        private TangibleUnit user, enemy, other;
        private Map map;
        private ArrayList<TangibleUnit> allUnits;
        private List<MapEntity> mapEntities;
        private AssetManager assetmanager;
        private Cursor cursor;
        private int turnNumber;
        
        public Conveyer() {}
        
        public Conveyer(TangibleUnit unit) {
            user = unit;
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
        
        public Map getMap() {
            return map;
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
        
        public Conveyer setMap(Map current) {
            map = current;
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
}
