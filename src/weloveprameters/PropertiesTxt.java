/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weloveprameters;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.util.Vector;

/**
 *
 * @author Mikolaj.Miazek
 */

public class PropertiesTxt {
    
    // Vector that contains parameters uploaded from file,txt.
    static Vector <String> props = new Vector <String>();
    
    /* ---------------------------------------------------------------------- */
    /*                                METHODS                                 */
    /* ---------------------------------------------------------------------- */
    
    // Uploads the data ( game parameters ) from the txt.file, which name is "path" parameter
    // of following function, to the "props" vector-object, with following form:
    // name_of_parameter=value_of_parameter.
    void upload (String path) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        try(BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream (path), "UTF-8"))) {
            String line;
            
            while((line = br.readLine()) != null) {               
                if(line.contains("#") == false && line.isEmpty() == false && line.length() > 1) {
                    props.add(line);    
                }
            }   
            
            br.close();  
        } 
    }
    
    // Gets the properties indicated by "i" parameter of the following function, from the "props"
    // vector-object at the index "i" and returns them as a string-object, with following form:
    // name_of_parameter=value_of_parameter.
    // If value of "i" is not an index of "props" ( too high or too low ), returns "NULL" string.
    String getPropAt(int i) {
        if(i >= 0 && i < props.size()) {
            return props.get(i);
        }else return "NULL";
    }
    
}
