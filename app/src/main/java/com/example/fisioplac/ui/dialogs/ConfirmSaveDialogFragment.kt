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

    // *** FUNÇÃO ADICIONADA DE VOLTA para corrigir o tamanho esticado ***
    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            // Define o layout para ter o tamanho do conteúdo (WRAP_CONTENT)
            setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogConfirmSaveBinding.inflate(inflater, container, false)

        // Remove o fundo padrão, remove o título e aplica o fundo escuro (dim)
        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)

            // --- O SEU NOVO CÓDIGO (está correto) ---
            // Ativa o escurecimento (dim) do fundo
            dialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            // Define a intensidade do escurecimento (0.7f = 70%)
            dialog!!.window!!.setDimAmount(0.7f)
            // --- FIM DO NOVO CÓDIGO ---
        }

        return binding.root
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