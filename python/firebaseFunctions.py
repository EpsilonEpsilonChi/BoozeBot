from firebase import firebase
firebase = firebase.FirebaseApplication("https://boozebot.firebaseio.com", None)
# result = firebase.get("/Users", None)
# print result

def addUser(username, fullname):
	# Add user to Firebase
	firebase.put('/Users', username, {"name": fullname})

if __name__ == '__main__':
	username = raw_input("Please enter a username: ")
	fullname = raw_input("Please enter your full name: ")
	addUser(username, fullname)