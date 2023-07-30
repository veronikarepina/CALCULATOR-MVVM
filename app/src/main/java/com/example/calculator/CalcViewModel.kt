package com.example.calculator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.objecthunter.exp4j.ExpressionBuilder
import java.math.BigDecimal
import java.math.MathContext

class CalcViewModel: ViewModel(){
    val answer = MutableLiveData<String>()
    val buttonC = MutableLiveData<Boolean>()
    val buttonAC = MutableLiveData<Boolean>()
    val error = MutableLiveData<Int>()
    val buttonFlag = MutableLiveData<Boolean>()
    var buttonState: String = ""
    var errorString = ""
    private var firstText = ""
    private var secondText = ""
    private val actions = listOf("+", "-", "*", "/", "=")
    private var lastButton = ""
    private var lastAction = ""
    private var lastDigit = ""
    private var errorFlag = false
    init {
        answer.value = "0"
        buttonFlag.value = false
    }
    private fun numberSplit(text: String): String{ //функция разбивает число на сотни (1000 -> 1 000)
        val textList = text.toMutableList() //создание изменяемого списка
        textList.removeAll(listOf(' ')) //удаление имеющихся пробелов
        val countThree: Int = textList.size / 3 // считаем число сотенных разрядов
        for (i in 1..countThree){ // цикл вставляет пробел перед каждой сотней
            val index = textList.size - 3 * (countThree - (i - 1))
            textList.add(index, ' ')
        }
        if (textList[0] == ' ') //если образовался пробел в начале строки, удаляем его
            textList.remove(textList[0])
        if(textList[0] == '-' && textList[1] == ' ')
            textList.remove(textList[1])
        return textList.joinToString("") //преобразуем список в строку
    }
    private fun textToShow(text: String):String{ //функция преобразовывает число в нужный формат для вывода. Это число для отображения
        var result = text
        if ("." in result || "," in result){
            if ("." in result)
                result = result.replace('.', ',')
            val firstString = result.substringBefore(',')
            val secondString = result.substringAfter(',')
            result = numberSplit(firstString) + "," + secondString
        }
        else if (result.length > 3) //выполняем преобразования в случае, если число состоит более чем из 3 цифр
            result = numberSplit(result)
        return result
    }
    private fun textToCount(text: String): String{ //функция удаляет все пробелы. Число для подсчетов
        var result = text.replace(" ", "")
        if (',' in result)
            result = result.replace(',', '.') //меняем запятую на точку для подсчетов
        return result
    }
    fun addText(buttonNumber: String){ //функция добавления нажатой цифры
        var textNow = answer.value.toString()
        buttonFlag.value = false
        when(errorFlag){
            true -> {
                lastAction = ""
                errorFlag = false
                textNow = buttonNumber
            }
            false -> {
                if (lastButton in actions) textNow = buttonNumber   //если последняя нажатая кнопка была оператором//то начинаем писать новое число
                else {
                    when(textNow == "0" || textNow == "-0"){
                        true -> {
                            buttonC.value = true
                            textNow = if ("-" in textNow) (-(buttonNumber.toInt())).toString()
                            else buttonNumber
                        }
                        false -> textNow += buttonNumber
                    }
                }
            }
        }
        answer.value = textToShow(textNow)
        lastButton = buttonNumber
    }
    fun addPoint(fraction: String){
        buttonFlag.value = false
        var textNow = answer.value.toString()
        if ("," in textNow){
            answer.value = textToShow(textNow)
        }
        else {
            if(textNow == "0" || textNow == "-0") buttonC.value = true
            textNow += fraction
            answer.value = textToShow(textNow)
        }
    }
    fun deleteLastDigit(){
        var textNow = answer.value.toString()
        textNow = if (textNow.length == 1) "0"
            else if ("-" in textNow && textNow.length == 2) "-0"
            else textToShow(textNow.replaceFirst(".$".toRegex(), ""))
        answer.value = textToShow(textNow)
    }
    fun delete(){
        buttonAC.value = true
        errorFlag = false
        firstText = ""
        secondText = ""
        lastButton = ""
        lastAction = ""
        lastDigit = ""
        answer.value = "0"
        buttonFlag.value = false
    }
    private fun textLong(text: Double): String {
        val resultLong = text.toLong()
        return if (text == resultLong.toDouble()) resultLong.toString()
        else text.toString()
    }
    private fun calculation(text: String): String{
        var resultText = ""
        try{
            val expression = ExpressionBuilder(text).build().evaluate()
            resultText = textLong(expression)
        }
        catch (e:Exception) {
            answer.value = "Ошибка"
        }
        return resultText
    }
    private fun countResult(firstOperand: String, secondOperand: String, operator: String): String{ //функция вычисляет
        val resultText: String                                                                 //результат операции
        if (operator == "/" && secondOperand == "0"){
            errorFlag = true
            error.value = R.string.string_error
            resultText = errorString
        }
        else {
            val fullText = firstOperand + operator + secondOperand
            resultText = calculation(fullText)
        }
        return resultText
    }
    fun actionTo(operator: String){
        val textNow = answer.value.toString()
        if(buttonFlag.value == true){
            buttonFlag.value = false
            buttonState = operator
            buttonFlag.value = true
        }
        buttonState = operator
        buttonFlag.value = true
        when(firstText == ""){ //первый операнд пуст
            true -> {
                firstText = textToCount(textNow) //в первый операнд записываем текущее число
                lastAction = operator       //фиксируем нажатый оператор в памяти
                lastButton = operator
            }
            false -> {  //первый операнд не пуст
                if (lastButton in actions){ //если предыдущая нажатая кнопка была также каким-либо оператором
                    lastAction = operator   //переопределяем оператор
                    lastButton = operator   //переопределяем последнюю нажатую кнопку
                }
                else { //иначе, предыдущая кнопка - не оператор
                    secondText = textToCount(textNow) //фиксируем второй операнд
                    firstText = countResult(firstText, secondText, lastAction)   //в первый операнд записываем результат вычисления
                    if (errorFlag){
                        answer.value = firstText
                        firstText = ""
                        lastButton = "="
                    }
                    else{
                        val dec = BigDecimal(firstText, MathContext.DECIMAL32)
                        answer.value = textToShow(dec.toString())  //выводим на экран этот результат
                        lastAction = operator //переопределяем оператор
                        lastButton = operator //переопределяем последнюю нажатую кнопку
                    }
                    secondText = "" //очищаем второй операнд
                }
            }
        }
    }
    fun countPercent(){
        val textNow = answer.value.toString()
        val textCount: Double
        when(errorFlag){
            true -> {} //если в рабочем поле "ошибка/error" - ничего не делаем
            false -> {  //иначе
                textCount = when(firstText == ""){
                    true -> textToCount(textNow).toDouble() / 100 //если первый операнд пуст
                    false -> {
                        if (lastAction == "-" || lastAction == "+")
                            textToCount(firstText).toDouble() / 100 * textToCount(textNow).toDouble()
                        else textToCount(textNow).toDouble() / 100
                    }
                }
                answer.value = textToShow(textLong(textCount))
            }
        }
    }
    fun equal(){
        val textNow = answer.value.toString()
        val result: String
        when(errorFlag){
            true -> {}
            false -> {
                if(lastAction == "") result = textNow //если не была нажата кнопка оператора оставляем в поле все как есть
                else if (lastButton == "=" && lastDigit != ""){
                    firstText = textToCount(textNow)
                    secondText = if(lastButton in actions && lastButton != "=") firstText else lastDigit
                    result = countResult(firstText, secondText, lastAction)
                }
                else{
                    secondText = textToCount(textNow) //фиксируем второй операнд
                    result = countResult(firstText, secondText, lastAction)
                    lastDigit = secondText
                }
                answer.value = if(errorFlag) result else textToShow(result)
                firstText = ""
                secondText = ""
                lastButton = "="
                buttonFlag.value = false
            }
        }
    }
    fun signChange(){
        var textNow = answer.value.toString()
        when(errorFlag){
            true -> {
                answer.value = "-0"
                errorFlag = false
            }
            false -> {
                when(lastButton in actions && lastButton != "="){
                    true -> {
                        answer.value = "-0"
                        lastButton = ""
                    }
                    false -> {
                        textNow = if (textNow.startsWith('-')) textNow.replace("-", "")
                        else textNow.padStart(textNow.length + 1, '-')
                        answer.value = textNow
                    }
                }
            }
        }
    }
}