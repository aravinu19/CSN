import com.fasterxml.jackson.core.JsonProcessingException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.io.FileUtils;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class list {

    private static String DownloadXpath = "/html/body/div[1]/div/div[3]/div[2]/div/div[5]/div/div[3]/div/div/a[2]";
    private static String filename[] = new String[1];

    public static void main(String[] iva) throws JsonProcessingException {
        System.out.println("=====================================================================================================");
        System.out.println("\t\t Welcome to Chiasenhac Downloader ( Free Lossless Audio Downloader )");
        System.out.println("\t\t\t\t\t \t\t\t\t\t  By ivara.");
        System.out.println("=====================================================================================================");

        Scanner in = new Scanner(new InputStreamReader(System.in));
        System.out.println("Enter Song Name : ");
        String searchQuery = in.nextLine();

        WebClient client = new WebClient();

        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);

        List<HtmlElement> items = null;

        try {
            String url = "http://search.chiasenhac.vn/search.php?s=" + URLEncoder.encode(searchQuery, "UTF-8") + "&cat=music";
            HtmlPage page = client.getPage(url);
            items = page.getByXPath("//tr");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (items.isEmpty()) {
            System.out.println("No Items Found");
        } else {
//
            String song, artist, downloadLink;
            List<SearchList> searchLists = new ArrayList<>();

            for (HtmlElement element : items) {

                song = "";
                artist = "";
                downloadLink = "";

                SearchList songData = new SearchList();

                HtmlAnchor anchor = element.getFirstByXPath(".//a");

                HtmlElement span = element.getFirstByXPath("//tr/td[2]/div/div/p[2]");

                if (anchor != null && (!anchor.asText().isEmpty())) {
                    song = anchor.asText();
//                    System.out.println("Song Name : " + anchor.asText());
                }
                if (span != null && (!span.asText().isEmpty())) {
                    artist = span.asText();
//                    System.out.println("Artists : " + span.asText());
                }
                if (anchor != null && (!anchor.getHrefAttribute().isEmpty())) {
                    downloadLink = anchor.getHrefAttribute();
//                    System.out.println("SOng Url : " + anchor.getHrefAttribute());
                }

                if ((!song.equals("")) && (!artist.equals("")) && (!downloadLink.equals(""))) {
                    songData.setSong(song);
                    songData.setArtist(artist);
                    songData.setDownloadLink(downloadLink);
                    searchLists.add(songData);
                }

            }

            GetUserSong(searchLists);

        }

    }

    private static void GetUserSong(List<SearchList> searchLists) {

        int songNo = 1;

        Scanner in = new Scanner(System.in);

        for (SearchList item : searchLists) {

            System.out.println(" Song No: " + songNo++ + "\n Song Title: " + item.getSong() + "\n Artist: " + item.getArtist()
                    + "\n Download Url: " + item.getDownloadLink() + "\n\n");

        }
        System.out.println("Enter Song No to Download : ");

        int songn = in.nextInt() - 1;

        SearchList songSelected = searchLists.get(songn);

        System.out.println("Selected Song Details: \n Song Title: " + songSelected.getSong() + "\n Artist: " + songSelected.getArtist() + "\n Download Url: " + songSelected.getDownloadLink() + "\n");

        filename[0] = songSelected.getSong() + "_" + songSelected.getArtist();

        try {
            grabSongDownloadLink(songSelected.getDownloadLink());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void grabSongDownloadLink(String downloadLink) throws IOException {
        int count = 0;
        String songDownloadUrl = "";
        String[] songQualities = {"128 Kbps MP3", "320 Kbps MP3", "500 Kbps M4A", "Lossless Audio"};
        String[] downloadLinks = new String[4];
        String[] linkXpaths = {"/html/body/div[1]/div/div[3]/div[1]/div/div[5]/div/div[2]/div[2]/div/div[5]/div/b[1]/a[1]",
         "/html/body/div[1]/div/div[3]/div[1]/div/div[5]/div/div[2]/div[2]/div/div[5]/div/b[1]/a[2]",
         "/html/body/div[1]/div/div[3]/div[1]/div/div[5]/div/div[2]/div[2]/div/div[5]/div/b[1]/a[3]",
         "/html/body/div[1]/div/div[3]/div[1]/div/div[5]/div/div[2]/div[2]/div/div[5]/div/b[1]/a[4]"};

        WebClient client = new WebClient();

        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);

        List<HtmlAnchor> list = null;

        try {
            HtmlPage page = client.getPage(downloadLink);
            list = page.getByXPath(DownloadXpath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (HtmlAnchor element : list){
            songDownloadUrl = element.getHrefAttribute();
        }

        if (songDownloadUrl.isEmpty()) return;

        HtmlPage page = client.getPage(songDownloadUrl);

        for (String xpath : linkXpaths){
            list = page.getByXPath(xpath);
            for (HtmlAnchor anchor : list){
                if (anchor.getHrefAttribute().isEmpty()){
                    downloadLinks[count++] = "Not Available";
                }
                else downloadLinks[count++] = anchor.getHrefAttribute();
            }
        }

        System.out.println("Available Quality to Download : ");
        int qualityCount = 0;
        for (String hrefs : downloadLinks) {
            if (!hrefs.isEmpty()){
                System.out.println( (qualityCount + 1) + " " + songQualities[qualityCount++] );
            }
            else qualityCount++;
        }

        Scanner in = new Scanner(System.in);

        System.out.println("Choose a quality to download : ");
        int qual = in.nextInt() - 1;

//        System.out.println("Download Link : " + downloadLinks[qual]);

//        saveFileFromUrlWithCommonsIO(filename[0], downloadLinks[qual]);

        String os = OSInfo.getOs().toString().toLowerCase();

        switch (os){

            case "windows": {
                System.out.println("In Current version windows version will be slow in Downloads");
                saveFileFromUrlWithCommonsIO(filename[0], downloadLinks[qual]);
                break;
            }

            case "mac": System.out.println("Sorry to Say this, but currently MAC OS is not supported :( "); break;

            default: downloadInLinux(filename[0], downloadLinks[qual]);

        }

        System.out.println("In case download doesn't start please use a vpn to unblock downloads.\n Becase some ISP doesn't Allow to acces ChiasenHac.vn\n" +
                "Sry for inconvenience \n We'll fix it soon with new idea.\n\n ");

        System.out.println("If you want to download by using External download manager :) \n Url : " + downloadLinks[qual]);

        System.out.println("=====================================================================================================");
        System.out.println("\t\t Thanks for Chiasenhac Downloader ( Free Lossless Audio Downloader)");
        System.out.println("\t\t\t\t\t By ivara.");
        System.out.println("=====================================================================================================");


    }

    private static void downloadInWindows(String address) {
        address = address.replaceAll(" ","");

        String command = "aria2c.exe " + address;

        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(command);


            // Read the output

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(proc.getInputStream()));

            String line = "";
            while((line = reader.readLine()) != null) {
                System.out.print(line + "\n");
            }

            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void saveFileFromUrlWithCommonsIO(String fileName,
                                                    String fileUrl) throws MalformedURLException, IOException {
        System.out.println("Downloading Started :)");
        FileUtils.copyURLToFile(new URL(fileUrl), new File(fileName));
        System.out.println("Downloading May be Completed , Check to make Sure it did :) ");
    }

    public static void downloadInLinux(String filename, String address){

        address = address.replaceAll(" ","");

        String command = "axel -v " + address;

        System.out.println("Please choose you your preffered Download Manager: \n 1. Wget \n 2. Axel ( needs to be installed)" +
                "\n 3. Java Method \n Option: ");

        switch (new Scanner(System.in).nextInt()){
            case 2: break;
            case 1: command = "wget " + address; break;
            default: {
                try {
                    saveFileFromUrlWithCommonsIO(filename, address);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(command);


        // Read the output

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(proc.getInputStream()));

        String line = "";
        while((line = reader.readLine()) != null) {
            System.out.print(line + "\n");
        }

        proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}