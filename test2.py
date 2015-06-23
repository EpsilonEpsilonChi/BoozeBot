import pickle

d = {}
dataFile = open(r"data.pkl", "rb")
d = pickle.load(dataFile)
dataFile.close()

print d