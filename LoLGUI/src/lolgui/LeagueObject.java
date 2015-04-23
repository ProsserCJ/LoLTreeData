/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lolgui;

import dto.League.League;

/**
 *
 * @author Nalta
 */
public class LeagueObject extends PanelObject{
    
    public League leagueData;
    
    LeagueObject(League data)
    {
        super(data.getName());
        leagueData = data;
    }
    
}
