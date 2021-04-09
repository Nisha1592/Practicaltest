package com.app.chatapp.ui.start.createAccount

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

import androidx.navigation.fragment.findNavController
import com.app.chatapp.R

import com.app.chatapp.data.EventObserver
import com.app.chatapp.databinding.FragmentCreateAccountBinding
import com.app.chatapp.ui.main.MainActivity
import com.app.chatapp.util.SharedPreferencesUtil
import com.app.chatapp.util.convertFileToByteArray
import com.app.chatapp.util.forceHideKeyboard
import com.app.chatapp.util.showSnackBar
import java.text.SimpleDateFormat
import java.util.*


class CreateAccountFragment : Fragment() {

    private val viewModel by viewModels<CreateAccountViewModel>()
    private lateinit var viewDataBinding: FragmentCreateAccountBinding
    private val selectImageIntentRequestCode = 1

    var cal = Calendar.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentCreateAccountBinding.inflate(inflater, container, false)
            .apply { viewmodel = viewModel }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupObservers()

    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        viewModel.dobText.value=sdf.format(cal.getTime())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupObservers() {
        viewModel.dataLoading.observe(viewLifecycleOwner,
            EventObserver { (activity as MainActivity).showGlobalProgressBar(it) })

        viewModel.snackBarText.observe(viewLifecycleOwner,
            EventObserver { text ->
                view?.showSnackBar(text)
                view?.forceHideKeyboard()
            })

        viewModel.editImageEvent.observe(viewLifecycleOwner,
            EventObserver { startSelectImageIntent() })

        viewModel.isCreatedEvent.observe(viewLifecycleOwner, EventObserver {
            SharedPreferencesUtil.saveUserID(requireContext(), it.uid)
            /*var value=viewModel.isCreatingAccount.value
            if(viewModel.isCreatingAccount.value==false)
            {*/
            viewModel.isCreatingAccount.value=false
                navigateToChats()

            /*}*/

        })

        viewModel.editdobEditext.observe(viewLifecycleOwner,
                EventObserver{

                    var  dateSetListener = object : DatePickerDialog.OnDateSetListener {
                        override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                               dayOfMonth: Int) {
                            cal.set(Calendar.YEAR, year)
                            cal.set(Calendar.MONTH, monthOfYear)
                            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            updateDateInView()
                        }
                    }

                    DatePickerDialog(requireContext(),
                            dateSetListener,
                            // set DatePickerDialog to point to today's date when it loads up
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)).show()
                })


    }

    private fun startSelectImageIntent() {
        val selectImageIntent = Intent(Intent.ACTION_GET_CONTENT)
        selectImageIntent.type = "image/*"
        startActivityForResult(selectImageIntent, selectImageIntentRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == selectImageIntentRequestCode) {
            data?.data?.let { uri ->
                viewModel.imageuri.value=uri
                convertFileToByteArray(requireContext(), uri).let {
                    //viewModel.changeUserImage(it)
                    viewModel.bbytearray.value=it
                }
            }
        }
    }





    private fun navigateToChats() {
        findNavController().navigate(R.id.action_createAccountFragment_to_navigation_chats)
    }



}