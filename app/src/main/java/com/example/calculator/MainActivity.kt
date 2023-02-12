package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.calculator.databinding.ActivityMainBinding
import net.objecthunter.exp4j.ExpressionBuilder
import java.math.BigDecimal
import java.math.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var firstText = ""
        var secondText = ""
        var action = ""
        val actions = listOf("+", "-", "*", "/", "=")
        var lastButton = ""
        var lastAction = ""
        var lastDigit = ""

        fun deleteButtonC(){
            binding.deleteButton.text = getString(R.string.string_c)
        }
        fun deleteButtonAC(){
            binding.deleteButton.text = getString(R.string.string_ac)
        }
        fun numberSplit(text: String): String{ //функция разбивает число на сотни (1000 -> 1 000)
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
        fun textToShow(text: String):String{ //функция преобразовывает число в нужный формат для вывода. Это число для отображения
            var result = text
            if ("." in result || "," in result){
                val textList: MutableList<Char> = result.toMutableList()
                if ("." in result){
                    val indexComma = textList.indexOf('.')
                    Log.d("MyLog", "indexComma $indexComma")
                    when(indexComma == -1){
                        true -> {}
                        false -> textList[indexComma] = ','
                    }
                }
                val indexPoint = textList.indexOf(',')
                val firstList = textList.slice(0.. indexPoint - 1)
                val secondList = textList.slice(indexPoint + 1 .. textList.size - 1)
                Log.d("MyLog", "Before $textList")
                Log.d("MyLog", "First list $firstList")
                Log.d("MyLog", "Second list $secondList")
                val separator = ""
                val firstString = firstList.joinToString(separator)
                val secondString = secondList.joinToString(separator)
                result = numberSplit(firstString) + "," + secondString
                Log.d("MyLog", "First list $result")
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
        fun textToCount(text: String): String{ //функция удаляет все пробелы. Число для подсчетов
            val textList: MutableList<Char> = text.toMutableList()
            Log.d("MyLog", "textList before $textList")
            val space = ' '
            for (i in 0 .. textList.size - 1){
                textList.remove(space)
            }
            Log.d("MyLog", "textList after $textList")
            if ("," in text){                               //меняем запятую на точку для подсчетов
                val indexComma = textList.indexOf(',')
                textList[indexComma] = '.'
                Log.d("MyLog", "index comma $indexComma")
            }
            val separator = ""
            return textList.joinToString(separator)
        }
        fun addText(buttonNumber: String){ //функция добавления нажатой цифры
            var textNow = binding.textResult.text.toString()
            if (lastButton in actions && textNow != "0" && textNow != "-0") { //если последняя нажатая кнопка была оператором
                if (textNow == "Ошибка") lastAction = ""                      //и не была нажата кнопка смены знака
                textNow = buttonNumber   //то начинаем писать новое число
            }
            else {
                when(textNow == "0" || textNow == "-0"){
                    true -> {
                        when(textNow == "0"){
                            true -> {
                                deleteButtonC()
                                textNow = buttonNumber
                            }
                            false -> {
                                deleteButtonC()
                                textNow = (-(buttonNumber.toInt())).toString()
                            }
                        }
                    }
                    false -> textNow = textNow + buttonNumber
                }
            }
            binding.textResult.text = textToShow(textNow)
            lastButton = buttonNumber
            Log.d("MyLog", "lastButton in numbers:$lastButton")
        }
        fun addPoint(fraction: String){
            var textNow = binding.textResult.text.toString()
            if ("," in textNow){
                binding.textResult.text = textToShow(textNow)
            }
            else {
                textNow = textNow + fraction
                binding.textResult.text = textToShow(textNow)
            }
        }
        binding.deleteButton.setOnClickListener {
            deleteButtonAC()
            firstText = ""
            secondText = ""
            action = ""
            lastButton = ""
            lastAction = ""
            lastDigit = ""
            binding.textResult.text = "0"
        }
        //обработчики нажатия кнопок от 0 до 9
        binding.zeroButton.setOnClickListener {
            val buttonNumber = binding.zeroButton.text.toString()
            addText(buttonNumber)
        }
        binding.oneButton.setOnClickListener {
            val buttonNumber = binding.oneButton.text.toString()
            addText(buttonNumber)
        }
        binding.twoButton.setOnClickListener {
            val buttonNumber = binding.twoButton.text.toString()
            addText(buttonNumber)
        }
        binding.threeButton.setOnClickListener {
            val buttonNumber = binding.threeButton.text.toString()
            addText(buttonNumber)
        }
        binding.fourButton.setOnClickListener {
            val buttonNumber = binding.fourButton.text.toString()
            addText(buttonNumber)
        }
        binding.fiveButton.setOnClickListener {
            val buttonNumber = binding.fiveButton.text.toString()
            addText(buttonNumber)
        }
        binding.sixButton.setOnClickListener {
            val buttonNumber = binding.sixButton.text.toString()
            addText(buttonNumber)
        }
        binding.sevenButton.setOnClickListener {
            val buttonNumber = binding.sevenButton.text.toString()
            addText(buttonNumber)
        }
        binding.eightButton.setOnClickListener {
            val buttonNumber = binding.eightButton.text.toString()
            addText(buttonNumber)
        }
        binding.nineButton.setOnClickListener {
            val buttonNumber = binding.nineButton.text.toString()
            addText(buttonNumber)
        }
        binding.floatButton.setOnClickListener {
            val fraction = binding.floatButton.text.toString()
            addPoint(fraction)
        }
        fun textLong(text: Double): String{
            val resultLong = text.toLong()
            var resultText = ""
            Log.d("MyLog", "resultLong $resultLong")
            if(text == resultLong.toDouble()){
                resultText = resultLong.toString()
                Log.d("MyLog", "resultText in true $resultText")
            }
            else{
                resultText = text.toString()
                Log.d("MyLog", "resultText in false $resultText")
            }
            return resultText
        }
        fun calculation(text: String): String{
            var resultText = ""
            try{
                val expression = ExpressionBuilder(text).build()
                val result = expression.evaluate()
                Log.d("MyLog", "result $result")
                resultText = textLong(result)
            }
            catch (e:Exception) {
                binding.textResult.text = "Ошибка"
                Log.d("Ошибка", "${e.message}")
            }
            return resultText
        }
        fun countResult(firstOperand: String, secondOperand: String, operator: String): String{ //функция вычисляет
            var resultText = ""                                                                 //результат операции
            if (operator == "/" && secondOperand == "0"){
                Log.d("MyLog", "operator $operator")
                Log.d("MyLog", "secondOperand $secondOperand")
                var str = getString(R.string.string_error)
                Log.d("MyLog", "str: $str")
                resultText = str
            }
            else {
                val fullText = firstOperand + operator + secondOperand
                Log.d("MyLog", "fullText $fullText")
                resultText = calculation(fullText)
            }
            return resultText
        }
        fun actionTo(operator: String){
            val textNow = binding.textResult.text.toString()
            if (firstText == "" && (textNow == "0" || textNow == "-0") && lastDigit != "" && lastAction == "=") { // если первый операнд пуст и в рабочее поле ничего не было введено
                binding.textResult.text == textNow                      //то оставляем как есть
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
                            binding.textResult.text = textNow //в рабочем поле ничего не меняется
                        }
                        else { //иначе, предыдущая кнопка - не оператор
                            Log.d("MyLog", "lastAction until count:$lastAction")
                            secondText = textToCount(textNow) //фиксируем второй операнд
                            firstText = countResult(firstText, secondText, lastAction)   //в первый операнд записываем результат вычисления
                            if (firstText == getString(R.string.string_error)){
                                binding.textResult.text = firstText
                                firstText = ""
                                lastButton = "="
                            }
                            else{
                                val dec = BigDecimal(firstText, MathContext.DECIMAL32)
                                Log.d("MyLog", "dec $dec")
                                binding.textResult.text = textToShow(dec.toString())  //выводим на экран этот результат
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
        //обработчики кнопок +,-,*,/,%
        binding.additionButton.setOnClickListener {
            action = "+"
            actionTo(action)
        }
        binding.subtractionButton.setOnClickListener {
            action = "-"
            actionTo(action)
        }
        binding.multiplicationButton.setOnClickListener {
            action = "*"
            actionTo(action)
        }
        binding.divisionButton.setOnClickListener {
            action = "/"
            actionTo(action)
        }
        binding.percentButton.setOnClickListener {
            var textNow = binding.textResult.text.toString()
            var textCount = 0.0
            when(textNow == getString(R.string.string_error)){
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
                    binding.textResult.text = textToShow(textLong(textCount))
                }
            }
        }
        binding.equalsButton.setOnClickListener {//обработчик кнопки =
            Log.d("MyLog", "LB: $lastButton")
            val textNow = binding.textResult.text.toString()
            when(textNow == getString(R.string.string_error)){
                true -> {}
                false -> {
                    if(lastAction == ""){ //если не была нажата кнопка оператора
                        binding.textResult.text = textNow //оставляем в поле все как есть
                    }
                    else if (lastButton == "=" && lastDigit != ""){
                        firstText = textToCount(textNow)
                        when(lastButton in actions && lastButton != "="){
                            true -> secondText = firstText
                            false -> secondText = lastDigit
                        }
                        val result = countResult(firstText, secondText, lastAction)
                        val decResult = BigDecimal(result, MathContext.DECIMAL32)
                        Log.d("MyLog", "dec $decResult")
                        binding.textResult.text = textToShow(decResult.toString())
                        firstText = ""
                        secondText = ""
                        lastButton = "="
                        Log.d("MyLog", "im here")
                    }
                    else{
                        secondText = textToCount(textNow) //фиксируем второй операнд
                        Log.d("MyLog", "SecondText in result: $secondText")
                        Log.d("MyLog", "firstText in result: $firstText")
                        val result = countResult(firstText, secondText, lastAction)
                        if (result == getString(R.string.string_error)){
                            binding.textResult.text = result
                        }
                        else {
                            val decResult = BigDecimal(result, MathContext.DECIMAL32)
                            Log.d("MyLog", "dec $decResult")
                            binding.textResult.text = textToShow(decResult.toString()) //считаем и выводим на экран результат
                            lastDigit = secondText
                            Log.d("MyLog", "lastDigit $lastDigit")
                            Log.d("MyLog", "lastAction $lastAction")
                        }
                        firstText = ""
                        secondText = ""
                        lastButton = "="
                        Log.d("MyLog", "No, im here")
                    }
                }
            }
        }
        binding.signButton.setOnClickListener {
            var textNow = binding.textResult.text.toString()
            when(textNow == getString(R.string.string_error)){
                true -> {binding.textResult.text = "-0"}
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
                    binding.textResult.text = textToShow(textNow)
                }
            }
        }
    }
}