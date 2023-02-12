package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import com.example.calculator.databinding.ActivityMainBinding
import net.objecthunter.exp4j.ExpressionBuilder
import java.math.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<CalcViewModel>()
    lateinit var result: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        result = binding.textResult.text.toString()

        viewModel.answer.observe(this, {binding.textResult.text = it})

        binding.deleteButton.setOnClickListener {
            viewModel.delete()
        }
        //обработчики нажатия кнопок от 0 до 9
        binding.zeroButton.setOnClickListener {
            viewModel.addText(getString(R.string.string_zero))
        }
        binding.oneButton.setOnClickListener {
            viewModel.addText(getString(R.string.string_one))
        }
        binding.twoButton.setOnClickListener {
            viewModel.addText(getString(R.string.string_two))
        }
        binding.threeButton.setOnClickListener {
            viewModel.addText(getString(R.string.string_three))
        }
        binding.fourButton.setOnClickListener {
            viewModel.addText(getString(R.string.string_four))
        }
        binding.fiveButton.setOnClickListener {
            viewModel.addText(getString(R.string.string_five))
        }
        binding.sixButton.setOnClickListener {
            viewModel.addText(getString(R.string.string_six))
        }
        binding.sevenButton.setOnClickListener {
            viewModel.addText(getString(R.string.string_seven))
        }
        binding.eightButton.setOnClickListener {
            viewModel.addText(getString(R.string.string_eight))
        }
        binding.nineButton.setOnClickListener {
            viewModel.addText(getString(R.string.string_nine))
        }
        binding.floatButton.setOnClickListener {
            viewModel.addPoint(getString(R.string.string_comma))
        }
        //обработчики кнопок +,-,*,/,%
        binding.additionButton.setOnClickListener {
            viewModel.actionTo(getString(R.string.string_plus))
        }
        binding.subtractionButton.setOnClickListener {
            viewModel.actionTo(getString(R.string.string_minus))
        }
        binding.multiplicationButton.setOnClickListener {
            viewModel.actionTo(getString(R.string.string_multiply))
        }
        binding.divisionButton.setOnClickListener {
            viewModel.actionTo(getString(R.string.string_divide))
        }
        binding.percentButton.setOnClickListener {
            viewModel.countPercent()
        }
        binding.equalsButton.setOnClickListener {//обработчик кнопки =
            viewModel.equal()
        }
        binding.signButton.setOnClickListener {
            viewModel.signChange()
        }
    }
}