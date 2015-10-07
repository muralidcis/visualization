package com.visualization;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

public class Data{
	private double [] values;
	private Color color = Color.BLACK;
	private String label = "";
	public double maxValues;
	public int currentGap;
	
	public Data(double[] values, String label) {
		super();
		this.values = values;
		this.label = label;
		
		List l1 = Arrays.asList(ArrayUtils.toObject(this.values));
		maxValues = Collections.max(l1);
	}

	public Data(double[] values, Color color, String label) {
		super();
		this.values = values;
		this.color = color;
		this.label = label;
		
		List l1 = Arrays.asList(ArrayUtils.toObject(this.values));
		maxValues = Collections.max(l1);
		
	}

	public Data (double[] values) {
		this.values = values;
		
		List l1 = Arrays.asList(ArrayUtils.toObject(this.values));
		maxValues = Collections.max(l1);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public double[] getValues() {
		return values;
	}

	public void setValues(double[] values) {
		this.values = values;
	}
	public int getDimension(){
		 return values.length;
	}
	public double getValue(int index){
		return values[index];
	}
	
	public void setValue(int index,double value){
		values[index] = value;
	}
	
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	
	public String toString(){
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(label);
		stringBuffer.append('[');
		for (double value : values) {
			stringBuffer.append(value);
			stringBuffer.append(',');
		}
		
		stringBuffer.append(']');
	return stringBuffer.toString();
	}

}
