USE cs122b_db143;

DROP TABLE IF EXISTS valid_strings;
CREATE TABLE IF NOT EXISTS valid_strings
(
    id          INT          PRIMARY KEY NOT NULL AUTO_INCREMENT,
    sentence    VARCHAR(512) NOT NULL,
    length      INT          NOT NULL
);

INSERT INTO valid_strings (sentence, length)
VALUES ("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
19);

INSERT INTO valid_strings (sentence, length)
VALUES ("Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
17);

INSERT INTO valid_strings (sentence, length)
VALUES ("Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.",
16);

INSERT INTO valid_strings (sentence, length)
VALUES ("Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
17);

INSERT INTO valid_strings (sentence, length)
VALUES ("Sollicitudin tempor id eu nisl nunc mi ipsum.",
8);

INSERT INTO valid_strings (sentence, length)
VALUES ("Dui id ornare arcu odio ut. Non enim praesent elementum facilisis leo vel fringilla est ullamcorper.",
16);

INSERT INTO valid_strings (sentence, length)
VALUES ("Iaculis urna id volutpat lacus laoreet non curabitur gravida.",
9);

INSERT INTO valid_strings (sentence, length)
VALUES ("Fames ac turpis egestas integer eget aliquet.",
7);

INSERT INTO valid_strings (sentence, length)
VALUES ("Accumsan lacus vel facilisis volutpat.",
5);

INSERT INTO valid_strings (sentence, length)
VALUES ("Praesent tristique magna sit amet purus gravida.",
7);

SELECT * FROM valid_strings;