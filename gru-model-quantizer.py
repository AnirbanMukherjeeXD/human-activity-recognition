# -*- coding: utf-8 -*-

__author__ = "@amitrajitbose"

# !pip install tensorflow_model_optimization

import tensorflow as tf
import numpy as np
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Flatten, Dropout,SimpleRNN, GRU, LSTM
from tensorflow import feature_column
from tensorflow.keras.utils import to_categorical
from tensorflow.keras.models import load_model
from tensorflow_model_optimization.sparsity import keras as sparsity

def quantize_model(model, filename="quantized_model", fullpath="./",):
  """
  Takes a keras model and quantizes it.
  Writes a tflite model file in the fullpath directory with the filename passed.
  """
  converter = tf.lite.TFLiteConverter.from_keras_model(model)
  tflite_model = converter.convert()
  exportFile = fullpath + filename + ".tflite"
  open(exportFile,"wb").write(tflite_model)
  print("Quantization Successful...! Exported File at ", exportFile)

def load_gru(weights_file, debug=False):
  # Defining Model Architecture
  # Alternatively, Can Be Imported From JSON File
  model = Sequential()
  model.add(GRU(100,return_sequences=True,input_shape=(128,9)))
  model.add(GRU(64))
  model.add(Dropout(0.15))
  model.add(Dense(64, activation='tanh'))
  model.add(Dense(6, activation='softmax'))

  # Loading Model Weights
  model.load_weights(weights_file)
  if debug:
    model.summary()

  return model

quantize_model(load_gru('/models/gru.h5'), "gru_quantized")

def load_gru_pruned(weights_file, debug=False):
  epochs = 100
  batch_size = 32
  num_train_samples = 7352
  end_step = np.ceil(1.0 * num_train_samples / batch_size).astype(np.int32) * epochs
  # print('End step: ' + str(end_step))
  pruning_params = {
        'pruning_schedule': sparsity.PolynomialDecay(initial_sparsity=0.50,
                                                    final_sparsity=0.90,
                                                    begin_step=2000,
                                                    end_step=end_step,
                                                    frequency=100)
  }

  pruned_model = tf.keras.Sequential([
      sparsity.prune_low_magnitude(GRU(100,return_sequences=True,input_shape=(128,9)),**pruning_params),
      sparsity.prune_low_magnitude(GRU(64), **pruning_params),
      Dropout(0.15),
      sparsity.prune_low_magnitude(Dense(64, activation='tanh'),**pruning_params),
      sparsity.prune_low_magnitude(Dense(6, activation='softmax'),**pruning_params)
  ])

  # Loading Model Weights
  pruned_model.load_weights(weights_file)

  if debug:
    pruned_model.summary()
  return pruned_model

# quantize_model(load_gru_pruned('/models/gru_pruned.h5'), "gru_pruned_quantized") # TODO: Debug work pending

