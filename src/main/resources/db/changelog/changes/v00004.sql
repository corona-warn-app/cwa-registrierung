create table cancellation_jobs
(
    uuid                  char(36)                 not null
        constraint job_pk
            primary key,
    filename              varchar                  not null,
    bcc                   varchar,
    additional_attachment varchar,
    partner_type          varchar                  not null,
    created               timestamp with time zone not null,
    cancel_in_portal      bool default true
);

create table cancellation_job_entries
(
    uuid                    char(36)
        constraint entries_pk
            primary key,
    job_uuid                char(36)
        constraint table_name_partners_id_fk
            references cancellation_jobs
            on update cascade on delete cascade,
    partner_id              varchar     not null,
    receiver                varchar     not null,
    attachment_filename     varchar     not null,
    created                 timestamptz not null,
    sent                    timestamptz,
    final_deletion_request  timestamptz not null,
    final_deletion_response timestamptz,
    message                 text
);

create or replace view cancellation_job_summaries as
select jobs.*,
       (select count(*)
        from cancellation_job_entries
        where job_uuid = jobs.uuid) as entries,
       (select count(*)
        from cancellation_job_entries
        where job_uuid = jobs.uuid
          and sent is not null)     as sent,
       (select count(*)
        from cancellation_job_entries
        where job_uuid = jobs.uuid
          and message is not null)  as errors
from cancellation_jobs jobs;