# Code running on computer (or Raspberry Pi) to control Arduino

# import serial
import pickle

# Open serial connection to Arduino
# ser = serial.Serial('/dev/ttyACM0', 9600)   # Change device name later ***

# Data file locations
bottlesDataFile = "bottleData.pkl"

# Variables/constants
flOzToMilliliterConversion = 29.5735
responseMessage            = "DONE"         # Response message from Arduino telling drink is done

# Bottle class and mapping of bottle name to Bottle class
bottles = {}
class Bottle:
    def __init__(self, name_in, sizeInMilliliters_in, price_in, location_in):
        self.name            = name_in
        self.amountRemaining = sizeInMilliliters_in / flOzToMilliliterConversion
        self.price			 = price_in
        self.location 		 = location_in

# Functions *******************************
# def waitForResponse():
#     responseReceived = False
#     while (not responseReceived):
#         if (ser.readline() == responseMessage):
#             responseReceived = True

def dispense(liquorName, amountInFlOz):
	print "Dispensing"

def saveData():
	# Save bottle data to file
	dataFile = open(bottlesDataFile, "wb")
	pickle.dump(bottles, dataFile)
	dataFile.close()

def restoreData():
	# Restore bottle data from file
	dataFile = open(bottlesDataFile, "rb")
	bottles = pickle.load(dataFile)
	dataFile.close()

# /Functions ******************************


# bottles["vodka"] = Bottle("Smirnoff", 750, 13.99, 3)
# saveData()

restoreData()
print bottles