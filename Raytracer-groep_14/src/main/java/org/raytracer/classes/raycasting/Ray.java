package org.raytracer.classes.raycasting;

import org.raytracer.classes.objects.Camera;
import org.raytracer.classes.vectors.Vector3;

public class Ray {
    
    private Vector3 origin;
    private Vector3 direction = new Vector3();
    private float t; // Distance scalar
    
    /**
     * Creates ray with direction calculated from camera
     *
     * @param camera for origin and calculating direction
     * @param x      Horizontal pixel of screen
     * @param y      Vertical pixel of screen
     */
    public Ray(Camera camera, int x, int y) {
        
        this.origin = camera.GetPosition();
        this.direction = calculateDirection(camera, x, y);
        
        // todo Is deze nog nodig?
        
        if (direction.length() != 1) {
            direction = direction.normalize();
        }
        
    }
    public Vector3 getDirection() {
        return direction;
    }
    
    public Vector3 getNormalizedDirection() {return direction.normalize();}
    
    public void setDirection(Vector3 direction) {
        this.direction = direction;
    }
    
    /**
     * calculates direction of ray
     * <p>
     * Formula used: 𝐷=𝑃(𝑢,𝑣) − 𝐸
     *
     * @param camera for position (E)
     * @param x      Horizontal pixel of screen
     * @param y      Vertical pixel of screen
     * @return
     */
    public Vector3 calculateDirection(Camera camera, int x, int y) {
        return camera.getPointOnScreen(x, y).subtract(camera.GetPosition());
    }
    
    public Vector3 getOrigin() {
        return origin;
    }
    
    public void setOrigin(Vector3 origin) {
        this.origin = origin;
    }
    
    //projection(tscalar) = origen + tScalar + direction, where t > 0
    
    
    /**
     * Move the point alongside the direction from the raycast
     *
     * @param t distance along the line between the start and end point
     * @return
     */
    public Vector3 getPointOnRay(float t) {
        return new Vector3(origin.add(direction.multiply(t)));
    }
    
    
}