package com.coolioasjulio.devanagri;

import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

import java.io.IOException;

public class ModelImport {
    public static void main(String[] args) throws IOException {
        MultiLayerNetwork model = importModel("model_complex.h5");
        ModelSerializer.writeModel(model, "model.zip", false);
    }
    public static MultiLayerNetwork importModel(String path) throws IOException{
        try {
            return KerasModelImport.importKerasSequentialModelAndWeights(path);
        } catch (UnsupportedKerasConfigurationException | InvalidKerasConfigurationException e) {
            e.printStackTrace();
        }
        throw new IOException("Failed to load model!");
    }
}
