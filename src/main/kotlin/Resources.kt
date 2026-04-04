package com

import io.ktor.server.resources.Resources
import io.ktor.server.application.Application
import io.ktor.server.application.install

fun Application.configResource() {
    install(Resources)
}