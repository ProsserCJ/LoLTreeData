
package lolgui;

import java.awt.Color;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Nalta
 */
public class TeamObject extends PanelObject{
    public Team teamData;
    public TeamObject(Team t){
        super(t.getName());
        super.fillColor = new Color(150,255,255);
        teamData = t;
    }
}
