# Draw devanagri to be read by CNN

from tkinter import Tk, Canvas, YES, BOTH
from PIL import ImageGrab
import numpy as np
from keras.models import load_model
import preprocess
from sklearn.externals import joblib

classifier = load_model('model_keras1_new.h5')
class_indices = joblib.load('class_indices.sav')

def get_img(master, widget):
    x=master.winfo_rootx()+widget.winfo_x()
    y=master.winfo_rooty()+widget.winfo_y()
    x1=x+widget.winfo_width()
    y1=y+widget.winfo_height()
    return ImageGrab.grab((x,y,x1,y1))
    
def paint(event):
    x1, y1 = event.x-radius,event.y-radius
    x2, y2 = event.x+radius,event.y+radius
    w.create_oval(x1,y1,x2,y2, outline=paint_color, fill=paint_color)

canvas_width = 280
canvas_height = 280
background = '#000000'
paint_color = '#FFFFFF'
radius = 15

master = Tk()

w = Canvas(master, 
           width=canvas_width, 
           height=canvas_width, 
           background=background,
           highlightthickness=0)
w.pack(expand=YES, fill=BOTH)
w.bind('<B1-Motion>', paint)

def get_pred():
    global image
    image = get_img(master, w)
    image = np.asarray(image)[:,:,0]
    image = preprocess.process(image)
    image = np.expand_dims(image, axis=0)
    image = np.expand_dims(image, axis=3)
    pred = classifier.predict(image)[0]
    pred = class_indices[np.argmax(pred)]
    print_pred(pred)
    w.delete('all')
    master.after(10000, get_pred)
    
def print_pred(pred):
    kha = u'\u0915'
    print('Predicted character is: ', end = '')
    print(chr(ord(kha)+pred))
    
master.after(10000, get_pred)
master.mainloop()