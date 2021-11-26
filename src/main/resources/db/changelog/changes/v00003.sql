create table registered_partners
(
    id         char(36)     not null primary key,
    partner_nr varchar(32)  not null,
    email      varchar(128) not null,
    token      varchar(128),
    approved   timestamptz,
    exported   timestamptz
)