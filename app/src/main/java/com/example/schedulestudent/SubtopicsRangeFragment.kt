package com.example.schedulestudent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import android.content.Intent
import android.widget.Button


class SubtopicsRangeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // 1️⃣ Inflate layout
        val view = inflater.inflate(
            R.layout.fragment_subtopics_range,
            container,
            false
        )

        val addButton = view.findViewById<Button>(R.id.btnAddSubtopicsRange)

        addButton.setOnClickListener {
            val intent = Intent(requireContext(), AddSubtopicsRangeActivity::class.java)
            startActivity(intent)
        }


        // 4️⃣ Return the view
        return view
    }
}
