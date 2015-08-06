package com.example.jereczem.ltt.networking;

import com.example.jereczem.ltt.networking.tasks.HttpGetAsyncTask;
import com.example.jereczem.ltt.networking.tasks.HttpPostAsyncTask;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by jereczem on 01.08.15.
 */
public class HttpResponseReceiver {
    private String url;
    private ArrayList<Parameter> parameters;

    public HttpResponseReceiver(String urlRoute){
        url = "" + urlRoute;
        parameters = new ArrayList<>();
    }

    public void addParameter(String key, String value){
        parameters.add(new Parameter(key, value));
    }

    public HttpResponse receive(){
        if(parameters.isEmpty()){
            try {
                return new HttpGetAsyncTask().execute(url).get();
            } catch (Exception e) {
                return new HttpResponse(500, e.toString());
            }
        }else{
            StringBuilder urlParameters = new StringBuilder();
            for(Parameter parameter : parameters){
                urlParameters.append(parameter.key)
                        .append("=")
                        .append(parameter.value);
                if(parameters.indexOf(parameter) != parameters.size() - 1)
                    urlParameters.append("&");
            }
            try {
                return new HttpPostAsyncTask().execute(url, urlParameters.toString()).get();
            } catch (Exception e) {
                return new HttpResponse(500, e.toString());
            }
        }
    }

    private class Parameter{
        public String key;
        public String value;
        public Parameter(String key, String value){
            this.key = key;
            this.value = value;
        }
    }
}
