# -*- coding: utf-8 -*-
"""
Created on Sat Apr 16 20:43:04 2016

@author: Oliver
"""


import numpy as np
from classify import analyze
from sklearn.datasets import load_svmlight_file

wan_x_train, wan_y_train = load_svmlight_file('./wan_full_train_lightsvm.txt')
wan_x_test, wan_y_test = load_svmlight_file('./wan_full_test_lightsvm.txt')
base_x_train, base_y_train = load_svmlight_file('./train_lightsvm.txt-new')
base_x_test, base_y_test = load_svmlight_file('./test_lightsvm.txt-new')

# Scale negative label to -1 from 0
wan_y_train =  np.ones(wan_y_train.shape) - 2*wan_y_train
wan_y_test =  np.ones(wan_y_test.shape)- 2*wan_y_test 



tot_x_train = np.hstack([wan_x_train.toarray(),  base_x_train.toarray()])
tot_x_test = np.hstack([wan_x_test.toarray(),  base_x_test.toarray()])

wan_x_train = wan_x_train.toarray()
wan_x_test = wan_x_test.toarray()
base_x_train = base_x_train.toarray()
base_x_test = base_x_test.toarray()

analyze(base_x_train, base_y_train, base_x_test, base_y_test)
analyze(wan_x_train, wan_y_train, wan_x_test, wan_y_test)
analyze(tot_x_train, wan_y_train, tot_x_test, wan_y_test)
