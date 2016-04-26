<!DOCTYPE html>

<html>

  <head>
    <title>Thymeleaf Sandbox: Spring Reactive</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>

  <body>

    <h1>Spring Reactive</h1>
    <h2>Thymeleaf Sandbox application (FREEMARKER)</h2>

    <span>Current time is ${.now}</span>

    <p>
        Some URLs:
    </p>

    <ul>
        <li>Thymeleaf:
            <ul>
                <li><a href="/thymeleaf">Index</a></li>
                <li><a href="/playlistentries.thymeleaf">Playlist Entries</a></li>
                <li><a href="/datadriven.thymeleaf">Playlist Entries (Data-Driven)</a></li>
            </ul>
        </li>
        <li>FreeMarker:
            <ul>
                <li><a href="/freemarker">Index</a></li>
                <li><a href="/playlistentries.freemarker">Playlist Entries</a></li>
                <li><a href="/datadriven.freemarker">Playlist Entries (Non Data-Driven but equivalent in size)</a></li>
            </ul>
        </li>
    </ul>

  </body>
  
</html>