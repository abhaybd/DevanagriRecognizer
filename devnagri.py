# Image Classification

# Uses keras 2
# Import libraries
from keras.models import Sequential
from keras.layers import Conv2D
from keras.layers import MaxPooling2D
from keras.layers import Dense
from keras.layers import GlobalAveragePooling2D
from keras.layers import Dropout

classifier = Sequential()

# Add convolution layer
classifier.add(Conv2D(filters=32, kernel_size=(3,3), input_shape=(28, 28, 1), activation='relu'))
classifier.add(Conv2D(filters=32, kernel_size=(3,3), activation='relu'))

# Pooling
classifier.add(MaxPooling2D(pool_size=(2,2)))

# More convolution
classifier.add(Conv2D(filters=64, kernel_size=(3,3), activation='relu'))
classifier.add(Conv2D(filters=64, kernel_size=(3,3), activation='relu'))

# Pooling
classifier.add(MaxPooling2D(pool_size=(2,2)))

classifier.add(GlobalAveragePooling2D())

classifier.add(Dropout(0.3))

classifier.add(Dense(units=36, activation='softmax'))

# Compiling the ANN
classifier.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy'])

from keras.preprocessing.image import ImageDataGenerator

train_datagen = ImageDataGenerator(
        rescale=1./255)

test_datagen = ImageDataGenerator(rescale=1./255)

train_set = train_datagen.flow_from_directory(
        'resources/New/Train',
        color_mode='grayscale',
        target_size=(28, 28),
        batch_size=32,
        class_mode='categorical')

test_set = test_datagen.flow_from_directory(
        'resources/New/Test',
        color_mode='grayscale',
        target_size=(28, 28),
        batch_size=32,
        class_mode='categorical')

# Create callbacks
from datetime import datetime
now = datetime.now()
dir_name = now.strftime('%Y-%m-%d %H-%M')
from keras.callbacks import EarlyStopping, TensorBoard, ModelCheckpoint
early_stopping = EarlyStopping(monitor='val_loss', patience=3)
tensorboard = TensorBoard(log_dir='logs/{}'.format(dir_name),
                          histogram_freq=0, write_graph=True, write_images=True)
model_checkpoint = ModelCheckpoint('model_train.h5', monitor='val_loss', save_best_only=True)
callbacks = [early_stopping, tensorboard, model_checkpoint]

# train model
classifier.fit_generator(
        train_set,
        steps_per_epoch=train_set.n//train_set.batch_size,
        epochs=50,
        validation_data=test_set,
        validation_steps=test_set.n//test_set.batch_size,
        callbacks=callbacks)

from keras.models import load_model
classifier = load_model('model_train.h5')

import numpy as np
from tqdm import tqdm
train_set.reset()
n_steps = train_set.n // train_set.batch_size
y_preds = []
y_true = []
for i in tqdm(range(n_steps)):
    x_batch, y_batch = test_set.next()
    preds = classifier.predict(x_batch)
    y_preds.extend([np.argmax(pred) for pred in preds])
    y_true.extend([np.argmax(y) for y in y_batch])

from sklearn.metrics import f1_score
f1 = f1_score(y_true, y_preds, average='macro')
print('F1: {:.3f}'.format(f1))