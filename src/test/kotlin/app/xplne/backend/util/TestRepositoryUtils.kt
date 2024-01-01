package app.xplne.backend.util

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import reactor.core.publisher.Mono

/**
 * Inserts all entities sequentially in the same Mono chain
 * @return Mono for the last insert.
 */
fun <T> R2dbcEntityTemplate.insertAll(entities:List<T & Any>): Mono<T> {
    var insertedMono: Mono<T> = Mono.empty()
    entities.forEach {
        insertedMono = insertedMono.then(this.insert(it))
    }
    return insertedMono
}