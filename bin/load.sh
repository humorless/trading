#!/usr/bin/env bash                                                                                                     
                                                                                                                        
psql -d queue -c "DROP TABLE IF EXISTS jobs;"                                                                  
psql -d queue -c "CREATE TABLE jobs (                                                  
type text,
key_id text,
input_json text,
priority text,
done boolean
);"                                                                                                                     
                                                                                                                        
psql -d queue -c "\copy jobs FROM './data.csv' HEADER CSV;"        
