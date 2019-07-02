/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemSleepNightBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_ITEM = 1

class SleepNightAdapter(val clickListener: SleepNightListener) : ListAdapter<DataItem, RecyclerView.ViewHolder>(SleepNightDiffCallBack()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    fun addHeaderAndSubmitList(list:List<SleepNight>?){
        adapterScope.launch {
            val items = when(list){
                null-> listOf(DataItem.Header)
                else-> listOf(DataItem.Header)+ list.map{
                    DataItem.SleepNightItem(it)
                }
            }
            withContext(Dispatchers.Main){
                submitList(items)
            }

        }


    }
    /*  var data = listOf<SleepNight>()
          set(value) { // let the recyclerView know, when there is a change in the data-set.
              field = value
              notifyDataSetChanged()
          }*/

    //Inflating the viewHolder layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ITEM_VIEW_TYPE_HEADER -> TextViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    //Get total item to show in the recyclerView
//    override fun getItemCount()= data.size

    //Binding the data to view holder
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ViewHolder ->{
                val item = getItem(position) as DataItem.SleepNightItem
                holder.bind(clickListener,item.sleepNight)
            }
        }

      // val item = getItem(position)
       // holder.bind(item)
      //  holder.bind(clickListener,item)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.SleepNightItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    class ViewHolder private constructor(val binding: ListItemSleepNightBinding) : RecyclerView.ViewHolder(binding.root) {

//        fun bind(item: SleepNight) {

        fun bind(clickListener: SleepNightListener,item:SleepNight){
            binding.sleep = item
            binding.clickItem = clickListener
            binding.executePendingBindings()

            /*
             val res = itemView.context.resources
             binding.sleepLength.text = convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, res)
             binding.qualityString.text = convertNumericQualityToString(item.sleepQuality, res)
             binding.qualityImage.setImageResource(when (item.sleepQuality) {
                 0 -> R.drawable.ic_sleep_0
                 1 -> R.drawable.ic_sleep_1
                 2 -> R.drawable.ic_sleep_2
                 3 -> R.drawable.ic_sleep_3
                 4 -> R.drawable.ic_sleep_4
                 5 -> R.drawable.ic_sleep_5
                 else -> R.drawable.ic_sleep_active
             })*/
        }


        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemSleepNightBinding.inflate(layoutInflater, parent, false)
                //  val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_sleep_night, parent, false)
                //  return ViewHolder(view)
                return ViewHolder(binding)
            }
        }

    }

   /* class SleepNightDiffCallBack : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem  newItem
        }

    }*/

    class SleepNightDiffCallBack:DiffUtil.ItemCallback<DataItem>(){
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
           return oldItem == newItem
        }

    }


    //View holder for header only.
    class TextViewHolder(view: View): RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): TextViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.header, parent, false)
                return TextViewHolder(view)
            }
        }
    }
}

class SleepNightListener(val clickListener:(sleepId:Long)->Unit){
    fun onClick(night: SleepNight) = clickListener(night.nightId)
}

sealed class DataItem{
    abstract  val id:Long

    data class SleepNightItem(val sleepNight: SleepNight):DataItem(){
        override val id = sleepNight.nightId
    }

    object Header:DataItem(){
        override val id= Long.MIN_VALUE

    }
}