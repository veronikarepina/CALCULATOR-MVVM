package com.example.calculator.ui

interface Strategy {
    fun doOperation(leftOperand: String, rightOperand: String): Double
}