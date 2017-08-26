package com.coolioasjulio.devanagri;

import org.deeplearning4j.nn.modelimport.keras.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import java.io.IOException;

public class ModelImport {
    public static MultiLayerNetwork importModel(String path) throws IOException{
        try {
            return KerasModelImport.importKerasSequentialModelAndWeights(path);
        } catch (InvalidKerasConfigurationException e) {
            e.printStackTrace();
        } catch (UnsupportedKerasConfigurationException e) {
            e.printStackTrace();
        }
        throw new IOException("Failed to load model!");
    }
}
