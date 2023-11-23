FROM gradle:jdk17

# Install plugin from sources
COPY build.gradle plugin/build.gradle
COPY src/main plugin/src/main
COPY README.md target* plugin/target/
RUN cd plugin && gradle publishToMavenLocal

# Copy test project
COPY src/system-test/resources/* /test-projects/
