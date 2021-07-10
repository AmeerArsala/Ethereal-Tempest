/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest;

import maps.data.MapData;
import maps.state.MapLevelAppState;

/**
 *
 * @author night
 */
public class GameContext {
    private MapLevelAppState mapState;
    private MapData mapData;
    
    public GameContext() {}
    
    public GameContext(MapLevelAppState mapState, MapData mapData) {
        this.mapState = mapState;
        this.mapData = mapData;
    }
    
    public GameContext(MapLevelAppState mapState) {
        this.mapState = mapState;
    }
    
    public GameContext(MapData mapData) {
        this.mapData = mapData;
    }

    public MapLevelAppState getMapState() {
        return mapState;
    }

    public MapData getMapData() {
        return mapData;
    }

    public void setMapState(MapLevelAppState mapState) {
        this.mapState = mapState;
    }

    public void setMapData(MapData mapData) {
        this.mapData = mapData;
    }
}
