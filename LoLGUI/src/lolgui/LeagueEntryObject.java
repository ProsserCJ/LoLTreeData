/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lolgui;

import dto.League.LeagueEntry;
import java.awt.Color;
import java.io.Serializable;

/**
 *
 * @author Nalta
 */
public class LeagueEntryObject extends PanelObject implements Serializable {
    
    public LeagueEntry entryData;
    
    public LeagueEntryObject() {
        super("");
        entryData = new LeagueEntry();
    }
    public LeagueEntryObject(LeagueEntry le){
        super(le.getPlayerOrTeamName());
        super.fillColor = new Color(150,255,150);
        entryData=le;
    }
            
}
