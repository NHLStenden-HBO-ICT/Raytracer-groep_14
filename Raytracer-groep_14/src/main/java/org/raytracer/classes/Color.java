package org.raytracer.classes;

public class Color {

	private float red;

	private float green;

	private float blue;


	public float getRed() {
		return red;
	}

	public float getGreen() {
		return green;
	}

	public float getBlue() {
		return blue;
	}

	public Color(){

		setColor(Color.White);
	}
	public Color(float red, float green, float blue){
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public void setColor(float red, float green, float blue){
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	public void setColor(Color color){
		this.red = color.red;
		this.green = color.green;
		this.blue = color.blue;
	}
	public int getRGB() {
		int redPart = (int)(red*255);
		int greenPart = (int)(green*255);
		int bluePart = (int)(blue*255);

		// Shift bits to right place
		redPart = (redPart << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
		greenPart = (greenPart << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
		bluePart = bluePart & 0x000000FF; //Mask out anything not blue.

		return 0xFF000000 | redPart | greenPart | bluePart; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
	}
	public java.awt.Color toAWTColor() {
		return new java.awt.Color(red, green, blue);
	}
	public static final Color White = new Color(1f,1f,1f);
	public static final Color Black = new Color(0,0,0);
	public static final Color Blue = new Color(0,0,1f);

}
