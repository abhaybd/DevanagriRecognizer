package com.coolioasjulio.devanagri;

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
        DevanagriRecognizer dr = new DevanagriRecognizer("model_keras1_new.h5");
        Drawer drawer = new Drawer(280,280);
        while(true){
            System.out.println("Draw a character!");
            while(!drawer.finished()){
                Thread.sleep(100);
            }
            BufferedImage image = drawer.getImage();
            int output = dr.guess(image);
            System.out.println("Output: " + output);
            char letter = (char)output;
            System.out.println("Guess: " + letter + " - " + (output-DevanagriRecognizer.START));
            drawer = new Drawer(280,280);
        }
    }

    private BufferedImage image;
    private boolean stop = false;
    private int penSize = 35;
    private int penX, penY;
    public Drawer(int width, int height){
        this(width, height, "");
    }

    public Drawer(int width, int height, String title){
        super(title);
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
                        float distance = (float) Math.sqrt(diffX*diffX + diffY*diffY)/(float)radius;
                        float value = (float)Math.pow(distance,10)*-1f+1f;
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

            public void mouseMoved(MouseEvent e) {}

        });
        this.pack();
        this.setVisible(true);
        final JFrame stop = new JFrame();
        JButton button = new JButton("Stop");
        stop.getContentPane().add(button);
        button.addActionListener(new ActionListener(){
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

