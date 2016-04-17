REDIRE
===========
(Recognition Entails Discovery of Inference Rules, Ellen.)


Files of importance: everything in the *src/* folder is the java source, for generating feature vectors.
The *out/* folder contains dumped feature vector files, in svm_light format. The *python/* folder contains the python code, 
which was used primarily for the machine learning, since scikit-learn is a lot better than the stanford
classifier, and we needed faster matrix algebra to do non-negative matrix decomposition.

Required JAR Files:
------------

Stanford NLP (also contains the EJML and SL4FJ jar files):
* [stanford-postagger](http://nlp.stanford.edu/software/stanford-postagger-2015-12-09.zip)
* [stanford-parser-full](http://nlp.stanford.edu/software/stanford-parser-full-2015-12-09.zip)
* [stanford-classifier](http://nlp.stanford.edu/software/stanford-classifier-2015-12-09.zip)
* [stanford-core](http://nlp.stanford.edu/software/stanford-corenlp-full-2015-12-09.zip)

Simmetric:
* [simmetrics](http://search.maven.org/remotecontent?filepath=com/github/mpkorstanje/simmetrics-core/4.1.0/simmetrics-core-4.1.0.jar)

Google Collections:
* [guava](http://search.maven.org/remotecontent?filepath=com/google/guava/guava/19.0/guava-19.0.jar)

WordNet:
* [WordNet] DO NOT DOWNLOAD MAKS' KEYLOGGER.
* [JWI](http://projects.csail.mit.edu/jwi/download.php?f=edu.mit.jwi_2.4.0_all.zip)

Word Similarity:
* [WS4J](https://storage.googleapis.com/google-code-archive-downloads/v2/code.google.com/ws4j/ws4j-1.0.1.jar)
