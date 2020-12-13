package com.gpstracker.gpstracker_project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.gpstracker.gpstracker_project.activity.ResultActivity
import java.text.SimpleDateFormat
import java.util.*

class ActivityAdapter (context: Context, var activities: List<Activity>): BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    val result = ResultActivity()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        // Check if the view already exists. If it does, there’s no need to inflate from the layout and call findViewById() again.
        if (convertView == null) {

            // If the view doesn’t exist, you inflate the custom row layout from your XML.
            view = inflater.inflate(R.layout.activity_list_item, parent, false)

            // Create a new ViewHolder with subviews initialized by using findViewById().
            holder = ViewHolder()

            // image
            holder.acIcon = view.findViewById(R.id.imageView_icon) as ImageView
            holder.acttitle = view.findViewById(R.id.tvActivityTitle) as TextView
            holder.actduration = view.findViewById(R.id.tvActivityDuration) as TextView
            holder.actDate = view.findViewById(R.id.tvActivityDate) as TextView

            //holder.startlat = view.findViewById(R.id.tvStartLat) as TextView
            //holder.endlat = view.findViewById(R.id.tvEndLat) as TextView
            holder.distance = view.findViewById(R.id.tvDistance) as TextView

            // Hang onto this holder for future recycling by using setTag() to set the tag property of the view that the holder belongs to.
            view.tag = holder
        } else {

            // Skip all the expensive inflation steps and just get the holder you already made.
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        // Get relevant subviews of the row view.
        val tvActivityIcon = holder.acIcon
        val tvActivityTitle = holder.acttitle
        val tvActivityDate = holder.actDate
        val tvActivityDuration = holder.actduration
        //val tvStartLat = holder.startlat
        //val tvEndLat = holder.endlat
        val tvDistance = holder.distance

        // Get activity for current position using getItem(position).
        val activity = getItem(position) as Activity

        // Set text on TextViews
        val duration = result.getDuration(activity.starttime, activity.endtime)

        //val distance = result.getTotalDistance()
        val distance = "to come"

        // get Date from starttime
        val sdf = SimpleDateFormat("dd/MM/yy HH:mm") // "dd/MM/yy hh:mm"
        val netDate = Date(activity.starttime)
        val date =sdf.format(netDate)


        //tvActivityIcon.drawable = ic_logo_transparent
        tvActivityTitle.text = activity.note.toString()
        tvActivityDate.text =  date
        tvActivityDuration.text = "Duration: " +  duration
        //tvStartLat.text = "Start Latidude: " + activity.startlat.toString()
        //tvEndLat.text = "End Latidude: " + activity.endlat.toString()
        tvDistance.text = "Distance: " + distance

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
        lateinit var acIcon: ImageView
        lateinit var acttitle: TextView
        lateinit var actduration: TextView
        lateinit var actDate: TextView
        //lateinit var startlat: TextView
        //lateinit var endlat: TextView
        lateinit var distance: TextView
    }
}
