# Installing TaskTide

The following describes how to:
<ul>
    <li>Fetch and run pre-compiled application</li>
    <li>Run docker image</li>
    <li>Build application from source</li>
</ul>

As a Java application TaskTide requires [java+17](https://docs.oracle.com/en/java/javase/).

<br>

---

<br>

## 1). Pre-compiled Application
Each [TaskTide release](https://github.com/BrenKenna/TaskTide/releases/v0.9.0) contains a zip of all TaskTide dependancies, wrapper scripts for running on linux/windows and configuration files which can be adjusted for the [target deployment strategy](/docs/documentation/Database-Driver-Installation.md).

``` bash
# 1). Fetch zip
sudo su
mkdir -p /opt/java && \
    cd /opt/java
curl -so /opt/java/tasktide.zip \
    https://github.com/BrenKenna/TaskTide/releases/download/v0.9.0/tasktide.zip

# 2). Unpack
unzip tasktide.zip
rm -f tasktide.zip

# 3). Organize installation
mv /opt/java/tasktide-*/* /opt/java/tasktide/
rm -fr /opt/java/tasktide/tasktide-*

ln -sf \
    /opt/java/tasktide/bin/tasktide \
    /usr/bin/tasktide

# 4). Define accesible reference to TaskTide configs
cat >/etc/profile.d/tasktide.sh <<'EOF'
# Define accessible references to TaskTide configs
export TASKTIDE_INSTALL="/opt/java/tasktide"
export TASKTIDE_CONFIGS="$TASKTIDE_INSTALL/config"
export TASKTIDE_CONFIG_FILE="$TASKTIDE_CONFIGS/META-INF/microprofile-config.properties"
export TASKTIDE_LOGGING="$TASKTIDE_CONFIGS/log4j2.xml"
EOF
```

<br>

---

<br>

## 2). Run Docker Image

To support deployment onto containerized platforms, a [docker file](/docker/tasktide-latest.Dockerfile) for caching TaskTide in local repository has been provided.

A second [docker file](/docker/tasktide-apptainer.Dockerfile) which installs [Apptainer](https://apptainer.org/) is also provided, to support running containerized workloads within TaskTide.

Since the [public TaskTide images](https://docker.tasktide.org) form a part of TaskTide's CI/CD workflow. It is not recommended to build from source, but instead use those images.


``` bash
# Fetch repo
docker image pull \
    -t latest \
    -f deployment/Docker/Dockerfile .

# Run TaskTide
docker container run --rm \
    bkenna/tasktide:latest \
        < CLI: manager | engine | web-api > \
            < CLI Opts: >
```

<br>

---

<br>

## 3). Building from Source

Since the repository is packaged with a pre-compiled version of TaskTide, it is highly recommended to use that instead of installing from source, or building custom container images.

Gradle wrapper scripts have been provided for both Windows & Linux in the event buliding from source is a requirement, or the repoistory is forked for development work. The following describes building TaskTide from source using Gradle.

Different gradle installation scripts have been supplied which download and install gradle if necessary. Linux distributions should [run this script](/tasktide/gradlew), and Windows users should [run this script](/tasktide/gradlew.bat).

For context to the reader - The use of gradle build automation is optional, and would be more for development work with TaskTide using an IDE like NetBeans/Eclipse. These gradlew scripts open all of the build automation tooling such as assembly, TaskTide’s test taxonomy, and other build tasks thar supported its development.

```bash
# Fetch TaskTide repo
git clone \
    https://github.com/BrenKenna/TaskTide.git \
    tasktide-repo
cd tasktide-repo/tasktide

# Assemble TaskTide
chmod +x gradlew*
sed -i 's/\r$//' gradlew
./gradlew assemble

# Unpack and strip version
mkdir -p /opt/java/tasktide
unzip -q \
    tasktide/build/distributions/tasktide*.zip \
    -d /opt/java/tasktide/
mv /opt/java/tasktide-*/* /opt/java/tasktide/
rm -fr /opt/java/tasktide/tasktide-*

# Install binary
ln -sf \
    /opt/java/tasktide/bin/tasktide \
    /usr/bin/tasktide

# Define accesible reference to TaskTide configs
cat >/etc/profile.d/tasktide.sh <<'EOF'
# Define accessible references to TaskTide configs
export TASKTIDE_INSTALL="/opt/java/tasktide"
export TASKTIDE_CONFIGS="$TASKTIDE_INSTALL/config"
export TASKTIDE_CONFIG_FILE="$TASKTIDE_CONFIGS/META-INF/microprofile-config.properties"
export TASKTIDE_LOGGING="$TASKTIDE_CONFIGS/log4j2.xml"
EOF

```

---

Since TaskTide ships with ready to go database drivers, the un-used driver set can be removed. Which also optimizes TaskTides startup time, and resource utilization. Please also note ***this is entirely an optional as TaskTide still works fine without doing this***. Future deployments may optimize this as it is largely from how Jakarta-NoSQL are loaded and is resolvable by clearing libs from classpath.


```bash

# Move to TaskTide config
cd $TASKTIDE_INSTALL
mkdir -p jnosql-libs

# Aggregate jnosql-libs
mv lib/jnosql-arangodb-1.1.6.jar jnosql-libs/
mv lib/jnosql-cassandra-1.1.6.jar jnosql-libs/
mv lib/jnosql-couchbase-1.1.6.jar jnosql-libs/
mv lib/jnosql-dynamodb-1.1.6.jar jnosql-libs/
mv lib/jnosql-mongodb-1.1.6.jar jnosql-libs/
mv lib/jnosql-redis-1.1.6.jar jnosql-libs/
mv lib/jnosql-couchdb-1.1.6.jar jnosql-libs/
mv lib/jnosql-mapping-graph-1.1.8.jar jnosql-libs/
mv lib/jnosql-mapping-key-value-1.1.8.jar jnosql-libs/
mv lib/jnosql-mapping-column-1.1.8.jar jnosql-libs/

# Package into tarball for future reference
tar -czf jnosql-libs.tar.gz jnosql-libs/

# Clear libaries from class path
rm -fr jnosql-libs/
```
