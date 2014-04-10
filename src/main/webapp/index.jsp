<!DOCTYPE html>
<html lang="de">
  <head>
    <meta charset="utf-8" />
    <title>MergeLock</title>
    <link type="text/css" rel="stylesheet" href="css/mergelock.css"  />
    <link type="image/x-icon" rel="icon" href="img/favicon.png" />
    <script type="text/javascript" src="js/jquery-1.8.2.min.js" ></script>
    <script type="text/javascript" src="js/jquery.gracefulWebSocket.js" ></script>
    <script type="text/javascript" src="js/mergelock.js" ></script>
  </head>
  <body>
    <div id="error" class="text"></div>
    <h2>Can I merge in project <span id="project"></span>?</h2>
    <div id="lockdiv">
      <div class="text maybe">Nobody has requested a merge lock yet.</div>
      <form action="#" id="lockform">
        <input type="text" id="lockname" />
        <button>Request Lock</button>
      </form>
    </div>
    <div id="unlockdiv">
      <div class="text yes"><b>YES</b>. The project is locked by you. <a href="/mergelock/status" style="font-size: 9px; text-decoration: none;">(?)</a></div>
      <form action="#" id="unlockform">
        <button>Unlock again</button>
      </form>
    </div>
    <div id="lockeddiv">
      <div class="text no"><b>No</b>. The project is locked by <span id="user"></span> <a href="/mergelock/status" style="font-size: 9px; text-decoration: none;">(?)</a></div>
    </div>
  </body>
</html>
