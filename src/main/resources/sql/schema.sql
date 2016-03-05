create table chat
(
	pk int not null auto_increment,
    id varchar(255) not null,
    enable_notifications boolean default true,
    
    unique (id),
    constraint pk_chat primary key (pk)
);

create table user
(
	pk int not null auto_increment,
    username varchar(255) not null,
    chatId varchar(255) not null,
    
    unique (chatId),
    unique (username),
    constraint pk_user primary key (pk)
);

create table ytchannel
(
	pk int not null auto_increment,
    id varchar(255) not null,
    username varchar(255) not null,
    lastvideoid varchar(255) default null,
    
    unique (id),
    unique (username),
    constraint pk_ytchannel primary key (pk)
);

create table chat2ytchannel
(
	chat_pk int not null,
    ytchannel_pk int not null,
    
    constraint pk_chat2ytchannel primary key (chat_pk, ytchannel_pk),
    constraint chat_fk foreign key (chat_pk) references chat (pk),
    constraint ytchannel_fk foreign key (ytchannel_pk) references ytchannel (pk)
);