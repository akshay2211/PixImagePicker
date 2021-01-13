package com.fxn.utility

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import com.fxn.modals.Img
import java.util.*

/**
 * Created by akshay on 02/13/20.
 */
open class ImageVideoFetcher(private val context: Context) : AsyncTask<Cursor?, Void?, ImageVideoFetcher.ModelList>() {
    var startingCount = 0
    @JvmField
    var header = ""
    private val selectionList = ArrayList<Img>()
    private val LIST = ArrayList<Img>()
    var preSelectedUrls = ArrayList<String>()
        private set

    fun setPreSelectedUrls(preSelectedUrls: ArrayList<String>): ImageVideoFetcher {
        this.preSelectedUrls = preSelectedUrls
        return this
    }

    override fun doInBackground(vararg params: Cursor?): ModelList? {
        val cursor = params[0]
        try {
            if (cursor != null) {
                val date = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)
                val data = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                val mediaType = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE)
                val contentUrl = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)
                val displayname = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
                val title = cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE)
                val parent = cursor.getColumnIndex(MediaStore.Files.FileColumns.PARENT)

                //int videoDate = cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN);
                val imageDate = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)
                var limit = 100
                if (cursor.count < limit) {
                    limit = cursor.count - 1
                }
                cursor.move(limit)
                synchronized(context) {
                    var pos = startingCount
                    for (i in limit until cursor.count) {
                        cursor.moveToNext()
                        val path = Uri.withAppendedPath(Constants.IMAGE_VIDEO_URI, "" + cursor.getInt(contentUrl))
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = cursor.getLong(imageDate) * 1000
                        val dateDifference = Utility.getDateDifference(context, calendar)
                        //Log.e("difference "+i,"->  "+dateDifference);
                        val media_type = cursor.getInt(mediaType)
                        val display_name = cursor.getString(displayname)
                        val Title = cursor.getString(title)
                        val Parent = cursor.getString(parent)
                        /*Log.e("all data", "->  mediaType "
							+ mediaType
							+ " "
							+ media_type
							+ "  "
							+ i
							+ "  "
							+ display_name
							+ "  "
							+ Title
							+ "  "
							+ Parent);*/
                        val im = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                        if (!header.equals("" + dateDifference, ignoreCase = true)) {
                            header = "" + dateDifference
                            pos += 1
                            LIST.add(Img("" + dateDifference, "", "", "", media_type))
                        }
                        val img = Img("" + header, "" + path, cursor.getString(data), "" + pos, media_type)
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