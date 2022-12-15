#!/usr/bin/python
#encoding: utf-8

import os
import sys
import time
    
def echo(text):
    sys.stdout.write(text + '\n')
    sys.stdout.flush()

def sh(cmd):
    echo(cmd)
    os.system(cmd)

def cd(path):
    echo("cd " + path)
    os.chdir(path)

def process(kind):
    cd('d:\\i-work\\' + item)
    if kind == '1':
        sh('git checkout develop')
        sh('git pull')
    if kind == '2':
        sh('git checkout beta')
        sh('git pull')
    if kind == '3':
        sh('git checkout main')
        sh('git pull')
    if kind == '4':
        sh('mvn clean package')
    if kind == '5':
        sh('mvn clean install')
    if kind == '5':
        sh('mvn clean deploy')

    if kind == '7':
        process('1')
        process('4')
    if kind == '8':
        process('2')
        process('5')
    if kind == '9':
        process('3')
        process('6')

echo(u"""************** summer-framework manage **************
1: switch to develop && git pull
2: switch to beta && git pull
3: switch to main && git pull
--
4: mvn clean package
5: mvn clean install
6: mvn clean deploy
--
7: switch to develop && git pull && mvn clean package
8: switch to beta && git pull && mvn clean install
9: switch to main && git pull && mvn clean deploy
""")

if len(sys.argv) == 2:
    choice = sys.argv[1]
else:
    choice = input("plese choice, other key to exit:")
echo('choice: ' + choice)

projects = ['summer-framework', 'summer-data', 'summer-mis', 'summer-view', 'summer-local']
for item in projects:
    process(choice)
