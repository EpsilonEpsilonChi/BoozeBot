import pickle

bottles = {"vodka" : 1, "gin" : 3, "tequila" : 7}
dataFile = open(r"data.pkl", "wb")
pickle.dump(bottles, dataFile)
dataFile.close()

users = {"Connor" : 2, "Alex" : 9, "Matt" : 27}
dataFile = open(r"data.pkl", "wb")
pickle.dump(users, dataFile)
dataFile.close()