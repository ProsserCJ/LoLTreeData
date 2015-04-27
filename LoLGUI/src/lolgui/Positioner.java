/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lolgui;

import dto.League.League;
import dto.League.LeagueEntry;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import static java.lang.Integer.max;
import static java.lang.Integer.min;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
/**
 *
 * @author Nalta
 */
public class Positioner {
    
    private Rectangle bounds;
    
    public void setSize(int width, int height){
        bounds.width = width;
        bounds.height = height;
    }
    
    Positioner(int width, int height){
        bounds = new Rectangle(0,0,width,height);
    }
    
    private ArrayList<QueryObject> queries = new ArrayList();
    
    private ArrayList<LeagueObject> leagueObjects = new ArrayList();
    
    private ArrayList<TeamObject> teamObjects = new ArrayList();
    
    private ArrayList<LeagueEntryObject> lEntryObjects = new ArrayList();
    
    public void setQueries(ArrayList<QueryObject> q){queries=q;}
    
    public void setResults(ArrayList<LeagueObject> l){leagueObjects=l;}
    
    public void setQueryResults(ArrayList<LeagueEntry> le){
        //TODO: fill out this method
        
    }
    
    private PanelObject clickedItem = null;
    
    public void paintAll(Graphics g)
    {
        for(TeamObject t : teamObjects)
            t.paint(g);
        for(QueryObject q : queries)
            q.paint(g);
        for(LeagueObject l : leagueObjects)
            l.paint(g);
        for(LeagueEntryObject le : lEntryObjects)
            le.paint(g);
        
    }
    
    public void addTeam(Team t){
        TeamObject teamObj = new TeamObject(t);
        teamObj.setCenter(this.bounds.width/2,this.bounds.height/10);
        this.teamObjects.add(teamObj);                
    }
    
    public void addQueryResults(String queryString, List<League> leagueList)
    {
        QueryObject q = new QueryObject(queryString);
        
        for(League l : leagueList)
        {
            boolean foundObj = false;
            for(LeagueObject lObj : leagueObjects)
            {
                if(lObj.leagueData == l)
                {
                    foundObj = true;
                    q.addRemoveLink(lObj);
                    break;
                }
            }
            if(!foundObj)
            {
                LeagueObject lObj = new LeagueObject(l);
                leagueObjects.add(lObj);
                q.addRemoveLink(lObj);
                addLeagueEntries(lObj);
            }
        }
        
        queries.add(q);
    }
    
    public void positionQueryResults()
    {
    
        int centerX = bounds.width/2;
        int centerY = bounds.height/2;
        
        HashMap<Integer,Integer> incomingLinksVSCount = new HashMap();
        
        for(LeagueObject l : leagueObjects)
        {
            int lCount = l.getIncomingLinks();
            if(incomingLinksVSCount.containsKey(lCount))
            {
                int currCount = incomingLinksVSCount.get(lCount);
                incomingLinksVSCount.put(lCount, currCount+1);
            }
            else
            {
                incomingLinksVSCount.put(lCount,1);
            }
        }
        
        int maxNumberOfLinks = -1;
        
        for(int l : incomingLinksVSCount.keySet())
            maxNumberOfLinks = max(maxNumberOfLinks,l);
        
        //sorted by number of links, outer ones should have more links than inner ones
        Collections.sort(leagueObjects, (Object o1, Object o2) -> {return ((PanelObject)o1).getIncomingLinks()-((PanelObject)o2).getIncomingLinks();});
        
        double currentAngle = 0;
        int currentNumberOfLinks = 1;
        
        double maxDistFromCenter = (min(bounds.width,bounds.height)/2.0)-50;
        
        for(LeagueObject l : leagueObjects)
        {
            double lineWidth = maxDistFromCenter * (1-((l.getIncomingLinks()-1)/(double)maxNumberOfLinks));
            
            int pointX = (int)(lineWidth * cos(currentAngle));
            int pointY = (int)(lineWidth * sin(currentAngle));
            
            l.setCenter(pointX+centerX, pointY+centerY);
            
            currentAngle+=3.14159265*2/incomingLinksVSCount.get(l.getIncomingLinks());
            
            if(currentNumberOfLinks < l.getIncomingLinks())
            {
                currentNumberOfLinks = l.getIncomingLinks();
                //currentAngle=0;
            }
            
            double childAngle = 0;
            double childAngleStep = 2*3.141592 / l.leagueData.getEntries().size();
            double childLineLength = 100;
            for(PanelObject entryObj : l.getLinks()){
                entryObj.visible=false;
                int dX = (int)(childLineLength * cos(childAngle));
                int dY = (int)(childLineLength * sin(childAngle));
                Point disp = new Point(dX,dY);
                entryObj.setCenter(add(l.getCenter(),disp));
                childAngle += childAngleStep;
            }
            
            
        }
        
        currentAngle = 3.14159265*.25;
        
        for(QueryObject q : queries)
        {
            double lineWidth = maxDistFromCenter * (0.3)/(double)maxNumberOfLinks;
            
            int pointX = (int)(lineWidth * cos(currentAngle));
            int pointY = (int)(lineWidth * sin(currentAngle));
            
            q.setCenter(pointX+centerX, pointY+centerY);
            
            currentAngle+=3.14159265*2/queries.size();
        }
        
        
        
    }
    
    public void handleDoubleClick(Point p){
        for(TeamObject t : teamObjects)
            if(t.checkClick(p)) t.toggleChildrenVisibility();
        for(QueryObject q : queries)
            if(q.checkClick(p)) q.toggleChildrenVisibility();
        for(LeagueObject l : leagueObjects)
            if(l.checkClick(p)) l.toggleChildrenVisibility();
        for(LeagueEntryObject le : lEntryObjects)
            if(le.checkClick(p)) le.toggleChildrenVisibility();
    }
    
    //assumes a player only belongs to one league
    private void addLeagueEntries(LeagueObject l){
        for(LeagueEntry le : l.leagueData.getEntries()){
            LeagueEntryObject entryObj = new LeagueEntryObject(le);
            l.addRemoveLink(entryObj);
            this.lEntryObjects.add(entryObj);
        }
    }
    
    
    
    void checkClick(Point p, boolean altClick)
    {
        //adding link
        if(altClick && this.clickedItem!=null)
        {
            PanelObject newlyClicked = null;
            for(TeamObject t :teamObjects)
            {
                if(t.checkClick(p))newlyClicked = t;
            }
            for(LeagueEntryObject le :lEntryObjects)
            {
                if(le.checkClick(p))newlyClicked = le;
            }
            
            if(newlyClicked instanceof LeagueEntryObject && clickedItem instanceof TeamObject)
            {
                clickedItem.addRemoveLink(newlyClicked);
            }
            if(newlyClicked instanceof TeamObject && clickedItem instanceof LeagueEntryObject)
            {
                newlyClicked.addRemoveLink(clickedItem);
            }
        }
        else
        {
            this.clickedItem = null;
            for(QueryObject q :queries)
            {
                q.refresh();
            }
            for(TeamObject t :teamObjects)
            {
                t.refresh();
            }
            for(QueryObject q :queries)
            {
                if(q.checkClick(p)){
                    q.select();
                    this.clickedItem=q;
                    return;
                }
            }
            for(LeagueObject l : leagueObjects)
            {
                if(l.checkClick(p)){                
                    l.select();
                    this.clickedItem=l;
                    return;
                }
            }
            for(TeamObject t : teamObjects)
            {
                if(t.checkClick(p)){                
                    t.select();
                    this.clickedItem=t;
                    return;
                }
            }
            for(LeagueEntryObject le :lEntryObjects)
            {
                if(le.checkClick(p)){                
                    le.select();
                    this.clickedItem=le;
                    return;
                }
            }
        }
    }
    
    public void moveSelected(int x, int y){
        
        if(clickedItem!=null)
            //also moves children
            this.clickedItem.move(x,y);
        else{
            Point offset = new Point(x,y);
            for(TeamObject t : teamObjects)
                t.setCenter(add(t.getCenter(),offset));
            for(QueryObject q : queries)
                q.setCenter(add(q.getCenter(),offset));
            for(LeagueObject l : leagueObjects)
                l.setCenter(add(l.getCenter(),offset));
            for(LeagueEntryObject le : lEntryObjects)
                le.setCenter(add(le.getCenter(),offset));
        }
    }
    
    
    private Point add(Point p1, Point p2)
    {
        return new Point(p1.x+p2.x,p1.y+p2.y);
    }
    
    
}
