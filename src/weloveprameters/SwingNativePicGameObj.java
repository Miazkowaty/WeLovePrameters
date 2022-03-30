/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weloveprameters;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Mikolaj.Miazek
 */

public class SwingNativePicGameObj {
    
    // Overall object variables.
    int _x; int _y;
    int _width; int _height; 
    int _R; int _G; int _B;
    String _shape;
    
    /* ---------------------------------------------------------------------- */
    /*                                METHODS                                 */
    /* ---------------------------------------------------------------------- */
    
    // Constructor.
    SwingNativePicGameObj(int x, int y, int R, int G, int B, int width, int height, String shape) {
        _x = x; _y = y;
        _width = width; _height = height;
        _R = R; _G = G; _B = B; 
        _shape = shape;
    }
    
    // Called when need to resize/rescale some SwingNativePicGameObject.
    public void changeParameters(int x, int y, int width, int height) {
        _x = x; _y = y;
        _width = width; _height = height;  
    }
    
    // Called when need to change R, G and B color values.
    public void changeRGB(int R, int G, int B) {
        _R = R; _G = G; _B = B;
    }
   
    // Painting function.
    public void paintObj(Graphics g) {  
        g.setColor(new Color( _R, _G, _B));
        
        if("prostokąty".equals(_shape)) {
            g.fillRect(_x, _y, _width, _height);
        }else if("trójkąty".equals(_shape)) {
            g.fillPolygon(new int[] {_x, (2 * _x + _width) / 2, _x + _width}, new int[] {_y + _height, _y, _y + _height}, 3);
        }else if("kółka".equals(_shape)) {
            g.fillOval(_x, _y, _width, _height);
        }else if("kwadraty".equals(_shape)) {
            g.fillRect(_x,_y, _width, _width);
        }  
    }  
    
    int getWidth() {  return _width;  }
    
    int getHeight() {  return _height;  }
    
    int getX() {  return _x;  }
    
    int getY() {  return _y;  }
    
}
