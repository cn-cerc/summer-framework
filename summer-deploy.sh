#!/bin/bash

pwd
git pull
mvn -T 1C clean deploy
cd ..

projects="
  summer-data
  summer-mis
  summer-view
  summer-local
  "

for project in $projects
do
  cd "$project" || exit
  git pull
  mvn -T 1C clean deploy
  cd ..
done
