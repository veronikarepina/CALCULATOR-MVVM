package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.example.calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<CalcViewModel>()
    lateinit var textView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        textView = binding.textResult
        with(viewModel){
            answer.observe(this@MainActivity) { binding.textResult.text = it }
            buttonC.observe(this@MainActivity) { deleteButtonC() }
            buttonAC.observe(this@MainActivity) { deleteButtonAC() }
            error.observe(this@MainActivity) { viewModel.errorString = getString(it) }
            buttonFlag.observe(this@MainActivity) { buttonOn(it) }
        }
        with(binding){
            deleteButton.setOnClickListener { viewModel.delete() }
            zeroButton.setOnClickListener { viewModel.addText(getString(R.string.string_zero)) }
            oneButton.setOnClickListener { viewModel.addText(getString(R.string.string_one)) }
            twoButton.setOnClickListener { viewModel.addText(getString(R.string.string_two)) }
            threeButton.setOnClickListener { viewModel.addText(getString(R.string.string_three)) }
            fourButton.setOnClickListener { viewModel.addText(getString(R.string.string_four)) }
            fiveButton.setOnClickListener { viewModel.addText(getString(R.string.string_five)) }
            sixButton.setOnClickListener { viewModel.addText(getString(R.string.string_six)) }
            sevenButton.setOnClickListener { viewModel.addText(getString(R.string.string_seven)) }
            eightButton.setOnClickListener { viewModel.addText(getString(R.string.string_eight)) }
            nineButton.setOnClickListener { viewModel.addText(getString(R.string.string_nine)) }
            floatButton.setOnClickListener { viewModel.addPoint(getString(R.string.string_comma)) }
            additionButton.setOnClickListener { viewModel.actionTo(getString(R.string.string_plus)) }
            subtractionButton.setOnClickListener { viewModel.actionTo(getString(R.string.string_minus)) }
            multiplyButton.setOnClickListener { viewModel.actionTo(getString(R.string.string_multiply)) }
            divisionButton.setOnClickListener { viewModel.actionTo(getString(R.string.string_divide)) }
            percentButton.setOnClickListener { viewModel.countPercent() }
            equalsButton.setOnClickListener { viewModel.equal() }
            signButton.setOnClickListener { viewModel.signChange() }
        }
        textView.setOnTouchListener(object: SwipeListener(this@MainActivity){
            override fun onSwipe() {
                viewModel.deleteLastDigit()
            }
        })
    }
    private fun buttonOn(it: Boolean){
        when(it){
            true -> {
                when(viewModel.buttonState){
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
            false -> {
                when(viewModel.buttonState){
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
    private fun deleteButtonC() {
        binding.deleteButton.text = getString(R.string.string_c)
    }
    private fun deleteButtonAC() {
        binding.deleteButton.text = getString(R.string.string_ac)
    }
}