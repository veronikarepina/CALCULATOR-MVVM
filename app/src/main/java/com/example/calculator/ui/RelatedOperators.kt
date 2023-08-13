package com.example.calculator.ui

class RelatedOperators {                                                                         //класс связанные операторы
    var bufferOperator = EMPTY_STRING                                                            //для запоминания операторов в сложных выражениях
    var lastOperator = EMPTY_STRING

    fun clearOperators(){
        bufferOperator = EMPTY_STRING
        lastOperator = EMPTY_STRING
    }

    fun clearBufferOperator(){
        bufferOperator = EMPTY_STRING
    }

    companion object {
        private const val EMPTY_STRING = ""
    }
}