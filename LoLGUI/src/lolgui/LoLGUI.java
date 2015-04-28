/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lolgui;

import dto.League.LeagueEntry;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
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

public class LoLGUI extends JFrame implements ActionListener, Serializable{
    Point DEFAULTBASE = new Point(50,50);
    final int DEFAULTWIDTH = 1000, DEFAULTHEIGHT = 700, MAXVERTICES = 50, MINVERTICES = 3;

    LoLPanel panel;
    
    
    GridLayout grid = new GridLayout(2,8);
    JPanel top = new JPanel(grid);
    JButton searchButton = new JButton("Search");
    
    JLabel tierLabel = new JLabel("Tier: ", SwingConstants.RIGHT);
    JTextField tierField = new JTextField("",10);
    JLabel leagueLabel = new JLabel("League: ", SwingConstants.RIGHT);
    JTextField leagueField = new JTextField("",10);
    JLabel championLabel = new JLabel("Champion: ", SwingConstants.RIGHT);
    JTextField championField = new JTextField("",10);
    
    JLabel rookieLabel = new JLabel("Rookie: ", SwingConstants.RIGHT);
    JCheckBox rookieBox = new JCheckBox();
    JLabel hotStreakLabel = new JLabel("Hot Streak: ", SwingConstants.RIGHT);
    JCheckBox hotStreakBox = new JCheckBox();
    JLabel veteranLabel = new JLabel("Veteran: ", SwingConstants.RIGHT);
    JCheckBox veteranBox = new JCheckBox();
    
    JLabel matchesLabel = new JLabel("Req. Matches: ", SwingConstants.RIGHT);
    JTextField matchesField = new JTextField("6",10);
    
    JPanel left = new JPanel(new GridLayout(3,1));
    JButton addTeamButton = new JButton("Create Team");
    JButton saveButton = new JButton("Save");
    JButton loadButton = new JButton("Load");
    
    public LoLGUI() {
        initComponents();
    }
    private void initComponents() {
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE);        
        setBounds(DEFAULTBASE.x,DEFAULTBASE.y, DEFAULTWIDTH, DEFAULTHEIGHT);
        
        panel = new LoLPanel(DEFAULTWIDTH,DEFAULTHEIGHT);
        left.add(addTeamButton, grid);
        left.add(saveButton, grid);
        left.add(loadButton, grid);
        getContentPane().add(left, BorderLayout.WEST);
        
        searchButton.addActionListener(this);
        
        top.add(tierLabel, grid);
        top.add(tierField, grid);
        top.add(leagueLabel, grid);
        top.add(leagueField, grid);
        top.add(championLabel, grid);
        top.add(championField, grid);
        top.add(matchesLabel,grid);
        top.add(matchesField,grid);
        
        top.add(rookieLabel, grid);
        top.add(rookieBox, grid);
        top.add(hotStreakLabel, grid);
        top.add(hotStreakBox, grid);
        top.add(veteranLabel, grid);
        top.add(veteranBox, grid);
        
        top.add(new JPanel(),grid);//filler
        top.add(searchButton, grid);
        
        getContentPane().add(panel);
        getContentPane().add(top, BorderLayout.NORTH);
        
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                // do stuff   
                panel.setSize(getWidth(), getHeight());
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
                    if (!newTeam.getName().equals("")) panel.addTeam(newTeam);
                  }
                });      
                dialog.show();
            }
         }); 
         
         saveButton.addActionListener(new ActionListener(){
            @Override
             public void actionPerformed(ActionEvent ae) {
               // panel.lastMouseClick = null;
                panel.positioner.refresh();
                serializePanel();
            }
         });
         
         loadButton.addActionListener(new ActionListener(){
            @Override
             public void actionPerformed(ActionEvent ae) {
                try { 
                    LoLPanel temp = panel;
                    panel = deserializePanel();
                    getContentPane().remove(temp);
                    getContentPane().add(panel);
                    repaint();
                }
                catch(Exception e) { 
                   System.out.print(e.getMessage());
                }
             }
         });
         
   }
    @Override
    public void actionPerformed(ActionEvent ae) {
        try{
            int matchesNum = Integer.parseInt(matchesField.getText());
            if(matchesNum < 0 || matchesNum > 6)
                throw new Exception();
            panel.getResults(tierField.getText(), leagueField.getText(), championField.getText()
                , rookieBox.isSelected(), hotStreakBox.isSelected(), veteranBox.isSelected()
                , matchesNum);
            repaint();
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(this, "Req. Matches must be an Integer from 0 to 6 (the number of matches required for selection)");
        }
    }
    
    private LoLPanel deserializePanel() throws Exception {
         FileInputStream fileIn = new FileInputStream("panel.ser");
         ObjectInputStream in = new ObjectInputStream(fileIn);
         panel = (LoLPanel)in.readObject();
         in.close();
         fileIn.close();
         System.out.println("Serialized panel loaded from panel.ser");
         return panel;
    }
    
    private void serializePanel() {
        try {
         panel.positioner.refresh();
         FileOutputStream fileOut = new FileOutputStream("panel.ser");
         ObjectOutputStream out = new ObjectOutputStream(fileOut);
         out.writeObject(panel);
         out.flush();
         out.close();
         fileOut.close();
         System.out.println("Panel serialized to panel.ser");
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoLGUI().setVisible(true);
            }
        });
    } 
}
