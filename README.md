# Trading System

# Initialization

Prepare the Postgres database
```
bin/init-pg.sh
bin/load.sh
```

# Trouble shooting

When compilation behavior seems wired, try to remove the `.cpcache` directory.

# Reference

1. [Using Postgres as a queue](https://gormcasper.dk/posts/using-postgres-as-a-queue/)
2. [Logging in Clojure: Making Sense of the Mess](https://lambdaisland.com/blog/2020-06-12-logging-in-clojure-making-sense-of-the-mess)
3. [Logging in Practice with Glögi and Pedestal.log](https://lambdaisland.com/blog/2020-09-28-logging-in-practice-glogi-pedestal)
