INSERT INTO keys (id, name) VALUES
  (13, 'The key 13')
, (14, 'The key 14');

INSERT INTO jobs (id, type, key_id, input_json, priority, done, updated) VALUES
  (0,'x',13,'{"a":1}',0,false,NOW())
, (1,'y',14,'{"b":1}',1,false,NOW()+ '1 hour')
, (2,'x',13,'{"a":2}',2,false,NOW()+ '2 hours');
