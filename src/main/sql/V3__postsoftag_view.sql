create view postsoftag as
select
t.tag_id tag,
array_agg(t.post_id) posts
from taginpost t
group by tag;