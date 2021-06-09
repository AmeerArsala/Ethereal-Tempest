/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.tile;

import com.google.gson.Gson;
import com.jme3.asset.AssetManager;
import etherealtempest.characters.Unit.UnitAllegiance;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import maps.layout.tile.TileOptionData.TileType;

/**
 *
 * @author night
 */
public class TileData {
    private String tileName = "Ground"; //gson
    
    private TileStatisticalData tileEffects;
    private TileOptionData tileFunction = null;
    private TileVisualData tileVisuals;
    
    public TileData(TileStatisticalData tileEffects, TileOptionData tileFunction, TileVisualData tileVisuals) {
        this.tileEffects = tileEffects;
        this.tileFunction = tileFunction;
        this.tileVisuals = tileVisuals;
    }
    
    public TileData(String tileName, TileStatisticalData tileEffects, TileOptionData tileFunction, TileVisualData tileVisuals) {
        this(tileEffects, tileFunction, tileVisuals);
        this.tileName = tileName;
    }
    
    public String getTileName() {
        return tileName;
    }
    
    public TileStatisticalData getEffects() {
        return tileEffects;
    }
    
    public TileOptionData getFunctionData() {
        return tileFunction;
    }
    
    public TileVisualData getVisuals() {
        return tileVisuals;
    }
    
    public void setTileName(String name) {
        tileName = name;
    }
    
    public void setEffects(TileStatisticalData TSD) {
        tileEffects = TSD;
    }
    
    public void setFunctionData(TileOptionData TOD) {
        tileFunction = TOD;
    }
    
    public void setTileVisualData(TileVisualData TVD) {
        tileVisuals = TVD;
    }
    
    
    public TileType getType() {
        return tileFunction != null ? tileFunction.getFunctionType() : TileType.Normal;
    }
    
    public boolean allegianceIsEligible(UnitAllegiance allegiance) {
        return tileFunction != null ? tileFunction.allegianceIsEligible(allegiance) : true;
    }
    
    public boolean alliedAllegianceIsEligible(UnitAllegiance allegiance) {
        return tileFunction != null ? tileFunction.alliedAllegianceIsEligible(allegiance) : true;
    }
    
    public static TileData loadPreset(AssetManager assetManager, String presetName) {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\GameInfo\\presets\\Tiles\\" + presetName + ".json"));
            
            TileData data = gson.fromJson(reader, TileData.class);
            
            if (data.tileVisuals != null) {
                data.tileVisuals.lightAssimilate(assetManager);
            }
            
            return data;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
