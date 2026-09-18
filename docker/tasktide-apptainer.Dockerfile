# Use Java25 base image installing required software
FROM eclipse-temurin:25-jre@sha256:bb036ed6cfdc57e3da7c22634d15f1b840d2caf76183861c80e81ca4b5104abb


# Configure container
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y --no-install-recommends \
        unzip \
        jq \
        curl \
        fuse \
        squashfuse \
        apptainer && \
    mkdir -p /home/tasktide/.apptainer && \
    rm -rf /var/lib/apt/lists/*


# Unpack task into working directory
COPY tasktide.zip /tmp/tasktide.zip
RUN unzip /tmp/tasktide.zip -d /opt && \
    mkdir -p /opt/tasktide && \
    mv /opt/tasktide-*/* /opt/tasktide/ && \
    chmod +x /opt/tasktide/bin/tasktide


# Mark config folder as volume for swapping configs
VOLUME [ "/opt/tasktide/config" ]


# Configure non-root user
RUN groupadd --system tasktide && \
    useradd --system --create-home --home-dir /home/tasktide --gid tasktide tasktide && \
    chown -R tasktide:tasktide /home/tasktide && \
    chown -R tasktide:tasktide /opt/tasktide


# Switch to non root user to run task tide
USER tasktide
WORKDIR /home/tasktide
ENTRYPOINT [ "/opt/tasktide/bin/tasktide" ]
