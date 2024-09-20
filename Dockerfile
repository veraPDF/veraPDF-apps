# See https://docs.docker.com/engine/userguide/eng-image/multistage-build/
# First build the app on a maven open jdk 11 container
ARG VERAPDF_VERSION=1.26
ARG VERAPDF_MINOR_VERSION=2
ARG VERAPDF_INSTALLER_FOLDER=releases
FROM eclipse-temurin:11-jdk-alpine AS app-installer
ENV VERAPDF_VERSION=${VERAPDF_VERSION}
ENV VERAPDF_MINOR_VERSION=${VERAPDF_MINOR_VERSION}
ENV VERAPDF_INSTALLER_FOLDER=${VERAPDF_INSTALLER_FOLDER}

WORKDIR /tmp
COPY docker-install.xml .
RUN wget -O /tmp/verapdf-installer.zip https://software.verapdf.org/${VERAPDF_INSTALLER_FOLDER}/${VERAPDF_VERSION}/verapdf-greenfield-${VERAPDF_VERSION}.${VERAPDF_MINOR_VERSION}-installer.zip
RUN unzip verapdf-installer.zip && java -jar ./verapdf-greenfield-${VERAPDF_VERSION}.${VERAPDF_MINOR_VERSION}/verapdf-izpack-installer-${VERAPDF_VERSION}.${VERAPDF_MINOR_VERSION}.jar docker-install.xml

# Now build a Java JRE for the Alpine application image
# https://github.com/docker-library/docs/blob/master/eclipse-temurin/README.md#creating-a-jre-using-jlink
FROM eclipse-temurin:11-jdk-alpine AS jre-builder

# Create a custom Java runtime
RUN "$JAVA_HOME/bin/jlink" \
         --add-modules java.base,java.logging,java.xml,jdk.crypto.ec,java.desktop,jdk.management \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /javaruntime

# Now the final application image
FROM alpine:3

# Set for additional arguments passed to the java run command, no default
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
# Specify the veraPDF REST version if you want to (to be used in build automation)
ARG VERAPDF_VERSION
ENV VERAPDF_VERSION=${VERAPDF_VERSION}

# Copy the JRE from the previous stage
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=jre-builder /javaruntime $JAVA_HOME

# Since this is a running network service we'll create an unprivileged account
# which will be used to perform the rest of the work and run the actual service:
RUN addgroup -S verapdf && adduser -S -G verapdf -h /opt/verapdf verapdf
RUN mkdir --parents /var/opt/verapdf/logs && chown -R verapdf:verapdf /var/opt/verapdf

USER verapdf

# Copy the application from the previous stage
COPY --from=app-installer /opt/verapdf/ /opt/verapdf/

WORKDIR /data
VOLUME /data

ENTRYPOINT ["/opt/verapdf/verapdf"]
