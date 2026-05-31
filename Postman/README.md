# Postman Collection

Import `Fluffy-Plushies-Webshop.postman_collection.json` into Postman.

Run the `Auth` folder before any other folder. It logs in the seeded users and populates the token variables used by the rest of the collection.

Before running requests that log in as seeded users, set these collection variables manually in Postman:

- `adminPassword`
- `merchantPassword`
- `userPassword`

The committed collection uses placeholder values for these variables because they are credentials for accounts that already exist when the application starts. Keeping real seeded-account passwords out of source control reduces the risk of exposing usable credentials.

Passwords in registration and password-change requests are intentionally included because they are used for throwaway users created during the collection flow. They are not passwords for pre-existing seeded accounts.
