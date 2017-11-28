package shido.com.apolloandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import shido.com.apolloandroid.api.GetAllTrips


class MainActivity : AppCompatActivity() {

    private var observable: Observable<GetAllTrips.Data>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val apolloClient = GraphqlServiceGenerator.createService()
        observable = Rx2Apollo.from(apolloClient.query(GetAllTrips()).watcher())
                .subscribeOn(Schedulers.io())
                .map { response ->
                    getAllTripsFields(response)

                }
                .cache()
                .observeOn(AndroidSchedulers.mainThread())




    }


    override fun onResume() {
        super.onResume()

    }

    private fun getAllTripsFields(response: Response<GetAllTrips.Data>): GetAllTrips.Data {
        if (response.hasErrors()) {
            throw RuntimeException(response.errors().get(0).message())
        }else{
            Log.e("Plog", "")
        }
        return response.data()!!
    }

    private fun buildAdapter(response: GetAllTrips.Data): RecyclerView.Adapter<*> {
        return TripAdapter(response.allTrips(),this)
    }
}
