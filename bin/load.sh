#!/usr/bin/env bash                                                                                                     

psql -d queue -c "DROP TABLE IF EXISTS jobs;"                                                                  
psql -d queue -c "DROP TABLE IF EXISTS keys;"

psql -d queue -c "CREATE TABLE keys (
id BIGINT,
name TEXT,
PRIMARY KEY(id)
);"

psql -d queue -c "CREATE TABLE jobs (                                                  
id BIGINT,
type TEXT,
key_id BIGINT,
input_json TEXT,
priority SMALLINT,
done BOOLEAN,
updated TIMESTAMP,
PRIMARY KEY(id),
CONSTRAINT fk_key_id
  FOREIGN KEY(key_id)
    REFERENCES keys(id)
      ON DELETE SET NULL
);"
# priority 2 > 1 > 0
# The higher the priority the bigger the integer

psql -d queue -f ./resources/input-data.sql
