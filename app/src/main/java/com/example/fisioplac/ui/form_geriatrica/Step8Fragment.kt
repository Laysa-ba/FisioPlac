package com.example.fisioplac.ui.form_geriatrica

import android.graphics.Typeface
import android.os.Bundle
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.fisioplac.R
import com.example.fisioplac.data.model.GeriatricFicha
import com.example.fisioplac.databinding.FragmentStep8Binding

class Step8Fragment : Fragment(), FormStepFragment {

    private var _binding: FragmentStep8Binding? = null
    private val binding get() = _binding!!

    // ViewModel em INGLÊS
    private val viewModel: GeriatricFormViewModel by activityViewModels()

    private var isChronometerRunning = false
    private var lastElapsedTime: Long = 0

    // Variáveis para guardar o resultado para o ViewModel
    private var tugtTempo: String = ""
    private var tugtDiagnostico: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStep8Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFormattedInstructions()
        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        // (Assumindo IDs em português do XML que você vai criar)
        binding.btnIniciarCronometro.setOnClickListener {
            startChronometer()
        }

        binding.btnFinalizarCronometro.setOnClickListener {
            stopChronometer()
        }

        // Navegação (VM em INGLÊS, IDs em PORTUGUÊS)
        binding.botaoProximo.setOnClickListener {
            // Verifica se o teste foi realizado
            if (lastElapsedTime == 0L) {
                Toast.makeText(requireContext(), "Por favor, realize o teste primeiro.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val data = collectDataFromUi()
            viewModel.onStep8NextClicked(data) // VM em INGLÊS
        }
    }

    private fun setupObservers() {
        // VM em INGLÊS
        viewModel.formData.observe(viewLifecycleOwner) { ficha ->
            if (ficha != null) {
                populateUi(ficha)
            }
        }

        viewModel.uiState.observe(viewLifecycleOwner, Observer { state ->
            binding.progressBar.isVisible = state.isLoading
            // Só pode concluir se o teste foi feito E não está carregando
            binding.botaoProximo.isEnabled = !state.isLoading && lastElapsedTime > 0
            binding.btnIniciarCronometro.isEnabled = !state.isLoading
            // O botão finalizar SÓ é habilitado quando o cronômetro está rodando
            binding.btnFinalizarCronometro.isEnabled = !state.isLoading && isChronometerRunning

            binding.botaoProximo.text = if (state.isLoading) "Salvando..." else "Avançar"

            state.errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.onErrorMessageShown() // VM em INGLÊS
            }
        })
    }

    /**
     * Preenche a UI com os dados do ViewModel.
     * (Campos do Modelo em PORTUGUÊS)
     */
    private fun populateUi(ficha: GeriatricFicha) {
        // IDs em PORTUGUÊS, Campos do Modelo em PORTUGUÊS
        if (ficha.tugtTempo.isNotBlank()) {
            // Salva os valores para o estado do Fragment
            this.tugtTempo = ficha.tugtTempo
            this.tugtDiagnostico = ficha.tugtDiagnostico

            // Tenta extrair o valor longo do tempo
            try {
                this.lastElapsedTime = (ficha.tugtTempo.replace("s", "").toDoubleOrNull() ?: 0.0).toLong() * 1000
            } catch (e: NumberFormatException) {
                this.lastElapsedTime = 0
            }

            binding.textViewResultadoTugt.text = "Resultado (${ficha.tugtTempo}): ${ficha.tugtDiagnostico}"
            binding.textViewResultadoTugt.visibility = View.VISIBLE
            binding.botaoProximo.isEnabled = true // Habilita o botão Concluir
        } else {
            // Garante que o estado inicial esteja limpo
            this.lastElapsedTime = 0
            this.tugtTempo = ""
            this.tugtDiagnostico = ""
            binding.textViewResultadoTugt.visibility = View.GONE
            binding.botaoProximo.isEnabled = false // Desabilita o botão Concluir
        }
    }

    /**
     * Coleta todos os dados da UI.
     * (Campos do Modelo em PORTUGUÊS)
     */
    override fun collectDataFromUi(): GeriatricFicha {
        val currentFicha = viewModel.formData.value!!
        return currentFicha.copy(
            tugtTempo = this.tugtTempo,
            tugtDiagnostico = this.tugtDiagnostico
        )
    }

    // --- Lógica do Cronômetro (adaptada da sua Activity) ---

    private fun startChronometer() {
        if (!isChronometerRunning) {
            binding.cronometroTugt.base = SystemClock.elapsedRealtime()
            binding.cronometroTugt.start()

            isChronometerRunning = true
            binding.btnIniciarCronometro.text = "Reiniciar"
            binding.btnFinalizarCronometro.isEnabled = true
            binding.botaoProximo.isEnabled = false // Não pode concluir enquanto o teste roda
            binding.textViewResultadoTugt.visibility = View.GONE
        } else {
            // Lógica de "Reiniciar"
            binding.cronometroTugt.stop()
            isChronometerRunning = false
            startChronometer()
        }
    }

    private fun stopChronometer() {
        if (isChronometerRunning) {
            binding.cronometroTugt.stop()
            lastElapsedTime = SystemClock.elapsedRealtime() - binding.cronometroTugt.base

            isChronometerRunning = false
            binding.btnFinalizarCronometro.isEnabled = false
            binding.botaoProximo.isEnabled = true // Agora pode concluir

            displayResult()
        }
    }

    private fun displayResult() {
        val elapsedSeconds = lastElapsedTime / 1000.0

        val resultText = when {
            elapsedSeconds <= 10.0 -> "Desempenho normal para adultos saudáveis. Baixo risco de queda."
            elapsedSeconds <= 20.0 -> "Normal para idosos frágeis ou com deficiência mas que são independentes na maioria das atividades de vida diária (AVD's). Baixo risco de queda."
            elapsedSeconds <= 29.0 -> "Avaliação funcional obrigatória. Abordagem específica para prevenção de queda. Risco de quedas moderado."
            else -> "Alto risco para quedas."
        }

        val formattedTime = String.format("%.2f", elapsedSeconds)

        // Salva nas variáveis do Fragment para o collectDataFromUi
        this.tugtTempo = "${formattedTime}s"
        this.tugtDiagnostico = resultText

        binding.textViewResultadoTugt.text = "Resultado (${formattedTime}s): $resultText"
        binding.textViewResultadoTugt.visibility = View.VISIBLE

        // Muda a cor do botão Concluir (opcional, baseado no seu XML original)
        binding.botaoProximo.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.verde)
    }

    private fun setFormattedInstructions() {
        val fullText = getString(R.string.tugt_instructions) // Você precisará adicionar esta string
        val spannable = SpannableString(fullText)
        val targetWord = "Instruções:"
        val startIndex = fullText.indexOf(targetWord)

        if (startIndex != -1) {
            val endIndex = startIndex + targetWord.length
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.laranja)),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        binding.textViewInstructions.text = spannable
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}