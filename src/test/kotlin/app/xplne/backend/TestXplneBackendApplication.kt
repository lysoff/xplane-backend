package app.xplne.backend

import app.xplne.backend.config.TestDatabaseConfiguration
import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
	fromApplication<XplneBackendApplication>()
			.with(TestDatabaseConfiguration::class)
			.run(*args)
}
