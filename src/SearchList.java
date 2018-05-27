public class SearchList {

    private String song;
    private String artist;
    private String downloadLink;

    public SearchList(String song, String artist, String downloadLink) {
        this.song = song;
        this.artist = artist;
        this.downloadLink = downloadLink;
    }

    public SearchList() {
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }
}
