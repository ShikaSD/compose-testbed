# Compose browser demos

This is a demo of Compose running in the browser using Kotlin/JS and original compiler.
The API here is not representative of anything final (actually, almost non-existent), so feel free to play with it and come up with anything on your own.
Compiler plugin is **NOT QUITE STABLE** yet, if you encounter any bugs/crashes, please report :).

**NOTE:** this is not an "official" Compose/JS implementation from JB/Google, but just an example to play/experiment with.

## Where to look?

- [`.`](./src/main/kotlin) - Playground
- [`macTest`](./macTest/src/main/kotlin/) - Mac OS playground
- [`prelude`](./prelude/src/main/kotlin/compose/web) - Some basic HTML definitions
- [`libs`](./libs/) - Prebuilt artifacts of runtime and compiler plugin

Inside of prelude, see [`JsApplier`](./prelude/src/main/kotlin/compose/web/internal/JsApplier.kt) for an example of how DOM elements are created.

## Where's the prebuilts coming from?

Prebuilt artifacts are generated from CRs in aosp repo (
[one](https://android-review.googlesource.com/c/platform/frameworks/support/+/1535138), 
[two](https://android-review.googlesource.com/c/platform/frameworks/support/+/1535139)
). I will try to keep them in sync while it makes sense.

Currently these artifacts are not published anywhere, but you can use [this template](https://github.com/ShikaSD/compose-browser-external-template) to include it in other projects.

## See also

- [Compose/JS from JB hackathon](https://github.com/JetBrains/compose-for-web-demos)

