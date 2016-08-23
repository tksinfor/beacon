package com.ik9.beacon.utilities;

import com.ik9.beacon.repository.ProfileEntity;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class HttpConnection {

    public static String bringCampaigns(String deviceID) {
        StringBuffer response = null;
        String data;

        try {
            URL url = new URL(Constants.URL_WEBSERVICE);

            data = "method=" + URLEncoder.encode("bringCampaigns", "UTF-8") +
                    "&deviceID=" + URLEncoder.encode(deviceID, "UTF-8");

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            urlConnection.setDoOutput(true);
            urlConnection.setFixedLengthStreamingMode(data.getBytes().length);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            out.flush();
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;

            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response != null ? response.toString() : null;
    }

    public static String bringProfileCustomer(String securityWord) {
        StringBuffer response = null;
        String data;

        try {
            URL url = new URL(Constants.URL_WEBSERVICE);

            data = "method=" + URLEncoder.encode("bringProfileCustomer", "UTF-8") +
                    "&securityWord=" + URLEncoder.encode(securityWord, "UTF-8");

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            urlConnection.setDoOutput(true);
            urlConnection.setFixedLengthStreamingMode(data.getBytes().length);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            out.flush();
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;

            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response != null ? response.toString() : null;
    }

    private static boolean processSend(String data) {
        URL url;
        HttpURLConnection urlConnection;

        try {

            url = new URL(Constants.URL_WEBSERVICE);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            urlConnection.setDoOutput(true);
            urlConnection.setFixedLengthStreamingMode(data.getBytes().length);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            out.write(data.getBytes());
            out.flush();
            out.close();

            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean sendBeaconDetection(String beaconID, String deviceID) {
        try {
//            String data_ = "method=" + URLEncoder.encode("sendBeaconDetection", "UTF-8") +
//                    "&beaconID=" + URLEncoder.encode(beaconID, "UTF-8") +
//                    "&deviceID=" + URLEncoder.encode(deviceID, "UTF-8");

            String data = URLEncoder.encode("sendBeaconDetection", "UTF-8") +
                    "/" + URLEncoder.encode(beaconID, "UTF-8") +
                    "/" + URLEncoder.encode(deviceID, "UTF-8");

            return processSend(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean sendLikedCampaign(String campaignID, String deviceID) {
        try {
//            String data = "method=" + URLEncoder.encode("sendLikedCampaign", "UTF-8") +
//                    "&campaignID=" + URLEncoder.encode(campaignID, "UTF-8") +
//                    "&deviceID=" + URLEncoder.encode(deviceID, "UTF-8");

            String data = URLEncoder.encode("sendLikedCampaign", "UTF-8") +
                    "/" + URLEncoder.encode(campaignID, "UTF-8") +
                    "/" + URLEncoder.encode(deviceID, "UTF-8");

            return processSend(data);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean sendProfileCustomer(ProfileEntity profileEntity) {
        try {
//            String data = "method=" + URLEncoder.encode("profileEntity", "UTF-8") +
//                    "&birth=" + URLEncoder.encode(profileEntity.getBirth(), "UTF-8") +
//                    "&city=" + URLEncoder.encode(profileEntity.getCity(), "UTF-8") +
//                    "&device_id=" + URLEncoder.encode(profileEntity.getDevice_id(), "UTF-8") +
//                    "&email=" + URLEncoder.encode(profileEntity.getEmail(), "UTF-8") +
//                    "&gender=" + URLEncoder.encode(profileEntity.getGender(), "UTF-8") +
//                    "&name=" + URLEncoder.encode(profileEntity.getName(), "UTF-8") +
//                    "&phone=" + URLEncoder.encode(profileEntity.getPhone(), "UTF-8") +
//                    "&state=" + URLEncoder.encode(profileEntity.getState(), "UTF-8") +
//                    "&uri_photo=" + URLEncoder.encode(profileEntity.getUriPhoto(), "UTF-8");

            String data = URLEncoder.encode("profileEntity", "UTF-8") +
                    "/" + URLEncoder.encode(profileEntity.getBirth(), "UTF-8") +
                    "/" + URLEncoder.encode(profileEntity.getCity(), "UTF-8") +
                    "/" + URLEncoder.encode(profileEntity.getDevice_id(), "UTF-8") +
                    "/" + URLEncoder.encode(profileEntity.getEmail(), "UTF-8") +
                    "/" + URLEncoder.encode(profileEntity.getGender(), "UTF-8") +
                    "/" + URLEncoder.encode(profileEntity.getName(), "UTF-8") +
                    "/" + URLEncoder.encode(profileEntity.getPhone(), "UTF-8") +
                    "/" + URLEncoder.encode(profileEntity.getState(), "UTF-8") +
                    "/" + URLEncoder.encode(profileEntity.getUriPhoto(), "UTF-8");

            return processSend(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean sendVizualizedCampaign(String campaignID, String deviceID) {
        try {
//            String data = "method=" + URLEncoder.encode("sendVizualizedCampaign", "UTF-8") +
//                    "&campaignID=" + URLEncoder.encode(campaignID, "UTF-8") +
//                    "&deviceID=" + URLEncoder.encode(deviceID, "UTF-8");

            String data = URLEncoder.encode("sendVizualizedCampaign", "UTF-8") +
                    "/" + URLEncoder.encode(campaignID, "UTF-8") +
                    "/" + URLEncoder.encode(deviceID, "UTF-8");

            return processSend(data);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return false;
    }

}
