create table COMPATIBILITY (
  tag_a int not null references TAG(ID),
  tag_b int not null references TAG(ID),
  compatibility double precision not null,
  PRIMARY KEY(tag_a, tag_b)
);

create unique index icomp on COMPATIBILITY(greatest(tag_a, tag_b), least(tag_a, tag_b));
