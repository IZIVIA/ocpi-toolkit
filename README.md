# ocpi-lib
Open Charge Point Interface (OCPI) java / kotlin library

## Usage

### Server (CPO or eMSP)

Examples:
- [Http4kTransportServer](ocpi-toolkit-2.1.1/src/test/kotlin/com/izivia/ocpi/toolkit/samples/common/Http4kTransportServer.kt): `TransportServer` implementation example
- [PlatformMongoRepository](ocpi-toolkit-2.1.1/src/test/kotlin/com/izivia/ocpi/toolkit/tests/integration/mock/PlatformMongoRepository.kt): `PlatformRepository` implementation example
- [VersionsCacheRepository](ocpi-toolkit-2.1.1/src/test/kotlin/com/izivia/ocpi/toolkit/samples/common/VersionsCacheRepository.kt): `VersionsRepository` implementation example
- [VersionDetailsCacheRepository](ocpi-toolkit-2.1.1/src/test/kotlin/com/izivia/ocpi/toolkit/samples/common/VersionDetailsCacheRepository.kt): `VersionsDetailsRepository` implementation example

**Common code (CPO / eMSP):**

```kotlin
// Http4kTransportServer is an implementation of TransportServer using htt4k. You have to code your own implementation.
// It defines the HTTP server, and how to handle requests.
// You can see an  example in the list above 
val server = Http4kTransportServer(
    baseUrl = config.baseUrl, // Example: http://localhost:8080, only used for pagination
    port = config.port // Example: 8080, used to know on which port the server will run
)

// PlatformMongoRepository is an implementation of PlatformRepository using mongo
// It will be used to store information about platforms with whom the server is communicating:
// A platform has: Tokens (A, B, C), Endpoints, Versions
// You can see an  example in the list above 
val platformRepository = PlatformMongoRepository(
    collection = mongoDatabase.getCollection<Location>(config.platformCollection)
)

// VersionsCacheRepository is an implementation of VersionsRepository
// It defines which OCPI version the server support, and the endpoints associated with it
// You can see an  example in the list above 
val versionsRepository = VersionsCacheRepository()

// VersionDetailsCacheRepository is an implementation of VersionDetailRepository
// It defines the available modules (and their endpoint) for the given version
// You can see an  example in the list above
val versionDetailsRepository = VersionDetailsCacheRepository()

// Required: defines /versions endpoint
VersionsServer(
    transportServer = server,
    platformRepository = platformRepository,
    validationService = VersionsValidationService(
        repository = versionsRepository
    )
)

// Required: defines /2.1.1, /2.2.1, whatever version endpoint
VersionDetailsServer(
    transportServer = server,
    platformRepository = platformRepository,
    validationService = VersionDetailsValidationService(
        repository = versionDetailsRepository
    )
)

// Required: defines /{version}/credentials endpoint for any client to register following OCPI protocol
CredentialsServer(
    transportServer = server,
    service = CredentialsServerService(
        platformRepository = platformRepository,
        serverBusinessDetails = cpoBusinessDetails,
        serverPartyId = cpoPartyId,
        serverCountryCode = cpoCountryCode,
        transportClientBuilder = Http4kTransportClientBuilder(),
        serverVersionsUrl = versionsUrl
    )
)
```

**CPO code:**

```kotlin
// LocationsCpoMongoService is an implementation of LocationsCpoService
// Used to know how to retrieve locations
val locationsService = LocationsCpoMongoService()

// Defines /{version}/locations endpoint for any registered client to retrieve locations
LocationsCpoServer(
    transportServer = server,
    service = LocationsCpoValidationService(
        service = locationsService
    ),
    platformRepository = platformRepository
)
```

**eMSP code:**

```kotlin
// LocationsEmspMongoService is an implementation of LocationsEmspService
// Used to know how to read / create / update / delete locations
val locationsService = LocationsEmspMongoService()

// Defines /{version}/locations endpoint for any registered client to retrieve locations (and also create / update /
// delete)
LocationsEmspServer(
    transportServer = server,
    service = LocationsEmspValidationService(
        service = LocationsApiClient(
            channel = chargingInfrastructureChannel
        )
    ),
    platformRepository = platformRepository
)
```

**Optional arguments**

It is possible to change the default path of a module using `basePath` argument:

```kotlin
LocationsEmspServer(
    transportServer = server,
    service = service,
    platformRepository = platformRepository,
    basePath = "/2.1.1/cpo/locations"
)
```

Make sure that `VersionDetailsRepository` points to the right endpoint (in that case `/2.1.1/cpo/locations`)
for the `locations` module.

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