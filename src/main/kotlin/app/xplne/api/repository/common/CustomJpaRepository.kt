package app.xplne.api.repository.common

import io.hypersistence.utils.spring.repository.BaseJpaRepository
import org.springframework.data.repository.ListPagingAndSortingRepository
import org.springframework.data.repository.NoRepositoryBean

/**
 * Is using instead of Spring JpaRepository, because some its methods can cause performance problems.
 * It is based on [BaseJpaRepository], implemented by Vlad Mihalcea. Adds pagination.
 *
 * [More about BaseJpaRepository](https://vladmihalcea.com/basejparepository-hypersistence-utils)
 */
@NoRepositoryBean
interface CustomJpaRepository<T, ID>: BaseJpaRepository<T, ID>, ListPagingAndSortingRepository<T, ID>