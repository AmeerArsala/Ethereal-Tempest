/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.item.weapon;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author night
 */
public enum WeaponType {
    @SerializedName("Sword") Sword(false),
    @SerializedName("Axe") Axe(false),
    @SerializedName("Polearm") Polearm(false),
    @SerializedName("Bow") Bow(false),
    @SerializedName("Crossbow") Crossbow(false),
    @SerializedName("Whip") Whip(false),
    @SerializedName("Club") Club(false),
    @SerializedName("Knife") Knife(false),
    @SerializedName("Hammer") Hammer(false),
    @SerializedName("Martial Arts") MartialArts(false),
    @SerializedName("Monster Weapon") MonsterWeapon(false),
    @SerializedName("Delta Ether") DeltaEther(true),
    @SerializedName("Gamma Ether") GammaEther(true),
    @SerializedName("Omega Ether") OmegaEther(true),
    @SerializedName("Pi Ether") PiEther(true),
    @SerializedName("Alpha Ether") AlphaEther(true);
    
    private final boolean isFormula;
    private WeaponType(boolean isFormula) {
        this.isFormula = isFormula;
    }
    
    public boolean isFormula() {
        return isFormula;
    }
}
