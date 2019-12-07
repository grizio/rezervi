create table theater(
  id uuid not null,
  uid uuid not null,
  name varchar(255) not null,
  address varchar(255) not null,
  plan jsonb not null,

  primary key (id),
  foreign key (uid) references application_user(uid)
);