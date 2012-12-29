/*
 *     This file is part of rs-instance-jumpstart.
 *
 *  rs-instance-jumpstart is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  rs-instance-jumpstart is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with rs-instance-jumpstart.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2012, Daniel Emmons
 */
package rsInstanceJumpstart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;

/*
 * @author Daniel Emmons
 * 
 */
public class rackspaceAccount {

    private String username;
    private String APIKey;
    private boolean authenticated;
    private String usAuthEndpoint;
    //private String ukAuthEndpoint; //some day?
    private String currentAuthToken;
    private String serviceCatalog;
    private JSONObject serviceCatalogJson;
    private String[][] serverList;
    private String CloudServersOpenStackDfw;
    private String CloudServersOpenStackOrd;
    private int responseCode = 0;

    public String getServiceCatalog() {
        return serviceCatalog;
    }

    public String[][] getServerList() {
        return serverList;
    }
    
    public String getAuthToken(){
        return currentAuthToken;
    }

    public String[][] listServers() {
        String[][] thisServerList = new String[100][5];
        //String thisServerList;
        HttpURLConnection connection;
        String output = "";
        int thisServerListCounter = 0;
        String thisServerLink;
        try {

            for (int i = 0; i < serviceCatalogJson.getJSONObject("access").getJSONArray("serviceCatalog").length(); i++) {
                if ("cloudServersOpenStack".equals(serviceCatalogJson.getJSONObject("access").getJSONArray("serviceCatalog").getJSONObject(i).get("name").toString())) {
                    for (int ii = 0; ii < serviceCatalogJson.getJSONObject("access").getJSONArray("serviceCatalog").getJSONObject(i).getJSONArray("endpoints").length(); ii++) {
                        String thisEndpoint = serviceCatalogJson.getJSONObject("access").getJSONArray("serviceCatalog").getJSONObject(i).getJSONArray("endpoints").getJSONObject(ii).get("publicURL").toString();
                        if (thisEndpoint.length() > 0) {
                            try {
                                output = "";
                                System.out.println("current endpoint: " + thisEndpoint);
                                connection = (HttpURLConnection) new URL(thisEndpoint + "/servers").openConnection();

                                connection.setRequestProperty("Accept", "application/json");
                                connection.setRequestProperty("X-Auth-Token", currentAuthToken);

                                InputStream response;

                                response = connection.getInputStream();

                                //System.out.println(String.valueOf(connection.getResponseCode()));

                                BufferedReader reader = null;
                                try {
                                    reader = new BufferedReader(new InputStreamReader(response));
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        output += line + "\n";
                                    }
                                } finally {
                                    if (reader != null) {
                                        try {
                                            reader.close();
                                        } catch (IOException e) {
                                            System.out.println(e);
                                        }
                                    }
                                }
                            } catch (IOException e) {
                                System.out.println(e);
                            }
                            JSONObject thisResponseJson = new JSONObject(output);
                            if (thisResponseJson.getJSONArray("servers").length() > 0) {
                                for (int iii = 0; iii < thisResponseJson.getJSONArray("servers").length(); iii++) {
                                    thisServerLink = thisResponseJson.getJSONArray("servers").getJSONObject(iii).getJSONArray("links").getJSONObject(0).get("href").toString();
                                    //query for details of each server

                                    try {
                                        output = "";
                                        connection = (HttpURLConnection) new URL(thisServerLink).openConnection();

                                        connection.setRequestProperty("Accept", "application/json");
                                        connection.setRequestProperty("X-Auth-Token", currentAuthToken);

                                        InputStream response;

                                        response = connection.getInputStream();

                                        //System.out.println(String.valueOf(connection.getResponseCode()));

                                        BufferedReader reader = null;
                                        try {
                                            reader = new BufferedReader(new InputStreamReader(response));
                                            String line;
                                            while ((line = reader.readLine()) != null) {
                                                output += line + "\n";
                                            }
                                        } finally {
                                            if (reader != null) {
                                                try {
                                                    reader.close();
                                                } catch (IOException e) {
                                                    System.out.println(e);
                                                }
                                            }
                                        }
                                    } catch (IOException e) {
                                        System.out.println(e);
                                    }
                                    //System.out.println(output);
                                    JSONObject thisServer = new JSONObject(output);
                                    System.out.println(thisServer.getJSONObject("server").get("name").toString());
                                    if ("SHUTOFF".equals(thisServerList[thisServerListCounter][2] = thisServer.getJSONObject("server").getString("status"))) {
                                        thisServerList[thisServerListCounter][0] = thisServer.getJSONObject("server").getString("name");
                                        thisServerList[thisServerListCounter][1] = thisServer.getJSONObject("server").getString("id");
                                        thisServerList[thisServerListCounter][2] = thisServer.getJSONObject("server").getString("status");
                                        thisServerList[thisServerListCounter][3] = thisServerLink;
                                        thisServerList[thisServerListCounter][4] = thisServer.getJSONObject("server").getString("accessIPv4");
                                        System.out.println(thisServerList[thisServerListCounter][2]);
                                        thisServerListCounter++;
                                    }
                                }
                            }
                        }

                    }

                }

            }

        } catch (JSONException e) {
            //let's assume for now we won't have any
        }

        //thisServerList = output;
        return thisServerList;
    }

    public void initiateConnection(String uname, String apikey) {
        //gets the variables set in ConnectDialog, authenticates, lists servers in all regions
        username = uname;
        APIKey = apikey;
        serviceCatalog = RackspaceAuth(uname, apikey);
        if (serviceCatalog.length() > 1) {
            try {
                serviceCatalogJson = new JSONObject(serviceCatalog);
                currentAuthToken = serviceCatalogJson.getJSONObject("access").getJSONObject("token").get("id").toString();
            } catch (JSONException e) {
                //let's assume for now we won't have any
            }

            serverList = listServers();
        }

    }

    public String RackspaceAuth(String uname, String apikey) {
        String authQueryJsonString;
        String output = "";
        HttpURLConnection connection;
        usAuthEndpoint = "https://identity.api.rackspacecloud.com/v2.0/tokens";

        authQueryJsonString = "{\"auth\":{\"RAX-KSKEY:apiKeyCredentials\":{\"username\":\"" + uname + "\",\"apiKey\":\"" + apikey + "\"}}}";

        try {
            connection = (HttpURLConnection) new URL(usAuthEndpoint).openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            connection.connect();
            OutputStream outputStream = connection.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.write(authQueryJsonString);
            outputStreamWriter.flush();

            InputStream response;
            responseCode = connection.getResponseCode();
            System.out.println("HTTP Response Code:" + responseCode);
            
            response = connection.getInputStream();
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(response));
                String line;
                while ((line = reader.readLine()) != null) {
                    output += line + "\n";
                }
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e);
            
        }
        System.out.println(output);
        return output;
    }
    
    public int getResponseCode(){
        return responseCode;
    }

    public void powerOnServer(String url) {
        String output = "";
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) new URL(url + "/action").openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("X-Auth-Token", currentAuthToken);
            connection.setDoOutput(true);

            connection.connect();
            OutputStream outputStream = connection.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.write("{\"reboot\":{\"type\":\"HARD\"}}");
            outputStreamWriter.flush();

            InputStream response;

            response = connection.getInputStream();

            System.out.println(String.valueOf(connection.getResponseCode()));

            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(response));
                String line;
                while ((line = reader.readLine()) != null) {
                    output += line + "\n";
                }
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println(output);
        
    }
    
    
}
