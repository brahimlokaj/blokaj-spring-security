# drop table if exists flyway_schema_history;
# drop table if exists roles;
# drop table if exists users;

create table if not exists roles
(
    id         bigint       not null auto_increment,
    created_at datetime     not null,
    deleted_at datetime,
    update_at  datetime     not null,
    name       varchar(255) not null,
    primary key (id)
) engine = InnoDB;

create table if not exists users
(
    id         bigint       not null auto_increment,
    created_at datetime     not null,
    deleted_at datetime,
    update_at  datetime     not null,
    email      varchar(255) not null,
    firstname  varchar(255) not null,
    lastname   varchar(255) not null,
    password   varchar(255) not null,
    username   varchar(255) not null,
    role_id    bigint,
    primary key (id)
) engine = InnoDB;

alter table roles
    add constraint UK_ofx66keruapi6vyqpv6f2or37 unique (name);

alter table users
    add constraint UK_6dotkott2kjsp8vw4d0m25fb7 unique (email);

alter table users
    add constraint UK_r43af9ap4edm43mmtq01oddj6 unique (username);

alter table users
    add constraint FKp56c1712k691lhsyewcssf40f foreign key (role_id) references roles (id);