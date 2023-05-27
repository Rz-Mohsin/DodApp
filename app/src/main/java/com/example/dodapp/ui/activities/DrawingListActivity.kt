package com.example.dodapp.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.dodapp.R
import com.example.dodapp.adapters.DrawingListAdapter
import com.example.dodapp.databinding.ActivityDrawingListBinding
import com.example.dodapp.db.DrawingDatabase
import com.example.dodapp.repository.DrawingRepository
import com.example.dodapp.ui.DrawingViewModel
import com.example.dodapp.ui.DrawingViewModelProviderFactory

class DrawingListActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDrawingListBinding
    lateinit var madapter : DrawingListAdapter
    lateinit var viewmodel : DrawingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = DrawingRepository(DrawingDatabase(this))
        viewmodel = ViewModelProvider(this,DrawingViewModelProviderFactory(repository))[DrawingViewModel::class.java]

        binding.btnAddDrawing.setOnClickListener {
            Log.d("error001","add drawing pressed")
            val fragment = DrawingProfileFragment()
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.addToBackStack("Profile Fragment")
            transaction.replace(R.id.fragmentContainer,fragment,null)
            transaction.commit()
        }

        madapter = DrawingListAdapter{
            Log.d("error001","clicked on ${it.name}")
            val bundle = Bundle()
            bundle.apply {
                putSerializable("drawing",it)
            }
            val fragment = DrawingDetailsFragment()
            fragment.arguments = bundle
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.addToBackStack("Details Fragment")
            transaction.replace(R.id.fragmentContainer,fragment,null)
            transaction.commit()
        }
        getAllDrawings()
        setUpRecyclerView()
    }

    private fun getAllDrawings() {
        viewmodel.getAllDrawing().observe(this, Observer { drawings ->
            drawings?.let {
                Log.d("error001","database fetched list size : ${it.size}")
                madapter.differ.submitList(it)
            }
        })
    }

    private fun setUpRecyclerView() {
        binding.rvDrawing.apply {
            adapter = madapter
            layoutManager = LinearLayoutManager(this@DrawingListActivity)
        }
    }
}