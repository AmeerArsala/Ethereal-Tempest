/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.item.weapon;

import com.google.gson.annotations.SerializedName;
import com.jme3.texture.Texture;
import java.util.function.Supplier;
import maps.data.MapTextures.GUI.ItemAndFormula;

/**
 *
 * @author night
 */
public enum WeaponType {
    @SerializedName("Sword") Sword(false, () -> { return ItemAndFormula.Sword; }),
    @SerializedName("Axe") Axe(false, () -> { return ItemAndFormula.Axe; }),
    @SerializedName("Polearm") Polearm(false, () -> { return ItemAndFormula.Polearm; }),
    @SerializedName("Bow") Bow(false, () -> { return ItemAndFormula.Bow; }),
    @SerializedName("Crossbow") Crossbow(false, () -> { return ItemAndFormula.Crossbow; }),
    @SerializedName("Whip") Whip(false, () -> { return ItemAndFormula.Whip; }),
    @SerializedName("Club") Club(false, () -> { return ItemAndFormula.Club; }),
    @SerializedName("Knife") Knife(false, () -> { return ItemAndFormula.Knife; }),
    @SerializedName("Hammer") Hammer(false, () -> { return ItemAndFormula.Hammer; }),
    @SerializedName("Martial Arts") MartialArts(false, () -> { return ItemAndFormula.MartialArts; }),
    @SerializedName("Monster Weapon") MonsterWeapon(false, () -> { return ItemAndFormula.MonsterWeapon; }),
    @SerializedName("Delta Ether") DeltaEther(true, () -> { return ItemAndFormula.DeltaEther; }),
    @SerializedName("Gamma Ether") GammaEther(true, () -> { return ItemAndFormula.GammaEther; }),
    @SerializedName("Omega Ether") OmegaEther(true, () -> { return ItemAndFormula.OmegaEther; }),
    @SerializedName("Pi Ether") PiEther(true, () -> { return ItemAndFormula.PiEther; }),
    @SerializedName("Alpha Ether") AlphaEther(true, () -> { return ItemAndFormula.AlphaEther; });
    
    private final boolean isFormula;
    private final Supplier<Texture> iconTexture;
    private WeaponType(boolean isFormula, Supplier<Texture> iconTexture) {
        this.isFormula = isFormula;
        this.iconTexture = iconTexture;
    }
    
    public boolean isFormula() {
        return isFormula;
    }
    
    public Texture getIconTexture() {
        return iconTexture.get();
    }
}
