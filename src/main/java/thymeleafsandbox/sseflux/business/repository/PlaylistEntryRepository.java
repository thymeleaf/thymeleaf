/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package thymeleafsandbox.sseflux.business.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import thymeleafsandbox.sseflux.business.PlaylistEntry;


@Repository
public class PlaylistEntryRepository {

    private static final String QUERY_FIND_ALL_PLAYLIST_ENTRIES =
            "SELECT p.PlaylistId as 'playlistID', " +
            "       p.Name as 'playlistName', " +
            "       t.Name as 'trackName', " +
            "       ar.Name as 'artistName', " +
            "       a.title as 'albumTitle' " +
            "FROM playlist p, PlaylistTrack pt, track t, Album a, Artist ar " +
            "WHERE p.PlaylistId = pt.PlaylistId AND " +
            "      pt.TrackId = t.TrackId AND " +
            "      t.AlbumId = a.AlbumId AND " +
            "      a.ArtistId = ar.ArtistId";


    private JdbcTemplate jdbcTemplate;


    public PlaylistEntryRepository() {
        super();
    }


    @Autowired
    public void setJdbcTemplate(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<PlaylistEntry> findAllPlaylistEntries() {

        return this.jdbcTemplate.query(
                QUERY_FIND_ALL_PLAYLIST_ENTRIES,
                (resultSet, i) -> {
                    return new PlaylistEntry(
                            Integer.valueOf(resultSet.getInt("playlistID")),
                            resultSet.getString("playlistName"),
                            resultSet.getString("trackName"),
                            resultSet.getString("artistName"),
                            resultSet.getString("albumTitle"));
                });

    }


    public Flux<PlaylistEntry> findLargeCollectionPlaylistEntries() {

        return Flux.fromIterable(
                this.jdbcTemplate.query(
                    QUERY_FIND_ALL_PLAYLIST_ENTRIES,
                    (resultSet, i) -> {
                        return new PlaylistEntry(
                                Integer.valueOf(resultSet.getInt("playlistID")),
                                resultSet.getString("playlistName"),
                                resultSet.getString("trackName"),
                                resultSet.getString("artistName"),
                                resultSet.getString("albumTitle"));
                    })).repeat(300);

    }

}
