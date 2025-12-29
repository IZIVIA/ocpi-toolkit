# Ocpi Toolkit

![CI](https://img.shields.io/github/actions/workflow/status/izivia/ocpi-toolkit/ci.yml?style=flat-square) ![Maven Central](https://img.shields.io/maven-central/v/com.izivia/ocpi-2-2-1?style=flat-square&filter=!1.1) ![License](https://img.shields.io/github/license/izivia/ocpi-toolkit?style=flat-square)

**The reference Kotlin library for implementing the OCPI (Open Charge Point Interface) protocol.**

This toolkit provides a complete implementation of the **business logic** and **validation rules** of OCPI, leaving you free to choose your technical stack for transport (HTTP frameworks, databases).

Why use it?
*   **Compliant**: Rigorously implements OCPI 2.2.1 standard. Currently used in production.
*   **Flexible**: Agnostic of HTTP framework (Spring, Ktor, Http4k...) and persistence.
*   **Robust**: Automatic handling of validations and protocol error cases.

---

## Quick Install

Simply add the dependency via Gradle:

```kotlin
dependencies {
    implementation("com.izivia:ocpi-2-2-1:LATEST_VERSION")
    implementation("com.izivia:ocpi-transport:LATEST_VERSION")

    // If you want to use Kotlinx Serialization
    implementation("com.izivia:ocpi-2-2-1-kotlinx-serialization:LATEST_VERSION")
    // If you want to use Jackson
    implementation("com.izivia:ocpi-2-2-1-jackson:LATEST_VERSION")
}
```

> See all available versions on [Maven Central](https://central.sonatype.com/search?namespace=com.izivia&q=ocpi).

---

## Quick Start

Start an OCPI server in 5 minutes.

> **Note**: `TransportServer` is an interface you must implement (or use an existing implementation like in our samples) to bind the toolkit to your preferred HTTP server.

```kotlin
// 1. Instantiate your transport server (fictional example here)
val server = MyTransportServer(port = 8080, baseUrl = "http://localhost:8080")

// 2. Configure the repository for versions (OCPI support)
val versionsRepository = VersionsCacheRepository()

// 3. Mount the "Versions" module (OCPI entry point)
VersionsServer(
    validationService = VersionsValidationService(
        repository = versionsRepository
    )
).registerOn(server)

// 4. Start!
server.start()
```

---

## Core Concepts

`ocpi-toolkit` follows a "**Bring Your Own Infrastructure**" philosophy:

1.  **Toolkit** (What we provide): Controllers, data validation, OCPI logic, DTOs.
2.  **Infrastructure** (What you provide):
    *   `TransportServer` / `TransportClient`: How to send/receive HTTP.
    *   `Repositories`: How to store data (Mongo, SQL, In-Memory...).

---

## Usage Examples

### 1. Credentials Handshake (Registration)

OCPI requires a handshake process where two platforms exchange tokens to start communicating.

#### As a Receiver (Server)

If you want to allow other platforms to register with you (e.g. you are a CPO allowing eMSPs to connect):

```kotlin
// 1. Service handling the logic
val credentialsService = CredentialsServerService(
    partnerRepository = partnerRepository,
    credentialsRoleRepository = object : CredentialsRoleRepository { ... }, // Define your role
    transportClientBuilder = Http4kTransportClientBuilder(),
    serverVersionsUrlProvider = { "https://myserver.com/versions" }
)

// 2. Register the module on the server
CredentialsServer(
    service = credentialsService
).registerOn(server)
```

Now, any partner with a valid `TOKEN_A` (that you gave them out-of-band) can perform the handshake against your server.

#### As a Sender (Client)

If you want to register with another platform (e.g. you are an eMSP connecting to a CPO):

```kotlin
// 1. Setup the service
val credentialsClientService = CredentialsClientService(
    clientVersionsEndpointUrl = "https://myserver.com/versions",
    clientPartnerRepository = partnerRepository,
    clientVersionsRepository = versionsRepository,
    clientCredentialsRoleRepository = object : CredentialsRoleRepository { ... },
    partnerId = "PARTNER_ID", // The ID of the partner you want to connect to (in your local DB)
    transportClientBuilder = Http4kTransportClientBuilder()
)

// 2. Perform the handshake
// This will:
// - Use TOKEN_A (stored in DB) to authenticate
// - Exchange versions
// - Generate TOKEN_B and send it to receiver
// - Receive TOKEN_C and store it
credentialsClientService.register()
```

### 2. Server Side (CPO): Exposing Charging Points

Complete example to expose your charging stations.

```kotlin
// Repositories (implement the interfaces provided by the toolkit)
val partnerRepository = MyPartnerRepository() // Stores partner credentials
val locationsService = LocationsCpoMongoService() // Your logic to retrieve locations

// HTTP Server
val server = Http4kTransportServer(port = 8080, baseUrl = "http://myserver.com")

// Configure Locations module (CPO)
LocationsCpoServer(
    transportServer = server,
    service = LocationsCpoValidationService(
        service = locationsService
    ),
    partnerRepository = partnerRepository
).registerOn(server)

server.start()
```

### 3. Client Side (eMSP): Retrieving Charging Points

Example to connect to a CPO and list their stations.

```kotlin
// Client for the Locations module
val locationsCpoClient = LocationsCpoClient(
    transportClientBuilder = Http4kTransportClientBuilder(),
    serverVersionsEndpointUrl = "https://cpo.com/versions", // CPO entry point
    partnerRepository = MyPartnerRepository()
)

// Calling a business method (auth & transport handled transparently)
val location = locationsCpoClient.getLocation(
    countryCode = "fr",
    partyId = "abc",
    locationId = "loc123"
)
println("Retrieved location: ${location}")
```

### 4. Implementing Transport

To use the toolkit, you must implement the `TransportServer` interface. Here is what it might look like with an imaginary framework:

```kotlin
class MyTransportServer(val port: Int) : TransportServer {
    override fun start() {
        // Start your real web server here
        MyWebServer.listen(port)
    }

    override fun handle(
        method: HttpMethod,
        path: List<PathSegment>,
        queryParams: List<String>,
        filters: List<(request: HttpRequest) -> Unit>,
        callback: suspend (request: HttpRequest) -> HttpResponse
    ) {
        // Bind toolkit routes to your framework
        MyWebServer.route(method, path) { req ->
            // Convert request and call toolkit callback
            val toolkitReq = HttpRequest(...)
            val toolkitRes = callback(toolkitReq)
            return toolkitRes.toFrameworkResponse()
        }
    }
    // ... other methods
}
```

> Complete examples with **Http4k** are available in the folder `ocpi-toolkit-2.2.1/src/test/kotlin/com/izivia/ocpi/toolkit/samples`.

---

## Contributing

Contributions are welcome!
Check out our [Contribution Guide](CONTRIBUTING.md) to get started.

## License

This project is licensed under the [MIT](LICENSE) license.
