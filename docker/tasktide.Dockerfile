# Use Java17 base image installing required software
FROM eclipse-temurin:17-jre@sha256:ee489b56e4876e1c5ac5de99b95dc10c318e64b466cd611b1afb29905fa4857c


# Configure container
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y --no-install-recommends \
        unzip \
        jq \
        curl && \
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
