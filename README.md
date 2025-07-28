# Intro
- This is a simple Spring Boot web app to demonstrate Loan API case study.
- All endpoints except H2 console and Swagger ui are protected with HTTP basic authentication.
- H2 console: 
  - URL: `http://localhost:8080/h2-console`
  - Password: 123456
- Swagger ui: 
  - URL: `http://localhost:8080/swagger-ui/index.html`
- There are 2 users:
  - Username: admin, password: 1234, role: ADMIN
  - Username: user, password: 5678, role: USER
- Windows Command Prompt is being used in following examples.

# How to build and run the app
- `git clone https://github.com/eoral/loan-api.git`
- `cd loan-api`
- Set Java 17 to JAVA_HOME environment variable temporarily in current console session.
    - Example (Windows): `set JAVA_HOME=C:\Program Files\Java\jdk-17`
    - Example (Unix): `export JAVA_HOME=/usr/lib/jvm/java-17`
- `mvnw.cmd clean install`
- `mvnw.cmd spring-boot:run`
- Check if the app works: `curl "http://localhost:8080/customers" -u admin:1234`
- Expected output: 
```
[
	{
		"id": 1,
		"name": "Eray",
		"surname": "Oral",
		"creditLimit": 1000000.0,
		"usedCreditLimit": 0.0
	},
	{
		"id": 2,
		"name": "Arzu",
		"surname": "Dogan",
		"creditLimit": 1000000.0,
		"usedCreditLimit": 0.0
	}
]
```

# Endpoint: Create Loan
- Make a POST request to `/loans`
- Users with ADMIN role can operate on all customers. Other users can operate on customers that were created by them.
- Sample request body:
```
{
	"customerId": 1,
	"amount": 120000,
	"numberOfInstallments": 12,
	"interestRate": 0.5
}
```
- Sample response body:
```
{
	"id": 1,
	"loanAmount": 120000,
	"numberOfInstallments": 12,
	"interestRate": 0.5,
	"startDate": "2025-07-27",
	"isPaid": false
}
```
- Sample curl command:
```
curl -v -X POST "http://localhost:8080/loans" -H "Content-Type: application/json" -d "{\"customerId\":1,\"amount\":120000,\"numberOfInstallments\":12,\"interestRate\":0.5}" -u admin:1234
```

# Endpoint: List Loans For A Given Customer
- Make a GET request to `/loans`
- Users with ADMIN role can operate on all customers. Other users can operate on customers that were created by them.
- Request params:
  - customerId: long, required
  - numberOfInstallments: integer, optional
  - isPaid: boolean, optional
- Sample response body:
```
[
	{
		"id": 1,
		"loanAmount": 120000.0,
		"numberOfInstallments": 12,
		"interestRate": 0.5,
		"startDate": "2025-07-27",
		"isPaid": false
	}
]
```
- Sample curl commands:
```
curl -v "http://localhost:8080/loans?customerId=1" -u admin:1234
curl -v "http://localhost:8080/loans?customerId=1&numberOfInstallments=12" -u admin:1234
curl -v "http://localhost:8080/loans?customerId=1&isPaid=false" -u admin:1234
curl -v "http://localhost:8080/loans?customerId=1&numberOfInstallments=12&isPaid=false" -u admin:1234
```

# Endpoint: List Installments For A Given Loan
- Make a GET request to `/loans/{loanId}/installments`
- Users with ADMIN role can operate on all customers. Other users can operate on customers that were created by them.
- Sample response body:
```
[
	{
		"id": 1,
		"amountWithoutInterest": 10000.0,
		"amount": 15000.0,
		"paidAmount": null,
		"dueDate": "2025-08-01",
		"paymentDate": null,
		"isPaid": false
	},
	{
		"id": 2,
		"amountWithoutInterest": 10000.0,
		"amount": 15000.0,
		"paidAmount": null,
		"dueDate": "2025-09-01",
		"paymentDate": null,
		"isPaid": false
	},
	{
		"id": 3,
		"amountWithoutInterest": 10000.0,
		"amount": 15000.0,
		"paidAmount": null,
		"dueDate": "2025-10-01",
		"paymentDate": null,
		"isPaid": false
	}
]
```
- Sample curl command:
```
curl -v "http://localhost:8080/loans/1/installments" -u admin:1234
```

# Endpoint: Pay Loan
- Make a POST request to `/loans/{loanId}/payment`
- Users with ADMIN role can operate on all customers. Other users can operate on customers that were created by them.
- Sample request body:
```
{
	"amount": 80000
}
```
- Sample response body:
```
{
	"numberOfInstallmentsPaid": 3,
	"totalAmountSpent": 45000.0,
	"isPaidCompletely": false
}
```
- Sample curl command:
```
curl -v -X POST "http://localhost:8080/loans/1/payment" -H "Content-Type: application/json" -d "{\"amount\":80000}" -u admin:1234
```
