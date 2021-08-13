/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.occupant.character;

import com.jme3.asset.AssetManager;
import fundamental.unit.CharacterizedUnit;
import fundamental.unit.PositionedUnit;
import fundamental.unit.aspect.UnitAllegiance;
import fundamental.unit.UnitDeserialization;
import maps.layout.MapCoords;

/**
 *
 * @author night
 */
public class TangibleUnitDeserialization {
    private UnitAllegiance allegiance;
    private UnitDeserialization unit;
    private CharacterizedUnit.Info unitCharacterInfo;
    private PositionedUnit.Params params;
    private MapCoords position;
    
    public TangibleUnitDeserialization(UnitAllegiance allegiance, UnitDeserialization unit, CharacterizedUnit.Info unitCharacterInfo, PositionedUnit.Params params, MapCoords position) {
        this.allegiance = allegiance;
        this.unit = unit;
        this.unitCharacterInfo = unitCharacterInfo;
        this.params = params;
        this.position = position;
    }
    
    public TangibleUnit constructTangibleUnit(AssetManager assetManager) {
        unitCharacterInfo.reset(); //TODO: change later
        return new TangibleUnit(unit.constructUnit(), unitCharacterInfo, params, allegiance, assetManager);
    }
    
    public TangibleUnit constructPositionedTangibleUnit(AssetManager assetManager) {
        unitCharacterInfo.reset(); //TODO: change later
        return new TangibleUnit(unit.constructUnit(), unitCharacterInfo, params, allegiance, position, assetManager);
    }
}
