package com.example.fisioplac.ui.form_geriatrica

import com.example.fisioplac.data.model.GeriatricFicha

/**
 * Interface que define um "contrato" para todos os fragmentos
 * que participam no formulário de múltiplos passos.
 * Garante que a Activity possa pedir os dados ao fragmento atual
 * antes de navegar.
 */
interface FormStepFragment {
    /**
     * Coleta todos os dados da UI do fragmento e retorna
     * um objeto GeriatricFicha atualizado.
     */
    fun collectDataFromUi(): GeriatricFicha
}