package app.xplne.api.repository.common

import io.hypersistence.utils.spring.repository.BaseJpaRepository

fun <T, ID> BaseJpaRepository<T, ID>.findByIdOrNull(id: ID): T? =
    findById(id!!).orElse(null)