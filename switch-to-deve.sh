#!/bin/bash

git checkout develop
git pull

cd ..
cd summer-data
git checkout develop
git pull

cd ..
cd summer-mis
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
