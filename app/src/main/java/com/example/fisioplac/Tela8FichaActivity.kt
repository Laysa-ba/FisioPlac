package com.example.fisioplac

import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Tela8FichaActivity : AppCompatActivity() {

    // Componentes da UI
    private lateinit var chronometer: Chronometer
    private lateinit var iniciarButton: Button
    private lateinit var finalizarButton: Button
    private lateinit var resultadoTextView: TextView
    private lateinit var avancarButton: Button

    private var isChronometerRunning = false
    private var lastElapsedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela8_ficha)

        // 1. Inicializa os componentes da UI
        chronometer = findViewById(R.id.chronometer_tugt)
        iniciarButton = findViewById(R.id.btn_iniciar_cronometro)
        finalizarButton = findViewById(R.id.btn_finalizar_cronometro)
        resultadoTextView = findViewById(R.id.text_view_resultado_tugt)
        avancarButton = findViewById(R.id.btn_avancar)

        // 2. Configura os cliques dos botões
        iniciarButton.setOnClickListener {
            startChronometer()
        }

        finalizarButton.setOnClickListener {
            stopChronometer()
        }

        avancarButton.setOnClickListener {
            // TODO: Coloque aqui a lógica para salvar os dados e ir para a próxima tela
        }
    }

    private fun startChronometer() {
        if (!isChronometerRunning) {
            // Reseta o cronômetro para começar do zero
            chronometer.base = SystemClock.elapsedRealtime()
            chronometer.start()

            // Atualiza o estado e a UI
            isChronometerRunning = true
            iniciarButton.text = "Reiniciar"
            finalizarButton.isEnabled = true
            avancarButton.isEnabled = false
            resultadoTextView.visibility = View.GONE
        } else {
            // Se já estiver rodando e o usuário clicar em "Reiniciar"
            chronometer.stop()
            isChronometerRunning = false
            startChronometer() // Chama a si mesmo para reiniciar
        }
    }

    private fun stopChronometer() {
        if (isChronometerRunning) {
            chronometer.stop()
            // Calcula o tempo total em milissegundos
            lastElapsedTime = SystemClock.elapsedRealtime() - chronometer.base

            // Atualiza o estado e a UI
            isChronometerRunning = false
            finalizarButton.isEnabled = false
            avancarButton.isEnabled = true

            // Avalia e mostra o resultado
            displayResult()
        }
    }

    private fun displayResult() {
        // Converte milissegundos para segundos
        val elapsedSeconds = lastElapsedTime / 1000.0

        val resultText = when {
            elapsedSeconds <= 10.0 ->
                "Desempenho normal para adultos saudáveis. Baixo risco de queda."
            elapsedSeconds <= 20.0 ->
                "Normal para idosos frágeis ou com deficiência mas que são independentes na maioria das atividades de vida diária (AVD's). Baixo risco de queda."
            elapsedSeconds <= 29.0 ->
                "Avaliação funcional obrigatória. Abordagem específica para prevenção de queda. Risco de quedas moderado."
            else -> // 30 segundos ou mais
                "Alto risco para quedas."
        }

        // Formata o tempo para exibição com duas casas decimais
        val formattedTime = String.format("%.2f", elapsedSeconds)
        resultadoTextView.text = "Resultado (${formattedTime}s): $resultText"
        resultadoTextView.visibility = View.VISIBLE
    }
}