from flask import Flask, jsonify, request
import json
import numpy as np
from random import random
import sys
import math
from sklearn import metrics
from scipy.special import expit


arr1 = np.load('TrainData.npy')
arr = np.transpose(arr1)
actuals = np.load('TrainLabel.npy') 

#make sure w1 + w2 = 1
w1 = 0.6 #weight related to false neg
w2 = 0.4 #weight related to false pos
thresh = 0.5 #classification threshold
lambda_l = 0.1 #lasso penalty parameter


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
        	G = math.sqrt(specificity*recall)
        	f_weighted = 1/(w1/recall + w2/precision)

        
        """
        lasso_penalty = np.linalg.norm(beta, ord =1) - abs(beta[len(beta)-1])
        add_term = lambda_l*lasso_penalty

        
        recall = TP/(TP+FN)
        specificity = TN/(TN+FP)
        
        

        G = G - add_term
        f_weighted = f_weighted - add_term
        accuracy = accuracy -add_term
        """
        absbeta = np.abs(np.array(params))
        norm1 = np.sum(absbeta)
        norm1= norm1 - absbeta[len(absbeta)-1]

        G = G - lambda_l*norm1
        f_weighted = f_weighted - lambda_l*norm1
        accuracy = accuracy - lambda_l*norm1

        #return(str(G))
        return(str(f_weighted))
        #return(str(accuracy))


        
       	
        
        
if __name__ == '__main__':
    app.run(debug=True)
