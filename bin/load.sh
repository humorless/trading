#!/usr/bin/env bash                                                                                                     
                                                                                                                        
psql -d queue -c "DROP TABLE IF EXISTS jobs;"                                                                  
psql -d queue -c "CREATE TABLE jobs (                                                  
id bigint,
type text,
key_id bigint,
input_json text,
priority smallint,
done boolean,
updated timestamp
);"                                                                                                                     
# priority 2 > 1 > 0
# The higher the priority the bigger the integer

#psql -d queue -c "\copy jobs FROM './resources/data.csv' HEADER CSV;"        
psql -d queue -f ./resources/input-data.sql
