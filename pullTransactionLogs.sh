#!/bin/bash

# Downloads transaction data for all users, puts into data/transactions/<username>.json
# Then reads the data to output analysis Excel spreadsheet

node helpers/pullTransactionData.js
cd helpers
python analyzeData.py
cd ..
