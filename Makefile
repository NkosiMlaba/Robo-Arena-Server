# Variables
VERSION := $(shell mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
SERVER_JAR_FILE = target/RobotWorld-1.0-SNAPSHOT-server-jar-with-dependencies.jar
CLIENT_JAR_FILE = target/RobotWorld-1.0-SNAPSHOT-client-jar-with-dependencies.jar
WEBAPI_JAR_FILE = target/RobotWorld-1.0-SNAPSHOT-webapi-jar-with-dependencies.jar

PID_FILE=/tmp/server.pid
RELEASE_DIR = release
VERSION_FILE = VERSION
RELEASE_NAME_PREFIX = RobotWorld
RELEASE_TARBALL_PREFIX = $(RELEASE_NAME_PREFIX)-
RELEASE_TARBALL_SUFFIX = .tar.gz

PORT = 5050

# Default target
.PHONY: all
all: clean build

# Clean the project
.PHONY: clean
clean:
	mvn clean

# Build the project and package it into a JAR file
.PHONY: build
build:
	mvn package

# Run tests
.PHONY: tests
tests:
	mvn test

# Run tests
.PHONY: test
test:
	mvn test

# Run tests
.PHONY: test-acceptance
test-acceptance:
	mvn clean verify

# Run the client
.PHONY: client
client: build
	java -jar $(CLIENT_JAR_FILE) localhost 5050

# Run the server
.PHONY: server
server: build
	java -jar $(SERVER_JAR_FILE) -p 5050 -s 10 -o 4,4

# Target to start the server in the background and save the PID
.PHONY: back-server
back-server:
	java -jar $(SERVER_JAR_FILE) & echo $$! > $(PID_FILE)

# Target to stop the server using the PID
.PHONY: stop-server
stop-server:
	
	@if netstat -tuln | grep ':5050' > /dev/null; then \
		PID=$$(netstat -tulnp 2>/dev/null | grep ':5050' | awk '{print $$7}' | cut -d'/' -f1); \
		if [ -n "$$PID" ]; then \
			kill $$PID && rm -f $(PID_FILE); \
			echo "Stopped server process $$PID"; \
		fi \
	else \
		echo "No server process found on port 5050"; \
	fi
	
	@if netstat -tuln | grep ':5000' > /dev/null; then \
		PID=$$(netstat -tulnp 2>/dev/null | grep ':5000' | awk '{print $$7}' | cut -d'/' -f1); \
		if [ -n "$$PID" ]; then \
			kill $$PID && rm -f $(PID_FILE); \
			echo "Stopped server process $$PID"; \
		fi \
	else \
		echo "No server process found on port 500"; \
	fi
# Target to stop the server using the PID
.PHONY: stop
stop: stop-server

# Target to run our server and acceptance tests
.PHONY: acceptance
acceptance:
# make back-server
	mvn clean verify

# Target to run our server and acceptance tests
.PHONY: acceptance-ourserver
acceptance-ourserver:
# make back-server
	mvn clean verify -Dserver.jar.path=$(SERVER_JAR_FILE)

# Target to bild and run webapi
.PHONY: webapi
webapi: build
	java -jar $(WEBAPI_JAR_FILE)

# Target to run webapi directly without building
.PHONY: run-webapi
run-webapi: 
	java -jar $(WEBAPI_JAR_FILE)

# Target to run server directly without building
.PHONY: run-server
run-server: 
	java -jar $(SERVER_JAR_FILE) -p 5050

# Target to run client directly without building
.PHONY: run-client
run-client: 
	java -jar $(CLIENT_JAR_FILE)
