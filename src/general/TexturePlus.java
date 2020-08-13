/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

import battle.Battle.ImpactType;
import com.jme3.texture.Texture;

/**
 *
 * @author night
 */
public class TexturePlus {
    public Texture texture = null;
    private ImpactType impactType = ImpactType.None;
    
    public TexturePlus(Texture T) {
        texture = T;
    }
    
    public TexturePlus(ImpactType IT) {
        impactType = IT;
    }
    
    public TexturePlus(Texture T, ImpactType IT) {
        texture = T;
        impactType = IT;
    }
    
    public TexturePlus setExtra(ImpactType type) {
        impactType = type;
        return this;
    }
    
    public ImpactType getType() { return impactType; }
}
