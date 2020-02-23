USE cs122b_db143;

DROP PROCEDURE IF EXISTS insert_sales_transactions;
DELIMITER #
CREATE PROCEDURE insert_sales_transactions
(
    IN e VARCHAR(50),
    IN mID VARCHAR(10),
    IN quant INT,
    IN d DATE,
    IN tk VARCHAR(50),
    IN tID VARCHAR(50)
)
BEGIN
    INSERT INTO sales (id, email, movieId, quantity, saleDate)
        VALUES (NULL, e, mID, quant, d);
    INSERT INTO transactions (sId, token, transactionId)
        VALUES (LAST_INSERT_ID(), tk, tID);
END#
DELIMITER ;