package untad.aldochristopherleo.sispenboulder.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import untad.aldochristopherleo.sispenboulder.data.User


class MainViewModel: ViewModel() {

    private val _user = MutableLiveData<User>()
    val user : LiveData<User> = _user

    private val _ahpPriority = MutableLiveData<ArrayList<Double>>()
    val ahpPriority : LiveData<ArrayList<Double>> = _ahpPriority

    init {
        getCurrentUser()
        getAhpPriority()
    }

    private fun getUserId() = FirebaseAuth.getInstance().currentUser

    private fun getCurrentUser() {
        val currentUser = getUserId()
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("users/${currentUser?.uid}")

        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val currentUserData = snapshot.getValue<User>()
                    _user.postValue(currentUserData!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    fun getAllParticipants(): DatabaseReference =
        Firebase.database.reference.child("participant")

    fun getEvents(): DatabaseReference =
        Firebase.database.reference.child("events")

    fun getParticipant(eventName : String): DatabaseReference =
        Firebase.database.reference.child("events/$eventName/participant")

    fun getResult(eventName: String): DatabaseReference =
        Firebase.database.reference.child("result/$eventName")

    private fun getAhpPriority(){
        val result = ArrayList<Double>()
        Firebase.database.reference.child("algorithm")
            .child("ahp").child("criteria").get().addOnSuccessListener {
                if (it.exists()){
                    for (item in it.children){
                        result.add(item.value.toString().toDouble())
                    }
                } else {
                    for (i in 1..12){
                        result.add(0.0)
                    }
                }
                _ahpPriority.postValue(result)
            }
    }
}