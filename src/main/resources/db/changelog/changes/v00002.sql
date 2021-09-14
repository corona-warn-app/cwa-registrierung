--changeset rlu:create-testcenter-export-log-table
create table "exports" (
    id serial,
    export_time timestamp,
    issued_by varchar,
    primary key (id)
);