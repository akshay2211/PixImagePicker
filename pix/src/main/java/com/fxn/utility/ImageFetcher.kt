package com.fxn.utility

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import com.fxn.modals.Img
import java.util.*

/**
 * Created by akshay on 06/04/18.
 */
class ImageFetcher(private val context: Context) : AsyncTask<Cursor?, Void?, ImageFetcher.ModelList>() {
    var startingCount = 0
    var header = ""
    private val selectionList = ArrayList<Img>()
    private val LIST = ArrayList<Img>()
    var preSelectedUrls = ArrayList<String>()
        private set

    fun setPreSelectedUrls(preSelectedUrls: ArrayList<String>): ImageFetcher {
        this.preSelectedUrls = preSelectedUrls
        return this
    }

    override fun doInBackground(vararg params: Cursor?): ModelList {
        val cursor = params[0]
        try {
            if (cursor != null) {
                val date = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)
                val data = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                val contentUrl = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                var limit = 100
                if (cursor.count < limit) {
                    limit = cursor.count - 1
                }
                cursor.move(limit)
                synchronized(context) {
                    var pos = startingCount
                    for (i in limit until cursor.count) {
                        cursor.moveToNext()
                        val path = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + cursor.getInt(contentUrl))
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = cursor.getLong(date) * 1000
                        val dateDifference = Utility.getDateDifference(context, calendar)
                        if (!header.equals("" + dateDifference, ignoreCase = true)) {
                            header = "" + dateDifference
                            pos += 1
                            LIST.add(Img("" + dateDifference, "", "", "", 1))
                        }
                        val img = Img("" + header, "" + path, cursor.getString(data), "" + pos,
                                1)
                        img.position = pos
                        if (preSelectedUrls.contains(img.url)) {
                            img.selected = true
                            selectionList.add(img)
                        }
                        pos += 1
                        LIST.add(img)
                    }
                    cursor.close()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ModelList(LIST, selectionList)
    }

    inner class ModelList(LIST: ArrayList<Img>, selection: ArrayList<Img>) {
        var lIST = ArrayList<Img>()
        var selection = ArrayList<Img>()

        init {
            lIST = LIST
            this.selection = selection
        }
    }
}