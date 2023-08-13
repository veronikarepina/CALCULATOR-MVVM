package com.example.calculator.ui

class Calculator {
    private var strategy: Strategy? = null

    fun setStrategy(strategy: Strategy){
        this.strategy = strategy
    }

    fun executeOperation(leftOperand: String, rightOperand: String): Double{
        if (strategy == null)
            return 0.0
        return strategy!!.doOperation(leftOperand, rightOperand)
    }
}