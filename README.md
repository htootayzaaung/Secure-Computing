# Secure Computing: Web Application Security Analysis and Fixes

## Project Overview
This project focused on identifying and mitigating security vulnerabilities in a web application, including **SQL Injection, Cross-Site Request Forgery (CSRF),** and **insecure password storage**. The aim was to analyze potential threats, demonstrate their impact, and implement security measures to protect sensitive user data.

## Identified Security Vulnerabilities
### 1. SQL Injection Vulnerability
The application was vulnerable to SQL Injection due to unsanitized user input. Attackers could manipulate SQL queries to gain unauthorized access to sensitive data.

### 2. Cross-Site Request Forgery (CSRF)
The application was exposed to CSRF attacks because it lacked validation mechanisms to ensure that requests originated from trusted sources.

### 3. Insecure Password Storage
Passwords were stored in plain text within the database, posing a significant security risk in the event of a data breach.

## Fixes Implemented
### 1. Fixes Implemented for SQL Injection Vulnerability
To address the SQL Injection vulnerability, I refactored SQL queries to use **prepared statements**. This prevented user inputs from being directly concatenated into SQL queries, mitigating the risk of malicious injection attacks. Post-implementation testing confirmed that injection attempts were no longer successful.

### 2. Fixes Implemented for CSRF (Completed by Other Team Members)
CSRF protection was implemented by modifying **doGet()** and **doPost()** methods to include **CSRF tokens**. The token was initialized during login and verified before processing sensitive requests.

### 3. Fixes Implemented for Lack of Data Encryption (Completed by Other Team Members)
To enhance password security, **bcrypt hashing** was integrated to replace plaintext storage. The database schema was updated to store encrypted hashes, improving protection against brute-force attacks.

## Testing and Validation
After implementing security fixes, penetration testing was conducted:
- **SQL Injection**: Tested with malicious inputs; the application now properly rejects them.
- **CSRF**: Unauthorized form submissions were blocked due to CSRF token validation.
- **Password Storage**: Verified password hashing with bcrypt, ensuring successful authentication and secure storage.

## Conclusion
This project strengthened the security of the web application by addressing critical vulnerabilities. My contributions specifically involved preventing SQL Injection attacks through the use of prepared statements. The team also worked collaboratively on mitigating CSRF risks and securing password storage. These improvements enhanced the application's resilience against common cybersecurity threats.
