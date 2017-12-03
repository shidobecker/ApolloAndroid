package shido.com.apolloandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.rx2.Rx2Apollo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import shido.com.apolloandroid.api.FindTrips
import shido.com.apolloandroid.api.GetAllTrips
import shido.com.apolloandroid.api.fragment.TripFields


class MainActivity : AppCompatActivity() {

    private var observable: Observable<List<TripFields>>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val apolloClient = GraphqlServiceGenerator.createService()
        observable = Rx2Apollo.from(apolloClient.query(GetAllTrips()).watcher())
                .subscribeOn(Schedulers.io())
                .map { response -> getAllTripsFields(response) }
                .cache()
                .observeOn(AndroidSchedulers.mainThread())

        buildAdapter()


        observable?.subscribe({ response ->
            tripAdapter.allTrips = response
            tripAdapter.notifyDataSetChanged()
        }, { onError -> Log.e("Error", "Error") })
    }


    private fun getAllTripsFields(response: Response<GetAllTrips.Data>): List<TripFields> {
        Log.e("GET ALL", "GET ALL")
        if (response.hasErrors()) {
            throw RuntimeException(response.errors().get(0).message())

        }
        response.data()?.allTrips()?.forEach {
            Log.e("PLOG", "${it.fragments().tripFields()?.title()}  ${it.fragments().tripFields()?.id()}")
        }

        return response.data()?.allTrips()?.map {
            it?.fragments()?.tripFields()!!
        }!!
    }

    private fun buildAdapter(): RecyclerView.Adapter<*> {
        tripAdapter = TripAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = tripAdapter
        return TripAdapter(this)
    }


    lateinit var tripAdapter: TripAdapter
    var search: MenuItem? = null

    private fun updateTitle(query: String?) {
        val apolloClient = GraphqlServiceGenerator.createService()
        val call = apolloClient.query(FindTrips(checkNotNull(query)))

        /* observable = Rx2Apollo.from(apolloClient.query(FindTrips(query!!)).watcher())
                 .subscribeOn(Schedulers.io())
                 .map { response -> getAllFindFields(response) }
                 .cache()
                 .observeOn(AndroidSchedulers.mainThread())
         observable?.subscribe()*/



        call.enqueue(object : ApolloCall.Callback<FindTrips.Data>() {
            override fun onFailure(e: ApolloException) {
                Log.e("ERROR", "ERROR QUERY")
            }

            override fun onResponse(response: Response<FindTrips.Data>) {
                val data = response.data()
                data?.findTrips()?.forEach {
                    Log.e("response query", it.fragments().tripFields().title())
                }
                getAllFindFields(response)
            }

        })


    }


    private fun getAllFindFields(response: Response<FindTrips.Data>): List<TripFields> {
        Log.e("GET ALL", "GET ALL")
        if (response.hasErrors()) {
            throw RuntimeException(response.errors().get(0).message())

        }
        var listOfTripFields: List<TripFields> = listOf()
        runOnUiThread {
            response.data()?.findTrips()?.forEach {
                Log.e("PLOG", "${it.fragments().tripFields().title()}  ${it.fragments().tripFields().id()}")
            }

            listOfTripFields =
                    response.data()?.findTrips()?.map {
                        it?.fragments()?.tripFields()!!
                    }!!
            tripAdapter.allTrips = listOfTripFields
            tripAdapter.notifyDataSetChanged()
        }
        return listOfTripFields
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        search = menu?.findItem(R.id.search)
        val sv = search?.actionView as android.widget.SearchView
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                updateTitle(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })

        sv.setOnCloseListener { return@setOnCloseListener true }
        sv.isSubmitButtonEnabled = true
        sv.setIconifiedByDefault(true)

        return super.onCreateOptionsMenu(menu)

    }
}
