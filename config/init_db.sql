create table resume
(
    uuid      varchar(36) not null
        constraint resume_pk
            primary key,
    full_name text        not null
);

alter table resume
    owner to postgres;

create table contact
(
    id          serial
        constraint contact_pk
            primary key,
    resume_uuid varchar(36) not null
        constraint contact_resume_uuid_fk
            references resume
            on update restrict on delete cascade,
    type        text        not null,
    value       text        not null
);

alter table contact
    owner to postgres;

create unique index contact_uuid_type_index
    on contact (resume_uuid, type);

create table section_type
(
    id   serial
        constraint section_type_pk
            primary key,
    name varchar(36) not null
);

alter table section_type
    owner to postgres;

create unique index section_type_id_uindex
    on section_type (id);

create unique index section_type_name_uindex
    on section_type (name);

INSERT INTO section_type (name)
VALUES ('personal');
INSERT INTO section_type (name)
VALUES ('objective');
INSERT INTO section_type (name)
VALUES ('achievement');
INSERT INTO section_type (name)
VALUES ('qualifications');
INSERT INTO section_type (name)
VALUES ('experience');
INSERT INTO section_type (name)
VALUES ('education');

create table sections
(
    id              serial
        constraint sections_pk
            primary key,
    section_type_id integer     not null
        constraint sections_section_type_id_fk
            references section_type
            on update cascade on delete cascade,
    resume_uuid     varchar(36) not null
        constraint sections_resume_uuid_fk
            references resume
            on update cascade on delete cascade,
    text            text
);

alter table sections
    owner to postgres;

create unique index sections_id_uindex
    on sections (id);


