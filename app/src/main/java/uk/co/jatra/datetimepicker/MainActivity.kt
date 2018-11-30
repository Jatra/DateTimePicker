package uk.co.jatra.datetimepicker

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import org.joda.time.LocalDate
import org.joda.time.LocalTime


fun timeString(count: Int) : String {
    val hour = if ((count / 2) == 12) 12 else ((count / 2) % 12)
    val min = if ((count % 2) == 0) "00" else "30"
    val amOrPm = if (count < 24) "am" else "pm"
    return "${String.format("%02d", hour)}:$min $amOrPm"
}

val days = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

fun dateString(count: Int) : String {
    val date: LocalDate = LocalDate.now().plusDays(count)
    return "${days[date.dayOfWeek - 1]} ${date.toString("dd MMM yy")}"
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val now = LocalTime.now()
        val hourOfDay = now.hourOfDay
        val minOfHour = now.minuteOfHour
        val initialTimeSelection = hourOfDay*2 + (if (minOfHour > 30) 1 else 0)

        date.adapter = DateAdapter(this)
        time.adapter = TimeAdapter(this)
        val dateLayoutManager = LinearLayoutManager(this)
        date.layoutManager = dateLayoutManager
        val timeLayoutManager = LinearLayoutManager(this)
        time.layoutManager = timeLayoutManager
        date.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE) {
                    selectedDate.text = dateString(dateLayoutManager.findFirstCompletelyVisibleItemPosition()-1)
                }
            }
        })
        time.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE) {
                    selectedTime.text = timeString(timeLayoutManager.findFirstCompletelyVisibleItemPosition()-1)
                }
            }
        })
        val timeSnapHelper = LinearSnapHelper()
        val dateSnapHelper = LinearSnapHelper()
        timeSnapHelper.attachToRecyclerView(time)
        dateSnapHelper.attachToRecyclerView(date)

        time.scrollToPosition(initialTimeSelection+1)
    }

    class TimeAdapter(val context: Context) : RecyclerView.Adapter<TimeAdapter.TimeViewHolder>() {

        override fun onBindViewHolder(viewHolder: TimeViewHolder, position: Int) {
            if (position == 0 || position == 49) {
                viewHolder.textView.text = ""
            }
            else {
                viewHolder.textView.text = timeString(position-1)
            }
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TimeViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.date_or_time, p0, false)
            return TimeViewHolder(view)
        }

        override fun getItemCount() = 48 + 2 //top and bottom

        class TimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView = itemView.findViewById<TextView>(R.id.textView)
        }


    }
    class DateAdapter(val context: Context) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {


        override fun onBindViewHolder(viewHolder: DateAdapter.DateViewHolder, position: Int) {
            if (position == 0 ) {
                viewHolder.textView.text = ""
            }
            else {
                viewHolder.textView.text = dateString(position-1)
            }
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): DateAdapter.DateViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.date_or_time, p0, false)
            return DateViewHolder(view)
        }

        override fun getItemCount() =  10_000

        class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView = itemView.findViewById<TextView>(R.id.textView)
        }


    }
}
