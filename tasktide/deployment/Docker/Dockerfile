# Use Java17 base image installing required software
FROM eclipse-temurin:17-jre


# Configure container
RUN groupadd --system tasktide && \
    useradd --system --create-home --gid tasktide tasktide
RUN apt-get update && apt-get install -y unzip


# Unpack task into working directory
WORKDIR /opt/tasktide
COPY tasktide/build/distributions/tasktide-0.9.0.zip /tmp/
RUN unzip /tmp/tasktide-0.9.0.zip -d /opt && \
    mv /opt/tasktide-0.9.0/* /opt/tasktide && \
    rm -f /tmp/tasktide-0.9.0.zip && \
    chmod +x /opt/tasktide/bin/tasktide && \
    chown -R tasktide:tasktide /opt/tasktide


# Mark config folder as volume for swapping configs
VOLUME [ "/opt/tasktide/config" ]


# Switch to non root user to run task tide
USER tasktide
CMD [ "./bin/tasktide" ]