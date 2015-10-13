/*
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.identitytoolkit.demo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpHeaders;
import com.google.common.base.Strings;
import com.google.identitytoolkit.GitkitClient;
import com.google.identitytoolkit.GitkitUser;
import com.google.identitytoolkit.IdToken;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

// ENDPOINTS MODELS
import com.appspot.apiv3_1076.products.Products;
import com.appspot.apiv3_1076.products.Products.SayHello;
// THe models
import com.appspot.apiv3_1076.products.model.HelloHello;

/**
 * Gitkit Demo.
 */
public class GitkitDemo extends AppCompatActivity implements OnClickListener {

    private GitkitClient client;
    // The Adapter crap
    private GreetingsDataAdapter mListAdapter;
    private static final String LOG_TAG = "MainActivity";
    // For the endpoints auth
    private String mEmailAccount = "";
    private String oAuthToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Step 1: Create a GitkitClient.
        // The configurations are set in the AndroidManifest.xml. You can also set or overwrite them
        // by calling the corresponding setters on the GitkitClient builder.
        //
        client = GitkitClient.newBuilder(this, new GitkitClient.SignInCallbacks() {
            // Implement the onSignIn method of GitkitClient.SignInCallbacks interface.
            // This method is called when the sign-in process succeeds. A Gitkit IdToken and the signed
            // in account information are passed to the callback.
            @Override
            public void onSignIn(IdToken idToken, GitkitUser user) {
                showProfilePage(idToken, user);
                Toast.makeText(GitkitDemo.this, "HERROW!", Toast.LENGTH_LONG).show();
                mEmailAccount = user.getEmail();
                oAuthToken = idToken.getTokenString();
                // Now use the idToken to create a session for your user.
                // To do so, you should exchange the idToken for either a Session Token or Cookie
                // from your server.
                // Finally, save the Session Token or Cookie to maintain your user's session.
            }

            // Implement the onSignInFailed method of GitkitClient.SignInCallbacks interface.
            // This method is called when the sign-in process fails.
            @Override
            public void onSignInFailed() {
                Toast.makeText(GitkitDemo.this, "Sign in failed", Toast.LENGTH_LONG).show();
            }
        }).build();

        showSignInPage();
    }


    // Step 3: Override the onActivityResult method.
    // When a result is returned to this activity, it is maybe intended for GitkitClient. Call
    // GitkitClient.handleActivityResult to check the result. If the result is for GitkitClient,
    // the method returns true to indicate the result has been consumed.
    //

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (!client.handleActivityResult(requestCode, resultCode, intent)) {
            super.onActivityResult(requestCode, resultCode, intent);
        }

    }

    // Step 4: Override the onNewIntent method.
    // When the app is invoked with an intent, it is possible that the intent is for GitkitClient.
    // Call GitkitClient.handleIntent to check it. If the intent is for GitkitClient, the method
    // returns true to indicate the intent has been consumed.

    @Override
    protected void onNewIntent(Intent intent) {
        if (!client.handleIntent(intent)) {
            super.onNewIntent(intent);
        }
    }

    private void showSignInPage() {
        setContentView(R.layout.welcome);
        Button button = (Button) findViewById(R.id.sign_in);
        button.setOnClickListener(this);
    }

    private void showProfilePage(IdToken idToken, GitkitUser user) {
        setContentView(R.layout.profile);
        showAccount(user);
        findViewById(R.id.sign_out).setOnClickListener(this);
        // INITIALIZE THE VIEW! BRO!
        ListView listView = (ListView) findViewById(R.id.greetings_list_view);
        mListAdapter = new GreetingsDataAdapter((Application) getApplication());
        listView.setAdapter(mListAdapter);
    }
    // Step 5: Respond to user actions.
    // If the user clicks sign in, call GitkitClient.startSignIn() to trigger the sign in flow.

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.sign_in) {
            client.startSignIn();
        } else if (v.getId() == R.id.sign_out) {
            showSignInPage();
        }
    }


    private void showAccount(GitkitUser user) {
        ((TextView) findViewById(R.id.account_email)).setText(user.getEmail());

        if (user.getDisplayName() != null) {
            ((TextView) findViewById(R.id.account_name)).setText(user.getDisplayName());
        }

        if (user.getPhotoUrl() != null) {
            final ImageView pictureView = (ImageView) findViewById(R.id.account_picture);
            new AsyncTask<String, Void, Bitmap>() {

                @Override
                protected Bitmap doInBackground(String... arg) {
                    try {
                        byte[] result = HttpUtils.get(arg[0]);
                        return BitmapFactory.decodeByteArray(result, 0, result.length);
                    } catch (IOException e) {
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    if (bitmap != null) {
                        pictureView.setImageBitmap(bitmap);
                    }
                }
            }.execute(user.getPhotoUrl());
        }
    }

    // Display Greeting
    private void displayGreetings(HelloHello... greetings) {
        String msg;
        if (greetings==null || greetings.length < 1) {
            msg = "Greeting was not present";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        } else {
            Log.d(LOG_TAG, "Displaying " + greetings.length + " greetings.");
            List<HelloHello> greetingsList = Arrays.asList(greetings);
            mListAdapter.replaceData(greetings);
        }
    }
    // ADAPTER FOR THE GREETINGS
    static class GreetingsDataAdapter extends ArrayAdapter {
        GreetingsDataAdapter(Application application) {
            super(application.getApplicationContext(), android.R.layout.simple_list_item_1,
                    application.greetings);
        }

        void replaceData(HelloHello[] greetings) {
            clear();
            for (HelloHello greeting : greetings) {
                add(greeting);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);

            HelloHello greeting = (HelloHello)this.getItem(position);

            StringBuilder sb = new StringBuilder();

            Set<String> fields = greeting.keySet();
            boolean firstLoop = true;
            for (String fieldName : fields) {
                // Append next line chars to 2.. loop runs.
                if (firstLoop) {
                    firstLoop = false;
                } else {
                    sb.append("\n");
                }

                sb.append(fieldName)
                        .append(": ")
                        .append(greeting.get(fieldName));
            }

            view.setText(sb.toString());
            return view;
        }
    }

    private class GetHelloTask extends AsyncTask<Void, Void, HelloHello> {
        @Override
        protected HelloHello doInBackground(Void... unused) {
            Products apiServiceHandle = AppConstants.getApiServiceHandle();

            try {
                SayHello getGreetingCommand = apiServiceHandle.sayHello();
                // We add the token to the request and send it off
                getGreetingCommand.setRequestHeaders(new HttpHeaders().setCookie("gtoken=" + oAuthToken));
                HelloHello greeting = getGreetingCommand.execute();
                return greeting;
            } catch (IOException e) {
                Log.e(LOG_TAG, "Exception during API call", e);
            }
            return null;
        }
        @Override
        protected void onPostExecute(HelloHello greeting) {
            if (greeting!=null) {
                displayGreetings(greeting);
            } else {
                Log.e(LOG_TAG, "No greetings were returned by the API.");
            }
        }
    }

    /**
     * Implements the "Get Greeting" button. See activity_main.xml for the dynamic reference
     * to this method.
     */
    public void onClickGetGreeting(View view) {
        // Interface code to get the value
        View rootView = view.getRootView();
        // Get the hello and shit.
        new GetHelloTask().execute();
    }

    private boolean isSignedIn() {
        return !Strings.isNullOrEmpty(oAuthToken);
    }
}
