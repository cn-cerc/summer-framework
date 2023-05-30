#!/bin/bash

pwd
git pull
mvn -T 1C clean deploy

cd ..
cd summer-data
git pull
mvn -T 1C clean deploy

cd ..
cd summer-mis
git pull
mvn -T 1C  clean deploy

cd ..
cd summer-view
git pull
mvn -T 1C clean deploy

cd ..
cd summer-local
git pull
mvn -T 1C clean deploy
