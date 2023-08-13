package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.example.calculator.databinding.ActivityMainBinding
import com.example.calculator.ui.CalcViewModel
import com.example.calculator.ui.SwipeListener

class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<CalcViewModel>()
    private lateinit var textView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        textView = binding.textResult

        with(viewModel){                                                          //слушатели переменных livedata
            outputResult.observe(this@MainActivity) { binding.textResult.text = it }
            textForDivisionButton.observe(this@MainActivity) { setTextForDivisionButton(it) }
            receiverStringError.observe(this@MainActivity) { viewModel.keeperStringError = getString(it) }
            operationButtonSwitch.observe(this@MainActivity) { switchActionButton(it) }
        }

        with(binding){                                                         //слушатели нажатия кнопок
            deleteButton.setOnClickListener { viewModel.clearAllFields() }
            zeroButton.setOnClickListener { viewModel.addDigit(getString(R.string.string_zero)) }
            oneButton.setOnClickListener { viewModel.addDigit(getString(R.string.string_one)) }
            twoButton.setOnClickListener { viewModel.addDigit(getString(R.string.string_two)) }
            threeButton.setOnClickListener { viewModel.addDigit(getString(R.string.string_three)) }
            fourButton.setOnClickListener { viewModel.addDigit(getString(R.string.string_four)) }
            fiveButton.setOnClickListener { viewModel.addDigit(getString(R.string.string_five)) }
            sixButton.setOnClickListener { viewModel.addDigit(getString(R.string.string_six)) }
            sevenButton.setOnClickListener { viewModel.addDigit(getString(R.string.string_seven)) }
            eightButton.setOnClickListener { viewModel.addDigit(getString(R.string.string_eight)) }
            nineButton.setOnClickListener { viewModel.addDigit(getString(R.string.string_nine)) }
            floatButton.setOnClickListener { viewModel.addPoint(getString(R.string.string_point)) }
            additionButton.setOnClickListener { viewModel.action(getString(R.string.string_plus)) }
            subtractionButton.setOnClickListener { viewModel.action(getString(R.string.string_minus)) }
            multiplyButton.setOnClickListener { viewModel.action(getString(R.string.string_multiply)) }
            divisionButton.setOnClickListener { viewModel.action(getString(R.string.string_divide)) }
            percentButton.setOnClickListener { viewModel.actionPercent() }
            equalsButton.setOnClickListener { viewModel.equal() }
            signButton.setOnClickListener { viewModel.changeSign() }
        }
        textView.setOnTouchListener(object: SwipeListener(this@MainActivity){             //слушатель свайпов влево/вправо для удаления последней цифры
            override fun onSwipe() {
                viewModel.deleteLastNumber()
            }
        })
    }

    private fun switchActionButton(it: Boolean){
        when(it){
            true -> {                                                                            //при нажатии на кнопку операции, функция делает видимой ее
                when(viewModel.operationButtonValue){                                            //белую версию, а версию по умолчанию - невидимой
                    getString(R.string.string_divide) -> {
                        binding.divisionButton.visibility = View.INVISIBLE
                        binding.whiteDivisionButton.visibility = View.VISIBLE
                    }
                    getString(R.string.string_multiply) -> {
                        binding.multiplyButton.visibility = View.INVISIBLE
                        binding.whiteMultiplyButton.visibility = View.VISIBLE
                    }
                    getString(R.string.string_plus) -> {
                        binding.additionButton.visibility = View.INVISIBLE
                        binding.whiteAdditionButton.visibility = View.VISIBLE
                    }
                    getString(R.string.string_minus) -> {
                        binding.subtractionButton.visibility = View.INVISIBLE
                        binding.whiteSubtractionButton.visibility = View.VISIBLE
                    }
                }
            }
            false -> {                                                                           //наоборот, версия по умолчанию - видима
                when(viewModel.operationButtonValue){                                            //белая версия - невидима
                    getString(R.string.string_divide) -> {
                        binding.divisionButton.visibility = View.VISIBLE
                        binding.whiteDivisionButton.visibility = View.INVISIBLE
                    }
                    getString(R.string.string_multiply) -> {
                        binding.multiplyButton.visibility = View.VISIBLE
                        binding.whiteMultiplyButton.visibility = View.INVISIBLE
                    }
                    getString(R.string.string_plus) -> {
                        binding.additionButton.visibility = View.VISIBLE
                        binding.whiteAdditionButton.visibility = View.INVISIBLE
                    }
                    getString(R.string.string_minus) -> {
                        binding.subtractionButton.visibility = View.VISIBLE
                        binding.whiteSubtractionButton.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    private fun setTextForDivisionButton(it: Boolean) {                                          //функция уставливает в кнопку удаления текст С/АС
        when (it){
            true -> binding.deleteButton.text = getString(R.string.string_ac)
            false -> binding.deleteButton.text = getString(R.string.string_c)
        }
    }
}