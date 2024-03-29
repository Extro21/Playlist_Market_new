package com.practicum.playlistmarket.settings.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.practicum.playlistmarket.R
import com.practicum.playlistmarket.databinding.FragmentSettingsBinding
import com.practicum.playlistmarket.settings.ui.view_model.SettingViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {


    private var _binding : FragmentSettingsBinding? = null
    private val binding get() = _binding!!


    private val viewModel: SettingViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.shareApp.setOnClickListener {
            val link = getString(R.string.massageEmail)
            viewModel.shareApp(link)
        }

        binding.support.setOnClickListener {
            viewModel.sentSupport()
        }


        binding.termsFUse.setOnClickListener {
            viewModel.termOfUse()
        }

        binding.switchSetting.isChecked = viewModel.default()

        binding.switchSetting.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

