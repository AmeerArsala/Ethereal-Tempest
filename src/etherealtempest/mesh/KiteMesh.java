/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.mesh;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

/**
 *
 * @author night
 */
public class KiteMesh extends CustomMesh {
    public KiteMesh(float length, Vector3f rightEdge, Vector3f focusFront) { //positive z = backwards (front)
        this(
            length, 
            new Vector3f(-rightEdge.x, rightEdge.y, rightEdge.z), 
            rightEdge, 
            focusFront, 
            new Vector3f(focusFront.x, focusFront.y, -focusFront.z)
        );
    }
    
    public KiteMesh(float length, Vector3f leftEdge, Vector3f rightEdge, Vector3f focusFront, Vector3f focusBack) {
        vertices = new Vector3f[6];
        vertices[0] = new Vector3f(0, 0, 0);      //bottom
        vertices[1] = new Vector3f(0, length, 0); //top
        vertices[2] = leftEdge;
        vertices[3] = rightEdge;
        vertices[4] = focusFront;
        vertices[5] = focusBack;
        
        create();
    }

    @Override
    protected void generate() {
        //create texCoords
        texCoord[0] = new Vector2f(0, 0);
        texCoord[1] = new Vector2f(1, 0);
        texCoord[2] = new Vector2f(0, 1);
        texCoord[3] = new Vector2f(1, 1);
        
        indices = new int[] {
            //front side
            0, 4, 2,
            2, 4, 1,
            1, 4, 3,
            3, 4, 0,
            
            //back side
            0, 2, 5,
            5, 2, 1,
            1, 3, 5,
            5, 3, 0
        };
    }
}
