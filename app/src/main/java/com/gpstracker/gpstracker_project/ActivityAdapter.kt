package com.gpstracker.gpstracker_project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ActivityAdapter (context: Context, var activities: List<Activity>): BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        // Check if the view already exists. If it does, there’s no need to inflate from the layout and call findViewById() again.
        if (convertView == null) {

            // If the view doesn’t exist, you inflate the custom row layout from your XML.
            view = inflater.inflate(R.layout.activity_list_item, parent, false)

            // Create a new ViewHolder with subviews initialized by using findViewById().
            holder = ViewHolder()
            holder.acttitle = view.findViewById(R.id.tvActivityTitle) as TextView
            holder.actduration = view.findViewById(R.id.tvActivityDuration) as TextView

            // Hang onto this holder for future recycling by using setTag() to set the tag property of the view that the holder belongs to.
            view.tag = holder
        } else {

            // Skip all the expensive inflation steps and just get the holder you already made.
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        // Get relevant subviews of the row view.
        val tvActivityTitle = holder.acttitle
        val tvActivityDuration = holder.actduration

        // Get activity for current position using getItem(position).
        val activity = getItem(position) as Activity

        // Set text on TextViews
        tvActivityTitle.text = "Eintrag: " + activity.id.toString()
        tvActivityDuration.text = activity.startlat.toString()

        // Return view containing all text values for current position
        return view
    }

    override fun getItem(position: Int): Any {
        return activities[position]
    }

    override fun getItemId(position: Int): Long {
        return activities[position].id
    }

    override fun getCount(): Int {
        return activities.size
    }

    private class ViewHolder {
        lateinit var acttitle: TextView
        lateinit var actduration: TextView
    }
}