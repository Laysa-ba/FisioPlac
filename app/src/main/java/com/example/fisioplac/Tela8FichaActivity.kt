package com.example.fisioplac

import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat

class Tela8FichaActivity : AppCompatActivity() {

    // Componentes da UI
    private lateinit var backButton: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var chronometer: Chronometer
    private lateinit var iniciarButton: Button
    private lateinit var finalizarButton: Button
    private lateinit var resultadoTextView: TextView
    private lateinit var avancarButton: Button
    private lateinit var instructionsTextView: TextView // NOVA VARIÁVEL

    private var isChronometerRunning = false
    private var lastElapsedTime: Long = 0

    private val totalPassosDaFicha = 13
    private val passoAtual = 8

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela8_ficha)

        // 1. Inicializa os componentes da UI
        backButton = findViewById(R.id.btn_back)
        progressBar = findViewById(R.id.ficha_progress_bar)
        chronometer = findViewById(R.id.chronometer_tugt)
        iniciarButton = findViewById(R.id.btn_iniciar_cronometro)
        finalizarButton = findViewById(R.id.btn_finalizar_cronometro)
        resultadoTextView = findViewById(R.id.text_view_resultado_tugt)
        avancarButton = findViewById(R.id.btn_avancar)
        instructionsTextView = findViewById(R.id.text_view_instructions) // INICIALIZA O TEXTVIEW

        // 2. Atualiza o progresso da barra
        updateProgressBar(passoAtual, totalPassosDaFicha)

        // 3. DEFINE O TEXTO DAS INSTRUÇÕES COM A COR (NOVA SEÇÃO)
        setFormattedInstructions()

        // 4. Configura os cliques dos botões
        backButton.setOnClickListener {
            finish()
        }

        // ... O resto do seu código de cliques continua o mesmo ...
        iniciarButton.setOnClickListener {
            startChronometer()
        }

        finalizarButton.setOnClickListener {
            stopChronometer()
            // Muda a cor de fundo (backgroundTint) do botão para verde
            avancarButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.verde)
        }

        avancarButton.setOnClickListener {
            // TODO: Lógica para salvar e avançar
        }
    }

    /**
     * NOVA FUNÇÃO: Pega a string com HTML, processa e aplica no TextView.
     */
    private fun setFormattedInstructions() {
        // 1. Pega a string pura do strings.xml
        val fullText = getString(R.string.tugt_instructions)

        // 2. Cria um "SpannableString", que permite formatar partes do texto
        val spannable = SpannableString(fullText)

        // 3. Define a palavra que queremos formatar
        val targetWord = "Instruções:"

        // 4. Encontra a posição inicial e final da palavra no texto
        val startIndex = fullText.indexOf(targetWord)
        val endIndex = startIndex + targetWord.length

        // Garante que a palavra foi encontrada antes de tentar formatar
        if (startIndex != -1) {
            // Aplica a cor Laranja
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(this, R.color.laranja)),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Aplica o estilo Negrito (Bold)
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // 5. Define o texto já formatado no TextView (ou JustifiedTextView)
        instructionsTextView.text = spannable
    }

    private fun updateProgressBar(currentStep: Int, totalSteps: Int) {
        val progress = (currentStep * 100) / totalSteps
        progressBar.progress = progress
    }

    private fun startChronometer() {
        if (!isChronometerRunning) {
            chronometer.base = SystemClock.elapsedRealtime()
            chronometer.start()

            isChronometerRunning = true
            iniciarButton.text = "Reiniciar"
            finalizarButton.isEnabled = true
            avancarButton.isEnabled = false
            resultadoTextView.visibility = View.GONE
        } else {
            chronometer.stop()
            isChronometerRunning = false
            startChronometer()
        }
    }

    private fun stopChronometer() {
        if (isChronometerRunning) {
            chronometer.stop()
            lastElapsedTime = SystemClock.elapsedRealtime() - chronometer.base

            isChronometerRunning = false
            finalizarButton.isEnabled = false
            avancarButton.isEnabled = true

            displayResult()
        }
    }

    private fun displayResult() {
        val elapsedSeconds = lastElapsedTime / 1000.0

        val resultText = when {
            elapsedSeconds <= 10.0 ->
                "Desempenho normal para adultos saudáveis. Baixo risco de queda."
            elapsedSeconds <= 20.0 ->
                "Normal para idosos frágeis ou com deficiência mas que são independentes na maioria das atividades de vida diária (AVD's). Baixo risco de queda."
            elapsedSeconds <= 29.0 ->
                "Avaliação funcional obrigatória. Abordagem específica para prevenção de queda. Risco de quedas moderado."
            else -> "Alto risco para quedas."
        }

        val formattedTime = String.format("%.2f", elapsedSeconds)
        resultadoTextView.text = "Resultado (${formattedTime}s): $resultText"
        resultadoTextView.visibility = View.VISIBLE
    }
}