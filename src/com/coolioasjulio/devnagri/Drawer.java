package com.coolioasjulio.devnagri;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class Drawer extends JFrame{
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws IOException, InterruptedException{
		Drawer drawer = new Drawer(320, 320);
		DevnagriRecognizer dr = new DevnagriRecognizer("model.h5");
		while(true){
			System.out.println("Draw a character!");
			while(!drawer.finished()){
				Thread.sleep(100);
			}
			BufferedImage image = drawer.getImage();
			int label = maxIndex(dr.guess(image));
			char letter = (char) (2325 + label);
			System.out.println("Guess: " + letter + " - " + label);
			drawer = new Drawer(320, 320);
		}
	}
	
	public static int maxIndex(int[] arr){
		int maxIndex = 0;
		for(int i = 1; i < arr.length; i++){
			if(arr[i] > arr[maxIndex]){
				maxIndex = i;
			}
		}
		return maxIndex;
	}
	
	public static int grayScale(int rgb){
		Color c = new Color(rgb);
		int gray = Math.round((float) (c.getRed() + c.getGreen() + c.getBlue()) / 3f);
		return gray;
	}
	
	private BufferedImage image;
	private boolean stop = false;
	private int penSize = 44;
	private int penX, penY;
	public Drawer(int width, int height){
		super();
		init(width, height);
	}
	
	public Drawer(int width, int height, String s){
		super(s);
		init(width, height);
	}
	
	public boolean finished(){
		return stop;
	}
	
	public BufferedImage getImage(){
		return image;
	}
	
	public void stopDrawing(){
		stop = true;
	}
	
	private void init(int width, int height){
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		ImageIcon icon = new ImageIcon(image);
		this.setUndecorated(true);
		this.getContentPane().add(new JLabel(icon));
		this.addMouseMotionListener(new MouseMotionListener(){
			@Override
			public void mouseDragged(MouseEvent e) {
				if(stop) return;
				penX = e.getX();
				penY = e.getY();
				int radius = penSize/2;
				for(int y = penY-radius; y < penY+radius; y++){
					for(int x = penX-radius; x < penX+radius; x++){
						if(x >= image.getWidth() || y >= image.getHeight() || x < 0 || y < 0) continue;
						int diffX = x-penX;
						int diffY = y-penY;
						float distance = (float) Math.sqrt(diffX*diffX + diffY*diffY);
						float value = 1 - distance/radius;
						boolean rightButton = SwingUtilities.isRightMouseButton(e);
						if(rightButton) value = 0;
						if(value > 0 || rightButton){
							int existing = image.getRGB(x, y);
							int toDraw = new Color(value, value, value, 1).getRGB();
							int rgb = Math.max(existing, toDraw);
							if(rightButton) rgb = toDraw;
							image.setRGB(x, y, rgb);							
						}
					}
				}
				repaint();
			}
			
			@Override
			public void mouseMoved(MouseEvent e) {}
			
		});
		this.pack();
		this.setVisible(true);
		JFrame stop = new JFrame();
		JButton button = new JButton("Stop");
		stop.getContentPane().add(button);
		button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				stop.dispose();
				stopDrawing();
				dispose();
			}
		});
		stop.pack();
		stop.setVisible(true);
	}
}

