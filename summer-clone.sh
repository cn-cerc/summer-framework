#!/bin/bash

cd ..

# 定义仓库列表
projects="
  summer-framework
  summer-data
  summer-mis
  summer-view
  summer-local
  summer-sample
  "

for project in $projects
do
  git clone git@gitee.com:mimrc/"$project".git &
done

wait
