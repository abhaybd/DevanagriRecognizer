package com.coolioasjulio.devanagri;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.util.ArrayUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class DevanagriRecognizer {
    public static final char START = (char)2325;
    private static final int[] labels = new int[]{
            10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
            1,
            20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
            2,
            30, 31, 32, 33, 34, 35, 36,
            3, 4, 5, 6, 7, 8, 9
    };

    public static char getLabel(int output){
        char letter = (char)(START-1);
        return (char) (letter + labels[output]);
    }

    private MultiLayerNetwork network;
    public DevanagriRecognizer(String path) throws IOException {
        if (path.endsWith("h5")) {
            network = ModelImport.importModel(path);
            ModelSerializer.writeModel(network, "model.zip", false);
            System.err.println("hdf5 file format not supported! model.zip file created, use that next time.");
        } else{
            network = ModelSerializer.restoreMultiLayerNetwork(path);
        }
    }

    public char guess(BufferedImage unscaled){
        BufferedImage image = toBufferedImage(unscaled.getScaledInstance(28,28,Image.SCALE_AREA_AVERAGING));
        double[][][][] imageArr = new double[1][1][image.getWidth()][image.getHeight()];
        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                imageArr[0][0][x][y] = grayScale(image.getRGB(x,y))/255d;
            }
        }

        double[] flat = ArrayUtil.flattenDoubleArray(imageArr);
        int[] shape = new int[]{1, 1, image.getWidth(), image.getHeight()};
        INDArray input = Nd4j.create(flat, shape, 'f');
        return getLabel(network.predict(input)[0]);
    }

    public static BufferedImage toBufferedImage(Image image){
        if(image instanceof BufferedImage) return (BufferedImage)image;
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        // Draw the image on to the buffered image
        Graphics2D bGr = bufferedImage.createGraphics();
        bGr.drawImage(image, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bufferedImage;
    }

    public static int grayScale(int rgb){
        Color c = new Color(rgb);
        int gray = Math.round((float) (c.getRed() + c.getGreen() + c.getBlue()) / 3f);
        return gray;
    }
}
