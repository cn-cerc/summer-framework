#!/bin/bash

git checkout beta
git pull

cd ..
cd summer-data
git checkout beta
git pull

cd ..
cd summer-mis
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
