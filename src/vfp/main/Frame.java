package vfp.main;



import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Frame extends JFrame {
	private MouseController mc = null;
    private Model m = null;
    private View view = null;
    public Frame() {

        initUI();
    }

    private void initUI(){

        setTitle("Visualization Final Project - Yasar Naci Gündüz / Preetha Moorthi");
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        m = new Model();
        View view = new View();
        MouseController mc = new MouseController();
        view.addMouseListener(mc);
 	    view.addMouseMotionListener(mc);
 	    view.setModel(m);
 	    mc.setModel(view.getModel());
 	    view.setupScreen();
 	    mc.setView(view);
        add(view);
        
        

        setSize((int)screensize.getWidth(),(int) screensize.getHeight());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                Frame sk = new Frame();
                sk.setVisible(true);
            }
        });
    }

}
