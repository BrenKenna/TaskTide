#!/bin/bash


#####################################################
#####################################################
##
## 1). Configure Maven Central Release
##
## e354bbf3c47f28654b80cbc8b85904d1765b9698
##
#####################################################
#####################################################


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

cd $VERSION
gh release upload $VERSION tasktide-0.9.0.zip

'''
BUILD SUCCESSFUL in 42s
24 actionable tasks: 24 execute

tasktide/build/distributions/tasktide-0.9.0.zip

cc0f9f6243f45e9d1eacdb4405a0eff6 tasktide-0.9.0.zip

Successfully uploaded 1 asset to v0.9.0
'''


#################################################################
#################################################################
##
## 2). Notes Landmark Buggey Historical Commits
##
## These note how the use-cases lead to developmental features
##
## Candidates: Zip archive each
## 239a7c2bf04ff0a0907b6bd3ad649aa9649f62d6, 26/02/19 <- Current
## e51273c4f82cff76535604b430a29e844d8d3853, 25/10/24 <- Leader Election
## 3f0b538d9bc5d97d2874b5494008443e999c9671, 25/08/18 <- Manager CQRS
##
#################################################################
#################################################################


# Initialize required variables
REPO="https://github.com/BrenKenna/TaskTide.git"
COMMIT="239a7c2bf04ff0a0907b6bd3ad649aa9649f62d6"
DATE="2026-02-19"
VERSION="v0.9.2"


# Configure active release
mkdir -p $DATE $VERSION
cd $DATE
git clone $REPO ./

git switch --detach $COMMIT


# Assemble task tide
export JAVA_HOME="/c/Program Files/Java/jdk-17"
export PATH="$JAVA_HOME/bin:$PATH"
cd tasktide
./gradlew assemble


# Package artifacts
mkdir tmp
mv tasktide/build/distributions/tasktide*zip tmp/
for i in $(find "tasktide/build/resources" -type f)
do
    mv $i tmp/
done

tar -czvf \
    ../../$VERSION/tasktide-engine_worker-web_api-motivation.tar.gz \
    -C tmp/ .

# Clear repo dir
cd ../../
rm -fr $DATE