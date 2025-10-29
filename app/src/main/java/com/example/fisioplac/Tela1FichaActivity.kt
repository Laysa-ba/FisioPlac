package com.example.fisioplac

import android.content.Intent
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

        // --- ADICIONADO ---
        // Chama a função para configurar os listeners de validação em tempo real
        setupValidationListeners()
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
                val intent = Intent(this, Tela2FichaActivity::class.java)
                intent.putExtra("PACIENTE_ID", pacienteId)
                startActivity(intent)
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
                // Limpa o erro se o usuário mudar para "Não"
                binding.tilDiasSemana.error = null
            }
        }
    }

    // --- NOVA FUNÇÃO ---
    /**
     * Configura listeners em cada campo para remover a mensagem de erro
     * assim que o usuário interage com o campo.
     */
    private fun setupValidationListeners() {
        // Lista de todos os TextInputLayouts que são obrigatórios
        val textInputLayouts = listOf(
            binding.tilNome, binding.tilNascimento, binding.tilIdade,
            binding.tilTelefone, binding.tilRenda, binding.tilEstadoCivil,
            binding.tilEscolaridade, binding.tilLocalResidencia, binding.tilMoraCom,
            binding.tilAtividadeSocial, binding.tilDoencas, binding.tilQueixaPrincipal,
            binding.tilDiasSemana
        )

        // Adiciona um listener para cada campo de texto da lista
        textInputLayouts.forEach { til ->
            til.editText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    // Se o campo não estiver mais vazio, remove o erro
                    if (!s.isNullOrBlank()) {
                        til.error = null
                    }
                }
            })
        }

        // Adiciona listeners para os RadioGroups
        binding.rgSexo.setOnCheckedChangeListener { _, _ ->
            binding.tvSexoLabel.error = null
        }

        // Listener já existente que também ajuda na validação
        binding.rgAtividadesFisicas.setOnCheckedChangeListener { _, checkedId ->
            binding.tvAtividadesFisicasLabel.error = null
            if (checkedId == R.id.rb_sim_atividade) {
                binding.diasSemanaContainer.visibility = View.VISIBLE
            } else {
                binding.diasSemanaContainer.visibility = View.GONE
                binding.actvDiasSemana.text.clear()
                binding.tilDiasSemana.error = null // Limpa o erro
            }
        }


        binding.rgFrequenciaSair.setOnCheckedChangeListener { _, _ ->
            binding.tvFrequenciaSairLabel.error = null
        }
    }


    // --- FUNÇÃO ATUALIZADA ---
    /**
     * Valida todos os campos obrigatórios do formulário antes de avançar.
     * Define o erro nos `TextInputLayout` para exibir o ícone de aviso.
     * @return `true` se todos os campos forem válidos, `false` caso contrário.
     */
    private fun validateFields(): Boolean {
        var allFieldsValid = true

        // 1. Validação dos campos de texto (TextInputLayouts)
        val mandatoryTextInputLayouts = mapOf(
            binding.tilNome to binding.etNome,
            binding.tilNascimento to binding.etNascimento,
            binding.tilIdade to binding.etIdade,
            binding.tilTelefone to binding.etTelefone,
            binding.tilRenda to binding.etRenda,
            binding.tilEstadoCivil to binding.actvEstadoCivil,
            binding.tilEscolaridade to binding.actvEscolaridade,
            binding.tilLocalResidencia to binding.actvLocalResidencia,
            binding.tilMoraCom to binding.actvMoraCom,
            binding.tilAtividadeSocial to binding.actvAtividadeSocial,
            binding.tilDoencas to binding.actvDoencas,
            binding.tilQueixaPrincipal to binding.etQueixaPrincipal
        )

        mandatoryTextInputLayouts.forEach { (layout, editText) ->
            if (editText.text.toString().trim().isEmpty()) {
                layout.error = "Campo obrigatório"
                allFieldsValid = false
            } else {
                layout.error = null
            }
        }

        // Validação para campos que não usam TextInputLayout
        if (binding.etData.text.toString().trim().isEmpty()) {
            binding.etData.error = "Campo obrigatório"
            allFieldsValid = false
        } else {
            binding.etData.error = null
        }
        if (binding.etEstagiario.text.toString().trim().isEmpty()) {
            binding.etEstagiario.error = "Campo obrigatório"
            allFieldsValid = false
        } else {
            binding.etEstagiario.error = null
        }

        // 2. Validação dos RadioGroups
        if (binding.rgSexo.checkedRadioButtonId == -1) {
            binding.tvSexoLabel.error = "!" // Mostra um indicador de erro
            allFieldsValid = false
        } else {
            binding.tvSexoLabel.error = null
        }

        if (binding.rgAtividadesFisicas.checkedRadioButtonId == -1) {
            binding.tvAtividadesFisicasLabel.error = "!"
            allFieldsValid = false
        } else {
            binding.tvAtividadesFisicasLabel.error = null
        }

        if (binding.rgFrequenciaSair.checkedRadioButtonId == -1) {
            binding.tvFrequenciaSairLabel.error = "!"
            allFieldsValid = false
        } else {
            binding.tvFrequenciaSairLabel.error = null
        }

        // 3. Validação condicional para "Dias na Semana"
        if (binding.rgAtividadesFisicas.checkedRadioButtonId == R.id.rb_sim_atividade) {
            if (binding.actvDiasSemana.text.toString().trim().isEmpty()) {
                binding.tilDiasSemana.error = "Obrigatório"
                allFieldsValid = false
            } else {
                binding.tilDiasSemana.error = null
            }
        } else {
            binding.tilDiasSemana.error = null
        }

        return allFieldsValid
    }

    // --- Demais funções (sem alterações) ---

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

    private fun calculateAge(birthDate: Calendar): Int {
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)

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

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedCalendar = Calendar.getInstance(utc)
            selectedCalendar.timeInMillis = selection

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = sdf.format(selectedCalendar.time)
            editText.setText(formattedDate)

            val age = calculateAge(selectedCalendar)
            binding.etIdade.setText(age.toString())
        }

        datePicker.show(fragmentManager, "BIRTH_DATE_PICKER")
    }

    private fun setCurrentDate() {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())
        binding.etData.setText(currentDate)
    }
}