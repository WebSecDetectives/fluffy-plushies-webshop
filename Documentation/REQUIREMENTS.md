# Security in Web Development Exam Project Requirements

Source: `exam-project-2026.pdf`

## Deadline

- Submit the report and project before **June 11, 2026 at 23:59**.
- Submission must be uploaded to Wiseflow.
- Report and code must be zipped into a single archive.
- A Git link alone is not accepted.

## Project Goal

Build or adapt a small web application as a proof of concept.

The main focus is not building a large feature-complete website. The main focus is:

- analyzing possible security vulnerabilities;
- testing whether the application can be broken;
- implementing selected mitigations;
- explaining what was fixed and why;
- documenting known remaining risks and limitations.

Existing code is allowed if:

- the original source is provided;
- license and references are included where relevant;
- the code can be explained during the exam;
- copied modules or functions are justified and understood.

## Minimum Application Requirements

The application must be a small web application/proof of concept with at least the following features:

- Multilevel login/privileges with backend authentication.
- New user registration.
- Session data stored in cookies or another form, such as `localStorage`.
- A list of items created by users.
- Item visibility settings: private/public.
- Admin user can see everything.
- A feature for adding data to an item, such as comments.
- File upload for images, such as profile pictures or item images.

The application does not need to be a mature production-quality system or compete with existing frameworks.
It should be small enough to understand, test, break, fix, and explain during the exam.

Known limitations must be documented in the report.

## Required Security Work

The project must include settings, security headers, or code that prevents or minimizes risk from at least the following:

- SQL injection.
- Command injection.
- Cross-site scripting, XSS.
- Cross-site request forgery, CSRF.
- XML external entity attacks, XXE.
- Serialization/injection issues.
- Client-side manipulation.
- Missing or weak server-side validation.

The project must demonstrate that session IDs and CSRF tokens are used in the correct places.

For each relevant security topic, the report should explain:

- what vulnerability or risk exists;
- how it was tested;
- what mitigation was implemented;
- why the mitigation is appropriate;
- what limitations or remaining risks still exist.

## Additional And Recommended Security Considerations

Consider and document the following where relevant:

- Password hashing in the user database.
- Use of Transport Layer Security, TLS.
- Use of encryption and/or hashing.
- Authorization and privilege checks.
- Uploaded file validation and storage risks.
- Security-relevant framework or server configuration.
- Relevant configuration files that affect security behavior, such as request handling, headers, TLS, file upload limits, error reporting, session handling, or framework defaults.

If significant configuration changes are made, the relevant configuration files should be included in the project.
The report may reference the important parts instead of pasting full configuration files.

## Technology Requirements And Constraints

- Any combination of frontend and backend languages is allowed.
- The project should use at least some JavaScript, HTML, and CSS.
- Common/readable backend languages are acceptable, such as Java, Python, PHP, etc.
- Helper libraries such as Bootstrap are allowed.
- Avoid full-featured backend frameworks like Django or similar if possible, because they handle many security details automatically.

If a framework is used that handles many potential vulnerabilities automatically, the report must explain:

- what security behavior the framework provides;
- how those mechanisms work;
- how they were tested;
- known vulnerabilities reported for that framework;
- what risks still remain.

## Deployment Requirements And Recommendations

- A running copy must be available on your own machine for the exam demo.
- Deployment to a real server is encouraged, especially if you want to test with tools such as Mozilla Observatory.
- A real server and domain can make TLS, headers, and server configuration easier to demonstrate.
- Possible hosting examples mentioned in the assignment include Amazon and DigitalOcean.

## Report Requirements

The report should document:

- what was done;
- why it was done;
- the chosen application and its purpose;
- a short company backstory, maximum half a page;
- vulnerabilities found;
- how vulnerabilities were tested;
- which vulnerabilities were fixed;
- how the fixes work;
- why the chosen fixes are appropriate;
- known limitations and remaining risks;
- security considerations that could be implemented later;
- relevant source code and configuration references;
- external references, including books, articles, libraries, tools, and copied code;
- a rough estimate of time spent on the project.

## Report Length

Maximum report length, excluding front page, table of contents, and appendix:

- 1 student: **15 pages**.
- 2 students: **20 pages**.
- 3 students: **25 pages**.
- 4 students: **30 pages**.

The report should not exceed the maximum page count. A report that is much shorter may be missing important content.

## Group And Submission Rules

- It is recommended to work in a group.
- Recommended group size is 2 to 4 students.
- Maximum group size is 4 students.
- Each student must submit individually in Wiseflow.
- Group members may submit the same report and project individually.
- The report front page must list all group members.
- The project source code should be documented with useful comments.
- Relevant configuration files should be included if significant changes were made.
- Important configuration changes may be referenced in the report instead of pasted in full.

## Practical Strategy

The project should be a small, readable web application with enough attack surface to audit:

- authentication;
- authorization;
- database queries;
- user input;
- comments or similar stored content;
- file upload;
- sessions/cookies;
- admin-only functionality.

It should be simple enough to understand, modify, break, fix, and explain within the available time.
