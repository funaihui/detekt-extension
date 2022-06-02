package com.lollitech.checkstyle

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.psi.*

class LollitechClassOrdering(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName, Severity.Style, "类的内容应该按照文档约定的顺序", Debt.FIVE_MINS
    )

    override fun visitClassBody(classBody: KtClassBody) {
        super.visitClassBody(classBody)

        var currentSection = Section(0)
        for (ktDeclaration in classBody.declarations) {
            val section = ktDeclaration.toSection() ?: continue
            when {
                section < currentSection -> {
                    val message = "${ktDeclaration.toDescription()} 应该在 ${currentSection.toDescription()} 之前."
                    report(
                        CodeSmell(
                            issue = issue,
                            entity = Entity.from(ktDeclaration),
                            message = message,
                            references = listOf(Entity.from(classBody))
                        )
                    )
                }
                section > currentSection -> currentSection = section
            }
        }
    }
}

private fun KtDeclaration.toDescription(): String = when {
    this is KtObjectDeclaration && isCompanion() -> "companion object"
    this is KtProperty -> "property `$name`"
    this is KtClassInitializer -> "initializer blocks"
    this is KtSecondaryConstructor -> "secondary constructor"
    this is KtNamedFunction -> "method `$name()`"
    this is KtClass -> "内部类 `$name()`"
    else -> ""
}

@Suppress("MagicNumber")
private fun KtDeclaration.toSection(): Section? = when {
    this is KtObjectDeclaration && isCompanion() -> Section(0)
    this is KtProperty -> Section(1)
    this is KtClassInitializer -> Section(2)
    this is KtSecondaryConstructor -> Section(2)
    this is KtNamedFunction && name == "initArgument" -> Section(3)
    this is KtNamedFunction && name == "initView" -> Section(4)
    this is KtNamedFunction && name == "showTitleBar" -> Section(5)
    this is KtNamedFunction && name == "initListener" -> Section(6)
    this is KtNamedFunction && name == "initAdapter" -> Section(7)
    this is KtNamedFunction && name == "registerObserver" -> Section(8)
    this is KtNamedFunction && name == "requestNetData" -> Section(9)
    this is KtNamedFunction && name == "bindData4NoNet" -> Section(10)
    this is KtNamedFunction && name == "onStart" -> Section(11)
    this is KtNamedFunction && name == "onResume" -> Section(12)
    this is KtNamedFunction && name == "onStop" -> Section(13)
    this is KtNamedFunction && name == "onDestroy" -> Section(14)
    this is KtNamedFunction && isOverride() -> Section(15)
    this is KtNamedFunction -> Section(16)
    this is KtClass -> Section(17)
    else -> null // For declarations not relevant for ordering, such as nested classes.
}

@Suppress("MagicNumber")
private class Section(val priority: Int) : Comparable<Section> {

    init {
        require(priority in 0..17)
    }

    fun toDescription(): String = when (priority) {
        0 -> "静态代码块"
        1 -> "属性和init方法"
        2 -> "次构造方法"
        3 -> "initArgument"
        4 -> "initView方法"
        5 -> "showTitleBar"
        6 -> "initListener"
        7 -> "initAdapter"
        8 -> "registerObserver"
        9 -> "requestNetData"
        10 -> "bindData4NoNet"
        11 -> "onStart"
        12 -> "onResume"
        13 -> "onStop"
        14 -> "onDestroy"
        15 -> "重写的方法"
        16 -> "普通方法"
        17 -> "内部类"
        else -> ""
    }

    override fun compareTo(other: Section): Int = priority.compareTo(other.priority)
}
