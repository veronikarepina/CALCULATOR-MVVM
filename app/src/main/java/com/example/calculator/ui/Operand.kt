package com.example.calculator.ui

class Operand {                                                                                  //класс операнд, для сохранения объектов чисел и взаимодействия с ними
    var operandValue = EMPTY_STRING

    companion object {
        private const val EMPTY_STRING = ""
        private const val ZERO_STRING = "0"
        private const val ZERO_WITH_POINT = "0."
        private const val NEGATIVE_ZERO_STRING = "-0"
        private const val POINT = "."
        private const val COMMA = ","
        private const val MINUS_CHAR = '-'
        private const val MINUS_STRING = "-"
        private const val SPACE_CHAR = ' '
    }

    fun clearDigit(){                                                                            //функция очищения поля переменной
        operandValue = EMPTY_STRING
    }

    fun addDigit(buttonNumber: String): String{                                                  //функция добавления цифры к числу
        if (operandValue == ZERO_STRING || operandValue == NEGATIVE_ZERO_STRING)                 //если число нулевое
            operandValue = operandValue.replace(ZERO_STRING, buttonNumber)                       //то ноль заменяется цифрой
        else
            operandValue += buttonNumber                                                         //иначе к числу добавляется цифра

        return convertToOutput()
    }

    fun addPoint(): String {                                                                     //функция добавления дробной точки
        if (operandValue.isEmpty())                                                              //если число пусто
            operandValue = ZERO_WITH_POINT                                                       //то в переменную записывается 0.
        else if(POINT !in operandValue)                                                          //иначе если точка не содержится в числе
            operandValue += POINT                                                                //добавляем точку к числу

        return convertToOutput()                                                                 //возращаем результат приведения числа к формату для вывода
    }

    fun changeSign(): String{                                                                    //функция изменения знака числа
        operandValue = if (operandValue.isEmpty()) NEGATIVE_ZERO_STRING                          //если число пусто, записываем -0
        else if (operandValue.startsWith(MINUS_CHAR))                                            //если начинается с -, то убираем его
            operandValue.replace(MINUS_STRING, EMPTY_STRING)
        else operandValue.padStart(operandValue.length + 1, MINUS_CHAR)                    //иначе добавляем минус в начало

        return convertToOutput()
    }

    fun deleteLastNumber(): String{                                                              //функция удаления последней цифры числа
        operandValue = if (operandValue.length == 1) ZERO_STRING                                 //если число состоит из одной цифры, она заменяется на 0
        else
            if(operandValue.length == 2 && operandValue.startsWith(MINUS_CHAR))                  //если число отрицательно и состоит из одной цифры,
                NEGATIVE_ZERO_STRING                                                             //заменяется на -0
        else
            operandValue.replaceFirst(".$".toRegex(), EMPTY_STRING)                              //иначе последний элемент строки удаляется

        return convertToOutput()
    }

    fun convertToOutput(): String{                                                               //функция преобразовывает число в нужный формат для вывода на экран
        return if (POINT in operandValue )                                                       //если содержится .
            convertFractionalNumber()                                                            //вызывается функция преобразования дробного числа
        else
            convertInteger()                                                                     //иначе - функция преобразования целого числа
    }

    private fun convertFractionalNumber(): String{                                               //функция преобразования дробного числа
        val integerPart = operandValue.substringBefore(POINT)                                    //целая и дробная части отделяются друг от друга
        val doublePart = operandValue.substringAfter(POINT)
        return splitNumberIntoHundreds(integerPart) + COMMA + doublePart                         //возвращается результат преобразования целой части + , + дробная часть без изменений
    }

    private fun convertInteger(): String{                                                        //функция преобразования целого числа
        return splitNumberIntoHundreds(operandValue)
    }

    private fun splitNumberIntoHundreds(text: String): String{                                   //функция разбивает число на сотни (1000 -> 1 000)
        val textList = text.toMutableList()                                                      //создание изменяемого списка
        val hundreds: Int = textList.size / 3                                                    // считаем число сотенных разрядов

        for (i in 1..hundreds){                                                            // цикл вставляет пробел перед каждой сотней
            val index = textList.size - 3 * (hundreds - (i - 1))
            textList.add(index, SPACE_CHAR)
        }

        if (textList[0] == SPACE_CHAR)                                                           //если образовался пробел в начале строки, удаляем его
            textList.remove(textList[0])

        if(textList[0] == MINUS_CHAR && textList[1] == SPACE_CHAR)                               //или пробел перед знаком минус, удаляем его
            textList.remove(textList[1])

        return textList.joinToString(EMPTY_STRING)                                               //возвращается преобразованный список в строку
    }
}