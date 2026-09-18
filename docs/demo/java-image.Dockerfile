# Java version
FROM eclipse-temurin:sha256:bb036ed6cfdc57e3da7c22634d15f1b840d2caf76183861c80e81ca4b5104abb

# Initialize env
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
       git \
       unzip \
    && rm -rf /var/lib/apt/lists/*

# Fetch repo
WORKDIR /src
RUN git clone https://github.com/BrenKenna/TaskTide.git

# Compile
WORKDIR /src/TaskTide/tasktide
RUN chmod +x gradlew \
    && sed -i 's/\r$//' gradlew \
    && ./gradlew assemble

# Unpack distribution and clean up
RUN mkdir -p ~/tasktide \
    && unzip -q tasktide/build/distributions/tasktide*.zip -d ~/tasktide/ \
    && rm -fr /src/TaskTide/
    && ln -s /opt/tasktide/tasktide/bin/task /usr/bin/tasktide

# Set directory for bash session
WORKDIR /app/tasktide
ENTRYPOINT [ "/bin/bash" ]