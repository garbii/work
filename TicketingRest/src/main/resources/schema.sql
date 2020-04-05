drop table if exists Ticket ;
drop table if exists Flight ;
drop table if exists Rout ;
drop table if exists Airport ;
drop table if exists Company ;

create table if not exists Airport (
	id identity,
	name varchar(25) not null,
	code varchar(10) not null,
	created_at date
);

create table if not exists Company (
	id identity,
	name varchar(50) not null,
	ack varchar(200) not null,
	created_at date
);
create table if not exists Rout (
	id identity,
	from_airport bigint not null,
	to_airport bigint not null,
	ack varchar(200) not null,
	created_at date
);

create table if not exists Flight (
	id identity,
	company BIGINT,
	rout BIGINT,
	capacity int not null,
	sold_ticket_count int,
	price double,
	ack varchar(200) not null,
	created_at date
);

create table if not exists Ticket (
	id identity,
	flight BIGINT,
	ticket_price double,
	ack varchar(200) not null,
	created_at date
);


