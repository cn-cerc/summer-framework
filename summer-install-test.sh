#!/bin/bash

mvn clean install

cd ..
cd summer-model
mvn clean install -D skipTests

cd ..
cd summer-controller
mvn clean install -D skipTests

cd ..
cd summer-view
mvn clean install -D skipTests

cd ..
cd summer-local
mvn clean install -D skipTests
