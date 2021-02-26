package io.hoarfroster.ditiezu.pages.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.hoarfroster.ditiezu.adapters.MainFragmentThreadListAdapter
import io.hoarfroster.ditiezu.databinding.FragmentMainBinding
import io.hoarfroster.ditiezu.utilities.DataFetcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        val ac = activity
        if (ac != null) {
            GlobalScope.launch {
                val data = DataFetcher.fetchMainPage()
                Log.i("a", data.size.toString())
                Log.i("a", data.toString())
                ac.runOnUiThread {
                    binding.recyclerView.adapter = MainFragmentThreadListAdapter(ac, data)
                }
            }
        }
    }
}