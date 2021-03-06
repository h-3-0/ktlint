package com.github.shyiko.ktlint.ruleset.standard

import com.github.shyiko.ktlint.core.Rule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.lang.FileASTNode
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes

/**
 * @author yokotaso <yokotaso.t@gmail.com>
 */
class UseWhiteSpaceInsteadOfTabRule : Rule("use-whitespace-instead-of-tab-rule") {
    private var indentSize = -1

    override fun visit(node: ASTNode, autoCorrect: Boolean, emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit) {
        if (node.elementType == KtStubElementTypes.FILE) {
            val ec = EditorConfig.from(node as FileASTNode)
            indentSize = ec.indentSize
            return
        }

        if (node.psi is PsiWhiteSpace &&
            (node.psi as PsiWhiteSpace).textContains('\t')) {
            emit(node.startOffset, getErrorMessage(indentSize), true)

            if (autoCorrect) {
                // indentSize should be set.
                assert(indentSize > 0, { "illegal indentSize." })
                val whitespace = node.text.replace("\t", " ".repeat(indentSize))
                (node as LeafPsiElement).rawReplaceWithText(whitespace)
            }
        }
    }

    fun getErrorMessage(indentSize: Int): String {
        val numOfSpace = if (indentSize > 0) indentSize.toString() else "some"
        return "Use $numOfSpace spaces for indentation. Do not use tabs."
    }
}
