/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lolgui;

import dto.League.LeagueEntry;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 *
 * @author PROSSERCJ1
 */

class Team implements Serializable {
    private String name = "";
    private List<LeagueEntry> players = new ArrayList<>();

    List<LeagueEntry> getPlayers() { return players; }
    String getName() { return name; }
    void setName(String s){ name = s; }
    void addPlayer(LeagueEntry l) { players.add(l); }
}


public class LoLGUI extends JFrame{
    Point DEFAULTBASE = new Point(50,50);
    final int DEFAULTWIDTH = 500, DEFAULTHEIGHT = 500, MAXVERTICES = 50, MINVERTICES = 3;

    LoLPanel panel;
    JButton addTeamButton;
    
    public LoLGUI() {
        initComponents();
    }
    private void initComponents() {
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE);        
        setBounds(DEFAULTBASE.x,DEFAULTBASE.y, DEFAULTWIDTH, DEFAULTHEIGHT);
        panel = new LoLPanel();
        addTeamButton = new JButton("Create Team");
        getContentPane().add(addTeamButton, BorderLayout.WEST);
        getContentPane().add(panel);
        
        
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                // do stuff   
                panel.setSize(getWidth(), getHeight());
            }
        });
        
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                panel.serializeData();
            }
        });
        
         addTeamButton.addActionListener(new ActionListener() 
         {
            @Override
            public void actionPerformed(ActionEvent ae) {
                JDialog dialog = new JDialog();
                Team newTeam = new Team();
                dialog.setSize(300,150);
                dialog.setContentPane(new TeamEditPanel(dialog, newTeam));
                dialog.setLocationRelativeTo(panel);
                dialog.addWindowListener(new WindowAdapter()
                {
                  @Override
                  public void windowClosed(WindowEvent e) {
                    if (!newTeam.getName().equals("")) panel.teamData.add(newTeam);
                  }
                });      
                dialog.show();
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
