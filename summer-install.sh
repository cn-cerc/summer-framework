#!/bin/bash

git pull
mvn clean install

cd ..
cd summer-data
git pull
mvn clean install

cd ..
cd summer-mis
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
