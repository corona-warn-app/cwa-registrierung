alter table cancellation_jobs
    add send_email bool default true;

drop view if exists cancellation_job_summaries;
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