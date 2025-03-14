-- trigger for unit table on insert
create
or replace function unit_insert_trigger()
returns trigger as
'
begin
insert into event (entity_id, entity_type, event_type, created)
values (new.id, ''UNIT'', ''CREATED'', now());
return new;
end;
'
language plpgsql;

create trigger unit_insert
    after insert
    on unit
    for each row
    execute function unit_insert_trigger();

-- trigger for unit table on update
create
or replace function unit_update_trigger()
returns trigger as
'
begin
insert into event (entity_id, entity_type, event_type, created)
values (new.id, ''UNIT'', ''UPDATED'', now());
return new;
end;
'
language plpgsql;

create trigger unit_update
    after update
    on unit
    for each row
    execute function unit_update_trigger();

-- trigger for booking table on insert
create
or replace function booking_insert_trigger()
returns trigger as
'
begin
insert into event (entity_id, entity_type, event_type, email, created)
values (new.id, ''BOOKING'', ''CREATED'', new.email, now());
return new;
end;
'
language plpgsql;

create trigger booking_insert
    after insert
    on booking
    for each row
    execute function booking_insert_trigger();

-- trigger for booking table on update
create
or replace function booking_update_trigger()
returns trigger as
'
begin
insert into event (entity_id, entity_type, event_type, email, created)
values (new.id, ''BOOKING'', ''UPDATED'', new.email, now());
return new;
end;
'
language plpgsql;

create trigger booking_update
    after update
    on booking
    for each row
    execute function booking_update_trigger();