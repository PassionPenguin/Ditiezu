package io.hoarfroster.ditiezu.pages

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.hoarfroster.ditiezu.adapters.MainViewPagerAdapter
import io.hoarfroster.ditiezu.databinding.ActivityMainBinding
import io.hoarfroster.ditiezu.transforms.transformPage

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        with(binding.tabLayout) {
            binding.viewPager.adapter = MainViewPagerAdapter(this@MainActivity)
            this.viewPager = binding.viewPager
            binding.viewPager.isUserInputEnabled = false
            binding.viewPager.setPageTransformer { view: View, fl: Float ->
                transformPage(view, fl)
            }
            this.tabNames = arrayOf("Home", "Category", "Notification", "Setting")
        }
    }
}