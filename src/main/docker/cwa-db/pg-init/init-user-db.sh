#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER $PG_USER;
    CREATE DATABASE $PG_DATABASE;
    GRANT ALL PRIVILEGES ON $PG_DATABASE TO $PG_USER;
EOSQL