package com.theoretics.mobilepos.util;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import static com.theoretics.mobilepos.util.DBHelper.CARD_COLUMN_CARDCODE;
import static com.theoretics.mobilepos.util.DBHelper.CARD_COLUMN_LANE;
import static com.theoretics.mobilepos.util.DBHelper.CARD_COLUMN_PC;
import static com.theoretics.mobilepos.util.DBHelper.CARD_COLUMN_PLATE;
import static com.theoretics.mobilepos.util.DBHelper.CARD_COLUMN_TIMEIN;
import static com.theoretics.mobilepos.util.DBHelper.CARD_COLUMN_VEHICLE;
import static com.theoretics.mobilepos.util.DBHelper.EXIT_TABLE_NAME;
import static com.theoretics.mobilepos.util.DBHelper.VIP_COLUMN_CARDCODE;
import static com.theoretics.mobilepos.util.DBHelper.VIP_COLUMN_CARDID;
import static com.theoretics.mobilepos.util.DBHelper.VIP_COLUMN_DATECREATED;
import static com.theoretics.mobilepos.util.DBHelper.VIP_COLUMN_DATEMODIFIED;
import static com.theoretics.mobilepos.util.DBHelper.VIP_COLUMN_MAXUSE;
import static com.theoretics.mobilepos.util.DBHelper.VIP_COLUMN_NAME;
import static com.theoretics.mobilepos.util.DBHelper.VIP_COLUMN_PARKERTYPE;
import static com.theoretics.mobilepos.util.DBHelper.VIP_COLUMN_PLATENUMBER;
import static com.theoretics.mobilepos.util.DBHelper.VIP_COLUMN_STATUS;
import static com.theoretics.mobilepos.util.DBHelper.XREAD_TABLE_NAME;

public class HttpHandler {

    private static final String TAG = HttpHandler.class.getSimpleName();
    public String retStr;
    private DBHelper dbh;
    String pattern = "yyyy-MM-dd hh:mm:ss";
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    public HttpHandler(DBHelper db) {
        dbh = db;
    }

    public String updateData2Server(final String urlWebService, final String sql, final String ldm) {
        //final String[] retStr = new String[1];
        /*
         * As fetching the json string is a network operation
         * And we cannot perform a network operation in main thread
         * so we need an AsyncTask
         * The constrains defined here are
         * Void -> We are not passing anything
         * Void -> Nothing at progress update as well
         * String -> After completion it should return a string and it will be the json string
         * */
        class SendSQL extends AsyncTask<Void, Void, String> {

            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some timecm n'\
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                retStr = s;

            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {

                try {
                    System.out.println("ANGELO : UPDATING TO SERVER " + urlWebService + sql);
                    //creating a URL
                    String u = urlWebService + sql;
                    URL url = new URL(u);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json = "";
                    int i = 0;
                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {
                        i++;
                        //appending it to string builder
                        sb.append(json + "\n");
                        //System.out.println("ANGELO : [" + i + "]" + json + "\n");
                        //insertNewVIP2DB(dbh, json);
                    }
                    System.out.println("ANGELO : [" + i + "]" + sb.toString());
                    //finally returning the read string

                    //retJSON[0] = readJSON(json);
                    //Date now = new Date();
                    //String n = sdf.format(now);
                    //System.out.println(n);
                    //dbh.updateLDC(sql);

                    return "true";
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        SendSQL sendSQL = new SendSQL();
        sendSQL.execute();

        return retStr;
    }


    public String saveRawText2Server(final String urlWebService, final String rawText) {
        //final String[] retStr = new String[1];
        /*
         * As fetching the json string is a network operation
         * And we cannot perform a network operation in main thread
         * so we need an AsyncTask
         * The constrains defined here are
         * Void -> We are not passing anything
         * Void -> Nothing at progress update as well
         * String -> After completion it should return a string and it will be the json string
         * */
        class SendSQL extends AsyncTask<Void, Void, String> {

            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some timecm n'\
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                retStr = s;

            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {

                try {
                    System.out.println("ANGELO : UPDATING TO SERVER " + urlWebService + rawText);
                    //creating a URL
                    String u = urlWebService + rawText;
                    URL url = new URL(u);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json = "";
                    int i = 0;
                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {
                        i++;
                        //appending it to string builder
                        sb.append(json + "\n");
                        //System.out.println("ANGELO : [" + i + "]" + json + "\n");
                        //insertNewVIP2DB(dbh, json);
                    }
                    System.out.println("ANGELO : [" + i + "]" + sb.toString());
                    //finally returning the read string

                    //retJSON[0] = readJSON(json);
                    //Date now = new Date();
                    //String n = sdf.format(now);
                    //System.out.println(n);
                    //dbh.updateLDC(sql);

                    return "true";
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        SendSQL sendSQL = new SendSQL();
        sendSQL.execute();

        return retStr;
    }

    public String sendGRAND2Server(final String urlWebService, final String rawText) {
        //final String[] retStr = new String[1];
        /*
         * As fetching the json string is a network operation
         * And we cannot perform a network operation in main thread
         * so we need an AsyncTask
         * The constrains defined here are
         * Void -> We are not passing anything
         * Void -> Nothing at progress update as well
         * String -> After completion it should return a string and it will be the json string
         * */
        class SendSQL extends AsyncTask<Void, Void, String> {

            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some timecm n'\
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                retStr = s;

            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {

                try {
                    System.out.println("ANGELO : UPDATING TO SERVER " + urlWebService + rawText);
                    //creating a URL
                    String u = urlWebService + rawText;
                    URL url = new URL(u);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json = "";
                    int i = 0;
                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {
                        i++;
                        //appending it to string builder
                        sb.append(json + "\n");
                        //System.out.println("ANGELO : [" + i + "]" + json + "\n");
                        //insertNewVIP2DB(dbh, json);
                    }
                    System.out.println("ANGELO : [" + i + "]" + sb.toString());
                    //finally returning the read string

                    //retJSON[0] = readJSON(json);
                    //Date now = new Date();
                    //String n = sdf.format(now);
                    //System.out.println(n);
                    //dbh.updateLDC(sql);

                    return "true";
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        SendSQL sendSQL = new SendSQL();
        sendSQL.execute();

        return retStr;
    }

    public String makeAmbulatory2Server(final String urlWebService, final String sql) {
        //final String[] retStr = new String[1];
        /*
         * As fetching the json string is a network operation
         * And we cannot perform a network operation in main thread
         * so we need an AsyncTask
         * The constrains defined here are
         * Void -> We are not passing anything
         * Void -> Nothing at progress update as well
         * String -> After completion it should return a string and it will be the json string
         * */
        class SendSQL extends AsyncTask<Void, Void, String> {

            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some timecm n'\
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                retStr = s;

            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {

                try {
                    System.out.println("ANGELO : UPDATING TO SERVER " + urlWebService + sql);
                    //creating a URL
                    String u = urlWebService + sql;
                    URL url = new URL(u);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json = "";
                    int i = 0;
                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {
                        i++;
                        //appending it to string builder
                        sb.append(json + "\n");
                        //System.out.println("ANGELO : [" + i + "]" + json + "\n");
                        //insertNewVIP2DB(dbh, json);
                    }
                    System.out.println("ANGELO : [" + i + "]" + sb.toString());
                    //finally returning the read string

                    //retJSON[0] = readJSON(json);
                    //Date now = new Date();
                    //String n = sdf.format(now);
                    //System.out.println(n);
                    //dbh.updateLDC(sql);

                    return "true";
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        SendSQL sendSQL = new SendSQL();
        sendSQL.execute();

        return retStr;
    }

    public void getNewVIPFromServer(final String urlWebService, final String ldc) {
        //final String[] retStr = new String[1];
        /*
         * As fetching the json string is a network operation
         * And we cannot perform a network operation in main thread
         * so we need an AsyncTask
         * The constrains defined here are
         * Void -> We are not passing anything
         * Void -> Nothing at progress update as well
         * String -> After completion it should return a string and it will be the json string
         * */
        class GetJSON extends AsyncTask<Void, Void, String> {

            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some timecm n'\
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                retStr = s;

            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {

                try {
                    System.out.println("ANGELO : RUNNING NEW VIPS " + urlWebService + ldc);
                    //creating a URL
                    String u = urlWebService + ldc;
                    URL url = new URL(u);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json = "";
                    int i = 0;
                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {
                        i++;
                        //appending it to string builder
                        sb.append(json + "\n");
                        System.out.println("ANGELO : [" + i + "]" + json + "\n");
                        insertNewVIP2DB(dbh, json);
                    }
                    System.out.println("ANGELO : [" + i + "]" + sb.toString());
                    //finally returning the read string

                    //retJSON[0] = readJSON(json);
                    //Date now = new Date();
                    //String n = sdf.format(now);
                    //System.out.println(n);
                    //dbh.updateLDC(ldc);

                    return "true";
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        GetJSON getJSON = new GetJSON();
        getJSON.execute();

    }

    //this method is actually fetching the json string
    public void getModifiedVIPFromServer(final String urlWebService, final String ldm) {
        //final String[] retStr = new String[1];
        /*
         * As fetching the json string is a network operation
         * And we cannot perform a network operation in main thread
         * so we need an AsyncTask
         * The constrains defined here are
         * Void -> We are not passing anything
         * Void -> Nothing at progress update as well
         * String -> After completion it should return a string and it will be the json string
         * */
        class GetJSON extends AsyncTask<Void, Void, String> {

            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some time
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                retStr = s;

            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {

                try {
                    System.out.println("ANGELO : " + urlWebService + ldm);
                    //creating a URL
                    String u = urlWebService + ldm;
                    URL url = new URL(u);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json;
                    int i = 0;
                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {
                        i++;
                        //appending it to string builder
                        sb.append(json + "\n");
                        System.out.println("ANGELO : [" + i + "]" + json + "\n");
                        updateModifiedVIP2DB(dbh, json);
                        //dbh.updateLDM(ldm);

                    }
                    System.out.println("ANGELO : [" + i + "]" + sb.toString());
                    //finally returning the read string
                    //Date now = new Date();
                    //String n = sdf.format(now);
                    //System.out.println(n);
                    //dbh.updateLDC(ldm);
                    //retJSON[0] = readJSON(json);

                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        GetJSON getJSON = new GetJSON();
        getJSON.execute();

    }


     public String makeServiceCall(String reqUrl) {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    private void insertNewVIP2DB(DBHelper dbh, String msg) {
        //String url = "http://api.androidhive.info/contacts/";
        //url = "http://192.168.1.116/timecheck.php";
        //HttpHandler sh = new HttpHandler();

        // Making a request to url and getting response
        //String jsonStr = sh.makeServiceCall(url);

        try {
            JSONObject c = new JSONObject(msg);

            // tmp hash map for single contact
            HashMap<String, String> contact = new HashMap<>();

            // adding each child node to HashMap key => value
            contact.put(VIP_COLUMN_CARDID, c.getString(VIP_COLUMN_CARDID));
            contact.put(VIP_COLUMN_CARDCODE, c.getString(VIP_COLUMN_CARDCODE));
            contact.put(VIP_COLUMN_PARKERTYPE, c.getString(VIP_COLUMN_PARKERTYPE));
            contact.put(VIP_COLUMN_PLATENUMBER, c.getString(VIP_COLUMN_PLATENUMBER));
            contact.put(VIP_COLUMN_NAME, c.getString(VIP_COLUMN_NAME));
            contact.put(VIP_COLUMN_MAXUSE, c.getString(VIP_COLUMN_MAXUSE));
            contact.put(VIP_COLUMN_STATUS, c.getString(VIP_COLUMN_STATUS));
            contact.put(VIP_COLUMN_DATECREATED, c.getString(VIP_COLUMN_DATECREATED));
            contact.put(VIP_COLUMN_DATEMODIFIED, c.getString(VIP_COLUMN_DATEMODIFIED));

            // adding contact to contact
            //sendmessage(c.getString("id") + " : " + c.getString("name") + " : " + c.getString("email") + " : " + c.getString("mobile"));
            System.out.println("ANGELO: "+c.getString(VIP_COLUMN_CARDID) + " : " + c.getString(VIP_COLUMN_CARDCODE) + " : " + c.getString(VIP_COLUMN_PARKERTYPE) + " : " + c.getString(VIP_COLUMN_PLATENUMBER));

            //String cardID, String parkerType, String plateNumber, String name, String cardNumber, int maxUse, int status, String ldc, String ldm)
            boolean inserted = dbh.insertContact(c.getString(VIP_COLUMN_CARDID), c.getString(VIP_COLUMN_PARKERTYPE), c.getString(VIP_COLUMN_PLATENUMBER),
                    c.getString(VIP_COLUMN_NAME), c.getString(VIP_COLUMN_CARDCODE), c.getString(VIP_COLUMN_MAXUSE),
                    c.getString(VIP_COLUMN_STATUS), c.getString(VIP_COLUMN_DATECREATED),c.getString(VIP_COLUMN_DATEMODIFIED));
            if (inserted) dbh.updateLDC("vips", c.getString(VIP_COLUMN_DATECREATED));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void insertNewExitCard(DBHelper dbh, String msg) {
        //String url = "http://api.androidhive.info/contacts/";
        //url = "http://192.168.1.116/timecheck.php";
        //HttpHandler sh = new HttpHandler();

        // Making a request to url and getting response
        //String jsonStr = sh.makeServiceCall(url);

        try {
            JSONObject c = new JSONObject(msg);

            // tmp hash map for single contact
            HashMap<String, String> contact = new HashMap<>();

            // adding each child node to HashMap key => value
            contact.put(CARD_COLUMN_CARDCODE, c.getString(CARD_COLUMN_CARDCODE));
            contact.put(CARD_COLUMN_PLATE, c.getString(CARD_COLUMN_PLATE));
            contact.put(CARD_COLUMN_VEHICLE, c.getString(CARD_COLUMN_VEHICLE));
            contact.put(CARD_COLUMN_LANE, c.getString(CARD_COLUMN_LANE));
            contact.put(CARD_COLUMN_PC, c.getString(CARD_COLUMN_PC));
            contact.put(CARD_COLUMN_TIMEIN, c.getString(CARD_COLUMN_TIMEIN));

            // adding contact to contact
            //sendmessage(c.getString("id") + " : " + c.getString("name") + " : " + c.getString("email") + " : " + c.getString("mobile"));
            System.out.println("ANGELO: "+c.getString(VIP_COLUMN_CARDID) + " : " + c.getString(VIP_COLUMN_CARDCODE) + " : " + c.getString(VIP_COLUMN_PARKERTYPE) + " : " + c.getString(VIP_COLUMN_PLATENUMBER));

            //String cardID, String parkerType, String plateNumber, String name, String cardNumber, int maxUse, int status, String ldc, String ldm)
            boolean inserted = dbh.insertContact(c.getString(VIP_COLUMN_CARDID), c.getString(VIP_COLUMN_PARKERTYPE), c.getString(VIP_COLUMN_PLATENUMBER),
                    c.getString(VIP_COLUMN_NAME), c.getString(VIP_COLUMN_CARDCODE), c.getString(VIP_COLUMN_MAXUSE),
                    c.getString(VIP_COLUMN_STATUS), c.getString(VIP_COLUMN_DATECREATED),c.getString(VIP_COLUMN_DATEMODIFIED));
            if (inserted) dbh.updateLDC("vips", c.getString(VIP_COLUMN_DATECREATED));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateModifiedVIP2DB(DBHelper dbh, String msg) {
        //String url = "http://api.androidhive.info/contacts/";
        //url = "http://192.168.1.116/timecheck.php";
        //HttpHandler sh = new HttpHandler();

        // Making a request to url and getting response
        //String jsonStr = sh.makeServiceCall(url);

        try {
            JSONObject c = new JSONObject(msg);

            // tmp hash map for single contact
            HashMap<String, String> contact = new HashMap<>();

            // adding each child node to HashMap key => value
            contact.put(VIP_COLUMN_CARDID, c.getString(VIP_COLUMN_CARDID));
            contact.put(VIP_COLUMN_CARDCODE, c.getString(VIP_COLUMN_CARDCODE));
            contact.put(VIP_COLUMN_PARKERTYPE, c.getString(VIP_COLUMN_PARKERTYPE));
            contact.put(VIP_COLUMN_PLATENUMBER, c.getString(VIP_COLUMN_PLATENUMBER));
            contact.put(VIP_COLUMN_NAME, c.getString(VIP_COLUMN_NAME));
            contact.put(VIP_COLUMN_MAXUSE, c.getString(VIP_COLUMN_MAXUSE));
            contact.put(VIP_COLUMN_STATUS, c.getString(VIP_COLUMN_STATUS));
            contact.put(VIP_COLUMN_DATECREATED, c.getString(VIP_COLUMN_DATECREATED));
            contact.put(VIP_COLUMN_DATEMODIFIED, c.getString(VIP_COLUMN_DATEMODIFIED));

            // adding contact to contact
            //sendmessage(c.getString("id") + " : " + c.getString("name") + " : " + c.getString("email") + " : " + c.getString("mobile"));
            System.out.println("ANGELO: "+c.getString(VIP_COLUMN_CARDID) + " : " + c.getString(VIP_COLUMN_CARDCODE) + " : " + c.getString(VIP_COLUMN_PARKERTYPE) + " : " + c.getString(VIP_COLUMN_PLATENUMBER));

            //String cardID, String parkerType, String plateNumber, String name, String cardNumber, int maxUse, int status, String ldc, String ldm)
            boolean inserted = dbh.updateContact(c.getString(VIP_COLUMN_CARDID), c.getString(VIP_COLUMN_PARKERTYPE), c.getString(VIP_COLUMN_PLATENUMBER),
                    c.getString(VIP_COLUMN_NAME), c.getString(VIP_COLUMN_CARDCODE), c.getString(VIP_COLUMN_MAXUSE),
                    c.getString(VIP_COLUMN_STATUS), c.getString(VIP_COLUMN_DATECREATED),c.getString(VIP_COLUMN_DATEMODIFIED));
            if (inserted) dbh.updateLDM("vips", c.getString(VIP_COLUMN_DATEMODIFIED));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}