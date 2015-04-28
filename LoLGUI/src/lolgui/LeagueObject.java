/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lolgui;

import dto.League.League;
import java.awt.Color;
import java.io.Serializable;

/**
 *
 * @author Nalta
 */
public class LeagueObject extends PanelObject implements Serializable{
    
    public League leagueData;
    
    LeagueObject(){
        super("");
        leagueData = new League();
    }
            
    LeagueObject(League data)
    {
        super(data.getName());
        leagueData = data;
        super.fillColor = new Color(255,255,150);
    }
    
}
