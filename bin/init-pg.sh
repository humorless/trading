#!/usr/bin/env bash                                                                                                     

psql -c "CREATE DATABASE queue;"
psql -c "CREATE ROLE orange WITH SUPERUSER LOGIN PASSWORD 'orange';"
psql -c "GRANT ALL ON DATABASE queue TO orange;"
