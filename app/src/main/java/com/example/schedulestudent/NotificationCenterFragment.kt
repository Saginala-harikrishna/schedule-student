package com.example.schedulestudent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class NotificationCenterFragment : Fragment() {

    private lateinit var containerCurrent: LinearLayout
    private lateinit var containerEnding: LinearLayout
    private lateinit var containerOverdue: LinearLayout
    private lateinit var emptyState: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_notification_center,
            container,
            false
        )

        containerCurrent = view.findViewById(R.id.containerCurrent)
        containerEnding = view.findViewById(R.id.containerEnding)
        containerOverdue = view.findViewById(R.id.containerOverdue)
        emptyState = view.findViewById(R.id.tvEmptyState)

        return view
    }

    /**
     * ✅ STEP N6B
     * Pull today's notifications from cache
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val result = NotificationCache.get()
        if (result != null) {
            render(result)
        } else {
            render(
                NotificationResult(
                    pendingCurrentTargets = emptyList(),
                    endingTomorrowRangeTargets = emptyList(),
                    overdueRangeTargets = emptyList()
                )
            )
        }
    }

    /**
     * UI rendering
     */
    fun render(result: NotificationResult) {
        containerCurrent.removeAllViews()
        containerEnding.removeAllViews()
        containerOverdue.removeAllViews()

        var hasContent = false

        // 🔹 Current Target Notifications
        if (result.pendingCurrentTargets.isNotEmpty()) {
            hasContent = true
            result.pendingCurrentTargets.forEach { title ->
                containerCurrent.addView(
                    createItemView(title, NotificationNav.FROM_CURRENT)
                )
            }
        }

        // 🔹 Range Targets Ending Tomorrow
        if (result.endingTomorrowRangeTargets.isNotEmpty()) {
            hasContent = true
            result.endingTomorrowRangeTargets.forEach { title ->
                containerEnding.addView(
                    createItemView(title, NotificationNav.FROM_RANGE)
                )
            }
        }

        // 🔹 Overdue Range Targets
        if (result.overdueRangeTargets.isNotEmpty()) {
            hasContent = true
            result.overdueRangeTargets.forEach { title ->
                containerOverdue.addView(
                    createItemView(title, NotificationNav.FROM_RANGE)
                )
            }
        }

        emptyState.visibility = if (hasContent) View.GONE else View.VISIBLE
    }

    private fun createItemView(
        text: String,
        source: String
    ): View {

        val tv = TextView(requireContext())
        tv.text = "• $text"
        tv.textSize = 16f
        tv.setPadding(16, 12, 16, 12)

        tv.setOnClickListener {
            val fragment = if (source == NotificationNav.FROM_CURRENT) {
                CurrentTargetFragment()
            } else {
                RangeTargetFragment()
            }

            fragment.arguments = Bundle().apply {
                putString(NotificationNav.ARG_SOURCE, source)
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        return tv
    }
}
