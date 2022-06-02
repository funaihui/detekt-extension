package com.lollitech.checkstyle.codesmell

import io.gitlab.arturbosch.detekt.api.*

open class ErrorCodeSmell(
    issue: Issue,
    entity: Entity,
    val metric: Metric,
    message: String,
    references: List<Entity> = emptyList()
) : CodeSmell(
    issue,
    entity,
    message,
    metrics = listOf(metric),
    references = references
) {

    val value: Int
        get() = metric.value
    override val severity: SeverityLevel = SeverityLevel.ERROR

    override fun compact(): String = "$id - $metric - ${entity.compact()}"

    override fun messageOrDescription(): String = message.ifEmpty { issue.description }
}