package com.example.fisioplac

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.fisioplac.databinding.ActivityTela1FichaBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class Tela1FichaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTela1FichaBinding
    private var pacienteId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTela1FichaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pacienteId = intent.getStringExtra("PACIENTE_ID")
        val pacienteNome = intent.getStringExtra("PACIENTE_NOME")

        if (pacienteNome != null) {
            binding.etNome.setText(pacienteNome)
        }

        setupDropdownMenus()
        setupClickListeners()
        setupRadioGroupLogic()
        setCurrentDate()
        setupPhoneMask()
        setupCurrencyMask()
    }

    private fun setupDropdownMenus() {
        val dropdownMap = mapOf(
            binding.actvEstadoCivil to R.array.opcoes_estado_civil,
            binding.actvEscolaridade to R.array.opcoes_escolaridade,
            binding.actvLocalResidencia to R.array.opcoes_local_residencia,
            binding.actvMoraCom to R.array.opcoes_mora_com,
            binding.actvDiasSemana to R.array.opcoes_dias_semana,
            binding.actvAtividadeSocial to R.array.opcoes_atividade_social,
            binding.actvDoencas to R.array.opcoes_doencas
        )

        dropdownMap.forEach { (autoCompleteTextView, arrayResourceId) ->
            setupAutoCompleteTextView(autoCompleteTextView, arrayResourceId)
        }
    }

    private fun setupAutoCompleteTextView(view: AutoCompleteTextView, arrayResourceId: Int) {
        val items = resources.getStringArray(arrayResourceId)
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, items)
        view.setAdapter(adapter)

        view.setOnItemClickListener { _, _, _, _ ->
            binding.main.requestFocus()
        }

        view.setOnDismissListener { 
            binding.main.requestFocus()
        }
    }

    private fun setupClickListeners() {
        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.btnAvancar.setOnClickListener {
            if (validateFields()) {
                Toast.makeText(this, "Ficha preenchida para o paciente com ID: $pacienteId", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Aviso: Todos os dados são obrigatórios!", Toast.LENGTH_LONG).show()
            }
        }

        binding.etNascimento.setOnClickListener {
            showDatePickerDialog(binding.etNascimento, supportFragmentManager)
        }
    }


    private fun setupRadioGroupLogic() {
        binding.diasSemanaContainer.visibility = View.GONE
        binding.rgAtividadesFisicas.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_sim_atividade) {
                binding.diasSemanaContainer.visibility = View.VISIBLE
            } else {
                binding.diasSemanaContainer.visibility = View.GONE
                binding.actvDiasSemana.text.clear()
            }
        }
    }

    private fun setupPhoneMask() {
        val phoneEditText = binding.etTelefone
        phoneEditText.addTextChangedListener(object : TextWatcher {
            private var isFormatting: Boolean = false
            private var deletingHyphen: Boolean = false
            private var hyphenStart: Int = 0

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                if (!isFormatting) {
                    if (count > 0 && s.length > start && s[start] == '-') {
                        deletingHyphen = true
                        hyphenStart = start
                    } else {
                        deletingHyphen = false
                    }
                }
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (isFormatting) return
                isFormatting = true
                val digits = s.toString().replace("[^\\d]".toRegex(), "")
                val formatted = StringBuilder()
                if (deletingHyphen && hyphenStart > 0 && hyphenStart - 1 < digits.length) {
                    val newDigits = digits.substring(0, hyphenStart - 2) + digits.substring(hyphenStart - 1)
                    formatted.append(formatPhoneNumber(newDigits))
                } else {
                    formatted.append(formatPhoneNumber(digits))
                }
                s.replace(0, s.length, formatted.toString())
                isFormatting = false
            }
        })
    }

    private fun setupCurrencyMask() {
        val editText = binding.etRenda
        editText.addTextChangedListener(object : TextWatcher {
            private var current = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.toString() != current) {
                    editText.removeTextChangedListener(this)
                    val cleanString = s.toString().replace("[^\\d]".toRegex(), "")
                    if (cleanString.isNotEmpty()) {
                        val parsed = cleanString.toDouble() / 100
                        val formatted = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(parsed)
                        current = formatted
                        editText.setText(formatted)
                        editText.setSelection(formatted.length)
                    } else {
                        current = ""
                        editText.setText("")
                    }
                    editText.addTextChangedListener(this)
                }
            }
        })
    }

    private fun formatPhoneNumber(digits: String): String {
        val formatted = StringBuilder()
        var i = 0
        if (i < digits.length) {
            formatted.append("(")
            formatted.append(digits.substring(i, minOf(i + 2, digits.length)))
            i += 2
        }
        if (i < digits.length) {
            formatted.append(") ")
            if (digits.length > 10) {
                formatted.append(digits.substring(i, minOf(i + 1, digits.length)))
                i += 1
                formatted.append(" ")
                formatted.append(digits.substring(i, minOf(i + 4, digits.length)))
                i += 4
            } else {
                formatted.append(digits.substring(i, minOf(i + 4, digits.length)))
                i += 4
            }
        }
        if (i < digits.length) {
            formatted.append("-")
            formatted.append(digits.substring(i, minOf(i + 4, digits.length)))
        }
        return formatted.toString()
    }

    // --- NOVO ---
    /**
     * Calcula a idade com base em uma data de nascimento.
     * @param birthDate A data de nascimento em um objeto Calendar.
     * @return A idade em anos.
     */
    private fun calculateAge(birthDate: Calendar): Int {
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)

        // Se o dia de hoje for anterior ao dia do aniversário no ano corrente,
        // significa que a pessoa ainda não fez aniversário este ano.
        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }


    private fun showDatePickerDialog(editText: EditText, fragmentManager: FragmentManager) {
        val utc = TimeZone.getTimeZone("UTC")
        val calendar = Calendar.getInstance(utc)
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        calendar.timeInMillis = today
        calendar.add(Calendar.YEAR, -100)
        val startDate = calendar.timeInMillis
        calendar.timeInMillis = today
        calendar.add(Calendar.YEAR, -30)
        val openAtDate = calendar.timeInMillis

        val constraints = CalendarConstraints.Builder()
            .setStart(startDate)
            .setEnd(today)
            .setOpenAt(openAtDate)
            .build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecione sua data de nascimento")
            .setCalendarConstraints(constraints)
            .setSelection(openAtDate)
            .build()

        // --- MODIFICADO ---
        datePicker.addOnPositiveButtonClickListener { selection ->
            // Configura o calendário com a data selecionada pelo usuário
            val selectedCalendar = Calendar.getInstance(utc)
            selectedCalendar.timeInMillis = selection

            // 1. Formata a data para exibir no campo de nascimento
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = sdf.format(selectedCalendar.time)
            editText.setText(formattedDate)

            // 2. Calcula a idade a partir da data selecionada
            val age = calculateAge(selectedCalendar)

            // 3. Preenche o campo de idade com o resultado
            binding.etIdade.setText(age.toString())
        }

        datePicker.show(fragmentManager, "BIRTH_DATE_PICKER")
    }

    private fun setCurrentDate() {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())
        binding.etData.setText(currentDate)
    }

    private fun validateFields(): Boolean {
        val fieldsToValidate = listOf(
            binding.etData, binding.etEstagiario, binding.etNome,
            binding.etNascimento, binding.etIdade, binding.etTelefone,
            binding.etRenda
        )

        var allFieldsValid = true
        for (field in fieldsToValidate) {
            if (field.text.toString().trim().isEmpty()) {
                field.error = "Campo obrigatório"
                allFieldsValid = false
            } else {
                field.error = null
            }
        }
        return allFieldsValid
    }
}