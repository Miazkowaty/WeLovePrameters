/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weloveprameters;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 *
 * @author Mikolaj.Miazek
 */

public class ExtPicGameObj {
    
    // Overall object variables.
    int _x; int _y;
    int _width; int _height; 
    BufferedImage _image;
    BufferedImage _primal_size_image;
    
    /* ---------------------------------------------------------------------- */
    /*                                METHODS                                 */
    /* ---------------------------------------------------------------------- */

    // Constructor.
    // There are always two active instances of BufferedImage object that contains uploaded picture
    // in order to avoid reading from file every time when need to resize the picture.
    ExtPicGameObj(int x, int y, String path, int width, int height) throws IOException {
        _x = x; _y = y;
        _width = width; _height = height;
        _image = ImageIO.read(new File(path));
        _primal_size_image = _image;
        _image = getScaledImage(_primal_size_image, _width, _height);
    }
    
    // Internal function called when need to resize some BufferedImage object.
    private BufferedImage getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
        
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        
        return resizedImg;
    }
    
    // Called when need to resize/rescale some ExtPicGameObject.
    public void changeParameters(int x, int y, int width, int height) {
        _x = x; _y = y;
        _width = width; _height = height;  
        _image = getScaledImage(_primal_size_image, _width, _height);    
    }
   
    // Painting function.
    public void paintObj(Graphics g) {  
        g.drawImage(_image, _x, _y, null);  
    }  
    
    int getWidth() {  return _width;  }
    
    int getHeight() {  return _height;  }
    
    int getX() {  return _x;  }
    
    int getY() {  return _y;  }
    
}
