package org.raytracer.classes;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class UICanvas{


    JFrame canvasFrame = new JFrame("best frame ever");

    private int Height, Width;
    PixelData [][] pixelData;

    public BufferedImage bufferedImage;


    public Scene activeScene= new Scene();

    Camera cam;

    //todo create a camera object
    //todo set the pixels from the screen
    //todo try to save the pixels inside some class containing a multidemensional array
    //todo try to create a few empty raycasts
    //todo make a mockup pixelloop that will try to call raycasts through the camera, looping with x and y or u and v

    public UICanvas(int height, int width){

        pixelData = new PixelData[height][width];

        this.Height = height;
        this.Width = width;

        if(canvasFrame != null){
            createNewFrame();
        }
        canvasFrame.setSize(height, width);
        canvasFrame.setVisible(true);
        cam = new Camera(width,height);
        this.Width = cam.getScreenWidth();
        this.Height = cam.getScreenHeight();
        bufferedImage = new BufferedImage(this.Width,this.Height, BufferedImage.TYPE_INT_RGB);
    }
    public boolean createNewFrame(){

        JLabel label = new JLabel(); //JLabel Creation
        //todo use a bufferedimage to display, instead of a saved image
        label.setIcon(new ImageIcon("rame.png")); //Sets the image to be displayed as an icon
        Dimension size = label.getPreferredSize(); //Gets the size of the image
        label.setBounds(50, 30, size.width, size.height); //Sets the location of the image

        Container c = canvasFrame.getContentPane();
        c.add(label); //Adds objects to the container
        canvasFrame.setVisible(true); // Exhibit the frame
        return true;
    }
    public void updateFrame(){
        JLabel label = new JLabel(); //JLabel Creation
        //castRays();
        label.setIcon(new ImageIcon(bufferedImage)); //Sets the image to be displayed as an icon
        Dimension size = label.getPreferredSize(); //Gets the size of the image
        label.setBounds(50, 30, size.width, size.height); //Sets the location of the image

        Container c = canvasFrame.getContentPane();
        c.remove(0);
        c.add(label); //Adds objects to the container
        canvasFrame.setVisible(true); // Exhibit the frame
    }
    public void updateFrame(BufferedImage bufferedImage){
        JLabel label = new JLabel("frame"); //JLabel Creation
        label.setIcon(new ImageIcon(bufferedImage));
        Dimension size = label.getPreferredSize(); //Gets the size of the image
        label.setBounds(50, 30, size.width, size.height); //Sets the location of the image
        Container c = canvasFrame.getContentPane();
        c.remove(0);
        c.add(label); //Adds objects to the container
        canvasFrame.setVisible(true); // Exhibit the frame
    }
    public void setupScenes(Vector3 position, Color color){

        activeScene.setupScene(new Sphere(position, 2,color, 0, 1 ));
    }
    public void castRays(){

        activeScene.GetSolidSceneObject().setPosition(new Vector3(activeScene.GetSolidSceneObject().getPosition().add(new Vector3(0,0,0.1f))));
        Raycast raycaster = new Raycast();
        pixelData = raycaster.castLine(10, cam, Width,Height, activeScene);
        BufferedImage image = null;
        image = new BufferedImage(this.Width,this.Height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < this.Width; i++) {
            for (int j = 0; j < this.Height; j++) {
                try {
                    image.setRGB(i,j,pixelData[i][j].getColor().getRGB());
                }
                catch (Exception e){
                    image.setRGB(i,j, Color.Black.getRGB());
                }
            }
        }
        updateFrame(image);
    }



}
