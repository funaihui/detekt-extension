package com.lollitech.checkstyle

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtNamedFunction

class LongMethodCheck(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName, Severity.Style, "检测方法是否超过规定的行数", Debt.FIVE_MINS
    )
    private val threshold: Int by config(defaultValue = 60)

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        val bodyExpression = function.bodyExpression
        val text = bodyExpression?.text
        if (!text.isNullOrEmpty()) {
            val lineCount = text.count { it == '\n' }

            if (lineCount > threshold) {
                report(
                    ThresholdedCodeSmell(
                        issue,
                        entity = Entity.from(function),
                        metric = Metric(type = "SIZE", value = lineCount, threshold = threshold),
                        message = "${function.name} 方法太长了，超过了我们约定的 $threshold 行",
                        references = emptyList()
                    )
                )
            }
        }
    }

}