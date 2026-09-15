#!/bin/bash


####################################################
####################################################
##
## 1). Configure Maven Central Release
##
## e354bbf3c47f28654b80cbc8b85904d1765b9698
##
####################################################
####################################################


# Initialize required variables
REPO="https://github.com/BrenKenna/TaskTide.git"
COMMIT="e354bbf3c47f28654b80cbc8b85904d1765b9698"
DATE="2026-08-19"
VERSION="v0.9.0"

# Configure active release
mkdir -p $DATE $VERSION && cd $DATE
git clone $REPO ./

git switch --detach $COMMIT


# Assemble task tide
cd tasktide
./gradlew assemble && ls -lht tasktide/build/distributions/tasktide-0.9.0.zip

mv tasktide/build/distributions/tasktide-0.9.0.zip ../../v0.9.0/
cd ../../v0.9.0/
md5sum tasktide-0.9.0.zip > tasktide-0.9.0.zip.md5sum

cp tasktide/build/resources/main/log4j2.xml ../../v0.9.0/
cp tasktide/build/resources/main/splash.txt ../../v0.9.0/
cp tasktide/build/resources/main/META-INF/* ../../v0.9.0/ 

cd ../
rm -fr $DATE

'''
BUILD SUCCESSFUL in 42s
24 actionable tasks: 24 execute

tasktide/build/distributions/tasktide-0.9.0.zip

cc0f9f6243f45e9d1eacdb4405a0eff6 tasktide-0.9.0.zip
'''