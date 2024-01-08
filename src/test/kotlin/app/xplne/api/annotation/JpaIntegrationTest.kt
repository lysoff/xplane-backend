package app.xplne.api.annotation

import app.xplne.api.config.TestDatabaseConfiguration
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import

/**
 * Includes configuring Spring context and database in Docker.
 */
@DataJpaTest(showSql = false)
@Import(TestDatabaseConfiguration::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Target(AnnotationTarget.CLASS)
annotation class JpaIntegrationTest
