package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>(); //done
        albumSongMap = new HashMap<>(); //done
        playlistSongMap = new HashMap<>(); //done
        playlistListenerMap = new HashMap<>(); //done
        creatorPlaylistMap = new HashMap<>(); //done
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>(); //done
        songs = new ArrayList<>(); //done
        playlists = new ArrayList<>(); //done
        albums = new ArrayList<>(); //done
        artists = new ArrayList<>(); //done
    }

    public User createUser(String name, String mobile) {
        User user = new User(name, mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        artistAlbumMap.put(artist, new ArrayList<>());
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Artist currArtist = null;
        for(Artist artist: artists){
            if(artist.getName().equals(artistName)){
                currArtist = artist;
                break;
            }
        }
        if(currArtist==null){
            currArtist = createArtist(artistName);
        }
        Album album = new Album(title);
        albums.add(album);
        artistAlbumMap.get(currArtist).add(album);
        albumSongMap.put(album, new ArrayList<>());
        return  album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        Album currAlbum = null;
        for(Album album: albums){
            if(album.getTitle().equals(albumName)){
                currAlbum = album;
                break;
            }
        }

        if(currAlbum==null){
            throw new Exception("Album does not exist");
        }

        Song song = new Song(title, length);
        songs.add(song);
        albumSongMap.get(currAlbum).add(song);
        songLikeMap.put(song, new ArrayList<>());
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {

        User currUser = null;
        for(User user: users){
            if(user.getMobile().equals(mobile)){
                currUser = user;
            }
        }
        if(currUser==null){
            throw new Exception("User does not exist");
        }
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        playlistSongMap.put(playlist, new ArrayList<>());

        for(Song song: songs){
            if(song.getLength()==length){
                playlistSongMap.get(playlist).add(song);
            }
        }
        creatorPlaylistMap.put(currUser, playlist);

        playlistListenerMap.put(playlist, new ArrayList<>());
        playlistListenerMap.get(playlist).add(currUser);
        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User currUser = null;
        for(User user: users){
            if(user.getMobile().equals(mobile)){
                currUser = user;
            }
        }
        if(currUser==null){
            throw new Exception("User does not exist");
        }
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        playlistSongMap.put(playlist, new ArrayList<>());
        for(String songTitle: songTitles){
            for(Song song: songs){
                if(song.getTitle().equals(songTitle)){
                    playlistSongMap.get(playlist).add(song);
                }
            }
        }

        creatorPlaylistMap.put(currUser, playlist);
        playlistListenerMap.put(playlist, new ArrayList<>());
        playlistListenerMap.get(playlist).add(currUser);
        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User currUser = null;
        for(User user: users){
            if(user.getMobile().equals(mobile)){
                currUser = user;
            }
        }
        if(currUser==null){
            throw new Exception("User does not exist");
        }

        Playlist currPlaylist = null;
        for(Playlist playlist: playlists){
            if(playlist.getTitle().equals(playlistTitle)){
                currPlaylist = playlist;
            }
        }
        if(currPlaylist==null){
            throw  new Exception("Playlist does not exist");
        }

        if(creatorPlaylistMap.containsKey(currUser)){
            if(creatorPlaylistMap.get(currUser).getTitle().equals(playlistTitle)){
                return currPlaylist;
            }
        }
        if(playlistListenerMap.containsKey(currPlaylist)){
            if(!playlistListenerMap.get(currPlaylist).contains(currUser)){
                playlistListenerMap.get(currPlaylist).add(currUser);
            }
        }
        return currPlaylist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User currUser = null;
        for(User user: users){
            if(user.getMobile().equals(mobile)){
                currUser = user;
            }
        }
        if(currUser==null){
            throw new Exception("User does not exist");
        }
        Song currSong = null;
        for(Song song: songs){
            if(song.getTitle().equals(songTitle)){
                currSong = song;
            }
        }
        if(currSong==null){
            throw new Exception("Song does not exist");
        }

        if(songLikeMap.get(currSong).contains(currUser)){
            return currSong;
        }
        songLikeMap.get(currSong).add(currUser);
        currSong.setLikes(currSong.getLikes()+1);

        Album currAlbum = null;
        for(Album album: albumSongMap.keySet()){
            if(albumSongMap.get(album).contains(currSong)){
                currAlbum = album;
            }
        }
        Artist currArtist = null;
        for(Artist artist: artistAlbumMap.keySet()){
            if(artistAlbumMap.get(artist).contains(currAlbum)){
                currArtist = artist;
            }
        }
        if (currArtist==null){
            throw new Exception("");
        }
        currArtist.setLikes(currArtist.getLikes()+1);
        return  currSong;
    }

    public String mostPopularArtist() {
        Artist currArtist = null;
        int maxLikes = 0;

        for(Artist artist: artists){
            if(artist.getLikes()>=maxLikes){
                currArtist = artist;
                maxLikes = artist.getLikes();
            }
        }
        if(currArtist==null){
            return "";
        }
        return currArtist.getName();
    }

    public String mostPopularSong() {
        Song currSong = null;
        int maxLikes = 0;

        for(Song song: songs){
            if(song.getLikes()>=maxLikes){
                currSong = song;
                maxLikes = song.getLikes();
            }
        }
        if(currSong==null){
            return "";
        }
        return currSong.getTitle();
    }

}
