/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.geometry;

import general.math.DomainBox;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import enginetools.MaterialParamsProtocol;
import etherealtempest.mesh.CustomMesh;
import general.math.FloatPair;
import general.math.function.MathFunction;
import general.math.function.ParametricFunction;
import general.math.function.ParametricFunction4f;
import general.math.function.RGBAFunction;
import general.utils.helpers.MathUtils;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class Heart extends Node {
    public static final DomainBox BASE_RELATIVE_DOMAIN_BOX = new DomainBox(
        new FloatPair(-16, 16),     // domainX
        new FloatPair(-17, 11.923f) // domainY
    );
    
    public static final int MIN_CRACKS = 3;
    public static final int MAX_CRACKS = 8;
    
    public static final float MIN_CRACK_WIDTH = 2f;
    public static final float MAX_CRACK_WIDTH = 4.25f;
    
    private static final float HEARTBREAK_START = -12f, HEARTBREAK_END = 5f; //y values
    private static final float T_VAL_FOR_HIGHEST_X = 1.518f, T_VAL_FOR_LOWEST_X = (FastMath.TWO_PI - 1.518f); //t values
    
    public static final ParametricFunction HEART_FUNCTION = new ParametricFunction(
        new MathFunction() { // x 
            @Override
            protected float f(float t) {
                return 16 * FastMath.pow(FastMath.sin(t), 3);
            }
        },
        new MathFunction() { // y
            @Override
            protected float f(float t) {
                return (13 * FastMath.cos(t)) - (5 * FastMath.cos(2 * t)) - (2 * FastMath.cos(3 * t)) - FastMath.cos(4 * t);
            }
        }
    );
    
    
    private final float scalar;
    private final float tStep;
    
    private final List<Vector3f> crackVertices = new ArrayList<>();
    private float crackWidth;
    
    private float percentFilled = 1f;
    
    private final HeartPiece rightHeartPiece = new HeartPiece(
        new HeartPieceMesh(new FloatPair(0f, FastMath.PI), T_VAL_FOR_HIGHEST_X) {
        
        @Override
        protected void setCracks() {
            vertexes.addAll(crackVertices);
        }
        
        @Override
        protected void setIndices() {
            int focusIndex = vertexes.size() - 2;
            int crackStart = vertexes.size() - crackVertices.size();
            
            float endYval = crackVertices.get(1).y;
            float t;
            for (t = domain.a; (scalar * HEART_FUNCTION.y(t)) > endYval; t += tStep) { //until it reaches the bottom
                indexes.add(focusIndex); //add focus vertex
                indexes.add((int)(t / tStep) + 1); //add next vertex
                indexes.add((int)(t / tStep)); //add current vertex
            }
            
            //bottom part
            indexes.add((int)(t / tStep)); //add current vertex
            indexes.add(crackStart); //add first crackVertex
            indexes.add((int)(domain.b / tStep)); //bottom corner
            
            indexes.add((int)(t / tStep)); //add current vertex
            indexes.add(crackStart + 1); //add second crackVertex
            indexes.add(crackStart); //add first crackVertex
            
            //cracks
            for (int i = 1; i < crackVertices.size(); i += 2) {
                indexes.add(crackStart + i + 2);
                indexes.add(crackStart + i + 1);
                indexes.add(crackStart + i);
            }
        }
    });
    
    private final HeartPiece leftHeartPiece = new HeartPiece(
        new HeartPieceMesh(new FloatPair(FastMath.PI, FastMath.TWO_PI), T_VAL_FOR_LOWEST_X) {
        
        @Override
        protected void setCracks() {
            for (int i = crackVertices.size() - 1; i >= 0; --i) {
                vertexes.add(crackVertices.get(i));
            }
        }
        
        @Override
        protected void setIndices() {
            int focusIndex = vertexes.size() - 3;
            int crackStart = vertexes.size() - crackVertices.size();
            
            float endYval = crackVertices.get(0).y;
            float t;
            for (t = domain.b; (scalar * HEART_FUNCTION.y(t)) > endYval; t -= tStep) {
                indexes.add(focusIndex); //add focus vertex
                indexes.add((int)(t / tStep)); //add current vertex
                indexes.add((int)(t / tStep) - 1); //add previous vertex
            }
            
            //bottom part
            indexes.add((int)(t / tStep));
            indexes.add((int)(domain.a / tStep)); //bottom corner
            indexes.add(crackStart); //add first crackVertex
            
            //cracks
            for (int i = 0; i < crackVertices.size(); i += 2) {
                indexes.add(crackStart + i);
                indexes.add(crackStart + i + 1);
                indexes.add(crackStart + i + 2);
            }
        }
    });
    
    public Heart(float scalar, int specificity, AssetManager assetManager) {
        this.scalar = scalar / 12f; // unit heart is divided by 12
        
        tStep = FastMath.PI / (20 * specificity);
        
        initializeHeartbreak();
        
        rightHeartPiece.initialize(assetManager, (mat) -> {
            materialInitialization(mat);
            mat.setFloat("GradientStart", 0.0f);
        });
        
        leftHeartPiece.initialize(assetManager, (mat) -> {
            materialInitialization(mat);
            mat.setFloat("GradientStart", -1.0f);
        });
        
        attachChild(rightHeartPiece.getGeometry());
        attachChild(leftHeartPiece.getGeometry());
    }
    
    private static void materialInitialization(Material mat) {
        mat.setColor("Color", ColorRGBA.Red);
        //mat.setColor("OutlineColor", ColorRGBA.Black);
            
        float $75 = 75f / 255f;
        mat.setColor("BackgroundColor", new ColorRGBA($75, $75, $75, 1f)); //gray
        //mat.setFloat("OutlineThickness", 0.125f);
        mat.setFloat("GradientCoefficient", 1f);
        mat.setFloat("PercentFilled", 1.0f);
    }
    
    private void initializeHeartbreak() {
        int cracks = MIN_CRACKS + (int)((MAX_CRACKS - MIN_CRACKS + 1) * Math.random());
        
        float minCrackWidthT = 5 * (FastMath.PI / 6f);
        float maxCrackWidthT = FastMath.asin(FastMath.pow(MAX_CRACK_WIDTH, (1f / 3f))); //less than minCrackWidthT since a higher t value goes clockwise
        
        float crackLength = (scalar * (HEARTBREAK_END - HEARTBREAK_START)) / cracks;
        float baseCrackWidth = tStep * ((int)((maxCrackWidthT + (float)((minCrackWidthT - maxCrackWidthT) * Math.random())) / tStep));
        crackWidth = scalar * baseCrackWidth; //vertex
        
        crackVertices.add(new Vector3f(0f, HEARTBREAK_START * scalar, 0f));
        
        for (int i = 0; i < cracks; ++i) {
            float a = (HEARTBREAK_START * scalar) + (i * crackLength);
            float b = a + crackLength;
            
            crackVertices.add(new Vector3f(crackWidth, (a + b) / 2f, 0f)); //peak corner
            crackVertices.add(new Vector3f(0f, b, 0f)); //base corner
        }
    }
    
    public float getScalar() { return scalar; }
    public float getTStep() { return tStep; }
    
    public Geometry getRightHeartPiece() { return rightHeartPiece.getGeometry(); }
    public Geometry getLeftHeartPiece() { return leftHeartPiece.getGeometry(); }
    
    public float getPercentFilled() { return percentFilled; }
    
    public void setPercentFilled(float amt) { //amt is a float from 0 to 1
        rightHeartPiece.setFillPercent(amt);
        leftHeartPiece.setFillPercent(amt);
        percentFilled = amt;
    }
    
    public void setHeartColorFunction(RGBAFunction heartColor) {
        rightHeartPiece.setRGBAFunction(heartColor);
        leftHeartPiece.setRGBAFunction(heartColor);
    }
    
    public DomainBox calculateRelativeDomainBox() {
        return BASE_RELATIVE_DOMAIN_BOX.multNew(scalar);
    }
    
    private abstract class HeartPieceMesh extends CustomMesh {
        protected final FloatPair domain;
        protected final float tValForFurthestX; // x'(tValForFurthestX) = 0
        protected final List<Vector3f> vertexes = new ArrayList<>();
        protected final List<Integer> indexes = new ArrayList<>();
    
        public HeartPieceMesh(FloatPair domain, float tValForFurthestX) {
            this.domain = domain;
            this.tValForFurthestX = tValForFurthestX;
        }
        
        @Override
        protected void generate() {
            texCoord[0] = new Vector2f(0, 0);
            texCoord[1] = new Vector2f(1, 0);
            texCoord[2] = new Vector2f(0, 1);
            texCoord[3] = new Vector2f(1, 1);
            
            setVertices();
            setIndices();
            
            vertices = new Vector3f[vertexes.size()];
            for (int i = 0; i < vertices.length; ++i) {
                vertices[i] = vertexes.get(i);
            }
            
            indices = new int[indexes.size()];
            for (int i = 0; i < indices.length; ++i) {
                indices[i] = indexes.get(i);
            }
        }
        
        private void setVertices() {
            for (float t = domain.a; t <= domain.b; t += tStep) {
                vertexes.add(new Vector3f(HEART_FUNCTION.x(t), HEART_FUNCTION.y(t), 0).multLocal(scalar));
            }
            
            setCracks();
        }
        
        protected abstract void setCracks();
        
        protected abstract void setIndices();
    }
    
    private class HeartPiece {
        private final HeartPieceMesh mesh;
        private Geometry geometry;
        private Material mat;
        
        private RGBAFunction heartColor = new RGBAFunction(new ParametricFunction4f(
            new MathFunction() { // R
                @Override
                protected float f(float hpPercent) {
                    if (hpPercent > 0.5f) {
                        return 1f;
                    }
                    
                    float rgbaValue = MathUtils.pointSlopeForm(
                        hpPercent,   // x (input)
                        0.5f, 0.25f, // x1, x2
                        255f, 128f   // y1, y2
                    );
                    
                    return rgbaValue / 255f;
                }
            },
            new MathFunction() { // G
                @Override
                protected float f(float hpPercent) {
                    float rgbaValue = MathUtils.pointSlopeForm(
                        hpPercent,   // x (input)
                        1f, 0.5f,    // x1, x2
                        0f, 221f     // y1, y2
                    );
                    
                    if (hpPercent > 0.5f) {
                        return rgbaValue / 255f;
                    }
                    
                    float rgbaValue2 = MathUtils.pointSlopeForm(
                        hpPercent,   // x (input)
                        0.5f, 0.25f, // x1, x2
                        221f, 0f     // y1, y2
                    );
                    
                    return rgbaValue2 / 255f;
                }
            },
            new MathFunction() { // B
                @Override
                protected float f(float hpPercent) {
                    if (hpPercent > 0.5f) {
                        return 0f;
                    }
                    
                    float rgbaValue = MathUtils.pointSlopeForm(
                        hpPercent,   // x (input)
                        0.5f, 0.25f, // x1, x2
                        255f, 0f     // y1, y2
                    );
                    
                    return rgbaValue / 255f;
                }
            },
            new MathFunction() { // A
                @Override
                protected float f(float hpPercent) {
                    return 1f;
                }
            }
        ));
        
        public HeartPiece(HeartPieceMesh heartMesh) {
            mesh = heartMesh;
        }
        
        public void initialize(AssetManager assetManager, MaterialParamsProtocol params) {
            mesh.create();
            geometry = new Geometry("heart piece geometry", mesh);
            
            mat = new Material(assetManager, "MatDefs/custom/YFill.j3md");
            params.execute(mat);
            geometry.setMaterial(mat);
        }
        
        public HeartPieceMesh getMesh() { return mesh; }
        public Geometry getGeometry() { return geometry; }
        public Material getMaterial() { return mat; }
        public RGBAFunction getRGBAFunction() { return heartColor; }
        
        public void setFillPercent(float fp) { //fp is from 0 to 1
            mat.setFloat("PercentFilled", fp);
            mat.setColor("Color", heartColor.rgba(fp));
        }
        
        public void setRGBAFunction(RGBAFunction rgbaFunc) {
            heartColor = rgbaFunc;
        }
    }
}