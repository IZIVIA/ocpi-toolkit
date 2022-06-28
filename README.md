# Ocpi Toolkit

[![OCPI CI](https://github.com/IZIVIA/ocpi-toolkit/actions/workflows/ci.yml/badge.svg)](https://github.com/IZIVIA/ocpi-toolkit/actions/workflows/ci.yml)

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

// Once that all the modules are defined, you need to start the server
server.start()
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

// Once that all the modules are defined, you need to start the server
server.start()
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

### Client

Examples:
- [Http4kTransportClient](ocpi-toolkit-2.1.1/src/test/kotlin/com/izivia/ocpi/toolkit/samples/common/Http4kTransportClient.kt): `TransportClient` implementation example
- [Http4kTransportClientBuilder](ocpi-toolkit-2.1.1/src/test/kotlin/com/izivia/ocpi/toolkit/samples/common/Http4kTransportClientBuilder.kt): `TransportClientBuilder` implementation example

> **Note:** Since you need to register to communicate with a server (CPO or eMSP), to use the client, you must have a
> server defined with version & versionDetails modules. During registration, the receiver will make requests to these
> endpoints to retrieve the latest available version between the two servers. Note that if you strictly follow the OCPI
> protocol, you must also have a credentials module set. We don't enforce that in the lib.

**Common (registration)**

```kotlin
// sender: the one that wants to register
// receiver: the one that receives the registration request

// PlatformMongoRepository is an implementation of PlatformRepository using mongo
// It will be used to store information about platforms with whom the client is communicating:
// A platform has: Tokens (A, B, C), Endpoints, Versions
// You can see an  example in the list above 
val senderPlatformRepository = PlatformMongoRepository(
    collection = mongoDatabase.getCollection<Location>(config.platformCollection)
)

// VersionsCacheRepository is an implementation of VersionsRepository
// It defines which OCPI version the client supports, and the endpoints associated with it
// You can see an  example in the list above 
val senderVersionsRepository = VersionsCacheRepository()

// Will be sent during registration for the receiver to use to request what versions the sender supports
val senderVersionsEndpoint = "https://sender.com/versions"

// Will be the first endpoint used by the sender to perform the registration process:
// - First it retrieves the available versions, it picks the latest
// - Then it retrieves the details of that version
// - Finally it requests a registration on the credentials module of the receiver
val receiverVersionsEndpoint = "https://receiver.com/versions"

// Http4kTransportClientBuilder is used to build a transport client during runtime to make all the
// registration process for you. You can do everything manually, but it's recommended to use CredentialsClientService.

val credentialsClientService = CredentialsClientService(
    clientVersionsEndpointUrl = senderVersionsEndpint,
    clientPlatformRepository = senderPlatformRepository,
    clientVersionsRepository = senderVersionsRepository,
    clientBusinessDetails = BusinessDetails(name = "Sender", website = null, logo = null),
    clientPartyId = "ABC",
    clientCountryCode = "FR",
    serverVersionsEndpointUrl = receiverVersionsEndpoint,
    transportClientBuilder = Http4kTransportClientBuilder()
)

credentialsClientService.register()
```

**Communicating with an eMSP**

```kotlin
// Now that the CPO is registered with the eMSP, all the information (tokens & endpoints) is stored in
// platformRepository. It is now possible to access the locations module of the eMSP using LocationsCpoClient.

val locationsCpoClient = LocationsCpoClient(
    transportClientBuilder = Http4kTransportClientBuilder(),
    serverVersionsEndpointUrl = "https://emsp.com/versions", // Used as ID for the platform (to retrieve information)
    platformRepository = platformRepository
)

// Example on how to get a specific location
locationsCpoClient.getLocation(countryCode = "fr", partyId = "abc", locationId = "location1")
```

**Communicating with a CPO**

```kotlin
// Now that the eMSP is registered with the CPO, all the information (tokens & endpoints) is stored in
// platformRepository. It is now possible to access the locations module of the CPO using LocationsEmspClient.

val locationsEmspClient = LocationsEmspClient(
    transportClientBuilder = Http4kTransportClientBuilder(),
    serverVersionsEndpointUrl = "https://cpo.com/versions",
    platformRepository = platformRepository
)

// Example on how to get a specific location
locationsEmspClient.getLocation(locationId = "location1")
```

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
