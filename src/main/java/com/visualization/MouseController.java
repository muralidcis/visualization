package com.visualization;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

public class MouseController implements MouseListener, MouseMotionListener {

	private Model model = null;
	private View view = null;
 
	public void mouseClicked(MouseEvent arg0) {
		view.clicked = new Point2D.Double(arg0.getX(), arg0.getY());
		view.dSelected = view.dPointerOn;
		view.repaint();
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
		
	}

	public void mouseReleased(MouseEvent arg0) {
		
	}

	public void mouseDragged(MouseEvent arg0) {
		
	}

	public void mouseMoved(MouseEvent arg0) {
		model.magnifierCoordinateX = arg0.getX();
		model.magnifierCoordinateY = arg0.getY();
		
		view.repaint();
	}

	public void setModel(Model model) {
		this.model  = model;	
	}

	public void setView(View view) {
		this.view  = view;
	}

}
