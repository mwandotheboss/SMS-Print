package com.smartwareafrica.smsprinter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.ImagePrintable
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import com.squareup.picasso.Picasso
import java.lang.Exception


class MainActivity : AppCompatActivity(), PrintingCallback {

    internal var printing: Printing? = null
    var printTextButton: MaterialButton? = null
    var printImageButton: MaterialButton? = null
    var bluetoothPairButton: MaterialButton? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        printTextButton = findViewById(R.id.btnPrintText)
        printImageButton = findViewById(R.id.btnPrintImages)
        bluetoothPairButton = findViewById(R.id.btnBluetoothPair)


        initView()
    }

    private fun initView() {
        if (printing != null) {
            printing!!.printingCallback = this

            bluetoothPairButton?.setOnClickListener {
                if (Printooth.hasPairedPrinter()) {
                    Printooth.removeCurrentPrinter()
                } else {
                    startActivityForResult(
                        Intent(this@MainActivity, ScanningActivity::class.java),
                        ScanningActivity.SCANNING_FOR_PRINTER
                    )
                    changePairAndUnpair()
                }
            }

            printImageButton?.setOnClickListener {
                if (Printooth.hasPairedPrinter())
                    startActivityForResult(
                        Intent(this@MainActivity, ScanningActivity::class.java),
                        ScanningActivity.SCANNING_FOR_PRINTER
                    )
                else
                    printImage()
            }

            printTextButton?.setOnClickListener {
                if (Printooth.hasPairedPrinter())
                    startActivityForResult(
                        Intent(this@MainActivity, ScanningActivity::class.java),
                        ScanningActivity.SCANNING_FOR_PRINTER
                    )
                else
                    printText()
            }

        }
    }

    private fun printText() {
        val printableText = ArrayList<Printable>()
        printableText.add(RawPrintable.Builder(byteArrayOf(27, 100, 4)).build())

        // Add text
        printableText.add(
            TextPrintable.Builder()
                .setText("Hello printing: this is the test service")
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setNewLinesAfter(1)
                .build()
        )

        //Custom text
        printableText.add(
            TextPrintable.Builder()
                .setText("Hello Mwando")
                .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
                .setNewLinesAfter(1)
                .build()
        )

        printing!!.print(printableText)
    }

    private fun printImage() {
        val imageURL =
            "https://firebasestorage.googleapis.com/v0/b/smartware-africa.appspot.com/o/Website%20Resources%2FSmartware-africa-logo-official.jpg?alt=media&token=03af99ae-6f26-4c36-b376-45305ad02b4e"
        val printableImage = ArrayList<Printable>()
        //Load bitmap from Internet

        Picasso.get().load(imageURL)
            .into(object : com.squareup.picasso.Target {
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    printableImage.add(ImagePrintable.Builder(bitmap!!).build())
                    printing?.print(printableImage)
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    TODO("Not yet implemented")
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    TODO("Not yet implemented")
                }

            })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER &&
            resultCode == RESULT_OK
        )
            startPrinting()

        changePairAndUnpair()
    }

    private fun startPrinting() {
        if (Printooth.hasPairedPrinter())
            printing = Printooth.printer()
        if (printing != null)
            printing!!.printingCallback = this
    }

    private fun changePairAndUnpair() {
        if (Printooth.hasPairedPrinter())
            bluetoothPairButton?.text = "Unpair ${Printooth.getPairedPrinter()!!.name}"
        else
            bluetoothPairButton?.text = "Pair with printer"
    }

    override fun connectingWithPrinter() {
        Toast.makeText(this, "Connecting to Printer", LENGTH_SHORT).show()
    }

    override fun connectionFailed(error: String) {
        Toast.makeText(this, "Failed: $error", LENGTH_SHORT).show()
    }

    override fun onError(error: String) {
        Toast.makeText(this, "Error: $error", LENGTH_SHORT).show()
    }

    override fun onMessage(message: String) {
        Toast.makeText(this, "Message: $message", LENGTH_SHORT).show()
    }

    override fun printingOrderSentSuccessfully() {
        Toast.makeText(this, "Printing...", LENGTH_SHORT).show()
    }


    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val smsReceived = "android.provider.Telephony.SMS_RECEIVED";
            val tag = "SMSBroadcastReceiver";

            Log.i(tag, "Intent received: " + intent!!.action)

            if (intent.action === smsReceived) {
                val bundle = intent.extras
                if (bundle != null) {
                    val pdus = bundle["pdus"] as Array<*>?
                    val messages: Array<SmsMessage?> = arrayOfNulls(pdus!!.size)
                    for (i in pdus.indices) {
                        messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                    }
                    if (messages.size > -1) {
                        Log.i(tag, "Message received: " + (messages[0]?.messageBody))
                    }
                }
            }
        }
    }

}
