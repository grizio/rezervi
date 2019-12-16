alter table session add column prices jsonb not null default '[]'::jsonb;
alter table session add column reservations jsonb not null default '[]'::jsonb;
