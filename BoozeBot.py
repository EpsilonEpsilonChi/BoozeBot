# Code running on computer (or Raspberry Pi) to control Arduino

# import serial
import cPickle as pickle

# Open serial connection to Arduino
# ser = serial.Serial('/dev/ttyACM0', 9600)   # Change device name later ***

# Data file locations
bottlesDataFile = "bottleData.pkl"

# Constants
numBottles				   = 3				# Number of bottles BoozeBot holds
flOzToMilliliterConversion = 29.5735		# Conversion ration from fluid ounces to milliliters
responseMessage            = "DONE"         # Response message from Arduino telling drink is done

# Bottle class and mapping of bottle name to Bottle class
bottles = {}
class Bottle:
    def __init__(self, key_in, name_in, sizeInMilliliters_in, price_in, location_in):
    	self.key			 = key_in
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
	with open(bottlesDataFile, "wb") as output:
		for key in bottles:
			pickle.dump(bottles[key], output, pickle.HIGHEST_PROTOCOL)

def restoreData():
	# Restore bottle data from file
	with open(bottlesDataFile, "rb") as input:
		for index in range(0, numBottles):
			bottleIn = pickle.load(input)
			bottles[bottleIn.key] = bottleIn
# /Functions ******************************


# bottles["vodka"]	  = Bottle("vodka", "Smirnoff", 750, 13.99, 3)
# bottles["gin"]   	  = Bottle("gin", "New Amsterdam", 750, 11.99, 5)
# bottles["triple sec"] = Bottle("triple sec", "Dekuypers", 750, 5.99, 2)
# saveData()

restoreData()
print "gin brand: " + bottles["gin"].name
