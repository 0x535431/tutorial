package com.google.identitytoolkit.demo;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.appspot.apiv3_1076.products.Products;

/**
 * Created by root on 10/11/2015.
 */
public class AppConstants {

    public static final String WEB_CLIENT_ID = "58941837826-55kqi4hk3a1of9cqj7l3s3mrg9puajml.apps.googleusercontent.com";

    public static final String AUDIENCE = "server:client_id:" + WEB_CLIENT_ID;

    /**
     * Class instance of the JSON factory.
     */
    public static final JsonFactory JSON_FACTORY = new AndroidJsonFactory();

    /**
     * Class instance of the HTTP transport.
     */
    public static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();


    /**
     * Retrieves a Helloworld api service handle to access the API.
     */
    public static Products getApiServiceHandle() {
        // Use a builder to help formulate the API request.
        Products.Builder helloWorld = new Products.Builder(AppConstants.HTTP_TRANSPORT,
                AppConstants.JSON_FACTORY, null);

        return helloWorld.build();
    }

}