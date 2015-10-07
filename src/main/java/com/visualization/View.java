package com.visualization;

//import infovis.debug.Debug;
//import infovis.scatterplot.Range;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.apache.commons.lang3.ArrayUtils;




public class View extends JPanel{
	Dimension screensize;
	Integer left,right,top,bottom,leftBraid,rightBraid,topBraid,bottomBraid,leftStacked,rightStacked
	,verticalLineGap,horizontalLineGap,starCenterX,starCenterY,bottomStacked;
	int overallMax = 0,overallMin = Integer.MAX_VALUE;
	
	double rate = 1,rateStacked;
    private ArrayList<Color> colors = new ArrayList<Color>();
    private Line2D bottomLine,bottomBraidLine;
	
	ArrayList<Data> d,dBraid,dStacked;
	ArrayList<Range> r;
	ArrayList<Path2D> pathsStacked = new ArrayList<Path2D>();

	ArrayList<Path2D> areas = new ArrayList<Path2D>();
	ArrayList<Path2D> areasStacked = new ArrayList<Path2D>();
	Point2D clicked;
	Model m;
	
	int screenMode = 1;
	ArrayList<Data> dAll = new ArrayList<Data>();
	int dPointerOn = -1;
	int dSelected = -1;
	boolean isFirst = true;
	public void setModel(Model m)
	{
		this.m = m;
	}

	public Model getModel() {
		// TODO Auto-generated method stub
		return this.m;
	}
	
	public View()
	{
		
	}
	
	public void setupScreen()
	{
		//set colors for different values of chart
		colors.add(new Color(122, 62, 72));
		colors.add(new Color(238, 205, 134));
		colors.add(new Color(225, 137, 66));
		colors.add(new Color(185, 88, 53));
		colors.add(new Color(61, 50, 66));
		colors.add(new Color(110, 130, 67));
		colors.add(new Color(199, 178, 111));//
		colors.add(new Color(182, 206, 182));
		colors.add(new Color(46, 56, 47));
		colors.add(new Color(147, 102, 99));
		colors.add(new Color(72, 64, 67));
		colors.add(new Color(110, 101, 104));
		colors.add(new Color(234, 211, 168));
		colors.add(new Color(210, 54, 65));
		colors.add(new Color(119, 17, 0));//
		colors.add(new Color(204, 102, 51));
		colors.add(new Color(255, 153, 0));
		colors.add(new Color(170, 0, 120));
		colors.add(new Color(0, 0, 120));
		colors.add(new Color(179, 179, 215));
		colors.add(new Color(209, 202, 176));
		
		//download values into model from ssv file, you may want to check data,model and range classes
		m.importValues();
		
	    screensize = Toolkit.getDefaultToolkit().getScreenSize();
	    
	    //set left right top and bottom of chart, so we can calculate the points based on that edges
	    left = (int) screensize.getWidth()/70;
	    top = (int) screensize.getHeight()/40;
	    
	    bottom = (int) (screensize.getHeight() - (screensize.getHeight()/1.5));
		right = (int) (screensize.getWidth() - (screensize.getWidth()/1.5));
		
		bottomStacked = bottom - ((bottom - top)/2);
		
	    r = m.getRanges();
	    
		for (int i = 0; i < r.size(); i++) {
			if(overallMax < r.get(i).getMax())
			{
				overallMax = (int) r.get(i).getMax();
			}
			
			if(overallMin > r.get(i).getMin())
			{
				overallMin = (int) r.get(i).getMin();
			}
		}
		
		verticalLineGap = (bottom - top) / 10;
		
		
		int valueGap = overallMax - overallMin;
		int graphicGap = bottom - top;
		
		rate = valueGap / graphicGap;
		rateStacked = rate * 8;
		
		d = m.getList();
		dBraid =  m.getList();
		dStacked = m.getList();
		
		
		
		horizontalLineGap = (right - left) / (d.get(0).getValues().length - 1);
		
		//braidedGraph
		bottomBraid = (int) (screensize.getHeight() - (screensize.getHeight()/1.5));
		topBraid = (int) screensize.getHeight()/40;
		
		leftBraid = left + (right - left) + 30; 
		rightBraid = right + (right - left) + 30; 
		
		leftStacked = left + 2*(right - left) + 30; 
		rightStacked = right + 2*(right - left) + 30; 
		
		
	}
    
	private void doDrawing(Graphics g) throws URISyntaxException {
		
		Graphics2D g2d = (Graphics2D) g;
		
		
		//stacked graph
        
        g2d.setColor(Color.white);
        g2d.fillRect(leftStacked, top - 10, rightStacked-leftStacked , bottom-top + 10);
        
        g2d.setColor(Color.black);
        g2d.drawLine(leftStacked,top - 10,leftStacked,bottom);
        g2d.drawLine(leftStacked,bottomStacked,rightStacked,bottomStacked);
        
        bottomLine = new  Line2D.Float();
        bottomLine.setLine(leftStacked,bottomStacked,rightStacked,bottomStacked);
        
        for(int i=0;i<m.getHeaders().size();i++)
    	{
    		g2d.drawString(m.getHeaders().get(i)
        			, leftStacked + i * horizontalLineGap - 10, bottom + 15);
    	}
        Collections.sort(dStacked, new Comparator<Data>() {
	        @Override public int compare(Data d1, Data d2) {
	        	return (int) (d2.maxValues - d1.maxValues);
	        }

	    });
		if(isFirst){
			for(int i=1; i<dStacked.size()/2;i++)
			{
				dStacked.remove(i);
			}
			isFirst = false;
		}
        g2d.setColor(Color.PINK);
        //graph 1
        
        Path2D path;
        pathsStacked.clear();
        
        if(clicked != null){
	        for(int i=0;i<areasStacked.size();i++)
	        {
	        	if(areasStacked.get(i).intersects(clicked.getX(), clicked.getY(), 1, 1))
	        	{
	        		dSelected = i;
	        		
	        		g2d.setColor(Color.black);
        			g2d.drawRoundRect(m.magnifierCoordinateX + 10, m.magnifierCoordinateY + 10, 100, 100, 5, 5);
        			

        			g2d.setColor(Color.white);
        			g.fillRoundRect(m.magnifierCoordinateX + 10 + 1, m.magnifierCoordinateY + 10 + 1, 99, 99, 5, 5);
        			
        			g2d.setColor(Color.red);
        			g2d.drawString(dStacked.get(i).getLabel(), m.magnifierCoordinateX + 15, m.magnifierCoordinateY + 25);
            		
	        	}
	        }
	    }
        
        
        
        
        for(int i=0;i<dStacked.size()/2;i++)
        {
        	path = new Path2D.Double();
        	Path2D pathReverse = new Path2D.Double();
        	int currentColor = i % colors.size();
			Color color;
			if(dSelected == i)
        		color = Color.red;
        	else
        		color = colors.get(currentColor);
			
			g2d.setColor(color);
			
			for(int j=0;j<dStacked.get(i).getValues().length - 1;j++)
            {
	        	Line2D l = new Line2D.Float();
	        	
	        	
	        		int totalLeft=bottomStacked,totalRight=bottomStacked;
	        		for(int a=0;a<i;a++)
	        		{
	        			totalLeft -= dStacked.get(a).getValue(j)/rateStacked;
	        			totalRight -= dStacked.get(a).getValue(j+1)/rateStacked;
	        		}
	        		l.setLine((leftStacked + j * horizontalLineGap), totalLeft, (leftStacked + (j+1) * horizontalLineGap), totalRight);
	        	
	        	if(j==0)
	        		path.moveTo(l.getX1(), l.getY1());
	        	else
	        		path.lineTo(l.getX1(), l.getY1());
	        	
	        	path.lineTo(l.getX2(), l.getY2());
        	}
			
        	for(int j=dStacked.get(i).getValues().length-1;j>0;j--)
            {
	        	Line2D l = new Line2D.Float();
	        	
	        	
	        		int totalLeft=bottomStacked,totalRight=bottomStacked;
	        		for(int a=0;a<i;a++)
	        		{
	        			totalLeft -= dStacked.get(a).getValue(j-1)/rateStacked;
	        			totalRight -= dStacked.get(a).getValue(j)/rateStacked;
	        		}
	        		l.setLine((leftStacked + (j) * horizontalLineGap), totalRight,(leftStacked + (j-1) * horizontalLineGap), totalLeft);
		        	
	        	if(j==dStacked.get(i).getValues().length - 1)
	        		pathReverse.moveTo(l.getX1(), l.getY1());
	        	else
	        		pathReverse.lineTo(l.getX1(), l.getY1());
	        	
	        	pathReverse.lineTo(l.getX2(), l.getY2());
        	}
        	pathsStacked.add((Path2D) pathReverse.clone());
        	if(i==0)
        	{
        		PathIterator pi = bottomLine.getPathIterator(null);
        		path.append(pi, false);
        		//PathIterator pi = (PathIterator) pathsStacked.get(index).iterator();
        	}
        	else{
        		PathIterator pi = (PathIterator) pathsStacked.get(i-1).getPathIterator(null);
        		path.append(pi, true);
        	}
        	
        	if(areasStacked.size() < dStacked.size())
        		areasStacked.add(path);
        	
        	g2d.fill(path);
        	
        }
        
        for(int i=dStacked.size()/2;i<dStacked.size();i++)
        {
        	path = new Path2D.Double();
        	Path2D pathReverse = new Path2D.Double();
        	int currentColor = i % colors.size();
			Color color;
			if(dSelected == i)
        		color = Color.red;
        	else
        		color = colors.get(currentColor);
			
			g2d.setColor(color);
			
			dBraid.get(i).setColor(color);
			
			for(int j=0;j<dStacked.get(i).getValues().length - 1;j++)
            {
	        	Line2D l = new Line2D.Float();
	        	
	        	
	        		int totalLeft=bottomStacked,totalRight=bottomStacked;
	        		for(int a=dStacked.size()/2;a<i;a++)
	        		{
	        			totalLeft += dStacked.get(a).getValue(j)/rateStacked;
	        			totalRight += dStacked.get(a).getValue(j+1)/rateStacked;
	        		}
	        		l.setLine((leftStacked + j * horizontalLineGap), totalLeft, (leftStacked + (j+1) * horizontalLineGap), totalRight);
	        	
	        	if(j==0)
	        		path.moveTo(l.getX1(), l.getY1());
	        	else
	        		path.lineTo(l.getX1(), l.getY1());
	        	
	        	path.lineTo(l.getX2(), l.getY2());
        	}
			
        	for(int j=dStacked.get(i).getValues().length-1;j>0;j--)
            {
	        	Line2D l = new Line2D.Float();
	        	
	        	
	        		int totalLeft=bottomStacked,totalRight=bottomStacked;
	        		for(int a=dStacked.size()/2;a<i;a++)
	        		{
	        			totalLeft += dStacked.get(a).getValue(j-1)/rateStacked;
	        			totalRight += dStacked.get(a).getValue(j)/rateStacked;
	        		}
	        		l.setLine((leftStacked + (j) * horizontalLineGap), totalRight,(leftStacked + (j-1) * horizontalLineGap), totalLeft);
		        	
	        	if(j==dStacked.get(i).getValues().length - 1)
	        		pathReverse.moveTo(l.getX1(), l.getY1());
	        	else
	        		pathReverse.lineTo(l.getX1(), l.getY1());
	        	
	        	pathReverse.lineTo(l.getX2(), l.getY2());
        	}
        	pathsStacked.add((Path2D) pathReverse.clone());
        	if(i==dStacked.size()/2)
        	{
        		PathIterator pi = bottomLine.getPathIterator(null);
        		path.append(pi, false);
        		//PathIterator pi = (PathIterator) pathsStacked.get(index).iterator();
        	}
        	else{
        		PathIterator pi = (PathIterator) pathsStacked.get(i-1).getPathIterator(null);
        		path.append(pi, true);
        	}
        	
        	g2d.fill(path);
        	
        	if(areasStacked.size() < dStacked.size())
        		areasStacked.add(path);
        	
        }
        
        for(int i=0;i<areasStacked.size();i++)
        {
        	if(areasStacked.get(i).intersects(m.magnifierCoordinateX, m.magnifierCoordinateY, 1, 1))
        	{
        		g2d.setColor(Color.black);
    			g2d.drawRoundRect(m.magnifierCoordinateX + 10, m.magnifierCoordinateY + 10, 100, 100, 5, 5);
    			

    			g2d.setColor(Color.white);
    			g.fillRoundRect(m.magnifierCoordinateX + 10 + 1, m.magnifierCoordinateY + 10 + 1, 99, 99, 5, 5);
    			
    			g2d.setColor(Color.red);
    			g2d.drawString(dStacked.get(i).getLabel(), m.magnifierCoordinateX + 15, m.magnifierCoordinateY + 25);
        		
        	}
        }
        
//      
		
		//braidedGraph
        g2d.setColor(Color.white);
        g2d.fillRect(leftBraid, topBraid - 10, rightBraid-leftBraid , bottomBraid-topBraid + 10);
        
        g2d.setColor(Color.black);
        g2d.drawLine(leftBraid,topBraid - 10,leftBraid,bottomBraid);
        g2d.drawLine(leftBraid,bottomBraid,rightBraid,bottomBraid);
        
        
        
        for(int i=0;i<m.getHeaders().size();i++)
    	{
    		g2d.drawString(m.getHeaders().get(i)
        			, leftBraid + i * horizontalLineGap - 10, bottomBraid + 15);
    	}
        
        bottomBraidLine = new  Line2D.Float();
        for(int j=0;j<dBraid.get(0).getValues().length-1;j++)
        {
        	
            bottomBraidLine.setLine(leftBraid + j*horizontalLineGap,bottomBraid,leftBraid + (j+1)*horizontalLineGap,bottomBraid);
        	
	        for(int i=0;i<dBraid.size();i++)
	        {
        	
	        	int currentColor = i % colors.size();
	        	Color color;
	        	if(dSelected == i)
	        		color = Color.red;
	        	else
	        		color = colors.get(currentColor);
				
				
	        	
				dBraid.get(i).currentGap = j;
	            	Collections.sort(dBraid, new Comparator<Data>() {
	        	        @Override public int compare(Data d1, Data d2) {
	        	        	double[] data2=d2.getValues();
	        	        	double[] data1=d1.getValues();
	        	        	return (int) (data2[d2.currentGap] - data1[d1.currentGap]);
	        	        }
	
	        	    });
	            
	            
	            
	            
	            g2d.setColor(dBraid.get(i).getColor());
	        	Line2D l = new Line2D.Float();
	        	l.setLine((leftBraid + j * horizontalLineGap), bottomBraid - dBraid.get(i).getValue(j)/rate, (leftBraid + (j+1) * horizontalLineGap), bottomBraid - dBraid.get(i).getValue(j+1)/rate);
	        	g2d.draw(l);
	        	//Path2D creates a path, we create path to make shapes on the graphic, the shape is basically
	        	//lines for each value in the Data.java end at the end we connect this line with bottom line of
	        	//the chart.After that we fill into it with the color on the line.
	        	Path2D path2D = new Path2D.Double();
	        	path2D.moveTo(l.getX1(), l.getY1());
	        	path2D.lineTo(l.getX2(), l.getY2());
	        	path2D.lineTo(bottomBraidLine.getX2(), bottomBraidLine.getY2());
	        	path2D.lineTo(bottomBraidLine.getX1(), bottomBraidLine.getY1());
	        	g2d.fill(path2D);
	            if(m.magnifierCoordinateX>leftBraid && m.magnifierCoordinateX <rightBraid 
	            		&& m.magnifierCoordinateY>topBraid && m.magnifierCoordinateY<bottomBraid)
	            {
	            	//This part works according to current position of cursor. We need to understand, which data 
	            	//we stand on chart.First we calculate which range we are in, because values change from range to range.
	            					double value1left = bottom - dBraid.get(i).getValue(j)/rate;
					        		double value1right = bottom - dBraid.get(i).getValue(j+1)/rate;
					        		
					        		double value2left = 0D;
					        		double value2right = 0D;
					        		if(i != d.size())
					        		{
					        			 value2left = bottom;
						        		 value2right = bottom;
					        		}
					        		else{
					        		 value2left = bottom - dBraid.get(i+1).getValue(j)/rate;
					        		 value2right = bottom - dBraid.get(i+1).getValue(j+1)/rate;
					        		}
					        		
					        		
					        		double exactValueBraidDown =  (value1left<value1right ? value1right - exactValueBraid(value1left,value1right,j,m.magnifierCoordinateX)
					        														  :value1left - exactValueBraid(value1left,value1right,j,m.magnifierCoordinateX));
					        		
					        		double exactValueBraidUp =   (value2left<value2right ? value2right - exactValueBraid(value2left,value2right,j,m.magnifierCoordinateX)
																						 :value2left - exactValueBraid(value2left,value2right,j,m.magnifierCoordinateX));
					        		
					        		//if cursor position between up and down values this means we found our data :)
					        		if(m.magnifierCoordinateY<=exactValueBraidUp && m.magnifierCoordinateY>=exactValueBraidDown)
					        		{
					        			g2d.setColor(Color.black);
					        			g2d.drawRoundRect(m.magnifierCoordinateX + 10, m.magnifierCoordinateY + 10, 100, 100, 5, 5);
					        			
					
					        			g2d.setColor(Color.white);
					        			g.fillRoundRect(m.magnifierCoordinateX + 10 + 1, m.magnifierCoordinateY + 10 + 1, 99, 99, 5, 5);
					        			
					        			g2d.setColor(Color.red);
					        			g2d.drawString(dBraid.get(i).getLabel(), m.magnifierCoordinateX + 15, m.magnifierCoordinateY + 25);
					            		
					        			dPointerOn = i;
					        		}
					        		
					        
	            }

        	}
        }
        
        g2d.setColor(Color.red);
        for(int i=1;i<=10;i++)
		{//this part just print red lines on the chart
            g2d.drawLine(leftBraid,bottomBraid- verticalLineGap * i,rightBraid,bottomBraid- verticalLineGap * i);
       	}
		
        //graph
        
        g2d.setColor(Color.white);
        g2d.fillRect(left, top - 10, right-left , bottom-top + 10);
        
        g2d.setColor(Color.black);
        g2d.drawLine(left,top - 10,left,bottom);
        g2d.drawLine(left,bottom,right,bottom);
        
        bottomLine = new  Line2D.Float();
        bottomLine.setLine(left,bottom,right,bottom);
        
        for(int i=0;i<m.getHeaders().size();i++)
    	{
    		g2d.drawString(m.getHeaders().get(i)
        			, left + i * horizontalLineGap - 10, bottom + 15);
    	}
        
		//we need to sort the objects of "Data.java" in the arraylist, because java print them in order
    	//bigger data must be printed first to prevent the lower one stay behind the taller.
		Collections.sort(d, new Comparator<Data>() {
	        @Override public int compare(Data d1, Data d2) {
	        	return (int) (d2.maxValues - d1.maxValues);
	        }

	    });
		if(isFirst){
			for(int i=1; i<d.size()/2;i++)
			{
				d.remove(i);
			}
			isFirst = false;
		}
		
		if(clicked != null){
	        for(int i=0;i<areas.size();i++)
	        {
	        	if(areas.get(i).intersects(clicked.getX(), clicked.getY(), 1, 1))
	        	{
	        		dSelected = i;
	        	}
	        }
	    }
        
        g2d.setColor(Color.PINK);
        //graph 1
        for(int i=0;i<d.size();i++)
        {

            Path2D path2D = null;
        	int currentColor = i % colors.size();
			Color color;
			if(dSelected == i)
        		color = Color.red;
        	else
        		color = colors.get(currentColor);
			
			g2d.setColor(color);
			
			dBraid.get(i).setColor(color);
			path2D = new Path2D.Double();
        	for(int j=0;j<d.get(i).getValues().length - 1;j++)
            {
        		Line2D l = new Line2D.Float();
	        	l.setLine((left + j * horizontalLineGap), bottom - d.get(i).getValue(j)/rate, (left + (j+1) * horizontalLineGap), bottom - d.get(i).getValue(j+1)/rate);
	        	g2d.draw(l);
	        	//Path2D creates a path, we create path to make shapes on the graphic, the shape is basically
	        	//lines for each value in the Data.java end at the end we connect this line with bottom line of
	        	//the chart.After that we fill into it with the color on the line.
	        	if(j==0)
	        		path2D.moveTo(l.getX1(), l.getY1());
	        	else
	        		path2D.lineTo(l.getX1(), l.getY1());
	        	path2D.lineTo(l.getX2(), l.getY2());
        	}
        	
        	path2D.lineTo(bottomLine.getX2(), bottomLine.getY2());
        	path2D.lineTo(bottomLine.getX1(), bottomLine.getY1());
        	path2D.lineTo((left), bottom - d.get(i).getValue(0)/rate);
        	path2D.closePath();
        	g2d.fill(path2D);
        	if(areas.size()<d.size())
            {
          	  areas.add(path2D);
            }
        }
        g2d.setColor(Color.red);
        for(int i=1;i<=10;i++)
		{//this part just print red lines on the chart
            g2d.drawLine(left,bottom- verticalLineGap * i,right,bottom- verticalLineGap * i);
       	}
        
        g2d.setColor(Color.black);
        
        for(int i=0;i<areas.size();i++)
        {
        	if(areas.get(i).intersects(m.magnifierCoordinateX, m.magnifierCoordinateY, 1, 1))
        	{
        		g2d.setColor(Color.black);
    			g2d.drawRoundRect(m.magnifierCoordinateX + 10, m.magnifierCoordinateY + 10, 100, 100, 5, 5);
    			

    			g2d.setColor(Color.white);
    			g.fillRoundRect(m.magnifierCoordinateX + 10 + 1, m.magnifierCoordinateY + 10 + 1, 99, 99, 5, 5);
    			
    			g2d.setColor(Color.red);
    			g2d.drawString(d.get(i).getLabel(), m.magnifierCoordinateX + 15, m.magnifierCoordinateY + 25);
        		
        	}
        }
      
     }
	
    @Override
    public void paintComponent(Graphics g) {
    	
		
        super.paintComponent(g);
        try {
			doDrawing(g);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public double exactValue(double y1,double y2,double area,double x)
    {
    	double	exactValue = y1>y2?y1-y2:y2-y1;	
    	x = x - (left + area * horizontalLineGap);
    	double percent = x / horizontalLineGap * 100;
    	
    	exactValue = y1>y2?exactValue / 100 * percent:exactValue - (exactValue / 100 * percent);
    	return exactValue;
    }
    public double exactValueBraid(double y1,double y2,double area,double x)
    {
    	double	exactValue = y1>y2?y1-y2:y2-y1;	
    	x = x - (leftBraid + area * horizontalLineGap);
    	double percent = x / horizontalLineGap * 100;
    	
    	exactValue = y1>y2?exactValue / 100 * percent:exactValue - (exactValue / 100 * percent);
    	return exactValue;
    }
    
    public double percent(double center,double point,double percentage)
    {
    	point = (point-center)*percentage/150;
    	return point;
    }
    
   

}

// parallel coordinates


public class View extends JPanel {
	private Model model = null;

	@Override
	public void paint(Graphics g) {
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
	
}

