package com.example.newsapplication.editor

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.newsapplication.News
import com.example.newsapplication.R
import com.google.firebase.database.FirebaseDatabase

class NewsDetailActivity : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvDescription: TextView
    private lateinit var imgNews: ImageView
    private lateinit var tvReporterEmail: TextView
    private lateinit var btnPublish: Button
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button

    private var news: News? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)

        // Initialize views
        tvTitle = findViewById(R.id.tv_Title)
        tvCategory = findViewById(R.id.tv_Category)
        tvDescription = findViewById(R.id.tv_Description)
        imgNews = findViewById(R.id.img_News)
        tvReporterEmail = findViewById(R.id.tv_ReporterEmail)
        btnPublish = findViewById(R.id.btn_Publish)
        btnEdit = findViewById(R.id.btn_Edit)
        btnDelete = findViewById(R.id.btn_Delete)

        // Retrieve the news object passed from the previous activity
        news = intent.getSerializableExtra("news") as? News

        news?.let {
            // Set the data to the views
            tvTitle.text = it.title
            tvCategory.text = it.category
            tvDescription.text = it.description

            // Get only name of the reporter. Without @gmail.com
            val reporterName = it.reporterEmail.split("@")[0]
            tvReporterEmail.text = "Reported by: $reporterName"

            // Load the image using Glide
            Glide.with(this)
                .load(it.imageUrl)
                .into(imgNews)
        }

        // Handle Publish Button click with confirmation dialog
        btnPublish.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Publish News")
                .setMessage("Are you sure you want to publish this news?")
                .setPositiveButton("Yes") { _, _ ->
                    news?.let {
                        it.status = "published"

                        val newsRef = FirebaseDatabase.getInstance().getReference("news")
                        newsRef.child(it.newsId).setValue(it)
                            .addOnSuccessListener {
                                Toast.makeText(this, "News published successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to publish news", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .setNegativeButton("No", null)
                .create()
            alertDialog.show()
        }

        // Handle Edit Button click
        btnEdit.setOnClickListener {
            val intent = Intent(this, EditNewsActivity::class.java)
            intent.putExtra("news", news)
            startActivity(intent)
        }

        // Handle Delete Button click with confirmation dialog
        btnDelete.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Delete News")
                .setMessage("Are you sure you want to delete this news?")
                .setPositiveButton("Yes") { _, _ ->
                    news?.let {
                        val newsRef = FirebaseDatabase.getInstance().getReference("news")
                        newsRef.child(it.newsId).removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(this, "News deleted successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to delete news", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .setNegativeButton("No", null)
                .create()
            alertDialog.show()
        }
    }
}
