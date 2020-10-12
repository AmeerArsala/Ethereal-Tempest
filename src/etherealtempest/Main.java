package etherealtempest;

import battle.Battle;
import com.jme3.app.DebugKeysAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.audio.AudioListenerState;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import edited.FlyCamera;
import edited.state.FlyCamTrueAppState;
import maps.state.TestMap;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    
    public static final FSM GameFSM = new FSM() {
        @Override
        public void setNewStateIfAllowed(FsmState st) {
            state = st; //change later
        }
    };
    
    public Main() {
        super(new StatsAppState(), new FlyCamTrueAppState(), new AudioListenerState(), new DebugKeysAppState());
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        settings.setFrameRate(120);
        //restart();
        debugFlyCam();
        flyCam.setMoveSpeed(350);
        
        /*Node forest = (Node)getAssetManager().loadModel("Models/Maps/forest.j3o");
        getRootNode().attachChild(forest);
        
        DirectionalLight light = new DirectionalLight();
        forest.addLight(light);
        
        AmbientLight ambient = new AmbientLight();
        getRootNode().addLight(ambient);*/
        
       stateManager.attach(new TestMap(this, getCamera(), flyCam));
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }
    
    public void debugFlyCam() {
        if (inputManager != null) {
            // We have to special-case the FlyCamAppState because too
            // many SimpleApplication subclasses expect it to exist in
            // simpleInit().  But at least it only gets initialized if
            // the app state is added.
            if (stateManager.getState(FlyCamTrueAppState.class) != null) {
                flyCam = new FlyCamera(getCamera());
                flyCam.setMoveSpeed(1f); // odd to set this here but it did it before
                stateManager.getState(FlyCamTrueAppState.class).setCamera((FlyCamera)flyCam);
            }
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
}
