<!DOCTYPE html>

<html>

  <head>
    <title>Thymeleaf Sandbox</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>

  <body>

    <h2>Big Listing (8,715 * 300 = 2,614,500 entries)</h2>

    <p>Using template engine: <strong>FreeMarker</strong></p>

    <table>
      <thead>
        <tr>
          <th>Playlist ID</th>
          <th>Playlist Name</th>
          <th>Track Name</th>
          <th>Artist Name</th>
          <th>Album Title</th>
        </tr>
      </thead>
      <tbody>
<#list dataSource as e>        <tr>
          <td>${e.playlistId}</td>
          <td>${e.playlistName}</td>
          <td>${e.trackName}</td>
          <td>${e.artistName}</td>
          <td>${e.albumTitle}</td>
        </tr>
</#list>
      </tbody>
    </table>

  </body>

</html>