create table IF NOT EXISTS GENRES
(
    ID   INTEGER                not null primary key,
    NAME CHARACTER VARYING(20)  not null
);

create table IF NOT EXISTS RATINGS
(
    ID   INTEGER               not null primary key,
    NAME CHARACTER VARYING(20) not null
);

create table IF NOT EXISTS FILMS
(
    ID          INTEGER                not null primary key,
    NAME        CHARACTER VARYING(100) not null,
    DESCRIPTION CHARACTER VARYING(200),
    RELEASEDATE DATE                   not null,
    DURATION    INTEGER                not null,
    RATING      INTEGER REFERENCES RATINGS (ID)
);

create table IF NOT EXISTS USERS
(
    ID        INTEGER               not null primary key,
    EMAIL     CHARACTER VARYING(50) not null,
    LOGIN     CHARACTER VARYING(50) not null,
    NAME      CHARACTER VARYING(50) not null,
    BIRTHDATE DATE                  not null
);

create table IF NOT EXISTS FILMLIKES
(
    FILM_ID INTEGER not null REFERENCES FILMS (ID),
    USER_ID INTEGER not null REFERENCES USERS (ID),
    PRIMARY KEY (FILM_ID, USER_ID)
);

create table IF NOT EXISTS FRIENDSHIPS
(
    USER_ID  INTEGER REFERENCES USERS (ID),
    FRIEND   INTEGER REFERENCES USERS (ID),
    ACCEPTED BOOLEAN default FALSE,
    PRIMARY KEY (USER_ID, FRIEND)
);

create table IF NOT EXISTS FILMSGENRES
(
    FILM_ID  INT NOT NULL REFERENCES FILMS (ID),
    GENRE_ID INT NOT NULL REFERENCES GENRES (ID),
    PRIMARY KEY (FILM_ID, GENRE_ID)
)