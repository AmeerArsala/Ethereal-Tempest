package etherealtempest;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.audio.AudioListenerState;
import com.jme3.renderer.RenderManager;
import com.jme3.texture.Image;
import com.jme3.texture.TextureArray;
import edited.FlyCamera;
import edited.state.FlyCamTrueAppState;
import java.util.ArrayList;
import java.util.List;
import maps.layout.MapLevel;
import maps.state.TestMap;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    private static Main app;
    
    public static final FSM GameFSM = new FSM() {
        @Override
        public void setNewStateIfAllowed(FsmState st) {
            state = st; //change later
        }
    };
    
    //this is for the frameCount and time when the tpf is delayed in order for the game not to go too fast or too slow but at a controlled speed
    //this is at the 1f / 60f for tpf in the TestMap class
    public static final Globals GameFlow = new Globals(); 
    
    public Main() {
        super(new StatsAppState(), new FlyCamTrueAppState(), new AudioListenerState(), new DebugKeysAppState());
    }

    public static void main(String[] args) {
        app = new Main();
        app.start();
    }
    
    public static int getScreenWidth() {
        return app.settings.getWidth();
    }
    
    public static int getScreenHeight() {
        return app.settings.getHeight();
    }

    @Override
    public void simpleInitApp() {
       settings.setFrameRate(120); //cap at 120fps
       
       debugFlyCam();
       flyCam.setMoveSpeed(350);

       tilesInitialization();
        
       stateManager.attach(new TestMap(this, getCamera(), flyCam, settings));
    }
    
    public void tilesInitialization() {
        final String prefix = "Textures/tiles/";
        String[] names = {"grass.jpg", "dirt.jpg", "rock.jpg", "plains.jpg", "sand.jpg", "mossystone.jpg"};
        List<Image> textures = new ArrayList<>();
        for (String name : names) {
            textures.add(assetManager.loadTexture(prefix + name).getImage());
        }
        
        MapLevel.tileTextures = new TextureArray(textures);
        MapLevel.OverflowBlendMap = assetManager.loadTexture(prefix + "BlendMap.png");
    }

    @Override
    public void simpleUpdate(float tpf) {
        Globals.updateGlobalInstance(tpf);
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
