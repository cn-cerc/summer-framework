#!/bin/bash

pwd
git pull
mvn clean install -T 1C -Dmaven.test.skip=true
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
  mvn clean install -T 1C -Dmaven.test.skip=true
  cd ..
done
