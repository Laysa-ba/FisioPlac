package com.example.fisioplac.ui.form_geriatrica

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager // <-- Importação correta
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.fisioplac.R
import com.example.fisioplac.databinding.FragmentTela1FichaBinding // <-- Use o XML do Fragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class Tela1FichaFragment : Fragment() {

    private var _binding: FragmentTela1FichaBinding? = null
    private val binding get() = _binding!!

    // 1. ACESSA O VIEWMODEL DA ACTIVITY "MÃE"
    private val viewModel: GeriatricFormViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTela1FichaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 2. CHAMA AS FUNÇÕES DE SETUP
        setupDropdownMenus()
        setupClickListeners()
        setupValidationAndEventForwarding()
        setupPhoneMask()
        setupCurrencyMask()

        // 3. OBSERVA O ESTADO DO VIEWMODEL
        observeUiState()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->

                // --- Barra de Progresso ---
                binding.fichaProgressBar.max = state.totalPassos
                binding.fichaProgressBar.progress = state.passoAtual

                // --- Preenchimento dos Campos ---
                if (binding.etData.text.toString() != state.dataAvaliacao) binding.etData.setText(state.dataAvaliacao)
                if (binding.etEstagiario.text.toString() != state.estagiario) binding.etEstagiario.setText(state.estagiario)
                if (binding.etNome.text.toString() != state.nome) binding.etNome.setText(state.nome)
                if (binding.etNascimento.text.toString() != state.dataNascimento) binding.etNascimento.setText(state.dataNascimento)
                if (binding.etIdade.text.toString() != state.idade) binding.etIdade.setText(state.idade)
                if (binding.etTelefone.text.toString() != state.telefone) binding.etTelefone.setText(state.telefone)
                if (binding.etRenda.text.toString() != state.renda) binding.etRenda.setText(state.renda)
                if (binding.etQueixaPrincipal.text.toString() != state.queixaPrincipal) binding.etQueixaPrincipal.setText(state.queixaPrincipal)
                if (binding.etOutrasDoencas.text.toString() != state.outrasDoencas) binding.etOutrasDoencas.setText(state.outrasDoencas)

                // --- Lógica Condicional (Radio Groups) ---
                binding.diasSemanaContainer.visibility = if (state.praticaAtividadeFisica == "Sim") View.VISIBLE else View.GONE

                // --- Exibição de Erros ---
                val errors = state.validationErrors
                binding.etData.error = errors["dataAvaliacao"]
                binding.etEstagiario.error = errors["estagiario"]
                binding.tilNome.error = errors["nome"]
                binding.tilNascimento.error = errors["dataNascimento"]
                binding.tilIdade.error = errors["idade"]
                binding.tilTelefone.error = errors["telefone"]
                binding.tilRenda.error = errors["renda"]
                binding.tilQueixaPrincipal.error = errors["queixaPrincipal"]
                binding.tilEstadoCivil.error = errors["estadoCivil"]
                binding.tilEscolaridade.error = errors["escolaridade"]
                binding.tilLocalResidencia.error = errors["localResidencia"]
                binding.tilMoraCom.error = errors["moraCom"]
                binding.tilAtividadeSocial.error = errors["atividadeSocial"]
                binding.tilDoencas.error = errors["doencasAssociadas"]
                binding.tilDiasSemana.error = errors["diasPorSemana"]
                // Labels de RadioGroup
                binding.tvSexoLabel.error = if (errors.containsKey("sexo")) "!" else null
                binding.tvAtividadesFisicasLabel.error = if (errors.containsKey("praticaAtividadeFisica")) "!" else null
                binding.tvFrequenciaSairLabel.error = if (errors.containsKey("frequenciaSair")) "!" else null
            }
        }
    }

    /**
     * Configura listeners para AVISAR o ViewModel sobre as mudanças
     */
    private fun setupValidationAndEventForwarding() {
        // Campos de Texto
        binding.etData.doOnTextChanged { text, _, _, _ -> viewModel.onDataAvaliacaoChanged(text.toString()) }
        binding.etEstagiario.doOnTextChanged { text, _, _, _ -> viewModel.onEstagiarioChanged(text.toString()) }
        binding.etNome.doOnTextChanged { text, _, _, _ -> viewModel.onNomeChanged(text.toString()) }
        binding.etTelefone.doOnTextChanged { text, _, _, _ -> viewModel.onTelefoneChanged(text.toString()) }
        binding.etRenda.doOnTextChanged { text, _, _, _ -> viewModel.onRendaChanged(text.toString()) }
        binding.etQueixaPrincipal.doOnTextChanged { text, _, _, _ -> viewModel.onQueixaPrincipalChanged(text.toString()) }
        binding.etOutrasDoencas.doOnTextChanged { text, _, _, _ -> viewModel.onOutrasDoencasChanged(text.toString()) }

        // RadioGroups
        binding.rgSexo.setOnCheckedChangeListener { _, checkedId ->
            // Ajuste os IDs (ex: R.id.rb_masculino) para os seus IDs do XML
            val sexo = if (checkedId == R.id.rb_masculino) "Masculino" else "Feminino"
            viewModel.onSexoChanged(sexo)
        }
        binding.rgAtividadesFisicas.setOnCheckedChangeListener { _, checkedId ->
            val pratica = if (checkedId == R.id.rb_sim_atividade) "Sim" else "Não"
            viewModel.onPraticaAtividadeFisicaChanged(pratica) // <-- NOME CORRIGIDO
        }
        binding.rgFrequenciaSair.setOnCheckedChangeListener { _, checkedId ->
            // Ajuste os IDs (ex: R.id.rb_diariamente) para os seus IDs do XML
            val freq = when (checkedId) {
                R.id.rb_diariamente -> "Diariamente"
                R.id.rb_pouca_frequencia -> "Pouca Frequência"
                else -> ""
            }
            viewModel.onFrequenciaSairChanged(freq)
        }
    }

    private fun setupDropdownMenus() {
        // Mapa que liga o (View) -> (Array de Strings, Função do ViewModel)
        val dropdownMap = mapOf(
            binding.actvEstadoCivil to (R.array.opcoes_estado_civil to { s: String -> viewModel.onEstadoCivilChanged(s) }),
            binding.actvEscolaridade to (R.array.opcoes_escolaridade to { s: String -> viewModel.onEscolaridadeChanged(s) }),
            binding.actvLocalResidencia to (R.array.opcoes_local_residencia to { s: String -> viewModel.onLocalResidenciaChanged(s) }),
            binding.actvMoraCom to (R.array.opcoes_mora_com to { s: String -> viewModel.onMoraComChanged(s) }),
            binding.actvDiasSemana to (R.array.opcoes_dias_semana to { s: String -> viewModel.onDiasPorSemanaChanged(s) }),
            binding.actvAtividadeSocial to (R.array.opcoes_atividade_social to { s: String -> viewModel.onAtividadeSocialChanged(s) }),
            binding.actvDoencas to (R.array.opcoes_doencas to { s: String -> viewModel.onDoencasAssociadasChanged(s) })
        )

        dropdownMap.forEach { (view, data) ->
            val (arrayId, onSelected) = data
            val items = resources.getStringArray(arrayId)
            val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, items)
            view.setAdapter(adapter)

            view.setOnItemClickListener { _, _, position, _ ->
                onSelected(items[position]) // Avisa o ViewModel
                binding.main.requestFocus() // Tira o foco
            }
            view.setOnDismissListener { binding.main.requestFocus() }
        }
    }

    private fun setupClickListeners() {
        binding.backArrow.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed() // Jeito certo de voltar
        }

        binding.btnAvancar.setOnClickListener {
            viewModel.onAvancarTela1Clicked() // ViewModel decide se navega
        }

        binding.etNascimento.setOnClickListener {
            showDatePickerDialog(binding.etNascimento, parentFragmentManager) // <-- MUDANÇA
        }
    }

    // --- FUNÇÕES HELPER (Copie TODAS elas para dentro da classe) ---

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

            val age = calculateAge(selectedCalendar)

            // AVISA O VIEWMODEL
            viewModel.onDataNascimentoChanged(formattedDate, age.toString())
        }
        datePicker.show(fragmentManager, "BIRTH_DATE_PICKER")
    }

    private fun setCurrentDate() {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())
        viewModel.onDataAvaliacaoChanged(currentDate) // Avisa o ViewModel
    }

    // --- CICLO DE VIDA ---
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Limpa a referência do binding
    }
}