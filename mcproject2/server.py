import cv2
from keras.models import load_model
import flask
from flask import Flask, json, request, jsonify
from werkzeug.utils import secure_filename
import time
import os
from PIL import ImageGrab, Image
import numpy as np

app = flask.Flask(__name__)
def predict_digit(img):
    model = load_model('mnist.h5')
    #resize image to 28x28 pixels
    img.resize((28,28),refcheck=False)
    img = Image.fromarray(img)
    #convert rgb to grayscale
    img = img.convert('L')
    img = np.array(img)
    #reshaping to support our model input and normalizing
    img = img.reshape(1,28,28,1)
    img = img/255.0
    #predicting the class
    res = model.predict([img])[0]
    return np.argmax(res), max(res)

@app.route('/uploader', methods = ['POST'])
def handle_request():
    image_files = request.files.getlist('files')[0]
    image_files2 = image_files
    #print(image_files.size())
    imgobj = image_files.read()
    npimg = np.fromstring(imgobj,np.uint8)
    img = cv2.imdecode(npimg,1)
    predictedimage,accuracy = predict_digit(img)

    current_path = os.getcwd()
    print(predictedimage,accuracy)
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
