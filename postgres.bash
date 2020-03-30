#!/usr/bin/env bash

docker stop us-postgres
docker rm us-postgres
docker run --name us-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=postgres -p 5434:5432 -d postgres
