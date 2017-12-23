# Preprocess images

from scipy import misc
from scipy.ndimage.measurements import center_of_mass
import numpy as np
import os
from os.path import isfile, isdir

class ProgBar(object):
    def __init__(self):
        self.counter = 0
        
    def progress(self):
        self.counter += 1
        print('.', end = '')
        if self.counter >= 100:
            print()
            self.counter = 0

def recurse(progbar, folder):
    for path in os.listdir(folder):
        abs_path = '{}/{}'.format(folder, path)
        if isdir(abs_path):
            recurse(progbar, abs_path)
        elif isfile(abs_path):
            img = misc.imread(abs_path)
            img = process(img)
            os.remove(abs_path)
            misc.imsave(abs_path, img)
            progbar.progress()
            
def process(img):
    # Crop to bounding box
    left, right, down, up = bounds(img)
    img = img[up:down,left:right]
    # Resize to 20x20
    img = misc.imresize(img, (20,20))
    # Calculate center of mass of cropped image
    x, y = center_of_mass(img)
    x = int(round(x))
    y = int(round(y))
    # Clamp the center of mass so the image doesn't go off the image when it's added
    x = max(6, min(x, 14))
    y = max(6, min(y, 14))
    # Superimpose the cropped iage offset by the center of mass
    processed = np.zeros((28, 28), dtype=np.float32)
    processed[14-y:14-y+img.shape[0], 14-x:14-x+img.shape[1]] = img
    return processed

def bounds(img):
    rows = np.any(img, axis=1)
    cols = np.any(img, axis=0)
    left, right = np.where(cols)[0][[0,-1]]
    up, down    = np.where(rows)[0][[0,-1]]
    return left, right, down, up

if __name__ == '__main__':
    PARENT_DIR = 'resources/New'
    progbar = ProgBar()
    recurse(progbar, PARENT_DIR)