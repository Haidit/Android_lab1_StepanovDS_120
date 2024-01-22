package com.haidit.intersvyaztestovoe.ui.detailed

import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.AlarmManager
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.RECEIVER_EXPORTED
import androidx.core.content.ContextCompat.registerReceiver
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.haidit.intersvyaztestovoe.R
import com.haidit.intersvyaztestovoe.databinding.CreateNotificationDialogBinding
import com.haidit.intersvyaztestovoe.databinding.FragmentDetailedBinding
import com.haidit.intersvyaztestovoe.utils.Notification
import com.haidit.intersvyaztestovoe.utils.channelID
import com.haidit.intersvyaztestovoe.utils.messageExtra
import com.haidit.intersvyaztestovoe.utils.notificationID
import com.haidit.intersvyaztestovoe.utils.reminderID
import com.haidit.intersvyaztestovoe.utils.titleExtra
import java.io.File
import java.util.Calendar

class DetailedFragment : Fragment() {

    private val viewModel: DetailedViewModel by activityViewModels()
    private var _binding: FragmentDetailedBinding? = null
    private val binding get() = _binding!!
    private lateinit var name: String
    private lateinit var desc: String
    private lateinit var url: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailedBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: DetailedFragmentArgs by navArgs()

        createNotificationChannel()

        with(binding) {
            name = args.itemName
            itemName.text = name
            desc = args.itemDesc
            itemDesc.text = desc
            url = args.itemPicture
            Glide.with(requireContext()).load(url).override(1024, 1024).into(binding.itemImage)

            saveButton.setOnClickListener {
                checkPermission(
                    WRITE_EXTERNAL_STORAGE,
                    getString(R.string.permission_required_message)
                )
                saveImage(url)
            }
            shareButton.setOnClickListener { shareImage() }
            remindButton.setOnClickListener { showDialog() }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createNotificationChannel() {
        val name = getString(R.string.channel_name)
        val desc = getString(R.string.channel_desc)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager =
            requireActivity().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createReminder(text: String) {
        val intent = Intent(requireContext(), Notification::class.java).apply {

        }
        val title = getString(R.string.reminder)
        val message = getString(R.string.time_to_come_back)
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            reminderID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime(text)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, time, pendingIntent
        )
    }

    private fun sendNotification(text: String) {

        val title = getString(R.string.reminder_created, name)
        val message = getString(R.string.reminder_created_text, text, name)

        val pendingIntent = NavDeepLinkBuilder(requireContext())
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(R.id.navigation_detailed)
            .setArguments(
                bundleOf(
                    Pair("itemName", name),
                    Pair("itemDesc", desc),
                    Pair("itemPicture", url)
                )
            )
            .createPendingIntent()

        val builder = NotificationCompat.Builder(requireContext(), channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(requireContext())) {
            checkPermission(
                POST_NOTIFICATIONS,
                getString(R.string.permission_required_message_notifications)
            )
            notify(notificationID, builder.build())
        }
    }

    private fun getTime(text: String): Long {

        val calendar = Calendar.getInstance()
        when (text) {
            getString(R.string.minutes_15) -> calendar.add(Calendar.MINUTE, 15)
            getString(R.string.hour_1) -> calendar.add(Calendar.HOUR, 1)
            getString(R.string.next_day) -> calendar.add(Calendar.DAY_OF_YEAR, 1)
            getString(R.string.next_week) -> calendar.add(Calendar.DAY_OF_YEAR, 7)
        }
        return calendar.timeInMillis
    }

    private fun saveImage(url: String) {
        val directory = File(Environment.DIRECTORY_PICTURES)

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val downloadManager = requireContext().getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        val downloadUri = Uri.parse(url)

        val request = DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE).setAllowedOverRoaming(
                false
            ).setTitle(url.substring(url.lastIndexOf("/") + 1))
                .setDescription(getString(R.string.downloading)).setDestinationInExternalPublicDir(
                    directory.toString(), url.substring(url.lastIndexOf("/") + 1)
                )
        }

        val downloadId = downloadManager.enqueue(request)
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                val id = p1?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    Snackbar.make(binding.root, "Изображение загружено", Snackbar.LENGTH_LONG)
                        .show()
                }
            }
        }
        registerReceiver(
            requireContext(),
            broadcastReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            RECEIVER_EXPORTED
        )

    }

    private fun checkPermission(permission: String, message: String) {
        if (ContextCompat.checkSelfPermission(
                requireContext(), permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), permission
                )
            ) {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.permission_required))
                    .setMessage(message)
                    .setPositiveButton(getString(R.string.allow)) { _, _ ->
                        ActivityCompat.requestPermissions(
                            requireActivity(), arrayOf(permission), 100
                        )
                    }.setNegativeButton(getString(R.string.deny)) { dialog, _ -> dialog.cancel() }
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(permission), 100
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            100 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    saveImage(url)
                }
                return
            }

            else -> {}
        }
    }

    private fun shareImage() {
        val bitmap = (binding.itemImage.drawable as BitmapDrawable).bitmap
        val resolver = requireActivity().contentResolver
        val path = MediaStore.Images.Media.insertImage(resolver, bitmap, "", null)
        val uri = Uri.parse(path)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_image)))
    }

    private fun showDialog() {
        val dialogBinding = CreateNotificationDialogBinding.inflate(layoutInflater)

        val dialogBuilder = AlertDialog.Builder(requireContext()).setView(dialogBinding.root)
        val dialog = dialogBuilder.create()
        val spinner = dialogBinding.spinner
        val positiveButton = dialogBinding.doneButton
        val negativeButton = dialogBinding.cancelButton

        var selectedText = ""
        val options = arrayOf(
            getString(R.string.minutes_15),
            getString(R.string.hour_1),
            getString(R.string.next_day),
            getString(R.string.next_week)
        )
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            options
        )
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedText = options[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        dialog.show()
        positiveButton.setOnClickListener {
            createReminder(selectedText)
            sendNotification(selectedText)
            dialog.dismiss()
        }
        negativeButton.setOnClickListener { dialog.dismiss() }
    }

}
