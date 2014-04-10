$(document)
  .ready(
    function() {

      function getURLParameter(name) {
        return decodeURI((RegExp(name + '=' + '(.+?)(&|$)').exec(
          location.search) || [
          , null
        ])[1]);
      }

      var projectName = getURLParameter('project');
      var defaultUser = getURLParameter('user');

      if (defaultUser != "null") {
        $("#lockname").val(defaultUser);
      }

      if (projectName == "null") {
        $("#error")
          .html(
            "Error: Please specify a project over an url-param. E.g. '?project=DDB'");
      } else {
        $("#project").html(projectName);
        document.title = 'MergeLock ' + projectName;
        var ownUrl = window.location.host + window.location.pathname;
        var ownUrlLastChar = ownUrl.charAt(ownUrl.length - 1);
        if (ownUrlLastChar === "/") {
          ownUrl = ownUrl.substring(0, ownUrl.length - 1);
        }
        var ws = $.gracefulWebSocket("ws:" + ownUrl + "/websocket");

        $("#lockform").submit(function(event) {
          event.preventDefault();
          var lockName = $("#lockname").val();
          if (lockName == null || lockName == "") {
            return false;
          }
          ws.send("lock#" + projectName + "#" + lockName);
        });

        $("#unlockform").submit(function(event) {
          event.preventDefault();
          var lockName = $("#lockname").val();
          ws.send("unlock#" + projectName + "#" + lockName);
        });

        ws.onopen = function(event) {
          var lockName = $("#lockname").val();
          if (lockName == null || lockName == "") {
            lockName = "anonymous";
          }
          ws.send("status#" + projectName + "#" + lockName);
        };

        ws.onclose = function(event) {
          unlockGui();
        };

        ws.onmessage = function(event) {
          var messageParts = event.data.split("#");
          var task = messageParts[0];
          var project = messageParts[1];
          var user = messageParts[2];

          if (project == projectName) { // Only listen for events of the selected project
            if (task == "locked") {
              var lockname = $("#lockname").val();
              if (lockname == user) { // If the event is about me
                lockGuiForMe();
              } else { // If the event is about others
                lockGuiForOther(user);
              }
            } else if (task == "unlocked") { // If the merge slot is free again
              unlockGui();
            }
          }
        };

      }

      function lockGuiForMe() {
        $("#lockdiv").css("display", "none");
        $("#unlockdiv").css("display", "block");
        $("#lockeddiv").css("display", "none");
        $("#user").html("");
      }

      function lockGuiForOther(lockedBy) {
        $("#lockdiv").css("display", "none");
        $("#unlockdiv").css("display", "none");
        $("#lockeddiv").css("display", "block");
        $("#user").html(lockedBy);
      }

      function unlockGui() {
        $("#lockdiv").css("display", "block");
        $("#unlockdiv").css("display", "none");
        $("#lockeddiv").css("display", "none");
        $("#user").html("");
      }

    });