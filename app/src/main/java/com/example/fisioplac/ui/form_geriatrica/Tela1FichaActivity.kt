package com.example.fisioplac.ui.form_geriatrica // <-- PACOTE ATUALIZADO

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.fisioplac.R // <-- Import do R
import com.example.fisioplac.TOTAL_FICHA_STEPS
import com.example.fisioplac.data.model.GeriatricFicha
import com.example.fisioplac.databinding.ActivityTela1FichaBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class Tela1FichaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTela1FichaBinding
    private var pacienteId: String? = null

    // 1. Obter a instância do ViewModel
    private val viewModel: GeriatricFormViewModel by viewModels()

    private val PASSO_ATUAL = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTela1FichaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fichaProgressBar.max = TOTAL_FICHA_STEPS
        binding.fichaProgressBar.progress = PASSO_ATUAL

        // 2. Recebe o ID do Paciente
        pacienteId = intent.getStringExtra("PACIENTE_ID")
        val pacienteNome = intent.getStringExtra("PACIENTE_NOME")

        if (pacienteId == null) {
            Toast.makeText(this, "Erro: ID do Paciente não encontrado.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        if (pacienteNome != null) {
            binding.etNome.setText(pacienteNome)
        }

        // 3. Funções de UI permanecem na Activity
        setupDropdownMenus()
        setupClickListeners()
        setupRadioGroupLogic()
        setCurrentDate()
        setupPhoneMask()
        setupCurrencyMask()
        setupValidationListeners()

        // 4. Observa o estado do ViewModel
        observeUiState()
    }

    /**
     * Observa o estado da UI (loading, erros, sucesso) vindo do ViewModel
     */
    private fun observeUiState() {
        viewModel.uiState.observe(this) { state ->
            // Controla o Loading
            binding.btnAvancar.isEnabled = !state.isLoading
            binding.btnAvancar.text = if (state.isLoading) "Salvando..." else "Concluir"

            clearAllErrors() // Limpa erros antigos

            // Mostra erros de validação
            state.validationErrors.forEach { (field, error) ->
                setErrorForField(field, error)
            }

            // Mostra erros gerais (como falha no save ou validação)
            state.errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.onErrorMessageShown()
            }

            // Sucesso!
            if (state.isSaveSuccess) {
                Toast.makeText(this, "Ficha salva com sucesso!", Toast.LENGTH_LONG).show()
                finish() // Fecha a tela do formulário
            }
        }
    }

    /**
     * Coleta todos os dados da UI e os coloca em um objeto GeriatricFicha.
     */
    private fun gatherDataFromView(): GeriatricFicha {
        // Helper para pegar texto de RadioGroups
        val sexo = findViewById<RadioButton>(binding.rgSexo.checkedRadioButtonId)?.text.toString()
        val praticaAtividade = findViewById<RadioButton>(binding.rgAtividadesFisicas.checkedRadioButtonId)?.text.toString()
        val freqSair = findViewById<RadioButton>(binding.rgFrequenciaSair.checkedRadioButtonId)?.text.toString()

        return GeriatricFicha(
            dataAvaliacao = binding.etData.text.toString(),
            estagiario = binding.etEstagiario.text.toString(),
            nome = binding.etNome.text.toString(),
            dataNascimento = binding.etNascimento.text.toString(),
            idade = binding.etIdade.text.toString(),
            sexo = sexo,
            telefone = binding.etTelefone.text.toString(),
            estadoCivil = binding.actvEstadoCivil.text.toString(),
            escolaridade = binding.actvEscolaridade.text.toString(),
            localResidencia = binding.actvLocalResidencia.text.toString(),
            moraCom = binding.actvMoraCom.text.toString(),
            renda = binding.etRenda.text.toString(),
            queixaPrincipal = binding.etQueixaPrincipal.text.toString(),
            outrasDoencas = binding.etOutrasDoencas.text.toString(),
            praticaAtividadeFisica = praticaAtividade,
            diasPorSemana = binding.actvDiasSemana.text.toString(),
            frequenciaSair = freqSair,
            atividadeSocial = binding.actvAtividadeSocial.text.toString(),
            doencasAssociadas = binding.actvDoencas.text.toString()
        )
    }

    // --- LÓGICA DE UI (MÁSCARAS, LISTENERS, DATEPICKER) ---
    // (A maior parte do seu código original permanece aqui, sem alterações)

    private fun setupClickListeners() {
        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.btnAvancar.setOnClickListener {
            val data = gatherDataFromView()
            // Envia os dados E o pacienteId para o ViewModel
            viewModel.onConcluirClicked(pacienteId!!, data)
        }

        binding.etNascimento.setOnClickListener {
            showDatePickerDialog(binding.etNascimento, supportFragmentManager)
        }
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
        dropdownMap.forEach { (view, id) -> setupAutoCompleteTextView(view, id) }
    }

    private fun setupAutoCompleteTextView(view: AutoCompleteTextView, arrayResourceId: Int) {
        val items = resources.getStringArray(arrayResourceId)
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, items)
        view.setAdapter(adapter)
        view.setOnItemClickListener { _, _, _, _ -> binding.main.requestFocus() }
        view.setOnDismissListener { binding.main.requestFocus() }
    }

    private fun setupRadioGroupLogic() {
        binding.diasSemanaContainer.visibility = View.GONE
        binding.rgAtividadesFisicas.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_sim_atividade) {
                binding.diasSemanaContainer.visibility = View.VISIBLE
            } else {
                binding.diasSemanaContainer.visibility = View.GONE
                binding.actvDiasSemana.text.clear()
                binding.tilDiasSemana.error = null
            }
        }
    }

    private fun setupValidationListeners() {
        val textInputLayouts = listOf(
            binding.tilNome, binding.tilNascimento, binding.tilIdade,
            binding.tilTelefone, binding.tilRenda, binding.tilEstadoCivil,
            binding.tilEscolaridade, binding.tilLocalResidencia, binding.tilMoraCom,
            binding.tilAtividadeSocial, binding.tilDoencas, binding.tilQueixaPrincipal,
            binding.tilDiasSemana
        )
        textInputLayouts.forEach { til ->
            til.editText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (!s.isNullOrBlank()) til.error = null
                }
            })
        }
        binding.rgSexo.setOnCheckedChangeListener { _, _ -> binding.tvSexoLabel.error = null }
        binding.rgAtividadesFisicas.setOnCheckedChangeListener { _, checkedId ->
            binding.tvAtividadesFisicasLabel.error = null
            if (checkedId == R.id.rb_sim_atividade) {
                binding.diasSemanaContainer.visibility = View.VISIBLE
            } else {
                binding.diasSemanaContainer.visibility = View.GONE
                binding.actvDiasSemana.text.clear()
                binding.tilDiasSemana.error = null
            }
        }
        binding.rgFrequenciaSair.setOnCheckedChangeListener { _, _ -> binding.tvFrequenciaSairLabel.error = null }
    }

    private fun setupPhoneMask() {
        // (Seu código original da máscara de telefone - o mesmo que você forneceu)
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
        // (Seu código original da máscara de Renda)
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
        // (Seu código original)
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
        // (Seu código original)
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }

    private fun showDatePickerDialog(editText: EditText, fragmentManager: FragmentManager) {
        // (Seu código original)
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

    // --- Funções de UI para Erros ---

    /**
     * Define o erro no TextInputLayout ou TextView correto com base no nome do campo.
     */
    private fun setErrorForField(fieldName: String, error: String) {
        val errorMsg = if (error.isNotBlank()) error else null
        when (fieldName) {
            "dataAvaliacao" -> binding.etData.error = errorMsg
            "estagiario" -> binding.etEstagiario.error = errorMsg
            "nome" -> binding.tilNome.error = errorMsg
            "dataNascimento" -> binding.tilNascimento.error = errorMsg
            "idade" -> binding.tilIdade.error = errorMsg
            "sexo" -> binding.tvSexoLabel.error = "!"
            "telefone" -> binding.tilTelefone.error = errorMsg
            "estadoCivil" -> binding.tilEstadoCivil.error = errorMsg
            "escolaridade" -> binding.tilEscolaridade.error = errorMsg
            "localResidencia" -> binding.tilLocalResidencia.error = errorMsg
            "moraCom" -> binding.tilMoraCom.error = errorMsg
            "renda" -> binding.tilRenda.error = errorMsg
            "queixaPrincipal" -> binding.tilQueixaPrincipal.error = errorMsg
            "praticaAtividadeFisica" -> binding.tvAtividadesFisicasLabel.error = "!"
            "frequenciaSair" -> binding.tvFrequenciaSairLabel.error = "!"
            "diasPorSemana" -> binding.tilDiasSemana.error = errorMsg
            "atividadeSocial" -> binding.tilAtividadeSocial.error = errorMsg
            "doencasAssociadas" -> binding.tilDoencas.error = errorMsg
        }
    }

    /**
     * Limpa todas as mensagens de erro da tela.
     */
    private fun clearAllErrors() {
        binding.etData.error = null
        binding.etEstagiario.error = null
        binding.tilNome.error = null
        binding.tilNascimento.error = null
        binding.tilIdade.error = null
        binding.tvSexoLabel.error = null
        binding.tilTelefone.error = null
        binding.tilEstadoCivil.error = null
        binding.tilEscolaridade.error = null
        binding.tilLocalResidencia.error = null
        binding.tilMoraCom.error = null
        binding.tilRenda.error = null
        binding.tilQueixaPrincipal.error = null
        binding.tvAtividadesFisicasLabel.error = null
        binding.tvFrequenciaSairLabel.error = null
        binding.tilDiasSemana.error = null
        binding.tilAtividadeSocial.error = null
        binding.tilDoencas.error = null
    }
}