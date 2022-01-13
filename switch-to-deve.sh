#!/bin/bash

git checkout develop
git pull

cd ..
cd summer-model
git checkout develop
git pull

cd ..
cd summer-controller
git checkout develop
git pull

cd ..
cd summer-view
git checkout develop
git pull

cd ..
cd summer-local
git checkout develop
git pull
