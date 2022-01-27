package ru.anasoft.weather.view.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import kotlinx.android.synthetic.main.fragment_history_item.view.*
import ru.anasoft.weather.R
import ru.anasoft.weather.model.WeatherHistory
import ru.anasoft.weather.utils.ICONS_PATH
import ru.anasoft.weather.utils.ICONS_SERVER

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.RecyclerItemViewHolder>() {

    private var data: List<WeatherHistory> = arrayListOf()

    fun setData(data: List<WeatherHistory>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerItemViewHolder {
        return RecyclerItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_history_item, parent, false) as View
        )
    }

    override fun onBindViewHolder(holder: RecyclerItemViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class RecyclerItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(data: WeatherHistory) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                itemView.apply {
                    cityName.text = data.city.name
                    dateTime.text = data.dateTime
                    temperature.text = data.temp.toString()
                    feelsLike.text = data.feelsLike.toString()
                    condition.text = data.condition
                    icon.loadUrl("$ICONS_SERVER$ICONS_PATH/${data.icon}.svg")
                }
            }
        }
        private fun ImageView.loadUrl(url: String) {

            val imageLoader = ImageLoader.Builder(this.context)
                .componentRegistry { add(SvgDecoder(this@loadUrl.context)) }
                .build()

            val request = ImageRequest.Builder(this.context)
                .data(url)
                .target(this)
                .build()

            imageLoader.enqueue(request)
        }
    }
}