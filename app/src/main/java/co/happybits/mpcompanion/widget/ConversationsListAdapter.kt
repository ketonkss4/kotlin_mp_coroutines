package co.happybits.mpcompanion.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import co.happybits.mpcompanion.R
import co.happybits.mpcompanion.data.Conversation

class ConversationsListAdapter : RecyclerView.Adapter<ConversationsListAdapter.ViewHolder>() {
    private val convoList: ArrayList<Conversation> = ArrayList()
    val selectData: MutableLiveData<Conversation> = MutableLiveData()

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.convo_list_item, null)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return convoList.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val conversation = convoList[position]
        viewHolder.itemView.setOnClickListener { selectData.setValue(conversation) }
        if (conversation.group) {
            viewHolder.bind(conversation.title)
        } else {
            conversation.members.forEach {
                if(!it.isMyId()) viewHolder.bind("${it.first_name} ${it.last_name}")
            }
        }
    }

    fun refreshList(list: List<Conversation>) {
        if (itemCount != 0) convoList.clear()
        convoList.addAll(list)
        notifyDataSetChanged()
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.convo_title) lateinit var convoTitle: TextView

        fun bind(title: String) {
            ButterKnife.bind(this, itemView)
            convoTitle.text = title
        }

    }
}