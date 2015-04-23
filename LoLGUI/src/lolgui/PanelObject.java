/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lolgui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;



/**
 *
 * @author Nalta
 */
public class PanelObject {
   
    private final int MAX_STR_LENGTH = 15;
    
    private ArrayList<PanelObject> links = new ArrayList();
    
    private String info;
    
    protected Color boxOutlineColor = new Color(0,0,0);
    protected Color fillColor = new Color(255,255,255);    
    protected Color lineColor = new Color(0,0,0);   
    protected Color selectedColor = new Color(255,0,0); 
    protected Color fontColor = new Color(0,0,0);
    
    private int linksToMe = 0;
    public int getIncomingLinks(){return linksToMe;}
    
    private Rectangle bounds = new Rectangle(); 
    
    private Point center = new Point();
    
    private boolean selected = false;
    
    PanelObject(String s)
    {
        setString(s);
        int defaultFontSize = 20;
        bounds.width = info.length()*defaultFontSize;
        bounds.height = defaultFontSize;
    }
    
    public void setCenter(int x, int y){
        center.x = x;
        center.y = y;
    }
    
    public void paint(Graphics g){
         if(selected)
            g.setColor(selectedColor);
        else
            g.setColor(lineColor);
        //draw lines before text
        for(PanelObject p : links)
        {
            g.drawLine(center.x, center.y, p.center.x, p.center.y);
        }
        
        //get string width and height
        bounds = g.getFontMetrics().getStringBounds(info, g).getBounds();
        bounds.width+=4;//margin
        
        //reset position
        bounds.x = (int)(center.x-(bounds.getWidth()/2));
        bounds.y = (int)(center.y-(bounds.getHeight()/2));
        
        if(selected)
            g.setColor(selectedColor);
        else
            g.setColor(fillColor);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        
        g.setColor(boxOutlineColor);
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        
        //draw string centered at the point
        g.setColor(fontColor);
        g.drawString(info, bounds.x+2, bounds.y+bounds.height-2);
                  
    }
    
    public Rectangle getSize(){
        return new Rectangle(0,0,bounds.width,bounds.height-2);
    }
    
    public void checkClick(Point p)
    {
        if(bounds.contains(p))
            select();
    }
    
    public void addLink(PanelObject o)    {
        o.linksToMe++;
        links.add(o);
    }
    
    public final void setString(String s)
    {
        if(s.length()>MAX_STR_LENGTH)
        {
            s = s.substring(0, MAX_STR_LENGTH-3)+"...";
        }
        info=s;
    }
    
    public final void select()
    {
        selected = true;
        for(PanelObject p : links){
            p.select();
        }
    }
    
    public final void refresh()
    {
        selected = false;
        for(PanelObject p : links){
            p.refresh();
        }
    }
    
}
