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
public enum WeaponAttribute {
    @SerializedName("Steel") Steel, //equivalent to having no attribute basically
    @SerializedName("Fire") Fire,
    @SerializedName("Ice") Ice,
    @SerializedName("Water") Water,
    @SerializedName("Ventus") Ventus,
    @SerializedName("Lightning") Lightning,
    @SerializedName("Terra") Terra,
    @SerializedName("Photon") Photon,
    @SerializedName("Umbra") Umbra,
    @SerializedName("Ecliptica") Ecliptica; //ecliptica is the "almighty" type
}
