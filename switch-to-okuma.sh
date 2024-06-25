#!/bin/bash

git checkout beta
git pull

cd ..
cd summer-model
git checkout beta
git pull

cd ..
cd summer-controller
git checkout beta
git pull

cd ..
cd summer-view
git checkout beta
git pull

cd ..
cd summer-local
git checkout beta
git pull
