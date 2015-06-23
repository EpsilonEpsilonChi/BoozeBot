# Code running on computer (or Raspberry Pi) to control Arduino

import serial

# Open serial connection to Arduino
ser = serial.Serial('/dev/ttyACM0', 9600)   # Change device name later ***

# Variables/constants
flOzToMilliliterConversion = 29.5735
responseMessage            = "DONE"         # Response message from Arduino telling drink is done

# User class
class Liquor:
    def __init__(self, name_in, sizeInMilliliters_in):
        self.name            = name_in
        self.amountRemaining = sizeInMilliliters_in / flOzToMilliliterConversion

# Functions *******************************
def waitForResponse():
    responseReceived = False
    while (!responseReceived):
        if (ser.readline() == responseMessage):
            responseReceived = True

# /Functions ******************************



