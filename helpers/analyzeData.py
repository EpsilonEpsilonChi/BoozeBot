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
timestampFormat = workbook.add_format({'num_format': 'mm/dd/yyyy hh:mm:ss AM/PM'})
numDrinksFormat = workbook.add_format({'align': 'center'})
costFormat		= workbook.add_format({'align': 'center'})

# Specify number formats
numDrinksFormat.set_num_format('0.0')
costFormat.set_num_format(0x07)

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
			timestamp = datetime.datetime.strptime(value["timestamp"], "%m-%d-%Y %H:%M:%S")

			curUserSheet.write_datetime("A" + str(counter), timestamp, timestampFormat)
			curUserSheet.write("B" + str(counter), value["recipeUsed"])
			curUserSheet.write_number("C" + str(counter), value["numStandardDrinks"], numDrinksFormat)
			curUserSheet.write_number("D" + str(counter), value["totalCost"], costFormat)

			counter += 1

# Close workbook
workbook.close()
