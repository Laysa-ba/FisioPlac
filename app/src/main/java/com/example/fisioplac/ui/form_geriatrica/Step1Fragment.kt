package com.example.fisioplac.ui.form_geriatrica

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.example.fisioplac.R
import com.example.fisioplac.data.model.GeriatricFicha
import com.example.fisioplac.databinding.FragmentStep1Binding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class Step1Fragment : Fragment() {

    private var _binding: FragmentStep1Binding? = null
    private val binding get() = _binding!!
    private val viewModel: GeriatricFormViewModel by activityViewModels()

    private var isRestoringState = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStep1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDropdownMenus()
        setupClickListeners()
        setupRadioGroupLogic()
        setupPhoneMask()
        setupCurrencyMask()
        setupValidationListeners()
        observeUiState()
        observeFormData()
    }

    private fun observeUiState() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            val isLoading = state.isLoading
            binding.btnAvancar.isEnabled = !isLoading
            binding.btnAvancar.text = if (isLoading) "Salvando..." else "Avançar"

            clearAllErrors()

            if (state.validationErrors.isNotEmpty()) {
                state.validationErrors.forEach { (field, error) ->
                    setErrorForField(field, error)
                }
            }

            state.errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.onErrorMessageShown()
            }
        }
    }

    private fun observeFormData() {
        viewModel.formData.observe(viewLifecycleOwner) { ficha ->
            isRestoringState = true

            setTextIfChanged(binding.etData, ficha.dataAvaliacao)
            setTextIfChanged(binding.etEstagiario, ficha.estagiario)
            setTextIfChanged(binding.etNome, ficha.nome)
            setTextIfChanged(binding.etNascimento, ficha.dataNascimento)
            setTextIfChanged(binding.etIdade, ficha.idade)
            setTextIfChanged(binding.etTelefone, ficha.telefone)
            setTextIfChanged(binding.etRenda, ficha.renda)
            setTextIfChanged(binding.etQueixaPrincipal, ficha.queixaPrincipal)
            setTextIfChanged(binding.etOutrasDoencas, ficha.outrasDoencas)

            setTextIfChanged(binding.actvEstadoCivil, ficha.estadoCivil, true)
            setTextIfChanged(binding.actvEscolaridade, ficha.escolaridade, true)
            setTextIfChanged(binding.actvLocalResidencia, ficha.localResidencia, true)
            setTextIfChanged(binding.actvMoraCom, ficha.moraCom, true)
            setTextIfChanged(binding.actvAtividadeSocial, ficha.atividadeSocial, true)
            setTextIfChanged(binding.actvDoencas, ficha.doencasAssociadas, true)
            setTextIfChanged(binding.actvDiasSemana, ficha.diasPorSemana, true)

            setCheckedRadioButton(binding.rgSexo, ficha.sexo)
            setCheckedRadioButton(binding.rgAtividadesFisicas, ficha.praticaAtividadeFisica)
            setCheckedRadioButton(binding.rgFrequenciaSair, ficha.frequenciaSair)

            binding.diasSemanaContainer.visibility = if (ficha.praticaAtividadeFisica == "Sim") View.VISIBLE else View.GONE

            isRestoringState = false
        }
    }

    private fun setTextIfChanged(editText: EditText, newText: String?, isAutoComplete: Boolean = false) {
        if (newText == null) return
        if (editText.text.toString() != newText) {
            editText.setText(newText)
            if (isAutoComplete && !newText.isNullOrBlank() && editText is AutoCompleteTextView) {
                editText.dismissDropDown()
            }
        }
    }

    private fun setCheckedRadioButton(radioGroup: RadioGroup, textToMatch: String?) {
        if (textToMatch == null) {
            radioGroup.clearCheck()
            return
        }
        for (i in 0 until radioGroup.childCount) {
            val view = radioGroup.getChildAt(i)
            if (view is RadioButton) {
                if (view.text.toString().equals(textToMatch, ignoreCase = true)) {
                    if (radioGroup.checkedRadioButtonId != view.id) {
                        radioGroup.check(view.id)
                    }
                    return
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnAvancar.setOnClickListener {
            if(isRestoringState) return@setOnClickListener
            val data = gatherDataFromView()
            viewModel.onStep1NextClicked(data)
        }

        binding.etNascimento.setOnClickListener {
            showDatePickerDialogNascimento(binding.etNascimento, parentFragmentManager)
        }

        binding.etData.setOnClickListener {
            showDatePickerDialogAvaliacao(binding.etData, parentFragmentManager)
        }
    }

    private fun getSelectedRadioButtonText(checkedId: Int): String? {
        if (checkedId == -1) return null
        return view?.findViewById<RadioButton>(checkedId)?.text?.toString()
    }

    private fun gatherDataFromView(): GeriatricFicha {
        val currentData = viewModel.formData.value!!

        val sexo = getSelectedRadioButtonText(binding.rgSexo.checkedRadioButtonId)
        val praticaAtividade = getSelectedRadioButtonText(binding.rgAtividadesFisicas.checkedRadioButtonId)
        val freqSair = getSelectedRadioButtonText(binding.rgFrequenciaSair.checkedRadioButtonId)

        return currentData.copy(
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

    // --- Funções de UI (Copiadas da sua Activity) ---

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
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, items)
        view.setAdapter(adapter)
        view.setOnItemClickListener { _, _, _, _ -> binding.main.requestFocus() }
        view.setOnDismissListener { binding.main.requestFocus() }
    }

    private fun setupRadioGroupLogic() {
        binding.diasSemanaContainer.visibility = View.GONE
        // O listener principal do RadioGroup foi movido para 'setupValidationListeners'
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
                    if (isRestoringState) return
                    if (!s.isNullOrBlank()) til.error = null
                }
            })
        }
        binding.rgSexo.setOnCheckedChangeListener { _, _ -> if(isRestoringState) return@setOnCheckedChangeListener; binding.tvSexoLabel.error = null }
        binding.rgAtividadesFisicas.setOnCheckedChangeListener { _, checkedId ->
            if(isRestoringState) return@setOnCheckedChangeListener
            binding.tvAtividadesFisicasLabel.error = null
            if (checkedId == R.id.rb_sim_atividade) {
                binding.diasSemanaContainer.visibility = View.VISIBLE
            } else {
                binding.diasSemanaContainer.visibility = View.GONE
                binding.actvDiasSemana.text.clear()
                binding.tilDiasSemana.error = null
            }
        }
        binding.rgFrequenciaSair.setOnCheckedChangeListener { _, _ -> if(isRestoringState) return@setOnCheckedChangeListener; binding.tvFrequenciaSairLabel.error = null }
    }

    private fun setupPhoneMask() {
        val phoneEditText = binding.etTelefone
        phoneEditText.addTextChangedListener(object : TextWatcher {
            private var isFormatting: Boolean = false
            private var deletingHyphen: Boolean = false
            private var hyphenStart: Int = 0
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                if (isRestoringState) return
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
                if (isRestoringState) return
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
                if (isRestoringState) return
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

    private fun showDatePickerDialogNascimento(editText: EditText, fragmentManager: FragmentManager) {
        val utc = TimeZone.getTimeZone("UTC") // <-- Fuso horário UTC
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

            // --- CORREÇÃO DO FUSO HORÁRIO ---
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.timeZone = utc // <-- Força o SDF a usar UTC
            val formattedDate = sdf.format(selectedCalendar.time)
            // --- FIM DA CORREÇÃO ---

            editText.setText(formattedDate)
            val age = calculateAge(selectedCalendar)
            binding.etIdade.setText(age.toString())
        }
        datePicker.show(fragmentManager, "BIRTH_DATE_PICKER")
    }

    private fun showDatePickerDialogAvaliacao(editText: EditText, fragmentManager: FragmentManager) {
        val utc = TimeZone.getTimeZone("UTC") // <-- Fuso horário UTC
        val calendar = Calendar.getInstance()
        val today = calendar.timeInMillis

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecione a Data da Avaliação")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds()) // <-- Usa UTC
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedCalendar = Calendar.getInstance(utc)
            selectedCalendar.timeInMillis = selection

            // --- CORREÇÃO DO FUSO HORÁRIO ---
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.timeZone = utc // <-- Força o SDF a usar UTC
            val formattedDate = sdf.format(selectedCalendar.time)
            // --- FIM DA CORREÇÃO ---

            editText.setText(formattedDate)
        }
        datePicker.show(fragmentManager, "AVALIACAO_DATE_PICKER")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // --- FUNÇÕES DE ERRO RESTAURADAS ---

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