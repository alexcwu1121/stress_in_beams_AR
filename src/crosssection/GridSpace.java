package crosssection;

import java.awt.Color;
import java.awt.Rectangle;

public class GridSpace{

    private Rectangle box;
    private Color color;

    /**
    constructor method that initilizaes the Gridspace rectangle.
    @param x1 The x position of the top right corner of the rectangle.
    @param y1 The y position of the top right corner of the rectangle.
    @param x2 The x length of the rectangle.
    @param y2 The y length of the rectangle.
    @author Nicholas Mataczynski
    */
    public GridSpace(int x1, int y1, int x2, int y2){
        box = new Rectangle(x1, y1, x2, y2);
    }

    /**
    Sets the color of the Gridspace object
    @param r The red value of the color.
    @param b The blue value of the color.
    @param g The green value of the color.
    @author Nicholas Mataczynski
    */
    public void setColor(int r, int b, int g){
        color = new Color(r, b, g);
    }

    /**
    Returns the Gridspace rectangle
    @return The rectangle of the Gridspace
    @author Nicholas Mataczynski
    */
    public Rectangle getRect(){
        return box;
    }

    /**
    Returns the color of the Gridspace
    @return The color of the Gridspace
    @author Nicholas Mataczynski
    */
    public Color getColor(){
        return color;
    }

}