package app.xplne.backend.annotation

import app.xplne.backend.config.TestDatabaseConfiguration
import com.chikli.spring.rxtx.RxTestTransaction
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import reactor.core.publisher.Mono

/**
 * Common annotation for reactive repository tests. Includes configuring Spring context and database in Docker.
 * Applies the extension for [Mono] with .testWithTx() method allowing wrapping reactive DB interactions in
 * transaction to roll it back at the end of a test ([RxTestTransaction]).
 */
@DataR2dbcTest
@Import(TestDatabaseConfiguration::class, RxTestTransaction::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Target(AnnotationTarget.CLASS)
annotation class RepositoryIntegrationTest
