package com.google.identitytoolkit.demo;

import com.appspot.apiv3_1076.products.model.HelloHello;
import com.google.api.client.util.Lists;
import java.util.ArrayList;
import java.lang.reflect.Array;

/**
 * Created by root on 10/11/2015.
 * This is the mofo that will handel the list!
 */
public class Application extends android.app.Application{
    ArrayList<HelloHello> greetings = Lists.newArrayList();
}
