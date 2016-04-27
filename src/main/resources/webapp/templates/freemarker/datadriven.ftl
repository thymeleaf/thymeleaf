<!DOCTYPE html>

<html>

  <head>
    <title>Thymeleaf Sandbox: Spring Reactive</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>

  <body>

    <h1>Spring Reactive</h1>
    <h2>Thymeleaf Sandbox application (FREEMARKER)</h2>

    <h3>Playlist Entries (Non data-driven &mdash; not possible with FreeMarker)</h3>

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
          <td>${e.playlistId?html}</td>
          <td>${e.playlistName?html}</td>
          <td>${e.trackName?html}</td>
          <td>${e.artistName?html}</td>
          <td>${e.albumTitle?html}</td>
        </tr>
</#list>
      </tbody>
    </table>

  </body>

</html>