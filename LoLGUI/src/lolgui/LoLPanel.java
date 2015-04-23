/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lolgui;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.JPanel;
import dto.League.League;
import dto.League.LeagueEntry;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author PROSSERCJ1
 */
public class LoLPanel extends JPanel {
    Map<String, Set<League>> leagueData;
    Map<String, String> championData;
    
    Positioner positioner;
    
    public LoLPanel() {
        super();
        setLayout(null);
        
        positioner = new Positioner(400,400);
        
        deserializeData();
        
//        for (String tier : leagueData.keySet()) {
//            System.out.println(tier);
//            for (League league : leagueData.get(tier)) {
//                System.out.println("\t" + league.getName());
//                for (LeagueEntry entry : league.getEntries()) {
//                    System.out.println("\t\t" + entry.getPlayerOrTeamName() + " plays " + championData.get(entry.getPlayerOrTeamId()));
//                }
//            }
//        } 
        
         addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e)
            {
                checkClick(e.getPoint());
                repaint();
            }
        });
        
        
        //seting test data
        ArrayList<League> searchResults = new ArrayList();
        for(League l : leagueData.get("MASTER"))
        {
            searchResults.add(l);
        }
        
        positioner.addQueryResults("MASTER LEAGUE", searchResults);
        
        //seting test data
        searchResults = new ArrayList();
        for(League l : leagueData.get("GOLD"))
        {
            searchResults.add(l);
        }
        positioner.addQueryResults("GOLD LEAGUE", searchResults);
        
        
        //seting test data
        searchResults = new ArrayList();
        searchResults.add(leagueData.get("GOLD").iterator().next());
        searchResults.add(leagueData.get("MASTER").iterator().next());
        positioner.addQueryResults("FIRST OF EACH", searchResults);
        
        
        positioner.positionQueryResults();
        
    }

    public void deserializeData() {
      try {
         FileInputStream fileIn = new FileInputStream("leagueData.ser");
         ObjectInputStream in = new ObjectInputStream(fileIn);
         leagueData = (HashMap<String, Set<League>>)in.readObject();
         in.close();
         fileIn.close();
         System.out.println("Serialized data loaded from leagueData.ser");
         
         fileIn = new FileInputStream("championData.ser");
         in = new ObjectInputStream(fileIn);
         championData = (HashMap<String, String>)in.readObject();
         in.close();
         fileIn.close();
         System.out.println("Serialized data loaded from championData.ser");
      }
      catch(Exception e) {
          leagueData = new HashMap<>();
          System.err.println(e.getMessage());
      }
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        positioner.paintAll(g);
        
    }
    
    public void loadTier(){
        
    }
    
    @Override
    public void setSize(int width, int height)
    {
        positioner.setSize(width,height);
        positioner.positionQueryResults();
    }
    
    public void checkClick(Point p){
        positioner.checkClick(p);
        
    }

}
