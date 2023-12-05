#!/bin/bash

# Build the Docker image
docker build -t codewaveservice .

# Run the Docker container
docker run -p 8080:8080 codewaveservice
