package app.xplne.api.config

import io.swagger.v3.oas.models.OpenAPI
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class OpenApiConfiguration {
    @Bean
    fun groupedPublicOpenApiV1(): GroupedOpenApi {
        return GroupedOpenApi
            .builder()
            .group("API-v1")
            .pathsToMatch("/v1/**")
            .addOpenApiCustomizer{ openApi ->
                openApi.info.version = "v1"
                openApi.info.title = "Xplne API"
                refineVersionInUrl(openApi, "/v1")
            }
            .build()
    }

    private fun refineVersionInUrl(openApi: OpenAPI, versionPath: String) {
        addVersionToServerBasePath(openApi, versionPath)
        removeVersionFromEndpoints(openApi, versionPath)
    }

    private fun addVersionToServerBasePath(openApi: OpenAPI, versionPath: String) {
        openApi.servers.forEach { it.url += versionPath }
    }

    private fun removeVersionFromEndpoints(openApi: OpenAPI, versionPath: String) {
        val paths: List<String> = openApi.paths.keys.toList()
        paths.forEach { pathWithVersion ->
            val newPath = pathWithVersion.replace(versionPath, "")
            openApi.paths[newPath] = openApi.paths[pathWithVersion]
            openApi.paths.remove(pathWithVersion)
        }
    }
}