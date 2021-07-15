/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import etherealtempest.state.LoadingScreenAppState;
import general.procedure.functional.NamedExecution;
import tests.state.GuiTestAppState;

/**
 *
 * @author night
 */
public class TestStateLoader {
    
    public static LoadingScreenAppState loadGuiTest(AssetManager assetManager, NamedExecution defaultProcesses) {
        NamedExecution[] processes = { defaultProcesses };
        float barWidthPercent = 0.5f;
        ColorRGBA barColor = ColorRGBA.White;
        boolean useRandomBGColorSeed = false;
        
        return new LoadingScreenAppState(assetManager, processes, barWidthPercent, barColor, useRandomBGColorSeed) {
            @Override
            protected void onFinish(AppStateManager stateManager) {
                stateManager.attach(new GuiTestAppState(assetManager));
            }
        };
    }
    
}
