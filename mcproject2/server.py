import cv2
from keras.models import load_model
import flask
from flask import Flask, json, request, jsonify
from werkzeug.utils import secure_filename
import time
import os
from PIL import ImageGrab, Image
import numpy as np
import matplotlib.pyplot as plt

app = flask.Flask(__name__)
def predict_digit(image):
  model = load_model("/try_2.h5")
  img_gray = cv2.cvtColor(image.copy(), cv2.COLOR_BGR2GRAY)
  kernel = np.ones((5, 5), np.uint8)
  img_eroded = cv2.erode(img_gray, kernel, iterations=10)

  ret, mask = cv2.threshold(img_eroded.copy(), 75, 255, cv2.THRESH_BINARY_INV)
  contours, hierarchy = cv2.findContours(mask.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
  #Getting the contour with maximum area
  c = max(contours, key=cv2.contourArea)
  x,y,w,h = cv2.boundingRect(c)
  #Boundary for visualization
  cv2.rectangle(image, (x,y), (x+w, y+h), color=(0, 255, 0), thickness=2)
  #Croppping the thresholded image and adding 50 pixels as padding
  digit = mask[max(y-50, 0):min(y+h+50, image.shape[0]), min(x-50, 0):max(x+w+50, image.shape[1])]
  #Resizing it to match the size of training images
  resized_digit = cv2.resize(digit, (18,18))
  padded_digit = np.pad(resized_digit, ((5,5),(5,5)), "constant", constant_values=0)

  image_to_test = np.array(padded_digit)
  prediction = model.predict(image_to_test.reshape(1, 28, 28, 1))
  print("Output predicted by the model:{}".format(np.argmax(prediction)))
  return np.argmax(prediction)

@app.route('/uploader', methods = ['POST'])
def handle_request():
    image_files = request.files.getlist('files')[0]
    image_files2 = image_files
    #print(image_files.size())
    imgobj = image_files.read()
    npimg = np.fromstring(imgobj,np.uint8)
    img = cv2.imdecode(npimg,1)
    predictedimage = predict_digit(img)

    current_path = os.getcwd()
    print(predictedimage)
    upload_path = os.path.join(current_path, str(predictedimage))
    if not os.path.exists(upload_path):
        os.makedirs(upload_path)

    image_filename = secure_filename(image_files.filename)
    timestr = time.strftime("%Y%m%d-%H%M%S")
    image_files.seek(0)
    image_files.save(os.path.join(upload_path, timestr+'_'+image_filename))

    resp = jsonify({'message':'Image uploaded successfully!'})
    resp.status_code = 201
    return resp
app.run(host="0.0.0.0",port=10001,debug=True)
