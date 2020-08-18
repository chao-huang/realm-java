package io.realm.transformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;



public class UrlEncodedAnalytics {

    private Logger logger = LoggerFactory.getLogger("realm-logger");

    private String prefix;
    private String suffix;

    public UrlEncodedAnalytics(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public void execute(RealmAnalytics analytics) {
        try {
            URL url = getUrl(analytics);

            logger.debug("submitting: " + url);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            logger.debug("submitted: " +  connection.getResponseCode() + " " + url);
            connection.getResponseCode();
        } catch (Exception ignored) {
            logger.debug("ignoring: ", ignored);
            // We ignore this exception on purpose not to break the build system if this class fails
        }
    }

    private URL getUrl(RealmAnalytics analytics) throws
            MalformedURLException,
            SocketException,
            NoSuchAlgorithmException,
            UnsupportedEncodingException {
        return new URL(prefix + Utils.base64Encode(analytics.generateJson()) + suffix);
    }

    public static class MixPanel extends UrlEncodedAnalytics {
        private static final String ADDRESS_PREFIX = "https://api.mixpanel.com/track/?data=";
        private static final String ADDRESS_SUFFIX = "&ip=1";

        public MixPanel() {
            super(ADDRESS_PREFIX, ADDRESS_SUFFIX);
        }
    }

    public static class Segment extends UrlEncodedAnalytics {
        // FIXME
        private static final String ADDRESS_PREFIX = "http://localhost:9091/api/client/v2.0/app/realm-sdk-integration-tests-dluxo/service/metric_webhook/incoming_webhook/metric?data=";
        private static final String ADDRESS_SUFFIX = "";

        public Segment() {
            super(ADDRESS_PREFIX, ADDRESS_SUFFIX);
        }
    }

}
