package com.example.fisioplac.data.model

/**
 * Representa um único medicamento.
 * (Movido de Tela2FichaActivity para seu próprio arquivo)
 */
data class Medicamento(
    val nome: String = "",
    val tempoDeUso: String = "",
    val comoUsar: String = "",
    var isExpanded: Boolean = false // Usado apenas pela UI do Adapter
)