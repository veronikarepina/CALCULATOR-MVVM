package com.example.calculator.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.calculator.R
import com.example.calculator.ui.strategies.*

class CalcViewModel: ViewModel(){
    val outputResult = MutableLiveData<String>()
    val textForDivisionButton = MutableLiveData<Boolean>()
    val operationButtonSwitch = MutableLiveData<Boolean>()
    val receiverStringError = MutableLiveData<Int>()

    var keeperStringError = EMPTY_STRING
    var operationButtonValue = EMPTY_STRING

    private val actions = listOf(
        ADDITION_OPERATION, SUBTRACTION_OPERATION,
        MULTIPLICATION_OPERATION, DIVISION_OPERATION, EQUALS_OPERATION
    )
    private var lastPressedButton = EMPTY_STRING
    private var bufferOperand = EMPTY_STRING

    private val leftOperand = Operand()
    private val rightOperand = Operand()
    private val activeOperand = Operand()
    private val relatedOperators = RelatedOperators()
    private val calculator = Calculator()

    companion object {
        private const val LINK_TO_STRING_ERROR = R.string.string_error
        private const val EMPTY_STRING = ""
        private const val STRING_VALUE_ZERO = "0"
        private const val ADDITION_OPERATION = "+"
        private const val SUBTRACTION_OPERATION = "-"
        private const val MULTIPLICATION_OPERATION = "*"
        private const val DIVISION_OPERATION = "/"
        private const val PERCENT_OPERATION = "%"
        private const val EQUALS_OPERATION = "="
    }

    init {
        operationButtonSwitch.value = false

        outputResult.value = STRING_VALUE_ZERO
    }

    fun changeSign(){                                                                            //функция меняет знак у текущего числа
        outputResult.value = activeOperand.changeSign()
    }

    fun addDigit(buttonDigit: String){                                                           //функция добавляет к текущему числу нажатую цифру
        disableOfPressedActionButton()                                                           //вызов функции отмены выделения нажатого оператора
        lastPressedButton = buttonDigit
        textForDivisionButton.value = false

        outputResult.value = activeOperand.addDigit(buttonDigit)
    }

    fun addPoint(point: String){                                                                 //функция добавления дробной точки к текущему операнду
        lastPressedButton = point

        outputResult.value = activeOperand.addPoint()
    }

    fun deleteLastNumber(){                                                                      //функция удаления последней цифры из текущего операнда
        if (activeOperand.operandValue.isNotEmpty())
            outputResult.value = activeOperand.deleteLastNumber()
    }

    fun clearAllFields(){                                                                        //функция очищения всех рабочих полей и объектов
        leftOperand.clearDigit()
        rightOperand.clearDigit()
        activeOperand.clearDigit()
        relatedOperators.clearOperators()

        textForDivisionButton.value = true
        operationButtonSwitch.value = false

        lastPressedButton = EMPTY_STRING
        bufferOperand = EMPTY_STRING

        outputResult.value = STRING_VALUE_ZERO
    }

    fun action(operator: String){                                                                //функция нажатия кнопки действия +,-,/,*
        if (lastPressedButton !in actions)                                                       //если последняя нажатая кнопка не является оператором
            initOperands()                                                                       //вызывается функция инициализации
        else
            disableOfPressedActionButton()                                                       //иначе отмена выделения предыдущего оператора

        activatePressedActionButton(operator)                                                    //выделение кнопки текущего оператора во вью слое

        relatedOperators.lastOperator = operator                                                 //запоминание оператора для дальнейших вычислений
        lastPressedButton = operator                                                             //запоминание последней нажатой кнопки

        activeOperand.clearDigit()                                                               //очищение текущего буферного операнда
    }

    fun actionPercent(){                                                                         //функция вычисления процента
        if (activeOperand.operandValue.isNotEmpty())                                             //если введено какое-либо число
            handlePercent()                                                                      //вызываем функцию обработки процента
    }

    fun equal(){                                                                                 //функция обработки нажатия =
        if (leftOperand.operandValue.isNotEmpty())                                               //если левый операнд не пуст
            handleEquals()                                                                       //вызываем функцию обработки равно

        disableOfPressedActionButton()

        lastPressedButton = EQUALS_OPERATION
    }

    private fun initOperands(){                                                                  //функция инициализации операндов
        if (leftOperand.operandValue.isEmpty())
            initLeftOperand()                                                                    //инициализируем первый операнд, если он пуст
        else
            initRightOperand()                                                                   //иначе инициализируем второй операнд
    }

    private fun initLeftOperand(){                                                               //инициализация левого(первого) операнда
        leftOperand.operandValue = activeOperand.operandValue
    }

    private fun initRightOperand(){                                                              //инициализация правого(второго) операнда
        rightOperand.operandValue = activeOperand.operandValue                                   //в правый операнд записывается текущее число
        calculateOperationResult()                                                               //вызывается функция подсчета
    }

    private fun calculateOperationResult(){                                                      //функция расчета результата операции
        val resultOfOperation = chooseStrategy()                                                 //выбор стратегии в соответствии с оператором
        if (resultOfOperation.isInfinite()){                                                     //если результат бесконечность
            clearAllFields()                                                                     //очищаются все поля
            receiverStringError.value = LINK_TO_STRING_ERROR                                     //и во вью отправляется ссылка на строку "Ошибка"

            outputResult.value = keeperStringError                                               //на экран выводится ошибка
        }
        else {
            leftOperand.operandValue = removeExtraPrecision(resultOfOperation)                   //иначе в левый операнд запысывается отформатированный результат

            outputResult.value = leftOperand.convertToOutput()                                   //и выводится на экран
        }
    }

    private fun chooseStrategy(): Double{                                                        //функция выбора стратегии
        when (relatedOperators.lastOperator){                                                    //в зависимости от нажатого оператора
            ADDITION_OPERATION -> calculator.setStrategy(AddStrategy())
            SUBTRACTION_OPERATION -> calculator.setStrategy(SubtractStrategy())
            MULTIPLICATION_OPERATION -> calculator.setStrategy(MultiplyStrategy())
            DIVISION_OPERATION -> calculator.setStrategy(DivideStrategy())
            PERCENT_OPERATION -> calculator.setStrategy(PercentStrategy())
        }

        return calculator.executeOperation(leftOperand.operandValue, rightOperand.operandValue)  //выполнение операции
    }

    private fun handlePercent(){
        if (leftOperand.operandValue.isEmpty())                                                  //если левый операнд был пуст
            calculatePercentForOneOperand()                                                      //вызывается функция подсчета процента для одного операнда
        else
            calculatePercentForExpression()                                                      //иначе - функция подсчета процента для выражения (напр. 100 - 5%)

        outputResult.value = activeOperand.convertToOutput()                                     //на экран выводится результат текущего операнда
    }

    private fun calculatePercentForOneOperand(){                                                 //функция вычисления процента для одного операнда
        initLeftOperand()                                                                        //инициализируется левый операнд
        relatedOperators.lastOperator = PERCENT_OPERATION                                        //запоминается последний оператор - процент

        activeOperand.operandValue = removeExtraPrecision(chooseStrategy())                      //вызывается стратегия и результат записывается в текущий операнд
        leftOperand.clearDigit()                                                                 //левый операнд очищается
    }

    private fun calculatePercentForExpression(){                                                 //функция вычисления процента для выражения (напр. 100 - 5%)
        relatedOperators.bufferOperator = relatedOperators.lastOperator                          //последний оператор запоминается в буферном операторе
        relatedOperators.lastOperator = PERCENT_OPERATION                                        //в последний оператор записывается процент

        rightOperand.operandValue = activeOperand.operandValue                                   //в правый операнд записывается текущий операнд
        activeOperand.operandValue = removeExtraPrecision(chooseStrategy())                      //в текущий операнд записывается результат вычисления количества
                                                                                                 //процентов(правый опер.) от левого операнда вызывом стратегии
        relatedOperators.lastOperator = relatedOperators.bufferOperator                          //в последний оператор возвращается буферный
        relatedOperators.clearBufferOperator()                                                   //буферный оператор очищается
    }

    private fun activatePressedActionButton(operator: String){                                   //функция выделения кнопки оператора во вью слое
        operationButtonValue = operator
        operationButtonSwitch.value = true
    }

    private fun disableOfPressedActionButton(){                                                  //фнкция отмены выделения кнопки оператора
        operationButtonSwitch.value = false
    }

    private fun handleEquals(){                                                                  //функция обработки =
        if (activeOperand.operandValue.isEmpty())                                                //если текущий операнд пуст
            handleEqualsForExtendedOperators()                                                   //вызывается функция обработки = для расширенных операторов (+=, -=, == и др)
        else                                                                                     //(== здесь в контексте повторения одной и той же операции, как на айфоне)
            handleEqualsForExpression()                                                          //иначе вызывается функция обработки = для выражения

        activeOperand.clearDigit()                                                               //текущий операнд очищается
        rightOperand.clearDigit()                                                                //правый операнд очищается
    }

    private fun handleEqualsForExtendedOperators(){                                              //функция обработки = для расширенных операторов (+=, -=, == и др)
        if (lastPressedButton == EQUALS_OPERATION && bufferOperand.isNotEmpty()){                //если последняя нажатая кнопка = и буферный операнд не пуст
            rightOperand.operandValue = bufferOperand                                            //в правый операнд записывается буферный. Для операций ==, === и тд - то есть повторение одной и той же операции, как на айфоне

            calculateOperationResult()                                                           //вызывается функция подсчета
        }
        else {                                                                                   //иначе - для операций +=,-=,*=,/=
            bufferOperand = leftOperand.operandValue                                             //в буферный операнд запоминается левый операнд на случай след операции == - т.е. повторения

            calculateOperationResult()                                                           //вызывается функция подсчета
        }
    }

    private fun handleEqualsForExpression(){                                                     //функция обработки = для выражений
        rightOperand.operandValue = activeOperand.operandValue                                   //в правый операнд записывается текущее число
        bufferOperand = activeOperand.operandValue                                               //и текущее число запоминается в буфере на случай повторения операции (==)

        calculateOperationResult()                                                               //вызывается функция подсчета
    }

    private fun removeExtraPrecision(text: Double): String {                                     //функция отбрасывает лишнюю точность
        val resultLong = text.toLong()
        return if (text == resultLong.toDouble()) resultLong.toString()
        else text.toString()
    }
}