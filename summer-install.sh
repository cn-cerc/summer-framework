#!/bin/bash

mvn clean install

cd ..
cd summer-model
git pull
mvn clean install

cd ..
cd summer-controller
git pull
mvn clean install

cd ..
cd summer-view
git pull
mvn clean install

cd ..
cd summer-local
git pull
mvn clean install
