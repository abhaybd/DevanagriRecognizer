# Image Classification

# Uses keras 2
# Import libraries
from keras.models import Sequential
from keras.layers import Conv2D
from keras.layers import MaxPooling2D
from keras.layers import Flatten
from keras.layers import Dense
from keras.layers import Dropout

classifier = Sequential()

# Add convolution layer
classifier.add(Conv2D(filters=25, kernel_size=(3,3), input_shape=(32, 32, 1), activation='relu'))

# Pooling
classifier.add(MaxPooling2D(pool_size=(2,2)))

# Flatten
classifier.add(Flatten())

# Add full connection
classifier.add(Dense(units=128, activation='relu'))
classifier.add(Dropout(rate=0.1))
classifier.add(Dense(units=36, activation='softmax'))

# Compiling the ANN
classifier.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy'])

from keras.preprocessing.image import ImageDataGenerator

train_datagen = ImageDataGenerator(
        rescale=1./255)

test_datagen = ImageDataGenerator(rescale=1./255)

train_set = train_datagen.flow_from_directory(
        'resources/Train',
        color_mode='grayscale',
        target_size=(32, 32),
        batch_size=32,
        class_mode='categorical')

test_set = test_datagen.flow_from_directory(
        'resources/Test',
        color_mode='grayscale',
        target_size=(32, 32),
        batch_size=32,
        class_mode='categorical')

classifier.fit_generator(
        train_set,
        steps_per_epoch=61200,
        epochs=25,
        validation_data=test_set,
        validation_steps=10800)

classifier.save('model.h5')

def validate(classifier, test_set, steps):
    correct = 0
    for i in range(steps):
        a = test_set.next()
        prediction = a[0]
        yhat = classifier.predict(prediction)
        yhat = yhat[0].tolist()
        yhat = yhat.index(max(yhat))
        y = a[1].tolist()
        y = y.index(max(y))
        correct+=1
    return float(correct)/float(steps)
    
    

        

