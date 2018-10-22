create table TAG (
    ID serial primary key,
    IG_ID bigint not null,
    TITLE varchar(255) not null
);

create table CATEGORY (
    ID serial primary key,
    TITLE varchar(255) not null
);

create table POST (
    ID serial primary key,
    IG_ID bigint not null,
    POST_TEXT text,
    POST_DATE bigint not null,
    PREVIEW_LINK varchar(255) not null,
    SHORT_CODE varchar(255) not null,
    LIKES int not null
);

create table TAGINPOST (
    TAG_ID int not null references TAG(ID),
    POST_ID int not null references POST(ID)
);

create table TAGINCATEGORY (
    TAG_ID int not null references TAG(ID),
    CATEGORY_ID int not null references CATEGORY(ID)
);

create table TAGCOUNT (
    ID bigserial primary key,
    TAG_ID int not null references TAG(ID),
    COUNT bigint not null,
    COUNT_DATE date default NOW()
);

