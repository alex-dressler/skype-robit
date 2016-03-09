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