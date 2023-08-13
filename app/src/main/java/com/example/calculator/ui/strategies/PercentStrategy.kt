package com.example.calculator.ui.strategies

import com.example.calculator.ui.Strategy

class PercentStrategy: Strategy {
    override fun doOperation(leftOperand: String, rightOperand: String): Double {
        return if (rightOperand.isEmpty())
            leftOperand.toDouble() / 100
        else
            leftOperand.toDouble() / 100 * rightOperand.toDouble()
    }
}