var colors     = require("colors/safe");
var mkdirp 	   = require("mkdirp");
var jsonfile   = require("jsonfile");
	Firebase   = require("firebase");

var ref      = new Firebase('https://boozebot.firebaseio.com');
var usersRef = ref.child("Users");

// Generates a timestamp
function generateTimestamp() {
  var today = new Date();
  var dd = today.getDate();
  var mm = today.getMonth() + 1;
  var yyyy = today.getFullYear();
  var hh = today.getHours();
  var minmin = today.getMinutes();
  var ss = today.getSeconds();

  if (dd < 10) { dd = '0' + dd; }
  if (mm < 10) { mm = '0' + mm; }
  if (hh < 10) { hh = '0' + hh; }
  if (minmin < 10) { minmin = '0' + minmin; }
  if (ss < 10) { ss = '0' + ss; }
   
  return dd + ":" + mm + ":" + yyyy + " " + hh + "-" + minmin + "-" + ss;
}

// Create new timestamped transaction dump folder
var folderName = "data/transactions/" + generateTimestamp();
mkdirp(folderName, function(dirErr) {
	// Check for folder creation errors
	if (dirErr) {
		console.log(colors.red("Error creating folder " + folderName + ": " + dirErr));
		process.exit();
	}
	console.log(colors.green("Created new transaction dump: " + folderName));

	// Pull Firebase reference to users list
	usersRef.once("value", function(snapshot) {
		// Ensure users reference exists   
	    if (snapshot.val() === null) { 
	    	console.log(colors.red("Users reference not valid")); 
	    	process.exit();
	    } 

    	// Loop through all users
    	var counter = 1;
    	var numUsers = snapshot.numChildren();
    	snapshot.forEach(function(userSnapshot) {
			var filename = userSnapshot.key() + ".json";
			var filepath = "./" + folderName + '/' + filename;

			// Pull the user's transactions
			userSnapshot.ref().child("Transactions").once("value", function(transactionListSnapshot) {
				// Check if user has any transactions
			    if (transactionListSnapshot.val() !== null) { 
			    	jsonfile.writeFile(filepath, transactionListSnapshot.val(), {spaces: 2}, function(fileErr) {
		    			if (fileErr) {
		    				console.log(colors.red("Error writing " + filename + " data: " + fileErr));
		    				// Send notification of error somewhere?
		    			} else {
		    				console.log(colors.green(filename + " data written successfully"));
		    				if (counter === numUsers) {
		    					process.exit();
		    				}
		    				counter = counter + 1;
		    			}
		    		});
			    }
			});
		});
	});
});
