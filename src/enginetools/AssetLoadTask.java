/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enginetools;

import com.jme3.asset.AssetManager;
import general.procedure.functional.NamedProcess;
import java.util.function.Consumer;

/**
 *
 * @author night
 */
public abstract class AssetLoadTask extends NamedProcess<AssetManager> {

    public AssetLoadTask(String name) {
        super(name);
    }
    
    public static AssetLoadTask fromAssetConsumer(String name, Consumer<AssetManager> procedure) {
        return new AssetLoadTask(name) {
            @Override
            public void run(AssetManager t) {
                procedure.accept(t);
            }
        };
    }
}
