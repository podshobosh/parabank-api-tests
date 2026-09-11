SELECT first_name, last_name, COUNT(*)
FROM customers JOIN accounts ON accounts.customer_id = customers.customer_id
GROUP BY customers.customer_id, first_name, last_name
HAVING COUNT(*) > 1;
