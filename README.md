# ocpi-lib
Open Charge Point Interface (OCPI) java / kotlin library

## Differences

### ocpi-lib-2.1.1 -> ocpi-lib-2.2.1

Also see:
- OCPI 2.1.1 -> OCPI 2.2 -> OCPI 2.2.1: [official doc](https://github.com/ocpi/ocpi/blob/2.2.1/changelog.asciidoc#changelog_changelog)

What actually changed in the lib:

| Module      | Changements                                                                                                                                                                                                                                                                                                                                                   |
|-------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Common      | - Added hub exceptions<br/>- Added CiString type                                                                                                                                                                                                                                                                                                              |
| Versions    | - Added V_2_2 and V_2_2_1 in VersionNumber enum<br/>- Added role in Endpoint                                                                                                                                                                                                                                                                                  |
| Credentials | - Credentials object has a list of CredentialRoles instead of only business_details, party_id & country_code. Also added CredentialsRoleRepository for the user to specify the roles of the platform they are implementing. In 2.1.1, the user had to pass business_details, party_id, country_code to CredentialsClientService and CredentialsServerService. |
| Locations   | - Too many changements, see [this commit for details](https://github.com/4sh/ocpi-lib/commit/dfbbd8bf2741788582e087a5921b099c07129788)                                                                                                                                                                                                                        |                                                                                                                                                                                                                                                                                                                                                            |

### ocpi-lib-2.1.1 -> ocpi-lib-2.1.1-gireve

| Module      | Changements                                                                                                                                                |
|-------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Common      | nothing changed                                                                                                                                            |
| Versions    | - Removed V_2_0 and V_2_1 in VersionNumber enum                                                                                                            |
| Credentials | - Removed PUT (the user has to first delete() then register() to update (so token A has to be exchanged outside OCPI protocol between delete and register) |
| Locations   | - evse_id is now required (added doc to explain that in Locations object)                                                                                  |