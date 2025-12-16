# Contributing

First off, thanks for taking the time to contribute!

All types of contributions are encouraged and valued.
Please make sure to read the relevant section before
making your contribution.

> And if you like the project, but just don't have time
> to contribute, that's fine. There are other easy ways
> to support the project and show your appreciation,
> which we would also be very happy about:
> - Tweet about it
> - Refer this project in your project's readme
> - Mention the project at local meetups and tell your
> friends/colleagues

## Code of Conduct

Be kind

## I Have a Question

Before you ask a question, it is best to search for
existing [Issues](/issues) that might help you. In case
you have found a suitable issue and still need clarification,
you can write your question in this issue.

If you then still feel the need to ask a question and need
clarification, we recommend the following:

- Open an [Issue](/issues/new).
- Provide as much context as you can about what you're running
into.

We will then take care of the issue as soon as possible.

## I want to suggest an enhancement

Enhancement suggestions are tracked as [GitHub issues](/issues).

- Open an [Issue](/issues/new).
- Use a **clear and descriptive title** for the issue to
identify the suggestion.
- Provide a **step-by-step description of the suggested enhancement**
in as many details as possible.
- **Describe the current behavior** and **explain which behavior you
expected to see instead** and why. At this point you can also tell
which alternatives do not work for you.

## I found a bug

You must never report security related issues, vulnerabilities
or bugs including sensitive information to the issue tracker,
or elsewhere in public. Instead sensitive bugs must be sent by
email to `gallon.lilian@gmail.com`.

- Open an [Issue](/issues/new). (Since we can't be sure at this
point whether it is a bug or not, we ask you not to talk about a
bug yet and not to label the issue.)
- Explain the behavior you would expect and the actual behavior.
- Please provide as much context as possible and describe the
*reproduction steps* that someone else can follow to recreate the
issue on their own. This usually includes your code. For good bug
reports you should isolate the problem and create a reduced test
case.
- Provide the information you collected in the previous section.

## I Want To Contribute

When contributing to this project, you must agree that you
have authored 100% of the content, that you have the necessary
rights to the content and that the content you contribute may
be provided under the project license.

**Commits:**

Please follow this format:

```
feat|fix|chore|doc(impact) #issueId: small description
```

Examples:
- `feat(credentials) #1: implemented /POST endpoint`
- `fix(versions) #2: /GET endpoint not returning the correct versions`
- `doc(readme) #3: updated code examples with last changes`
- `doc(contributing): init CONTRIBUTING.md`

**Code style:**

Code style is enforced by ktlint. You can run `./gradlew ktlintCheck`
to make sure your code follows our code style. The CI will check this
anyway.
