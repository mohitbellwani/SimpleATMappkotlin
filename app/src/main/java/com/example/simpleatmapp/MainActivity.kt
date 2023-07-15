package com.example.simpleatmapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var withdrawBtn: Button
    private lateinit var depositBtn: Button
    private lateinit var amountField: EditText
    private lateinit var deposit2000Field: EditText
    private lateinit var deposit325Field: EditText
    private lateinit var deposit500Field: EditText
    private lateinit var deposit200Field: EditText
    private lateinit var deposit100Field: EditText
    private lateinit var totalAmountTextView: TextView
    private lateinit var currencyNotesTextView: TextView

    private var currencyNotes = mutableMapOf(2000 to 5, 500 to 5, 325 to 5, 200 to 5, 100 to 5)
    private var totalAmount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        withdrawBtn = findViewById(R.id.withdrawBtn)
        depositBtn = findViewById(R.id.depositBtn)
        amountField = findViewById(R.id.amountField)
        deposit2000Field = findViewById(R.id.deposit2000Field)
        deposit500Field = findViewById(R.id.deposit500Field)
        deposit325Field = findViewById(R.id.deposit325Field)
        deposit200Field = findViewById(R.id.deposit200Field)
        deposit100Field = findViewById(R.id.deposit100Field)
        totalAmountTextView = findViewById(R.id.totalAmountTextView)
        currencyNotesTextView = findViewById(R.id.currencyNotesTextView)

        withdrawBtn.setOnClickListener {
            withdrawAmount()
        }

        depositBtn.setOnClickListener {
            depositAmount()
        }

        updateCurrencyNotesDisplay()
    }

    private fun withdrawAmount() {
        val requestedAmount = amountField.text.toString().toIntOrNull()

        if (requestedAmount != null && requestedAmount > 0) {
            val withdrawNotes = mutableMapOf<Int, Int>()
            val success = withdrawHelper(requestedAmount, currencyNotes.toMutableMap(), withdrawNotes)

            if (success) {
                withdrawNotes.forEach { (denomination, count) ->
                    currencyNotes[denomination] = currencyNotes[denomination]!! - count
                }
                updateCurrencyNotesDisplay()
                amountField.text.clear()
                showToast("Amount withdrawn successfully")
            } else {
                showToast("Insufficient funds. Please try again.")
            }
        } else {
            showToast("Invalid amount. Please try again.")
        }
    }

    private fun withdrawHelper(
        remainingAmount: Int,
        currentCurrencyNotes: MutableMap<Int, Int>,
        withdrawNotes: MutableMap<Int, Int>
    ): Boolean {
        if (remainingAmount == 0) {
            return true
        }

        val denominations = currentCurrencyNotes.keys.sortedDescending()

        for (denomination in denominations) {
            val count = currentCurrencyNotes[denomination] ?: 0

            if (count > 0 && remainingAmount >= denomination) {
                withdrawNotes[denomination] = withdrawNotes.getOrDefault(denomination, 0) + 1
                currentCurrencyNotes[denomination] = count - 1

                val success = withdrawHelper(
                    remainingAmount - denomination,
                    currentCurrencyNotes,
                    withdrawNotes
                )

                if (success) {
                    return true
                }

                withdrawNotes[denomination] = withdrawNotes.getOrDefault(denomination, 0) - 1
                currentCurrencyNotes[denomination] = count
            }
        }

        return false
    }


    private fun depositAmount() {
        val deposit2000 = deposit2000Field.text.toString().toIntOrNull() ?: 0
        val deposit500 = deposit500Field.text.toString().toIntOrNull() ?: 0
        val deposit325 = deposit325Field.text.toString().toIntOrNull() ?: 0
        val deposit200 = deposit200Field.text.toString().toIntOrNull() ?: 0
        val deposit100 = deposit100Field.text.toString().toIntOrNull() ?: 0

        if (deposit2000 >= 0 && deposit500 >= 0 && deposit325 >= 0 && deposit200 >= 0 && deposit100 >= 0 ) {
            currencyNotes[2000] = currencyNotes[2000]!! + deposit2000
            currencyNotes[500] = currencyNotes[500]!! + deposit500
            currencyNotes[325] = currencyNotes[325]!! + deposit325
            currencyNotes[200] = currencyNotes[200]!! + deposit200
            currencyNotes[100] = currencyNotes[100]!! + deposit100

            updateCurrencyNotesDisplay()
            deposit2000Field.text.clear()
            deposit500Field.text.clear()
            deposit325Field.text.clear()
            deposit200Field.text.clear()
            deposit100Field.text.clear()
            showToast("Amount deposited successfully")
        } else {
            showToast("Invalid amount. Please try again.")
        }
    }


    private fun updateCurrencyNotesDisplay() {
        val currencyNotesText = StringBuilder()

        currencyNotes.forEach { (denomination, count) ->
            if (count > 0) {
                currencyNotesText.append("$denomination x $count\n")
            }
        }

        currencyNotesTextView.text = currencyNotesText.toString()

        totalAmount = calculateTotalAmount()
        totalAmountTextView.text = "Total Amount: $totalAmount"
    }

    private fun calculateTotalAmount(): Int {
        var amount = 0

        currencyNotes.forEach { (denomination, count) ->
            amount += denomination * count
        }

        return amount
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        //Log.d( "log message: ",message)
    }
}
