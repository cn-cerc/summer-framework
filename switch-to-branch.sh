#!/bin/bash

branch=$1

git checkout $branch
git pull &

cd ..
cd summer-data
git checkout $branch
git pull &

cd ..
cd summer-mis
git checkout $branch
git pull &

cd ..
cd summer-view
git checkout $branch
git pull &

cd ..
cd summer-local
git checkout $branch
git pull &

wait