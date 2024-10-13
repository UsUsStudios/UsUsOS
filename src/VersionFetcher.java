import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VersionFetcher {
    // Fetches the version number from GitHub
    public static String getVersionNumber() {
        String owner = "UsUsStudios";
        String repo = "UsUsOS";
        String path = "ususos/version.usus";

        String versionNumber = "1.0";

        try {
            versionNumber = getFileContent(owner, repo, path);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return versionNumber;
    }

    // Sends a request to GitHub
    private static String getFileContent(String owner, String repo, String path) throws Exception {
        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/contents/" + path;

        @SuppressWarnings("deprecation")
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/vnd.github.v3.raw");

        if (connection.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
        }

        StringBuilder response;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
        }

        return response.toString();
    }
}
