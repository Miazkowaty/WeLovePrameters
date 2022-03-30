/*
 * To change this license header, choose License Headers in Project Properties.  
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weloveprameters;  

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;

/**
 *
 * @author Mikolaj.Miazek
 */

public class WeLovePrameters {
    
    /**
     * @param args the command line arguments
     * @throws java.io.UnsupportedEncodingException
     */
    
    /* ---------------------------------------------------------------------- */
    /*                                METHODS                                 */
    /* ---------------------------------------------------------------------- */
    
    // Main function.
    public static void main(String[] args) throws IOException {
        WeLovePrameters actually_not_bcs_of_this_project = new WeLovePrameters();
    }

    // Constructor.
    public WeLovePrameters() throws UnsupportedEncodingException, IOException {
        PropertiesTxt props = new PropertiesTxt();
        props.upload("_ext_parameters/par2.txt");
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();        
        int main_win_width = Integer.parseInt(getUsefulProp(props.getPropAt(7)));
        int main_win_height = Integer.parseInt(getUsefulProp(props.getPropAt(8)));
        int main_win_x = (screenSize.width - main_win_width) / 2;
        int main_win_y = (screenSize.height - main_win_height) / 2;
        
        JFrame main_win = new JFrame(getUsefulProp(props.getPropAt(0)));       
        main_win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main_win.setBounds(main_win_x, main_win_y, main_win_width, main_win_height);
        
            Gameplay g_play = new Gameplay(screenSize.width , main_win_width - 16, main_win_height - 61, props);
            g_play.setDoubleBuffered(true);
            g_play.setLayout(null);
        main_win.add(g_play);    
            
            JMenuBar menu_bar = new JMenuBar();
                JMenu menu_help = new JMenu("Help");
            menu_bar.add(menu_help);
            
                JMenuItem menu_help_descr = new JMenuItem("How to play?");
            
                ActionListener menu_help_Action_listener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        g_play.change_size(main_win.getWidth() - 16, main_win.getHeight() - 61);
                        ImageIcon how_t_ply = new ImageIcon("pictures/how_t_ply/how_t_ply.png"); 
                        JOptionPane.showMessageDialog(null, null, "How to play?", JOptionPane.INFORMATION_MESSAGE, how_t_ply);
                    }
                };
                
                menu_help_descr.addActionListener(menu_help_Action_listener);
                menu_help.add(menu_help_descr);
            
                JMenu menu_about = new JMenu("About");
            menu_bar.add(menu_about);
            
                JMenuItem menu_about_descr = new JMenuItem("Authors");
            
                ActionListener menu_about_Action_listener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        g_play.change_size(main_win.getWidth() - 16, main_win.getHeight() - 61); 
                        JOptionPane.showMessageDialog(null, "Created by Maria Sulińska and Mikołaj Miazek.", "Authors", JOptionPane.PLAIN_MESSAGE);
                    }
                };
                
                menu_about_descr.addActionListener(menu_about_Action_listener);
                menu_about.add(menu_about_descr);
            
                JMenu menu_difficulty = new JMenu("Difficulty");
            menu_bar.add(menu_difficulty);                
            
                JRadioButtonMenuItem[] tab_of_buttons = new JRadioButtonMenuItem[Integer.parseInt(getUsefulProp(props.getPropAt(5)))]; 
                ButtonGroup group = new ButtonGroup();
                
                ActionListener JRadio_buttons_Action_listener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        for(int i = 0; i < tab_of_buttons.length; i++) {
                            if(tab_of_buttons[i].isSelected()) {
                                g_play.change_diff(i * Integer.parseInt(getUsefulProp(props.getPropAt(6))));
                            }
                        }
                    }
                };
                
                for(int i = 0; i < tab_of_buttons.length; i++) {
                        tab_of_buttons[i] = new JRadioButtonMenuItem(Integer.toString(i));
                        tab_of_buttons[i].setSelected(false);
                        tab_of_buttons[i].addActionListener(JRadio_buttons_Action_listener);
                    group.add(tab_of_buttons[i]);
                    menu_difficulty.add(tab_of_buttons[i]);
                }
                
                tab_of_buttons[0].setSelected(true);
                g_play.change_diff(0);
                
                JMenu menu_highscores = new JMenu("Highscores");
            menu_bar.add(menu_highscores);

                JMenuItem menu_highscores_descr = new JMenuItem("Show highscores");

                ActionListener menu_highscores_Action_listener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        g_play.change_size(main_win.getWidth() - 16, main_win.getHeight() - 61);
                        String displ_msg = ("");

                        try(InputStream input = new FileInputStream("highscores/highscores.properties")) {
                            Properties prop = new Properties();
                            prop.load(input);

                            for(int i = 0; i < Integer.parseInt(getUsefulProp(props.getPropAt(23))) ;i++) {
                                displ_msg += prop.getProperty(Integer.toString(i)) + "\n";
                            }
                        }catch(IOException ex) {}

                        JOptionPane.showMessageDialog(null, displ_msg, "Show highscores", JOptionPane.PLAIN_MESSAGE);
                    }
                };

                menu_highscores_descr.addActionListener(menu_highscores_Action_listener);
                menu_highscores.add(menu_highscores_descr);

        main_win.setJMenuBar(menu_bar);

        main_win.setIconImage(Toolkit.getDefaultToolkit().getImage("pictures/icon_picture/weiti.png"));
        main_win.setVisible(true);

        main_win.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                g_play.change_size(main_win.getWidth() - 16, main_win.getHeight() - 61);
            }

            @Override
            public void componentMoved(ComponentEvent componentEvent) {
                // Does nothing.
            }
        });

        main_win.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                // Does nothing.
            }
            
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                
                if(key == KeyEvent.VK_SPACE) {
                    g_play.startT();
                }else if(key == KeyEvent.VK_P) {
                    g_play.pause_unpause();
                }else if(key == KeyEvent.VK_ESCAPE) {
                    g_play.new_game();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();  
                
                if(key == KeyEvent.VK_SPACE) {
                    try {
                        g_play.stopT();
                    }catch(IOException | InterruptedException ex) {
                        Logger.getLogger(WeLovePrameters.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }        
        });
    }
    
    // Parser-like function, called whenever need to change some property form,
    // from "name_of_parameter1=value_of_parameter1" form,
    // to the "value_of_parameter" form, so it just cuts off the "name_of_parameter" part.
    // Returns the param_value as a string-object.
    static String getUsefulProp(String s) {
        int index_of_equals_sign_in_s = s.indexOf("=");
        s = s.substring(index_of_equals_sign_in_s + 1);       
        return s;
    }
    
}