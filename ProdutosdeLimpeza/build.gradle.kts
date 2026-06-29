plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.plugin) apply false
    alias(libs.plugins.google.services) apply false
    // ADICIONE ESTA LINHA:
    alias(libs.plugins.secrets.gradle.plugin) apply false
}