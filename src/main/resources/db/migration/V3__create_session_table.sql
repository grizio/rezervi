create table session(
  id uuid not null,
  date timestamp not null,
  theater_id uuid not null,

  primary key (id),
  foreign key (theater_id) references theater(id)
);