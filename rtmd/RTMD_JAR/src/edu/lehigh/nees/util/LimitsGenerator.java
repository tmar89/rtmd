/**
 * 
 */
package edu.lehigh.nees.util;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
 
public class LimitsGenerator extends JPanel {    
	private static final long serialVersionUID = 3807198237888009520L;
	public Point actStart = new Point(50,50);
    public Point actEnd = new Point(actStart.x+100,actStart.y+50);
    public Point pistonStart = new Point(actEnd.x,actStart.y+10);
    public Point pistonEnd = new Point(actEnd.x+100/2,pistonStart.y+30);
    int pistonResize = 0;
    JTextField actPosition;
 
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);        
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);                
        // Draw Actuator Body Rectangle
        g2.setPaint(Color.blue);
        g2.fill(new Rectangle2D.Double(actStart.x,actStart.y,actEnd.x-actStart.x,actEnd.y-actStart.y));     
        // Draw Actuator Piston
        g2.setPaint(Color.gray);
        g2.fill(new Rectangle2D.Double(pistonStart.x,pistonStart.y,pistonEnd.x-pistonStart.x+pistonResize,pistonEnd.y-pistonStart.y));
        
        //g2.fill(new Ellipse2D.Double(origin.x-1.5, origin.y-1.5, 4, 4));        
        //g2.draw(new Line2D.Double(x1, y1, x2, y2));
    }
 
    private JPanel getUIPanel() {
    	// Create the panel to add the items too
    	JPanel panel = new JPanel();
    	
    	// Actuator Position
    	JLabel actPositionLabel = new JLabel("Actuator Position");
    	panel.add(actPositionLabel);
        actPosition = new JTextField("0000.00");        
        actPosition.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	double newPosition = Double.parseDouble(actPosition.getText().trim());
            	// Normalize +/- 500.00 to +/- 50
            	int newPositionInt = (int)newPosition/10;
            	pistonResize = newPositionInt;
            	repaint();
            }
        });        
        panel.add(actPosition);
        return panel;
    }
 
    public static void main(String[] args) {
        LimitsGenerator lg = new LimitsGenerator();
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(lg);
        f.add(lg.getUIPanel(), "Last");
        f.setSize(400,400);
        f.setLocation(200,200);
        f.setVisible(true);
    } 
}