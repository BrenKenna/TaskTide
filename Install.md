# Installing TaskTide
<p>
The repository is packaged with a pre-compiled version of TaskTide, and it is highly recommended to use that over installing from source. Gradle wrapper scripts have been provided for both Windows & Linux in the event buliding from source is a requirement.
</p>

---

## Pre-compiled Binary
<p>
The <a href="https://github.com/BrenKenna/TaskTide/releases/edit/v0.9.0">release section</a> of this repository contains a zip which contains all TaskTide dependancies, wrapper scripts for running on linux/windows and configuration files which can be adjusted for the "<a href="/Database-Driver-Installation.md">target deployment strategy</a>".
</p>

``` bash
# 1). Fetch zip
curl -so tasktide.zip https://github.com/BrenKenna/TaskTide/releases/download/v0.9.0/tasktide.zip

# 2). Unpack
unzip tasktide.zip && rm -f tasktide.zip
```

---

## Docker deployment
<p>
To support deployment onto <a href="/tasktide/deployment/Docker/Dockerfile">containerized platforms</a>. Where a database and a multiple TaskTide-Engine containers can be sporadically spun-up in batches depending on workload over time. A dockerfile for caching TaskTide in local repository, and docker-compose using <a href="https://hub.docker.com/_/couchdb">couchDB</a> as the database backend as have been provided.
</p>


``` bash
docker image build -t latest -f deployment/Docker/Dockerfile .

```

---

## Building from Source
<p>
The following describes building TaskTide from source using Gradle, different gradle installation scripts have been supplied which download and install gradle if necessary. Linux distributions should "<a href="/tasktide/gradlew">run this script</a>", and Windows should "<a href="/tasktide/gradlew.bat">should run this script</a>".
</p>
