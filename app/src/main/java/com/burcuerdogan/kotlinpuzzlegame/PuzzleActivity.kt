package com.burcuerdogan.kotlinpuzzlegame

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.WorkSource
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class PuzzleActivity : AppCompatActivity() {

    var pieces : ArrayList<PuzzlePiece>? = null
    var mCurrentPhotoPath :String? = null
    var mCurrentPhotoUri :String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle)
        val layout = findViewById<RelativeLayout>(R.id.layout)
        val imageView = findViewById<ImageView>(R.id.imageView)
        val intent = intent
        val assetName = intent.getStringExtra("assetName")
        mCurrentPhotoPath = intent.getStringExtra("mCurrentPhotoPath")
        mCurrentPhotoUri = intent.getStringExtra("mCurrentPhotoUri")

        //run image related code after the view was laid out
        //to have all dimensions calculated

        imageView.post {
            if (assetName != null){
                setPicFromAsset(assetName.imageView)
            }
            else if (mCurrentPhotoPath != null){
                setPicFromPhotoPath(mCurrentPhotoPath,imageView)
            }
            else if (mCurrentPhotoUri != null){
                imageView.setImageURI(Uri.parse(mCurrentPhotoUri))
            }
            pieces = splitImage()
            val touchListener = TouchListener(this@PuzzleActivity)
            //shuffle pieces order
            Collections.shuffle(pieces)
            for (piece in pieces!!){
                piece.setOnTouchListener(touchListener)
                layout.addView(piece)

                //randomize position, on the bottom of the screen
                val lParams = piece.layoutParams as RelativeLayout.LayoutParams
                lParams.leftMargin = Random.nextInt(
                    layout.width - piece.pieceWidth
                )
                lParams.topMargin = layout.height - piece.pieceHeigth

                piece.layoutParams = lParams

            }
        }

    }

    private fun setPicFromAsset(assetName: String,imageView: ImageView?) {

        val targetW = imageView!!.width
        val targetH = imageView.height
        val am = assets

        try {
            val `is` = am.open("img/$assetName")
            //Get the dimensions of the bitmap
            val bmOPtion = BitmapFactory.Options()
            BitmapFactory.decodeStream(
                `is`, Rect(-1,-1,-1,-1),bmOPtion
            )
            val photoW = bmOPtion.outWidth
            val photoH = bmOPtion.outHeight

            //Determine how much to scale down the image
            val scalFctor = Math.min(
                photoW/targetW,photoH/targetH
            )

            //Decode the image file into a Bitmap sized to fill the view
            bmOPtion.inJustDecodeBounds = false
            bmOPtion.inSampleSize = scalFctor
            bmOPtion.inPurgeable = true
            val bitmap = BitmapFactory.decodeStream(
                `is`, Rect(-1,-1,-1,-1),bmOPtion
            )
            imageView.setImageBitmap(bitmap)



        }catch (e:IOException){
            e.printStackTrace()
            Toast.makeText(this@PuzzleActivity,e.localizedMessage,Toast.LENGTH_SHORT).show()
        }

    }
    private fun splitImage():ArrayList<PuzzlePiece?>{

        val piecesNumber = 12
        val rows = 4
        val cols = 3
        val imageView = findViewById<ImageView>(R.id.imageView)
        val pieces = ArrayList<PuzzlePiece?>(piecesNumber)

        //Get the scaled bitmap of the source image
        val drawable = imageView.drawable as BitmapDrawable
        val bitmap = drawable.bitmap

        val dimensions = getBitmapPositionInsideImageView(imageView)

        val scaledBitmapLeft = dimensions[0]
        val scaledBitmapTop = dimensions[1]
        val scaledBitmapWidth = dimensions[2]
        val scaledBitmapHeight = dimensions[3]

        val croppedImageWidth = scaledBitmapWidth - 2 * Math.abs(scaledBitmapLeft)
        val croppedImageHeight = scaledBitmapHeight - 2 * Math.abs(scaledBitmapTop)

        val scaledBitmap = Bitmap.createScaledBitmap(
            bitmap,scaledBitmapWidth,scaledBitmapHeight,true
        )
        val croppedBitmap = Bitmap.createBitmap(
            ReateBitmap(scaledBitmap,
                Math.abs(scaledBitmapLeft),
                Math.abs(scaledBitmapTop),croppedImageWidth,croppedImageHeight)
        )

        //calculate the with and height of the pieces





    }
    companion object{
        fun rotateImage(source:Bitmap,angle:Float):Bitmap {

            val matrix = Matrix()
            matrix.postRotate(angle)

            return Bitmap.createBitmap(
                source, 0, 0,source.width,source.height,matrix,true
            )

        }
    }
}