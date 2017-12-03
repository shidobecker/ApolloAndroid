package shido.com.apolloandroid;

import com.apollographql.apollo.ApolloClient;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;

/**
 * Created by mira on 28/11/2017.
 */

public class GraphqlServiceGenerator {

    public static String BASE_GRAPHQL_URL = "https://graphql-demo.commonsware.com/0.3/graphql";
    public static String LOCAL_SERVER = "http://192.168.1.2:4000/0.3/graphql";

    private static OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();

    private static ApolloClient.Builder apolloClient = ApolloClient.builder();


    public static  ApolloClient createService() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
            .setLevel(Level.BODY);

        if (BuildConfig.DEBUG) {
            if (!okHttpClient.interceptors().contains(logging)) {
                okHttpClient.interceptors().add(logging);
            }
        }

        okHttpClient.connectTimeout(20, TimeUnit.SECONDS);
        //apolloClient.build().query()
        return apolloClient.okHttpClient(okHttpClient.build())
            .serverUrl(LOCAL_SERVER)
            .build();
    }

}
