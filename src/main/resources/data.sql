insert into customer (id, name, surname, credit_limit, used_credit_limit)
  values (1, 'Eray', 'Oral', 1000000, 0);
select next value for customer_seq;

insert into loan (id, customer_id, loan_amount, number_of_installments, create_date, is_paid)
  values (1, 1, 100000, 4, '2025-05-01', false);
select next value for loan_seq;

insert into loan_installment (id, loan_id, amount, paid_amount, due_date, payment_date, is_paid)
  values (1, 1, 26000, 26000, '2025-06-01', '2025-06-01', true);
insert into loan_installment (id, loan_id, amount, paid_amount, due_date, payment_date, is_paid)
  values (2, 1, 26000, 26000, '2025-07-01', '2025-07-01', true);
insert into loan_installment (id, loan_id, amount, paid_amount, due_date, payment_date, is_paid)
  values (3, 1, 26000, 26000, '2025-08-01', null, false);
insert into loan_installment (id, loan_id, amount, paid_amount, due_date, payment_date, is_paid)
  values (4, 1, 26000, 26000, '2025-09-01', null, false);
select next value for loan_ins_seq;
select next value for loan_ins_seq;
select next value for loan_ins_seq;
select next value for loan_ins_seq;
