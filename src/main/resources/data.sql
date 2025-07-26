insert into customer (id, name, surname, credit_limit, used_credit_limit, created_by, created_date, last_modified_by, last_modified_date)
  values (1, 'Eray', 'Oral', 1000000, 0, 'admin', '2025-01-01T10:00:00+00:00', 'admin', '2025-01-01T10:00:00+00:00');
select next value for customer_seq;

--insert into loan (id, customer_id, loan_amount, number_of_installments, interest_rate, create_date, is_paid)
--  values (1, 1, 100000, 4, 0.2, '2025-05-01', false);
--select next value for loan_seq;
--
--insert into loan_installment (id, loan_id, amount_without_interest, amount, paid_amount, due_date, payment_date, is_paid)
--  values (1, 1, 25000, 30000, 30000, '2025-06-01', '2025-06-01', true);
--insert into loan_installment (id, loan_id, amount_without_interest, amount, paid_amount, due_date, payment_date, is_paid)
--  values (2, 1, 25000, 30000, 30000, '2025-07-01', '2025-07-01', true);
--insert into loan_installment (id, loan_id, amount_without_interest, amount, paid_amount, due_date, payment_date, is_paid)
--  values (3, 1, 25000, 30000, null, '2025-08-01', null, false);
--insert into loan_installment (id, loan_id, amount_without_interest, amount, paid_amount, due_date, payment_date, is_paid)
--  values (4, 1, 25000, 30000, null, '2025-09-01', null, false);
--select next value for loan_ins_seq;
--select next value for loan_ins_seq;
--select next value for loan_ins_seq;
--select next value for loan_ins_seq;
