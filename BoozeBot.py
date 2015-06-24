# Code running on computer (or Raspberry Pi) to control Arduino

# import serial
import cPickle as pickle

# Open serial connection to Arduino
# ser = serial.Serial('/dev/ttyACM0', 9600)   # Change device name later ***

# Data file locations
bottlesDataFile    = "data/bottleData.pkl"

# Config variables/constants
verbose					   = True			# Toggles output of script
maxBottles				   = 14				# Maximum number of bottles that can be held by BoozeBot
flOzToMilliliterConversion = 29.5735		# Conversion ration from fluid ounces to milliliters
responseMessage            = "DONE"         # Response message from Arduino telling drink is done

# Bottle class
bottleTypes 	= {}	# Mapping of bottle liquor types to Bottle			
bottleLocations = {}	# Mapping of bottle location numbers to Bottle
class Bottle:
    def __init__(self, type_in, name_in, sizeInMilliliters_in, location_in, price_in, proof_in):
    	self.type			 = type_in
        self.name            = name_in
        self.amountRemaining = sizeInMilliliters_in / flOzToMilliliterConversion
        self.location 		 = location_in
        self.price			 = price_in
        self.proof   		 = proof_in

        bottleLocations[location_in] = self    

# Functions *************************************************************************************
# Halts further execution while drink is made
def waitForResponse():
	if verbose: print "Waiting for robot to finish making drink..."

	responseReceived = False
	while (not responseReceived):
		time.sleep(0.10)
		if (ser.readline() == responseMessage):
			responseReceived = True

	if verbose: print "Drink made successfully"

# Saves bottle and user info (for data persistance)
def saveData():
	if verbose: print "Saving data to file..."

	# Save bottle data to file
	with open(bottlesDataFile, "wb") as output:
		for key in bottleLocations:
			pickle.dump(bottleLocations[key], output, pickle.HIGHEST_PROTOCOL)

	if verbose: print "Data saved successfully"

# Restores bottle and user info to data structures (for data persistance)
def restoreData():
	if verbose: print "Restoring data from file..."

	# Restore bottle data from file
	with open(bottlesDataFile, "rb") as input:
		while True:
			try:
				bottleIn = pickle.load(input)
				bottleTypes[bottleIn.type] = bottleIn
				bottleLocations[bottleIn.location] = bottleIn
			except (EOFError):
				break

	if verbose: print "Data restored successfully"

# Creates dummy data for bottles
def storeInitialBottles():
	if verbose: print "Creating dummy bottle data..."

	bottleTypes["vodka"]	    = Bottle("vodka", "Smirnoff", 1750, 1, 26.99, 80)
	bottleTypes["gin"]   	    = Bottle("gin", "New Amsterdam", 750, 2, 11.99, 80)
	bottleTypes["tequila"]      = Bottle("tequila", "Jose Cuervo Silver", 750, 3, 19.99, 80)
	bottleTypes["white rum"]    = Bottle("white rum", "Bacardi Limon", 750, 4, 12.99, 70)
	bottleTypes["dark rum"]     = Bottle("dark rum", "Captain Morgan", 1750, 5, 27.99, 70)
	bottleTypes["triple sec"]   = Bottle("triple sec", "Dekuypers", 750, 6, 5.99, 40)
	bottleTypes["whiskey"]      = Bottle("whiskey", "Jack Daniels", 750, 7, 23.99, 80)
	bottleTypes["blue curacao"] = Bottle("blue curacao", "Blue Juice", 750, 8, 7.99, 40)
	bottleTypes["well vodka"]   = Bottle("well vodka", "Crystal Palace", 1750, 9, 11.79, 80)
	bottleTypes["sour"]			= Bottle("sour", "Sweet/Sour Mix", 1500, 10, 7.49, 0)
	bottleTypes["cola"]			= Bottle("cola", "Coca Cola", 2000, 11, 1.19, 0)
	bottleTypes["lemon lime"]   = Bottle("lemon lime", "Sprite", 2000, 12, 1.19, 0)
	bottleTypes["orange soda"]	= Bottle("orange soda", "Crush", 2000, 13, 0.99, 0)
	bottleTypes["cranberry"]	= Bottle("cranberry", "Ocean Spray", 1500, 14, 3.49, 0)

	saveData()

# Run if adding bottle to BoozeBot
def addBottle(type_in, name_in, sizeInMilliliters_in, location_in, price_in, proof_in): 
	# Check bounds on location_in
	if ((location_in > maxBottles) | (location_in < 0)): 
		print "Cannot add bottle, invalid location number" 

	if location_in in bottleLocations:							# Check if bottle location exists
		print "Cannot add bottle to spot " + str(location_in) + ", already a bottle in that spot"
	else:
		if type_in in bottleTypes:								# Check if type exists 
			print "Cannot add bottle of type " + '"' + type_in + '", type already exists'
		else:
			bottleTypes[type_in] = Bottle(type_in, name_in, sizeInMilliliters_in, location_in, price_in, proof_in)

			if verbose: print "Added " + name_in + " as type " + type_in + " to BoozeBot"

# Run if removing a bottle from BoozeBot and not replacing
def removeBottle(location_in):
	if location_in in bottleLocations:							# Check that bottle location exists
		toBeRemovedType = bottleLocations[location_in].type
		del bottleLocations[location_in]
		del bottleTypes[toBeRemovedType]

		if verbose: print "Removed bottle " + str(location_in) + " from BoozeBot"
		saveData()
	else:
		print "Cannot remove bottle " + str(location_in) + ", location does not exist"

# Run if changing out a bottle for a different type of bottle
def changeBottle(location_in, type_in, name_in, sizeInMilliliters_in, price_in, proof_in):
	removeBottle(location_in)
	addBottle(type_in, name_in, sizeInMilliliters_in, location_in, price_in, proof_in)

# Run if refilling a bottle in given location with given amount of liquor/mixer
def refillBottle(location_in, sizeInMilliliters_in):
	if location_in in bottleLocations: 							# Check that bottle location exists
		bottleLocations[location_in].amountRemaining = sizeInMilliliters_in / flOzToMilliliterConversion

		if verbose: print "Bottle " + location_in + " refilled"
		saveData()
	else:
		print "Cannot refill bottle " + str(location_in) + ", location does not exist"

# FOR TESTING
def pourShot(type_in):
	bottleTypes[type_in].amountRemaining -= 1.5
	saveData()

# FOR TESTING
def pourShotLoc(location_in):
	bottleLocations[location_in].amountRemaining -= 1.5
	saveData()

# Print bottle details (for debugging) with type input
def printBottleDetailsType(type_in):
	tempBottle = bottleTypes[type_in]
	print 'bottleTypes["' + type_in + '"] = bottleLocations[' + str(tempBottle.location) + ']' 
	print "  Type:              " + tempBottle.type
	print "  Name:              " + tempBottle.name
	print "  Amount Remaining:  " + str(tempBottle.amountRemaining) + " fl oz"
	print "  Location:          " + str(tempBottle.location)
	print "  Price:             " + "$" + str(tempBottle.price)
	print "  Proof:             " + str(tempBottle.proof) + " proof"
 
# Print bottle details (for debugging) with bottle location number input
def printBottleDetailsLoc(location_in):
	printBottleDetailsType(bottleLocations[location_in].type)

# /Functions ************************************************************************************

# Main code
if __name__ == '__main__':
	restoreData()				# Restore bottle and user info to data structures if script is restarted

	# storeInitialBottles()
	# pourShotLoc(14)
	# refillBottle(14, 1500)
	# removeBottle(10)
	# addBottle("orange juice", "Simply Orange", 1500, 14, 4.49, 0)
	# changeBottle(11, "hot cocoa", "Swiss Miss", 1000, 14.99, 0)

	# for key in bottleTypes:
	# 	printBottleDetailsType(key)

	for key in bottleLocations:
		printBottleDetailsLoc(key)


