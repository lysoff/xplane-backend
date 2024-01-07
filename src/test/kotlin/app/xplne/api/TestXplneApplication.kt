package app.xplne.api

import app.xplne.api.config.TestDatabaseConfiguration
import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
	fromApplication<XplneApplication>()
			.with(TestDatabaseConfiguration::class)
			.run(*args)
}
