package crosssection;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Dimension;
import javax.swing.*;

public class gridSpace extends JPanel{

    private Rectangle box;
    private Color color;

    public gridSpace(int x1, int y1, int x2, int y2){
        box = new Rectangle(x1, y1, x2, y2);
    }

    public void setData(int x1, int y1, int x2, int y2){
        box = new Rectangle(x1, y1, x2, y2);
    }

    public void setColor(int r, int b, int g){
        color = new Color(r, b, g);
    }

    public Rectangle getRect(){
        return box;
    }

    public Color getColor(){
        return color;
    }

}