/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weloveprameters;

import Jama.Matrix;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.Math.pow;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author Mikolaj.Miazek
 */

public class Gameplay extends JPanel {
    
    // Overall game variables passed via constructor.
    int screenSize_width, canvas_width, canvas_height;
    PropertiesTxt props;
    Highscores hscores;
    
    // SPACE-key "pressed time"-counting variables.
    int busy_counting = 0;
    long time_start, time_end;
    int isSpacePressed = 0;
    
    // Timer variables and 'ActionListener's:
    
    // Internal-clock animation.
    ActionListener clk_animation_listener = new ActionListener() {
        // Action that takes place whenever tmr_clk performs an action.
        @Override
        public void actionPerformed(ActionEvent evt) {
            time_p -= 1;
            check_for_state_change();
            time_p_lab.setText(Integer.toString(time_p) + " [s]");
        }
    };
    Timer tmr_clk = new Timer(1000, clk_animation_listener);
    
    // Bin-movement animation.
    ActionListener bin_animation_listener = new ActionListener() {
        // Action that takes place whenever tmr_bin performs an action.
        @Override
        public void actionPerformed(ActionEvent evt) {
            int _bin_X_pos;

            if("figuryGeometryczne".equals(getUsefulProp(props.getPropAt(12)))) {  
                _bin_X_pos = binN_P[0] * canvas_width / NORM;
            }else {
                _bin_X_pos = binE_P[0] * canvas_width / NORM;
            }
                
            t_bin = t_bin + dt_bin;
   
            if("figuryGeometryczne".equals(getUsefulProp(props.getPropAt(12)))) {        
                bin_native.changeParameters(_bin_X_pos + (int)Math.round(bin_max_displacement * canvas_width / 100 * Math.sin(t_bin)), bin_native.getY(), bin_native.getWidth(), bin_native.getHeight());
                    target_native.changeParameters(bin_native.getX() + targetN_P[0] * bin_native.getWidth() / NORM, target_native.getY(), ball_native.getWidth(), ball_native.getHeight());
            }else {
                bin_ext.changeParameters(_bin_X_pos + (int)Math.round(bin_max_displacement * canvas_width / 100 * Math.sin(t_bin)), bin_ext.getY(), bin_ext.getWidth(), bin_ext.getHeight()); 
                    target_ext.changeParameters(bin_ext.getX() + targetE_P[0] * bin_ext.getWidth() / NORM, target_ext.getY(), ball_ext.getWidth(), ball_ext.getHeight());
            }
            
            repaint();
    
            if(t_bin >= 6.28) {
                t_bin = 0;
            }
        }
    };
    Timer tmr_bin = new Timer(1, bin_animation_listener);
        double t_bin = 0, dt_bin;
    
    // Ball-movement animation.
    ActionListener throw_animation_listener = new ActionListener() {
        // Action that takes place whenever tmr_loading or tmr_throw performs an action.
        @Override
        public void actionPerformed(ActionEvent e) {
            if(tmr_loading.isRunning()) {
                long diff = System.currentTimeMillis() - time_start;

                if(diff < TIME) {
                    loading_bar.changeParameters(power_bar_background.getX() + (power_bar_background.getWidth() - lb_P[2] * power_bar_background.getWidth() / NORM) / 2, power_bar_background.getY() + (power_bar_background.getHeight() - lb_P[3] * power_bar_background.getHeight() / NORM) / 2, (int)diff * lb_P[2] * power_bar_background.getWidth() / NORM / TIME, lb_P[3] * power_bar_background.getHeight() / NORM); 
                    loading_bar.changeRGB(((int)diff * 255 / TIME) * 2 / 3 + 255 / 3, (255 - (int)diff * 255 / TIME) * 2 / 3 + 255 / 3, 0);
                    repaint();
                }
            }

            if(tmr_throw.isRunning()) {
                int _ball_X_pos;

                if("figuryGeometryczne".equals(getUsefulProp(props.getPropAt(12)))) {  
                    _ball_X_pos = ballN_P[0] * canvas_width / NORM;
                }else {
                    _ball_X_pos = ballE_P[0] * canvas_width / NORM;
                }

                if(t_throw < _ball_X_pos + ballDisplacementX) {
                    t_throw = t_throw + dt_throw; 
                    double x_pos = t_throw, y_pos = ABCParams[0] * pow(t_throw, 2) + ABCParams[1] * t_throw + ABCParams[2]; 

                    if("figuryGeometryczne".equals(getUsefulProp(props.getPropAt(12)))) {        
                        ball_native.changeParameters((int)Math.round(x_pos), (int)Math.round(y_pos), ballN_P[2] * canvas_width / NORM, ballN_P[3] * canvas_height / NORM);

                        if(ball_native.getX() > target_native.getX() - ball_native.getWidth() / 3 && ball_native.getX() < target_native.getX() + ball_native.getWidth() / 3 && ball_native.getY() > target_native.getY() - ball_native.getHeight() / 3 && ball_native.getY() < target_native.getY() + ball_native.getHeight() / 3) {
                            coins_nr += funds_per_strike; 
                            coins_nr_lab.setText("Funds: " + Integer.toString(coins_nr));
                            ects_loca += 1;
                            ects_loca_lab.setText("Hit rate: " + Integer.toString(ects_loca));
                            tmr_throw.stop();
                            setBallAtDefaultPos();
                        }      
                    }else {
                        ball_ext.changeParameters((int)Math.round(x_pos), (int)Math.round(y_pos), ballE_P[2] * canvas_width / NORM, ballE_P[3] * canvas_height / NORM);

                        if(ball_ext.getX() > target_ext.getX() - ball_ext.getWidth() / 3 && ball_ext.getX() < target_ext.getX() + ball_ext.getWidth() / 3 && ball_ext.getY() > target_ext.getY() - ball_ext.getHeight() / 3 && ball_ext.getY() < target_ext.getY() + ball_ext.getHeight() / 3) {
                            coins_nr += funds_per_strike; 
                            coins_nr_lab.setText("Funds: " + Integer.toString(coins_nr));
                            ects_loca += 1;
                            ects_loca_lab.setText("Hit rate: " + Integer.toString(ects_loca));
                            tmr_throw.stop(); 
                            setBallAtDefaultPos();
                        }           
                    }

                    repaint();
                }else {
                    tmr_throw.stop();
                    t_throw = _ball_X_pos + (int)Math.round(ballDisplacementX);
                    double x_pos = t_throw, y_pos = ABCParams[0] * pow(t_throw, 2) + ABCParams[1] * t_throw + ABCParams[2]; 

                    if("figuryGeometryczne".equals(getUsefulProp(props.getPropAt(12)))) {        
                        ball_native.changeParameters((int)Math.round(x_pos), (int)Math.round(y_pos), ballN_P[2] * canvas_width / NORM, ballN_P[3] * canvas_height / NORM);
                    }else {
                        ball_ext.changeParameters((int)Math.round(x_pos), (int)Math.round(y_pos), ballE_P[2] * canvas_width / NORM, ballE_P[3] * canvas_height / NORM);
                    }

                    repaint();
                    
                    if(coins_nr >= funds_per_miss) {
                        coins_nr -= funds_per_miss; 
                        coins_nr_lab.setText("Funds: " + Integer.toString(coins_nr));
                    }
                }
            }      
        }
    };
    Timer tmr_loading = new Timer(1, throw_animation_listener);
    Timer tmr_throw = new Timer(1, throw_animation_listener);  
        int t_throw, dt_throw;
        int tmr_throw_was_running = 0, was_ended = 0;
    
    // Throw-animation variables.
    long ballDisplacementX = 0;
    double[] ABCParams = new double[] {0, 0, 0};
    
    // Maximum loading time.
    int TIME;
    
    // Time of every single lvl.
    int time_per_lvl;
    
    // Maximum displacement of bin.
    int bin_max_displacement;
    
    // Funds variables.
    int funds_per_strike, funds_per_lvlUP, funds_per_miss, funds_required_for_lvlUP;
    
    // Other flags.
    int paused = 1;
 
    // Bar text-virables and JLabel instances.
    // The left side
    int coins_nr, current_sem, ects_loca;
    JLabel coins_nr_lab = new JLabel(""), current_sem_lab = new JLabel(""), ects_loca_lab = new JLabel("");
    // The right side
    int time_p;
    JLabel time_p_lab = new JLabel(""), pause_p_lab = new JLabel("Press \"p\" to start"), end_game_lab = new JLabel("");
    
    // All of the displayed object-variables.
    // Word "native" means "uses swing-built-in shapes" like triangle or oval,
    // prefix "ext" means "external" - graphics loaded-in from external files. 
    ExtPicGameObj background_ext;
    SwingNativePicGameObj background_native;
    ExtPicGameObj bar;
        // The left side
        ExtPicGameObj info_bar_Ltop;
            ExtPicGameObj coin;
        ExtPicGameObj info_bar_Lmid;
            ExtPicGameObj current_semester;
        ExtPicGameObj info_bar_Lbot;
            ExtPicGameObj ects_locally;
        // The right side
        ExtPicGameObj info_bar_Rtop;
            ExtPicGameObj clock;
        ExtPicGameObj info_bar_Rmid;
            ExtPicGameObj pause;
        ExtPicGameObj info_bar_Rbot;
            ExtPicGameObj exit;
        // The middle
        ExtPicGameObj title;
    // Game objects    
    ExtPicGameObj player_ext;
    SwingNativePicGameObj player_native;
    ExtPicGameObj bin_ext;
    SwingNativePicGameObj bin_native;
    ExtPicGameObj ball_ext;
    SwingNativePicGameObj ball_native;
        ExtPicGameObj target_ext;
        SwingNativePicGameObj target_native;
    // Power bar
    ExtPicGameObj power_bar_background;
    ExtPicGameObj loading_background;
    SwingNativePicGameObj loading_bar;
    // Pause
    ExtPicGameObj pause_background;
    
    // Proportions used to arrange positions and dimensions of the on-screen objects
    // declared below not to change all of their multiple uses in later code.
    // Normalized to NORM ( like "value/NORM" ).
    int b_P[] = {1, 2, 198, 40};
        // The left side
        int i_b_Ltop_P[] = {0, 20, 40, 40}, i_b_Lmid_P[] = {0, 80, 40, 40}, i_b_Lbot_P[] = {0, 140, 40, 40};
        // The right side
        int i_b_Rtop_P[] = {160, 20, 40, 40}, i_b_Rmid_P[] = {160, 80, 40, 40}, i_b_Rbot_P[] = {160, 140, 40, 40};
        // The middle
        int te_P[] = {175, 75, 150, 50};
    // Game objects
    int playerE_P[] = {12, 47, 27, 148}, playerN_P[] = {3, 133, 40, 50};
    int binE_P[] = {145, 99, 20, 93}, binN_P[] = {150, 120, 17, 67};
    int ballE_P[] = {29, 93, 8, 20}, ballN_P[] = {33, 117, 7, 10};
        int targetE_P[] = {45, 25, 0, 0}, targetN_P[] = {60, 80, 0, 0};
    // Power bar
    int pbb_P[] = {0, 2, 25, 14}, lb_P[] = {0, 0, 185, 145};
    // Normalization value.
    int NORM = 200;
    
    /* ---------------------------------------------------------------------- */
    /*                                METHODS                                 */
    /* ---------------------------------------------------------------------- */
    
    // Constructor.
    public Gameplay(int _screenSize_width, int _canvas_width, int _canvas_height, PropertiesTxt _props) {
        screenSize_width = _screenSize_width;
        canvas_width = _canvas_width;        
        canvas_height = _canvas_height;
        props = _props;
        hscores = new Highscores(Integer.parseInt(getUsefulProp(props.getPropAt(23))));
        TIME = Integer.parseInt(getUsefulProp(props.getPropAt(15))); 
        time_per_lvl = Integer.parseInt(getUsefulProp(props.getPropAt(14)));
        bin_max_displacement = Integer.parseInt(getUsefulProp(props.getPropAt(16)));
        funds_per_strike = Integer.parseInt(getUsefulProp(props.getPropAt(19)));
        funds_per_lvlUP = Integer.parseInt(getUsefulProp(props.getPropAt(20)));
        funds_per_miss = Integer.parseInt(getUsefulProp(props.getPropAt(21)));
        funds_required_for_lvlUP = Integer.parseInt(getUsefulProp(props.getPropAt(22)));
        coins_nr = 0; coins_nr_lab.setText("Funds: " + Integer.toString(coins_nr));
        current_sem = Integer.parseInt(getUsefulProp(props.getPropAt(3))); current_sem_lab.setText("Level: " + Integer.toString(current_sem));
        ects_loca = 0; ects_loca_lab.setText("Hit rate: " + Integer.toString(ects_loca));
        time_p = time_per_lvl; time_p_lab.setText(Integer.toString(time_p) + " [s]");

        // On-screen objects initialization.
        try {            
            if("jednolite".equals(getUsefulProp(props.getPropAt(10)))) {
                // props[11] contains RGB description in XXX XXX XXX format.
                // This part of code has to trim this string into 3 independent values and parse them to the integer ( which is format required by other function ).
                String param = getUsefulProp(props.getPropAt(11));
                param = param + " ";
                int[] color = new int[3];
                int end;
                
                for(int i = 0; i < 3 ;i ++) {
                    end = param.indexOf(' ');
                    color[i] = Integer.parseInt(param.substring(0, end));
                    param = param.substring(end + 1, param.length());
                }     
                
                background_native = new SwingNativePicGameObj(0, 0, color[0], color[1], color[2], canvas_width, canvas_height, "prostokąty");
            }else {
                background_ext = new ExtPicGameObj(0, 0, "pictures/background_picture/hall.png", canvas_width, canvas_height);
            }   
            
            bar = new ExtPicGameObj(b_P[0] * canvas_width / NORM, b_P[1] * canvas_height / NORM, "pictures/bar_pictures/bar2.png", b_P[2] * canvas_width / NORM, b_P[3] * canvas_height / NORM);  
            
                // The left side
                info_bar_Ltop = new ExtPicGameObj(bar.getX(), bar.getY() + i_b_Ltop_P[1] * bar.getHeight() / NORM, "pictures/bar_pictures/info_bar2.png", i_b_Ltop_P[2] * bar.getWidth() / NORM, i_b_Ltop_P[3] * bar.getHeight() / NORM);    
                    coin = new ExtPicGameObj(info_bar_Ltop.getX() + info_bar_Ltop.getWidth() - info_bar_Ltop.getHeight() / 2, info_bar_Ltop.getY(), "pictures/bar_pictures/cash.png", info_bar_Ltop.getHeight(), info_bar_Ltop.getHeight());        
                    
                info_bar_Lmid = new ExtPicGameObj(bar.getX(), bar.getY() + i_b_Lmid_P[1] * bar.getHeight() / NORM, "pictures/bar_pictures/info_bar2.png", i_b_Lmid_P[2] * bar.getWidth() / NORM, i_b_Lmid_P[3] * bar.getHeight() / NORM);                
                    current_semester = new ExtPicGameObj(info_bar_Lmid.getX() + info_bar_Lmid.getWidth() - info_bar_Lmid.getHeight() / 2, info_bar_Lmid.getY(), "pictures/bar_pictures/global.png", info_bar_Lmid.getHeight(), info_bar_Lmid.getHeight());   
                    
                info_bar_Lbot = new ExtPicGameObj(bar.getX(), bar.getY() + i_b_Lbot_P[1] * bar.getHeight() / NORM, "pictures/bar_pictures/info_bar2.png", i_b_Lbot_P[2] * bar.getWidth() / NORM, i_b_Lbot_P[3] * bar.getHeight() / NORM);               
                    ects_locally = new ExtPicGameObj(info_bar_Lbot.getX() + info_bar_Lbot.getWidth() - info_bar_Lbot.getHeight() / 2, info_bar_Lbot.getY(), "pictures/bar_pictures/local.png", info_bar_Lbot.getHeight(), info_bar_Lbot.getHeight());    
                    
                // The right side
                info_bar_Rtop = new ExtPicGameObj(bar.getX() + i_b_Rtop_P[0] * bar.getWidth() / NORM, bar.getY() + i_b_Rtop_P[1] * bar.getHeight() / NORM, "pictures/bar_pictures/info_bar2.png", i_b_Rtop_P[2] * bar.getWidth() / NORM, i_b_Rtop_P[3] * bar.getHeight() / NORM);                
                    clock = new ExtPicGameObj(info_bar_Rtop.getX() - info_bar_Rtop.getHeight() / 2, info_bar_Rtop.getY(), "pictures/bar_pictures/time.png", info_bar_Rtop.getHeight(), info_bar_Rtop.getHeight());   
                    
                info_bar_Rmid = new ExtPicGameObj(bar.getX() + i_b_Rmid_P[0] * bar.getWidth() / NORM, bar.getY() + i_b_Rmid_P[1] * bar.getHeight() / NORM, "pictures/bar_pictures/info_bar2.png", i_b_Rmid_P[2] * bar.getWidth() / NORM, i_b_Rmid_P[3] * bar.getHeight() / NORM);                
                    pause = new ExtPicGameObj(info_bar_Rmid.getX() - info_bar_Rmid.getHeight() / 2, info_bar_Rmid.getY(), "pictures/bar_pictures/stop.png", info_bar_Rmid.getHeight(), info_bar_Rmid.getHeight());  
                    
                info_bar_Rbot = new ExtPicGameObj(bar.getX() + i_b_Rbot_P[0] * bar.getWidth() / NORM, bar.getY() + i_b_Rbot_P[1] * bar.getHeight() / NORM, "pictures/bar_pictures/info_bar2.png", i_b_Rbot_P[2] * bar.getWidth() / NORM, i_b_Rbot_P[3] * bar.getHeight() / NORM);           
                    exit = new ExtPicGameObj(info_bar_Rbot.getX() - info_bar_Rbot.getHeight() / 2, info_bar_Rbot.getY(), "pictures/bar_pictures/surrender.png", info_bar_Rbot.getHeight(), info_bar_Rbot.getHeight());   
                    
                // The middle        
                title = new ExtPicGameObj(info_bar_Rmid.getX() - te_P[0] * (bar.getWidth() - 2 * info_bar_Lmid.getWidth()) / NORM, bar.getY() + te_P[1] * bar.getHeight() / NORM, "pictures/bar_pictures/bar2_title.png", te_P[2] * (bar.getWidth() - 2 * info_bar_Lmid.getWidth()) / NORM, te_P[3] * bar.getHeight() / NORM);
                
            // Game objects
            if("figuryGeometryczne".equals(getUsefulProp(props.getPropAt(12)))) {
                player_native = new SwingNativePicGameObj(playerN_P[0] * canvas_width / NORM, playerN_P[1] * canvas_height / NORM, 255, 0, 0, playerN_P[2] * canvas_width / NORM, playerN_P[3] * canvas_height / NORM, getUsefulProp(props.getPropAt(13)));
                bin_native = new SwingNativePicGameObj(binN_P[0] * canvas_width / NORM, binN_P[1] * canvas_height / NORM, 0, 0, 255, binN_P[2] * canvas_width / NORM, binN_P[3] * canvas_height / NORM, getUsefulProp(props.getPropAt(13)));
                ball_native = new SwingNativePicGameObj(ballN_P[0] * canvas_width / NORM, ballN_P[1] * canvas_height / NORM, 0, 255, 0, ballN_P[2] * canvas_width / NORM, ballN_P[3] * canvas_height / NORM, getUsefulProp(props.getPropAt(13)));
                    target_native = new SwingNativePicGameObj(bin_native.getX() + targetN_P[0] * bin_native.getWidth() / NORM, bin_native.getY() + targetN_P[1] * bin_native.getHeight() / 200, 125, 125, 125, ball_native.getWidth(), ball_native.getHeight(), getUsefulProp(props.getPropAt(13)));
            }else {
                player_ext = new ExtPicGameObj(playerE_P[0] * canvas_width / NORM, playerE_P[1] * canvas_height / NORM, "pictures/gameplay_pictures/player_student_no_ball.png", playerE_P[2] * canvas_width / NORM, playerE_P[3] * canvas_height / NORM);
                bin_ext = new ExtPicGameObj(binE_P[0] * canvas_width / NORM, binE_P[1] * canvas_height / NORM, "pictures/gameplay_pictures/bin_empty.png", binE_P[2] * canvas_width / NORM, binE_P[3] * canvas_height / NORM);
                ball_ext = new ExtPicGameObj(ballE_P[0] * canvas_width / NORM, ballE_P[1] * canvas_height / NORM, "pictures/gameplay_pictures/ball_red_light_top.png", ballE_P[2] * canvas_width / NORM, ballE_P[3] * canvas_height / NORM);
                    target_ext = new ExtPicGameObj(bin_ext.getX() + targetE_P[0] * bin_ext.getWidth() / NORM, bin_ext.getY() + targetE_P[1] * bin_ext.getHeight() / NORM, "pictures/gameplay_pictures/target.png", ball_ext.getWidth(), ball_ext.getHeight());
            }   
            
            // Power bar
            power_bar_background = new ExtPicGameObj((canvas_width - pbb_P[2] * canvas_width / NORM) / 2, canvas_height - pbb_P[1] * canvas_height / NORM - pbb_P[3] * canvas_height / NORM, "pictures/bar_pictures/bar3.png", pbb_P[2] * canvas_width / NORM, pbb_P[3] * canvas_height / NORM);
            loading_background = new ExtPicGameObj(power_bar_background.getX() + (power_bar_background.getWidth() - lb_P[2] * power_bar_background.getWidth() / NORM) / 2, power_bar_background.getY() + (power_bar_background.getHeight() - lb_P[3] * power_bar_background.getHeight() / NORM) / 2, "pictures/bar_pictures/info_bar2.png", lb_P[2] * power_bar_background.getWidth() / NORM, lb_P[3] * power_bar_background.getHeight() / NORM);
            loading_bar = new SwingNativePicGameObj(power_bar_background.getX() + (power_bar_background.getWidth() - lb_P[2] * power_bar_background.getWidth() / NORM) / 2, power_bar_background.getY() + (power_bar_background.getHeight() - lb_P[3] * power_bar_background.getHeight() / NORM) / 2, 0, 255, 0, 0, lb_P[3] * power_bar_background.getHeight() / NORM, "prostokąty");
            // Pause
            pause_background = new ExtPicGameObj(0, 0, "pictures/pause_bkgr/pause_bkgr.png", canvas_width, canvas_height);
        }catch(IOException ex) {
            Logger.getLogger(Gameplay.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // On-screen text initialization.
        add(coins_nr_lab); coins_nr_lab.setBounds(info_bar_Ltop.getX() + coin.getWidth() / 2,info_bar_Ltop.getY(), info_bar_Ltop.getWidth() - coin.getWidth() / 2, info_bar_Ltop.getHeight()); coins_nr_lab.setFont(new Font("Serif", Font.BOLD, info_bar_Ltop.getHeight() * 8 / 10)); coins_nr_lab.setForeground(Color.black);
        add(current_sem_lab); current_sem_lab.setBounds(info_bar_Lmid.getX() + current_semester.getWidth() / 2, info_bar_Lmid.getY(), info_bar_Lmid.getWidth() - current_semester.getWidth() / 2, info_bar_Lmid.getHeight()); current_sem_lab.setFont(new Font("Serif", Font.BOLD, info_bar_Lmid.getHeight() * 8 / 10)); current_sem_lab.setForeground(Color.black);
        add(ects_loca_lab); ects_loca_lab.setBounds(info_bar_Lbot.getX() + ects_locally.getWidth() / 2, info_bar_Lbot.getY(), info_bar_Lbot.getWidth() - ects_locally.getWidth() / 2, info_bar_Lbot.getHeight()); ects_loca_lab.setFont(new Font("Serif", Font.BOLD, info_bar_Lbot.getHeight() * 8 / 10)); ects_loca_lab.setForeground(Color.black);
        add(time_p_lab); time_p_lab.setBounds(info_bar_Rtop.getX() + clock.getWidth(), info_bar_Rtop.getY(), info_bar_Rtop.getWidth() - clock.getWidth(), info_bar_Rtop.getHeight()); time_p_lab.setFont(new Font("Serif", Font.BOLD, info_bar_Rtop.getHeight() * 8 / 10)); time_p_lab.setForeground(Color.black);
        add(pause_p_lab); pause_p_lab.setBounds(info_bar_Rmid.getX() + pause.getWidth(), info_bar_Rmid.getY(), info_bar_Rmid.getWidth() - pause.getWidth(), info_bar_Rmid.getHeight()); pause_p_lab.setFont(new Font("Serif", Font.BOLD, info_bar_Rmid.getHeight() * 8 / 10)); pause_p_lab.setForeground(Color.black);
        add(end_game_lab); end_game_lab.setBounds(info_bar_Rbot.getX() + exit.getWidth(), info_bar_Rbot.getY(), info_bar_Rbot.getWidth() - exit.getWidth(), info_bar_Rbot.getHeight()); end_game_lab.setFont(new Font("Serif", Font.BOLD, info_bar_Rbot.getHeight() * 8 / 10)); end_game_lab.setForeground(Color.black);
        repaint();
        
        // "dt" determines lenght of "timer-step". 
        if(canvas_width >= screenSize_width * 95 / 100) {
            dt_bin = 0.1 * Integer.parseInt(getUsefulProp(props.getPropAt(17)));
        }else if(canvas_width >= screenSize_width * 3 / 4 && canvas_width < screenSize_width * 95 / 100) {
            dt_bin = 0.07 * Integer.parseInt(getUsefulProp(props.getPropAt(17)));
        }else if(canvas_width >= screenSize_width * 2 / 4 && canvas_width < screenSize_width * 3 / 4) {
            dt_bin = 0.04 * Integer.parseInt(getUsefulProp(props.getPropAt(17)));
        }else if(canvas_width >= screenSize_width * 1 / 4 && canvas_width < screenSize_width * 2 / 4) {
            dt_bin = 0.02 * Integer.parseInt(getUsefulProp(props.getPropAt(17)));
        }else if(canvas_width < screenSize_width * 1 / 4) {
            dt_bin = 0.008 * Integer.parseInt(getUsefulProp(props.getPropAt(17)));
        }
    }
    
    // Changes the values of internal fields of the class everytime main window resizes.
    public void change_size(int _canvas_width, int _canvas_height) {
        if(tmr_loading.isRunning()) {
            tmr_loading.stop();
            busy_counting = 0;
        }
        
        if(tmr_throw.isRunning()) {
            tmr_throw.stop();
        }
        
        canvas_width = _canvas_width;
        canvas_height = _canvas_height;
        
        if("jednolite".equals(getUsefulProp(props.getPropAt(10)))) {
            background_native.changeParameters(0, 0, canvas_width, canvas_height); 
        }else {
            background_ext.changeParameters(0, 0, canvas_width, canvas_height); 
        }
        
        bar.changeParameters(b_P[0] * canvas_width / NORM, b_P[1] * canvas_height / NORM, b_P[2] * canvas_width / NORM, b_P[3] * canvas_height / NORM);
        
            // The left side
            info_bar_Ltop.changeParameters(bar.getX(), bar.getY() + i_b_Ltop_P[1] * bar.getHeight() / NORM, i_b_Ltop_P[2] * bar.getWidth() / NORM, i_b_Ltop_P[3] * bar.getHeight() / NORM);
                coin.changeParameters(info_bar_Ltop.getX() + info_bar_Ltop.getWidth() - info_bar_Ltop.getHeight() / 2, info_bar_Ltop.getY(), info_bar_Ltop.getHeight(), info_bar_Ltop.getHeight());
                
            info_bar_Lmid.changeParameters(bar.getX(), bar.getY() + i_b_Lmid_P[1] * bar.getHeight() / NORM, i_b_Lmid_P[2] * bar.getWidth() / NORM, i_b_Lmid_P[3] * bar.getHeight() / NORM);
                current_semester.changeParameters(info_bar_Lmid.getX() + info_bar_Lmid.getWidth() - info_bar_Lmid.getHeight() / 2, info_bar_Lmid.getY(), info_bar_Lmid.getHeight(), info_bar_Lmid.getHeight());
                
            info_bar_Lbot.changeParameters(bar.getX(), bar.getY() + i_b_Lbot_P[1] * bar.getHeight() / NORM, i_b_Lbot_P[2] * bar.getWidth() / NORM, i_b_Lbot_P[3] * bar.getHeight() / NORM);
                ects_locally.changeParameters(info_bar_Lbot.getX() + info_bar_Lbot.getWidth() - info_bar_Lbot.getHeight() / 2, info_bar_Lbot.getY(), info_bar_Lbot.getHeight(), info_bar_Lbot.getHeight());
                
            // The right side
            info_bar_Rtop.changeParameters(bar.getX() + i_b_Rtop_P[0] * bar.getWidth() / NORM, bar.getY() + i_b_Rtop_P[1] * bar.getHeight() / NORM, i_b_Rtop_P[2] * bar.getWidth() / NORM, i_b_Rtop_P[3] * bar.getHeight() / NORM);
                clock.changeParameters(info_bar_Rtop.getX() - info_bar_Rtop.getHeight() / 2, info_bar_Rtop.getY(), info_bar_Rtop.getHeight(), info_bar_Rtop.getHeight());
                
            info_bar_Rmid.changeParameters(bar.getX() + i_b_Rmid_P[0] * bar.getWidth() / NORM, bar.getY() + i_b_Rmid_P[1] * bar.getHeight() / NORM, i_b_Rmid_P[2] * bar.getWidth() / NORM, i_b_Rmid_P[3] * bar.getHeight() / NORM);
                pause.changeParameters(info_bar_Rmid.getX() - info_bar_Rmid.getHeight() / 2, info_bar_Rmid.getY(), info_bar_Rmid.getHeight(), info_bar_Rmid.getHeight());
                
            info_bar_Rbot.changeParameters(bar.getX() + i_b_Rbot_P[0] * bar.getWidth() / NORM, bar.getY() + i_b_Rbot_P[1] * bar.getHeight() / NORM, i_b_Rbot_P[2] * bar.getWidth() / NORM, i_b_Rbot_P[3] * bar.getHeight() / NORM);
                exit.changeParameters(info_bar_Rbot.getX() - info_bar_Rbot.getHeight() / 2, info_bar_Rbot.getY(), info_bar_Rbot.getHeight(), info_bar_Rbot.getHeight());
                
            // The middle
            title.changeParameters(info_bar_Rmid.getX() - te_P[0] * (bar.getWidth() - 2 * info_bar_Lmid.getWidth()) / NORM, bar.getY() + te_P[1] * bar.getHeight() / NORM, te_P[2] * (bar.getWidth() - 2 * info_bar_Lmid.getWidth()) / NORM, te_P[3] * bar.getHeight() / NORM);
            
        // Game objects   
        if("figuryGeometryczne".equals(getUsefulProp(props.getPropAt(12)))) {
            player_native.changeParameters(playerN_P[0] * canvas_width / NORM, playerN_P[1] * canvas_height / NORM, playerN_P[2] * canvas_width / NORM, playerN_P[3] * canvas_height / NORM);
            bin_native.changeParameters(binN_P[0] * canvas_width / NORM, binN_P[1] * canvas_height / NORM, binN_P[2] * canvas_width / NORM, binN_P[3] * canvas_height / NORM);
            ball_native.changeParameters(ballN_P[0] * canvas_width / NORM, ballN_P[1] * canvas_height / NORM, ballN_P[2] * canvas_width / NORM, ballN_P[3] * canvas_height / NORM);
                target_native.changeParameters(bin_native.getX() + targetN_P[0] * bin_native.getWidth() / NORM, bin_native.getY() + targetN_P[1] * bin_native.getHeight() / 200, ball_native.getWidth(), ball_native.getHeight());
        }else {
            player_ext.changeParameters(playerE_P[0] * canvas_width / NORM, playerE_P[1] * canvas_height / NORM, playerE_P[2] * canvas_width / NORM, playerE_P[3] * canvas_height / NORM);
            bin_ext.changeParameters(binE_P[0] * canvas_width / NORM, binE_P[1] * canvas_height / NORM, binE_P[2] * canvas_width / NORM, binE_P[3] * canvas_height / NORM);
            ball_ext.changeParameters(ballE_P[0] * canvas_width / NORM, ballE_P[1] * canvas_height / NORM, ballE_P[2] * canvas_width / NORM, ballE_P[3] * canvas_height / NORM);
                target_ext.changeParameters(bin_ext.getX() + targetE_P[0] * bin_ext.getWidth() / NORM, bin_ext.getY() + targetE_P[1] * bin_ext.getHeight() / NORM, ball_ext.getWidth(), ball_ext.getHeight());
        }
        
        // Power bar
        power_bar_background.changeParameters((canvas_width - pbb_P[2] * canvas_width / NORM) / 2, canvas_height - pbb_P[1] * canvas_height / NORM - pbb_P[3] * canvas_height / NORM, pbb_P[2] * canvas_width / NORM, pbb_P[3] * canvas_height / NORM);
        loading_background.changeParameters(power_bar_background.getX() + (power_bar_background.getWidth() - lb_P[2] * power_bar_background.getWidth() / NORM) / 2, power_bar_background.getY() + (power_bar_background.getHeight() - lb_P[3] * power_bar_background.getHeight() / NORM) / 2, lb_P[2] * power_bar_background.getWidth() / NORM, lb_P[3] * power_bar_background.getHeight() / NORM);
        loading_bar.changeParameters(power_bar_background.getX() + (power_bar_background.getWidth() - lb_P[2] * power_bar_background.getWidth() / NORM) / 2, power_bar_background.getY() + (power_bar_background.getHeight() - lb_P[3] * power_bar_background.getHeight() / NORM) / 2, 0, lb_P[3] * power_bar_background.getHeight() / NORM);
        // Pause
        pause_background.changeParameters(0, 0, canvas_width, canvas_height);
        // On-screen text initialization.
        coins_nr_lab.setBounds(info_bar_Ltop.getX() + coin.getWidth() / 2,info_bar_Ltop.getY(), info_bar_Ltop.getWidth() - coin.getWidth() / 2, info_bar_Ltop.getHeight()); coins_nr_lab.setFont(new Font("Serif", Font.BOLD, info_bar_Ltop.getHeight() * 8 / 10)); coins_nr_lab.setForeground(Color.black);
        current_sem_lab.setBounds(info_bar_Lmid.getX() + current_semester.getWidth() / 2, info_bar_Lmid.getY(), info_bar_Lmid.getWidth() - current_semester.getWidth() / 2, info_bar_Lmid.getHeight()); current_sem_lab.setFont(new Font("Serif", Font.BOLD, info_bar_Lmid.getHeight() * 8 / 10)); current_sem_lab.setForeground(Color.black);
        ects_loca_lab.setBounds(info_bar_Lbot.getX() + ects_locally.getWidth() / 2, info_bar_Lbot.getY(), info_bar_Lbot.getWidth() - ects_locally.getWidth() / 2, info_bar_Lbot.getHeight()); ects_loca_lab.setFont(new Font("Serif", Font.BOLD, info_bar_Lbot.getHeight() * 8 / 10)); ects_loca_lab.setForeground(Color.black);
        time_p_lab.setBounds(info_bar_Rtop.getX() + clock.getWidth(), info_bar_Rtop.getY(), info_bar_Rtop.getWidth() - clock.getWidth(), info_bar_Rtop.getHeight()); time_p_lab.setFont(new Font("Serif", Font.BOLD, info_bar_Rtop.getHeight() * 8 / 10)); time_p_lab.setForeground(Color.black);
        pause_p_lab.setBounds(info_bar_Rmid.getX() + pause.getWidth(), info_bar_Rmid.getY(), info_bar_Rmid.getWidth() - pause.getWidth(), info_bar_Rmid.getHeight()); pause_p_lab.setFont(new Font("Serif", Font.BOLD, info_bar_Rmid.getHeight() * 8 / 10)); pause_p_lab.setForeground(Color.black);
        end_game_lab.setBounds(info_bar_Rbot.getX() + exit.getWidth(), info_bar_Rbot.getY(), info_bar_Rbot.getWidth() - exit.getWidth(), info_bar_Rbot.getHeight()); end_game_lab.setFont(new Font("Serif", Font.BOLD, info_bar_Rbot.getHeight() * 8 / 10)); end_game_lab.setForeground(Color.black);  
        repaint();
        
        // "dt" determines lenght of "timer-step". 
        if(canvas_width >= screenSize_width * 95 / 100) {
            dt_bin = 0.1 * Integer.parseInt(getUsefulProp(props.getPropAt(17)));
        }else if(canvas_width >= screenSize_width * 3 / 4 && canvas_width < screenSize_width * 95 / 100) {
            dt_bin = 0.07 * Integer.parseInt(getUsefulProp(props.getPropAt(17)));
        }else if(canvas_width >= screenSize_width * 2 / 4 && canvas_width < screenSize_width * 3 / 4) {
            dt_bin = 0.04 * Integer.parseInt(getUsefulProp(props.getPropAt(17)));
        }else if(canvas_width >= screenSize_width * 1 / 4 && canvas_width < screenSize_width * 2 / 4) {
            dt_bin = 0.02 * Integer.parseInt(getUsefulProp(props.getPropAt(17)));
        }else if(canvas_width < screenSize_width * 1 / 4) {
            dt_bin = 0.008 * Integer.parseInt(getUsefulProp(props.getPropAt(17)));
        }
    }   
    
    // Sets Ball object at its default position.
    private void setBallAtDefaultPos() {
        if("figuryGeometryczne".equals(getUsefulProp(props.getPropAt(12)))) {
            ball_native.changeParameters(ballN_P[0] * canvas_width / NORM, ballN_P[1] * canvas_height / NORM, ballN_P[2] * canvas_width / NORM, ballN_P[3] * canvas_height / NORM);
        }else {
            ball_ext.changeParameters(ballE_P[0] * canvas_width / NORM, ballE_P[1] * canvas_height / NORM, ballE_P[2] * canvas_width / NORM, ballE_P[3] * canvas_height / NORM);
        }
        
        repaint();
    }
    
    // Gets start time.
    public void startT() {  
        if(busy_counting == 0 && tmr_throw.isRunning() == false && isSpacePressed == 0 && paused == 0) {
            tmr_loading.start();
            time_start = System.currentTimeMillis();
            busy_counting = 1;
        }
        
        isSpacePressed = 1;
    }
    
    // Gets stop time, calulates the difference and launches the ball move animation.
    public void stopT() throws IOException, InterruptedException {
        if(busy_counting == 1 && paused == 0) {
            tmr_loading.stop();
            time_end = System.currentTimeMillis();
            busy_counting = 0;
            throwTheBall(time_end - time_start);
        }
        
        isSpacePressed = 0;
    }
    
    // Called to calculate and prepare move and timer parameters.
    private void throwTheBall(long pressed_time) throws IOException, InterruptedException {  
        setBallAtDefaultPos();
               
        if("figuryGeometryczne".equals(getUsefulProp(props.getPropAt(12)))) {        
            if(pressed_time <= TIME) {
                ballDisplacementX = (canvas_width - ball_native.getX()) * 2 * pressed_time / TIME;
            }else if(pressed_time > TIME) {
                ballDisplacementX = (canvas_width - ball_native.getX()) * 2;
            }     
            
            ABCParams = calcFuncABCparams(new double[] {ball_native.getX(), (2 * ball_native.getX() + ballDisplacementX) / 2, ball_native.getX() + ballDisplacementX}, new double[] {ball_native.getY(), canvas_height / 4, canvas_height - 1.4 * ball_native.getHeight()});
            t_throw = ball_native.getX();
        }else {
            if(pressed_time <= TIME) {
                ballDisplacementX = (canvas_width - ball_ext.getX()) * 2 * pressed_time / TIME;
            }else if(pressed_time > TIME) {
                ballDisplacementX = (canvas_width - ball_ext.getX()) * 2;
            }     
            
            ABCParams = calcFuncABCparams(new double[] {ball_ext.getX(), (2 * ball_ext.getX() + ballDisplacementX) / 2, ball_ext.getX() + ballDisplacementX}, new double[] {ball_ext.getY(), canvas_height / 4, canvas_height - 1.4  * ball_ext.getHeight()});
            t_throw = ball_ext.getX();
        }
        
        // "dt" determines lenght of "timer-step". 
        // "Delay" is a time-distance between every rising edge of clock.
        if(canvas_width >= screenSize_width * 95 / 100) {
            dt_throw = 15 * Integer.parseInt(getUsefulProp(props.getPropAt(18)));
            tmr_throw.setDelay(1);
        }else if(canvas_width >= screenSize_width * 3 / 4 && canvas_width < screenSize_width * 95 / 100) {
            dt_throw = 8 * Integer.parseInt(getUsefulProp(props.getPropAt(18)));
            tmr_throw.setDelay(1);
        }else if(canvas_width >= screenSize_width * 2 / 4 && canvas_width < screenSize_width * 3 / 4) {
            dt_throw = 4 * Integer.parseInt(getUsefulProp(props.getPropAt(18)));
            tmr_throw.setDelay(1);
        }else if(canvas_width >= screenSize_width * 1 / 4 && canvas_width < screenSize_width * 2 / 4) {
            dt_throw = 2 * Integer.parseInt(getUsefulProp(props.getPropAt(18)));
            tmr_throw.setDelay(1);
        }else if(canvas_width < screenSize_width * 1 / 4) {
            dt_throw = 1 * Integer.parseInt(getUsefulProp(props.getPropAt(18)));
            tmr_throw.setDelay(2);
        }
        
        tmr_throw.start();
    }
    
    // Called to calculate a, b and c parameters of square function that represents motion-path. A, b and c returned as a array of double.
    private double[] calcFuncABCparams(double[] _x_points, double[] _y_points) {
        double[][] data = new double[3][3];
        double[] rhs = new double[3];

        for(int i = 0; i < 3; i++) {
            double v = 1;
          
            for(int j = 0; j < 3; j++) {
                data[i][3 - j - 1] = v;
                v *= _x_points[i];
            }

            rhs[i] = _y_points[i];
        }

        Matrix m = new Matrix (data);
        Matrix b = new Matrix (rhs, 3);
        return (m.solve(b)).getRowPackedCopy(); 
    }
    
    // Called every time user press "P" key.
    public void pause_unpause() {
        if(paused == 1) {
            paused = 0;
            pause_p_lab.setText("Press \"p\" to pause");
            end_game_lab.setText("Press \"esc\" for a new game");           
            tmr_clk.start();
            tmr_bin.start();
            
            if(tmr_throw_was_running == 1 && was_ended == 0) {
                tmr_throw.start();
                tmr_throw_was_running = 0;
            }else if(tmr_throw_was_running == 1 && was_ended == 1) {
                was_ended = 0;
                tmr_throw_was_running = 0;
            }
        }else if(paused == 0) {           
            paused = 1;
            pause_p_lab.setText("Press \"p\" to start");
            end_game_lab.setText("");
            tmr_clk.stop();
            tmr_bin.stop();
    
            if(tmr_loading.isRunning()) {
                loading_bar.changeParameters(power_bar_background.getX() + (power_bar_background.getWidth() - lb_P[2] * power_bar_background.getWidth() / NORM) / 2, power_bar_background.getY() + (power_bar_background.getHeight() - lb_P[3] * power_bar_background.getHeight() / NORM) / 2, 0, lb_P[3] * power_bar_background.getHeight() / NORM);
                tmr_loading.stop();
                busy_counting = 0; 
                was_ended = 0;
            }else if(tmr_throw.isRunning()) {
                tmr_throw.stop();
                tmr_throw_was_running = 1;
            }else {
                was_ended = 0;
            }
        }
        
        repaint();
    }
    
    // Called every time user press "ESC" key. Sets objects and variables default possitions and values.
    public void new_game() {
        if(paused == 0) {
            was_ended = 1;
            pause_unpause();
            change_size(canvas_width, canvas_height);
            t_bin = 0;
            bin_max_displacement = Integer.parseInt(getUsefulProp(props.getPropAt(16)));
            coins_nr = 0; coins_nr_lab.setText("Funds: " + Integer.toString(coins_nr));
            current_sem = Integer.parseInt(getUsefulProp(props.getPropAt(3))); current_sem_lab.setText("Level: " + Integer.toString(current_sem));
            ects_loca = 0; ects_loca_lab.setText("Hit rate: " + Integer.toString(ects_loca));
            time_p = time_per_lvl; time_p_lab.setText(Integer.toString(time_p) + " [s]");
        }
    }
    
    // Changes the difficulty-connected gameplay params.
    public void change_diff(int change_val) {
        int max_change_val = (Integer.parseInt(getUsefulProp(props.getPropAt(5))) - 1) * Integer.parseInt(getUsefulProp(props.getPropAt(6)));
        TIME = Integer.parseInt(getUsefulProp(props.getPropAt(15))) - Integer.parseInt(getUsefulProp(props.getPropAt(15))) * 75 / 100 * change_val / max_change_val;
    }
    
    // Checks out the state of all important gameplay-connected parameters.
    public void check_for_state_change() {
        if(time_p < 0) {
            time_p = time_per_lvl;
            
            int current_lvl = 0;
            
            if(Integer.parseInt(getUsefulProp(props.getPropAt(3))) == 1) {
                current_lvl = current_sem;
            }else if(Integer.parseInt(getUsefulProp(props.getPropAt(3))) == 0) {
                current_lvl = current_sem + 1; 
            }
            
            if(coins_nr >= funds_required_for_lvlUP * current_lvl && current_sem < Integer.parseInt(getUsefulProp(props.getPropAt(1)))) { 
                current_sem += 1; current_sem_lab.setText("Level: " + Integer.toString(current_sem));
                coins_nr += funds_per_lvlUP; coins_nr_lab.setText("Funds: " + Integer.toString(coins_nr));
                
                try(InputStream input = new FileInputStream("_ext_parameters/" + getUsefulProp(props.getPropAt(2)) + "." + getUsefulProp(props.getPropAt(4)))) {
                    Properties prop = new Properties();
                    prop.load(input);
                    bin_max_displacement = bin_max_displacement + bin_max_displacement * Integer.parseInt(prop.getProperty("db.change")) / 100;
                }catch(IOException ex) {}
            }else if(coins_nr < funds_required_for_lvlUP * current_lvl && current_sem <= Integer.parseInt(getUsefulProp(props.getPropAt(1)))) {
                coins_nr = funds_required_for_lvlUP * (current_lvl - 1); coins_nr_lab.setText("Funds: " + Integer.toString(coins_nr));
            }else if(coins_nr >= funds_required_for_lvlUP * current_lvl && current_sem == Integer.parseInt(getUsefulProp(props.getPropAt(1)))) {

                ////////////////////////////////////////////////////////////////
                hscores.proceed(getCurrentTimeUsingCalendar(), coins_nr);
                ////////////////////////////////////////////////////////////////

                new_game();
                JOptionPane.showMessageDialog(null, "You Win!!!");
            }
        }
    }

    // Gets current system time and saves it as a String.
    private String getCurrentTimeUsingCalendar() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        String formattedDate = dateFormat.format(date);
        return formattedDate;
    }

    // Painting function.
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); 
        
        if("jednolite".equals(getUsefulProp(props.getPropAt(10)))) {
            background_native.paintObj(g);
        }else {
            background_ext.paintObj(g);
        }    
        
        bar.paintObj(g);            
            // The left side
            info_bar_Ltop.paintObj(g);                
                coin.paintObj(g);                
            info_bar_Lmid.paintObj(g);                
                current_semester.paintObj(g);               
            info_bar_Lbot.paintObj(g);                
                ects_locally.paintObj(g);                
            // The right side
            info_bar_Rtop.paintObj(g);               
                clock.paintObj(g);               
            info_bar_Rmid.paintObj(g);               
                pause.paintObj(g);                              
            info_bar_Rbot.paintObj(g);                                 
                exit.paintObj(g);               
            // The middle 
            title.paintObj(g);  
        
        // Game objects 
        if("figuryGeometryczne".equals(getUsefulProp(props.getPropAt(12)))) {
            player_native.paintObj(g);                         
            bin_native.paintObj(g);  
                target_native.paintObj(g);
            ball_native.paintObj(g);  
        }else {
            player_ext.paintObj(g);                             
            bin_ext.paintObj(g);  
                target_ext.paintObj(g);
            ball_ext.paintObj(g);
        }      
        
        // Power bar
        power_bar_background.paintObj(g);
        loading_background.paintObj(g);
        loading_bar.paintObj(g);
        
        // Pause
        if(paused == 1) {
            pause_background.paintObj(g);
        }
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
