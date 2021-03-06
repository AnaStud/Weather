package ru.anasoft.weather.view.contacts

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ru.anasoft.weather.R
import ru.anasoft.weather.databinding.FragmentContactsBinding

const val REQUEST_CODE = 112

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    companion object {
        @JvmStatic
        fun newInstance() = ContactsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
    }

    private fun checkPermission() {
        context?.let {
            when {
                ContextCompat.checkSelfPermission(it,
                    Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED -> {
                    getContacts()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                    showRequestPermissionRationale()
                }
                else -> {
                    myRequestPermission()
                }
            }
        }
    }

    private fun myRequestPermission() {
        requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE), REQUEST_CODE)
    }

    private fun showRequestPermissionRationale() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.RationaleTitle))
            .setMessage(getString(R.string.RationaleText))
            .setPositiveButton(getString(R.string.RationaleYes)) { _, _ -> myRequestPermission() }
            .setNegativeButton(getString(R.string.RationaleNo)) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    @SuppressLint("Range")
    private fun getContacts() {
        context?.let {
            val contentResolver = it.contentResolver
            val cursorContacts = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null
            )
            val cursorPhones = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
            )
            cursorContacts?.let { itCursor->
                for (i in 0 until itCursor.count) {
                    if (itCursor.moveToPosition(i)) {
                        val name = itCursor.getString(itCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        var phone = ""
                        if (itCursor.getString(itCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) == "1") {
                            cursorPhones?.let { itCursorPhones ->
                                if (itCursorPhones.moveToPosition(i)) {
                                    phone = itCursorPhones.getString(itCursorPhones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                }
                            }
                        }
                        addView(name, phone)
                    }
                }
            }
            cursorContacts?.close()
            cursorPhones?.close()
        }
    }

    private fun addView(name:String, phone:String) {
        binding.containerContacts.addView(TextView(requireContext()).apply {
            text = name
            textSize = resources.getDimension(R.dimen.text_size_10_sp)
            setOnClickListener {
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:$phone")
                startActivity(intent)
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray) {

        if (requestCode == REQUEST_CODE) {
            val sizeGR = grantResults.size
            if (sizeGR != 0) {
                when {
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED) -> {
                        getContacts()
                    }
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                        showRequestPermissionRationale()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}