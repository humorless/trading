#!/usr/bin/env bash                                                                                                     
                                                                                                                        
psql -d queue -c "DROP TABLE IF EXISTS jobs;"                                                                  
psql -d queue -c "CREATE TABLE jobs (                                                  
type text,
key_id bigint,
input_json text,
priority text,
done boolean,
updated timestamp
);"                                                                                                                     
                                                                                                                        
psql -d queue -c "\copy jobs FROM './resources/data.csv' HEADER CSV;"        
