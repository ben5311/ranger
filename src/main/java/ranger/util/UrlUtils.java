package ranger.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Util class for handling URLs in parsers.
 */
public class UrlUtils {

    private UrlUtils() {
    }

    /**
     * Converts filePath to URL without checked exception
     *
     * @param filePath path to file to be converted to URL
     * @return URL of file
     */
    public static URL URLof(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("filePath must not be null nor empty");
        }
        if (filePath.charAt(0) == ':') {      //URL points to predefined classpath resource
            URL classpathResourceUrl =
                    UrlUtils.class.getClassLoader().getResource("predefined/" + filePath.substring(1));
            if (classpathResourceUrl == null) {
                throw new IllegalArgumentException(
                        "You wanted to access predefined resource '" + filePath + "' but it does not exist");
            }
            return classpathResourceUrl;
        } else {
            try {
                return new File(filePath).toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Checks if url points to a directory
     *
     * @param url the URL to check
     * @return true if url points to a directory
     */
    public static boolean isDirectory(URL url) {
        if (url == null) { throw new IllegalArgumentException("url must not be null"); }
        return url.toString().endsWith("/");
    }

    /**
     * Resolve path from parentDirectoryUrl
     *
     * @param parentDirectoryUrl the URL to current directory
     * @param path               the relative path to append to parentDirectoryUrl
     * @return resolved URL
     */
    public static URL resolve(URL parentDirectoryUrl, String path) {
        if (parentDirectoryUrl == null || path == null || path.isEmpty()) {
            throw new IllegalArgumentException("parentDirectoryUrl and path must not be null nor empty");
        }
        if (!isDirectory(parentDirectoryUrl)) {
            throw new IllegalArgumentException(
                    "parentDirectoryUrl must point to a directory and " + "end with a '/' but was: '" +
                            parentDirectoryUrl + "'");
        }
        if (path.startsWith("/")) {
            throw new IllegalArgumentException("path must be relative but was absolute: '" + path + "'");
        }
        try {
            if (parentDirectoryUrl.getProtocol().equals("jar")) {
                URL innerFileUrl = new URL(parentDirectoryUrl.toString().substring(4));
                URL resolved = innerFileUrl.toURI().resolve(path).toURL();
                return new URL("jar:" + resolved);
            } else if (parentDirectoryUrl.getProtocol().equals("file")) {
                Path resolvedPath = Paths.get(parentDirectoryUrl.toURI()).resolve(path);
                return resolvedPath.toUri().toURL();
            } else {
                return parentDirectoryUrl.toURI().resolve(path).toURL();
            }
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns parent directory from url
     *
     * @param url the URL
     * @return URL to url's parent directory
     */
    public static URL getParentURL(URL url) {
        if (url == null) { throw new IllegalArgumentException("url must not be null"); }
        String urlString = url.toString();
        if (urlString.endsWith("/")) { urlString = urlString.substring(0, urlString.length() - 1); }   //strip last '/'
        if (urlString.matches("\\w+:")) { return null; }    //url points to root so return null
        if (!urlString.contains("/")) {
            throw new IllegalArgumentException("Could not resolve parent URL from url '" + url + "'");
        }
        try {   //remove everything after last '/'
            return new URL(urlString.substring(0, urlString.lastIndexOf('/') + 1));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

}
