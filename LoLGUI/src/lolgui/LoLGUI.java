/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lolgui;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 *
 * @author PROSSERCJ1
 */

public class LoLGUI extends JFrame{
    Point DEFAULTBASE = new Point(50,50);
    final int DEFAULTWIDTH = 500, DEFAULTHEIGHT = 500, MAXVERTICES = 50, MINVERTICES = 3;

    LoLPanel panel;
    
    public LoLGUI() {
        initComponents();
    }
    private void initComponents() {
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE);        
        setBounds(DEFAULTBASE.x,DEFAULTBASE.y, DEFAULTWIDTH, DEFAULTHEIGHT);
        panel = new LoLPanel();
        getContentPane().add(panel);
        
        
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                // do stuff   
                panel.setSize(getWidth(), getHeight());
            }
        });
        
       
        
    }
    
    
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoLGUI().setVisible(true);
            }
        });
    } 

    
}
