#!/bin/bash

git pull
mvn clean deploy

cd ..
cd summer-model
git pull
mvn clean deploy

cd ..
cd summer-controller
git pull
mvn clean deploy

cd ..
cd summer-view
git pull
mvn clean deploy

cd ..
cd summer-local
git pull
mvn clean deploy
