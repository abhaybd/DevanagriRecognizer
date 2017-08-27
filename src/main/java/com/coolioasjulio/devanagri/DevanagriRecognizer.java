package com.coolioasjulio.devanagri;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.util.ArrayUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class DevanagriRecognizer {
    private MultiLayerNetwork network;
    public DevanagriRecognizer(String path) throws IOException {
        network = ModelImport.importModel(path);
    }

    public int[] guess(BufferedImage unscaled){
        BufferedImage image = toBufferedImage(unscaled.getScaledInstance(32,32,Image.SCALE_AREA_AVERAGING));
        double[][][][] imageArr = new double[1][1][image.getWidth()][image.getHeight()];
        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                imageArr[0][0][x][y] = grayScale(image.getRGB(x,y))/255d;
            }
        }
        double[] flat = ArrayUtil.flattenDoubleArray(imageArr);
        int[] shape = new int[]{1, 1, image.getWidth(), image.getHeight()};
        INDArray input = Nd4j.create(flat, shape, 'f');
        return network.predict(input);
    }

    public static BufferedImage toBufferedImage(Image image){
        if(image instanceof BufferedImage) return (BufferedImage)image;
        BufferedImage bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(image, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public static int grayScale(int rgb){
        Color c = new Color(rgb);
        int gray = Math.round((float) (c.getRed() + c.getGreen() + c.getBlue()) / 3f);
        return gray;
    }
}
