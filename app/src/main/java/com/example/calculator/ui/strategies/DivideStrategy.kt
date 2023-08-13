package com.example.calculator.ui.strategies

import com.example.calculator.ui.Strategy

class DivideStrategy: Strategy {
    override fun doOperation(leftOperand: String, rightOperand: String): Double {
        return if (rightOperand.isEmpty())
            leftOperand.toDouble() / leftOperand.toDouble()
        else leftOperand.toDouble() / rightOperand.toDouble()
    }
}