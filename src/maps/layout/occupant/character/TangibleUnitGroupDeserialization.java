/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.occupant.character;

import com.jme3.asset.AssetManager;
import fundamental.unit.CharacterizedUnit;
import fundamental.unit.PositionedUnit;
import fundamental.unit.UnitDeserialization;
import fundamental.unit.aspect.UnitAllegiance;
import maps.layout.MapCoords;

/**
 *
 * @author night
 */
public class TangibleUnitGroupDeserialization {
    private UnitAllegiance allegiance;
    private UnitDeserialization unit;
    private CharacterizedUnit.Info unitCharacterInfo;
    private PositionedUnit.Params params;
    private MapCoords[] positions;
    
    public TangibleUnitGroupDeserialization(UnitAllegiance allegiance, UnitDeserialization unit, CharacterizedUnit.Info unitCharacterInfo, PositionedUnit.Params params, MapCoords[] positions) {
        this.allegiance = allegiance;
        this.unit = unit;
        this.unitCharacterInfo = unitCharacterInfo;
        this.params = params;
        this.positions = positions;
    }
    
    public TangibleUnit[] constructPositionedTangibleUnits(AssetManager assetManager) {
        unitCharacterInfo.reset(); //change later
        
        TangibleUnit[] units = new TangibleUnit[positions.length];
        for (int i = 0; i < units.length; i++) {
            units[i] = new TangibleUnit(unit.constructUnit(), unitCharacterInfo, params, allegiance, positions[i], assetManager);
        }
        
        return units;
    }
}
