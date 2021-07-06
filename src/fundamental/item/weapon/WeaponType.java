/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.item.weapon;

import com.google.gson.annotations.SerializedName;
import com.jme3.texture.Texture;
import maps.data.MapTextures.GUI.ItemAndFormula;

/**
 *
 * @author night
 */
public enum WeaponType {
    @SerializedName("Sword") Sword(false, ItemAndFormula.Sword),
    @SerializedName("Axe") Axe(false, ItemAndFormula.Axe),
    @SerializedName("Polearm") Polearm(false, ItemAndFormula.Polearm),
    @SerializedName("Bow") Bow(false, ItemAndFormula.Bow),
    @SerializedName("Crossbow") Crossbow(false, ItemAndFormula.Crossbow),
    @SerializedName("Whip") Whip(false, ItemAndFormula.Whip),
    @SerializedName("Club") Club(false, ItemAndFormula.Club),
    @SerializedName("Knife") Knife(false, ItemAndFormula.Knife),
    @SerializedName("Hammer") Hammer(false, ItemAndFormula.Hammer),
    @SerializedName("Martial Arts") MartialArts(false, ItemAndFormula.MartialArts),
    @SerializedName("Monster Weapon") MonsterWeapon(false, ItemAndFormula.MonsterWeapon),
    @SerializedName("Delta Ether") DeltaEther(true, ItemAndFormula.DeltaEther),
    @SerializedName("Gamma Ether") GammaEther(true, ItemAndFormula.GammaEther),
    @SerializedName("Omega Ether") OmegaEther(true, ItemAndFormula.OmegaEther),
    @SerializedName("Pi Ether") PiEther(true, ItemAndFormula.PiEther),
    @SerializedName("Alpha Ether") AlphaEther(true, ItemAndFormula.AlphaEther);
    
    private final boolean isFormula;
    private final Texture iconTexture;
    private WeaponType(boolean isFormula, Texture iconTexture) {
        this.isFormula = isFormula;
        this.iconTexture = iconTexture;
    }
    
    public boolean isFormula() {
        return isFormula;
    }
    
    public Texture getIconTexture() {
        return iconTexture;
    }
}
