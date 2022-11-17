package com.teaagent.ui.listEntries

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.teaagent.R
import com.teaagent.data.FirebaseUtil
import com.teaagent.databinding.ActivityShowTradeListBinding
import com.teaagent.domain.firemasedbEntities.TradeAnalysis
import com.teaagent.domain.firemasedbEntities.enums.stockEntry.TradeIncomeType
import com.teaagent.ui.report.xcel.Constants
import com.teaagent.ui.report.xcel.ExcelUtils
import com.teaagent.ui.saveentry.SaveAccountDetailActivity
import com.teaagent.ui.saveentry.SaveAccountViewModel
import com.teaagent.ui.saveentry.SaveEntryViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class TradeListActivity : AppCompatActivity(), ItemClickListener {
    val TAG: String = "ListTransactions"
    private lateinit var instituteName: String
    private var kg: Double = 0.0
    private var amount: Double = 0.0
    private lateinit var binding: ActivityShowTradeListBinding

    // Repository

    //        var recycleViewAdapter: CustomAdapter? = null
    var recycleViewAdapter: TradeListNewAdapter? = null


    //    var recycleViewAdapter: ItemAdapter? = null
    var dateTime = Calendar.getInstance()
    var data = ArrayList<String>()

//    //    // ViewModel
//    private val listEntryActivityyViewModel: ListTransactionsViewModel by viewModels {
//        ListTransactionsViewModelFactory()
//    }


    // ViewModel
    private val saveAccountDetailViewModel: SaveAccountViewModel by viewModels {
        SaveEntryViewModelFactory()
    }

    var recyclerview: RecyclerView? = null
    var search: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = ActivityShowTradeListBinding.inflate(layoutInflater)
        val view = binding.root
        getSupportActionBar()?.setDisplayShowTitleEnabled(false)

        recyclerview = binding.list
        search = binding.search
        recyclerview!!.layoutManager = LinearLayoutManager(this)

        search?.doOnTextChanged { text, _, _, _ ->
            val query = text.toString()//.toLowerCase(Locale.getDefault())
            filterWithQuery(query)
        }

        setContentView(view)

        declareTypeSpinner()

        // getAccountDetails()
        getAllStockListOfThePhoneUser()
        // setSearchTransactionsClickListener()
        setClickListenerOfAllStockListOfThePhoneUser()
        setClickListenerAllOpenStockList()
        setClickListenerAllExitedTradeDetails()

        buttonCalender()
        sendClick()

//        registerEditTextChangeListenerrs()
    }
    /* fun onTradeListRowClicked(view: View?,position: Int) {
         clearPreviousUserInputValusWithIntentExtras()
     }*/

    private fun filterWithQuery(query: String) {
        if (query.isNotEmpty()) {
            val filteredList: List<TradeAnalysis> = onQueryChanged(query)
            tradeList = filteredList as ArrayList<TradeAnalysis>
            attachAdapter(filteredList)
        } else if (query.isEmpty()) {
            tradeList?.let { attachAdapter(it) }
        }
    }

    private fun attachAdapter(list: List<TradeAnalysis>) {
        recycleViewAdapter = TradeListNewAdapter(list, this)
        Log.d(TAG, "recycleViewAdapter  list " + list)
        recyclerview?.adapter = recycleViewAdapter
    }

    private fun onQueryChanged(filterQuery: String): List<TradeAnalysis> {
        val filteredList = ArrayList<TradeAnalysis>()

        for (currentSport in tradeList!!) {
            if (currentSport.stockName?.toLowerCase(Locale.getDefault())
                    ?.contains(filterQuery) == true
            ) {
                filteredList.add(currentSport)
            }
        }
        return filteredList
    }

    /*  private fun getAccountDetails() {
          lifecycleScope.launch {
              showProgressDialog()
              saveAccountDetailViewModel.getAllAccountDetailsFirebaseDb()
          }

          saveAccountDetailViewModel.tradeDetailsLiveData.observe(this, Observer() { it ->
              getAccountInfos(it as ArrayList<TradeAnalysis>)

            val dataList=  it as ArrayList<TradeAnalysis>
              ExcelUtils.exportDataIntoWorkbook(this,"tradeAlalysisReport",dataList)
              dismissProgressDialog()
          })
      }*/

    var total: Long = 0
    private fun convertBalanceTxToStringList(customers: ArrayList<TradeAnalysis>): ArrayList<String> {
        total = 0
        var customerString: ArrayList<String> = ArrayList()
        for (customer in customers) {
            val customerText: String = convertBalanceTxToString(customer)
            customerString.add(customerText)

//            val balanceAmount =
//                customer?.balanceAmount
//            val balanceAmount =
//                StringEncryption.decryptMsg(customer?.balanceAmount).toString()
            total = 0;//TODO commented temp total + balanceAmount.toLong()
        }
        return customerString
    }

    private fun convertBalanceTxToString(tradeAnalysis: TradeAnalysis): String {
        val id = tradeAnalysis?.id
        val stockName =
            tradeAnalysis?.stockName


        val tradeIncomeType = tradeAnalysis?.tradeIncomeType.toString()
        val isBuy =
            tradeAnalysis?.isBuy

        val EntryPrice =
            tradeAnalysis?.EntryPrice
        val ExitPrice =
            tradeAnalysis?.ExitPrice
        val SLPrice =
            tradeAnalysis?.SLPrice

        val sLLevel =
            tradeAnalysis?.sLLevel
        val targetLevel =
            tradeAnalysis?.targetLevel


        val timestampTradePlanned =
            tradeAnalysis?.timestampTradePlanned
        val phoneUserName =
            tradeAnalysis?.phoneUserName.toString()

        val HTFLocation =
            tradeAnalysis?.HTFLocation
        val HTFTrend =
            tradeAnalysis?.HTFTrend
        val ITFTrend =
            tradeAnalysis?.ITFTrend
        val ExecutionZone =
            tradeAnalysis?.ExecutionZone
        val note =
            tradeAnalysis?.note

        val entryEmotion = tradeAnalysis?.entryEmotion


        val tradeManagementType =
            tradeAnalysis?.tradeManagementType
        val tradeExitPostAnalysisTypeType =
            tradeAnalysis?.tradeExitPostAnalysisTypeType
        val missedTradeType =
            tradeAnalysis?.missedTradeType
        val mentalState =
            tradeAnalysis?.mentalState
        val confidenceLevel =
            tradeAnalysis?.confidenceLevel
        val exitNote =
            tradeAnalysis?.exitNote
        val timestampTradeExited =
            tradeAnalysis?.timestampTradeExited
        val executionTrend =
            tradeAnalysis?.executionTrend
        val quantity =
            tradeAnalysis?.quantity

        val noteMistake =
            tradeAnalysis?.noteMistake
        val noteImpromement =
            tradeAnalysis?.noteImpromement

        val b =
            TradeAnalysis(
                id,
                phoneUserName,
                tradeIncomeType,
                stockName,
                isBuy,

                EntryPrice,
                SLPrice,
                ExitPrice,

                sLLevel,
                targetLevel,

                HTFLocation,
                HTFTrend,
                ITFTrend,
                executionTrend,
                ExecutionZone,

                tradeManagementType,
                tradeExitPostAnalysisTypeType,
                missedTradeType,
                mentalState,
                confidenceLevel,
                exitNote,

                entryEmotion,

                timestampTradePlanned,
                timestampTradeExited,
                note,
                quantity,
                noteMistake,
                noteImpromement

            )
        return b.toString()
    }

    private fun buttonCalender() {
        binding.buttonfromDate.setOnClickListener {

            Toast.makeText(this, "in upcoming version", Toast.LENGTH_SHORT).show()

            val c = Calendar.getInstance()
            val mYear = c[Calendar.YEAR]
            val mMonth = c[Calendar.MONTH]
            val mDay = c[Calendar.DAY_OF_MONTH]

            val datePickerDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    dateTime.set(mYear, monthOfYear, dayOfMonth)

                    binding.tViewFromDate.text =
                        dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                },
                mYear,
                mMonth,
                mDay
            )
            datePickerDialog.show()
        }
    }

    var mProgressDialog: ProgressDialog? = null

    /* private fun setSearchTransactionsClickListener() {
         showProgressDialog()
         binding.buttonSearchCustomerName.setOnClickListener {
             GlobalScope.launch(Dispatchers.Main) {
                 listEntryActivityyViewModel.getTxByTypeFromFirebaseDb(
                     institutionType!!
                 )
             }
         }
         allTradeDetailsMutableLiveDataCallback()

     }
 */
    var customerString: ArrayList<String>? = null
    var tradeList: ArrayList<TradeAnalysis>? = null


    private suspend fun allTradeDetailsMutableLiveDataCallback() {
        saveAccountDetailViewModel.tradeDetailsLiveData.observe(this, Observer { it ->
            if (it != null) {
                dismissProgressDialog()
                binding.buttonShareScreen.visibility = View.VISIBLE
                binding.search.visibility = View.VISIBLE

                Log.d(TAG, "***************** ********************* customers $it")

                customerString = convertBalanceTxToStringList(it as ArrayList<TradeAnalysis>)
                tradeList = it


                /* recycleViewAdapter = CustomAdapter(customerString, this)
                 recyclerview?.adapter = recycleViewAdapter*/

                recycleViewAdapter = TradeListNewAdapter(it, this)
                recyclerview?.adapter = recycleViewAdapter

                binding.totalAmount.setText("Total trades : " + it.size)


            }
        })
    }

    fun getAllStockListOfThePhoneUser() {
        showProgressDialog()
        GlobalScope.launch(Dispatchers.Main) {
            saveAccountDetailViewModel.getAllStockListOfThePhoneUser(false, false)
            allTradeDetailsMutableLiveDataCallback()
        }
    }


    fun setClickListenerOfAllStockListOfThePhoneUser() {
        /*binding.buttonGetNetAssetsByName.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                saveAccountDetailViewModel.getAllStockListOfThePhoneUser(false, false)
            }
        }
        GlobalScope.launch(Dispatchers.Main) { // launches coroutine in main thread
            allTradeDetailsMutableLiveDataCallback()
        }*/
        getAllStockListOfThePhoneUser()
    }


    fun setClickListenerAllOpenStockList() {
        showProgressDialog()
        binding.btnGetOpenTradeList.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                saveAccountDetailViewModel.getAllStockListOfThePhoneUser(true, false)
            }
        }
        GlobalScope.launch(Dispatchers.Main) { // launches coroutine in main thread
            allTradeDetailsMutableLiveDataCallback()
        }
    }

    fun setClickListenerAllExitedTradeDetails() {
        showProgressDialog()
        binding.btnGetExitedTradeList.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                saveAccountDetailViewModel.getAllStockListOfThePhoneUser(false, true)
            }
        }
        GlobalScope.launch(Dispatchers.Main) { // launches coroutine in main thread
            allTradeDetailsMutableLiveDataCallback()
        }
    }

    private fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
        }
        mProgressDialog?.setTitle("Getting Trade list...")
        mProgressDialog?.show()
    }

    private fun dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog?.dismiss()
        }
    }

    private fun sendClick() {
        binding.buttonShareScreen.setOnClickListener {
            val dataList = tradeList as ArrayList<TradeAnalysis>
            ExcelUtils.exportDataIntoWorkbook(this, Constants.EXCEL_FILE_NAME, dataList)
            dismissProgressDialog()
        }
//        {
//            if (instituteName.length > 0) {
//                val listActiviTyIntent = Intent(this, ReportActivity::class.java)
//                listActiviTyIntent.putExtra(ReportActivityBundleTag, instituteName)
//                startActivity(listActiviTyIntent)
//            } else {
//                Toast.makeText(
//                    this, "Please select customer ",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
    }

    private fun getAccountInfos(list: ArrayList<TradeAnalysis>) {

        var customerNames: ArrayList<String> = ArrayList()
        for (customer in list) {
            customer.stockName?.let { customerNames.add(it) }
        }

        val hashSet: HashSet<String> = HashSet()
        hashSet.addAll(customerNames!!)

        val customerNAmes: MutableList<String> = ArrayList()
        customerNAmes.add("Select name")
        customerNAmes.addAll(hashSet)


        val spinner: Spinner = findViewById(R.id.spinnerSearchCustomerName)
        val adapter: ArrayAdapter<Any?> =
            ArrayAdapter<Any?>(
                this, android.R.layout.simple_spinner_dropdown_item,
                customerNAmes!! as List<Any?>
            )
        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                instituteName = customerNAmes?.get(position).toString()
                Log.d(TAG, "onItemSelected instituteName " + instituteName)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        })
        spinner.adapter = adapter
    }

    /* fun updateList(list: List<DataHolder?>) {
         displayedList = list
         notifyDataSetChanged()
     }

     @OnTextChanged(R.id.feature_manager_search)
     protected fun onTextChanged(text: CharSequence) {
         filter(text.toString())
     }
     fun filter(text: String?) {
         val temp: MutableList<DataHolder> = ArrayList()
         for (d in displayedList) {
             //or use .equal(text) with you want equal match
             //use .toLowerCase() for better matches
             if (d.getEnglish().contains(text)) {
                 temp.add(d)
             }
         }
         //update recyclerview
         updateList(temp)
     }*/
    var institutionType: String? = null

    private fun declareTypeSpinner() {
        val spinner: Spinner = findViewById(R.id.spinnerInstituteType)
        val enumValues: List<Enum<*>> = ArrayList(
            EnumSet.allOf(
                TradeIncomeType::class.java
            )
        )

        val adapter: ArrayAdapter<Any?> =
            ArrayAdapter<Any?>(
                this, android.R.layout.simple_spinner_dropdown_item,
                enumValues!! as List<Any?>
            )
        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                institutionType = enumValues?.get(position).toString()
                Log.d(TAG, "onItemSelected accountType " + institutionType)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                institutionType = enumValues?.get(0).toString()
                Log.d(TAG, "default onItemSelected accountType " + institutionType)

            }
        })
        spinner.adapter = adapter
    }


    override fun onClick(position: Int) {
        val balanceTx: TradeAnalysis? = tradeList?.get(position)
        val txId = balanceTx?.id
        Toast.makeText(
            this,
            "id " + txId,
            Toast.LENGTH_LONG
        ).show()

        var task: Task<DocumentSnapshot>? = FirebaseUtil.getTxById(txId!!)
        task?.addOnSuccessListener(OnSuccessListener { it ->
            it.data

            val i = Intent(this, SaveAccountDetailActivity::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.putExtra("balanceTx", balanceTx)
            this.startActivity(i)

            Toast.makeText(
                this,
                it.data.toString(),
                Toast.LENGTH_LONG
            ).show()

        })
        task?.addOnCompleteListener(OnCompleteListener { it ->

//            Toast.makeText(
//                this,
//                "completed ",
//                Toast.LENGTH_LONG
//            ).show()
        })


    }


}



