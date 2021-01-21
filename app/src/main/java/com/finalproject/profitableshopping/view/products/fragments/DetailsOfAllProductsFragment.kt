package com.finalproject.profitableshopping.view.products.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.finalproject.profitableshopping.R
import com.finalproject.profitableshopping.data.models.Comment
import com.finalproject.profitableshopping.data.models.Product
import com.finalproject.profitableshopping.viewmodel.CategoryViewModel
import com.finalproject.profitableshopping.viewmodel.CommentViewModel
import com.finalproject.profitableshopping.viewmodel.ProductViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_products_details.*

private const val ARG_PRODUCT_ID = "product_id"

class DetailsOfAllProductsFragment : Fragment() {

    private var productId: String? = null
    private lateinit var progressBar: ProgressBar
    lateinit var productViewModel: ProductViewModel
    lateinit var productImageIv: ImageView
    lateinit var productNameTv: TextView
    lateinit var productQuantityTv: TextView
    lateinit var productReviewsTv: TextView
    lateinit var productRialPriceTv: TextView
    lateinit var productDollarPriceTv: TextView
    lateinit var productDescriptionTv: TextView
    lateinit var ratingBtn: FloatingActionButton
    lateinit var callbacks: ProductDetailsFragment.Callbacks
    lateinit var product: Product
    private lateinit var commentViewModel: CommentViewModel



    override fun onStart() {
        super.onStart()
        callbacks = (context as ProductDetailsFragment.Callbacks)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productViewModel = ViewModelProviders.of(this).get(ProductViewModel::class.java)
        arguments?.let {
            productId = it.getString(ARG_PRODUCT_ID)
            productViewModel.loadProduct(productId!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_details_of_all_products, container, false)

        //  progressBar = view.findViewById(R.id.progress_circular)
        productImageIv = view.findViewById(R.id.img_product_details) as ImageView
        productNameTv = view.findViewById(R.id.tv_product_name_details) as TextView
        //  productReviewsTv = view.findViewById(R.id.reviews_tv) as TextView
        // productQuantityTv = view.findViewById(R.id.quantity_tv) as TextView
        productRialPriceTv = view.findViewById(R.id.tv_product_price_rial_details) as TextView
        productDollarPriceTv = view.findViewById(R.id.tv_product_price_details) as TextView
        productDescriptionTv = view.findViewById(R.id.tv_product_desc_details) as TextView
        ratingBtn = view.findViewById(R.id.btn_rating) as FloatingActionButton

        ratingBtn.setOnClickListener {
            showDialogRating()
        }

        return view
    }

    private fun showDialogRating() {
        var builder = AlertDialog.Builder(context!!)
        builder.setTitle("Rating product")
        builder.setMessage("Please fill information")
        val itemView = LayoutInflater.from(context).inflate(R.layout.layout_rating_comment, null)

        val ratingBar = itemView.findViewById<RatingBar>(R.id.ratingBar)
        val edt_comment = itemView.findViewById<EditText>(R.id.et_comment)

        builder.setView(itemView)
        builder.setNegativeButton("Cancel") { dialogInterface, i -> dialogInterface.dismiss() }
        builder.setPositiveButton("Ok") { dialogInterface, i ->
            val comment =Comment()
            comment.rate= ratingBar.rating.toInt()
            val response = commentViewModel.addComment(comment)
            response.observe(
                viewLifecycleOwner,
                Observer { message ->
                    Toast.makeText(context, message.toString(), Toast.LENGTH_SHORT).show()
                }
            )
        }

        val dialog= builder.create()
        dialog.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //  showProgress(true)
        productViewModel.productIDetailsLiveData.observe(
            viewLifecycleOwner,
            Observer { product ->
                this.product = product
                //   showProgress(false)
                updateUi(product)
            }
        )
    }

    private fun updateUi(product: Product) {
        productNameTv.text = product.name
        productRialPriceTv.text = product.rialPrice.toString()
        productDollarPriceTv.text = product.dollarPrice.toString()
        //     productQuantityTv.text = product.quantity.toString()
        productDescriptionTv.text = product.description
        if (product.images.isNotEmpty())
            Picasso.get().also {
                val path = product.images[0].getUrl()
                it.load(path)
                    .resize(350, 350)
                    .centerCrop()
                    .placeholder(R.drawable.shoe)
                    .into(productImageIv)
            }
    }

    companion object {
        @JvmStatic
        fun newInstance(productId: String) =
            DetailsOfAllProductsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PRODUCT_ID, productId)
                }
            }
    }


}