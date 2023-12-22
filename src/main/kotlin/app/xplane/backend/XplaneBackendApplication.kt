package app.xplane.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class XplaneBackendApplication

fun main(args: Array<String>) {
	runApplication<XplaneBackendApplication>(*args)
}
