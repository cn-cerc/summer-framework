#!/bin/bash

mvn clean install

cd ..
cd summer-model
git pull

cd ..
cd summer-controller
git pull

cd ..
cd summer-view
git pull

cd ..
cd summer-local
git pull
