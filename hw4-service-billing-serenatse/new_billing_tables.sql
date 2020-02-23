USE cs122b_db143;

DROP TABLE IF EXISTS movie_prices;
CREATE TABLE IF NOT EXISTS movie_prices
(
	movieId	 		VARCHAR(10) 	PRIMARY KEY NOT NULL,
	unit_price		FLOAT			NOT NULL,
	discount		FLOAT			NOT NULL			
);

-- INSERT INTO movie_prices (movieId, unit_price, discount)
-- SELECT id, rand()*10+10, rand()*0.5+0.5 
-- FROM cs122b_db143.movies;

DROP TABLE IF EXISTS transactions;
CREATE TABLE IF NOT EXISTS transactions
(
	sId				INT 			PRIMARY KEY NOT NULL,
	token			VARCHAR(50)		NOT NULL,
	transactionId	VARCHAR(50),

	FOREIGN KEY (sId)
		REFERENCES cs122b_db143.sales(id)
);

