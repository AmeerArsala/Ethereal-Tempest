/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle;

import battle.skill.Skill;
import com.jme3.asset.AssetManager;
import java.util.ArrayList;
import java.util.List;
import maps.layout.Cursor;
import maps.layout.Map;
import maps.layout.TangibleUnit;

/**
 *
 * @author night
 */
public class Conveyer {
        private TangibleUnit user, enemy;
        private Map map;
        private ArrayList<TangibleUnit> allUnits;
        private AssetManager assetmanager;
        private List<Runnable> tasks = new ArrayList<>();
        private Cursor cursor;
        
        public Conveyer(TangibleUnit unit) {
            user = unit;
        }
        
        public TangibleUnit getUnit() {
            return user;
        }
        
        public TangibleUnit getEnemyUnit() {
            return enemy;
        }
        
        public Map getMap() {
            return map;
        }
        
        public ArrayList<TangibleUnit> getAllUnits() {
            return allUnits;
        }
        
        public AssetManager getAssetManager() {
            return assetmanager;
        }
        
        public List<Runnable> onSelect() {
            return tasks;
        }
        
        public Cursor getCursor() {
            return cursor;
        }
        
        public Conveyer setEnemyUnit(TangibleUnit enemyUnit) {
            enemy = enemyUnit;
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
        
        public Conveyer setAssetManager(AssetManager am) {
            assetmanager = am;
            return this;
        }
        
        public Conveyer setTasks(List<Runnable> RS) {
            tasks = RS;
            return this;
        } 
        
        public Conveyer addOnSelect(Runnable task) {
            tasks.add(task);
            return this;
        }
        
        public Conveyer setCursor(Cursor C) {
            cursor = C;
            return this;
        }
}
