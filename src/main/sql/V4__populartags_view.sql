create view populartags as
select
 t.id id,
 t.ig_id ig_id,
 t.title title,
 max(tc.count) cnt,
 max(tc.count_date) date
from tag t
  join tagcount tc
  on tc.tag_id = t.id
where tc.count > 500000
group by t.id
order by cnt desc;