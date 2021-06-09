/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.flow;

import java.util.ArrayList;
import java.util.List;
import maps.layout.occupant.MapEntity;
import maps.layout.occupant.character.TangibleUnit;

/**
 *
 * @author night
 */
public interface UnitPlacementInitiation {
    public void initiation(ArrayList<TangibleUnit> units, List<MapEntity> mapEntities);
}
