package app.xplne.api.config

import io.hypersistence.utils.spring.repository.BaseJpaRepositoryImpl
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(
    value = [
        "app.xplne.api.repository",
        "io.hypersistence.utils.spring.repository"],
    repositoryBaseClass = BaseJpaRepositoryImpl::class
)
class JpaConfiguration