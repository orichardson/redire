# -*- coding: utf-8 -*-
"""
Created on Sat Apr 16 17:10:09 2016

@author: Oliver
"""



from sklearn import linear_model, svm, metrics, preprocessing
from sklearn.feature_selection import VarianceThreshold, SelectPercentile, f_classif
from sklearn.pipeline import Pipeline
from sklearn.ensemble import GradientBoostingClassifier
from sklearn.ensemble import BaggingClassifier

def analyze(X_train, Y_train, X_test, Y_test):

	pipe = Pipeline([('varthresh', VarianceThreshold()), 
					('selper', SelectPercentile(f_classif, 80)),
					('prepro', preprocessing.StandardScaler(with_mean=False)) ])
	
	
	xain, yain = pipe.fit_transform(X_train, Y_train), Y_train
	xest, yest = pipe.transform(X_test), Y_test
	
	classifiers = [('rbf SVM', svm.SVC()),
			('poly2 SVM', svm.SVC(kernel='poly',degree=2, verbose=False, max_iter=100000000)),
			('poly2 SVM, g=0.1',svm.SVC(kernel='poly',degree=2, verbose=False, max_iter=1000000,gamma=0.1)),
			('poly4 SVM',svm.SVC(kernel='poly',degree=4, verbose=False, max_iter=1000000)),
			('poly3 SVM, g=0.1',svm.SVC(kernel='poly',degree=3, verbose=False, max_iter=1000000,gamma=0.1)),
			('Logistic',linear_model.LogisticRegression()),
			('SGD',linear_model.SGDClassifier()),
			('GradBoost stub', GradientBoostingClassifier(n_estimators=100, learning_rate=1.0,  max_depth=1, random_state=0)),
			('GradBoost sapling', GradientBoostingClassifier(n_estimators=100, learning_rate=1.0,  max_depth=2, random_state=0)),
			('linear SVM', svm.SVC(kernel='linear')),
			('Bagging,poly2', BaggingClassifier(svm.SVC(kernel='poly',degree=2, max_iter=1000000,gamma=0.1), max_samples=0.5, max_features=0.5)),
			('Bagging,logistic', BaggingClassifier(linear_model.LogisticRegression()))
		]
		
	BaggingClassifier(svm.SVC(kernel='linear'), max_samples=0.5, max_features=0.5)
	
	print('Name & acc  & prec$^+$ & prec$^-$ & rec$^+$ & rec$^-$ & F1$^+$ & F1$^-$ \\\\')
	for name,clf in classifiers:
		clf.fit(xain, yain)
	
		system = clf.predict(xest);	
		
		acc = metrics.accuracy_score(yest, system)
		prec = metrics.precision_score(yest, system, average=None)
		rec = metrics.recall_score(yest, system, average=None)
		f1 = metrics.f1_score(yest, system, average=None)
		
		print((' & '.join(['{}'] + ['{:06.4f}']*7)).format(name, acc, prec[0],prec[1], rec[0],rec[1], f1[0],f1[1]),end=' \\\\\n')
		
	print()
