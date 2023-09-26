package com.androiddev.mycontactsassignment

import android.annotation.SuppressLint
import android.Manifest
import androidx.activity.result.ActivityResultLauncher
import android.content.ContentResolver


import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.androiddev.mycontactsassignment.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val CONTACTS_PERMISSION_REQUEST = 1
    var displayName = "No contacts imported";
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var contactNameTextView: TextView
    private lateinit var contactNoTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        contactNameTextView = findViewById<TextView>(R.id.cName)
        contactNoTextView = findViewById<TextView>(R.id.cNo)

//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
//
//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAnchorView(R.id.fab)
//                .setAction("Action", null).show()
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


    fun getContacts(view: View) {
//        var intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
//        Action pick mtlb koi action perfrom hoga
//        content uri me contacts k object save hota hen
//            resultLauncher.launch(intent)
        if (hasPermission()) {
            val pickContactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            resultLauncher.launch(pickContactIntent)
        } else {
            requestPermission()
        }


    }

    //        ise execute krana hai to to register krana h activity
//    @RequiresApi(Build.VERSION_CODES.O)
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                val contactUri: Uri? = data.data
                if (contactUri != null) {
                    val company = extractCompanyName(contactUri)
//                    companyNameTextView.text = "Company: $company"
                }
            }
        }
    }

    @SuppressLint("Range")
    fun extractCompanyName(uri : Uri){
        val cursor = contentResolver.query(
            uri,
            null ,null,
            null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                displayName = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)).toString();
//                val company = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                // Now you have the person's name and company information
                // You can use or show this information in your app
                println("Contact name is: "+displayName)
                contactNameTextView.text = displayName
                val id = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                val phoneNumberCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    arrayOf(id),
                    null)
//

                phoneNumberCursor?.use { phoneCursor ->
                    if (phoneCursor.moveToFirst()) {
                        val phoneno =phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        val contactName =phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                        println("came in phone")
                        println(phoneno)
                        contactNoTextView.text = phoneno
                        println(contactName)
                    }
                }
            }

        }
    }
    // Check if READ_CONTACTS permission is granted
    private fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
    }
    // Request READ_CONTACTS permission
    private fun requestPermission() {
        println("req perm")
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), CONTACTS_PERMISSION_REQUEST)

    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CONTACTS_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val pickContactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                resultLauncher.launch(pickContactIntent)
            }
            //how canI ask for permission again and again
        }
    }

    fun clearSelected(view: View) {

        contactNameTextView.text=""
        contactNoTextView.text=""
        Toast.makeText(this, "Text Cleared!", Toast.LENGTH_SHORT).show()   }
//
    }


//}