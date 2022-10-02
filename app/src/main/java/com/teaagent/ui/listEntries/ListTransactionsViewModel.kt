package com.teaagent.ui.listEntries

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.teaagent.data.FirebaseUtil
import com.teaagent.domain.firemasedbEntities.BalanceTx
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


// 1
class ListTransactionsViewModel() : ViewModel() {
    val TAG: String = "ListEntryViewModel"
    val customerNames = MutableLiveData<List<String>>()
    val customerEntities = MutableLiveData<List<BalanceTx>>()
    val reportEntities = MutableLiveData<List<BalanceTx>>()


    suspend fun getByNameAndDateFromFirebaseDb(
        customerName: String,
        startDate: Long
    )/*: ArrayList<String> */ {
        var customers: ArrayList<BalanceTx> = ArrayList()
//        var customers: ArrayList<String> = ArrayList()

        var entryTimestampDate = startDate


        val job = GlobalScope.async {
            var task: Task<QuerySnapshot>? =
                FirebaseUtil.getByNameAndDate(
                    customerName,
                    BalanceTx.convertDate(entryTimestampDate)
                )
            task?.addOnCompleteListener(OnCompleteListener<QuerySnapshot?> { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {

                        var c: BalanceTx = document.toObject(BalanceTx::class.java)

//                        customers.add(c.toString())
                        customers.add(c)

//                    Log.d(FirebaseUtil.TAG, document.id + " => " + document.data)
                        Log.d(FirebaseUtil.TAG, "toObject" + " => " + c.toString())
//                        Log.d(FirebaseUtil.TAG, "netTotal" + " => " + c.netTotal)
                    }

                } else {
                    Log.e(FirebaseUtil.TAG, "Error getting documents: ", task.exception)
                }
            })
            task?.addOnSuccessListener { it ->
                Log.d(
                    FirebaseUtil.TAG,
                    "*****************addOnSuccessListener ********************* customers $customers"
                )
//                customerNames.postValue(customers)
                customerEntities.postValue(customers)
            }
        }
    }



    suspend fun getByNameFirebaseDb(
        customerName: String){
        var customers: ArrayList<BalanceTx> = ArrayList()
        GlobalScope.async {
            var task: Task<QuerySnapshot>? =
                FirebaseUtil.getByName(customerName)
            task?.addOnCompleteListener(OnCompleteListener<QuerySnapshot?> { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        var c: BalanceTx = document.toObject(BalanceTx::class.java)
                        customers.add(c)
                        Log.d(FirebaseUtil.TAG, "toObject" + " => " + c.toString())
                    }

                } else {
                    Log.e(FirebaseUtil.TAG, "Error getting documents: ", task.exception)
                }
            })
            task?.addOnSuccessListener { it ->
                Log.d(
                    FirebaseUtil.TAG,
                    "***************** reportEntities addOnSuccessListener ********************* customers $customers"
                )
                reportEntities.postValue(customers)
            }
        }
    }
}
