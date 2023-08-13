package com.example.calculator.ui.strategies

import com.example.calculator.ui.Strategy

class AddStrategy: Strategy {
    override fun doOperation(leftOperand: String, rightOperand: String): Double {
        return if (rightOperand.isEmpty())
            leftOperand.toDouble() * 2
        else
            leftOperand.toDouble() + rightOperand.toDouble()
    }
}