package com.coolioasjulio.devnagri;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class DevnagriRecognizer {
	
	public DevnagriRecognizer(String modelPath) throws IOException{
		//TODO import model at modelPath using ModelImporter.importModel()
	}
	
	public int[] guess(BufferedImage image){
		//TODO guess
		return null;
	}
	
	public static int grayScale(int rgb){
		Color c = new Color(rgb);
		return Math.round((float)(c.getRed() + c.getGreen() + c.getBlue())/3f);
	}
}
