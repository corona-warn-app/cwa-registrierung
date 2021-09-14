--liquibase formatted sql
--changeset rlu:create-testcenter-database-structure
create table "partners" (
    id serial,
    name varchar(100) not null,
    contact varchar(100),
    email varchar(255) not null unique,
    phone varchar(100),
    street varchar(255),
    number varchar(255),
    postal_code varchar(5),
    city varchar(100),
    count int,
    software_solution varchar(255),
    legalisation_proof boolean,
    rat boolean,
    estimated_capacity int,
    created timestamp not null,
    primary key (id)
);

CREATE INDEX partner_created_idx ON partners USING BTREE(created);