create table public.auditory (
    id bigint,
    name varchar(100),
    number_of_seats bigint,
    vip_seats_str text
);

insert into public.auditory(id, name, number_of_seats,vip_seats_str)
values(1,'Grand Hall',20,'1,2,3');