/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental;

import com.google.gson.annotations.SerializedName;
import fundamental.ability.Ability;
import fundamental.formation.Formation;
import fundamental.formula.Formula;
import fundamental.item.ConsumableItem;
import fundamental.item.Item;
import fundamental.item.weapon.Weapon;
import fundamental.jobclass.JobClass;
import fundamental.skill.Skill;
import fundamental.talent.Talent;
import java.util.function.Supplier;

/**
 *
 * @author night
 */
public class FundamentalSuppliers {
    public enum JobClassSupplier {
        Freeblade(() -> { return JobClass.Freeblade; });
        
        private final Supplier<JobClass> supplier;
        private JobClassSupplier(Supplier<JobClass> supplier) {
            this.supplier = supplier;
        }
        
        public JobClass get() {
            return supplier.get();
        }
    }
    
    public enum ItemSupplier {
        @SerializedName("ConsumableItem.Apple") Apple(() -> { return ConsumableItem.Apple(); }),
        @SerializedName("Weapon.Copper_Shortsword") WEAPON_CopperShortsword(() -> { return Weapon.Copper_Shortsword(); }),
        @SerializedName("Weapon.Cutlass") WEAPON_Cutlass(() -> { return Weapon.Cutlass(); }),
        @SerializedName("Weapon.Firangi") WEAPON_Firangi(() -> { return Weapon.Firangi(); }),
        @SerializedName("Weapon.Francisca") WEAPON_Francisca(() -> { return Weapon.Francisca(); }),
        @SerializedName("Weapon.Glaive") WEAPON_Glaive(() -> { return Weapon.Glaive(); }),
        @SerializedName("Weapon.Rebel_Pike") WEAPON_RebelPike(() -> { return Weapon.Rebel_Pike(); }),
        @SerializedName("Weapon.Steel_Broadsword") WEAPON_SteelBroadsword(() -> { return Weapon.Steel_Broadsword(); }),
        @SerializedName("Weapon.Svardstav") WEAPON_Svardstav(() -> { return Weapon.Svardstav(); });
        
        private final Supplier<Item> supplier;
        private ItemSupplier(Supplier<Item> supplier) {
            this.supplier = supplier;
        }
        
        public Item get() {
            return supplier.get();
        }
    }
    
    public enum FormulaSupplier {
        Anemo_Schism(() -> { return Formula.Anemo_Schism(); });
        
        private final Supplier<Formula> supplier;
        private FormulaSupplier(Supplier<Formula> supplier) {
            this.supplier = supplier;
        }
        
        public Formula get() {
            return supplier.get();
        }
    }
    
    public enum TalentSupplier {
        EyeOfTheStorm(() -> { return Talent.EyeOfTheStorm(); }),
        Opportunist(() -> { return Talent.Opportunist(); }),
        Optimism(() -> { return Talent.Optimism(); });
        
        private final Supplier<Talent> supplier;
        private TalentSupplier(Supplier<Talent> supplier) {
            this.supplier = supplier;
        }
        
        public Talent get() {
            return supplier.get();
        }
    }
    
    public enum SkillSupplier {
        Heavy_Swing(() -> { return Skill.Heavy_Swing; });
        
        private final Supplier<Skill> supplier;
        private SkillSupplier(Supplier<Skill> supplier) {
            this.supplier = supplier;
        }
        
        public Skill get() {
            return supplier.get();
        }
    }
    
    public enum AbilitySupplier {
        ;
        
        private final Supplier<Ability> supplier;
        private AbilitySupplier(Supplier<Ability> supplier) {
            this.supplier = supplier;
        }
        
        public Ability get() {
            return supplier.get();
        }
    }
    
    public enum FormationSupplier {
        Trigonal_Planar(() -> { return Formation.Trigonal_Planar(); });
        
        private final Supplier<Formation> supplier;
        private FormationSupplier(Supplier<Formation> supplier) {
            this.supplier = supplier;
        }
        
        public Formation get() {
            return supplier.get();
        }
    }
}
