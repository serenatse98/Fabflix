USE cs122b_db143;

DROP TABLE IF EXISTS responses;
CREATE TABLE IF NOT EXISTS responses
(
	transactionid	VARCHAR(128)	PRIMARY KEY NOT NULL,
	email			VARCHAR(50)		NULL,
	sessionid		VARCHAR(128)	NULL,
	response 		TEXT 			NOT NULL,
	httpstatus		INT				NOT NULL
);