#!/bin/bash

pwd
git pull
mvn -T 1C clean install
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
  mvn -T 1C clean install
  cd ..
done
