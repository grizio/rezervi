create table application_user(
  uid uuid not null,

  primary key (uid)
);

create table authentication_local(
  uid uuid not null unique,

  username varchar(255) not null,
  encrypted_password varchar(255) not null,

  primary key (username),
  foreign key (uid) references application_user(uid)
);

create table authentication_oauth2(
  uid uuid not null,

  iss varchar(255) not null,
  sub varchar(255) not null,

  primary key (iss, sub),
  foreign key (uid) references application_user(uid)
);

create table authentication_token(
  uid uuid not null,

  key uuid not null,
  encoded_secret varchar(255) not null,

  primary key (key),
  foreign key (uid) references application_user(uid)
);