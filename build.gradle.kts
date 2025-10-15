// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    // Keep plugins false at root
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}
