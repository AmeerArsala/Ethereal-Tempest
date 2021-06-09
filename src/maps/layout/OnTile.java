/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout;

import maps.layout.tile.Tile;

/**
 *
 * @author night
 */
public interface OnTile {
    public Tile getCurrentTile();
    public Tile getCurrentTile(MapLevel map);
}
