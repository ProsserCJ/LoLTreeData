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
import java.awt.event.MouseMotionListener;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 *
 * @author PROSSERCJ1
 */

public class LoLPanel extends JPanel implements MouseMotionListener, Serializable{
    Map<String, Set<League>> leagueData;
    Map<String, String> championData;
    private List<Team> teamData;
    
    Point lastMouseClick;
    
    Positioner positioner;
    
    public LoLPanel(int w, int h) {
        super();
        setLayout(null);
        
        positioner = new Positioner(w,h);
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
            @Override
            public void mousePressed(MouseEvent e)
            {
                lastMouseClick = e.getPoint();
                positioner.checkClick(lastMouseClick,e.isShiftDown());
                repaint();
            }
            @Override
            public void mouseClicked(MouseEvent e){
                
                if(SwingUtilities.isRightMouseButton(e))
                {
                    positioner.makeLinksOrbit();
                }
                else
                {
                    if(e.getClickCount()>=2){
                        positioner.handleDoubleClick(e.getPoint());
                    }
                }
                repaint();
            }
        });
        addMouseMotionListener(this);
        
        
        for(String tier : leagueData.keySet())
        {
        
            //seting test data
            ArrayList<League> searchResults = new ArrayList();
            for(League l : leagueData.get(tier))
            {
                searchResults.add(l);
            }

            positioner.addQueryResults(tier, searchResults);
        }
        
        positioner.positionQueryResults();
        
        repaint();
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
        ArrayList<LeagueEntry> s = new ArrayList();
        
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
        positioner.setQueryResults(s);
        //return s;
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        positioner.paintAll(g); 
    }
    
    public void addTeam(Team t)
    {
        teamData.add(t);
        positioner.addTeam(t);
        this.repaint();
    }
    
    @Override
    public void setSize(int width, int height)
    {
        positioner.setSize(width,height);
    }
    
     public void mouseMoved(MouseEvent e) {
         //don't care
    }

    public void mouseDragged(MouseEvent e) {
        try {positioner.moveSelected(e.getX()-lastMouseClick.x,e.getY()-lastMouseClick.y, e.isControlDown()); }
        catch(Exception ex)
        {
            System.out.print(ex.getMessage());
        }
        repaint();
        lastMouseClick = e.getPoint();
    }
}
