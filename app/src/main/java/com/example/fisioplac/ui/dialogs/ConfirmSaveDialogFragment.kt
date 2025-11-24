package com.example.fisioplac.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.fisioplac.databinding.DialogConfirmSaveBinding

class ConfirmSaveDialogFragment : DialogFragment() {

    private var _binding: DialogConfirmSaveBinding? = null
    private val binding get() = _binding!!

    // A LÓGICA FOI MOVIDA DE 'onStart' PARA 'onCreateView'
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogConfirmSaveBinding.inflate(inflater, container, false)

        // --- A LÓGICA DA JANELA VEIO PARA CÁ ---
        dialog?.window?.apply {
            // 1. Remove a barra de título (DEVE SER CHAMADO ANTES DE TUDO)
            requestFeature(Window.FEATURE_NO_TITLE)

            // 2. Remove o fundo padrão para vermos o CardView
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // 3. Ativa o escurecimento (dim) do fundo
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

            // 4. Define a intensidade do escurecimento (0.7f = 70%)
            setDimAmount(0.7f)
        }
        // --- FIM DA LÓGICA DA JANELA ---

        return binding.root
    }

    // 'onStart' AGORA SÓ DEFINE O TAMANHO
    override fun onStart() {
        super.onStart()
        // Definir o tamanho no onStart é mais seguro
        // depois que a view já foi inflada
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnConfirmarSalvar.setOnClickListener {
            // Envia o resultado "true" de volta para o Step12Fragment
            setFragmentResult("confirmSaveRequest", bundleOf("result" to true))
            dismiss()
        }

        binding.btnCancelar.setOnClickListener {
            // Apenas fecha o pop-up
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}