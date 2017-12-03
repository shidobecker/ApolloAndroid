package shido.com.apolloandroid

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.trip_row.view.*
import shido.com.apolloandroid.api.fragment.TripFields

/**
 * Created by mira on 28/11/2017.
 */
class TripAdapter(val context :Context) : RecyclerView.Adapter<TripAdapter.ViewHolder>() {

    var allTrips: List<TripFields> = listOf()

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindTrip(allTrips[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.trip_row, parent, false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return allTrips.size
    }


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindTrip(trip: TripFields){
            itemView.idTrip.text = "ID : " + trip.id()
            itemView.title.text = trip.title()
            itemView.duration.text = "Duration :" + trip.duration().toString()
            itemView.startTime.text = "Start Time : "  + trip.startTime()
            itemView.priority.text = "Priority: " + trip.priority().name
            itemView.creationTime.text = "Creation time: " + trip.creationTime()
        }
    }
}