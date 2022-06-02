package com.lollitech.checkstyle

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.api.config
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

// TODO: 2022/5/18 待完善
class BlankLineRule(config: Config) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName, Severity.Maintainability, "遵守空行规格可以让代码看起来更简洁", Debt(hours = 1)
    )

    private val threshold: Int by config(defaultValue = 10)

    private var amount: Int = 0

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        if (amount > threshold) {
            report(
                ThresholdedCodeSmell(
                    issue,
                    entity = Entity.from(file),
                    metric = Metric(type = "SIZE", value = amount, threshold = threshold),
                    message = "The file ${file.name} has $amount function declarations. " + "Threshold is specified with $threshold.",
                    references = emptyList()
                )
            )
        }
        amount = 0
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        amount++
    }
}
