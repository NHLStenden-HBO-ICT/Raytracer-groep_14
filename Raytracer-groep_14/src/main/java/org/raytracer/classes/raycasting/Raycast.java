package org.raytracer.classes.raycasting;

import org.raytracer.classes.objects.Color;
import org.raytracer.classes.objects.SolidObject;
import org.raytracer.classes.scenes.Scene;
import org.raytracer.classes.gui.UICanvas;
import org.raytracer.classes.rendering.RenderPixelColors;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class Raycast {
    
    /**
     * Create a new image out of the data collected by the raytracer
     *
     * @param rayReach
     * @param scene
     * @return
     */
    public BufferedImage castRays(float rayReach, Scene scene) {
        RenderPixelColors renderPixelColors = new RenderPixelColors(scene.getWidthAndHeight());
        SolidObject object = scene.getFirstSolidObject(); // todo weghalen en de lijst aanroepen
        
        for (int i = 0; i < scene.getWidthAndHeight(); i++) {
            for (int j = 0; j < scene.getWidthAndHeight(); j++) {
                Ray tempRay = new Ray(scene.GetCamera(), i, j);
                Intersection intersection = object.calculateIntersection(tempRay);
                
                if (intersection != null) {
                    //todo object dat in de lijst voorkomt gebruiken om kleur op te vragen.
                    renderPixelColors.writeFramePixel(i, j, object.getColor());
                } else {
                    renderPixelColors.writeFramePixel(i, j, Color.White);
                }
            }
        }
        return renderPixelColors.finishFrame();
    }

    /**
     * Start casting mulithreaded rays
     * @param rayReach
     * @param scene
     * @return
     */
    public BufferedImage castThreadedRays(float rayReach,Scene scene){
        RenderPixelColors renderPixelColors = new RenderPixelColors(scene.getWidthAndHeight());
        SolidObject object = scene.getFirstSolidObject();
        Future<BufferedImage> threadedImage = ThreadManager.executerService.submit(() -> {
            for (int i = 0; i < scene.getWidthAndHeight(); i++) {
                for (int j = 0; j < scene.getWidthAndHeight(); j++) {
                    Intersection intersection = object.calculateIntersection(new Ray(scene.GetCamera(), i, j));
                    if(intersection != null){
                        renderPixelColors.writeFramePixel(i,j,object.getColor());   //replacement code, needs a colour to return else all goes to hell
                    }
                    else
                    {
                        renderPixelColors.writeFramePixel(i,j, new Color(0,0,Math.min(255, j)));
                    }
                }
            }
            return renderPixelColors.finishFrame();
        });
        while (!threadedImage.isDone()){
            System.out.println("processing stay patient");
        }
        try {
            return threadedImage.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public BufferedImage castThreadedRaysMultipleObjects(float rayReach,Scene scene){
        RenderPixelColors renderPixelColors = new RenderPixelColors(scene.getWidthAndHeight());
        List<SolidObject> objectList = scene.getObjectList();
        Future<BufferedImage> threadedImage = ThreadManager.executerService.submit(() -> {
            for (int i = 0; i < scene.getWidthAndHeight(); i++) {
                for (int j = 0; j < scene.getWidthAndHeight(); j++) {
                    float lastPos = 300;
                    SolidObject closestObject = null;
                    getClosestObject(scene, renderPixelColors, objectList, i, j, lastPos, closestObject);
                }
            }
            return renderPixelColors.finishFrame();
        });
        while (!threadedImage.isDone()){
            System.out.println("processing stay patient");
        }
        try {
            return threadedImage.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * get the closest intersection point
     * @param scene
     * @param renderPixelColors
     * @param objectList
     * @param i
     * @param j
     * @param lastPos
     * @param closestObject
     */
    private void getClosestObject(Scene scene, RenderPixelColors renderPixelColors, List<SolidObject> objectList, int i, int j, float lastPos, SolidObject closestObject) {
        for (SolidObject item: objectList) {
            Intersection intersection = item.calculateIntersection(new Ray(scene.GetCamera(), i, j));
            if(intersection != null){
                if (intersection.getDistanceToCameraOrigin() < lastPos){
                    lastPos = intersection.getDistanceToCameraOrigin();
                    closestObject = item;
                }
            }
            if (closestObject != null){
                renderPixelColors.writeFramePixel(i, j, closestObject.getColor());   //replacement code, needs a colour to return else all goes to hell
            }
            else {
                renderPixelColors.writeFramePixel(i, j, new Color(0,0,Math.min(j,255f)));
            }
        }
    }

    //todo create a way to give a sample size to the raytracer
    public BufferedImage castThreadedRaysMultipleObjectsAntiAliasing(float rayReach,Scene scene) {
        if (!ThreadManager.getExecuterStatus()){
            ThreadManager.restartExecuter();
        }
        RenderPixelColors renderPixelColors = new RenderPixelColors(scene.getWidthAndHeight());
        List<SolidObject> objectList = scene.getObjectList();
        List<Future<BufferedImage>> threadImages = new ArrayList<Future<BufferedImage>>();
        for (int l = 0; l < 3; l++) {
            raytraceToImg(scene, renderPixelColors, objectList, threadImages);
        }
            List<BufferedImage> imageResultList = new ArrayList<>();
        imageResultList.add(null);
        imageResultList.add(null);
        imageResultList.add(null);
            boolean processing = true;
            while (processing){
                if (IntStream.of(0, 1, 2).allMatch(j -> checkIfThreadIsDone(threadImages, imageResultList, j))){
                    for(int i = 0; i < 3; i++){
                        if (imageResultList.get(i) == null){
                            checkIfThreadIsDone(threadImages,imageResultList,i);
                        }
                    }
                    processing = false;
                }
            }
        frameComparer(scene, renderPixelColors, imageResultList);
        ThreadManager.executerService.shutdown();
        return renderPixelColors.finishFrame();
    }

    private void frameComparer(Scene scene, RenderPixelColors renderPixelColors, List<BufferedImage> imageResultList) {
        for (int i = 0; i < scene.getWidthAndHeight(); i++){
            for (int j = 0; j < scene.getWidthAndHeight(); j++){
                int colorInt = 0;
                for (BufferedImage image: imageResultList) {
                    if (image == null){
                        System.out.println("heya");
                    }
                    else {
                        colorInt += image.getRGB(i,j);
                    }
                }
                renderPixelColors.writeFramePixel(i, j, colorInt/3);
            }
        }
    }

    private boolean checkIfThreadIsDone(List<Future<BufferedImage>> threadImages, List<BufferedImage> imageResultList, int f) {
        if (threadImages.get(f).isDone()){
            try {
                if (imageResultList.get(f) == null){
                    imageResultList.remove(f);
                    imageResultList.add(f, threadImages.get(f).get());
                }
                //imageResultList.add(threadImages.get(f).get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return threadImages.get(f).isDone();
    }

    private void raytraceToImg(Scene scene, RenderPixelColors renderPixelColors, List<SolidObject> objectList, List<Future<BufferedImage>> threadImages) {
        threadImages.add(ThreadManager.executerService.submit(() -> {
            for (int i = 0; i < scene.getWidthAndHeight(); i++) {
                for (int j = 0; j < scene.getWidthAndHeight(); j++) {
                    float lastPos = 300;
                    SolidObject closestObject = null;
                    getClosestObject(scene, renderPixelColors, objectList, i, j, lastPos, closestObject);
                }
            }
            return renderPixelColors.finishFrame();
        }));
    }



    private void raytraceToImg(Scene scene, RenderPixelColors renderPixelColors, List<SolidObject> objectList, List<Future<BufferedImage>> threadImages, float aASupplement) {
        threadImages.add(ThreadManager.executerService.submit(() -> {
            for (int i = 0; i < scene.getWidthAndHeight(); i++) {
                for (int j = 0; j < scene.getWidthAndHeight(); j++) {
                    float lastPos = 300;
                    SolidObject closestObject = null;
                    getClosestObject(scene, renderPixelColors, objectList, i, j, lastPos, closestObject);
                }
            }
            return renderPixelColors.finishFrame();
        }));
    }

    /**
     * A fun method to cast an image line by line
     *
     * @param rayReach
     * @param scene
     * @param canvas
     */
    public void castLine(float rayReach, Scene scene, UICanvas canvas) {
        RenderPixelColors renderPixelColors = new RenderPixelColors(scene.getWidthAndHeight());
        SolidObject object = scene.getFirstSolidObject();
        for (int i = 0; i < scene.getWidthAndHeight(); i++) {
            for (int j = 0; j < scene.getWidthAndHeight(); j++) {
                Ray tempRay = new Ray(scene.GetCamera(), i, j);
                Intersection intersection = object.calculateIntersection(tempRay);
                if (intersection != null) {
                    renderPixelColors.writeFramePixel(i, j, object.getColor());
                } else {
                    renderPixelColors.writeFramePixel(i, j, Color.White);
                }
                canvas.updateFrame(renderPixelColors.finishFrame());
            }
        }
    }
}
