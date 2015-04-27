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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author PROSSERCJ1
 */

public class LoLPanel extends JPanel {
    Map<String, Set<League>> leagueData;
    Map<String, String> championData;
    List<Team> teamData;
    
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
      FileInputStream fileIn;
      ObjectInputStream in;
              
      try {
         fileIn = new FileInputStream("leagueData.ser");
         in = new ObjectInputStream(fileIn);
         leagueData = (HashMap<String, Set<League>>)in.readObject();
         in.close();
         fileIn.close();
         System.out.println("Serialized data loaded from leagueData.ser");
      }
      catch(Exception e) {
          leagueData = new HashMap<>();
          System.err.println("Failed to load leagueData.ser");
      }
      try{ 
         fileIn = new FileInputStream("championData.ser");
         in = new ObjectInputStream(fileIn);
         championData = (HashMap<String, String>)in.readObject();
         in.close();
         fileIn.close();
         System.out.println("Serialized data loaded from championData.ser");
      } catch(Exception e) {
         championData = new HashMap<>();
         System.err.println("Failed to load championData.ser");
      }
      
      try{   
         fileIn = new FileInputStream("teamData.ser");
         in = new ObjectInputStream(fileIn);
         teamData = (List<Team>)in.readObject();
         in.close();
         fileIn.close();
         System.out.println("Serialized data loaded from teamData.ser");
      } catch(Exception e) {
         teamData = new ArrayList<>();
         System.err.println("Failed to load teamData.ser");
      }
    }
    
    public void serializeData() {
           try {
            FileOutputStream fileOut = new FileOutputStream("teamData.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(teamData);
            out.flush();
            out.close();
            fileOut.close();
            System.out.println("Serialized team data to teamData.ser with " + teamData.size() + " entries.");
         } catch(Exception e) {
             System.err.println(e.getMessage());
         }
    }
    
    public void getResults(String tierStr, String leagueStr, String championStr, boolean rookie, boolean hotStreak, boolean veteran, int matches){
        TreeSet<LeagueEntry> s = new TreeSet();
        
        for (String tier : leagueData.keySet()) {
            for (League league : leagueData.get(tier)) {
                for (LeagueEntry entry : league.getEntries()) {
                    int fits = 0;
                    if(tier.equalsIgnoreCase(tierStr))
                        ++fits;
                    if(league.getName().equalsIgnoreCase(leagueStr))
                        ++fits;
                    if(championData.get(entry.getPlayerOrTeamId()).equalsIgnoreCase(championStr))
                        ++fits;
                    if(rookie == entry.isFreshBlood())
                        ++fits;
                    if(hotStreak == entry.isHotStreak())
                        ++fits;
                    if(veteran == entry.isVeteran())
                        ++fits;
                    if(fits >= matches)
                        s.add(entry);
                }
            }
        } 
        positioner.setResults(s);
        //return s;
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
