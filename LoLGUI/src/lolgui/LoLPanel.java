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

/**
 *
 * @author PROSSERCJ1
 */
public class LoLPanel extends JPanel {
    Map<String, Set<League>> leagueData;
    
    public LoLPanel() {
        deserializeData();
        
        for (String tier : leagueData.keySet()) {
            System.out.println(tier);
            for (League league : leagueData.get(tier)) {
                System.out.println("\t" + league.getName() + "(" + league.getEntries().size() + ")");
            }
        }
    }

    public void deserializeData() {
      try {
         FileInputStream fileIn = new FileInputStream("leagueData.ser");
         ObjectInputStream in = new ObjectInputStream(fileIn);
         leagueData = (HashMap<String, Set<League>>)in.readObject();
         in.close();
         fileIn.close();
         System.out.println("Serialized data loaded from leagueData.ser");
      }
      catch(Exception e) {
          leagueData = new HashMap<>();
          System.err.println(e.getMessage());
      }
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        
        g.drawString(leagueData.toString(),15,15);
    }
}
