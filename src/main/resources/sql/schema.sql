create table chat
(
    id varchar(255) not null,
    enable_notifications boolean default true,
    
    constraint pk_chat primary key (id)
);

create table user
(
    username varchar(255) not null,
    chatId varchar(255) not null,
    
    constraint pk_user primary key (username, chatId)
);

create table ytchannel
(
    id varchar(255) not null,
    username varchar(255) not null,
    lastvideoid varchar(255) default null,
    lastvideodate date default null,
    
    unique (username),
    constraint pk_ytchannel primary key (id)
);

create table chat2ytchannel
(
	chat_pk varchar(255) not null,
    ytchannel_pk varchar(255) not null,
    
    constraint pk_chat2ytchannel primary key (chat_pk, ytchannel_pk),
    constraint chat_fk foreign key (chat_pk) references chat (id),
    constraint ytchannel_fk foreign key (ytchannel_pk) references ytchannel (id)
);

create table ytplaylist
(
    id varchar(255) not null,
    lastvideoid varchar(255) default null,
    
    constraint pk_ytplaylist primary key (id)
);

create table chat2ytplaylist
(
	chat_pk varchar(255) not null,
    ytplaylist_pk varchar(255) not null,
    
    constraint pk_chat2ytplaylist primary key (chat_pk, ytplaylist_pk),
    foreign key (chat_pk) references chat (id),
    foreign key (ytplaylist_pk) references ytplaylist (id)
);

create table custom_command
(
	`code` varchar(255) not null,
    chat_id varchar(255) not null,
    `value` varchar(4000) not null,
    
    constraint pk_custom_command primary key (`code`, chat_id)
);