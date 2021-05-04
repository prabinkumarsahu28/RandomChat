package com.example.randomchat.viewholders

import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.randomchat.R
import com.example.randomchat.models.UserStatus
import kotlinx.android.synthetic.main.status_sample_layout.view.*
import omari.hamza.storyview.StoryView
import omari.hamza.storyview.callback.StoryClickListeners
import omari.hamza.storyview.model.MyStory


class StatusViewHolder(itemView: View, private val supportFragmentManager: FragmentManager?) :
    RecyclerView.ViewHolder(itemView) {

    fun setData(userStatus: UserStatus) {

        itemView.apply {

            circular_status_view.setPortionsCount(userStatus.statuses!!.size)
            if (userStatus.statuses!!.isNotEmpty()) {
                val lastStatus = userStatus.statuses!![(userStatus.statuses!!.size - 1)].imageUrl
                Glide.with(this)
                    .load(lastStatus)
                    .placeholder(R.drawable.image_placeholder)
                    .centerCrop()
                    .into(imgLastStatus)
            }

            circular_status_view.setOnClickListener {
                if (userStatus.statuses!!.isNotEmpty()) {
                    val myStories: ArrayList<MyStory> = ArrayList()
                    for (story in userStatus.statuses!!) {
                        myStories.add(MyStory(
                            story.imageUrl
                        ))
                    }

                    StoryView.Builder(supportFragmentManager)
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(userStatus.name) // Default is Hidden
                        .setSubtitleText("") // Default is Hidden
                        .setTitleLogoUrl(userStatus.dp) // Default is Hidden
                        .setStoryClickListeners(object : StoryClickListeners {
                            override fun onDescriptionClickListener(position: Int) {
                                //your action
                            }

                            override fun onTitleIconClickListener(position: Int) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show()
                }
            }
        }
    }

}