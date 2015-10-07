package vfp.main;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.stream.FileImageInputStream;

public class Model {
	private ArrayList<Data> list  = new ArrayList<Data>();
	private ArrayList<Range> ranges = new ArrayList<Range>();
	private ArrayList<String> labels = new ArrayList<String>();
	private ArrayList<String> headers = new ArrayList<String>();
	private int dim = 0;
	public int magnifierCoordinateX=-1,magnifierCoordinateY=-1
			   ,magnifierFirstCoordinateX=-1,magnifierFirstCoordinateY=-1
			   ,magnifierLastCoordinateX=-1,magnifierLastCoordinateY=-1
			   ,zoneX,zoneY;
	
	public ArrayList<String> getLabels() {
		return labels;
	}
	public void setLabels(ArrayList<String> labels) {
		this.labels = labels;
	}
	public ArrayList<String> getHeaders() {
		return headers;
	}
	public void setHeaders(ArrayList<String> headers) {
		this.headers = headers;
	}
	public ArrayList<Data> getList() {
		return list;
	}
	public void setList(ArrayList<Data> list) {
		this.list = list;
	}
	public ArrayList<Range> getRanges() {
		return ranges;
	}
	public void setRanges(ArrayList<Range> ranges) {
		this.ranges = ranges;
	}
	public Model() {
		importValues();
	}
	public Iterator<Data> iterator() {
		return list.iterator();
	}
	public int getDim() {
		return dim;
	}
	public void setDim(int dim) {
		this.dim = dim;
	}
	
	
	public void importValues() {
		File file = new File("cameras.ssv");
		//File file = new File("libraries.ssv");
	    Debug.p(file.getAbsoluteFile().toString());
	   
	    try {
	    	 String thisLine = null;
	    	 BufferedReader br = new BufferedReader(new FileReader(file));
	         try {
	        	 //Import Labels
	        	 thisLine = br.readLine();
				 String l [] = thisLine.split(";");
				 
				 headers.clear();
				 for (int i = 1; i < l.length; i++) headers.add(l[i]);
				 for (int i = 1; i < l.length; i++) labels.add(l[i]); // import labels excluding name
				 setDim(l.length-1);
				 
				  // Prepare Ranges
				 double lowRanges [] = new double[l.length-1];
				 for (int i = 0; i < lowRanges.length; i++) lowRanges[i] = Double.MAX_VALUE;
				 double highRanges [] = new double[lowRanges.length];
			     for (int i = 0; i < highRanges.length; i++) highRanges[i] = Double.MIN_VALUE;
	        	 
	        	 // Import Data and adapt Ranges
				 while ((thisLine = br.readLine()) != null) { // while loop begins here
					 String values [] = thisLine.split(";");
					 double dValues [] = new double[values.length -1];
					 
					 for (int j =1; j < values.length; j++) {
						 
						 try{
						 dValues[j-1] = Double.parseDouble(values[j]);}
						 catch(NumberFormatException ex)
						 {
							 dValues[j-1] = 0.0;
						 }
						 if (dValues[j-1] <  lowRanges[j-1]) lowRanges[j-1] = dValues[j-1];
						 if (dValues[j-1] >  highRanges[j-1]) highRanges[j-1] = dValues[j-1];
					 }	
					 list.add(new Data(dValues, values[0]));
	   			}
				 
				for (int i = 0; i < highRanges.length; i++) {
					ranges.add(new Range(lowRanges[i],highRanges[i]));
				} 
				 
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // end while 

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
    
}
