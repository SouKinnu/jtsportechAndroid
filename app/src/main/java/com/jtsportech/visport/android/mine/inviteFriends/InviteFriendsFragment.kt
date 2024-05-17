package com.jtsportech.visport.android.mine.inviteFriends

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jtsportech.visport.android.R

class InviteFriendsFragment : Fragment() {

    companion object {
        fun newInstance() = InviteFriendsFragment()
    }

    private lateinit var viewModel: InviteFriendsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_invite_friends, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(InviteFriendsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}