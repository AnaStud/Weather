package ru.anasoft.weather.view.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import ru.anasoft.weather.databinding.FragmentHistoryBinding
import ru.anasoft.weather.viewmodel.AppState
import ru.anasoft.weather.viewmodel.HistoryViewModel

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding: FragmentHistoryBinding
        get() {
            return _binding!!
        }

    private val adapter: HistoryAdapter by lazy {
        HistoryAdapter()
    }

    private val viewModel: HistoryViewModel by lazy {
        ViewModelProvider(this).get(HistoryViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getLiveData().observe(viewLifecycleOwner, Observer<AppState> { renderData(it) })
        viewModel.getAllHistory()
        binding.historyFragmentRecyclerView.adapter = adapter
    }



    private fun renderData(appState: AppState) {
        with(binding) {
            when (appState) {
                is AppState.Loading -> {}
                is AppState.SuccessHistory -> {
                    adapter.setData(appState.listWeatherHistory)
                }
                is AppState.Error -> {
                    Snackbar
                        .make(historyFragmentRecyclerView,
                            "Ошибка загрузки: ${appState.error.message}",
                            Snackbar.LENGTH_INDEFINITE)
                        .setAction("Повторить") { viewModel.getAllHistory() }
                        .show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance() = HistoryFragment()
    }

}