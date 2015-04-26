/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lolgui;

import dto.League.League;
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
    
    public void setQueries(ArrayList<QueryObject> q){queries=q;}
    
    public void setResults(ArrayList<LeagueObject> l){leagueObjects=l;}
    
    private PanelObject clickedItem = null;
    
    public void paintAll(Graphics g)
    {
        for(QueryObject q : queries)
            q.paint(g);
        for(LeagueObject l : leagueObjects)
            l.paint(g);
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
                    q.addLink(lObj);
                    break;
                }
            }
            if(!foundObj)
            {
                LeagueObject lObj = new LeagueObject(l);
                leagueObjects.add(lObj);
                q.addLink(lObj);
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
    
    
    void checkClick(Point p)
    {
        this.clickedItem = null;
        for(QueryObject q :queries)
        {
            q.refresh();
        }
        for(QueryObject q :queries)
        {
            if(q.checkClick(p)){
                q.select();
                this.clickedItem=q;
            }
        }
        for(LeagueObject l : leagueObjects)
        {
            if(l.checkClick(p)){                
                l.select();
                this.clickedItem=l;
            }
        }
    }
    
    public void moveSelected(int x, int y){
        if(clickedItem!=null)
            this.clickedItem.setCenter(x,y);
    }
}
