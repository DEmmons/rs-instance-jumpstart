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
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Daniel Emmons
 */
public class CloudServerPanelInstance {

    private String serverStatus;
    private String serverUuid;
    private String serverName;
    private String serverLink;
    private String serverIP4;
    private String authToken;
    public javax.swing.JPanel jPanelCloudServer;
    public javax.swing.JLabel jLabelCloudServerName;
    public javax.swing.JLabel jLabelCloudServerStatus;
    public javax.swing.JLabel jLabelCloudServerUuid;
    public javax.swing.JButton jButtonCloudServerPowerOn;

    public void setValues(String thisServerName, String thisServerId, String thisServerStatus, String thisServerLink, String thisServerIP4, String thisAuthToken) {
        serverStatus = thisServerStatus;
        serverUuid = thisServerId;
        serverName = thisServerName;
        serverLink = thisServerLink;
        serverIP4 = thisServerIP4;
        authToken = thisAuthToken;


        jLabelCloudServerName.setText("Name: " + serverName + "      IPv4 Address: " + serverIP4);
        jLabelCloudServerUuid.setText("UUID: " + serverUuid);
        jLabelCloudServerStatus.setText("Status: " + serverStatus);
        jButtonCloudServerPowerOn.setActionCommand(serverLink);

    }

    public void jButtonServerPowerOnActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        //power on the system, then disable button
        powerOnServer(evt.getActionCommand());
        ((javax.swing.JButton) evt.getSource()).setEnabled(false);
    }

    public void powerOnServer(String url) {
        String output = "";
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) new URL(url + "/action").openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("X-Auth-Token", authToken);
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
        jPanelCloudServer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 51)));
        jLabelCloudServerStatus.setText("Checking...");

        int checkCounter = 0;
        boolean successfulPowerOn = false;
     
        
        while (successfulPowerOn == false) {
            try {
                //thread to sleep for the specified number of milliseconds
                Thread.sleep(1000);
            } catch (java.lang.InterruptedException ie) {
                System.out.println(ie);
            }
            if (checkSuccessfulPowerOn() == true) {
                //true
                successfulPowerOn = true;
                jPanelCloudServer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 255, 51)));
                jLabelCloudServerStatus.setText("Active");
                
            } else if (checkCounter >= 30) {
                //timeout (30 secounds)
                jPanelCloudServer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 175, 0)));
                jLabelCloudServerStatus.setText("Timed out");
                successfulPowerOn = true;
            } else {
                //false
                checkCounter++;
            }


        }

    }

    private boolean checkSuccessfulPowerOn() {
        String output = "";
        HttpURLConnection connection;
        String successCheckStatus = "";
        try {
            try {
                output = "";
                connection = (HttpURLConnection) new URL(serverLink).openConnection();

                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("X-Auth-Token", authToken);

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
            JSONObject thisServer = new JSONObject(output);
            System.out.println(thisServer.getJSONObject("server").get("status").toString());
            successCheckStatus = thisServer.getJSONObject("server").get("status").toString();
            
        } catch (JSONException e) {
            //let's assume for now we won't have any
        }
        //System.out.println(output);
        if ("ACTIVE".equals(successCheckStatus)){
            return true;
        } else {
            return false;
        }
        
    }

    public void initialize() {
        //create objects

        jPanelCloudServer = new javax.swing.JPanel();
        jLabelCloudServerName = new javax.swing.JLabel();
        jLabelCloudServerUuid = new javax.swing.JLabel();
        jLabelCloudServerStatus = new javax.swing.JLabel();
        jButtonCloudServerPowerOn = new javax.swing.JButton();

        //initialize layout values
        jPanelCloudServer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 51)));

        jLabelCloudServerName.setText("Server Name");

        jLabelCloudServerUuid.setText("UUID");

        jLabelCloudServerStatus.setText("Status");

        jButtonCloudServerPowerOn.setText("Power On");
        jButtonCloudServerPowerOn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonServerPowerOnActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanelCloudServerLayout = new org.jdesktop.layout.GroupLayout(jPanelCloudServer);
        jPanelCloudServer.setLayout(jPanelCloudServerLayout);
        jPanelCloudServerLayout.setHorizontalGroup(
                jPanelCloudServerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanelCloudServerLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanelCloudServerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                .add(jPanelCloudServerLayout.createSequentialGroup()
                .add(jLabelCloudServerUuid, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButtonCloudServerPowerOn)
                .add(19, 19, 19))
                .add(jPanelCloudServerLayout.createSequentialGroup()
                .add(jLabelCloudServerName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 403, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 81, Short.MAX_VALUE)
                .add(jLabelCloudServerStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(47, 47, 47)))));
        jPanelCloudServerLayout.setVerticalGroup(
                jPanelCloudServerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanelCloudServerLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanelCloudServerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabelCloudServerName)
                .add(jLabelCloudServerStatus))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 10)
                .add(jPanelCloudServerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabelCloudServerUuid)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, jButtonCloudServerPowerOn))
                .addContainerGap()));
    }
}
