package com.moez.QKSMS.ui.conversations

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.moez.QKSMS.R
import com.moez.QKSMS.ui.base.QkActivity
import com.moez.QKSMS.util.observe
import kotlinx.android.synthetic.main.conversation_list_activity.*
import timber.log.Timber

class ConversationListActivity : QkActivity(), Observer<ConversationListViewState> {

    private lateinit var viewModel: ConversationListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.conversation_list_activity)
        requestPermissions()

        viewModel = ViewModelProviders.of(this)[ConversationListViewModel::class.java]
        viewModel.state.observe(this)

        conversationList.layoutManager = LinearLayoutManager(this)

        swipeRefresh.setOnRefreshListener { viewModel.onRefresh() }
    }

    override fun onChanged(state: ConversationListViewState?) {
        state?.let {
            if (conversationList.adapter == null) conversationList.adapter = ConversationAdapter(this, state.conversations)

            swipeRefresh.isRefreshing = state.refreshing
        }
    }

    private fun requestPermissions() {
        Dexter.withActivity(this)
                .withPermissions(arrayListOf(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS))
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionRationaleShouldBeShown(request: MutableList<PermissionRequest>, token: PermissionToken) {
                    }

                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        for (response in report.grantedPermissionResponses) Timber.v("Permission granted: ${response.permissionName}")
                        for (response in report.deniedPermissionResponses) Timber.v("Permission denied: ${response.permissionName}")
                    }
                })
                .check()
    }
}
