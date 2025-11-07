// Top of your project-level build.gradle.kts
plugins {
    id("com.android.application") version "8.5.0" apply false
    id("com.android.library") version "8.5.0" apply false
    kotlin("android") version "1.9.25" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
}

// Clean task if not already there
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}