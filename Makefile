# Variables
VERSION := $(shell mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
WEBAPI_JAR_FILE = target/Robo-Arena-1.0-SNAPSHOT-webapi-jar-with-dependencies.jar
PID_FILE=/tmp/server.pid
PORT = 5050

.PHONY: all
all: clean build

.PHONY: clean
clean:
	mvn clean

.PHONY: build
build:
	mvn package

.PHONY: tests
tests:
	mvn test

.PHONY: test
test:
	mvn test

.PHONY: test-acceptance
test-acceptance:
	mvn clean verify

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

.PHONY: stop
stop: stop-server

.PHONY: acceptance
acceptance:
	mvn clean verify

.PHONY: webapi
webapi: build
	java -jar $(WEBAPI_JAR_FILE)

.PHONY: run-webapi
run-webapi: 
	java -jar $(WEBAPI_JAR_FILE)

.PHONY: push
push:
	@read -p "Enter commit message: " msg; \
	git add .; \
	git commit -m "$$msg"; \
	git push
