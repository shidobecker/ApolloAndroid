package shido.com.apolloandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import shido.com.apolloandroid.api.GetAllTrips


class MainActivity : AppCompatActivity() {

    private var observable: Observable<GetAllTrips.Data>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val apolloClient = GraphqlServiceGenerator.createService()
        Log.e("AP", "AP")
        observable = Rx2Apollo.from(apolloClient.query(GetAllTrips()).watcher())
                .subscribeOn(Schedulers.io())
                .map { response -> getAllTripsFields(response) }
                .cache()
                .observeOn(AndroidSchedulers.mainThread())

       // val list = observable?.allTrips

        observable?.subscribe({response -> buildAdapter(response)}, { onError -> Log.e("Error", "Error")})
    }


    override fun onResume() {
        super.onResume()

    }

    private fun getAllTripsFields(response: Response<GetAllTrips.Data>): GetAllTrips.Data {
        Log.e("GET ALL", "GET ALL")
        if (response.hasErrors()) {
            throw RuntimeException(response.errors().get(0).message())
        }else{
            response.data()?.allTrips()?.forEach {
                Log.e("PLOG", "${it.title()}  ${it.id()}")
            }
            Log.e("Plog", "")
        }
        return response.data()!!
    }

    private fun buildAdapter(response: GetAllTrips.Data): RecyclerView.Adapter<*> {
        val adapter = TripAdapter(response.allTrips(),this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

        return TripAdapter(response.allTrips(),this)
    }
}
