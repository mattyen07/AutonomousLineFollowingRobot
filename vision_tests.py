#EXPERIMENTS WITH OPENCV
#import the necessary packages
#only works if OpenCV is installed on pi
from picamera.array import PiRGBArray
from picamera import PiCamera
import socket
import sys
import time
import cv2
import numpy as np
import pickle
import struct
import base64
import zmq

#this stuff did not pan out, we did not get around to it
port = 5000
#initialize socket stuff (if we had gone this route)
'''
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print("Socket created")
try:
    s.bind((socket.gethostname(),port))
except socket.error as msg:
    print(msg)
print("Socket bind complete.")
'''
# initialize the camera and grab a reference to the raw camera capture
camera = PiCamera()
camera.resolution = (640, 480)
camera.framerate = 32
rawCapture = PiRGBArray(camera, size=(640, 480))
# allow the camera to warmup
time.sleep(0.1)

#for streaming
#camera_stream = cv2.VideoCapture(0)

for frame in camera.capture_continuous(rawCapture, format="bgr", use_video_port=True):
    image = frame.array
    image_red = cv2.cvtColor(image, cv2.COLOR_BGR2HSV) #change from bgr to hsv
    """
    try:
        grabbed, frame2 = camera_stream.read()
        #frame = cv2.resize(frame2, (640,480))
        encoded, buffer = cv2.imencode('.jpg',frame2)
        jpg_as_text = base64.base64encode(buffer)
        footage_socket.send(jpg_as_text)
    except KeyboardInterrupt:
        camera_stream.release()
        break
    """
    #lower threshold, upper color threshold
    brightmask = cv2.inRange(image, (245,245,245), (255,255,255)) #range for "bright" values
    darkmask = cv2.inRange(image, (0,0,0), (10,10,10)) #range for "dark" values
    redmask = cv2.inRange(image_red, (160,100,100),(179,255,255)) #range for "red" values
    brightBits = cv2.countNonZero(brightmask) #find amount of screen that is "bright"
    darkBits = cv2.countNonZero(darkmask) #find amount of screen that is "dark"
    redBits = cv2.countNonZero(redmask) #find amount of screen that is "red"

    if brightBits > 640 * 480 * 0.9:
        print("Too Bright") #90% of screen

    if darkBits > 640 * 480 * 0.9:
        print("Too Dark") #90% of screen

    if redBits > 640 * 480 * 0.2:
        print("Too Red") #20% of screen

    #show frames
    cv2.imshow("Frame", image)
    cv2.imshow("BrightMask", brightmask)
    cv2.imshow("DarkMask", darkmask)
    cv2.imshow("RedMask", redmask)

    key = cv2.waitKey(1) & 0xFF

    rawCapture.truncate(0)

    #press q to destroy
    if key == ord("q"):
        cv2.destroyAllWindows()
        break