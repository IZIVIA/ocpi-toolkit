# Ocpi Toolkit

![CI](https://img.shields.io/github/actions/workflow/status/izivia/ocpi-toolkit/ci.yml?style=for-the-badge) ![Latest Release](https://img.shields.io/github/v/tag/izivia/ocpi-toolkit?style=for-the-badge&label=latest%20version)

Open Charge Point Interface (OCPI) kotlin library.

> ⚠️ Currently in active development. Not ready for production yet. See https://github.com/IZIVIA/ocpi-toolkit/issues/33 for details.

## Setup

In your `build.gradle.kts`, add:

```kts
dependencies {
    implementation("com.izivia:ocpi-2-2-1:0.0.15")
    implementation("com.izivia:ocpi-transport:0.0.15")
}
```

To see all available artifacts, go to: https://central.sonatype.com/search?namespace=com.izivia&q=ocpi

## Usage

**You have the following updated code samples in [ocpi-toolkit-2.2.1/src/test](https://github.com/IZIVIA/ocpi-toolkit/tree/main/ocpi-toolkit-2.2.1/src/test/kotlin/com/izivia/ocpi/toolkit/samples).**

### Server (CPO or eMSP)

Examples:
- [Http4kTransportServer](ocpi-toolkit-2.1.1/src/test/kotlin/com/izivia/ocpi/toolkit/samples/common/Http4kTransportServer.kt): `TransportServer` implementation example
- [PartnerMongoRepository](ocpi-toolkit-2.1.1/src/test/kotlin/com/izivia/ocpi/toolkit/tests/integration/mock/PartnerMongoRepository.kt): `PartnerRepository` implementation example
- [VersionsCacheRepository](ocpi-toolkit-2.1.1/src/test/kotlin/com/izivia/ocpi/toolkit/samples/common/VersionsCacheRepository.kt): `VersionsRepository` implementation example
- [VersionDetailsCacheRepository](ocpi-toolkit-2.1.1/src/test/kotlin/com/izivia/ocpi/toolkit/samples/common/VersionDetailsCacheRepository.kt): `VersionsDetailsRepository` implementation example

**Common code (CPO / eMSP):**

```kotlin
// PartnerMongoRepository is an implementation of PartnerRepository using mongo
// It will be used to store information about partners with whom the server is communicating:
// A partner has: Tokens (A, serverToken, clientToken), Endpoints, Versions, Roles
// You can see an  example in the list above
val partnerRepository = PartnerMongoRepository(
    collection = mongoDatabase.getCollection<Location>(config.partnerCollection)
)

// Http4kTransportServer is an implementation of TransportServer using htt4k. You have to code your own implementation.
// It defines the HTTP server, and how to handle requests.
// You can see an  example in the list above
val server = Http4kTransportServer(
    baseUrl = config.baseUrl, // Example: http://localhost:8080, only used for pagination
    port = config.port, // Example: 8080, used to know on which port the server will run
    secureFilter = senderPartnerRepository::checkToken // The filter called on secured routes
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
    validationService = VersionsValidationService(
        repository = versionsRepository
    )
).registerOn(server)

// Required: defines /2.1.1, /2.2.1, whatever version endpoint
VersionDetailsServer(
    validationService = VersionDetailsValidationService(
        repository = versionDetailsRepository
    )
).registerOn(server)

// Required: defines /{version}/credentials endpoint for any client to register following OCPI protocol
CredentialsServer(
    service = CredentialsServerService(
        partnerRepository = partnerRepository,
        credentialsRoleRepository = object : CredentialsRoleRepository { ... },
        transportClientBuilder = Http4kTransportClientBuilder(),
        serverVersionsUrl = { versionsUrl }
    )
).registerOn(server)
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
    partnerRepository = partnerRepository
).registerOn(server)

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
    service = LocationsEmspValidationService(
        service = LocationsApiClient(
            channel = chargingInfrastructureChannel
        )
    )
).registerOn(server)

// Once that all the modules are defined, you need to start the server
server.start()
```

**Optional arguments**

It is possible to change the default path of a module using `basePath` argument:

```kotlin
LocationsEmspServer(
    service = service,
    basePath = "/2.1.1/cpo/locations"
).registerOn(server)
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

// PartnerMongoRepository is an implementation of PartnerRepository using mongo
// It will be used to store information about partners with whom the client is communicating:
// A partner has: Tokens (A, B, C), Endpoints, Versions
// You can see an  example in the list above
val senderPartnerRepository = PartnerMongoRepository(
    collection = mongoDatabase.getCollection<Location>(config.partnerCollection)
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
    partnerRepository = senderPartnerRepository,
    clientVersionsRepository = senderVersionsRepository,
    credentialsRoleRepository = object : CredentialsRoleRepository { ... },
    serverVersionsUrlProvider = { receiverVersionsEndpoint },
    transportClientBuilder = Http4kTransportClientBuilder()
)

credentialsClientService.register()
```

**Communicating with an eMSP**

```kotlin
// Now that the CPO is registered with the eMSP, all the information (tokens & endpoints) is stored in
// partnerRepository. It is now possible to access the locations module of the eMSP using LocationsCpoClient.

val locationsCpoClient = LocationsCpoClient(
    transportClientBuilder = Http4kTransportClientBuilder(),
    serverVersionsEndpointUrl = "https://emsp.com/versions", // Used as ID for the partner (to retrieve information)
    partnerRepository = partnerRepository
)

// Example on how to get a specific location
locationsCpoClient.getLocation(countryCode = "fr", partyId = "abc", locationId = "location1")
```

**Communicating with a CPO**

```kotlin
// Now that the eMSP is registered with the CPO, all the information (tokens & endpoints) is stored in
// partnerRepository. It is now possible to access the locations module of the CPO using LocationsEmspClient.

val locationsEmspClient = LocationsEmspClient(
    transportClientBuilder = Http4kTransportClientBuilder(),
    serverVersionsEndpointUrl = "https://cpo.com/versions",
    partnerRepository = partnerRepository
)

// Example on how to get a specific location
locationsEmspClient.getLocation(locationId = "location1")
```
