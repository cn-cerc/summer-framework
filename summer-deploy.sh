#!/bin/bash

pwd
git pull
mvn -T 1C clean deploy

projects="
  summer-data
  summer-mis
  summer-view
  summer-local
  "

for project in $projects
do
  cd $project
  git pull
  mvn -T 1C clean deploy
  cd ..
done
