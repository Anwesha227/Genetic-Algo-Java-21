from flask import Flask, jsonify, request
import json
import numpy as np
from random import random
import sys
import math
from sklearn import metrics
from scipy.special import expit


arr1 = np.load('TestData.npy')
arr = np.transpose(arr1)
actuals = np.load('TestLabel.npy')

#make sure w1 + w2 = 1
w1 = 0.5 #weight related to false neg
w2 = 0.5 #weight related to false pos
thresh = 0.5 #classification threshold


def sigmoid(x):
	return expit(x)


app = Flask(__name__)

@app.route('/add/', methods = ['POST'])
def add_numbers():
    if request.method == 'POST':
        decoded_data = request.data.decode('utf-8')
        params = json.loads(decoded_data)
       

        beta = np.array(params).reshape(1,len(params))
        
        a = np.matmul(beta,arr)
        b = sigmoid(a)
        predicted = b.ravel()

        

        for i in range(predicted.shape[0]):
        	if predicted[i]>=thresh:predicted[i]=1
        	else:predicted[i]=0
        #s now is analogous to the array 'assigned' of 0s and 1s

        TN, FP, FN, TP = metrics.confusion_matrix(actuals, predicted).ravel()
        accuracy = metrics.accuracy_score(actuals, predicted)
        
        if(TP==0):
            f_weighted = 0
            precision = 0
            recall = 0
            if(TN==0):
                specificity = 0
            else:
                specificity = TN/(TN+FP)
        else:
            if(TN==0):
                specificity = 0
            else:
                specificity = TN/(TN+FP)
            precision = metrics.precision_score(actuals,predicted)
            recall = metrics.recall_score(actuals,predicted)
            f_weighted = 1/(w1/recall + w2/precision)

        #return(str(f_weighted))
        
        
        

        """
        recall = TP/(TP+FN)
        specificity = TN/(TN+FP)
        return str(math.sqrt(recall*specificity))
        """
        retstr = str(accuracy)+","+str(f_weighted)+","+str(precision)+","+str(recall)+","+str(specificity)
        return(retstr)

        
       	
        
        
if __name__ == '__main__':
    app.run(host="localhost", port=8000, debug=True)
