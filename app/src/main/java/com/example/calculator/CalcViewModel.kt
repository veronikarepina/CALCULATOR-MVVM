package com.example.calculator

import android.util.Log
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
    private var firstText = ""
    private var secondText = ""
    private val actions = listOf("+", "-", "*", "/", "=")
    private var lastButton = ""
    private var lastAction = ""
    private var lastDigit = ""
    private var errorFlag = false
    var buttonState: String = ""
    var errorString = ""

    init {
        answer.value = "0"
        buttonFlag.value = false
    }

    private fun numberSplit(text: String): String{ //функция разбивает число на сотни (1000 -> 1 000)
        var result = text
        val textList: MutableList<Char> = result.toMutableList() //создание изменяемого списка
        val space = ' '
        for (i in 0 until textList.size - 1){ //цикл удаляет из строки уже имеющиеся пробелы
            textList.remove(space)
        }
        val countThree: Int = textList.size / 3 // считаем число сотенных разрядов
        var index: Int
        for (i in 1..countThree){ // цикл вставляет пробел перед каждой сотней
            index = textList.size - 3 * (countThree - (i - 1))
            textList.add(index, space)
        }
        if (textList[0] == ' ') //если образовался пробел в начале строки, удаляем его
            textList.remove(textList[0])
        if(textList[0] == '-' && textList[1] == ' ')
            textList.remove(textList[1])
        val separator = ""
        result = textList.joinToString(separator) //преобразуем список в строку
        return result
    }
    private fun textToShow(text: String):String{ //функция преобразовывает число в нужный формат для вывода. Это число для отображения
        var result = text
        if ("." in result || "," in result){
            val textList: MutableList<Char> = result.toMutableList()
            if ("." in result){
                val indexComma = textList.indexOf('.')
                when(indexComma == -1){
                    true -> {}
                    false -> textList[indexComma] = ','
                }
            }
            val indexPoint = textList.indexOf(',')
            val firstList = textList.slice(0.. indexPoint - 1)
            val secondList = textList.slice(indexPoint + 1 .. textList.size - 1)
            val separator = ""
            val firstString = firstList.joinToString(separator)
            val secondString = secondList.joinToString(separator)
            result = numberSplit(firstString) + "," + secondString
        }
        else if (text.length > 3){ //выполняем преобразования в случае, если число состоит более чем из 3 цифр
            result = numberSplit(result)
        }
        else
        {
            result = text
        }
        return result
    }
    private fun textToCount(text: String): String{ //функция удаляет все пробелы. Число для подсчетов
        val textList: MutableList<Char> = text.toMutableList()
        val space = ' '
        for (i in 0 .. textList.size - 1){
            textList.remove(space)
        }
        if ("," in text){                               //меняем запятую на точку для подсчетов
            val indexComma = textList.indexOf(',')
            textList[indexComma] = '.'
        }
        val separator = ""
        return textList.joinToString(separator)
    }
    fun addText(buttonNumber: String){ //функция добавления нажатой цифры
        var textNow = answer.value.toString()
        buttonFlag.value = false
        if (lastButton in actions && textNow != "0" && textNow != "-0") { //если последняя нажатая кнопка была оператором
            if (errorFlag) lastAction = ""
            else textNow = buttonNumber   //то начинаем писать новое число //и не была нажата кнопка смены знака
        }
        else {
            when(textNow == "0" || textNow == "-0"){
                true -> {
                    when(textNow == "0"){
                        true -> {
                            buttonC.value = true
                            textNow = buttonNumber
                        }
                        false -> {
                            buttonC.value = true
                            textNow = (-(buttonNumber.toInt())).toString()
                        }
                    }
                }
                false -> textNow = textNow + buttonNumber
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
            textNow = textNow + fraction
            answer.value = textToShow(textNow)
        }
    }
    fun deleteLastDigit(){
        var textNow = answer.value.toString()
        if (errorFlag){}
        else if (textNow == "0" || textNow == "-0"){}
        else {
            if (textNow.length == 1){
                textNow = "0"
            }
            else if("-" in textNow && textNow.length == 2){
                textNow = "-0"
            }
            else {
                Log.d("MyLog", "textNow before: $textNow")
                Log.d("MyLog", "textLenght before: ${textNow.length}")
                textNow = textToShow(textNow.replaceFirst(".$".toRegex(), ""))
                Log.d("MyLog", "textNow after: $textNow")
                Log.d("MyLog", "textLenght after: ${textNow.length}")
            }
        }
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
    private fun textLong(text: Double): String{
        val resultLong = text.toLong()
        var resultText = ""
        if(text == resultLong.toDouble()){
            resultText = resultLong.toString()
        }
        else{
            resultText = text.toString()
        }
        return resultText
    }
    private fun calculation(text: String): String{
        var resultText = ""
        try{
            val expression = ExpressionBuilder(text).build()
            val result = expression.evaluate()
            resultText = textLong(result)
        }
        catch (e:Exception) {
            answer.value = "Ошибка"
        }
        return resultText
    }
    private fun countResult(firstOperand: String, secondOperand: String, operator: String): String{ //функция вычисляет
        var resultText = ""                                                                 //результат операции
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
        if (firstText == "" && (textNow == "0" || textNow == "-0") && lastDigit != "" && lastAction == "=") { // если первый операнд пуст и в рабочее поле ничего не было введено
            answer.value = textNow                      //то оставляем как есть
        }
        else {
            when(firstText == ""){ //первый операнд пуст
                true -> {
                    firstText = textToCount(textNow) //в первый операнд записываем текущее число
                    Log.d("MyLog", "firstText:$firstText")             //с рабочего поля
                    lastAction = operator       //фиксируем нажатый оператор в памяти
                    lastButton = operator
                    Log.d("MyLog", "lastAction with space:$lastAction")
                }
                false -> {  //первый операнд не пуст
                    if (lastButton in actions){ //если предыдущая нажатая кнопка была также каким-либо оператором
                        Log.d("MyLog", "last button before:$lastButton")
                        lastAction = operator   //переопределяем оператор
                        lastButton = operator   //переопределяем последнюю нажатую кнопку
                        Log.d("MyLog", "last action after:$lastAction")
                        answer.value = textNow //в рабочем поле ничего не меняется
                    }
                    else { //иначе, предыдущая кнопка - не оператор
                        Log.d("MyLog", "lastAction until count:$lastAction")
                        secondText = textToCount(textNow) //фиксируем второй операнд
                        firstText = countResult(firstText, secondText, lastAction)   //в первый операнд записываем результат вычисления
                        Log.d("MyLog", "res: $firstText")
                        if (errorFlag){
                            answer.value = firstText
                            firstText = ""
                            lastButton = "="
                        }
                        else{
                            val dec = BigDecimal(firstText, MathContext.DECIMAL32)
                            Log.d("MyLog", "dec $dec")
                            answer.value = textToShow(dec.toString())  //выводим на экран этот результат
                            lastAction = operator //переопределяем оператор
                            lastButton = operator //переопределяем последнюю нажатую кнопку
                            Log.d("MyLog", "lastAction after count:$lastAction")
                        }
                        secondText = "" //очищаем второй операнд
                    }
                }
            }
        }
    }
    fun countPercent(){
        var textNow = answer.value.toString()
        var textCount = 0.0
        when(errorFlag){
            true -> {} //если в рабочем поле "ошибка/error" - ничего не делаем
            false -> {  //иначе
                when(firstText == ""){
                    true -> { //если первый операнд пуст
                        if (textNow == "0" || textNow == "-0"){} // и в рабочем поле находится 0 или -0 - ничего не делаем
                        else {
                            textCount = textToCount(textNow).toDouble() / 100
                            Log.d("MyLog", "textResult $textCount")
                        }
                    }
                    false -> {
                        if(lastAction == "-" || lastAction == "+"){
                            textCount = textToCount(firstText).toDouble() / 100 * textToCount(textNow).toDouble()
                        }
                        else{
                            textCount = textToCount(textNow).toDouble() / 100
                            Log.d("MyLog", "textResult $textCount")
                        }
                    }
                }
                answer.value = textToShow(textLong(textCount))
            }
        }
    }
    fun equal(){
        val textNow = answer.value.toString()
        when(errorFlag){
            true -> {}
            false -> {
                if(lastAction == ""){ //если не была нажата кнопка оператора
                    answer.value = textNow //оставляем в поле все как есть
                }
                else if (lastButton == "=" && lastDigit != ""){
                    firstText = textToCount(textNow)
                    when(lastButton in actions && lastButton != "="){
                        true -> secondText = firstText
                        false -> secondText = lastDigit
                    }
                    val result = countResult(firstText, secondText, lastAction)
                    val decResult = BigDecimal(result, MathContext.DECIMAL32)
                    answer.value = textToShow(decResult.toString())
                    firstText = ""
                    secondText = ""
                    lastButton = "="
                }
                else{
                    secondText = textToCount(textNow) //фиксируем второй операнд
                    val result = countResult(firstText, secondText, lastAction)
                    Log.d("MyLog", "res: $result")
                    if (errorFlag){
                        answer.value = result
                    }
                    else {
                        val decResult = BigDecimal(result, MathContext.DECIMAL32)
                        answer.value = textToShow(decResult.toString()) //считаем и выводим на экран результат
                        lastDigit = secondText
                    }
                    firstText = ""
                    secondText = ""
                    lastButton = "="
                }
            }
        }
    }
    fun signChange(){
        var textNow = answer.value.toString()
        when(errorFlag){
            true -> {answer.value = "-0"}
            false -> {
                if (firstText == "" && textNow == "0" || textNow == "-0") { // если первый операнд пуст и в рабочее поле ничего не было введено
                    when(textNow == "0"){                                 // то меняем знак нуля, ожидая ввода числа
                        true -> textNow = "-0"
                        false -> textNow = "0"
                    }
                }
                else {
                    when (firstText == ""){
                        true -> textNow = (-(textToCount(textNow).toInt())).toString()
                        false -> {
                            when(lastButton in actions){
                                true -> {
                                    if (textNow == "0" || textNow == "-0"){
                                        when(textNow == "0"){
                                            true -> textNow = "-0"
                                            false -> textNow = "0"
                                        }
                                    }
                                    else textNow = "-0"
                                }
                                false -> textNow = (-(textToCount(textNow).toInt())).toString()
                            }
                        }
                    }
                }
                answer.value = textToShow(textNow)
            }
        }
    }
}