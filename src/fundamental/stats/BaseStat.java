/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.stats;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;

/**
 *
 * @author night
 */
public enum BaseStat {
        @SerializedName("Level") Level(0, "Level"),
        @SerializedName("Max HP") MaxHP(1, "Max HP"),
        @SerializedName("Strength") Strength(2, "STR"),
        @SerializedName("Ether") Ether(3, "ETHER"),
        @SerializedName("Agility") Agility(4, "AGI"),
        @SerializedName("Comprehension") Comprehension(5, "COMP"),
        @SerializedName("Dexterity") Dexterity(6, "DEX"),
        @SerializedName("Defense") Defense(7, "DEF"),
        @SerializedName("Resilience") Resilience(8, "RSL"),
        @SerializedName("Mobility") Mobility(9, "MOBILITY"),
        @SerializedName("Physique") Physique(10, "PHYSIQUE"),
        @SerializedName("Base Adrenaline") Adrenaline(11, "INIT. ADR"),
        @SerializedName("Current HP") CurrentHP(12, "Current HP"),
        @SerializedName("Current TP") CurrentTP(13, "Current TP"),
        @SerializedName("Max TP") MaxTP(14, "Max TP");
        
        private final int value;
        private final String name;
        
        private BaseStat(int val, String sname) {
            value = val;
            name = sname;
        }

        public int getValue() {
            return value;
        }
        
        public String getName() {
            return name;
        }
        
        public static HashMap<BaseStat, Integer> canvas(int num) {
            HashMap<BaseStat, Integer> canvas = new HashMap<>();
            
            BaseStat[] stats = BaseStat.values();
            
            for (BaseStat stat : stats) {
                canvas.put(stat, num);
            }
            
            return canvas;
        }
    }
