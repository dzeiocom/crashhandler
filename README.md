<p align="center">
  <img alt="Dzeio Charts logo" width="30%" src="sample/src/main/ic_launcher-playstore.png">
</p>

<p align="center">
  <a href="https://discord.gg/d3QeWKBmBD">
	<img src="https://img.shields.io/discord/1143555541004726272?color=%235865F2&label=Discord" alt="Discord Link">
  </a>
  <a href="https://github.com/dzeiocom/crashhandler/stargazers">
  	<img src="https://img.shields.io/github/stars/dzeiocom/crashhandler?style=flat-square" alt="Github stars">
  </a>
  <a href="https://github.com/dzeiocom/crashhandler/actions/workflows/build.yml">
  	<img src="https://img.shields.io/github/actions/workflow/status/dzeiocom/crashhandler/build.yml?style=flat-square" alt="Build passing" />
  </a>
</p>

# Crash Handler

Lightweight & customizable crash android crash handler library

## Install

- Add Jitpack.io to your `settings.gradle` file `maven { url 'https://jitpack.io' }`

Add to you dependencies (check the latest release for the version):
- (Gradle Kotlin DSL) Add `implementation("com.dzeio:crashhandler:1.0.2")` 
- (Gradle Groovy DSL) Add `implementation "com.dzeio:crashhandler:1.0.2"`

## Usage

_note: full featured example in the `sample` app_

Create and add this to your Application.{kt,java}

```kotlin
// create the Crash Handler
CrashHandler.Builder()
    // need the application context to run
    .withContext(this)
    
    // every other items below are optionnal
    // define a custom activity to use
    .withActivity(ErrorActivity::class.java)

    // define the preferenceManager to have the previous crash date in the logs
    .withPrefs(prefs)
    .withPrefsKey("com.dzeio.crashhandler.key")

    // a Prefix to add at the beginning the crash message
    .withPrefix("Prefix")

    // a Suffix to add at the end of the crash message
    .withSuffix("Suffix")
    
    // add a location where the crash logs are also exported (can be recovered as a zip ByteArray by calling {CrashHandler.getInstance().export()})
    .withExportLocation(
        File(this.getExternalFilesDir(null) ?: this.filesDir, "crash-logs")
    )

    // build & start the module
    .build().setup()
```

## Build

- Install Android Studio
- Build the app
- it will be running on your emulator/device
- test it!

## Contributing

See [CONTRIBUTING.md](https://github.com/dzeiocom/crashhandler/blob/master/CONTRIBUTING.md)

TL::DR

- Fork
- Commit your changes
- Pull Request on this Repository

## License

This project is licensed under the MIT License. A copy of the license is available at [LICENSE.md](https://github.com/dzeiocom/crashhandler/blob/master/LICENSE.md)
