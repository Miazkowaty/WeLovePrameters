/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weloveprameters;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Properties;

/**
 *
 * @author Czarek.Miazek
 */

public class Highscores {
    
    String[] highscores_names;
    int[] highscores_values;
    int nr_of_highscores;
    int has_changed = 0;
    
    /* ---------------------------------------------------------------------- */
    /*                                METHODS                                 */
    /* ---------------------------------------------------------------------- */
    
    Highscores(int _nr_of_highscores) {
        highscores_names = new String[_nr_of_highscores];
        highscores_values = new int[_nr_of_highscores];
        nr_of_highscores = _nr_of_highscores;
        
        for(int i = 0; i < nr_of_highscores ; i++) {
            highscores_names[i] = "******* *******";
            highscores_values[i] = 0;
        }
        
        save_highscores();
    }
    
    public void proceed(String name, int value) {
        for(int i = 0; i < nr_of_highscores; i++) {
            if(value >= highscores_values[i]) {
                for(int j = nr_of_highscores - 1; j > i ; j--) {
                    highscores_names[j] = highscores_names[j - 1];
                    highscores_values[j] = highscores_values[j - 1];
                }
                
                highscores_names[i] = name;
                highscores_values[i] = value;               
                has_changed = 1;
                break;
            }
        }
        
        if(has_changed == 1) {         
            save_highscores();        
            has_changed = 0;
        }
    } 
    
    private void save_highscores() {
        try(OutputStream output = new FileOutputStream("highscores/highscores.properties")) {
            Properties prop = new Properties();
                
            for(int i = 0; i < nr_of_highscores; i++) {
                prop.setProperty(Integer.toString(i), highscores_names[i] + ": " + Integer.toString(highscores_values[i]) + " points");
            }
                
            prop.store(output, null);
        }catch(IOException io) {}
    }
    
}
