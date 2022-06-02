package com.lollitech.checkstyle

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class StyleCustomProvider : RuleSetProvider {
    override val ruleSetId: String = "custom"

    override fun instance(config: Config): RuleSet = RuleSet(
        ruleSetId, listOf(
            BlankLineRule(config), LollitechClassOrdering(config), LongMethodCheck(config)
        )
    )
}