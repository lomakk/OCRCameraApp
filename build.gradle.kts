plugins {
    //trick: for the same plugin versions in all sub-modules
    id("com.android.application").version("7.4.2").apply(false)
    id("com.android.library").version("7.4.2").apply(false)
    kotlin("android").version("1.8.0").apply(false)
    kotlin("multiplatform").version("1.8.0").apply(false)

//    alias(tools.plugins.android.library)
//    alias(tools.plugins.android.application)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
