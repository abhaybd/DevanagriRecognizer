# Image Classification

# Uses keras 1.2
# Import libraries
from keras.models import Sequential
from keras.layers import Convolution2D
from keras.layers import MaxPooling2D
from keras.layers import Flatten
from keras.layers import Dense
from keras.layers import Dropout

classifier = Sequential()

# Add convolution layer
classifier.add(Convolution2D(32, 3, 3, input_shape=(28, 28, 1), activation='relu'))
classifier.add(Convolution2D(32, 3, 3, activation='relu'))

# Pooling
classifier.add(MaxPooling2D(pool_size=(2,2)))

# More convolution
classifier.add(Convolution2D(32, 3, 3, activation='relu'))
classifier.add(Convolution2D(32, 3, 3, activation='relu'))

# Pooling
classifier.add(MaxPooling2D(pool_size=(2,2)))

# Flatten
classifier.add(Flatten())

# Add full connection
classifier.add(Dense(output_dim=128, activation='relu'))
classifier.add(Dropout(p=0.1))
classifier.add(Dense(output_dim=36, activation='softmax'))

# Compiling the ANN
classifier.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy'])

from keras.preprocessing.image import ImageDataGenerator

train_datagen = ImageDataGenerator(
        rescale=1./255)

test_datagen = ImageDataGenerator(rescale=1./255)

train_set = train_datagen.flow_from_directory(
        'resources/New/Train',
        color_mode='grayscale',
        target_size=(28,28),
        batch_size=32,
        class_mode='categorical')

test_set = test_datagen.flow_from_directory(
        'resources/New/Test',
        color_mode='grayscale',
        target_size=(28,28),
        batch_size=32,
        class_mode='categorical')

classifier.fit_generator(
        train_set,
        samples_per_epoch=61200,
        nb_epoch=25,
        validation_data=test_set,
        nb_val_samples=10800)

classifier.save('model_keras1_new.h5')

def validate(classifier, test_set, steps):
    import numpy as np
    correct = 0
    n_guesses = 0
    for i in range(steps):
        a = test_set.next()
        prediction = a[0]
        yhat = classifier.predict(prediction)
        y = a[1]
        for i in range(len(yhat)):
            n_guesses += 1
            if np.argmax(yhat[i]) == np.argmax(y[i]):
                correct += 1
    return float(correct)/float(n_guesses)