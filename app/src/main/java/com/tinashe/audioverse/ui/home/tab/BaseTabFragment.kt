package com.tinashe.audioverse.ui.home.tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.tinashe.audioverse.R
import com.tinashe.audioverse.data.model.Presenter
import com.tinashe.audioverse.data.model.Recording
import com.tinashe.audioverse.data.model.RecordingType
import com.tinashe.audioverse.data.model.Section
import com.tinashe.audioverse.injection.ViewModelFactory
import com.tinashe.audioverse.ui.home.tab.vh.RecordingHolder
import com.tinashe.audioverse.utils.Helper
import com.tinashe.audioverse.utils.custom.RecyclerSectionItemDecoration
import com.tinashe.audioverse.utils.custom.UniversalAdapter
import com.tinashe.audioverse.utils.getViewModel
import com.tinashe.audioverse.utils.hide
import com.tinashe.audioverse.utils.vertical
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_list.*
import timber.log.Timber
import javax.inject.Inject

class BaseTabFragment : Fragment() {

    lateinit var title: String
    private var section: Section? = null
    private lateinit var sectionHolder: SectionHolder

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: TabViewModel

    private lateinit var presentersListAdapter: PresenterListAdapter

    private lateinit var recordingsListAdapter: UniversalAdapter<Recording, RecordingHolder>

    companion object {
        fun newInstance(title: String, section: Section): BaseTabFragment {
            val fragment = BaseTabFragment()
            fragment.title = title
            fragment.section = section

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return when (section) {
            Section.MY_LIST_HISTORY,
            Section.MY_LIST_PLAY_LISTS,
            Section.MY_LIST_FAVORITES -> inflater.inflate(R.layout.fragment_empty, container, false)
            else -> inflater.inflate(R.layout.fragment_list, container, false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        viewModel = getViewModel(this, viewModelFactory)
        section?.let {
            viewModel.initTab(it)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.recordings.observe(this, Observer {

            it?.let {

                if (it.isNotEmpty()) {
                    progressBar.hide()
                }
                recordingsListAdapter.items = it.toMutableList()
            } ?: progressBar.hide()
        })

        viewModel.presenters.observe(this, Observer {

            if (it?.isNotEmpty() != false) {
                progressBar.hide()
            }

            val items = mutableListOf<Presenter>()
            it?.forEach { items.add(it) }

            sectionHolder.people = items

            presentersListAdapter.submitList(it)
        })

        viewModel.sectionHolder.value?.let {
            when (it) {

                Section.MY_LIST_FAVORITES -> {
                }
                Section.MY_LIST_PLAY_LISTS -> {
                }
                Section.MY_LIST_HISTORY -> {
                }
                Section.RECORDINGS_NEW,
                Section.RECORDINGS_TRENDING,
                Section.RECORDINGS_FEATURED -> {

                    recordingsListAdapter = UniversalAdapter(
                            { parent, _ -> RecordingHolder.inflate(parent) },
                            { vh, _, item ->
                                vh.bind(item, RecordingType.FEATURED, object : RecordingHolder.MoreOptions {
                                    override fun play() {
                                        context?.let {
                                            Helper.playRecording(it, item)
                                        }
                                    }

                                    override fun share(content: String) {
                                        activity?.let {
                                            Helper.shareText(it, content)
                                        }
                                    }

                                    override fun favorite(enabled: Boolean) {
                                        //TODO: Implement
                                    }

                                })
                            }
                    )
                    listView.apply {
                        vertical()
                        adapter = recordingsListAdapter
                    }
                }
                Section.PRESENTERS -> {

                    presentersListAdapter = PresenterListAdapter()
                    presentersListAdapter.setHasStableIds(true)

                    sectionHolder = SectionHolder()
                    val sectionItemDecoration = RecyclerSectionItemDecoration(resources.getDimensionPixelSize(R.dimen.header), true, sectionHolder)

                    listView.apply {
                        vertical()
                        addItemDecoration(sectionItemDecoration)
                        adapter = presentersListAdapter
                    }
                }
            }
        }

    }

    class SectionHolder : RecyclerSectionItemDecoration.SectionCallback {

        var people = mutableListOf<Presenter>()

        override fun isSection(position: Int): Boolean {
            return position == 0 || getSectionHeader(position) != getSectionHeader(position - 1)
        }

        override fun getSectionHeader(position: Int): CharSequence {
            return if (people.size <= position) {
                ""
            } else people[position].displayName[0].toString().toUpperCase().trim()
        }
    }

    fun scrollToTop() {
        if (listView != null) {
            listView.scrollToPosition(0)
        }
    }
}