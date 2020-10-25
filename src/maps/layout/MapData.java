/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout;

import com.google.gson.Gson;
import com.jme3.asset.AssetManager;
import general.visual.DeserializedParticleEffect;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import maps.flow.Objective;
import maps.layout.tile.TileData;

/**
 *
 * @author night
 */
public class MapData { // use gson
    //TODO: ADD WEATHER, LIGHTING, ETC.
    private String objectiveName; //case sensitive
    private Translation[] translations;
    private MapLayerData[] layers;
    private String[] weatherAndEffects; //PATH TO JSONS
    
    public MapData(String objectiveName, Translation[] translations, MapLayerData[] layers, String[] weatherAndEffects) {
        this.objectiveName = objectiveName;
        this.translations = translations;
        this.layers = layers;
        this.weatherAndEffects = weatherAndEffects;
    }
    
    private class MapLayerData {
        private int columnCount;
        private String[] layer; //written in shorthand, separated by spaces
        
        public MapLayerData(int columnCount, String[] layer) {
            this.columnCount = columnCount;
            this.layer = layer;
        }
    }
    
    private class Translation {
        private String shorthand;
        private String tileDataPath;
        
        public Translation(String shorthand, String tileDataPath) {
            this.shorthand = shorthand;
            this.tileDataPath = tileDataPath;
        }
    }
    
    public String matchPath(String shorthand) {
        for (Translation translation : translations) {
            if (translation.shorthand.equals(shorthand)) {
                return translation.tileDataPath;
            }
        }
        
        return shorthand;
    }
    
    public List<TileData[][]> interpret(AssetManager assetManager) {
        List<TileData[][]> data = new ArrayList<>();
        for (MapLayerData mapLayer : layers) {
            String[] rows = mapLayer.layer;
            TileData[][] layerData = new TileData[rows.length][mapLayer.columnCount];
            
            for (int row = 0; row < rows.length; row++) { //bottom-up
                int columnsAdded = 0; //adding columns for multiplication
                int r = (rows.length - 1) - row; //reversing the order of the entries
                String[] columns = rows[r].split("\\s+"); //this array is in shorthand, separated by spaces
                
                for (int c = 0; c < columns.length; c++) {
                    int index = columns[c].indexOf("*");
                    
                    if (index > 0) { // if "g" is shorthand, then "g*4" will be interpreted as "g g g g"
                        int amt = Integer.parseInt(columns[c].substring(index + 1));
                        for (int i = 0; i < amt; i++) {
                            layerData[row][c + columnsAdded + i] = TileData.loadPreset(assetManager, matchPath(columns[c].substring(0, index)));
                        }
                        
                        columnsAdded += amt - 1;
                    } else {
                        layerData[row][c + columnsAdded] = TileData.loadPreset(assetManager, matchPath(columns[c]));
                    }
                }
            }
            
            data.add(layerData);
        }
        
        return data;
    }
    
    public Objective retrieveObjective() {
        return new Objective(objectiveName);
    }
    
    public DeserializedParticleEffect[] retrieveMapEffects(AssetManager assetManager) {
        if (weatherAndEffects != null) {
            DeserializedParticleEffect[] effects = new DeserializedParticleEffect[weatherAndEffects.length];
            for (int i = 0; i < effects.length; i++) {
                effects[i] = DeserializedParticleEffect.loadEffect(weatherAndEffects[i], assetManager);
            }
            
            return effects;
        }
        
        return null;
    }
    
    public static MapData deserializePreset(String presetName) {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\GameInfo\\presets\\Maps\\" + presetName + ".json"));
            
            return gson.fromJson(reader, MapData.class);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
}
