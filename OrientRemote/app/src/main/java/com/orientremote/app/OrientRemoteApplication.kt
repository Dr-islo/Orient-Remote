package com.orientremote.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point. Annotated for Hilt so the dependency graph (repository, IR
 * transmitter, preferences manager) is available across all Activities/ViewModels. Intentionally
 * has no other setup — this app runs with zero background services, zero analytics, and zero
 * network clients, matching the offline-forever requirement.
 */
@HiltAndroidApp
class OrientRemoteApplication : Application()
