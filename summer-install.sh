#!/bin/bash

pwd
git pull
mvn install -T 1C -Dmaven.test.skip=true -Dmaven.compiler.onlyFilesChanged=true
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
  mvn install -T 1C -Dmaven.test.skip=true -Dmaven.compiler.onlyFilesChanged=true
  cd ..
done
