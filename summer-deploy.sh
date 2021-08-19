#!/bin/bash

mvn clean deploy

cd ..
cd summer-model
mvn clean deploy

cd ..
cd summer-view
mvn clean deploy

cd ..
cd summer-controller
mvn clean deploy

cd ..
cd summer-local
mvn clean deploy
