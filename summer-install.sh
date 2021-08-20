#!/bin/bash

mvn clean install

cd ..
cd summer-model
mvn clean install

cd ..
cd summer-controller
mvn clean install

cd ..
cd summer-view
mvn clean install

cd ..
cd summer-local
mvn clean install
