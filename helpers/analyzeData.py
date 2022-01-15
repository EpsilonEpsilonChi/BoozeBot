import os
import json
import datetime
import xlsxwriter
from pprint import pprint

# Config variables
verbose 				= True
defaultSheetZoomLevel   = 150
transactionDataPath	    = "../data/transactions/"
outputFileName		    = "transactions.xlsx"

# Array of files that should be ignored when searching through directories
ignoreFiles = [".DS_Store", "transactions.xlsx", "~$transactions.xlsx"]

# Create new excel workbook
workbook = xlsxwriter.Workbook(transactionDataPath + outputFileName)

# Define formats
headerFormat    = workbook.add_format({'align': 'center', 'bold': True, 'underline': True})
subHeaderFormat = workbook.add_format({'align': 'center', 'underline': True})
timestampFormat = workbook.add_format({'bold': True, 'num_format': 'mm/dd/yyyy hh:mm:ss AM/PM'})
drinkFormat 	= workbook.add_format({'bold': True, 'align': 'left'})
numDrinksFormat = workbook.add_format({'bold': True, 'align': 'center'})
totalCostFormat = workbook.add_format({'bold': True, 'align': 'center'})
lineItemFormat  = workbook.add_format({'align': 'right', 'underline': True})
amountFormat	= workbook.add_format({'align': 'center'})
costFormat		= workbook.add_format({'align': 'center'})

# Specify number formats
numDrinksFormat.set_num_format('0.0')
totalCostFormat.set_num_format(0x07)
amountFormat.set_num_format('0.0')
costFormat.set_num_format(0x07)

# Add borders to formats
borderStyle = 1
timestampFormat.set_top(borderStyle)
drinkFormat.set_top(borderStyle)
numDrinksFormat.set_top(borderStyle)
totalCostFormat.set_top(borderStyle)
timestampFormat.set_bottom(borderStyle)
drinkFormat.set_bottom(borderStyle)
numDrinksFormat.set_bottom(borderStyle)
totalCostFormat.set_bottom(borderStyle)

# Returns true if inputted value is a file that should be ignored, false otherwise
def shouldFileBeIgnored(fileIn):
	if fileIn in ignoreFiles:
		return True
	else:
		return False

# Does initial formatting of user worksheet 
def formatUserSheet(workbookIn, worksheetIn):
	# Set zoom level
	worksheetIn.set_zoom(defaultSheetZoomLevel)

	# Set column widths
	worksheetIn.set_column("A:A", 20)
	worksheetIn.set_column("B:B", 25)
	worksheetIn.set_column("C:D", 15)
	
	# Write headers & labels
	worksheetIn.write("A1", "Timestamp", headerFormat)
	worksheetIn.write("B1", "Drink Recipe", headerFormat)
	worksheetIn.write("C1", "Num Std Drinks", headerFormat)
	worksheetIn.write("D1", "Total Cost", headerFormat)

# Find most recent transaction data dump
mostRecentPath = ""
mostRecentEditTime = 0
if verbose: print "Finding most recent transaction data dump..."
for folder in os.listdir(transactionDataPath):
	if not shouldFileBeIgnored(folder):
		path = os.path.join(transactionDataPath, folder)
		editTime = os.stat(path).st_mtime
		if editTime > mostRecentEditTime:
			mostRecentEditTime = editTime
			mostRecentPath = path

# Loop through all users in transaction data dump
for user in os.listdir(mostRecentPath):
	if not shouldFileBeIgnored(user):
		# Create and format worksheet for user
		if verbose: print "  Creating worksheet for " + user[0:(len(user) - 5)] + "..."
		curUserSheet = workbook.add_worksheet(user[0:(len(user) - 5)])
		formatUserSheet(workbook, curUserSheet)

		# Read JSON in from file
		with open(mostRecentPath + '/' + user) as userJSONFile:
			userData = json.load(userJSONFile)

		# Loop through each transaction and add to worksheet
		if verbose: print "  Writing data for " + user[0:(len(user) - 5)] + "..."
		counter = 2
		for key, value in userData.iteritems():
			timestamp = datetime.datetime.strptime(value["timestamp"], "%d-%m-%Y %H:%M:%S")

			# Write basic information
			curUserSheet.write_datetime("A" + str(counter), timestamp, timestampFormat)
			curUserSheet.write("B" + str(counter), value["recipeUsed"], drinkFormat)
			curUserSheet.write_number("C" + str(counter), value["numStandardDrinks"], numDrinksFormat)
			curUserSheet.write_number("D" + str(counter), value["totalCost"], totalCostFormat)

			counter += 1
			beginCollapsedRange = counter

			# Write ingredient information
			curUserSheet.write("B" + str(counter), "Ingredient Name", subHeaderFormat)
			curUserSheet.write("C" + str(counter), "Amount (fl oz)", subHeaderFormat)
			curUserSheet.write("D" + str(counter), "Item Price", subHeaderFormat)
			curUserSheet.set_row(counter - 1, None, None, {'level': 1, 'hidden': True})

			counter += 1

			ingredientNum = 1
			for x in range(0, value["ingredientCount"]):
				curIngredient = value["ingredient" + str(ingredientNum)]

				curUserSheet.write("A" + str(counter), "Ingredient " + str(ingredientNum) + ":", lineItemFormat)
				curUserSheet.write("B" + str(counter), curIngredient["liquorName"])
				curUserSheet.write("C" + str(counter), curIngredient["amountUsed"], amountFormat)
				curUserSheet.write("D" + str(counter), curIngredient["lineItemPrice"], costFormat)
				curUserSheet.set_row(counter - 1, None, None, {'level': 1, 'hidden': True})

				ingredientNum += 1
				counter += 1

			curUserSheet.set_row(counter - 2, None, None, {'level': 1, 'hidden': True, 'collapsed': True})

# Close workbook
workbook.close()
