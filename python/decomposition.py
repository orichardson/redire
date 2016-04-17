# -*- coding: utf-8 -*-
"""
Created on Fri Apr 15 19:39:50 2016

@author: Oliver
"""
from sklearn.datasets import load_svmlight_file
from sklearn.decomposition import NMF

from scipy.sparse import diags
import numpy as np

def samples(X, model):
	z = model.transform(X)
	z1,z2 = z[::2], z[1::2]
	
	return np.hstack([z1+z2, abs(z1-z2)])
	
def KLDAggregate(F,z1,z2,labels):
	# pk = # wik(1) and wik(2) and ri = 1
	n_sharedPP = np.zeros((F))
	n_sharedNPP = np.zeros((F))
	norm_p = np.zeros((F))
	norm_q = np.zeros((F))
	
	for i in range(z1.shape[0]) :
		# conditional probability that z2[i,k] and ri = 1, to calculate pk
		para = 1 if labels[i] > 0 else 0

		paraF2 = z2[i] * (para);	
		nparaF2 = z2[i] * (1-para);
		
		n_sharedPP = n_sharedPP + paraF2.multiply(z1[i]).toarray()[0]
		n_sharedNPP = n_sharedNPP + nparaF2.multiply(z1[i]).toarray()[0]
		
		norm_p = norm_p + paraF2.toarray()[0]
		norm_q = norm_q + nparaF2.toarray()[0]
		
	ones = np.ones(F)	
	pre_pk = np.nan_to_num(n_sharedPP / norm_p);
	pre_qk = np.nan_to_num(n_sharedNPP / norm_q);
	
	pk = [ pre_pk, ones-pre_pk ]
	qk = [ pre_qk, ones-pre_qk ]
	
	KL = sum(pk[x] * np.log(pk[x] / qk[x]) for x in [0,1])
		
	return np.clip(np.nan_to_num(KL),0,100)


x_test, y_test = load_svmlight_file('../out/test_distsim2.txt')
F = n_features=x_test.shape[1]
x_train, y_train = load_svmlight_file('../out/train_distsim2.txt',F)

print('loaded');

x1,x2,y1,ytest = x_train[::2], x_train[1::2], y_train[::2],y_test[::2]
weights = KLDAggregate(F,x1,x2,y1)

print('KL weights computed');

scaler = diags([weights], [0])
scaled_x_train = x_train*scaler
scaled_x_test = x_test*scaler
	
print('rescaled');

model = NMF(n_components=100, init='nndsvd', shuffle=True)
model.fit(scaled_x_train)

print('NMF decomp fitted');

		
from sklearn import linear_model, svm, metrics

z_train = samples(scaled_x_train, model)
z_test = samples(scaled_x_test, model)

classifiers = [svm.SVC(),  svm.SVC(kernel='poly',degree=4, max_iter=1000000),
		svm.SVC(kernel='linear', max_iter=10000000),
		linear_model.LogisticRegression(), linear_model.SGDClassifier()]

for clf in classifiers:
	clf.fit(z_train, y1)
	
	system = clf.predict(z_test)
	
	acc = metrics.accuracy_score(ytest, system)
	prec = metrics.precision_score(ytest, system, average=None)
	rec = metrics.recall_score(ytest, system, average=None)
	f1 = metrics.f1_score(ytest, system, average=None)

	print(acc, prec, rec,f1)